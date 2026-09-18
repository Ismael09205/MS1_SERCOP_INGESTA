package com.ibm.sercop_ingestion_service.adapter.out.sercop.cliente;

import com.ibm.sercop_ingestion_service.application.port.out.SercopArchivoPort;
import com.ibm.sercop_ingestion_service.application.service.config.ConfiguracionIngestaSercop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class SercopArchivoAdapter implements SercopArchivoPort {

    private final RestClient restClient;
    private final ConfiguracionIngestaSercop configuracion;

    public SercopArchivoAdapter(RestClient.Builder restClientBuilder, ConfiguracionIngestaSercop configuracion) {
        this.restClient = restClientBuilder
                .baseUrl("https://datosabiertos.compraspublicas.gob.ec")
                .build();
        this.configuracion = configuracion;
    }

    @Override
    public Path descargarArchivo(int anio, int mes) {

        int intento = 0;

        while (true) {

            intento++;

            long inicioIntento = System.currentTimeMillis();

            log.info("============================================================");
            log.info("INICIO INTENTO DE DESCARGA SERCOP");
            log.info("Año: {}", anio);
            log.info("Mes: {}", mes);
            log.info("Intento: {}/{}", intento, configuracion.getMaxReintentos());
            log.info("============================================================");

            try {

                Path archivo = descargarIntento(anio, mes, intento);

                long duracion = System.currentTimeMillis() - inicioIntento;

                log.info("============================================================");
                log.info("DESCARGA SERCOP COMPLETADA");
                log.info("Año: {}", anio);
                log.info("Mes: {}", mes);
                log.info("Intento: {}", intento);
                log.info("Archivo: {}", archivo);
                log.info("Tamaño: {} bytes", Files.size(archivo));
                log.info("Tamaño: {} MB", String.format("%.2f", Files.size(archivo) / (1024.0 * 1024.0)));
                log.info("Duración: {} segundos", String.format("%.2f", duracion / 1000.0));
                log.info("============================================================");

                return archivo;

            } catch (RestClientResponseException e) {

                long duracion = System.currentTimeMillis() - inicioIntento;

                log.error("============================================================");
                log.error("ERROR HTTP DURANTE DESCARGA SERCOP");
                log.error("Año: {}", anio);
                log.error("Mes: {}", mes);
                log.error("Intento: {}", intento);
                log.error("Código HTTP: {}", e.getStatusCode().value());
                log.error("Mensaje HTTP: {}", e.getStatusText());
                log.error("Duración del intento: {} segundos", String.format("%.2f", duracion / 1000.0));
                log.error("============================================================");

                if (!debeReintentar(e.getStatusCode()) || intento >= configuracion.getMaxReintentos()) {

                    throw new IllegalStateException(
                            "No se pudo descargar el archivo de SERCOP después de " + intento + " intento(s)",
                            e
                    );
                }

                long espera = calcularEspera(intento);

                log.warn("SERCOP permite reintento. Esperando {} ms antes del siguiente intento", espera);

                esperar(espera);

            } catch (RestClientException | IOException e) {

                long duracion = System.currentTimeMillis() - inicioIntento;

                log.error("============================================================");
                log.error("ERROR DURANTE DESCARGA SERCOP");
                log.error("Año: {}", anio);
                log.error("Mes: {}", mes);
                log.error("Intento: {}", intento);
                log.error("Tipo de excepción: {}", e.getClass().getName());
                log.error("Mensaje: {}", e.getMessage());
                log.error("Duración del intento: {} segundos", String.format("%.2f", duracion / 1000.0));
                log.error("============================================================", e);

                if (intento >= configuracion.getMaxReintentos()) {

                    throw new IllegalStateException(
                            "No se pudo descargar el archivo de SERCOP después de " + intento + " intento(s)",
                            e
                    );
                }

                long espera = calcularEspera(intento);

                log.warn("Error recuperable. Esperando {} ms antes del siguiente intento", espera);

                esperar(espera);
            }
        }
    }

    private Path descargarIntento(int anio, int mes, int intento) throws IOException {

        Path archivo = Files.createTempFile(
                "sercop_" + anio + "_" + mes + "_",
                ".zip"
        );

        Path archivoFinal = archivo;

        String urlDescripcion = "/PLATAFORMA/download?type=json&year="
                + anio
                + "&month="
                + mes
                + "&method=all";

        long inicioDescarga = System.currentTimeMillis();

        try {

            log.info("Solicitando archivo a SERCOP...");
            log.info("Endpoint: {}", urlDescripcion);

            restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/PLATAFORMA/download")
                            .queryParam("type", "json")
                            .queryParam("year", anio)
                            .queryParam("month", mes)
                            .queryParam("method", "all")
                            .build())
                    .exchange((request, response) -> {

                        long momentoRespuesta = System.currentTimeMillis();

                        log.info("------------------------------------------------------------");
                        log.info("RESPUESTA INICIAL DE SERCOP");
                        log.info("Intento: {}", intento);
                        log.info("HTTP: {}", response.getStatusCode());
                        log.info("Código HTTP: {}", response.getStatusCode().value());
                        log.info("Tiempo hasta respuesta HTTP: {} segundos",
                                String.format("%.2f", (momentoRespuesta - inicioDescarga) / 1000.0));

                        log.info("Content-Type: {}",
                                response.getHeaders().getFirst("Content-Type"));

                        log.info("Content-Length: {}",
                                response.getHeaders().getContentLength());

                        log.info("Content-Encoding: {}",
                                response.getHeaders().getFirst("Content-Encoding"));

                        log.info("Transfer-Encoding: {}",
                                response.getHeaders().getFirst("Transfer-Encoding"));

                        log.info("Accept-Ranges: {}",
                                response.getHeaders().getFirst("Accept-Ranges"));

                        log.info("Connection: {}",
                                response.getHeaders().getFirst("Connection"));

                        log.info("Content-Disposition: {}",
                                response.getHeaders().getFirst("Content-Disposition"));

                        log.info("------------------------------------------------------------");

                        if (response.getStatusCode().isError()) {

                            log.error("SERCOP respondió con error HTTP {}",
                                    response.getStatusCode().value());

                            throw new RestClientResponseException(
                                    "Error HTTP al descargar archivo de SERCOP",
                                    response.getStatusCode().value(),
                                    response.getStatusText(),
                                    response.getHeaders(),
                                    null,
                                    null
                            );
                        }

                        long tamanioEsperado = response.getHeaders().getContentLength();

                        long bytesTotales = 0;
                        long inicioLectura = System.currentTimeMillis();
                        long ultimoReporte = inicioLectura;
                        long ultimoByteRecibido = inicioLectura;

                        long ultimoBytesReporte = 0;

                        int bytesLeidos;

                        log.info("INICIANDO LECTURA DEL ARCHIVO...");
                        log.info("Tamaño esperado: {} bytes ({} MB)",
                                tamanioEsperado,
                                tamanioEsperado > 0
                                        ? String.format("%.2f", tamanioEsperado / (1024.0 * 1024.0))
                                        : "desconocido");

                        try (InputStream entrada = response.getBody();
                             OutputStream salida = Files.newOutputStream(archivoFinal)) {

                            byte[] buffer = new byte[8192];

                            while ((bytesLeidos = entrada.read(buffer)) != -1) {

                                salida.write(buffer, 0, bytesLeidos);

                                bytesTotales += bytesLeidos;

                                long ahora = System.currentTimeMillis();

                                ultimoByteRecibido = ahora;

                                if (ahora - ultimoReporte >= 5000) {

                                    double megabytes = bytesTotales / (1024.0 * 1024.0);

                                    double segundosTranscurridos =
                                            (ahora - inicioLectura) / 1000.0;

                                    double velocidadPromedio =
                                            segundosTranscurridos > 0
                                                    ? megabytes / segundosTranscurridos
                                                    : 0;

                                    double megabytesDesdeReporte =
                                            (bytesTotales - ultimoBytesReporte)
                                                    / (1024.0 * 1024.0);

                                    double segundosDesdeReporte =
                                            (ahora - ultimoReporte) / 1000.0;

                                    double velocidadActual =
                                            segundosDesdeReporte > 0
                                                    ? megabytesDesdeReporte / segundosDesdeReporte
                                                    : 0;

                                    String porcentaje = "desconocido";

                                    if (tamanioEsperado > 0) {

                                        double porcentajeNumero =
                                                (bytesTotales * 100.0) / tamanioEsperado;

                                        porcentaje =
                                                String.format("%.2f%%", porcentajeNumero);
                                    }

                                    log.info(
                                            "PROGRESO SERCOP | Recibido: {} MB | Progreso: {} | Velocidad actual: {} MB/s | Velocidad promedio: {} MB/s | Tiempo: {} s",
                                            String.format("%.2f", megabytes),
                                            porcentaje,
                                            String.format("%.2f", velocidadActual),
                                            String.format("%.2f", velocidadPromedio),
                                            String.format("%.0f", segundosTranscurridos)
                                    );

                                    ultimoReporte = ahora;
                                    ultimoBytesReporte = bytesTotales;
                                }
                            }
                        }

                        long finLectura = System.currentTimeMillis();

                        double segundosTotales =
                                (finLectura - inicioLectura) / 1000.0;

                        double megabytesTotales =
                                bytesTotales / (1024.0 * 1024.0);

                        double velocidadFinal =
                                segundosTotales > 0
                                        ? megabytesTotales / segundosTotales
                                        : 0;

                        log.info("------------------------------------------------------------");
                        log.info("LECTURA SERCOP FINALIZADA");
                        log.info("Bytes recibidos: {}", bytesTotales);
                        log.info("Megabytes recibidos: {} MB",
                                String.format("%.2f", megabytesTotales));
                        log.info("Tiempo de lectura: {} segundos",
                                String.format("%.2f", segundosTotales));
                        log.info("Velocidad promedio final: {} MB/s",
                                String.format("%.2f", velocidadFinal));

                        if (tamanioEsperado > 0) {

                            double porcentajeFinal =
                                    (bytesTotales * 100.0) / tamanioEsperado;

                            log.info("Porcentaje recibido: {}%",
                                    String.format("%.2f", porcentajeFinal));

                            if (bytesTotales != tamanioEsperado) {

                                log.warn(
                                        "ADVERTENCIA: los bytes recibidos ({}) NO coinciden con Content-Length ({})",
                                        bytesTotales,
                                        tamanioEsperado
                                );
                            } else {

                                log.info("Content-Length coincide con los bytes recibidos.");
                            }
                        }

                        log.info("------------------------------------------------------------");

                        return archivoFinal;
                    });

            long duracionTotal =
                    System.currentTimeMillis() - inicioDescarga;

            if (!Files.exists(archivo) || Files.size(archivo) == 0) {

                log.error("El archivo descargado está vacío.");

                throw new IOException(
                        "El archivo descargado de SERCOP está vacío"
                );
            }

            log.info("Archivo temporal creado correctamente.");
            log.info("Ruta: {}", archivo);
            log.info("Tamaño real: {} bytes", Files.size(archivo));
            log.info("Tamaño real: {} MB",
                    String.format("%.2f", Files.size(archivo) / (1024.0 * 1024.0)));
            log.info("Tiempo total del intento: {} segundos",
                    String.format("%.2f", duracionTotal / 1000.0));

            return archivo;

        } catch (Exception e) {

            log.error("------------------------------------------------------------");
            log.error("LA DESCARGA FALLÓ");
            log.error("Año: {}", anio);
            log.error("Mes: {}", mes);
            log.error("Intento: {}", intento);
            log.error("Archivo temporal: {}", archivo);
            log.error("Archivo existe: {}", Files.exists(archivo));

            try {

                if (Files.exists(archivo)) {

                    long tamanioParcial = Files.size(archivo);

                    log.error("Tamaño parcial del archivo: {} bytes", tamanioParcial);
                    log.error("Tamaño parcial: {} MB",
                            String.format("%.2f", tamanioParcial / (1024.0 * 1024.0)));
                }

            } catch (IOException error) {

                log.error("No se pudo obtener el tamaño del archivo parcial.", error);
            }

            log.error("Tiempo transcurrido antes del fallo: {} segundos",
                    String.format(
                            "%.2f",
                            (System.currentTimeMillis() - inicioDescarga) / 1000.0
                    ));

            log.error("Tipo de excepción: {}", e.getClass().getName());
            log.error("Mensaje de excepción: {}", e.getMessage());
            log.error("------------------------------------------------------------", e);

            eliminarArchivo(archivo);

            if (e instanceof RestClientResponseException excepcionHttp) {
                throw excepcionHttp;
            }

            if (e instanceof IOException excepcionIo) {
                throw excepcionIo;
            }

            throw new RestClientException(
                    "Error durante la descarga del archivo de SERCOP",
                    e
            );
        }
    }

    private boolean debeReintentar(HttpStatusCode status) {

        int codigo = status.value();

        return codigo == 429
                || codigo == 500
                || codigo == 502
                || codigo == 503
                || codigo == 504;
    }

    private long calcularEspera(int intento) {

        return configuracion.getEsperaInicial()
                * (long) Math.pow(2, intento - 1);
    }

    private void esperar(long milisegundos) {

        log.warn("Esperando {} ms antes de volver a intentar la descarga...");

        try {

            Thread.sleep(milisegundos);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "La espera para reintentar la descarga de SERCOP fue interrumpida",
                    e
            );
        }
    }

    private void eliminarArchivo(Path archivo) {

        if (archivo == null) {
            return;
        }

        try {

            Files.deleteIfExists(archivo);

        } catch (IOException e) {

            log.warn(
                    "No se pudo eliminar el archivo temporal: {}",
                    archivo
            );
        }
    }
}