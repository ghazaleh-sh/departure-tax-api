package ir.co.sadad.departuretaxapi.dtos.provider.responses;

import lombok.Data;

import java.util.List;

@Data
public class PaymentFailedDto implements ThirdPartyServicesResponse {
    private String message;
    private String code;
    private List<Details> details;

    @Data
    public static class Details {
        private String message;
        private String item;
    }

}
