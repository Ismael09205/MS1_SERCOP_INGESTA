package com.ibm.sercop_ingestion_service.domain.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class ProcesoContratacion {
    private Long identificador;
    private String ocid;
    private int anio;
    private int mes;
    private String metodo;
    private String tipoInterno;
    private String localidad;
    private String region;
    private String proveedores;
    private String comprador;
    private BigDecimal monto;
    private OffsetDateTime fecha;
    private String titulo;
    private String descripcion;
    private BigDecimal presupuesto;
}