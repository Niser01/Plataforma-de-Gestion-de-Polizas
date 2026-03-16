package com.bolivar.api_gestion_de_polizas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class addRiesgoDTO {
    private String tipoRiesgo;
    private String descripcion;
}
