package com.bolivar.api_gestion_de_polizas.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolivar.api_gestion_de_polizas.entities.Riesgo;
import java.util.List;


public interface RiesgoRepository extends JpaRepository<Riesgo, UUID>{

    List<Riesgo> findByPolizaId(UUID polizaId);
}
