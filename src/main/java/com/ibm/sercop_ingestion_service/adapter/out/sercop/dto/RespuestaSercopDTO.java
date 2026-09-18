package com.ibm.sercop_ingestion_service.adapter.out.sercop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class RespuestaSercopDTO {
    private Long total;
    private Long page;
    private Long pages;
    private List<DataJsonSercop> data;
}
