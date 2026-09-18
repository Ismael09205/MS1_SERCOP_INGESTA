package com.ibm.sercop_ingestion_service.application.port.out;

import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;

import java.util.List;
import java.util.Optional;

public interface ProcesoContratacionPersistencePort {

    ProcesoContratacion guardarProceso(ProcesoContratacion procesoContratacion);

    ProcesoContratacion actualizarProceso(ProcesoContratacion procesoContratacion);

    Optional<ProcesoContratacion> buscarPorOcid(String ocid);

    List<ProcesoContratacion> buscarPorOcids(List<String> ocids);

    List<ProcesoContratacion> guardarProcesos(List<ProcesoContratacion> procesos);

    List<ProcesoContratacion> actualizarProcesos(List<ProcesoContratacion> procesos);
}