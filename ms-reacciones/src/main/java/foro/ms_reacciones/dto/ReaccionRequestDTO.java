package foro.ms_reacciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReaccionRequestDTO {
    
    @NotBlank(message = "El tipo de reacción es obligatorio")
    private String tipo;
    
    @NotNull(message = "Falta el ID del usuario")
    private Long usuarioId;
    
    @NotNull(message = "Falta el ID del hilo")
    private Long hiloId;
}