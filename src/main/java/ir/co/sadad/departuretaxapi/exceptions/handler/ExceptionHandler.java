package ir.co.sadad.departuretaxapi.exceptions.handler;

import ir.co.sadad.departuretaxapi.exceptions.DepartureTaxException;
import ir.co.sadad.departuretaxapi.exceptions.PaymentTax4xxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import jakarta.validation.*;

import java.net.ConnectException;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandler {

    private final MessageSource messageSource;

    @org.springframework.web.bind.annotation.ExceptionHandler(DepartureTaxException.class)
    public ResponseEntity<GeneralExceptionResponse> handleCoreServiceException(DepartureTaxException ex) {

        String localizedMessage;
        try {
            localizedMessage = messageSource.getMessage(ex.getMessage(), null, new Locale("fa"));
        } catch (NoSuchMessageException e) {
            localizedMessage = ex.getMessage();
        }


        List<GeneralExceptionResponse.SubError> subErrorList = new ArrayList<>();
        if (ex.getDescription() != null) {
            GeneralExceptionResponse.SubError subError = new GeneralExceptionResponse.SubError();
            try {
                subError.setLocalizedMessage(messageSource.getMessage(Objects.requireNonNull(ex.getDescription()), null, new Locale("fa")));
            } catch (NoSuchMessageException exp) {
                subError.setLocalizedMessage(ex.getDescription());
            }
            subError.setItem(ex.getItem());
            subErrorList.add(subError);
        }


        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
        generalErrorResponse
                .setStatus(ex.getHttpStatusCode())
                .setTimestamp(new Date().getTime())
                .setCode((ex.getCode() == null ? ex.getHttpStatusCode().value() : ex.getCode()) + "PO")
                .setLocalizedMessage(localizedMessage)
                .setMessage(ex.getMessage())
                .setSubErrors(subErrorList);

        return new ResponseEntity<>(generalErrorResponse, ex.getHttpStatusCode());

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(PaymentTax4xxException.class)
    public ResponseEntity<GeneralExceptionResponse> handlePayment4xxServiceException(PaymentTax4xxException ex) {

        String localizedMessage;
        try {
            if (ex.getDescription() != null)
                localizedMessage = messageSource.getMessage(ex.getDescription(), null, new Locale("fa"));
            else
                localizedMessage = messageSource.getMessage(ex.getMessage(), null, new Locale("fa"));
        } catch (NoSuchMessageException e) {
            localizedMessage = ex.getDescription();
        }

        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
        generalErrorResponse
                .setStatus(ex.getHttpStatusCode())
                .setTimestamp(new Date().getTime())
                .setCode((ex.getCode() == null ? ex.getHttpStatusCode().value() : ex.getCode()) + "PO")
                .setLocalizedMessage(localizedMessage)
                .setMessage(ex.getMessage());

        return new ResponseEntity<>(generalErrorResponse, ex.getHttpStatusCode());

    }


    @org.springframework.web.bind.annotation.ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<GeneralExceptionResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String localizedMessage;
        try {
            localizedMessage = messageSource.getMessage(ex.getMessage(), null, new Locale("fa"));
        } catch (NoSuchMessageException e) {
            localizedMessage = ex.getMessage();
        }
        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
        generalErrorResponse
                .setStatus(BAD_REQUEST)
                .setTimestamp(new Date().getTime())
                .setCode(messageSource.getMessage("parameter.has.error", null, new Locale("fa")))
                .setLocalizedMessage(localizedMessage)
                .setMessage(ex.getMessage());


        for (ConstraintViolation constraintViolation : ex.getConstraintViolations()) {

            List<GeneralExceptionResponse.SubError> subErrorList = new ArrayList<>();

            GeneralExceptionResponse.SubError subError = new GeneralExceptionResponse.SubError();
            subError.setMessage(constraintViolation.getMessage());
            subError.setItem("item");
            try {
                subError.setLocalizedMessage(messageSource.getMessage(Objects.requireNonNull(constraintViolation.getMessage()),
                        null, new Locale("fa")));
            } catch (NoSuchMessageException exp) {
                subError.setLocalizedMessage(constraintViolation.getMessage());
            }
            subErrorList.add(subError);
            generalErrorResponse.setSubErrors(subErrorList);
        }

        return new ResponseEntity<>(generalErrorResponse, BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ConnectException.class)
    public ResponseEntity<GeneralExceptionResponse> handleConnectException(ConnectException ex) {
        log.warn("Connection Timeout Exception: ", ex);

        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
        generalErrorResponse
                .setStatus(HttpStatus.REQUEST_TIMEOUT)
                .setTimestamp(new Date().getTime())
                .setCode(HttpStatus.REQUEST_TIMEOUT.value() + "PO")
                .setMessage(ex.getMessage())
                .setLocalizedMessage(messageSource.getMessage("core.service.timeout.exception", null, new Locale("fa")));

        return new ResponseEntity<>(generalErrorResponse, HttpStatus.REQUEST_TIMEOUT);

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(JDBCConnectionException.class)
    public ResponseEntity<GeneralExceptionResponse> handleJDBCConnectionException(JDBCConnectionException ex) {
        log.warn("JDBC Connection Exception: ", ex);

        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
        generalErrorResponse
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .setTimestamp(new Date().getTime())
                .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value() + "PO")
                .setMessage(ex.getMessage())
                .setLocalizedMessage(messageSource.getMessage("database.connection.exception", null, new Locale("fa")));

        return new ResponseEntity<>(generalErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GeneralExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("api calling exception", ex);

        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
        generalErrorResponse
                .setStatus(HttpStatus.BAD_REQUEST)
                .setTimestamp(new Date().getTime())
                .setCode(HttpStatus.BAD_REQUEST.value() + "PO")
                .setMessage(ex.getMessage())
                .setLocalizedMessage(messageSource.getMessage("http.message.not.readable.exception", null, new Locale("fa")));

        return new ResponseEntity<>(generalErrorResponse, HttpStatus.BAD_REQUEST);

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralExceptionResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.warn("validation exception", ex);
        String generalMsg = messageSource.getMessage("method.argument.not.valid", null, new Locale("fa"));

        List<GeneralExceptionResponse.SubError> subErrorList = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            GeneralExceptionResponse.SubError subError = new GeneralExceptionResponse.SubError();
            subError.setMessage(error.getDefaultMessage());
            subError.setItem(error.getField());
            try {
                subError.setLocalizedMessage(messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), null, new Locale("fa")));
            } catch (NoSuchMessageException exp) {
                subError.setLocalizedMessage(error.getDefaultMessage());
            }
            subErrorList.add(subError);
        });

        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
        generalErrorResponse
                .setStatus(HttpStatus.BAD_REQUEST)
                .setTimestamp(new Date().getTime())
                .setCode("E" + HttpStatus.BAD_REQUEST.value() + "PO")
