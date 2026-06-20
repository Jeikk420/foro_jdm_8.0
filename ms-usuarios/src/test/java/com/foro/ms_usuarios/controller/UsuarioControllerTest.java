package com.foro.ms_usuarios.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foro.ms_usuarios.dto.UsuarioRequestDTO;
import com.foro.ms_usuarios.dto.UsuarioResponseDTO;
import com.foro.ms_usuarios.service.UsuarioService;

// 👇 Apagamos el motor de base de datos y SOLO encendemos la capa Web (Controller)
@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc; // Nuestro Postman simulado

    @MockBean
    private UsuarioService usuarioService; // El mecánico de mentira

    @Autowired
    private ObjectMapper objectMapper; // Para convertir los objetos a JSON

    @Test
    public void testCrearUsuario_RetornaStatusCreated() throws Exception {
        // 👉 GIVEN: Preparamos los datos que enviaría el usuario
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setUsername("Javier_Drift");
        request.setEmail("javier@zerogrip.com");
        request.setPassword("12345");
        request.setRango("Piloto");
        request.setCorte("Estándar");
        request.setEquipo("Zero Grip Society");

        // Preparamos lo que el servicio (falso) nos va a responder
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(1L);
        response.setUsername("Javier_Drift");
        response.setEquipo("Zero Grip Society");

        when(usuarioService.crearUsuario(any(UsuarioRequestDTO.class))).thenReturn(response);

        // 👉 WHEN & THEN: Hacemos el disparo simulado y verificamos la respuesta
        mockMvc.perform(post("/api/usuarios") // 🚨 Cambia esta ruta si tu endpoint es diferente
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Esperamos un 201 Created
                .andExpect(jsonPath("$.username").value("Javier_Drift"))
                .andExpect(jsonPath("$.equipo").value("Zero Grip Society"));
    }
}