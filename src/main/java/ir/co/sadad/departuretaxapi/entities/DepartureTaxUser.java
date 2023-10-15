package ir.co.sadad.departuretaxapi.entities;

import ir.co.sadad.departuretaxapi.enums.DepartureRequestStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author G.ShahrokhAbadi on 19/7/2023
 */
@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "DEPARTURE_TAX_USER")
public class DepartureTaxUser extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", columnDefinition = "BINARY(16)")
    private String requestId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "SERVICE_TYPE", nullable = false)
    private int serviceType;
    @Column(name = "SERVICE_TYPE_TITLE")
    private String serviceTypeTitle;
    @Column(name = "AMOUNT")
    private Long amount;
    @Column(name = "NATIONAL_CODE", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String nationalCode;
    @Column(name = "MOBILE")
    private String mobile;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;
    @Column(name = "PUSH_ORDER_ID")
    private Long orderId;
    @Column(name = "OFFLINE_ID")
    private String offlineId;
    @Enumerated(EnumType.STRING)
    @Column(name = "REQUEST_STATUS", nullable = false)
    private DepartureRequestStatus requestStatus;
    @Column(name = "BRANCH_CODE")
    private String branchCode;
    @Column(name = "CHANNEL")
    private int channel;
    @Column(name = "RESPONSE_DATE_TIME")
    private String responseDateTime;


    @OneToOne
    @JoinColumn(name = "PAYMENT_ID", foreignKey = @ForeignKey(name = "FKDEPARTURE_TAX_USER_TO_PAYMENT"))
    private DepartureTaxPayment userPayment;
}
