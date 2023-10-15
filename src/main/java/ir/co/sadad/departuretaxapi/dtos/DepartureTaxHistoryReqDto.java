package ir.co.sadad.departuretaxapi.dtos;

import ir.co.sadad.departuretaxapi.enums.DepartureRequestStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartureTaxHistoryReqDto {

    private int pageNumber;
    private int pageSize;
    private String passengerNationalCode;
    private String fromAccount;
    private String offlineId;
    private String requestId;
    private String traceId;
    private DepartureRequestStatus status;
    private String sortBy;
    private String sort;
    private Long amountFrom;
    private Long amountTo;
    private String dateFrom;
    private String dateTo;
}
