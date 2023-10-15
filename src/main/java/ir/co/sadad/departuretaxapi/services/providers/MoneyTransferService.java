package ir.co.sadad.departuretaxapi.services.providers;

import ir.co.sadad.departuretaxapi.dtos.provider.ExecutionPaymentReqDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.ExecutionPaymentResDto;
import ir.co.sadad.departuretaxapi.dtos.provider.InitiationPaymentReqDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.InitiationPaymentResDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.PaymentInquiryResDto;

public interface MoneyTransferService {

    InitiationPaymentResDto paymentOrderProvider(InitiationPaymentReqDto initiationPaymentReqDto, String requestId, String token, String userAgent);

    ExecutionPaymentResDto paymentFinalProvider(String instructionIdentification, String requestId, ExecutionPaymentReqDto executionPaymentReqDto, String token, String userAgent);

    void resendSmsProvider(String instructionIdentification, String requestId, String token, String userAgent);

    PaymentInquiryResDto paymentInquiry(String instructionIdentification, String requestId, String token, String userAgent);
}
