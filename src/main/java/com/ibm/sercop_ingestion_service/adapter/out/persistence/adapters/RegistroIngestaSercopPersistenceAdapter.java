package com.ibm.sercop_ingestion_service.adapter.out.persistence.adapters;

import com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence.RegistroIngestaSercopEntity;
import com.ibm.sercop_ingestion_service.adapter.out.persistence.mapper.RegistroMapper;
import com.ibm.sercop_ingestion_service.adapter.out.persistence.repository.RegistroIngestaSercopJpaRepository;
import com.ibm.sercop_ingestion_service.application.port.out.RegistroIngestaSercopPersistencePort;
import com.ibm.sercop_ingestion_service.application.record.RegistroIngestaSercop;
import org.springframework.stereotype.Component;

@Component
public class RegistroIngestaSercopPersistenceAdapter implements RegistroIngestaSercopPersistencePort {

    private final RegistroIngestaSercopJpaRepository repository;
    private final RegistroMapper mapper;

    public RegistroIngestaSercopPersistenceAdapter(RegistroIngestaSercopJpaRepository repository, RegistroMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public RegistroIngestaSercop guardar(RegistroIngestaSercop registro) {

        RegistroIngestaSercopEntity entidad = mapper.toEntity(registro);

        RegistroIngestaSercopEntity entidadGuardada = repository.save(entidad);

        return mapper.toDomain(entidadGuardada);
    }
}