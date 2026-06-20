package ms_hilos.ms_hilos.controller;

import ms_hilos.ms_hilos.dto.HiloRequestDTO;
import ms_hilos.ms_hilos.dto.HiloResponseDTO;
import ms_hilos.ms_hilos.service.HiloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hilos")
public class HiloController {

    @Autowired
    private HiloService service;

    // Crear
    @PostMapping
    public ResponseEntity<HiloResponseDTO> crear(@Valid @RequestBody HiloRequestDTO request) {
        HiloResponseDTO response = service.crearHilo(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Buscar
    @GetMapping("/{id}")
    public ResponseEntity<HiloResponseDTO> obtenerPorId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(service.obtenerPorId(id), HttpStatus.OK);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<HiloResponseDTO> actualizar(@PathVariable("id") Long id, @Valid @RequestBody HiloRequestDTO request) {
        return new ResponseEntity<>(service.actualizarHilo(id, request), HttpStatus.OK);
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminarHilo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}