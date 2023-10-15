package ir.co.sadad.departuretaxapi.dtos;

import ir.co.sadad.departuretaxapi.validations.NationalCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ServiceTypeInquiryReqDto {

    @NationalCode
    private String nationalCode;
    /**
     * کد گروه سفر- 1 تا 6
     */
    @NotNull(message = "{group.code.must.not.be.null}")
    @Pattern(regexp = "^[1-6]$", message = "{group.code.pattern.not.valid}")
    private String serviceGroupCode;

    private String mobileNumber;
}
