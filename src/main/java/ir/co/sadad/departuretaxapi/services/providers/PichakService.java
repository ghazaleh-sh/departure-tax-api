package ir.co.sadad.departuretaxapi.services.providers;

import ir.co.sadad.departuretaxapi.dtos.provider.responses.PichakInfoResDto;

public interface PichakService {

    PichakInfoResDto getUserInfo(String nationalCode, String token);
}
