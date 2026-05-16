package cl.romboticket.ms_pagos.service;

import cl.romboticket.ms_pagos.dto.TransaccionRequestDTO;
import cl.romboticket.ms_pagos.dto.TransaccionResponseDTO;
import cl.romboticket.ms_pagos.model.MetodoPago;
import cl.romboticket.ms_pagos.model.Transaccion;
import cl.romboticket.ms_pagos.repository.MetodoPagoRepository;
import cl.romboticket.ms_pagos.repository.TransaccionRepository;
import cl.romboticket.ms_pagos.webclient.OrdenClient;
import lombok.RequiredArgsConstructor;
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

    public List<TransaccionResponseDTO> obtenerTodos() {
        return transaccionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TransaccionResponseDTO> obtenerPorId(Long id){
        return transaccionRepository.findById(id).map(this::mapToDTO);
    }

    @Transactional
    public TransaccionResponseDTO guardar(TransaccionRequestDTO request){
        // revisar que exista la orden del ms-reservas
        ordenClient.obtenerOrden(request.getOrdenId());

        // validamos el met_pago
        MetodoPago metodo = metodoPagoRepository.findById(request.getMetodoPagoId())
                .orElseThrow(() -> new NoSuchElementException("Método de pago con ID " + request.getMetodoPagoId() + " no encontrado"));

        // simular éxito 85% de probabilidad
        boolean esPagoExitoso = Math.random() > 0.15;
        String estadoPagoFinal = esPagoExitoso ? "APROBADO" : "RECHAZADO";
        String estadoOrdenFinal = esPagoExitoso ? "Completada" : "Fallida";

        // armamos y guardamos la transacción en la BD de pagos
        Transaccion transaccion = new Transaccion();
        transaccion.setOrdenId(request.getOrdenId());
        transaccion.setMonto(request.getMonto());
        transaccion.setEstadoPago(estadoPagoFinal);
        transaccion.setMetodoPago(metodo);

        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        ordenClient.cambiarEstadoOrden(request.getOrdenId(), estadoOrdenFinal);
        //mostrar boleta o detalle
        return mapToDTO(transaccionGuardada);
    }
}