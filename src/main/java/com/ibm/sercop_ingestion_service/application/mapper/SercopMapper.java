package com.ibm.sercop_ingestion_service.application.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.sercop_ingestion_service.adapter.out.sercop.dto.DataJsonSercop;
import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class SercopMapper {

    public ProcesoContratacion mapperProcesoContratacion(DataJsonSercop dataJsonSercop) {

        JsonNode release = dataJsonSercop.getReleases().get(0);

        ProcesoContratacion procesoContratacion = new ProcesoContratacion();

        procesoContratacion.setOcid(release.path("ocid").asText());

        OffsetDateTime fecha = OffsetDateTime.parse(release.path("date").asText());

        procesoContratacion.setAnio(fecha.getYear());
        procesoContratacion.setMes(fecha.getMonthValue());
        procesoContratacion.setFecha(fecha);

        procesoContratacion.setMetodo(release.path("tender").path("procurementMethod").asText(null));
        procesoContratacion.setTipoInterno(release.path("tender").path("procurementMethodDetails").asText(null));
        procesoContratacion.setComprador(release.path("buyer").path("name").asText(null));
        procesoContratacion.setTitulo(release.path("tender").path("title").asText(null));
        procesoContratacion.setDescripcion(release.path("tender").path("description").asText(null));

        JsonNode valorTender = release.path("tender").path("value");

        if (!valorTender.isMissingNode() && !valorTender.path("amount").isMissingNode()) {
            procesoContratacion.setPresupuesto(valorTender.path("amount").decimalValue().setScale(2, RoundingMode.HALF_UP));
        }

        JsonNode awards = release.path("awards");

        if (awards.isArray() && !awards.isEmpty()) {

            JsonNode award = awards.get(0);

            JsonNode valorAward = award.path("value");

            if (!valorAward.isMissingNode() && !valorAward.path("amount").isMissingNode()) {
                procesoContratacion.setMonto(valorAward.path("amount").decimalValue().setScale(2, RoundingMode.HALF_UP));
            }

            JsonNode suppliers = award.path("suppliers");

            if (suppliers.isArray()) {

                String proveedores = StreamSupport.stream(suppliers.spliterator(), false)
                        .map(supplier -> supplier.path("name").asText(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));

                procesoContratacion.setProveedores(proveedores);
            }
        }

        JsonNode parties = release.path("parties");

        if (parties.isArray()) {

            for (JsonNode party : parties) {

                JsonNode roles = party.path("roles");

                if (roles.isArray()) {

                    for (JsonNode role : roles) {

                        if ("supplier".equals(role.asText())) {

                            JsonNode address = party.path("address");

                            procesoContratacion.setLocalidad(address.path("locality").asText(null));
                            procesoContratacion.setRegion(address.path("region").asText(null));

                            break;
                        }
                    }
                }
            }
        }

        return procesoContratacion;
    }
}
