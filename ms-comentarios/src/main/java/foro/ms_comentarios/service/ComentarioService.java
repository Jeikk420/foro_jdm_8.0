package foro.ms_comentarios.service;

import foro.ms_comentarios.client.HiloClient;
import foro.ms_comentarios.client.UsuarioClient;
import foro.ms_comentarios.dto.ComentarioRequestDTO;
import foro.ms_comentarios.dto.ComentarioResponseDTO;
import foro.ms_comentarios.model.Comentario;
import foro.ms_comentarios.repository.ComentarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComentarioService {
    
    private static final Logger log = LoggerFactory.getLogger(ComentarioService.class);

    @Autowired private ComentarioRepository repository;
    @Autowired private UsuarioClient usuarioClient;
    @Autowired private HiloClient hiloClient;

    // ==========================================
    // 🛠️ CREAR (POST) - (Tu lógica intacta)
    // ==========================================
    public ComentarioResponseDTO crearComentario(ComentarioRequestDTO dto) {
        log.info("Validando usuario " + dto.getUsuarioId() + " e hilo " + dto.getHiloId());
        String mensajeRed = "Conexion Optima";

        try {
            usuarioClient.obtenerPorId(dto.getUsuarioId());
            hiloClient.obtenerPorId(dto.getHiloId());
            log.info("Ambas validaciones remotas exitosas.");
        } catch (Exception e) {
            log.error("Fallo de validación remota: " + e.getMessage());
            log.warn("Servicio remoto caido. Asignando piloto anonimo(ID:0) para salvar el comentario.");
            dto.setUsuarioId(0L);
            mensajeRed = "Conexion inestable, guardado offline";
        }

        Comentario comentario = new Comentario();
        comentario.setContenido(dto.getContenido());
        comentario.setUsuarioId(dto.getUsuarioId());
        comentario.setHiloId(dto.getHiloId());
        comentario.setEstadoRed(mensajeRed);

        Comentario guardado = repository.save(comentario);

        ComentarioResponseDTO response = new ComentarioResponseDTO();
        response.setId(guardado.getId());
        response.setContenido(guardado.getContenido());
        response.setUsuarioId(guardado.getUsuarioId());
        response.setHiloId(guardado.getHiloId());
        response.setFechaCreacion(guardado.getFechaCreacion());
        response.setEstadoRed(guardado.getEstadoRed());
        
        return response;
    }

    // ==========================================
    // 🔍 BUSCAR (GET) - (¡Bug reparado!)
    // ==========================================
    public ComentarioResponseDTO obtenerPorId(Long id) {
        Comentario comentario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + id));
        
        ComentarioResponseDTO response = new ComentarioResponseDTO();
        response.setId(comentario.getId());
        response.setContenido(comentario.getContenido());
        // 👇 Faltaban estas piezas:
        response.setUsuarioId(comentario.getUsuarioId());
        response.setHiloId(comentario.getHiloId());
        response.setFechaCreacion(comentario.getFechaCreacion());
        response.setEstadoRed(comentario.getEstadoRed());
        
        return response;
    }

    // ==========================================
    // ⚙️ ACTUALIZAR (PUT)
    // ==========================================
    public ComentarioResponseDTO actualizarComentario(Long id, ComentarioRequestDTO dto) {
        log.info("Actualizando comentario con ID: " + id);
        
        Comentario comentario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + id));

        // Por lógica de negocio, normalmente solo se permite editar el texto del comentario
        comentario.setContenido(dto.getContenido());

        Comentario actualizado = repository.save(comentario);

        ComentarioResponseDTO response = new ComentarioResponseDTO();
        response.setId(actualizado.getId());
        response.setContenido(actualizado.getContenido());
        response.setUsuarioId(actualizado.getUsuarioId());
        response.setHiloId(actualizado.getHiloId());
        response.setFechaCreacion(actualizado.getFechaCreacion());
        response.setEstadoRed(actualizado.getEstadoRed());
        
        return response;
    }

    // ==========================================
    // 🗑️ ELIMINAR (DELETE)
    // ==========================================
    public void eliminarComentario(Long id) {
        log.info("Eliminando comentario con ID: " + id);
        if (!repository.existsById(id)) {
            throw new RuntimeException("Comentario no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}