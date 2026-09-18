package com.ibm.sercop_ingestion_service.application.mapper;

import com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence.ProcesoContratacionEntity;
import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
import org.springframework.stereotype.Component;

@Component
public class ProcesoContratacionPersistenceMapper {

    public ProcesoContratacionEntity toEntity(ProcesoContratacion proceso) {

        ProcesoContratacionEntity entity=new ProcesoContratacionEntity();

        entity.setIdentificadorSercop(proceso.getIdentificador());
        entity.setOcid(proceso.getOcid());
        entity.setAnio(proceso.getAnio());
        entity.setMes(proceso.getMes());
        entity.setMetodo(proceso.getMetodo());
        entity.setTipoInterno(proceso.getTipoInterno());
        entity.setLocalidad(proceso.getLocalidad());
        entity.setRegion(proceso.getRegion());
        entity.setProveedores(proceso.getProveedores());
        entity.setComprador(proceso.getComprador());
        entity.setMonto(proceso.getMonto());
        entity.setFecha(proceso.getFecha());
        entity.setTitulo(proceso.getTitulo());
        entity.setDescripcion(proceso.getDescripcion());
        entity.setPresupuesto(proceso.getPresupuesto());

        return entity;
    }

    public ProcesoContratacion toDomain(
            ProcesoContratacionEntity entity) {

        ProcesoContratacion proceso = new ProcesoContratacion();

        proceso.setIdentificador(entity.getIdentificadorSercop());
        proceso.setOcid(entity.getOcid());
        proceso.setAnio(entity.getAnio());
        proceso.setMes(entity.getMes());
        proceso.setMetodo(entity.getMetodo());
        proceso.setTipoInterno(entity.getTipoInterno());
        proceso.setLocalidad(entity.getLocalidad());
        proceso.setRegion(entity.getRegion());
        proceso.setProveedores(entity.getProveedores());
        proceso.setComprador(entity.getComprador());
        proceso.setMonto(entity.getMonto());
        proceso.setFecha(entity.getFecha());
        proceso.setTitulo(entity.getTitulo());
        proceso.setDescripcion(entity.getDescripcion());
        proceso.setPresupuesto(entity.getPresupuesto());

        return proceso;
    }
}