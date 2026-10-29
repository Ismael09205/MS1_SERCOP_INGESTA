package com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence;

import com.ibm.sercop_ingestion_service.domain.enums.CpcCategoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "proceso_contratacion")
public class ProcesoContratacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "identificador_sercop")
    private Long identificadorSercop;
    @Column(nullable = false, unique = true)
    private String ocid;
    private int anio;
    private int mes;
    private String metodo;
    @Column(name = "tipo_interno", columnDefinition = "TEXT")
    private String tipoInterno;
    private String localidad;
    private String region;
    @Column(columnDefinition = "TEXT")
    private String proveedores;
    private String comprador;
    private BigDecimal monto;
    private OffsetDateTime fecha;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private BigDecimal presupuesto;
    // Código CPC principal del proceso
    @Column(name = "codigo_cpc")
    private String codigoCpc;
    // Categoría de software/cloud resuelta a partir del código CPC
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_cpc")
    private CpcCategoria categoriaCpc;
}