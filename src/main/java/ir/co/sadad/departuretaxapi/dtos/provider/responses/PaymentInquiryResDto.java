package ir.co.sadad.departuretaxapi.dtos.provider.responses;

import ir.co.sadad.departuretaxapi.enums.InstructionType;
import ir.co.sadad.departuretaxapi.enums.Mechanism;
import ir.co.sadad.departuretaxapi.enums.TransactionStatus;
import lombok.Data;

@Data
public class PaymentInquiryResDto implements ThirdPartyServicesResponse {
    /**
     *شناسه درخواست انتقال
     */
    private String instructionIdentification;
    /**
     *تاریخ درخواست
     */
    private String initiationDate;
    private String currency;
    /**
     *نوع دستورالعمل
     */
    private InstructionType instructionType;
    private String fromAccount;
    private String targetAccount;
    private Mechanism mechanism;
    /**
     *وضعیت تراکنش
     */
    private TransactionStatus transactionStatus;
    /**
     *شماره پیگیری تراکنش
     */
    private String traceId;
}
