package foro.ms_comentarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComentarioRequestDTO {
    
    @NotBlank(message = "El comentario no puede estar vacío")
    private String contenido;
    
    @NotNull(message = "Falta el ID del usuario")
    private Long usuarioId;
    
    @NotNull(message = "Falta el ID del hilo")
    private Long hiloId;
}