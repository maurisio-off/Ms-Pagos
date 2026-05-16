package cl.romboticket.ms_pagos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaccion")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaccionId;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "monto", precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "estado_pago", length = 20)
    private String estadoPago; //Los estados son "APROBADO", "RECHAZADO" o"PERNDIENTE"

    @ManyToOne
    @JoinColumn(name = "metodo_pago_id")
    private MetodoPago metodoPago;


}
