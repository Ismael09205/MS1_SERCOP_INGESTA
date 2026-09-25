package com.ibm.sercop_ingestion_service.application.exception;

public class ErrorDatoSercopException extends RuntimeException {

    public ErrorDatoSercopException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}