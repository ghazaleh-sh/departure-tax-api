package ir.co.sadad.departuretaxapi.dtos.provider;

import ir.co.sadad.departuretaxapi.dtos.BasicPspReqDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PushOrderReqDto extends BasicPspReqDto {

    private Long amount;
    private String firstName;
    private String lastName;
    private int serviceType;
    private String nationalCode;
    private String transactionDateTime;
    private String referenceNumber;
    private String branchCode;
    private String userName;
    private int channel;
    private String mobile;
    private String email;
    private String terminalId;
    private String systemTraceNo;
}
