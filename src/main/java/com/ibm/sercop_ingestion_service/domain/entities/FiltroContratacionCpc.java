package com.ibm.sercop_ingestion_service.domain.entities;

import com.ibm.sercop_ingestion_service.domain.enums.CpcCategoria;

/**
 * Regla de dominio que determina si un proceso de contratación
 * pertenece a alguna categoría CPC relevante (software, cloud, licencias).
 */
public class FiltroContratacionCpc {

    private FiltroContratacionCpc() {
    }

    /**
     * Retorna true si el proceso tiene una categoría CPC asignada,
     * lo que indica que su código CPC coincide con algún prefijo relevante.
     */
    public static boolean esRelevante(ProcesoContratacion proceso) {
        return proceso.getCategoriaCpc() != null;
    }

    /**
     * Asigna la categoría CPC al proceso resolviendo el código CPC ya mapeado.
     * Debe llamarse después de que SercopMapper haya seteado el codigoCpc.
     */
    public static void aplicar(ProcesoContratacion proceso) {
        CpcCategoria categoria = CpcCategoria.resolverPorCodigo(proceso.getCodigoCpc());
        proceso.setCategoriaCpc(categoria);
    }
}
