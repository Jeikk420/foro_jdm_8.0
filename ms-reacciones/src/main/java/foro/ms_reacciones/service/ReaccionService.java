package foro.ms_reacciones.service;

import foro.ms_reacciones.client.HiloClient;
import foro.ms_reacciones.client.UsuarioClient;
import foro.ms_reacciones.dto.ReaccionRequestDTO;
import foro.ms_reacciones.dto.ReaccionResponseDTO;
import foro.ms_reacciones.model.Reaccion;
import foro.ms_reacciones.repository.ReaccionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReaccionService {
    
    private static final Logger log = LoggerFactory.getLogger(ReaccionService.class);

    @Autowired private ReaccionRepository repository;
    @Autowired private UsuarioClient usuarioClient;
    @Autowired private HiloClient hiloClient;

    // ==========================================
    // 🛠️ CREAR (POST)
    // ==========================================
    public ReaccionResponseDTO crearReaccion(ReaccionRequestDTO dto) {
        log.info("Validando reacción del usuario " + dto.getUsuarioId() + " al hilo " + dto.getHiloId());

        try {
            usuarioClient.obtenerPorId(dto.getUsuarioId());
            hiloClient.obtenerPorId(dto.getHiloId());
            log.info("Validaciones remotas exitosas para la reacción.");
        } catch (Exception e) {
            log.error("Error al validar usuario o hilo: " + e.getMessage());
            throw new RuntimeException("No se pudo procesar la reacción. Usuario o Hilo inválido.");
        }

        Reaccion reaccion = new Reaccion();
        reaccion.setTipo(dto.getTipo());
        reaccion.setUsuarioId(dto.getUsuarioId());
        reaccion.setHiloId(dto.getHiloId());

        Reaccion guardada = repository.save(reaccion);

        ReaccionResponseDTO response = new ReaccionResponseDTO();
        response.setId(guardada.getId());
        response.setTipo(guardada.getTipo());
        response.setUsuarioId(guardada.getUsuarioId());
        response.setHiloId(guardada.getHiloId());
        response.setFechaCreacion(guardada.getFechaCreacion());
        
        return response;
    }

    // ==========================================
    // 🔍 BUSCAR (GET) - (¡Fuga reparada!)
    // ==========================================
    public ReaccionResponseDTO obtenerPorId(Long id) {
        Reaccion reaccion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reacción no encontrada con ID: " + id));
        
        ReaccionResponseDTO response = new ReaccionResponseDTO();
        response.setId(reaccion.getId());
        // 👇 Se agregaron todas las piezas que faltaban
        response.setTipo(reaccion.getTipo());
        response.setUsuarioId(reaccion.getUsuarioId());
        response.setHiloId(reaccion.getHiloId());
        response.setFechaCreacion(reaccion.getFechaCreacion());
        
        return response;
    }

    // ==========================================
    // ⚙️ ACTUALIZAR (PUT)
    // ==========================================
    public ReaccionResponseDTO actualizarReaccion(Long id, ReaccionRequestDTO dto) {
        log.info("Actualizando reacción con ID: " + id);
        
        Reaccion reaccion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reacción no encontrada con ID: " + id));

        // Generalmente solo se actualiza el tipo de reacción (ej: de 'Like' a 'Me encanta')
        reaccion.setTipo(dto.getTipo());

        Reaccion actualizada = repository.save(reaccion);

        ReaccionResponseDTO response = new ReaccionResponseDTO();
        response.setId(actualizada.getId());
        response.setTipo(actualizada.getTipo());
        response.setUsuarioId(actualizada.getUsuarioId());
        response.setHiloId(actualizada.getHiloId());
        response.setFechaCreacion(actualizada.getFechaCreacion());
        
        return response;
    }

    // ==========================================
    // 🗑️ ELIMINAR (DELETE)
    // ==========================================
    public void eliminarReaccion(Long id) {
        log.info("Eliminando reacción con ID: " + id);
        if (!repository.existsById(id)) {
            throw new RuntimeException("Reacción no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}