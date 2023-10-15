package ir.co.sadad.departuretaxapi.services;

import ir.co.sadad.departuretaxapi.dtos.*;
import ir.co.sadad.departuretaxapi.dtos.provider.*;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.*;
import ir.co.sadad.departuretaxapi.entities.DepartureGroupType;
import ir.co.sadad.departuretaxapi.entities.DepartureTaxPayment;
import ir.co.sadad.departuretaxapi.entities.DepartureTaxUser;
import ir.co.sadad.departuretaxapi.enums.*;
import ir.co.sadad.departuretaxapi.exceptions.*;
import ir.co.sadad.departuretaxapi.repositories.DepartureGroupTypeRepository;
import ir.co.sadad.departuretaxapi.repositories.DepartureTaxUserRepository;
import ir.co.sadad.departuretaxapi.services.providers.MoneyTransferService;
import ir.co.sadad.departuretaxapi.services.providers.PSPDepartureTaxService;
import ir.co.sadad.departuretaxapi.services.providers.PichakService;
import ir.co.sadad.departuretaxapi.services.utilities.ConverterHelper;
import ir.co.sadad.departuretaxapi.services.utilities.DateTimeDepartureFormat;
import ir.co.sadad.departuretaxapi.services.utilities.TripleDesHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author G.ShahrokhAbadi on 19/7/2023
 * <p>
 * this service includes the whole flow of departure tax payment
 */
@Service
@Slf4j
//@AllArgsConstructor
@RequiredArgsConstructor
public class DepartureTaxServiceImpl implements DepartureTaxService {

    @Value(value = "${pushOrder.signData}")
    private String pushOrder_signData;

    @Value(value = "${pushOrder.keyData}")
    private String pushOrder_keyData;

    @Value(value = "${pushOrder.applicationName}")
    private String applicationName;

    @Value(value = "${pushOrder.keyVersion}")
    private int keyVersion;

    @Value(value = "${pushOrder.userName}")
    private String userName;

    @Value(value = "${pushOrder.terminalId}")
    private String terminalId;
    @Value(value = "${taxPayment.targetAccount}")
    private String targetAccount;

    private final DepartureGroupTypeRepository groupTypeRepository;
    private final DepartureTaxUserRepository departureTaxUserRepository;
    private final PSPDepartureTaxService pspDepartureTaxService;
    private final MoneyTransferService moneyTransferService;
    private final PichakService pichakService;
    private final StatusManagementService statusManagement;
    private final ModelMapper modelMapper;

    public List<DepartureTaxGroupDto> DepartureGroupList() {
        List<DepartureTaxGroupDto> taxGroupDto = new ArrayList<>();
        List<DepartureGroupType> groupList = groupTypeRepository.findByVisibilityIsTrue();

        for (DepartureGroupType group : groupList) {
            DepartureTaxGroupDto dto = new DepartureTaxGroupDto();
            modelMapper.map(group, dto);
            taxGroupDto.add(dto);
        }

        return taxGroupDto;

    }

