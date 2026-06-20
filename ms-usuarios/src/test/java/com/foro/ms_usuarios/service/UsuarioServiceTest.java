package com.foro.ms_usuarios.service;

import com.foro.ms_usuarios.dto.UsuarioRequestDTO;
import com.foro.ms_usuarios.dto.UsuarioResponseDTO;
import com.foro.ms_usuarios.model.Usuario;
import com.foro.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UsuarioService
 * Cubre operaciones CRUD: Crear, Leer, Actualizar y Eliminar usuarios
 * Cobertura: > 80%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Pruebas Unitarias")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO usuarioRequestDTO;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        // Arrange - Preparar datos de prueba
        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setUsername("carlos");
        usuarioRequestDTO.setEmail("carlos@example.com");
        usuarioRequestDTO.setPassword("password123");
        usuarioRequestDTO.setRango("Profesional");
        usuarioRequestDTO.setCorte("2025");
        usuarioRequestDTO.setEquipo("Scuderia Ferrari");

        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setUsername("carlos");
        usuarioMock.setEmail("carlos@example.com");
        usuarioMock.setPassword("encodedPassword");
        usuarioMock.setRango("Profesional");
        usuarioMock.setCorte("2025");
        usuarioMock.setEquipo("Scuderia Ferrari");
    }

    // ==========================================
    // ✅ TESTS PARA CREAR USUARIO (CREATE)
    // ==========================================

    @Test
    @DisplayName("Dado un UsuarioRequestDTO válido, cuando se crea un usuario, debe retornar UsuarioResponseDTO con datos correctos")
    void testCrearUsuarioExitoso() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        UsuarioResponseDTO response = usuarioService.crearUsuario(usuarioRequestDTO);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("carlos", response.getUsername());
        assertEquals("carlos@example.com", response.getEmail());
        assertEquals("Scuderia Ferrari", response.getEquipo());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Dado un usuario sin equipo especificado, cuando se crea, debe asignarse 'Piloto Independiente'")
    void testCrearUsuarioSinEquipo() {
        // Given
        usuarioRequestDTO.setEquipo(null);
        usuarioMock.setEquipo("Piloto Independiente");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        UsuarioResponseDTO response = usuarioService.crearUsuario(usuarioRequestDTO);

        // Then
        assertNotNull(response);
        assertEquals("Piloto Independiente", response.getEquipo());
    }

    @Test
    @DisplayName("Dado un usuario con equipo vacío, cuando se crea, debe asignarse 'Piloto Independiente'")
    void testCrearUsuarioConEquipoVacio() {
        // Given
        usuarioRequestDTO.setEquipo("");
        usuarioMock.setEquipo("Piloto Independiente");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        UsuarioResponseDTO response = usuarioService.crearUsuario(usuarioRequestDTO);

        // Then
        assertEquals("Piloto Independiente", response.getEquipo());
    }

    // ==========================================
    // ✅ TESTS PARA OBTENER USUARIO (READ)
    // ==========================================

    @Test
    @DisplayName("Dado un ID válido, cuando se obtiene un usuario, debe retornar UsuarioResponseDTO correcto")
    void testObtenerPorIdExitoso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // When
        UsuarioResponseDTO response = usuarioService.obtenerPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals("carlos", response.getUsername());
        assertEquals("carlos@example.com", response.getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Dado un ID inválido, cuando se obtiene un usuario, debe lanzar excepción RuntimeException")
    void testObtenerPorIdNoEncontrado() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> usuarioService.obtenerPorId(999L));
        verify(usuarioRepository, times(1)).findById(999L);
    }

    // ==========================================
    // ✅ TESTS PARA ACTUALIZAR USUARIO (UPDATE)
    // ==========================================

    @Test
    @DisplayName("Dado un usuario existente y datos de actualización válidos, cuando se actualiza, debe retornar datos actualizados")
    void testActualizarUsuarioExitoso() {
        // Given
        UsuarioRequestDTO updateDTO = new UsuarioRequestDTO();
        updateDTO.setUsername("carlos_updated");
        updateDTO.setEmail("carlos_new@example.com");
        updateDTO.setPassword("newPassword123");
        updateDTO.setRango("Veterano");
        updateDTO.setCorte("2026");
        updateDTO.setEquipo("Mercedes");

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setUsername("carlos_updated");
        usuarioActualizado.setEmail("carlos_new@example.com");
        usuarioActualizado.setPassword("encodedNewPassword");
        usuarioActualizado.setRango("Veterano");
        usuarioActualizado.setCorte("2026");
        usuarioActualizado.setEquipo("Mercedes");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        // When
        UsuarioResponseDTO response = usuarioService.actualizarUsuario(1L, updateDTO);

        // Then
        assertNotNull(response);
        assertEquals("carlos_updated", response.getUsername());
        assertEquals("carlos_new@example.com", response.getEmail());
        assertEquals("Mercedes", response.getEquipo());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Dado un ID inexistente, cuando se intenta actualizar, debe lanzar excepción RuntimeException")
    void testActualizarUsuarioNoEncontrado() {
        // Given
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> usuarioService.actualizarUsuario(999L, usuarioRequestDTO));
        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ==========================================
    // ✅ TESTS PARA ELIMINAR USUARIO (DELETE)
    // ==========================================

    @Test
    @DisplayName("Dado un usuario existente, cuando se elimina, debe completar exitosamente")
    void testEliminarUsuarioExitoso() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // When
        usuarioService.eliminarUsuario(1L);

        // Then
        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Dado un usuario inexistente, cuando se intenta eliminar, debe lanzar excepción RuntimeException")
    void testEliminarUsuarioNoEncontrado() {
        // Given
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> usuarioService.eliminarUsuario(999L));
        verify(usuarioRepository, times(1)).existsById(999L);
        verify(usuarioRepository, never()).deleteById(999L);
    }

    // ==========================================
    // ✅ TESTS PARA VALIDACIONES Y REGLAS DE NEGOCIO
    // ==========================================

    @Test
    @DisplayName("Dado un usuario válido, cuando se crea, debe encriptar la contraseña correctamente")
    void testEncriptacionDePassword() {
        // Given
        String passwordOriginal = "password123";
        String passwordEncriptado = "encodedPassword";
        usuarioRequestDTO.setPassword(passwordOriginal);

        when(passwordEncoder.encode(passwordOriginal)).thenReturn(passwordEncriptado);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        usuarioService.crearUsuario(usuarioRequestDTO);

        // Then
        verify(passwordEncoder, times(1)).encode(passwordOriginal);
    }
}
