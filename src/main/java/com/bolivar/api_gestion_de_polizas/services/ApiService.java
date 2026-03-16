package com.bolivar.api_gestion_de_polizas.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bolivar.api_gestion_de_polizas.dto.addRiesgoDTO;
import com.bolivar.api_gestion_de_polizas.entities.Poliza;
import com.bolivar.api_gestion_de_polizas.entities.Riesgo;
import com.bolivar.api_gestion_de_polizas.repositories.PolizaRepository;
import com.bolivar.api_gestion_de_polizas.repositories.RiesgoRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiService {
    
    //Inyecciones de las dependencias
    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;

    //Definicion de los estados posibles de salida
    public enum EstadoObtenido{
        // Operaciones exitosas        
        OPERACION_EXITOSA,
        RECURSO_ACTUALIZADO,
        RECURSO_CANCELADO,
        // Recursos no encontrados
        POLIZA_NO_ENCONTRADA,
        RIESGO_NO_ENCONTRADO,
        // Validaciones
        DATOS_INVALIDOS,
        FORMATO_INVALIDO,
        PARAMETRO_FALTANTE,
        // Errores internos
        ERROR_INTERNO
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RespuestaApi<T>{
        private EstadoObtenido estadoObtenido;
        private String mensajeError;
        private T datosSalida;
    }

    public RespuestaApi<List<Poliza>> getPolizas(Poliza.TipoPoliza tipoPoliza, Poliza.EstadoPoliza estadoPoliza){
        try{
            //Validacion de parametros 
            if (tipoPoliza == null || estadoPoliza == null){
                return new RespuestaApi<List<Poliza>>(EstadoObtenido.PARAMETRO_FALTANTE, "Los parametros 'tipo' y 'estado' son obligatorios", null);
            }

            List<Poliza> polizas = polizaRepository.findByTipoPolizaAndEstadoPoliza(tipoPoliza, estadoPoliza);
            if(polizas.isEmpty()){
                return new RespuestaApi<List<Poliza>>(EstadoObtenido.POLIZA_NO_ENCONTRADA, "No se encontraron pólizas con tipo: " + tipoPoliza + " y estado: " + estadoPoliza, null);
            }

            return new RespuestaApi<List<Poliza>>(EstadoObtenido.OPERACION_EXITOSA, null, polizas);
        }catch(Exception e){
            return new RespuestaApi<List<Poliza>>(EstadoObtenido.ERROR_INTERNO, e.getMessage(), null);
        }
    }

    public RespuestaApi<List<Riesgo>> getRiesgo(UUID polizaId){
        try{
            //Validacion de parametros
            if(polizaId == null){
                return new RespuestaApi<List<Riesgo>>(EstadoObtenido.PARAMETRO_FALTANTE, "Los parametro poliza Id es obligatorio", null);
            }

            boolean existePoliza = polizaRepository.existsById(polizaId);
            if(!existePoliza){
                return new RespuestaApi<List<Riesgo>>(EstadoObtenido.POLIZA_NO_ENCONTRADA, null, null);
            }

            List<Riesgo> riesgo = riesgoRepository.findByPolizaId(polizaId);

            return new RespuestaApi<List<Riesgo>>(EstadoObtenido.OPERACION_EXITOSA, null, riesgo);
        }catch(Exception e){
            return new RespuestaApi<List<Riesgo>>(EstadoObtenido.ERROR_INTERNO, e.getMessage(), null);
        }
    }

    public RespuestaApi<Void> updatePoliza(UUID polizaId, BigDecimal ipc){
        try{
            //Validacion de parametros
            if(polizaId == null){
                return new RespuestaApi<Void>(EstadoObtenido.PARAMETRO_FALTANTE, "Los parametro poliza Id es obligatorio", null);
            }

            if(ipc == null || ipc.compareTo(BigDecimal.ZERO)<=0){
                return new RespuestaApi<Void>(EstadoObtenido.DATOS_INVALIDOS, "El valor del IPC debe ser positivo.", null);
            }

            Optional<Poliza> existePoliza = polizaRepository.findById(polizaId);
            if(existePoliza.isEmpty()){
                return new RespuestaApi<Void>(EstadoObtenido.POLIZA_NO_ENCONTRADA, null, null);
            }
            
            Poliza poliza = existePoliza.get();
            
            //Validacion de reglas de negocio
            if(poliza.getEstadoPoliza() == Poliza.EstadoPoliza.CANCELADA){
                return new RespuestaApi<Void>(EstadoObtenido.DATOS_INVALIDOS, "No se puede renovar una póliza cancelada.", null);
            }

            //Actualizacion del canon y prima respecto del IPC
            BigDecimal factor = new BigDecimal("1").add(ipc.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
            BigDecimal nuevoCanonMensual = (poliza.getValorCanonMensual().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            BigDecimal nuevaPrima = (poliza.getPrima().multiply(factor).setScale(2, RoundingMode.HALF_UP));                  
            
            poliza.setEstadoPoliza(Poliza.EstadoPoliza.RENOVADA);
            poliza.setValorCanonMensual(nuevoCanonMensual);
            poliza.setPrima(nuevaPrima);
            poliza.setFechaDeModificacion(LocalDateTime.now());

            polizaRepository.save(poliza);

            return new RespuestaApi<Void>(EstadoObtenido.RECURSO_ACTUALIZADO, null, null);
        }catch(Exception e){
            return new RespuestaApi<Void>(EstadoObtenido.ERROR_INTERNO, e.getMessage(), null);
        }
    }

    public RespuestaApi<Void> cancelPoliza(UUID polizaId){
        try{
            //Validaciones
            if(polizaId == null){
                return new RespuestaApi<Void>(EstadoObtenido.PARAMETRO_FALTANTE, "Los parametro poliza Id es obligatorio", null);
            }
            Optional<Poliza> existePoliza = polizaRepository.findById(polizaId);
            if(existePoliza.isEmpty()){
                return new RespuestaApi<Void>(EstadoObtenido.POLIZA_NO_ENCONTRADA, null, null);
            }

            Poliza poliza = existePoliza.get();

            if(poliza.getEstadoPoliza() == Poliza.EstadoPoliza.CANCELADA){
                return new RespuestaApi<Void>(EstadoObtenido.DATOS_INVALIDOS, "La poliza ya se encuentra cancelada.", null);
            }

            //Reglas de negocio se cancelan todos los riesgos asociados a la poliza
            List<Riesgo> riesgos = riesgoRepository.findByPolizaId(polizaId);
            if(!riesgos.isEmpty()){
                riesgos.forEach(riesgo -> {
                    riesgo.setEstadoRiesgo(Riesgo.EstadoRiesgo.CANCELADO);
                    riesgo.setFechaDeModificacion(LocalDateTime.now());
                });
                riesgoRepository.saveAll(riesgos);
            }

            poliza.setEstadoPoliza(Poliza.EstadoPoliza.CANCELADA);
            poliza.setFechaDeModificacion(LocalDateTime.now());

            polizaRepository.save(poliza);

            return new RespuestaApi<Void>(EstadoObtenido.RECURSO_CANCELADO, null, null);
        }catch(Exception e){
            return new RespuestaApi<Void>(EstadoObtenido.ERROR_INTERNO, e.getMessage(), null);
        }
    }

    public RespuestaApi<Void> addRiesgo(UUID polizaId, addRiesgoDTO riesgoDto){
        try{
            //Validaciones
            if(polizaId == null){
                return new RespuestaApi<Void>(EstadoObtenido.PARAMETRO_FALTANTE, "Los parametro poliza Id es obligatorio", null);
            }
            if(riesgoDto == null || riesgoDto.getDescripcion() == null || riesgoDto.getTipoRiesgo() == null){
                return new RespuestaApi<Void>(EstadoObtenido.DATOS_INVALIDOS, "Los datos del riesgo son obligatorios", null);
            }

            Optional<Poliza> existePoliza = polizaRepository.findById(polizaId);
            if(existePoliza.isEmpty()){
                return new RespuestaApi<Void>(EstadoObtenido.POLIZA_NO_ENCONTRADA, null, null);
            }

            Poliza poliza = existePoliza.get();
            if(poliza.getEstadoPoliza() == Poliza.EstadoPoliza.CANCELADA){
                return new RespuestaApi<Void>(EstadoObtenido.DATOS_INVALIDOS, "La poliza se encuentra cancelada.", null);
            }

            if(poliza.getTipoPoliza() == Poliza.TipoPoliza.INDIVIDUAL){
                return new RespuestaApi<Void>(EstadoObtenido.DATOS_INVALIDOS, "Solo se pueden agregar riesgos para polizas colectivas en este endpoint", null);
            }

            Riesgo riesgo = new Riesgo();
            riesgo.setDescripcion(riesgoDto.getDescripcion());
            riesgo.setEstadoRiesgo(Riesgo.EstadoRiesgo.ACTIVO);
            riesgo.setFechaDeCreacion(LocalDateTime.now());
            riesgo.setPoliza(poliza);
            
            riesgoRepository.save(riesgo);
            
            return new RespuestaApi<Void>(EstadoObtenido.OPERACION_EXITOSA, null, null);
        }catch(Exception e){
            return new RespuestaApi<Void>(EstadoObtenido.ERROR_INTERNO, e.getMessage(), null);
        }
    }

    public RespuestaApi<Void> cancelarRiesgo(UUID riesgoId){
        try{    
            //Validaciones
            if(riesgoId == null){
                return new RespuestaApi<Void>(EstadoObtenido.PARAMETRO_FALTANTE, "El id del riesgo es necesario", null);
            }

            Optional<Riesgo> existeRiesgo = riesgoRepository.findById(riesgoId);
            if(existeRiesgo.isEmpty()){
                return new RespuestaApi<Void>(EstadoObtenido.RIESGO_NO_ENCONTRADO, null, null);
            } 

            Riesgo riesgo = existeRiesgo.get();

            if(riesgo.getEstadoRiesgo() == Riesgo.EstadoRiesgo.CANCELADO){
                return new RespuestaApi<Void>(EstadoObtenido.DATOS_INVALIDOS, "El riesgo ya se encuentra cancelado", null);
            }

            riesgo.setEstadoRiesgo(Riesgo.EstadoRiesgo.CANCELADO);
            riesgo.setFechaDeModificacion(LocalDateTime.now());

            riesgoRepository.save(riesgo);


            return new RespuestaApi<Void>(EstadoObtenido.RECURSO_CANCELADO, null, null);
        }catch(Exception e){
            return new RespuestaApi<Void>(EstadoObtenido.ERROR_INTERNO, e.getMessage(), null);
        }
    }


}
