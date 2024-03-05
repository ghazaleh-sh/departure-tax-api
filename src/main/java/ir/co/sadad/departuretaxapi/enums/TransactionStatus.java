package ir.co.sadad.departuretaxapi.enums;

public enum TransactionStatus {
    RECEIVED,
    PROCESSING,
    REGISTERED,
    SUCCEEDED,
    FAILED,
    CONTRADICTION,
    CANCELED,

    UNKNOWN,
    ACTIVE,
    ERROR5XX_EXE,
    ERROR4XX_EXE,
    ERROR5XX_INQUIRY,
    ERROR4XX_INQUIRY

}
