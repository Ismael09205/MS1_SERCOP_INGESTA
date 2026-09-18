package com.ibm.sercop_ingestion_service.domain.entities;

import com.ibm.sercop_ingestion_service.domain.enums.EstadoIngestaSercop;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EstadoIngesta {

    private Long id;
    private int anio;
    private int ultimaPaginaProcesada;
    private long totalPaginas;
    private EstadoIngestaSercop estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaFin;
}