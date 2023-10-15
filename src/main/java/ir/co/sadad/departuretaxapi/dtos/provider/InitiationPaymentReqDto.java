package ir.co.sadad.departuretaxapi.dtos.provider;

import ir.co.sadad.departuretaxapi.enums.InstructionType;
import ir.co.sadad.departuretaxapi.enums.Mechanism;
import ir.co.sadad.departuretaxapi.enums.ProductType;
import ir.co.sadad.departuretaxapi.enums.Usage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitiationPaymentReqDto {

    /**
     * نوع انتقال
     */
    private InstructionType instructionType;
    /**
     * مکانیزم انتقال
     */
    private Mechanism mechanism;
    private String fromAccount;
    private String targetAccount;
    private String amount;
    private String currency;
    private String descriptionInstruction;
    /**
     *کد شرح تراکنش
     */
    private String smtCode;
    /**
     *  نوع حساب
     */
    private ProductType productType;
    /**
     *  بابت
     */
    private String purpose;
    /**
     * شناسه واریز
     */
    private String creditPayId;
    /**
     *  شناسه برداشت
     */
    private String debitPayId;
    /**
     *  نوع پرداخت
     */
    private Usage usage;
}