    @Override
    @SneakyThrows
    public ServiceTypeInquiryResDto serviceTypeInquiry(ServiceTypeInquiryReqDto typeReqDto, String token) {

        TypeInquiryReqDto typeInquiryReqDto = prepareServiceTypeInquiryRequest(typeReqDto.getNationalCode(), Integer.parseInt(typeReqDto.getServiceGroupCode()));
        TypeInquiryResDto serviceResult;
        String jsonData = null;
        String status = "EXCEPTION";

        try {
            serviceResult = pspDepartureTaxService.serviceTypeInquiryProvider(typeInquiryReqDto);

            jsonData = ConverterHelper.convertResponseToJson(serviceResult);
            status = "FAILED";
            switch (serviceResult.getResCode()) {
                case 0 -> {
                    ServiceTypeInquiryResDto response = new ServiceTypeInquiryResDto();
                    response.setServiceTypeTitle(serviceResult.getServiceTypeTitle());
                    response.setServiceTypeCode(serviceResult.getServiceType());
                    response.setServiceType(TripType.getByCode(serviceResult.getServiceType()));
                    response.setAmount(serviceResult.getAmount());
                    response.setPassengerNationalCode(typeReqDto.getNationalCode());
                    response.setPassengerMobileNumber(typeReqDto.getMobileNumber());
                    PichakInfoResDto pichakRes = pichakService.getUserInfo(typeReqDto.getNationalCode(), token);
                    if (pichakRes != null) {
                        response.setPassengerFirstName(pichakRes.getName());
                        response.setPassengerLastName(pichakRes.getLastName());
                    }
                    response.setRequestId(statusManagement.saveTypeInquiryUser(response));

                    statusManagement.saveExceptionLogs("", "",
                            response.getRequestId(), jsonData, "serviceTypeInquiry", "SUCCESS");

                    return response;
                }
                case 1 -> throw new PspDepartureException("psp.service.type.repeated", HttpStatus.BAD_REQUEST);
                case 2 -> {
                    if (typeReqDto.getServiceGroupCode().equals("5") || typeReqDto.getServiceGroupCode().equals("6"))
                        throw new PspBorderLineException("INVALID_NATIONALCODE_BORDERLINE", HttpStatus.BAD_REQUEST);
                    throw new PspDepartureException("psp.service.type.exception", HttpStatus.BAD_REQUEST);
                }
                case 3 -> throw new PspDepartureException("psp.service.type.parameters", serviceResult.getMessage(), HttpStatus.BAD_REQUEST);
                case 4 -> throw new PspDepartureException("psp.service.type.sign.exception", HttpStatus.BAD_REQUEST);
                default -> throw new PspDepartureException("GENERAL_INTERNAL_ERROR", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            statusManagement.saveExceptionLogs(e.getClass().getName(), e.getMessage(),
                    null, jsonData, "serviceTypeInquiry", status);
            if (e instanceof PspBorderLineException)
                throw new DepartureTaxException(e.getMessage(), ((DepartureTaxException) e).getHttpStatusCode());
            throw new DepartureTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @Override
    @SneakyThrows
    public PaymentOrderResDto initiatePayment(PaymentOrderReqDto orderReqDto, String token, String userAgent) {
        DepartureTaxUser savedUser = findUserByRequestId(orderReqDto.getRequestId());

        InitiationPaymentReqDto initiateReqDto = preparePaymentOrder(orderReqDto.getFromAccount(), savedUser.getAmount());
        PaymentOrderResDto result = new PaymentOrderResDto();

        statusManagement.saveInitiationPaymentOrder(savedUser);
        InitiationPaymentResDto serviceResult = null;
        String jsonData = null;

        try {
            serviceResult = moneyTransferService.paymentOrderProvider(initiateReqDto, orderReqDto.getRequestId(), token, userAgent);
        } catch (PaymentTax4xxException e) {
            throw new PaymentTax4xxException("PAYMENT_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (PaymentTaxException e) {
            throw new DepartureTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            jsonData = ConverterHelper.convertResponseToJson(serviceResult);
            if (serviceResult.getInstructionIdentification() != null) {

                result.setRequestId(orderReqDto.getRequestId());
                result.setInstructionIdentification(serviceResult.getInstructionIdentification());
                result.setIsRequiredTan(serviceResult.getIsRequiredTan());
                statusManagement.saveInitiationPaymentResult(initiateReqDto, result);
                statusManagement.saveExceptionLogs("", "",
                        orderReqDto.getRequestId(), jsonData, "initiatePayment", "SUCCESS");

                if (!serviceResult.getIsRequiredTan()) {
                    PaymentFinalReqDto paymentExeReqDto = new PaymentFinalReqDto();
                    paymentExeReqDto.setAuthorizationCode(null);
                    paymentExeReqDto.setInstructionIdentification(serviceResult.getInstructionIdentification());
                    paymentExeReqDto.setRequestId(orderReqDto.getRequestId());

                    executePaymentAndPushOrder(paymentExeReqDto, token, userAgent);

                } else return result;

            } else
                throw new DepartureTaxException("initiation.payment.not.valid", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            if (!(e instanceof PaymentTaxException))  //if executePaymentAndPushOrder is directly called, the method saves exception by itself, no need to save again
                statusManagement.saveExceptionLogs(e.getClass().getName(), e.getMessage(),
                        orderReqDto.getRequestId(), jsonData, "initiatePayment", "FAILED");
            throw new DepartureTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


    @Override
    @SneakyThrows
    public PushOrderFinalResDto executePaymentAndPushOrder(PaymentFinalReqDto finalUserReqDto, String token, String userAgent) {
        DepartureTaxUser savedUser = findUserByRequestId(finalUserReqDto.getRequestId());

        ExecutionPaymentResDto paymentResult;
        statusManagement.saveExecutionPaymentRequest(savedUser);
        String jsonData = null;

        try {
            paymentResult = moneyTransferService.paymentFinalProvider(finalUserReqDto.getInstructionIdentification(), finalUserReqDto.getRequestId(), preparePaymentFinal(finalUserReqDto.getAuthorizationCode()), token, userAgent);
        } catch (PaymentTax4xxException e) {
            throw new PaymentTax4xxException("PAYMENT_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (PaymentTaxException e) {
            throw new PaymentTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            jsonData = ConverterHelper.convertResponseToJson(paymentResult);
            switch (paymentResult.getTransactionStatus()) {
                case SUCCEEDED -> {
                    statusManagement.saveExecutionPaymentResult(paymentResult, finalUserReqDto.getRequestId());
                    statusManagement.saveExceptionLogs("", "", finalUserReqDto.getRequestId(),
                            jsonData, "executePaymentAndPushOrder", "SUCCESS");

                    return pushOrderRequest(finalUserReqDto.getRequestId(), false);
                }
                case FAILED -> {
                    statusManagement.saveExecutionPaymentFailedResult(finalUserReqDto.getRequestId());
                    throw new PaymentTaxException("PAYMENT_ERROR", HttpStatus.BAD_REQUEST);

                }
                case UNKNOWN -> {
                    statusManagement.saveExecutionPaymentProcessingResult(paymentResult, finalUserReqDto.getRequestId());
                    throw new PaymentTaxException("PAYMENT_PROCESSING", HttpStatus.BAD_REQUEST);
                }
                default -> throw new PaymentTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (DepartureTaxException e) {
            if (!(e instanceof PspDepartureException)) // if pushOrderRequest method raises an exception, it saves log by itself, no need to save again
                statusManagement.saveExceptionLogs(e.getClass().getName(), e.getMessage(),
                        finalUserReqDto.getRequestId(), jsonData, "executePaymentAndPushOrder", "FAILED");
            throw new PaymentTaxException(e.getMessage(), e.getHttpStatusCode());
        }

    }

    @Override
    @SneakyThrows
    public PushOrderFinalResDto pushOrderRequest(String requestId, Boolean inquiry) {

        statusManagement.savePushOrderRequest(requestId);
        PushOrderResDto result;
        String jsonData = null;

        try {
            result = pspDepartureTaxService.pushOrderProvider(preparePushOrderRequest(requestId, inquiry));

            jsonData = ConverterHelper.convertResponseToJson(result);
            switch (result.getResCode()) {
                case 0 -> {
                    statusManagement.savePushOrderSuccessResult(result, requestId);
                    statusManagement.saveExceptionLogs("", "", requestId, jsonData,
                            "pushOrderRequest", "SUCCESS");
                    return makePushOrderResForClient(requestId);
                }
                case 1 -> throw new PspDepartureException("psp.push.order.repeated", HttpStatus.BAD_REQUEST);
                case 2 -> throw new PspDepartureException("psp.push.order.exception", HttpStatus.BAD_REQUEST);
                case 3 -> throw new PspDepartureException("psp.push.order.parameters", result.getMessage(), HttpStatus.BAD_REQUEST);
                case 4 -> throw new PspDepartureException("psp.push.order.sign.exception", HttpStatus.BAD_REQUEST);
                default -> throw new PspDepartureException("GENERAL_INTERNAL_ERROR", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            statusManagement.savePushOrderFailedResult(requestId);
            statusManagement.saveExceptionLogs(e.getClass().getName(), e.getMessage(),
                    requestId, jsonData, "pushOrderRequest", "UNKNOWN");
            throw new PspDepartureException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public PushOrderFinalResDto paymentProcessingInquiry(String requestId, String token, String userAgent) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(requestId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));

        if (savedUser.getRequestStatus().equals(DepartureRequestStatus.PROCESSING)) {
            PaymentInquiryResDto paymentResult;
            try {
                paymentResult = moneyTransferService.paymentInquiry(savedUser.getUserPayment().getInstructionIdentification(),
                        requestId, token, userAgent);
            } catch (PaymentTax4xxException e) {
                throw new PaymentTax4xxException("PAYMENT_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (PaymentTaxException e) {
                throw new DepartureTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String jsonData = null;
            try {
                jsonData = ConverterHelper.convertResponseToJson(paymentResult);
                switch (paymentResult.getTransactionStatus()) {
                    case SUCCEEDED -> {
                        statusManagement.savePaymentInquiryResult(paymentResult, requestId);
                        statusManagement.saveExceptionLogs("", "", requestId,
                                jsonData, "executePaymentAndPushOrder", "SUCCESS");

                        return pushOrderRequest(requestId, false);
                    }
                    case FAILED -> {
                        statusManagement.savePaymentInquiryFailedResult(paymentResult, requestId);
                        throw new PaymentTaxException("PAYMENT_HAS_BEEN_FAILED", HttpStatus.BAD_REQUEST);

                    }
                    case UNKNOWN -> {
                        statusManagement.savePaymentInquiryProcessingResult(paymentResult, requestId);
                        throw new PaymentTaxException("PAYMENT_PROCESSING", HttpStatus.BAD_REQUEST);
                    }
                    default ->
                            throw new PaymentTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
                }

            } catch (DepartureTaxException e) {
                if (!(e instanceof PspDepartureException)) // if pushOrderRequest method raises an exception, it saves log by itself, no need to save again
                    statusManagement.saveExceptionLogs(e.getClass().getName(), e.getMessage(),
                            requestId, jsonData, "paymentProcessingInquiry", "FAILED");
                throw new DepartureTaxException(e.getMessage(), e.getHttpStatusCode());
            }

        } else if (savedUser.getRequestStatus().equals(DepartureRequestStatus.UNKNOWN))
            return pushOrderRequest(requestId, true);

        else throw new DepartureTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @Override
    public List<DepartureTaxHistoryResDto> departureTaxHistory(DepartureTaxHistoryReqDto historyReq, String ssn) {

        List<DepartureTaxUser> historyList = setAdvancedHistoryParams(historyReq, ssn);

        int totalItems = historyList.size();
        int startIndex = (historyReq.getPageNumber() - 1) * historyReq.getPageSize();
        int endIndex = Math.min(startIndex + historyReq.getPageSize(), totalItems);

        startIndex = Math.max(startIndex, 0);  // Ensure startIndex is not negative
        endIndex = Math.min(endIndex, totalItems);

        historyList = historyList.subList(startIndex, endIndex);

        if (!historyList.isEmpty()) {
            List<DepartureTaxHistoryResDto> res = new ArrayList<>();
            for (DepartureTaxUser record : historyList) {
                DepartureTaxHistoryResDto his = new DepartureTaxHistoryResDto();
                modelMapper.map(record, his);
                his.setServiceTypeCode(record.getServiceType());
                his.setServiceType(TripType.getByCode(record.getServiceType()));
                his.setPassengerNationalCode(record.getNationalCode());
                his.setPassengerFirstName(record.getFirstName());
                his.setPassengerLastName(record.getLastName());
                his.setPassengerMobileNumber(record.getMobile());
                his.setStatus(record.getRequestStatus());

                if (record.getUserPayment() != null) {
                    his.setFromAccount(record.getUserPayment().getFromAccount());
                    his.setIdentification(record.getUserPayment().getIdentification());
                    his.setInitiatorName(record.getUserPayment().getInitiatorName());
                    his.setInitiationDate(record.getUserPayment().getInitiationDate());
                    his.setCurrency(record.getUserPayment().getCurrency());
                    his.setInstructionIdentification(record.getUserPayment().getInstructionIdentification());
                    his.setTraceId(record.getUserPayment().getTraceId());
                    his.setInitiatorReference(record.getUserPayment().getInitiatorReference());
//                    his.setTransactionStatus(record.getUserPayment().getTransactionStatus().toString());
                }
                res.add(his);
            }
            return res;

        } else return null;

    }

    @Override
    @SneakyThrows
    public void resendSms(String requestId, String token, String userAgent) {
        DepartureTaxUser savedUser = findUserByRequestId(requestId);
        String instructionIdentification = savedUser.getUserPayment().getInstructionIdentification();
        if (instructionIdentification == null || instructionIdentification.isEmpty())
            throw new DepartureTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.BAD_REQUEST);

        try {
            moneyTransferService.resendSmsProvider(instructionIdentification, requestId, token, userAgent);
        } catch (Exception e) {
            throw new DepartureTaxException("GENERAL_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private DepartureTaxUser findUserByRequestId(String requestId) {
        return departureTaxUserRepository.findByRequestId(requestId)
                .orElseThrow(() -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
    }

    private List<DepartureTaxUser> setAdvancedHistoryParams(DepartureTaxHistoryReqDto historyReq, String ssn) {
        Specification<DepartureTaxUser> spec = Specification.where(DepartureTaxUserRepository.withSsn(ssn))
                .and(DepartureTaxUserRepository.withStatusList(List.of(new DepartureRequestStatus[]
                        {DepartureRequestStatus.SUCCESS, DepartureRequestStatus.UNKNOWN, DepartureRequestStatus.FAILED, DepartureRequestStatus.PROCESSING})));

        if (historyReq.getPassengerNationalCode() != null && !historyReq.getPassengerNationalCode().isEmpty())
            spec = spec.and(DepartureTaxUserRepository.withPassengerNationalCode(historyReq.getPassengerNationalCode()));

        if (historyReq.getFromAccount() != null && !historyReq.getFromAccount().isEmpty())
            spec = spec.and(DepartureTaxUserRepository.withFromAccount(historyReq.getFromAccount()));

        if (historyReq.getOfflineId() != null && !historyReq.getOfflineId().isEmpty())
            spec = spec.and(DepartureTaxUserRepository.withOfflineId(historyReq.getOfflineId()));

        if (historyReq.getRequestId() != null && !historyReq.getRequestId().isEmpty())
            spec = spec.and(DepartureTaxUserRepository.withRequestId(historyReq.getRequestId()));

        if (historyReq.getStatus() != null)
            spec = spec.and(DepartureTaxUserRepository.withStatus(historyReq.getStatus()));

        if (historyReq.getTraceId() != null && !historyReq.getTraceId().isEmpty())
            spec = spec.and(DepartureTaxUserRepository.withTraceId(historyReq.getTraceId()));

        if (historyReq.getAmountFrom() != null || historyReq.getAmountTo() != null)
            spec = spec.and(DepartureTaxUserRepository.withAmountInRange(historyReq.getAmountFrom(), historyReq.getAmountTo()));

        if ((historyReq.getDateFrom() != null && !Objects.equals(historyReq.getDateFrom(), "")) || (historyReq.getDateTo() != null && !Objects.equals(historyReq.getDateTo(), "")))
            spec = spec.and(DepartureTaxUserRepository.withDateInRange(historyReq.getDateFrom(), historyReq.getDateTo()));

        List<DepartureTaxUser> historyList = departureTaxUserRepository.findAll(spec);

        Comparator<DepartureTaxUser> comparator;
        if (historyReq.getSortBy() != null && !historyReq.getSortBy().isEmpty()) {
            comparator = switch (historyReq.getSortBy()) {
                case "date" -> Comparator.comparing(DepartureTaxUser::getResponseDateTime).reversed();
                case "nationalCode" -> Comparator.comparing(DepartureTaxUser::getNationalCode).reversed();
                case "requestId" -> Comparator.comparing(DepartureTaxUser::getRequestId).reversed();
                case "amount" -> Comparator.comparingLong(DepartureTaxUser::getAmount).reversed();

                default -> throw new IllegalArgumentException("Invalid sortByField: " + historyReq.getSortBy());
            };

        } else comparator = Comparator.comparing(DepartureTaxUser::getResponseDateTime).reversed(); //نزولی دیفالت

        if (historyReq.getSort() != null && historyReq.getSort().equals("asc"))
            comparator = comparator.reversed();

        return historyList.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

    }

    private PushOrderFinalResDto makePushOrderResForClient(String userId) {
        DepartureTaxUser user = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));

        DepartureTaxPayment userPayment = user.getUserPayment();
        if (userPayment == null)
            throw new DepartureTaxException("user.not.paid", HttpStatus.BAD_REQUEST);

        return PushOrderFinalResDto.builder()
                .orderId(user.getOrderId())
                .referenceNumber(user.getReferenceNumber())
                .offlineId(user.getOfflineId())
                .requestId(user.getRequestId())
                .instructionIdentification(userPayment.getInstructionIdentification())
                .identification(userPayment.getIdentification())
                .initiatorReference(userPayment.getInitiatorReference())
                .initiatorName(userPayment.getInitiatorName())
                .initiationDate(DateTimeDepartureFormat.paymentDate(userPayment.getInitiationDate()))
                .currency(userPayment.getCurrency())
                .fromAccount(userPayment.getFromAccount())
                .traceId(userPayment.getTraceId())
                .transactionStatus(userPayment.getTransactionStatus())
                .passengerNationalCode(user.getNationalCode())
                .passengerFirstName(user.getFirstName())
                .passengerLastName(user.getLastName())
                .passengerMobileNumber(user.getMobile())
                .serviceTypeCode(user.getServiceType())
                .serviceType(TripType.getByCode(user.getServiceType()))
                .serviceTypeTitle(user.getServiceTypeTitle())
                .amount(user.getAmount())
                .responseDateTime(user.getResponseDateTime())
                .status(user.getRequestStatus())
                .build();
    }


    private PushOrderReqDto preparePushOrderRequest(String userId, Boolean inquiry) throws Exception {
        DepartureTaxUser user = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));

        DepartureTaxPayment savedPayment = user.getUserPayment();
        if (savedPayment == null)
            throw new DepartureTaxException("user.not.paid", HttpStatus.BAD_REQUEST);

        PushOrderReqDto orderReqDto = PushOrderReqDto.builder().amount(user.getAmount())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .serviceType(user.getServiceType())
                .nationalCode(user.getNationalCode())
                .transactionDateTime(DateTimeDepartureFormat.pushOrderDate(savedPayment.getInitiationDate()))
                .referenceNumber(inquiry ? ConverterHelper.createRRNRandomly() : ConverterHelper.createRRN(savedPayment.getTraceId()))
                .mobile(user.getMobile())
                .email(user.getEmail())
                .systemTraceNo(savedPayment.getTraceId())
                .channel(user.getChannel())
                .build();


        byte[] keyData = Base64.decodeBase64(pushOrder_keyData);
        byte[] keySign = Base64.decodeBase64(pushOrder_signData);
        TripleDesHelper dataTripleDes = new TripleDesHelper(keyData);
        TripleDesHelper signTripleDes = new TripleDesHelper(keySign);

        String signData = orderReqDto.getNationalCode().concat(";").concat(String.valueOf(orderReqDto.getAmount()))
                .concat(";").concat(orderReqDto.getReferenceNumber())
                .concat(";").concat(new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date()));

        orderReqDto.setFirstName(orderReqDto.getFirstName() == null ? null : Base64.encodeBase64String(dataTripleDes.encrypt(orderReqDto.getFirstName(), StandardCharsets.UTF_16LE)));
        orderReqDto.setLastName(orderReqDto.getLastName() == null ? null : Base64.encodeBase64String(dataTripleDes.encrypt(orderReqDto.getLastName(), StandardCharsets.UTF_16LE)));
        orderReqDto.setNationalCode(Base64.encodeBase64String(dataTripleDes.encrypt(orderReqDto.getNationalCode(), StandardCharsets.UTF_16LE)));
//        orderReqDto.setBranchCode(orderReqDto.getBranchCode());
        orderReqDto.setUserName(userName);
        orderReqDto.setTerminalId(terminalId);
        orderReqDto.setApplicationName(applicationName);
        orderReqDto.setKeyVersion(keyVersion);
        orderReqDto.setSignData(Base64.encodeBase64String(signTripleDes.encrypt(signData, StandardCharsets.UTF_16LE)));

        return orderReqDto;

    }

    private TypeInquiryReqDto prepareServiceTypeInquiryRequest(String nationalCode, int serviceGroupCode) throws GeneralSecurityException, UnsupportedEncodingException {
        TypeInquiryReqDto reqDto = new TypeInquiryReqDto();

        byte[] keySign = Base64.decodeBase64(pushOrder_signData);
        TripleDesHelper signTripleDes = new TripleDesHelper(keySign);

        String signData = nationalCode.concat(";").concat(String.valueOf(serviceGroupCode).concat(";")).concat(new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date()));

        reqDto.setNationalCode(nationalCode);
        reqDto.setServiceGroupCode(serviceGroupCode);
        reqDto.setApplicationName(applicationName);
        reqDto.setKeyVersion(keyVersion);
        reqDto.setSignData(Base64.encodeBase64String(signTripleDes.encrypt(signData, StandardCharsets.UTF_16LE)));

        return reqDto;

    }

    private InitiationPaymentReqDto preparePaymentOrder(String fromAccount, Long amount) {

        return InitiationPaymentReqDto.builder()
                .instructionType(InstructionType.DEPARTURE_TAX)
                .mechanism(Mechanism.INTRA_BANK)
                .fromAccount(fromAccount)
                .targetAccount(targetAccount)
                .amount(String.valueOf(amount))
                .currency("IRR")
                .descriptionInstruction("")
                .smtCode("")
                .productType(ProductType.INDIVIDUAL)
                .purpose("DRPA")
                .creditPayId("")
                .debitPayId("")
                .usage(Usage.NORMAL_TRANSFER)
                .build();
    }

    private ExecutionPaymentReqDto preparePaymentFinal(String authorizationCode) {

        return ExecutionPaymentReqDto.builder()
                .status(TransactionStatus.ACTIVE)
                .authorizationCode(authorizationCode).build();
    }
}
