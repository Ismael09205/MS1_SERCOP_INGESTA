package com.ibm.sercop_ingestion_service.application.validator;

import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
import org.springframework.stereotype.Component;

@Component
public class ProcesoValidadorSercop {

    public boolean esValido(ProcesoContratacion contratacion) {

        if (contratacion == null) {
            return false;
        }

        String titulo = contratacion.getTitulo();
        String descripcion = contratacion.getDescripcion();
        String ocid = contratacion.getOcid();

        if (ocid == null || ocid.isBlank()) {
            System.out.println("El proceso no se puede persistir, sin OCID");
            return false;
        }

        boolean tieneTitulo = titulo != null && !titulo.isBlank();
        boolean tieneDescripcion = descripcion != null && !descripcion.isBlank();

        return tieneTitulo || tieneDescripcion;
    }
}