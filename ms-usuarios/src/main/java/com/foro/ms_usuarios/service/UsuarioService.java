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
    private PasswordEncoder passwordEncoder; // 👈 1. Inyectamos la juguera criptográfica

    // ==========================================
    // 🛠️ MÉTODO PARA CREAR (POST)
    // ==========================================
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        log.info("Iniciando creación de usuario: " + dto.getUsername());

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        
        // 👇 2. Encriptamos la clave antes de guardarla
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); 
        
        usuario.setRango(dto.getRango());
        usuario.setCorte(dto.getCorte());

        // 👉 AQUÍ INICIA LA REGLA DE NEGOCIO EN VIVO
        String equipoRecibido = dto.getEquipo();
        
        if (equipoRecibido == null || equipoRecibido.isEmpty()) {
            usuario.setEquipo("Piloto Independiente");
        } else {
            usuario.setEquipo(equipoRecibido);
        }
        // 👉 FIN DE LA REGLA DE NEGOCIO
        
        Usuario guardado = repository.save(usuario);
        log.info("Usuario guardado exitosamente en BD con ID: " + guardado.getId());

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(guardado.getId());
        response.setUsername(guardado.getUsername());
        response.setEmail(guardado.getEmail());
        response.setRango(guardado.getRango());
        response.setCorte(guardado.getCorte());
        response.setEquipo(guardado.getEquipo());
        return response;
    } 

    // ==========================================
    // 🛠️ MÉTODO PARA ACTUALIZAR (PUT)
    // ==========================================
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        // 1. Buscamos el usuario en la base de datos
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Le cambiamos los repuestos (actualizamos sus datos)
        usuario.setUsername(requestDTO.getUsername());
        usuario.setEmail(requestDTO.getEmail());
        
        // 👇 3. Encriptamos la nueva clave si el usuario la cambia
        usuario.setPassword(passwordEncoder.encode(requestDTO.getPassword())); 
        
        usuario.setRango(requestDTO.getRango());
        usuario.setCorte(requestDTO.getCorte());
        usuario.setEquipo(requestDTO.getEquipo());

        // 4. Guardamos los cambios en la BD
        Usuario usuarioActualizado = repository.save(usuario);

        // 5. Empaquetamos la respuesta para no mostrar la contraseña
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(usuarioActualizado.getId());
        response.setUsername(usuarioActualizado.getUsername());
        response.setEmail(usuarioActualizado.getEmail());
        response.setRango(usuarioActualizado.getRango());
        response.setCorte(usuarioActualizado.getCorte());
        response.setEquipo(usuarioActualizado.getEquipo());

        return response;
    }

    // ==========================================
    // 🗑️ MÉTODO PARA ELIMINAR (DELETE)
    // ==========================================
    public void eliminarUsuario(Long id) {
        // Verificamos si el piloto existe antes de intentar borrarlo
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        // Lo borramos de la base de datos
        repository.deleteById(id);
    }

    // ==========================================
    // 🔍 MÉTODO PARA BUSCAR (GET)
    // ==========================================
    public UsuarioResponseDTO obtenerPorId(Long id) {
        log.info("Buscando usuario con ID: " + id);
        
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
                
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