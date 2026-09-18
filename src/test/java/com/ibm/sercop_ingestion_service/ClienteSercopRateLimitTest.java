//
//        package com.ibm.sercop_ingestion_service;
//
//import com.ibm.sercop_ingestion_service.adapter.out.sercop.cliente.ClienteSercop;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class ClienteSercopRateLimitTest {
//
//    @Autowired
//    private ClienteSercop clienteSercop;
//
//    @Test
//    void deberiaMedirLimiteDePeticiones() {
//
//        for (int i = 1; i <= 2; i++) {
//
//            long inicio = System.currentTimeMillis();
//
//            try {
//
//                String respuesta =
//                        clienteSercop.buscarProcesos(
//                                2026,
//                                "software",
//                                1,
//                                10
//                        );
//                System.out.println(respuesta);
//                long tiempo =
//                        System.currentTimeMillis() - inicio;
//
//                System.out.println();
//                System.out.println(
//                        "========== PETICIÓN " + i + " =========="
//                );
//                System.out.println(
//                        "Estado: OK"
//                );
//                System.out.println(
//                        "Tiempo: " + tiempo + " ms"
//                );
//                System.out.println(
//                        "Respuesta recibida: " +
//                                (respuesta != null)
//                );
//
//            } catch (Exception e) {
//
//                long tiempo =
//                        System.currentTimeMillis() - inicio;
//
//                System.out.println();
//                System.out.println(
//                        "========== PETICIÓN " + i + " =========="
//                );
//                System.out.println(
//                        "Estado: ERROR"
//                );
//                System.out.println(
//                        "Tiempo: " + tiempo + " ms"
//                );
//                System.out.println(
//                        "Error: " + e.getMessage()
//                );
//            }
//        }
//    }
//}
//
