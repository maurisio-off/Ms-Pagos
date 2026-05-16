package cl.romboticket.ms_pagos.repository;

import cl.romboticket.ms_pagos.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository  extends JpaRepository <Transaccion, Long>{
}
