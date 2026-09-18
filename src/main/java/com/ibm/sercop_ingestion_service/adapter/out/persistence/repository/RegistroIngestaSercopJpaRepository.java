package com.ibm.sercop_ingestion_service.adapter.out.persistence.repository;

import com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence.RegistroIngestaSercopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroIngestaSercopJpaRepository extends JpaRepository<RegistroIngestaSercopEntity, Long> {
}