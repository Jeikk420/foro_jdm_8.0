package foro.ms_notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificacionRequestDTO {
    
    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;
    
    @NotNull(message = "Falta el ID del usuario receptor")
    private Long usuarioId;

    // Agregamos esto para poder editar el estado en el PUT
    private boolean leida; 
}