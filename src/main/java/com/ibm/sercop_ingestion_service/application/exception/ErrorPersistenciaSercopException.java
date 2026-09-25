package com.ibm.sercop_ingestion_service.application.exception;

public class ErrorPersistenciaSercopException extends RuntimeException {

    public ErrorPersistenciaSercopException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}