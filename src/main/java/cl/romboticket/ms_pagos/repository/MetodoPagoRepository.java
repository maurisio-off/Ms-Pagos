package cl.romboticket.ms_pagos.repository;

import cl.romboticket.ms_pagos.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetodoPagoRepository extends JpaRepository <MetodoPago, Long>{
}
