package com.ibm.sercop_ingestion_service.application.port.out;

import com.ibm.sercop_ingestion_service.application.record.RegistroIngestaSercop;

public interface RegistroIngestaSercopPersistencePort {

    RegistroIngestaSercop guardar(RegistroIngestaSercop registro);
}