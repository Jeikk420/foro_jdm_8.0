package com.foro.ms_usuarios.service;

import com.foro.ms_usuarios.dto.UsuarioRequestDTO;
import com.foro.ms_usuarios.dto.UsuarioResponseDTO;
import com.foro.ms_usuarios.model.Usuario;
import com.foro.ms_usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==========================================
    // 🛠️ MÉTODO PARA CREAR (POST)
    // ==========================================
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        log.info("Iniciando creación de usuario: " + dto.getUsername());

        // 🔥 VALIDACIÓN: Verificar si ya existe
        if (repository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El nombre de usuario '" + dto.getUsername() + "' ya está registrado.");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email '" + dto.getEmail() + "' ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); 
        usuario.setRango(dto.getRango());
        usuario.setCorte(dto.getCorte());

        String equipoRecibido = dto.getEquipo();
        if (equipoRecibido == null || equipoRecibido.isEmpty()) {
            usuario.setEquipo("Piloto Independiente");
        } else {
            usuario.setEquipo(equipoRecibido);
        }
        
        Usuario guardado = repository.save(usuario);
        log.info("Usuario guardado exitosamente con ID: " + guardado.getId());

        return convertirADTO(guardado);
    } 

    // ==========================================
    // 🛠️ MÉTODO PARA ACTUALIZAR (PUT)
    // ==========================================
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setUsername(requestDTO.getUsername());
        usuario.setEmail(requestDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(requestDTO.getPassword())); 
        usuario.setRango(requestDTO.getRango());
        usuario.setCorte(requestDTO.getCorte());
        usuario.setEquipo(requestDTO.getEquipo());

        Usuario usuarioActualizado = repository.save(usuario);
        return convertirADTO(usuarioActualizado);
    }

    // ==========================================
    // 🗑️ MÉTODO PARA ELIMINAR (DELETE)
    // ==========================================
    public void eliminarUsuario(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        repository.deleteById(id);
    }

    // ==========================================
    // 🔍 MÉTODO PARA BUSCAR (GET)
    // ==========================================
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertirADTO(usuario);
    }

    // 👉 MÉTODO AUXILIAR PARA LIMPIAR CÓDIGO (DRY)
    private UsuarioResponseDTO convertirADTO(Usuario usuario) {
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(usuario.getId());
        response.setUsername(usuario.getUsername());
        response.setEmail(usuario.getEmail());
        response.setRango(usuario.getRango());
        response.setCorte(usuario.getCorte());
        response.setEquipo(usuario.getEquipo());
        return response;
    }
}