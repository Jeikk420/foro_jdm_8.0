package com.foro.ms_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class UsuarioRequestDTO {

    @NotBlank(message = "El username no puede estar vacio")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "El rango es obligatorio loji pollo")
    private String rango;

    private String corte;
    private String equipo;
}