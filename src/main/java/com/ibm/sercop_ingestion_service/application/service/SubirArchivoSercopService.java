package com.ibm.sercop_ingestion_service.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.sercop_ingestion_service.adapter.out.sercop.dto.DataJsonSercop;
import com.ibm.sercop_ingestion_service.application.port.out.RegistroIngestaSercopPersistencePort;
import com.ibm.sercop_ingestion_service.application.record.RegistroIngestaSercop;
import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
import com.ibm.sercop_ingestion_service.application.mapper.SercopMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class SubirArchivoSercopService {

    private final ObjectMapper objectMapper;
    private final SercopMapper sercopMapper;
    private final ProcesarSercopProcesosService procesarSercopProcesosService;
    private final RegistroIngestaSercopPersistencePort registroPersistencePort;

    public SubirArchivoSercopService(ObjectMapper objectMapper, SercopMapper sercopMapper, ProcesarSercopProcesosService procesarSercopProcesosService, RegistroIngestaSercopPersistencePort registroPersistencePort) {
        this.objectMapper = objectMapper;
        this.sercopMapper = sercopMapper;
        this.procesarSercopProcesosService = procesarSercopProcesosService;
        this.registroPersistencePort = registroPersistencePort;
    }

    /**
     * Procesa un archivo ZIP recibido mediante el endpoint manual.
     */
    public void obtenerArchivo(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        RegistroIngestaSercop registro = new RegistroIngestaSercop();

        try (InputStream entrada = archivo.getInputStream()) {

            procesarZip(entrada, registro);

            registro.finalizar();
            registroPersistencePort.guardar(registro);

            log.info("Archivo de SERCOP procesado correctamente");

        } catch (IOException e) {

            registro.marcarError();

            try {
                registroPersistencePort.guardar(registro);
            } catch (Exception errorPersistencia) {
                log.error("No se pudo guardar el registro de ingesta con estado ERROR", errorPersistencia);
            }

            throw new IllegalStateException(
                    "No se pudo procesar el archivo de SERCOP",
                    e
            );
        }
    }

    /**
     * Procesa un archivo ZIP descargado automaticamente desde SERCOP.
     */
    public void procesarArchivo(Path archivo, RegistroIngestaSercop registro) {

        try (InputStream entrada = Files.newInputStream(archivo)) {
            procesarZip(entrada, registro);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo procesar el archivo de SERCOP",
                    e
            );
        }
    }

    /**
     * Procesa el contenido ZIP y transforma los registros JSON
     * en entidades de dominio para su posterior procesamiento.
     */
    private void procesarZip(InputStream entrada, RegistroIngestaSercop registro) {

        try (ZipInputStream zipInputStream = new ZipInputStream(entrada)) {

            ZipEntry entradaZip;

            while ((entradaZip = zipInputStream.getNextEntry()) != null) {

                if (entradaZip.isDirectory()) {
                    continue;
                }

                if (!entradaZip.getName().toLowerCase().endsWith(".json")) {
                    continue;
                }

                log.info("Procesando archivo JSON: {}", entradaZip.getName());

                List<DataJsonSercop> datos = objectMapper.readValue(
                        zipInputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, DataJsonSercop.class)
                );

                registro.registrarProcesosRecibidos(datos.size());

                List<ProcesoContratacion> procesos = new ArrayList<>();

                for (DataJsonSercop dato : datos) {
                    ProcesoContratacion proceso = sercopMapper.mapperProcesoContratacion(dato);
                    procesos.add(proceso);
                }

                procesarSercopProcesosService.procesar(procesos, registro);

                log.info("Registros procesados: {}", procesos.size());

                break;
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo leer el contenido ZIP de SERCOP",
                    e
            );
        }
    }
}