package com.ibm.sercop_ingestion_service.application.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.sercop_ingestion_service.adapter.out.sercop.dto.DataJsonSercop;
import com.ibm.sercop_ingestion_service.application.mapper.SercopMapper;
import com.ibm.sercop_ingestion_service.application.port.out.RegistroIngestaSercopPersistencePort;
import com.ibm.sercop_ingestion_service.application.record.RegistroIngestaSercop;
import com.ibm.sercop_ingestion_service.domain.entities.ProcesoContratacion;
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

    private static final int TAMANO_LOTE = 500;

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
     * Procesa un archivo ZIP descargado automáticamente desde SERCOP.
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
     * en entidades de dominio por lotes.
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

                procesarJsonPorLotes(zipInputStream, registro);

                break;
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo leer el contenido ZIP de SERCOP",
                    e
            );
        }
    }

    /**
     * Lee el JSON de forma progresiva y procesa los registros en lotes.
     */
    private void procesarJsonPorLotes(InputStream entrada, RegistroIngestaSercop registro) throws IOException {

        MappingIterator<DataJsonSercop> iterador = objectMapper
                .readerFor(DataJsonSercop.class)
                .readValues(entrada);

        List<ProcesoContratacion> lote = new ArrayList<>(TAMANO_LOTE);

        int totalProcesados = 0;

        while (iterador.hasNext()) {

            DataJsonSercop dato = iterador.next();

            ProcesoContratacion proceso = sercopMapper.mapperProcesoContratacion(dato);

            lote.add(proceso);

            if (lote.size() >= TAMANO_LOTE) {

                procesarLote(lote, registro);

                totalProcesados += lote.size();

                lote.clear();
            }
        }

        if (!lote.isEmpty()) {

            procesarLote(lote, registro);

            totalProcesados += lote.size();

            lote.clear();
        }

        registro.registrarProcesosRecibidos(totalProcesados);

        log.info("Archivo JSON procesado. Total de registros: {}", totalProcesados);
    }

    /**
     * Envía un lote de procesos al servicio de procesamiento.
     */
    private void procesarLote(List<ProcesoContratacion> lote, RegistroIngestaSercop registro) {

        procesarSercopProcesosService.procesar(lote, registro);

        log.info("Lote procesado correctamente. Registros del lote: {}", lote.size());
    }
}