package ir.co.sadad.departuretaxapi.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class PspDepartureException extends DepartureTaxException {

    public PspDepartureException(String message, Integer code, HttpStatusCode httpStatus) {
        super(message, code, httpStatus);
    }

    public PspDepartureException(String message, HttpStatusCode httpStatus) {
        super(message, httpStatus);
    }

    public PspDepartureException(String message, String description, HttpStatusCode httpStatus) {
        super(message, description, httpStatus);
    }
}
