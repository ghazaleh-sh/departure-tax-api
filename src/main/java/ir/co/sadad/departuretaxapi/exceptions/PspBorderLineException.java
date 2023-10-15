package ir.co.sadad.departuretaxapi.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class PspBorderLineException extends PspDepartureException{
    public PspBorderLineException(String message, HttpStatusCode httpStatus) {
        super(message, httpStatus);
    }
}
