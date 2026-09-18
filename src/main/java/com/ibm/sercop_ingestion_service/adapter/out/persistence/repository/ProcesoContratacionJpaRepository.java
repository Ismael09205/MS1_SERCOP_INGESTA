package com.ibm.sercop_ingestion_service.adapter.out.persistence.repository;

import com.ibm.sercop_ingestion_service.adapter.out.persistence.entityPersistence.ProcesoContratacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcesoContratacionJpaRepository extends JpaRepository<ProcesoContratacionEntity, Long> {
        Optional<ProcesoContratacionEntity> findByOcid(String ocid);
        List<ProcesoContratacionEntity> findByOcidIn(List<String> ocids);
}
