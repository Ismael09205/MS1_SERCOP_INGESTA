package com.ibm.sercop_ingestion_service.adapter.out.persistence.adapters;

import com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence.ProcesoContratacionEntity;
import com.ibm.sercop_ingestion_service.adapter.out.persistence.repository.ProcesoContratacionJpaRepository;
import com.ibm.sercop_ingestion_service.application.mapper.ProcesoContratacionPersistenceMapper;
import com.ibm.sercop_ingestion_service.application.port.out.ProcesoContratacionPersistencePort;
import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProcesoContratacionPersistenceAdapter implements ProcesoContratacionPersistencePort {

    private static final int TAMANO_BLOQUE_OCIDS = 500;

    private final ProcesoContratacionJpaRepository repository;
    private final ProcesoContratacionPersistenceMapper mapper;

    public ProcesoContratacionPersistenceAdapter(ProcesoContratacionJpaRepository repository, ProcesoContratacionPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProcesoContratacion guardarProceso(ProcesoContratacion procesoContratacion) {

        ProcesoContratacionEntity nuevaEntidad = mapper.toEntity(procesoContratacion);

        ProcesoContratacionEntity entidadGuardada = repository.save(nuevaEntidad);

        return mapper.toDomain(entidadGuardada);
    }

    @Override
    public ProcesoContratacion actualizarProceso(ProcesoContratacion procesoContratacion) {

        Optional<ProcesoContratacionEntity> entidadExistente = repository.findByOcid(procesoContratacion.getOcid());

        if (entidadExistente.isEmpty()) {
            throw new IllegalStateException(
                    "No se encontro el proceso de contratacion con OCID: "
                            + procesoContratacion.getOcid()
            );
        }

        ProcesoContratacionEntity entidad = entidadExistente.get();

        entidad.setIdentificadorSercop(procesoContratacion.getIdentificador());
        entidad.setAnio(procesoContratacion.getAnio());
        entidad.setMes(procesoContratacion.getMes());
        entidad.setMetodo(procesoContratacion.getMetodo());
        entidad.setTipoInterno(procesoContratacion.getTipoInterno());
        entidad.setLocalidad(procesoContratacion.getLocalidad());
        entidad.setRegion(procesoContratacion.getRegion());
        entidad.setProveedores(procesoContratacion.getProveedores());
        entidad.setComprador(procesoContratacion.getComprador());
        entidad.setMonto(procesoContratacion.getMonto());
        entidad.setFecha(procesoContratacion.getFecha());
        entidad.setTitulo(procesoContratacion.getTitulo());
        entidad.setDescripcion(procesoContratacion.getDescripcion());
        entidad.setPresupuesto(procesoContratacion.getPresupuesto());

        ProcesoContratacionEntity entidadActualizada = repository.save(entidad);

        return mapper.toDomain(entidadActualizada);
    }

    @Override
    public Optional<ProcesoContratacion> buscarPorOcid(String ocid) {
        return repository.findByOcid(ocid).map(mapper::toDomain);
    }

    @Override
    public List<ProcesoContratacion> buscarPorOcids(List<String> ocids) {

        List<ProcesoContratacionEntity> entidades = new ArrayList<>();

        for (int inicio = 0; inicio < ocids.size(); inicio += TAMANO_BLOQUE_OCIDS) {

            int fin = Math.min(inicio + TAMANO_BLOQUE_OCIDS, ocids.size());

            List<String> bloque = ocids.subList(inicio, fin);

            entidades.addAll(repository.findByOcidIn(bloque));
        }

        return entidades.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ProcesoContratacion> guardarProcesos(List<ProcesoContratacion> procesos) {

        List<ProcesoContratacionEntity> entidades = procesos.stream()
                .map(mapper::toEntity)
                .toList();

        List<ProcesoContratacionEntity> entidadesGuardadas = repository.saveAll(entidades);

        return entidadesGuardadas.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ProcesoContratacion> actualizarProcesos(List<ProcesoContratacion> procesos) {

        List<String> ocids = procesos.stream()
                .map(ProcesoContratacion::getOcid)
                .toList();

        List<ProcesoContratacionEntity> entidadesExistentes = new ArrayList<>();

        for (int inicio = 0; inicio < ocids.size(); inicio += TAMANO_BLOQUE_OCIDS) {

            int fin = Math.min(inicio + TAMANO_BLOQUE_OCIDS, ocids.size());

            List<String> bloque = ocids.subList(inicio, fin);

            entidadesExistentes.addAll(repository.findByOcidIn(bloque));
        }

        Map<String, ProcesoContratacion> procesosPorOcid = procesos.stream()
                .collect(Collectors.toMap(ProcesoContratacion::getOcid, proceso -> proceso));

        for (ProcesoContratacionEntity entidad : entidadesExistentes) {

            ProcesoContratacion proceso = procesosPorOcid.get(entidad.getOcid());

            entidad.setIdentificadorSercop(proceso.getIdentificador());
            entidad.setAnio(proceso.getAnio());
            entidad.setMes(proceso.getMes());
            entidad.setMetodo(proceso.getMetodo());
            entidad.setTipoInterno(proceso.getTipoInterno());
            entidad.setLocalidad(proceso.getLocalidad());
            entidad.setRegion(proceso.getRegion());
            entidad.setProveedores(proceso.getProveedores());
            entidad.setComprador(proceso.getComprador());
            entidad.setMonto(proceso.getMonto());
            entidad.setFecha(proceso.getFecha());
            entidad.setTitulo(proceso.getTitulo());
            entidad.setDescripcion(proceso.getDescripcion());
            entidad.setPresupuesto(proceso.getPresupuesto());
        }

        List<ProcesoContratacionEntity> entidadesActualizadas = repository.saveAll(entidadesExistentes);

        return entidadesActualizadas.stream()
                .map(mapper::toDomain)
                .toList();
    }
}