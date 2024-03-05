package ir.co.sadad.departuretaxapi.services;

import ir.co.sadad.departuretaxapi.dtos.*;

import java.util.List;

public interface DepartureTaxService {

    List<DepartureTaxGroupDto> DepartureGroupList();

    ServiceTypeInquiryResDto serviceTypeInquiry(ServiceTypeInquiryReqDto typeInquiryReqDto, String authToken);

    PaymentOrderResDto initiatePayment(PaymentOrderReqDto orderReqDto, String token, String userAgent);

    PushOrderFinalResDto executePaymentAndPushOrder(PaymentFinalReqDto finalUserReqDto, String token, String userAgent);

    PushOrderFinalResDto pushOrderRequest(String requestId, String traceId, String initiateDate, Boolean inquiry);

    List<DepartureTaxHistoryResDto> departureTaxHistory(DepartureTaxHistoryReqDto req, String ssn);

    void resendSms(String requestId, String token, String userAgent);

    PushOrderFinalResDto paymentProcessingInquiry(String requestId, String token, String userAgent);
}
