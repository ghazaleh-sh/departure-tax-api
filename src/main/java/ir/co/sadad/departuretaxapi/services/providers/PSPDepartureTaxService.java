package ir.co.sadad.departuretaxapi.services.providers;

import ir.co.sadad.departuretaxapi.dtos.provider.PushOrderReqDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.PushOrderResDto;
import ir.co.sadad.departuretaxapi.dtos.provider.TypeInquiryReqDto;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.TypeInquiryResDto;

public interface PSPDepartureTaxService {

    TypeInquiryResDto serviceTypeInquiryProvider(TypeInquiryReqDto inquiryReqDto);

    PushOrderResDto pushOrderProvider(PushOrderReqDto orderReqDto);

}
