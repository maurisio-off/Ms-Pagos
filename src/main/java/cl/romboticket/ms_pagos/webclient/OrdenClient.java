package cl.romboticket.ms_pagos.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
@Component
public class OrdenClient {
    private final WebClient webClient;

    public OrdenClient(@Value("${url.ordenes:http://localhost:8088/api/ordenes}")String ordenServidor) {
        this.webClient = WebClient.builder().baseUrl(ordenServidor).build();
    }

    public Map<String, Object> obtenerOrden(Long ordenId) {
        return this.webClient.get()
                .uri("/{id}", ordenId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Orden NO encontrada")))
                .bodyToMono(Map.class).onErrorReturn(Map.of("total", "0.00","fechaVenta",
                        "Servicio no disponible"))
                .block();
    }


    public void cambiarEstadoOrden(Long ordenId, String nuevoEstado) {
        this.webClient.patch()
                .uri("/{id}/estado?nuevoEstado={nuevoEstado}", ordenId, nuevoEstado) // <--- Aquí cambiamos "estado" por "nuevoEstado"
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
