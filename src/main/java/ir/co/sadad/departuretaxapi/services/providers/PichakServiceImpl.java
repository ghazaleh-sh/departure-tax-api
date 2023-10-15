package ir.co.sadad.departuretaxapi.services.providers;

import ir.co.sadad.departuretaxapi.dtos.provider.responses.PichakInfoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class PichakServiceImpl implements PichakService {

    private final WebClient webClient;

    @Value(value = "${pichak.url}")
    private String pichakUrl;

    @Override
    public PichakInfoResDto getUserInfo(String nationalCode, String token) {
        Mono<PichakInfoResDto> response;

        try {
            response = webClient
                    .get()
                    .uri(pichakUrl + nationalCode)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(PichakInfoResDto.class);
            return response.block();

        } catch (Exception e) {
            e.getStackTrace();
            log.error("pichak failed with this error: {}" + e.getMessage());
            return null;
        }
    }
}
