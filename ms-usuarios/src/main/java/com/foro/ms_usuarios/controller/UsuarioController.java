package com.foro.ms_usuarios.controller;

import com.foro.ms_usuarios.dto.UsuarioRequestDTO;
import com.foro.ms_usuarios.dto.UsuarioResponseDTO;
import com.foro.ms_usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO response = service.crearUsuario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return new ResponseEntity<>(service.obtenerPorId(id), HttpStatus.OK);
    }
    // ==========================================
    // 🛠️ RUTA PARA ACTUALIZAR (PUT)
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO response = service.actualizarUsuario(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK); // Retorna 200 OK
    }

    // ==========================================
    // 🗑️ RUTA PARA ELIMINAR (DELETE)
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 Sin Contenido (éxito al borrar)
    }
}
