package com.bolivar.api_gestion_de_polizas.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolivar.api_gestion_de_polizas.dto.addRiesgoDTO;
import com.bolivar.api_gestion_de_polizas.dto.coreMockDTO;
import com.bolivar.api_gestion_de_polizas.entities.Poliza;
import com.bolivar.api_gestion_de_polizas.entities.Riesgo;
import com.bolivar.api_gestion_de_polizas.services.ApiService;
import com.bolivar.api_gestion_de_polizas.services.ApiService.RespuestaApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApiController {
    //Inyecciones
    private final ApiService apiService;

    private ResponseEntity<?> respuestaApiController (RespuestaApi<?> respuesta){
        switch (respuesta.getEstadoObtenido()) {
            //Caso existoso
            case OPERACION_EXITOSA:
            case RECURSO_ACTUALIZADO:
            case RECURSO_CANCELADO:                
                return ResponseEntity.status(HttpStatus.OK).body(respuesta);

            //Casos de validaciones
            case DATOS_INVALIDOS:
            case FORMATO_INVALIDO:
            case PARAMETRO_FALTANTE:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            
            //Casos de recursos no encontrados            
            case POLIZA_NO_ENCONTRADA:
            case RIESGO_NO_ENCONTRADO:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @GetMapping("/polizas")
    public ResponseEntity<?> getPolizas(@RequestParam(required = false) String tipo, @RequestParam(required = false) String estado){
        RespuestaApi<List<Poliza>> respuesta = apiService.getPolizas(tipo, estado);
        return respuestaApiController(respuesta);
    }

    @GetMapping("/polizas/{id}/riesgos")
    public ResponseEntity<?> getRiesgo(@PathVariable("id") UUID polizaId){
        RespuestaApi<List<Riesgo>> respuesta = apiService.getRiesgo(polizaId);
        return respuestaApiController(respuesta);
    }

    @PostMapping("/polizas/{id}/renovar")
    public ResponseEntity<?> updatePoliza(@PathVariable("id") UUID polizaId, @RequestParam(required = false) BigDecimal ipc){
        RespuestaApi<Void> respuesta = apiService.updatePoliza(polizaId, ipc);
        return respuestaApiController(respuesta);
    }

    @PostMapping("/polizas/{id}/cancelar")
    public ResponseEntity<?> cancelPoliza(@PathVariable("id") UUID polizaId){
        RespuestaApi<Void> respuesta = apiService.cancelPoliza(polizaId);
        return respuestaApiController(respuesta);
    }

    @PostMapping("/polizas/{id}/riesgos")
    public ResponseEntity<?> addRiesgo(@PathVariable("id") UUID polizaId, @RequestBody addRiesgoDTO riesgoDto){
        RespuestaApi<Void> respuesta = apiService.addRiesgo(polizaId, riesgoDto);
        return respuestaApiController(respuesta);
    }

    @PostMapping("/riesgos/{id}/cancelar")
    public ResponseEntity<?> cancelarRiesgo(@PathVariable("id") UUID riesgoId){
        RespuestaApi<Void> respuesta = apiService.cancelarRiesgo(riesgoId);
        return respuestaApiController(respuesta);
    }

    @PostMapping("/core-mock/evento")
    public ResponseEntity<?> coreMockEvento(@RequestBody(required = false) coreMockDTO mockDto){
        RespuestaApi<Void> respuesta = apiService.coreMock(mockDto);
        return respuestaApiController(respuesta);
    }

}
