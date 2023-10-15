package ir.co.sadad.departuretaxapi.dtos;

import lombok.Data;

@Data
public class PaymentOrderResDto {

    private String requestId;
    private String instructionIdentification;
    private Boolean isRequiredTan;
}
