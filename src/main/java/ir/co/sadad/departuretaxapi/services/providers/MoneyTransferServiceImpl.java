package ir.co.sadad.departuretaxapi.services.providers;

import ir.co.sadad.departuretaxapi.dtos.provider.*;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.ExecutionPaymentResDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.InitiationPaymentResDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.PaymentFailedDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.PaymentInquiryResDto;
import ir.co.sadad.departuretaxapi.exceptions.PaymentTax4xxException;
import ir.co.sadad.departuretaxapi.exceptions.PaymentTaxException;
import ir.co.sadad.departuretaxapi.services.StatusManagementService;
import ir.co.sadad.departuretaxapi.services.utilities.ConverterHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoneyTransferServiceImpl implements MoneyTransferService {

    private final WebClient webClient;

    private final StatusManagementService statusManagement;

    @Value(value = "${taxPayment.initiation_url}")
    private String initiationUrl;

    @Value(value = "${taxPayment.execute_url}")
    private String executeUrl;

    @Value(value = "${taxPayment.resend_sms_url}")
    private String resendSmsUrl;

    @Value(value = "${taxPayment.inquiry_url}")
    private String inquiryUrl;

    private MultiValueMap<String, String> setHeaders(String token, String userAgent) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, token);
        headers.set(HttpHeaders.USER_AGENT, userAgent);
        return headers;
    }

    @Override
    @SneakyThrows
    public InitiationPaymentResDto paymentOrderProvider(InitiationPaymentReqDto initiationPaymentReqDto, String requestId, String token, String userAgent) {
        Mono<InitiationPaymentResDto> response;

        response = webClient
                .post()
                .uri(initiationUrl)
                .headers(httpHeaders -> httpHeaders.addAll(setHeaders(token, userAgent)))
                .body(Mono.just(initiationPaymentReqDto),
                        new ParameterizedTypeReference<InitiationPaymentReqDto>() {
                        })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> handle4xxError(res, requestId, "paymentInitiation"))
                .onStatus(HttpStatusCode::is5xxServerError, res -> handle5xxError(res, requestId, "paymentInitiation"))

                .bodyToMono(InitiationPaymentResDto.class);
        return response.block();

    }

    @Override
    @SneakyThrows
    public ExecutionPaymentResDto paymentFinalProvider(String instructionIdentification, String requestId, ExecutionPaymentReqDto executionPaymentReqDto, String token, String userAgent) {
        Mono<ExecutionPaymentResDto> response;

        response = webClient
                .put()
                .uri(executeUrl + instructionIdentification + "/control")
                .headers(httpHeaders -> httpHeaders.addAll(setHeaders(token, userAgent)))
                .body(Mono.just(executionPaymentReqDto),
                        new ParameterizedTypeReference<ExecutionPaymentReqDto>() {
                        })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> handle4xxError(res, requestId, "paymentExecution"))
                .onStatus(HttpStatusCode::is5xxServerError, res -> handle5xxError(res, requestId, "paymentExecution"))

                .bodyToMono(ExecutionPaymentResDto.class);
        return response.block();

    }

    @Override
    @SneakyThrows
    public void resendSmsProvider(String instructionIdentification, String requestId, String token, String userAgent) {

        webClient
                .post()
                .uri(resendSmsUrl, uri -> uri.queryParam("instructionIdentification", instructionIdentification).build())
                .headers(httpHeaders -> httpHeaders.addAll(setHeaders(token, userAgent)))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> handle4xxError(res, requestId, "resendSms"))
                .onStatus(HttpStatusCode::is5xxServerError, res -> handle5xxError(res, requestId, "resendSms"))

                .bodyToMono(Void.class)
                .block();
    }

    @Override
    @SneakyThrows
    public PaymentInquiryResDto paymentInquiry(String instructionIdentification, String requestId, String token, String userAgent) {
        Mono<PaymentInquiryResDto> response;

        response = webClient
                .get()
                .uri(inquiryUrl + instructionIdentification)
                .headers(httpHeaders -> httpHeaders.addAll(setHeaders(token, userAgent)))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> handle4xxError(res, requestId, "paymentInquiry"))
                .onStatus(HttpStatusCode::is5xxServerError, res -> handle5xxError(res, requestId, "paymentInquiry"))

                .bodyToMono(PaymentInquiryResDto.class);
        return response.block();
    }

