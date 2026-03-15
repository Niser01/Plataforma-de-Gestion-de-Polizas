package com.bolivar.api_gestion_de_polizas.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolivar.api_gestion_de_polizas.entities.Poliza;

public interface PolizaRepository extends JpaRepository<Poliza, UUID>{

    List<Poliza> findByTipoPolizaAndEstadoPoliza(Poliza.TipoPoliza tipoPoliza, Poliza.EstadoPoliza estadoPoliza);
        
}
