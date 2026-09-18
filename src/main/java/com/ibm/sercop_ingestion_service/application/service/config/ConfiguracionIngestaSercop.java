package com.ibm.sercop_ingestion_service.application.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sercop.ingesta")
public class ConfiguracionIngestaSercop {

    private int maxReintentos;
    private long esperaInicial;

}