package com.ibm.sercop_ingestion_service.application.exception;

public class ErrorIngestaException extends RuntimeException {

    public ErrorIngestaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}