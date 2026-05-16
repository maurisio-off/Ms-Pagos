package cl.romboticket.ms_pagos.config;

import cl.romboticket.ms_pagos.model.MetodoPago;
import cl.romboticket.ms_pagos.model.Transaccion;
import cl.romboticket.ms_pagos.repository.MetodoPagoRepository;
import cl.romboticket.ms_pagos.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MetodoPagoRepository metodoPagoRepository;
    private final TransaccionRepository transaccionRepository;

    @Override
    public void run(String... args) {
        log.info(">>> DataInitializer: Verificando datos de prueba en Pagos...");

        if (metodoPagoRepository.count() > 0) {
            log.info(">>> DataInitializer: La BD ya tiene métodos de pago, se omite la carga inicial.");
            return;
        }

        log.info(">>> DataInitializer: BD vacía detectada. Insertando métodos de pago base...");

        // 1. Insertar Métodos de Pago según tu tabla METODO_PAGO
        MetodoPago tarjetaCredito = metodoPagoRepository.save(new MetodoPago(null, "Tarjeta de Crédito"));
        MetodoPago redcompra = metodoPagoRepository.save(new MetodoPago(null, "Redcompra / Débito"));
        MetodoPago webpay = metodoPagoRepository.save(new MetodoPago(null, "Webpay Plus"));

        log.info(">>> Métodos de pago base insertados correctamente.");

        if (transaccionRepository.count() == 0) {
            log.info(">>> DataInitializer: Insertando transacciones históricas congruentes...");

            // Transacción para la Orden ID 1 (María - $5000.0)
            Transaccion t1 = new Transaccion();
            t1.setOrdenId(1L); // Relación lógica con MS Órdenes
            t1.setMonto(new BigDecimal("5000.00"));
            t1.setEstadoPago("APROBADO");
            t1.setMetodoPago(tarjetaCredito); // FK a MetodoPago
            transaccionRepository.save(t1);

            // Transacción para la Orden ID 2 (Juan - $3500.0)
            Transaccion t2 = new Transaccion();
            t2.setOrdenId(2L); // Relación lógica con MS Órdenes
            t2.setMonto(new BigDecimal("3500.00"));
            t2.setEstadoPago("APROBADO");
            t2.setMetodoPago(webpay); // FK a MetodoPago
            transaccionRepository.save(t2);

            log.info(">>> DataInitializer: 2 Transacciones aprobadas insertadas con éxito.");
        }
    }
}