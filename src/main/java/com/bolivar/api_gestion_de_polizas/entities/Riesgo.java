package com.bolivar.api_gestion_de_polizas.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Riesgo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String tipoRiesgo;
    private String descripcion;
    private LocalDateTime fechaDeCreacion;
    private LocalDateTime fechaDeModificacion;

    @ManyToOne
    @JoinColumn(name = "poliza_id")
    @JsonBackReference
    private Poliza poliza;
}
