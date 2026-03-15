package com.bolivar.api_gestion_de_polizas.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Poliza {

    public enum TipoPoliza {
        INDIVIDUAL,
        COLECTIVA
    }

    public enum EstadoPoliza {
        ACTIVA,
        CANCELADA,
        RENOVADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    private TipoPoliza tipoPoliza;
    
    private LocalDateTime fechaInicioVigencia;
    private LocalDateTime fechaFinVigencia;
    private Integer mesesVigencia;
    private BigDecimal valorCanonMensual;
    private BigDecimal prima;
    
    @Enumerated(EnumType.STRING)
    private EstadoPoliza estadoPoliza;
    
    private LocalDateTime fechaDeCreacion;
    private LocalDateTime fechaDeModificacion;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Riesgo> riesgos;

}
