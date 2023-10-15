package ir.co.sadad.departuretaxapi.exceptions;

import org.springframework.http.HttpStatusCode;

public class PaymentTaxException extends DepartureTaxException {
    public PaymentTaxException(String message, Integer code, HttpStatusCode httpStatus) {
        super(message, code, httpStatus);
    }

    public PaymentTaxException(String message, HttpStatusCode httpStatus) {
        super(message, httpStatus);
    }

    public PaymentTaxException(String message, String description, HttpStatusCode httpStatus) {
        super(message, description, httpStatus);
    }

    public PaymentTaxException(String message, String description, String item, HttpStatusCode httpStatus) {
        super(message, description, item, httpStatus);
    }
}
