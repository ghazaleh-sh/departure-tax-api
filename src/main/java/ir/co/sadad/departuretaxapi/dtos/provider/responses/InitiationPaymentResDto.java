package ir.co.sadad.departuretaxapi.dtos.provider.responses;

import lombok.Data;

import java.util.List;

@Data
public class InitiationPaymentResDto implements ThirdPartyServicesResponse {

    /**
     * شناسه درخواست انتقال
     */
    private String instructionIdentification;
    /**
     * نیاز/ عدم نیاز به ارسال پیامک
     */
    private Boolean isRequiredTan;

}
