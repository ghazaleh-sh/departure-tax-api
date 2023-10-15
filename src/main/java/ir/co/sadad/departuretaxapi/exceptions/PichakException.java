package ir.co.sadad.departuretaxapi.exceptions;

import org.springframework.http.HttpStatusCode;

public class PichakException extends DepartureTaxException{

    public PichakException(String message, Integer code, HttpStatusCode httpStatus) {
        super(message, code, httpStatus);
    }
}
