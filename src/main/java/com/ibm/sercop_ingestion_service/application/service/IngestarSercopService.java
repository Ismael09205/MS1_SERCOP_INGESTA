package com.ibm.sercop_ingestion_service.application.service;

import com.ibm.sercop_ingestion_service.application.port.out.RegistroIngestaSercopPersistencePort;
import com.ibm.sercop_ingestion_service.application.port.out.SercopArchivoPort;
import com.ibm.sercop_ingestion_service.application.record.RegistroIngestaSercop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class IngestarSercopService {

    private final SercopArchivoPort sercopArchivoPort;
    private final SubirArchivoSercopService subirArchivoSercopService;
    private final RegistroIngestaSercopPersistencePort registroPersistencePort;

    public IngestarSercopService(SercopArchivoPort sercopArchivoPort, SubirArchivoSercopService subirArchivoSercopService, RegistroIngestaSercopPersistencePort registroPersistencePort) {
        this.sercopArchivoPort = sercopArchivoPort;
        this.subirArchivoSercopService = subirArchivoSercopService;
        this.registroPersistencePort = registroPersistencePort;
    }

    public void ejecutar(int anio, int mes) {

        RegistroIngestaSercop registro = new RegistroIngestaSercop();
        Path archivo = null;

        try {

            log.info("Iniciando ingesta de SERCOP. Año: {}, mes: {}", anio, mes);

            archivo = sercopArchivoPort.descargarArchivo(anio, mes);

            log.info("Archivo descargado correctamente: {}", archivo);

            subirArchivoSercopService.procesarArchivo(archivo, registro);

            registro.finalizar();

            registroPersistencePort.guardar(registro);

            log.info("Ingesta de SERCOP finalizada correctamente");

        } catch (Exception e) {

            registro.marcarError();

            log.error("La ingesta de SERCOP finalizó con error", e);

            try {
                registroPersistencePort.guardar(registro);
            } catch (Exception errorPersistencia) {
                log.error("No se pudo guardar el registro de ingesta con estado ERROR", errorPersistencia);
            }

            throw e;

        } finally {
            eliminarArchivo(archivo);
        }
    }

    private void eliminarArchivo(Path archivo) {

        if (archivo == null) {
            return;
        }

        try {
            Files.deleteIfExists(archivo);
            log.info("Archivo temporal eliminado: {}", archivo);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo temporal: {}", archivo, e);
        }
    }
}