package ir.co.sadad.departuretaxapi.services.providers;

import ir.co.sadad.departuretaxapi.dtos.provider.PushOrderReqDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.PushOrderResDto;
import ir.co.sadad.departuretaxapi.dtos.provider.TypeInquiryReqDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.TypeInquiryResDto;
import ir.co.sadad.departuretaxapi.exceptions.PspDepartureException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@RequiredArgsConstructor
@Service
public class PSPDepartureTaxServiceImpl implements PSPDepartureTaxService {

    private final WebClient webClient;

    @Value(value = "${pushOrder.ServiceTypeInquiry}")
    private String typeInquiryUrl;//="http://localhost:2000/api/v0/ServiceTypeInquiry";

    @Value(value = "${pushOrder.url}")
    private String pushOrderUrl;//="http://localhost:2000/api/v0/PushOrder";


    @Override
    @SneakyThrows
    public TypeInquiryResDto serviceTypeInquiryProvider(TypeInquiryReqDto inquiryReqDto) {
        Mono<TypeInquiryResDto> response;

        response = webClient
                .post()
                .uri(typeInquiryUrl)
                .header("Content-type", "application/json")
                .body(Mono.just(inquiryReqDto),
                        new ParameterizedTypeReference<TypeInquiryReqDto>() {
                        })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res ->
                        res.bodyToMono(PspDepartureException.class)
                                .handle((error, sink) ->
                                        {
                                            log.error("service type inquiry exception : {0}", error);
                                            sink.error(new PspDepartureException(error.getMessage(), HttpStatus.BAD_REQUEST));

                                        }
                                )
                )
                .onStatus(HttpStatusCode::is5xxServerError, res ->
                {
                    log.error("service type inquiry error : {}", res);
                    throw new PspDepartureException("type.inquiry.failed", res.statusCode());
                })
                .bodyToMono(TypeInquiryResDto.class);
        return response.block();

    }

    @Override
    public PushOrderResDto pushOrderProvider(PushOrderReqDto orderReqDto) {
        Mono<PushOrderResDto> response;

        response = webClient
                .post()
                .uri(pushOrderUrl)
                .header("Content-type", "application/json")
                .body(Mono.just(orderReqDto),
                        new ParameterizedTypeReference<PushOrderReqDto>() {
                        })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res ->
                        res.bodyToMono(PspDepartureException.class)
                                .handle((error, sink) ->
                                        {
                                            log.error("push order exception : {0}", error);
                                            sink.error(new PspDepartureException(error.getMessage(), HttpStatus.BAD_REQUEST));

                                        }
                                )
                )
                .onStatus(HttpStatusCode::is5xxServerError, res ->
                {
                    log.error("push order inquiry error : {}", res);
                    throw new PspDepartureException("push.order.failed", res.statusCode());
                })
                .bodyToMono(PushOrderResDto.class);
        return response.block();

    }
}
