package cl.romboticket.ms_pagos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionRequestDTO {
    @NotNull(message = "El ID de la orden no puede ser nulo")
    private Long ordenId;

    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotNull(message = "El ID del método de pago es obligatorio")
    private Long metodoPagoId; // Recibimos el ID para buscarlo en la BD
}
