package com.ibm.sercop_ingestion_service.adapter.out.persistence.mapper;

import com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence.RegistroIngestaSercopEntity;
import com.ibm.sercop_ingestion_service.application.record.RegistroIngestaSercop;
import org.springframework.stereotype.Component;

@Component
public class RegistroMapper {

    public RegistroIngestaSercopEntity toEntity(RegistroIngestaSercop registro) {

        RegistroIngestaSercopEntity entidad = new RegistroIngestaSercopEntity();

        entidad.setProcesosRecibidos(registro.getProcesosRecibidos());
        entidad.setProcesosValidos(registro.getProcesosValidos());
        entidad.setProcesosInvalidos(registro.getProcesosInvalidos());
        entidad.setProcesosDuplicados(registro.getProcesosDuplicados());
        entidad.setProcesosPersistidos(registro.getProcesosPersistidos());
        entidad.setProcesosActualizados(registro.getProcesosActualizados());
        entidad.setProcesosSinCambios(registro.getProcesosSinCambios());
        entidad.setEstado(registro.getEstado());
        entidad.setFechaInicio(registro.getFechaInicio());
        entidad.setFechaFin(registro.getFechaFin());

        return entidad;
    }

    public RegistroIngestaSercop toDomain(RegistroIngestaSercopEntity entidad) {

        RegistroIngestaSercop registro = new RegistroIngestaSercop();

        registro.setProcesosRecibidos(entidad.getProcesosRecibidos());
        registro.setProcesosValidos(entidad.getProcesosValidos());
        registro.setProcesosInvalidos(entidad.getProcesosInvalidos());
        registro.setProcesosDuplicados(entidad.getProcesosDuplicados());
        registro.setProcesosPersistidos(entidad.getProcesosPersistidos());
        registro.setProcesosActualizados(entidad.getProcesosActualizados());
        registro.setProcesosSinCambios(entidad.getProcesosSinCambios());
        registro.setEstado(entidad.getEstado());
        registro.setFechaFin(entidad.getFechaFin());

        return registro;
    }
}