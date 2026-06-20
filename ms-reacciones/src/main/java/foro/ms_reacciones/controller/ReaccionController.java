package foro.ms_reacciones.controller;

import foro.ms_reacciones.dto.ReaccionRequestDTO;
import foro.ms_reacciones.dto.ReaccionResponseDTO;
import foro.ms_reacciones.service.ReaccionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reacciones")
public class ReaccionController {

    @Autowired
    private ReaccionService service;

    // Crear
    @PostMapping
    public ResponseEntity<ReaccionResponseDTO> reaccionar(@Valid @RequestBody ReaccionRequestDTO request) {
        return new ResponseEntity<>(service.crearReaccion(request), HttpStatus.CREATED);
    }

    // Buscar
    @GetMapping("/{id}")
    public ResponseEntity<ReaccionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return new ResponseEntity<>(service.obtenerPorId(id), HttpStatus.OK);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ReaccionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReaccionRequestDTO request) {
        return new ResponseEntity<>(service.actualizarReaccion(id, request), HttpStatus.OK);
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarReaccion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}