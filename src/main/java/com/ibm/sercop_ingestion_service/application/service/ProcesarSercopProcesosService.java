package com.ibm.sercop_ingestion_service.application.service;

import com.ibm.sercop_ingestion_service.application.exception.ErrorPersistenciaSercopException;
import com.ibm.sercop_ingestion_service.application.port.out.ProcesoContratacionPersistencePort;
import com.ibm.sercop_ingestion_service.application.record.RegistroIngestaSercop;
import com.ibm.sercop_ingestion_service.application.validator.ComparadorProcesoContratacion;
import com.ibm.sercop_ingestion_service.application.validator.ProcesoValidadorSercop;
import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcesarSercopProcesosService {

    private final ProcesoContratacionPersistencePort persistencePort;
    private final ProcesoValidadorSercop validador;
    private final ComparadorProcesoContratacion comparador;

    public ProcesarSercopProcesosService(ProcesoContratacionPersistencePort persistencePort, ProcesoValidadorSercop validador, ComparadorProcesoContratacion comparador) {
        this.persistencePort = persistencePort;
        this.validador = validador;
        this.comparador = comparador;
    }

    public void procesar(List<ProcesoContratacion> procesos, RegistroIngestaSercop registro) {

        List<ProcesoContratacion> procesosValidos = new ArrayList<>();

        for (ProcesoContratacion proceso : procesos) {

            if (!validador.esValido(proceso)) {
                registro.registrarProcesoInvalido();
                continue;
            }

            registro.registrarProcesoValido();
            procesosValidos.add(proceso);
        }

        procesarLote(procesosValidos, registro);
    }

    private void procesarLote(List<ProcesoContratacion> procesos, RegistroIngestaSercop registro) {

        if (procesos.isEmpty()) {
            return;
        }

        Map<String, ProcesoContratacion> procesosUnicosPorOcid = procesos.stream()
                .collect(Collectors.toMap(
                        ProcesoContratacion::getOcid,
                        proceso -> proceso,
                        (existente, duplicado) -> existente
                ));

        int cantidadDuplicados = procesos.size() - procesosUnicosPorOcid.size();

        for (int i = 0; i < cantidadDuplicados; i++) {
            registro.registrarProcesoDuplicado();
        }

        List<ProcesoContratacion> procesosUnicos = new ArrayList<>(procesosUnicosPorOcid.values());

        List<String> ocids = procesosUnicos.stream()
                .map(ProcesoContratacion::getOcid)
                .toList();

        List<ProcesoContratacion> procesosExistentes = persistencePort.buscarPorOcids(ocids);

        Map<String, ProcesoContratacion> existentesPorOcid = procesosExistentes.stream()
                .collect(Collectors.toMap(ProcesoContratacion::getOcid, proceso -> proceso));

        List<ProcesoContratacion> procesosNuevos = new ArrayList<>();
        List<ProcesoContratacion> procesosModificados = new ArrayList<>();

        for (ProcesoContratacion proceso : procesosUnicos) {

            ProcesoContratacion procesoExistente = existentesPorOcid.get(proceso.getOcid());

            if (procesoExistente == null) {
                procesosNuevos.add(proceso);
                continue;
            }

            if (comparador.tieneCambios(procesoExistente, proceso)) {
                procesosModificados.add(proceso);
                continue;
            }

            registro.registrarProcesoSinCambios();
        }

        if (!procesosNuevos.isEmpty()) {

            try {

                persistencePort.guardarProcesos(procesosNuevos);

                for (int i = 0; i < procesosNuevos.size(); i++) {
                    registro.registrarProcesoPersistido();
                }

            } catch (ErrorPersistenciaSercopException e) {

                log.error(
                        "Error al guardar lote de {} procesos de SERCOP",
                        procesosNuevos.size(),
                        e
                );

                throw e;
            }
        }

        if (!procesosModificados.isEmpty()) {

            try {

                persistencePort.actualizarProcesos(procesosModificados);

                for (int i = 0; i < procesosModificados.size(); i++) {
                    registro.registrarProcesoActualizado();
                }

            } catch (ErrorPersistenciaSercopException e) {

                log.error(
                        "Error al actualizar lote de {} procesos de SERCOP",
                        procesosModificados.size(),
                        e
                );

                throw e;
            }
        }

        log.info(
                "Lote procesado: nuevos={}, modificados={}, sin cambios={}, duplicados={}",
                procesosNuevos.size(),
                procesosModificados.size(),
                procesos.size() - procesosNuevos.size() - procesosModificados.size(),
                cantidadDuplicados
        );
    }
}