package com.ibm.sercop_ingestion_service.adapter.out.persistence.adapters;

import com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence.ProcesoContratacionEntity;
import com.ibm.sercop_ingestion_service.adapter.out.persistence.repository.ProcesoContratacionJpaRepository;
import com.ibm.sercop_ingestion_service.application.exception.ErrorPersistenciaSercopException;
import com.ibm.sercop_ingestion_service.application.mapper.ProcesoContratacionPersistenceMapper;
import com.ibm.sercop_ingestion_service.application.port.out.ProcesoContratacionPersistencePort;
import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @PersistenceContext
    private EntityManager entityManager;

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
    @Transactional
    public ProcesoContratacion actualizarProceso(ProcesoContratacion procesoContratacion) {

        Optional<ProcesoContratacionEntity> entidadExistente = repository.findByOcid(procesoContratacion.getOcid());

        if (entidadExistente.isEmpty()) {
            throw new IllegalStateException(
                    "No se encontro el proceso de contratacion con OCID: "
                            + procesoContratacion.getOcid()
            );
        }

        ProcesoContratacionEntity entidad = entidadExistente.get();

        actualizarEntidad(entidad, procesoContratacion);

        entityManager.flush();

        return mapper.toDomain(entidad);
    }

    @Override
    public Optional<ProcesoContratacion> buscarPorOcid(String ocid) {
        return repository.findByOcid(ocid).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public void guardarProcesos(List<ProcesoContratacion> procesos) {

        if (procesos.isEmpty()) {
            return;
        }

        try {

            List<ProcesoContratacionEntity> entidades = procesos.stream()
                    .map(mapper::toEntity)
                    .toList();

            repository.saveAll(entidades);

            entityManager.flush();
            entityManager.clear();

        } catch (DataAccessException e) {

            throw new ErrorPersistenciaSercopException(
                    "No se pudieron guardar los procesos de SERCOP",
                    e
            );
        }
    }
    @Override
    @Transactional
    public void actualizarProcesos(List<ProcesoContratacion> procesos) {

        if (procesos.isEmpty()) {
            return;
        }

        try {

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

                if (proceso == null) {
                    continue;
                }

                actualizarEntidad(entidad, proceso);
            }

            entityManager.flush();
            entityManager.clear();

        } catch (DataAccessException e) {

            throw new ErrorPersistenciaSercopException(
                    "No se pudieron actualizar los procesos de SERCOP",
                    e
            );
        }
    }

    private void actualizarEntidad(ProcesoContratacionEntity entidad, ProcesoContratacion proceso) {

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
}