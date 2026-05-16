package cl.romboticket.ms_pagos.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionResponseDTO {

    private Long transaccionId;
    private String detalleOrden;
    private BigDecimal monto;
    private String estadoPago;
    private String metodoPago; // Aquí devolvemos el texto ("TARJETA_CREDITO") para que el usuario lo lea fácil
}
