package cl.romboticket.ms_pagos.service;

import cl.romboticket.ms_pagos.dto.TransaccionRequestDTO;
import cl.romboticket.ms_pagos.dto.TransaccionResponseDTO;
import cl.romboticket.ms_pagos.model.MetodoPago;
import cl.romboticket.ms_pagos.model.Transaccion;
import cl.romboticket.ms_pagos.repository.MetodoPagoRepository;
import cl.romboticket.ms_pagos.repository.TransaccionRepository;
import cl.romboticket.ms_pagos.webclient.OrdenClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final OrdenClient ordenClient;
    private final MetodoPagoRepository metodoPagoRepository;

    private TransaccionResponseDTO mapToDTO(Transaccion transaccion) {
        Map<String, Object> ordenInfo = ordenClient.obtenerOrden(transaccion.getOrdenId());
        String totalStr = ordenInfo.getOrDefault("total","0.00").toString();
        String fechaOrden = ordenInfo.getOrDefault("fechaVenta", "Fecha No Disponible").toString();
        BigDecimal total = new BigDecimal(totalStr);

        String detalleOrden = "Total: $" + total + " | Fecha de la Orden: " + fechaOrden;

        return new TransaccionResponseDTO(
                transaccion.getTransaccionId(),
                detalleOrden,
                transaccion.getMonto(),
                transaccion.getEstadoPago(),
                transaccion.getMetodoPago().getNombreMetPago()
        );
    }

    public Page<TransaccionResponseDTO> obtenerTodos(Pageable pageable) {
        return transaccionRepository.findAll(pageable).map(this::mapToDTO);
    }

    public Optional<TransaccionResponseDTO> obtenerPorId(Long id){
        return transaccionRepository.findById(id).map(this::mapToDTO);
    }

    public TransaccionResponseDTO guardar(TransaccionRequestDTO request){
        // MEJORA: Capturamos la información de la orden para evitar la doble llamada
        Map<String, Object> ordenInfo = ordenClient.obtenerOrden(request.getOrdenId());

        MetodoPago metodo = metodoPagoRepository.findById(request.getMetodoPagoId())
                .orElseThrow(() -> new NoSuchElementException("Método de pago con ID " + request.getMetodoPagoId() + " no encontrado"));

        boolean esPagoExitoso = Math.random() > 0.15;
        String estadoPagoFinal = esPagoExitoso ? "APROBADO" : "RECHAZADO";
        String estadoOrdenFinal = esPagoExitoso ? "Completada" : "Fallida";

        Transaccion transaccion = new Transaccion();
        transaccion.setOrdenId(request.getOrdenId());
        transaccion.setMonto(request.getMonto());
        transaccion.setEstadoPago(estadoPagoFinal);
        transaccion.setMetodoPago(metodo);

        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        ordenClient.cambiarEstadoOrden(request.getOrdenId(), estadoOrdenFinal);

        String totalStr = ordenInfo.getOrDefault("total","0.00").toString();
        String fechaOrden = ordenInfo.getOrDefault("fechaVenta", "Fecha No Disponible").toString();
        BigDecimal total = new BigDecimal(totalStr);
        String detalleOrden = "Total: $" + total + " | Fecha de la Orden: " + fechaOrden;

        return new TransaccionResponseDTO(
                transaccionGuardada.getTransaccionId(),
                detalleOrden,
                transaccionGuardada.getMonto(),
                transaccionGuardada.getEstadoPago(),
                transaccionGuardada.getMetodoPago().getNombreMetPago()
        );
    }
}