//    public void save4xxException(PaymentFailedDto errorDto, String requestId, String serviceName){
//        log.error("4xx Error in save4xxException method: {}", errorDto);
//
//        switch (serviceName) {
//            case "paymentInitiation" -> statusManagement.saveInitiationPaymentFailedResult(requestId);
//            case "paymentExecution" -> statusManagement.saveExecutionPaymentFailedResult(null, requestId);
//            case "paymentInquiry" -> statusManagement.savePaymentInquiryFailedResult(null, requestId);
//        }
//
//        statusManagement.saveExceptionLogs("PaymentTax4xxException", errorDto.getMessage(), requestId,
//                ConverterHelper.convertResponseToJson(errorDto), serviceName, "EXCEPTION");
//    }


//    public void save5xxException(PaymentFailedDto errorDto, String requestId, String serviceName){
//        log.error("5xx Error in save5xxException method: {}", errorDto);
//
//        switch (serviceName) {
//            case "paymentInitiation" -> statusManagement.saveInitiationPaymentFailedResult(requestId);
//            case "paymentExecution" -> statusManagement.saveExecutionPaymentProcessingResult(null, requestId);
//            case "paymentInquiry" -> statusManagement.savePaymentInquiryProcessingResult(null, requestId);
//        }
//
//        statusManagement.saveExceptionLogs("PaymentTax5xxException", errorDto.getMessage(), requestId,
//                ConverterHelper.convertResponseToJson(errorDto), serviceName, "EXCEPTION");
//    }

    @SneakyThrows
    private Mono<? extends Throwable> handle4xxError(ClientResponse response, String requestId, String serviceName) {
        log.error("4xx Error in save4xxException method");

        switch (serviceName) {
            case "paymentInitiation" -> statusManagement.saveInitiationPaymentFailedResult(requestId);
            case "paymentExecution" -> statusManagement.saveExecutionPaymentFailedResult(null, requestId);
            case "paymentInquiry" -> statusManagement.savePaymentInquiryFailedResult(null, requestId);
        }

        return response.bodyToMono(PaymentFailedDto.class)
                .flatMap(errorDto -> {
                    log.error("4xx Error: {}", errorDto);
                    statusManagement.saveExceptionLogs("PaymentTax4xxException", errorDto.getMessage(), requestId,
                            ConverterHelper.convertResponseToJson(errorDto), serviceName, "EXCEPTION");
                    return Mono.error(new PaymentTax4xxException(errorDto.getMessage(),
                            errorDto.getDetails() != null ? errorDto.getDetails().get(0).getMessage() : null,
                            errorDto.getDetails() != null ? errorDto.getDetails().get(0).getItem() : null,
                            response.statusCode()));
                });

    }

    @SneakyThrows
    private Mono<? extends Throwable> handle5xxError(ClientResponse response, String requestId, String serviceName) {
        log.error("5xx Error in payment service");

        switch (serviceName) {
            case "paymentInitiation" -> statusManagement.saveInitiationPaymentFailedResult(requestId);
            case "paymentExecution" -> statusManagement.saveExecutionPaymentProcessingResult(null, requestId);
            case "paymentInquiry" -> statusManagement.savePaymentInquiryProcessingResult(null, requestId);
        }

        return response.bodyToMono(PaymentFailedDto.class)
                .flatMap(errorDto -> {
                    log.error("5xx Error: {}", errorDto);
                    statusManagement.saveExceptionLogs("PaymentTax5xxException", errorDto.getMessage(), requestId,
                            ConverterHelper.convertResponseToJson(errorDto), serviceName, "EXCEPTION");
                    return Mono.error(new PaymentTaxException(serviceName + "payment.failed", errorDto.getMessage(),
                            null, response.statusCode()));
                });
    }
}
