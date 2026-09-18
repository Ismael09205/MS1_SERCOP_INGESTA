package com.ibm.sercop_ingestion_service.application.port.out;

import java.nio.file.Path;

public interface SercopArchivoPort {

    Path descargarArchivo(int anio, int mes);
}