//                .setMessage(ex.getMessage())
                .setLocalizedMessage(generalMsg)
                .setSubErrors(subErrorList);
        return new ResponseEntity<>(generalErrorResponse, HttpStatus.BAD_REQUEST);

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<GeneralExceptionResponse> handleMissingRequestHeaderExceptions(
            MissingRequestHeaderException ex) {

        log.warn("missing request header exception", ex);

        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
        generalErrorResponse
                .setStatus(HttpStatus.BAD_REQUEST)
                .setTimestamp(new Date().getTime())
                .setCode(HttpStatus.BAD_REQUEST.value() + "PO")
                .setMessage(ex.getMessage())
                .setLocalizedMessage(ex.getLocalizedMessage());
        return new ResponseEntity<>(generalErrorResponse, HttpStatus.BAD_REQUEST);

    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GeneralExceptionResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        log.error("validation exception", ex.getRootCause());

        return handleCoreServiceException(new DepartureTaxException("unknown.error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

//    @org.springframework.web.bind.annotation.ExceptionHandler(HttpClientErrorException.class)
//    public ResponseEntity<GeneralExceptionResponse> handleHttpClientErrorException(
//            HttpClientErrorException ex) throws JsonProcessingException {
//
//        HttpClientErrorDto httpClientErrorDto = new ObjectMapper().readValue(ex.getResponseBodyAsString(), HttpClientErrorDto.class);
//
//        List<GeneralExceptionResponse.SubError> subErrorList = new ArrayList<>();
//        httpClientErrorDto.getErrors().forEach((error) -> {
//            GeneralExceptionResponse.SubError subError = new GeneralExceptionResponse.SubError();
//            subError.setCode("E" + ex.getStatusCode().value() + "CRTLL");
//            subError.setTimestamp(new Date().getTime());
//            subError.setLocalizedMessage(error.getField() + " " + error.getDefaultMessage());
//            subErrorList.add(subError);
//        });
//
//        GeneralExceptionResponse generalErrorResponse = new GeneralExceptionResponse();
//        generalErrorResponse
//                .setStatus(ex.getStatusCode())
//                .setTimestamp(new Date().getTime())
//                .setCode("E" + ex.getStatusCode().value() + "CRTLL")
//                .setLocalizedMessage(messageSource.getMessage("method.argument.not.valid", null, new Locale("fa")))
//                .setSubErrors(subErrorList);
//        return new ResponseEntity<>(generalErrorResponse, ex.getStatusCode());
//
//    }

}