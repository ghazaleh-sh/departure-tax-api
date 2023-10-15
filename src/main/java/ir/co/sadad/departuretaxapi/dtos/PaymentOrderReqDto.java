package ir.co.sadad.departuretaxapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentOrderReqDto {

    @NotBlank(message = "{from.account.must.not.be.null}")
    private String fromAccount;

    @NotBlank(message = "{request.id.must.not.be.null}")
    private String requestId;

}
