package foro.ms_comentarios.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComentarioResponseDTO {
    private Long id;
    private String contenido;
    private Long usuarioId;
    private Long hiloId;
    private LocalDateTime fechaCreacion;
    private String estadoRed;
}