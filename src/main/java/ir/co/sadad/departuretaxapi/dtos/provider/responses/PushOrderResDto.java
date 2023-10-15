package ir.co.sadad.departuretaxapi.dtos.provider.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushOrderResDto implements ThirdPartyServicesResponse {

    @JsonProperty("ResCode")
    private int resCode;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("OrderId")
    private Long orderId;
    @JsonProperty("ReferenceNumber")
    private String referenceNumber;
    @JsonProperty("OfflineId")
    private String offlineId;

}
