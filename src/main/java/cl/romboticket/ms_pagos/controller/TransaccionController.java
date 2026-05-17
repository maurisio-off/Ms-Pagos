package cl.romboticket.ms_pagos.controller;

import cl.romboticket.ms_pagos.dto.TransaccionRequestDTO;
import cl.romboticket.ms_pagos.dto.TransaccionResponseDTO;
import cl.romboticket.ms_pagos.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class TransaccionController {
    private final TransaccionService transaccionService;
    //page agregado
    @GetMapping
    public ResponseEntity<Page<TransaccionResponseDTO>> obtenerTodos(
            @PageableDefault(page = 0, size = 20, sort = "transaccionId") Pageable pageable){
        return ResponseEntity.ok(transaccionService.obtenerTodos(pageable));
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
