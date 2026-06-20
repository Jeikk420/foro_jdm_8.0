package foro.ms_comentarios.controller;

import foro.ms_comentarios.dto.ComentarioRequestDTO;
import foro.ms_comentarios.dto.ComentarioResponseDTO;
import foro.ms_comentarios.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired 
    private ComentarioService service;

    // Crear
    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> crear(@Valid @RequestBody ComentarioRequestDTO request) {
        return new ResponseEntity<>(service.crearComentario(request), HttpStatus.CREATED);
    }

    // Buscar
    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return new ResponseEntity<>(service.obtenerPorId(id), HttpStatus.OK);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ComentarioRequestDTO request) {
        return new ResponseEntity<>(service.actualizarComentario(id, request), HttpStatus.OK);
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarComentario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}