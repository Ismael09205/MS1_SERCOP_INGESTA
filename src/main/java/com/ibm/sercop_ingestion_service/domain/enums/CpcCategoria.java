package com.ibm.sercop_ingestion_service.domain.enums;

/**
 * Categorías CPC relevantes para contratos de software, cloud y licencias.
 * Cada valor contiene el prefijo oficial SERCOP que identifica la categoría.
 */
public enum CpcCategoria {

    LICENCIAS_SOFTWARE("51290", "Licencias de Software y Aplicaciones"),
    SUSCRIPCIONES_SAAS("73310", "Suscripciones de Software y Derechos de Uso (SaaS)"),
    DESARROLLO_CONSULTORIA_TI("8314", "Desarrollo de Software, Análisis, Diseño y Consultoría TI"),
    BASES_DATOS_PLATAFORMAS("84300", "Bases de Datos en Línea, Chatbots y Plataformas de Información"),
    CLOUD_COMPUTING("85960", "Cloud Computing (IaaS/PaaS), GPU Cloud, Data Center Virtual y Migración");

    private final String prefijo;
    private final String descripcion;

    CpcCategoria(String prefijo, String descripcion) {
        this.prefijo = prefijo;
        this.descripcion = descripcion;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Devuelve la categoría que corresponde al código CPC indicado,
     * o null si el código no coincide con ningún prefijo relevante.
     */
    public static CpcCategoria resolverPorCodigo(String codigoCpc) {

        if (codigoCpc == null || codigoCpc.isBlank()) {
            return null;
        }

        // Eliminar puntos para comparar código normalizado (e.g. "51290.00.2" -> "5129000.2")
        String codigoNormalizado = codigoCpc.replace(".", "");

        for (CpcCategoria categoria : values()) {
            if (codigoNormalizado.startsWith(categoria.prefijo)) {
                return categoria;
            }
        }

        return null;
    }
}
