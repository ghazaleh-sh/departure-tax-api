package ir.co.sadad.departuretaxapi.dtos;

import ir.co.sadad.departuretaxapi.enums.DepartureRequestStatus;
import ir.co.sadad.departuretaxapi.enums.TransactionStatus;
import ir.co.sadad.departuretaxapi.enums.TripType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PushOrderFinalResDto {

    private Long orderId;
    private String referenceNumber;
    private String offlineId;
    private String requestId;
    private String instructionIdentification;
    private String identification;
    private String initiatorReference;
    private String initiatorName;
    private String initiationDate;
    private String currency;
    private String fromAccount;
    private String traceId;
    private TransactionStatus transactionStatus;
    private String passengerNationalCode;
    private String passengerFirstName;
    private String passengerLastName;
    private String passengerMobileNumber;
    private int serviceTypeCode;
    private TripType serviceType;
    private String serviceTypeTitle;
    private Long amount;
    private String responseDateTime;
    private DepartureRequestStatus status;

}
