package ir.co.sadad.departuretaxapi.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DEPARTURE_TAX_LOG")
public class DepartureTaxLog extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ERROR_CLASS")
    private String errorClass;

    @Column(name = "ERROR_MESSAGE", length = 1000)
    private String errorMessage;

    @Column(name = "REQUEST_ID")
    private String requestId;

    @Column(name = "METHOD_NAME")
    private String methodName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "SERVICE_RESPONSE", columnDefinition = "CLOB")
    private String serviceResponse;
}
