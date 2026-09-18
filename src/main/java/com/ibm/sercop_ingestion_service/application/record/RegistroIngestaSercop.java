package com.ibm.sercop_ingestion_service.application.record;

import com.ibm.sercop_ingestion_service.domain.enums.EstadoIngestaSercop;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegistroIngestaSercop {

    private final LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private int procesosRecibidos;
    private int procesosValidos;
    private int procesosInvalidos;
    private int procesosPersistidos;
    private int procesosActualizados;
    private int procesosSinCambios;
    private int procesosDuplicados;
    private EstadoIngestaSercop estado;

    public RegistroIngestaSercop() {
        this.fechaInicio = LocalDateTime.now();
        this.estado = EstadoIngestaSercop.EN_PROCESO;
    }

    public void registrarProcesosRecibidos(int cantidad) {
        procesosRecibidos += cantidad;
    }

    public void registrarProcesoValido() {
        procesosValidos++;
    }

    public void registrarProcesoInvalido() {
        procesosInvalidos++;
    }

    public void registrarProcesoPersistido() {
        procesosPersistidos++;
    }

    public void registrarProcesoActualizado() {
        procesosActualizados++;
    }

    public void registrarProcesoSinCambios() {
        procesosSinCambios++;
    }

    public void registrarProcesoDuplicado() {
        procesosDuplicados++;
    }

    public void finalizar() {
        this.fechaFin = LocalDateTime.now();
        this.estado = EstadoIngestaSercop.COMPLETADA;
    }

    public void marcarError() {
        this.fechaFin = LocalDateTime.now();
        this.estado = EstadoIngestaSercop.ERROR;
    }
}