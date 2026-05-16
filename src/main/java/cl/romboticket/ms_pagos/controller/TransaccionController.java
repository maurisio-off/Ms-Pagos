package cl.romboticket.ms_pagos.controller;

import cl.romboticket.ms_pagos.dto.TransaccionRequestDTO;
import cl.romboticket.ms_pagos.dto.TransaccionResponseDTO;
import cl.romboticket.ms_pagos.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class TransaccionController {
    private final TransaccionService transaccionService;

    @GetMapping
    public ResponseEntity<List<TransaccionResponseDTO>> obtenerTodos(){
        return ResponseEntity.ok(transaccionService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionResponseDTO> obtenerPorId(@PathVariable Long id){
       return transaccionService.obtenerPorId(id)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<TransaccionResponseDTO> pagar(@Valid @RequestBody TransaccionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionService.guardar(dto));
    }
}
