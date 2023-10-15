package ir.co.sadad.departuretaxapi.exceptions;

import org.springframework.http.HttpStatusCode;

public class PaymentTax4xxException extends PaymentTaxException{

    public PaymentTax4xxException(String message, String description, HttpStatusCode httpStatus) {
        super(message, description, httpStatus);
    }

    public PaymentTax4xxException(String message, String description, String item, HttpStatusCode httpStatus) {
        super(message, description, item, httpStatus);
    }
}
