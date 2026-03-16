package com.bolivar.api_gestion_de_polizas.dto;

import com.bolivar.api_gestion_de_polizas.services.ApiService.EventoCore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class coreMockDTO {
    private EventoCore evento;
    private Long polizaId;
}
