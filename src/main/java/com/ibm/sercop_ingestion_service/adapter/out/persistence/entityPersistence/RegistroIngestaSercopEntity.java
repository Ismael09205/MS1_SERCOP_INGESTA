package com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence;

import com.ibm.sercop_ingestion_service.domain.enums.EstadoIngestaSercop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_ingesta_sercop")
@Getter
@Setter
public class RegistroIngestaSercopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "procesos_recibidos", nullable = false)
    private int procesosRecibidos;

    @Column(name = "procesos_validos", nullable = false)
    private int procesosValidos;

    @Column(name = "procesos_invalidos", nullable = false)
    private int procesosInvalidos;

    @Column(name = "procesos_duplicados", nullable = false)
    private int procesosDuplicados;

    @Column(name = "procesos_persistidos", nullable = false)
    private int procesosPersistidos;

    @Column(name = "procesos_actualizados", nullable = false)
    private int procesosActualizados;

    @Column(name = "procesos_sin_cambios", nullable = false)
    private int procesosSinCambios;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoIngestaSercop estado;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
}

