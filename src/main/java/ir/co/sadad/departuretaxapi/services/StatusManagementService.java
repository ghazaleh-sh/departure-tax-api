package ir.co.sadad.departuretaxapi.services;

import ir.co.sadad.departuretaxapi.dtos.*;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.ExecutionPaymentResDto;
import ir.co.sadad.departuretaxapi.dtos.provider.InitiationPaymentReqDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.PaymentInquiryResDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.PushOrderResDto;
import ir.co.sadad.departuretaxapi.entities.DepartureTaxLog;
import ir.co.sadad.departuretaxapi.entities.DepartureTaxPayment;
import ir.co.sadad.departuretaxapi.entities.DepartureTaxUser;
import ir.co.sadad.departuretaxapi.enums.Channel;
import ir.co.sadad.departuretaxapi.enums.DepartureRequestStatus;
import ir.co.sadad.departuretaxapi.enums.TransactionStatus;
import ir.co.sadad.departuretaxapi.exceptions.DepartureTaxException;
import ir.co.sadad.departuretaxapi.repositories.DepartureTaxLogRepository;
import ir.co.sadad.departuretaxapi.repositories.DepartureTaxPaymentRepository;
import ir.co.sadad.departuretaxapi.repositories.DepartureTaxUserRepository;
import ir.co.sadad.departuretaxapi.services.utilities.DateTimeDepartureFormat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class StatusManagementService {

    private final DepartureTaxUserRepository departureTaxUserRepository;
    private final DepartureTaxPaymentRepository taxPaymentRepository;
    private final DepartureTaxLogRepository departureTaxLogRepository;
    private final ModelMapper modelMapper;
    private final MessageSource messageSource;

    @SneakyThrows
    public String saveTypeInquiryUser(ServiceTypeInquiryResDto result) {
        DepartureTaxUser user = new DepartureTaxUser();
        user.setRequestId(UUID.randomUUID().toString());
        user.setNationalCode(result.getPassengerNationalCode());
        user.setServiceType(result.getServiceTypeCode());
        user.setServiceTypeTitle(result.getServiceTypeTitle());
        user.setAmount(result.getAmount());
        user.setFirstName(result.getPassengerFirstName());
        user.setLastName(result.getPassengerLastName());
        user.setMobile(result.getPassengerMobileNumber());
        user.setChannel(Channel.BAM.getCode());
        user.setRequestStatus(DepartureRequestStatus.typeInquiry);
        user.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());

        DepartureTaxUser savedUser = departureTaxUserRepository.saveAndFlush(user);

        return savedUser.getRequestId();
    }

    public void saveInitiationPaymentOrder(DepartureTaxUser savedUser) {
        try {
            savedUser.setRequestStatus(DepartureRequestStatus.paymentOrder);
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("changing status to paymentOrder has error " + e.getMessage());
        }
    }

    //    @Transactional
    public void saveInitiationPaymentSuccessResult(InitiationPaymentReqDto req, PaymentOrderResDto result) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(result.getRequestId())
                .orElseThrow(() -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
        try {
            DepartureTaxPayment payment = new DepartureTaxPayment();
            modelMapper.map(req, payment);
            payment.setInstructionIdentification(result.getInstructionIdentification());
            DepartureTaxPayment savedPayment = taxPaymentRepository.saveAndFlush(payment);

            savedUser.setRequestStatus(DepartureRequestStatus.paymentInitiated);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            savedUser.setUserPayment(savedPayment);
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("storing success initiated payment has error: " + e.getMessage());
            throw new DepartureTaxException("initiation.payment.db.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void saveInitiationPaymentFailedResult(String requestId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(requestId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
        try {
            savedUser.setRequestStatus(DepartureRequestStatus.paymentInitFailed);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);
        } catch (Exception e) {
            log.error("storing failed initiated payment has error: " + e.getMessage());
        }
    }

    public void saveExecutionPaymentRequest(DepartureTaxUser savedUser) {
        try {
            savedUser.setRequestStatus(DepartureRequestStatus.paymentExeOrder);
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("changing status to paymentExeOrder has error " + e.getMessage());
        }
    }

    //    @Transactional
    public void saveExecutionPaymentSuccessResult(ExecutionPaymentResDto result, String userId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
        try {
            DepartureTaxPayment savedPayment = savedUser.getUserPayment();
            savedPayment.setIdentification(result.getIdentification());
            savedPayment.setInitiatorReference(result.getInitiatorReference());
            savedPayment.setInitiatorName(result.getInitiatorName());
            savedPayment.setInitiationDate(DateTimeDepartureFormat.paymentDate(result.getInitiationDate()));
            savedPayment.setPayeeReference(result.getPayeeReference());
            savedPayment.setTransactionStatus(result.getTransactionStatus());
            savedPayment.setTransactionDescription(result.getTransactionDescription());
            savedPayment.setInstructionDescription(result.getInstructionDescription());
            savedPayment.setTraceId(result.getTraceId());
            taxPaymentRepository.saveAndFlush(savedPayment);

            savedUser.setRequestStatus(DepartureRequestStatus.paymentSuccess);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("storing success execution payment has error: " + e.getMessage());
            //TODO: call job
        }

    }

    public void saveExecutionPaymentFailedResult(ExecutionPaymentResDto result, String requestId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(requestId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
        try {
            DepartureTaxPayment savedPayment = savedUser.getUserPayment();
            if (result != null) {
                savedPayment.setIdentification(result.getIdentification());
                savedPayment.setInitiatorReference(result.getInitiatorReference());
                savedPayment.setInitiatorName(result.getInitiatorName());
                savedPayment.setInitiationDate(DateTimeDepartureFormat.paymentDate(result.getInitiationDate()));
                savedPayment.setPayeeReference(result.getPayeeReference());
                savedPayment.setTransactionStatus(result.getTransactionStatus());  //FAILED
                savedPayment.setTransactionDescription(result.getTransactionDescription());
                savedPayment.setInstructionDescription(result.getInstructionDescription());
                savedPayment.setTraceId(result.getTraceId());
            } else savedPayment.setTransactionStatus(TransactionStatus.ERROR4XX_EXE);

            taxPaymentRepository.saveAndFlush(savedPayment);

            savedUser.setRequestStatus(DepartureRequestStatus.paymentExeFailed);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);
        } catch (Exception e) {
            log.error("storing failed execution payment has error: " + e.getMessage());
            //TODO: call job
        }
    }

    public void saveExecutionPaymentProcessingResult(ExecutionPaymentResDto result, String userId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
        try {
            DepartureTaxPayment savedPayment = savedUser.getUserPayment();
            if (result != null) {
                savedPayment.setIdentification(result.getIdentification());
                savedPayment.setInitiatorReference(result.getInitiatorReference());
                savedPayment.setInitiatorName(result.getInitiatorName());
                savedPayment.setInitiationDate(DateTimeDepartureFormat.paymentDate(result.getInitiationDate()));
                savedPayment.setPayeeReference(result.getPayeeReference());
                savedPayment.setTransactionStatus(result.getTransactionStatus()); //UNKNOWN
                savedPayment.setTransactionDescription(result.getTransactionDescription());
                savedPayment.setInstructionDescription(result.getInstructionDescription());
                savedPayment.setTraceId(result.getTraceId());

            } else savedPayment.setTransactionStatus(TransactionStatus.ERROR5XX_EXE);

            taxPaymentRepository.saveAndFlush(savedPayment);

            savedUser.setRequestStatus(DepartureRequestStatus.PROCESSING);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("changing status to processing in inquiry service has error " + e.getMessage());
            savedUser.setRequestStatus(DepartureRequestStatus.PROCESSING);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);
        }
    }


    public void savePaymentInquiryProcessingResult(PaymentInquiryResDto result, String userId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
        try {
            DepartureTaxPayment savedPayment = savedUser.getUserPayment();
            if (result != null) {
                savedPayment.setInitiationDate(DateTimeDepartureFormat.paymentDate(result.getInitiationDate()));
                savedPayment.setTransactionStatus(result.getTransactionStatus()); //UNKNOWN
                savedPayment.setTraceId(result.getTraceId());
            }
            else savedPayment.setTransactionStatus(TransactionStatus.ERROR5XX_INQUIRY);

            taxPaymentRepository.saveAndFlush(savedPayment);

            savedUser.setRequestStatus(DepartureRequestStatus.PROCESSING);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("storing execution payment has error: " + e.getMessage());
        }

    }


    public void savePaymentInquirySuccessResult(PaymentInquiryResDto result, String userId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));

        try {
            DepartureTaxPayment savedPayment = savedUser.getUserPayment();
            savedPayment.setInitiationDate(DateTimeDepartureFormat.paymentDate(result.getInitiationDate()));
            savedPayment.setTransactionStatus(result.getTransactionStatus());
            savedPayment.setTraceId(result.getTraceId());
            taxPaymentRepository.saveAndFlush(savedPayment);

            savedUser.setRequestStatus(DepartureRequestStatus.paymentSuccess);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("storing success payment inquiry has error: " + e.getMessage());
            //TODO: call job to save data again
        }
    }

    public void savePaymentInquiryFailedResult(PaymentInquiryResDto result, String requestId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(requestId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));

        try {
            DepartureTaxPayment savedPayment = savedUser.getUserPayment();
            if (result != null) {
                savedPayment.setInitiationDate(DateTimeDepartureFormat.paymentDate(result.getInitiationDate()));
                savedPayment.setTransactionStatus(result.getTransactionStatus());  //FAILED
                savedPayment.setTraceId(result.getTraceId());

            } else savedPayment.setTransactionStatus(TransactionStatus.ERROR4XX_INQUIRY);

            taxPaymentRepository.saveAndFlush(savedPayment);

            savedUser.setRequestStatus(DepartureRequestStatus.FAILED);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("storing execution payment has error: " + e.getMessage());
            savedUser.setRequestStatus(DepartureRequestStatus.FAILED);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);
        }

    }

    public void savePushOrderRequest(String userId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
        try {
            savedUser.setRequestStatus(DepartureRequestStatus.pushOrderRequest);
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("changing status to pushOrderRequest has error " + e.getMessage());
        }
    }

    public void savePushOrderSuccessResult(PushOrderResDto result, String userId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));
        try {
            savedUser.setOrderId(result.getOrderId());
            savedUser.setOfflineId(result.getOfflineId());
            savedUser.setReferenceNumber(result.getReferenceNumber());
            savedUser.setRequestStatus(DepartureRequestStatus.SUCCESS);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("storing pushOrder result has error: " + e.getMessage());
            savedUser.setRequestStatus(DepartureRequestStatus.SUCCESS);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);
            //TODO: cal job to store data
        }

    }

    public void savePushOrderFailedResult(String userId) {
        DepartureTaxUser savedUser = departureTaxUserRepository.findByRequestId(userId).orElseThrow(
                () -> new DepartureTaxException("user.not.found", HttpStatus.BAD_REQUEST));

        try {
            savedUser.setRequestStatus(DepartureRequestStatus.UNKNOWN);
            savedUser.setResponseDateTime(DateTimeDepartureFormat.currentUTCDate());
            departureTaxUserRepository.saveAndFlush(savedUser);

        } catch (Exception e) {
            log.error("changing status to unknown in pushOrder has error " + e.getMessage());
        }
    }

    public void saveExceptionLogs(String eName, String eMsg, String reqId, String jsonData, String methodName, String status) {

        String localizedMessage;
        try {
            localizedMessage = messageSource.getMessage(eMsg, null, new Locale("fa"));
        } catch (NoSuchMessageException e) {
            localizedMessage = eMsg;
        }

        try {
            departureTaxLogRepository.save(DepartureTaxLog
                    .builder()
                    .errorClass(eName)
                    .errorMessage(localizedMessage)
                    .requestId(reqId)
                    .methodName(methodName)
                    .serviceResponse(jsonData)
                    .status(status)
                    .build());
        } catch (Exception ex) {
            log.info("--------- error doesn't save into DepartureTaxLog table with message: " + ex.getMessage());

        }
    }
}
