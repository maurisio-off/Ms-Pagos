package cl.romboticket.ms_pagos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metodo_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metodo_pago_id")
    private Long metodoPagoId;

    @Column(name = "nombre_met_pago", length = 50)
    private String nombreMetPago; //Son "TRAJETA_CREDITO","TARJETA_DEBITO"

}
