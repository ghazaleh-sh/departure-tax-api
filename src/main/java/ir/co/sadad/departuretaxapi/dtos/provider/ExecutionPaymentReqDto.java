package ir.co.sadad.departuretaxapi.dtos.provider;

import ir.co.sadad.departuretaxapi.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecutionPaymentReqDto {

    /**
     * وضعیت درخواست
     */
    private TransactionStatus status;
    /**
     *کد احراز پیامکی
     */
    private String authorizationCode;
}
