package com.foro.ms_usuarios.dto;

import lombok.Data;

@Data 
public class UsuarioResponseDTO {
    
    private Long id;
    private String username;
    private String email;
    private String rango;
    private String corte;
    private String equipo;
}