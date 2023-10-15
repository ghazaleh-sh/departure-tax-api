package ir.co.sadad.departuretaxapi.dtos.provider.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeInquiryResDto implements ThirdPartyServicesResponse {

    @JsonProperty("ResCode")
    private Integer resCode;
    @JsonProperty("Message")
    private String message;
    /**
     * عنوان نوع سفر
     */
    @JsonProperty("ServiceTypeTitle")
    private String serviceTypeTitle;
    @JsonProperty("Amount")
    private Long amount;
    /**
     * کد نوع سفر - 7 تا 22
     */
    @JsonProperty("ServiceType")
    private Integer serviceType;

}
