package ir.co.sadad.departuretaxapi.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentFinalReqDto {

    @NotBlank(message = "{instruction.id.must.not.be.null}")
    private String instructionIdentification;

    @NotBlank(message = "{request.id.must.not.be.null}")
    private String requestId;

    private String authorizationCode;
}
