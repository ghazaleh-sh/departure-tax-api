package ir.co.sadad.departuretaxapi.dtos.provider;

import ir.co.sadad.departuretaxapi.dtos.BasicPspReqDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TypeInquiryReqDto extends BasicPspReqDto {

    private String nationalCode;
    /**
     * کد گروه سفر- 1 تا 6
     */
    private int serviceGroupCode;

}
