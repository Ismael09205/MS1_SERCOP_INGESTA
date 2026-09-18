package com.ibm.sercop_ingestion_service.application.validator;

import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
@Component
public class ComparadorProcesoContratacion {

    public boolean tieneCambios(ProcesoContratacion actual, ProcesoContratacion nuevo) {

        boolean hayCambios = false;

        if (!Objects.equals(actual.getIdentificador(), nuevo.getIdentificador())) {
            log.info("OCID '{}': identificador: '{}' -> '{}'", nuevo.getOcid(), actual.getIdentificador(), nuevo.getIdentificador());
            hayCambios = true;
        }

        if (actual.getAnio() != nuevo.getAnio()) {
            log.info("OCID '{}': anio: '{}' -> '{}'", nuevo.getOcid(), actual.getAnio(), nuevo.getAnio());
            hayCambios = true;
        }

        if (actual.getMes() != nuevo.getMes()) {
            log.info("OCID '{}': mes: '{}' -> '{}'", nuevo.getOcid(), actual.getMes(), nuevo.getMes());
            hayCambios = true;
        }

        if (!Objects.equals(actual.getMetodo(), nuevo.getMetodo())) {
            log.info("OCID '{}': metodo: '{}' -> '{}'", nuevo.getOcid(), actual.getMetodo(), nuevo.getMetodo());
            hayCambios = true;
        }

        if (!Objects.equals(actual.getTipoInterno(), nuevo.getTipoInterno())) {
            log.info("OCID '{}': tipoInterno: '{}' -> '{}'", nuevo.getOcid(), actual.getTipoInterno(), nuevo.getTipoInterno());
            hayCambios = true;
        }

        if (!Objects.equals(actual.getLocalidad(), nuevo.getLocalidad())) {
            log.info("OCID '{}': localidad: '{}' -> '{}'", nuevo.getOcid(), actual.getLocalidad(), nuevo.getLocalidad());
            hayCambios = true;
        }

        if (!Objects.equals(actual.getRegion(), nuevo.getRegion())) {
            log.info("OCID '{}': region: '{}' -> '{}'", nuevo.getOcid(), actual.getRegion(), nuevo.getRegion());
            hayCambios = true;
        }

        if (!Objects.equals(actual.getProveedores(), nuevo.getProveedores())) {
            log.info("OCID '{}': proveedores: '{}' -> '{}'", nuevo.getOcid(), actual.getProveedores(), nuevo.getProveedores());
            hayCambios = true;
        }

        if (!Objects.equals(actual.getComprador(), nuevo.getComprador())) {
            log.info("OCID '{}': comprador: '{}' -> '{}'", nuevo.getOcid(), actual.getComprador(), nuevo.getComprador());
            hayCambios = true;
        }

        if (!sonIguales(actual.getMonto(), nuevo.getMonto())) {
            log.info("OCID '{}': monto: '{}' -> '{}'", nuevo.getOcid(), actual.getMonto(), nuevo.getMonto());
            hayCambios = true;
        }

        if (!sonIguales(actual.getFecha(), nuevo.getFecha())) {
            log.info("OCID '{}': fecha: '{}' -> '{}'", nuevo.getOcid(), actual.getFecha(), nuevo.getFecha());
            hayCambios = true;
        }

        if (!Objects.equals(actual.getTitulo(), nuevo.getTitulo())) {
            log.info("OCID '{}': titulo: '{}' -> '{}'", nuevo.getOcid(), actual.getTitulo(), nuevo.getTitulo());
            hayCambios = true;
        }

        if (!Objects.equals(actual.getDescripcion(), nuevo.getDescripcion())) {
            log.info("OCID '{}': descripcion: '{}' -> '{}'", nuevo.getOcid(), actual.getDescripcion(), nuevo.getDescripcion());
            hayCambios = true;
        }

        if (!sonIguales(actual.getPresupuesto(), nuevo.getPresupuesto())) {
            log.info("OCID '{}': presupuesto: '{}' -> '{}'", nuevo.getOcid(), actual.getPresupuesto(), nuevo.getPresupuesto());
            hayCambios = true;
        }

        return hayCambios;
    }

    private boolean sonIguales(BigDecimal actual, BigDecimal nuevo) {

        if (actual == null || nuevo == null) {
            return actual == nuevo;
        }

        return actual.compareTo(nuevo) == 0;
    }

    private boolean sonIguales(OffsetDateTime actual, OffsetDateTime nuevo) {

        if (actual == null || nuevo == null) {
            return actual == nuevo;
        }

        return actual.isEqual(nuevo);
    }
}
