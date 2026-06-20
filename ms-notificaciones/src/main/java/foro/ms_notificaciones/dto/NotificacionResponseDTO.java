package foro.ms_notificaciones.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionResponseDTO {
    private Long id;
    private String mensaje;
    private Long usuarioId;
    private boolean leida;
    private LocalDateTime fechaCreacion;
}