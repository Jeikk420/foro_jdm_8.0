package foro.ms_reacciones.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReaccionResponseDTO {
    private Long id;
    private String tipo;
    private Long usuarioId;
    private Long hiloId;
    private LocalDateTime fechaCreacion;
}