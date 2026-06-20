package com.foro.ms_usuarios.exception; // <- Ajusta esta línea si es necesario

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private String mensaje;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String mensaje, int status) {
        this.mensaje = mensaje;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}