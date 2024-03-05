package ir.co.sadad.departuretaxapi.services.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.co.sadad.departuretaxapi.dtos.provider.responses.ThirdPartyServicesResponse;

import java.util.Random;

public class ConverterHelper {
    public static final int DEFAULT_CODE = 66;

    public static String createRRN(String sTraceNo) {
        if (sTraceNo == null)
            return null;
        String date = sTraceNo.substring(0, 6);
        String traceNo = sTraceNo.substring(sTraceNo.length() - 4);
        return DEFAULT_CODE + date + traceNo;
    }

    public static String createRRNRandomly() {
        Random random = new Random();
        char[] digits = new char[10];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < 10; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return DEFAULT_CODE + new String(digits);
    }

    public static String convertResponseToJson(ThirdPartyServicesResponse data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (data != null)
                return objectMapper.writeValueAsString(data);
            else return null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
