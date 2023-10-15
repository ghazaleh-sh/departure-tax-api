package ir.co.sadad.departuretaxapi.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class DepartureTaxException extends RuntimeException {


    private final HttpStatusCode httpStatusCode;
    private Integer code;
    //    private GeneralErrorResponse generalErrorResponse;
    private String description;
    private String item;

    public DepartureTaxException(String message, Integer code, HttpStatusCode httpStatusCode) {
        super(message);
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    public DepartureTaxException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public DepartureTaxException(String message, String description, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.description = description;
    }

    public DepartureTaxException(String message, String description, String item, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.description = description;
        this.item = item;
    }
}
