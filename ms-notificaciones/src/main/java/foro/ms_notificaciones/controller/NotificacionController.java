package foro.ms_notificaciones.controller;

import foro.ms_notificaciones.dto.NotificacionRequestDTO;
import foro.ms_notificaciones.dto.NotificacionResponseDTO;
import foro.ms_notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService service;

    // Crear
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> notificar(@Valid @RequestBody NotificacionRequestDTO request) {
        return new ResponseEntity<>(service.crearNotificacion(request), HttpStatus.CREATED);
    }

    // Buscar
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return new ResponseEntity<>(service.obtenerPorId(id), HttpStatus.OK);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody NotificacionRequestDTO request) {
        return new ResponseEntity<>(service.actualizarNotificacion(id, request), HttpStatus.OK);
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarNotificacion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}