package ir.co.sadad.departuretaxapi.dtos;

import ir.co.sadad.departuretaxapi.enums.TripType;
import lombok.Data;

@Data
public class ServiceTypeInquiryResDto {

    private String requestId;
    private String passengerNationalCode;
    private String passengerFirstName;
    private String passengerLastName;
    private String passengerMobileNumber;
    /**
     * کد نوع سفر - 7 تا 22
     */
    private Integer serviceTypeCode;
    /**
     * اطلاعات اضافه مربوط به سفر- enum
     */
    private TripType serviceType;
    /**
     * عنوان نوع سفر
     */
    private String serviceTypeTitle;
    private Long amount;
}
