package ir.co.sadad.departuretaxapi.entities;

import ir.co.sadad.departuretaxapi.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author G.ShahrokhAbadi on 19/7/2023
 */
@Getter
@Setter
@Entity
@Table(name = "DEPARTURE_TAX_PAYMENT")
public class DepartureTaxPayment extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long paymentId;
    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_STATUS")
    private TransactionStatus transactionStatus;
    @Column(name = "FROM_ACCOUNT", nullable = false)
    private String fromAccount;
    @Column(name = "INSTRUCTION_IDENTIFICATION")
    private String instructionIdentification;
    @Enumerated(EnumType.STRING)
    @Column(name = "INSTRUCTION_type", nullable = false)
    private InstructionType instructionType;
    @Column(name = "AMOUNT", nullable = false)
    private Long amount;
    @Column(name = "CURRENCY", nullable = false)
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(name = "MECHANISM", nullable = false)
    private Mechanism mechanism;
    @Column(name = "IDENTIFICATION")
    private String identification;
    @Column(name = "INITIATOR_REFERENCE")
    private String initiatorReference;
    @Column(name = "INITIATOR_NAME")
    private String initiatorName;
    @Column(name = "INITIATION_DATE")
    private String initiationDate;
    @Column(name = "PAYEE_REFERENCE")
    private String payeeReference;
    @Column(name = "TRANSACTION_DESCRIPTION")
    private String transactionDescription;
    @Column(name = "INSTRUCTION_DESCRIPTION")
    private String instructionDescription;
    @Column(name = "TRACE_ID")
    private String traceId;
    @Column(name = "PURPOSE")
    private String purpose;
    @Enumerated(EnumType.STRING)
    @Column(name = "USAGE_TYPE")
    private Usage usage;
    @Enumerated(EnumType.STRING)
    @Column(name = "PRODUCT_TYPE")
    private ProductType productType;
    @Column(name = "SMT_CODE")
    private String smtCode;
    @Column(name = "CREDITPAY_ID")
    private String creditPayId;
    @Column(name = "DEBITPAY_ID")
    private String debitPayId;

}
