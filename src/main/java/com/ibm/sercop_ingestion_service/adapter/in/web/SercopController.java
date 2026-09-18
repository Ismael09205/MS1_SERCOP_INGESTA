package com.ibm.sercop_ingestion_service.adapter.in.web;

import com.ibm.sercop_ingestion_service.application.service.IngestarSercopService;
import com.ibm.sercop_ingestion_service.application.service.SubirArchivoSercopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class SercopController {

    private final SubirArchivoSercopService subirArchivo;
    private final IngestarSercopService ingestarSercopService;

    public SercopController(SubirArchivoSercopService subirArchivo, IngestarSercopService ingestarSercopService) {
        this.subirArchivo = subirArchivo;
        this.ingestarSercopService = ingestarSercopService;
    }

    @PostMapping("/archivo")
    public ResponseEntity<Void> ingestarArchivo(@RequestParam("archivo") MultipartFile archivo) {
        subirArchivo.obtenerArchivo(archivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ingesta")
    public ResponseEntity<String> ejecutarIngesta() {

        ingestarSercopService.ejecutar(2026, 0);

        return ResponseEntity.ok("Ingesta de SERCOP completada");
    }

}