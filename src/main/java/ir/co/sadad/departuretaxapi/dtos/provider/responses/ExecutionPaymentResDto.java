package ir.co.sadad.departuretaxapi.dtos.provider.responses;

import ir.co.sadad.departuretaxapi.enums.InstructionType;
import ir.co.sadad.departuretaxapi.enums.Mechanism;
import ir.co.sadad.departuretaxapi.enums.TransactionStatus;
import lombok.Data;

@Data
public class ExecutionPaymentResDto implements ThirdPartyServicesResponse {
    /**
     *شناسه درخواست انتقال
     */
    private String identification;
    /**
     *کدملی درخواست دهنده
     */
    private String initiatorReference;
    /**
     *نام درخواست دهنده
     */
    private String initiatorName;
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
    /**
     *شماره ملی صاحب حساب مقصد
     */
    private String payeeReference;
    private String targetAccount;
    private int amount;
    private Mechanism mechanism;
    /**
     *وضعیت تراکنش
     */
    private TransactionStatus transactionStatus;
    /**
     *شرح تراکنش
     */
    private String transactionDescription;
    /**
     *شرح دستورالعمل
     */
    private String instructionDescription;
    /**
     *شماره پیگیری تراکنش
     */
    private String traceId;

}
