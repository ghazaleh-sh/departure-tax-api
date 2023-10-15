package ir.co.sadad.departuretaxapi.controllers;

import ir.co.sadad.departuretaxapi.dtos.*;
import ir.co.sadad.departuretaxapi.enums.DepartureRequestStatus;
import ir.co.sadad.departuretaxapi.services.DepartureTaxService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "${v1API}/departure")
public class DepartureTaxController {

    private DepartureTaxService departureTaxService;

    @GetMapping(value = "/serviceGroup")
    public ResponseEntity<List<DepartureTaxGroupDto>> groupTypeList(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken) {
        List<DepartureTaxGroupDto> response = departureTaxService.DepartureGroupList();

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(value = "/serviceTypeRequests")
    public ResponseEntity<ServiceTypeInquiryResDto> serviceInquiryType(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
            @Valid @RequestBody ServiceTypeInquiryReqDto typeInquiryReqDto) {
        ServiceTypeInquiryResDto response = departureTaxService.serviceTypeInquiry(typeInquiryReqDto, authToken);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(value = "/confirm")
    public ResponseEntity<PaymentOrderResDto> initiateRequest(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
            @RequestHeader(name = HttpHeaders.USER_AGENT) String userAgent,
            @Valid @RequestBody PaymentOrderReqDto orderReqDto) {
        PaymentOrderResDto response = departureTaxService.initiatePayment(orderReqDto, authToken, userAgent);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PatchMapping(value = "/confirm")
    public ResponseEntity<PushOrderFinalResDto> executeRequest(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
            @RequestHeader(name = HttpHeaders.USER_AGENT) String userAgent,
            @Valid @RequestBody PaymentFinalReqDto finalUserReqDto) {
        PushOrderFinalResDto response = departureTaxService.executePaymentAndPushOrder(finalUserReqDto, authToken, userAgent);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping(value = "/history")
    public ResponseEntity<List<DepartureTaxHistoryResDto>> departureHistory(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
            @RequestHeader("ssn") String ssn,
            @RequestParam("pageNumber") String pageNumber, @RequestParam("pageSize") String pageSize,
            @RequestParam(name = "passengerNationalCode", required = false) String passengerNationalCode,
            @RequestParam(name = "fromAccount", required = false) String fromAccount,
            @RequestParam(name = "offlineId", required = false) String offlineId,
            @RequestParam(name = "requestId", required = false) String requestId,
            @RequestParam(name = "traceId", required = false) String traceId,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(value = "amount_gt", required = false) Long amountFrom,
            @RequestParam(value = "amount_lt", required = false) Long amountTo,
            @RequestParam(name = "date_gt", required = false) String dateFrom,
            @RequestParam(name = "date_lt", required = false) String dateTo,
            @RequestParam(value = "status", required = false) DepartureRequestStatus status) {

        DepartureTaxHistoryReqDto req = DepartureTaxHistoryReqDto.builder()
                .pageNumber(Integer.parseInt(pageNumber))
                .pageSize(Integer.parseInt(pageSize))
                .passengerNationalCode(passengerNationalCode)
                .fromAccount(fromAccount)
                .offlineId(offlineId)
                .requestId(requestId)
                .traceId(traceId)
                .status(status)
                .sortBy(sortBy)
                .sort(sort)
                .amountFrom(amountFrom)
                .amountTo(amountTo)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();

        List<DepartureTaxHistoryResDto> response = departureTaxService.departureTaxHistory(req, ssn);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @GetMapping(value = "/inquiryPushOrder")
    public ResponseEntity<PushOrderFinalResDto> inquiryPushOrder(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
            @RequestHeader(name = HttpHeaders.USER_AGENT) String userAgent,
            @RequestParam("requestId") String requestId) {
        PushOrderFinalResDto response = departureTaxService.paymentProcessingInquiry(requestId, authToken, userAgent);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping(value = "/resend-sms")
    public ResponseEntity<Object> resendSms(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
            @RequestHeader(name = HttpHeaders.USER_AGENT) String userAgent,
            @RequestParam("requestId") String requestId) {
        departureTaxService.resendSms(requestId, authToken, userAgent);

        return ResponseEntity.ok().build();
    }
}
