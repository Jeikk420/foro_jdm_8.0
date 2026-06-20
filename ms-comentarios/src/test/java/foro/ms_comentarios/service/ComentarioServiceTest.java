package foro.ms_comentarios.service;

import foro.ms_comentarios.client.HiloClient;
import foro.ms_comentarios.client.UsuarioClient;
import foro.ms_comentarios.dto.ComentarioRequestDTO;
import foro.ms_comentarios.dto.ComentarioResponseDTO;
import foro.ms_comentarios.model.Comentario;
import foro.ms_comentarios.repository.ComentarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComentarioServiceTest {

    @Mock
    private ComentarioRepository repository; // Simulamos la base de datos

    @Mock
    private UsuarioClient usuarioClient; // Simulamos la radio a Usuarios

    @Mock
    private HiloClient hiloClient; // Simulamos la radio a Hilos

    @InjectMocks
    private ComentarioService service; // El motor real de comentarios

    // ==========================================
    // 🧪 TEST 1: Creación Exitosa (Radios OK)
    // ==========================================
    @Test
    void crearComentario_Exitoso_ConexionOptima() {
        // 1. GIVEN (Dado un escenario donde los otros microservicios responden bien)
        ComentarioRequestDTO request = new ComentarioRequestDTO();
        request.setContenido("¡Tremendo auto!");
        request.setUsuarioId(1L);
        request.setHiloId(1L);

        Comentario comentarioGuardado = new Comentario();
        comentarioGuardado.setId(1L);
        comentarioGuardado.setContenido("¡Tremendo auto!");
        comentarioGuardado.setUsuarioId(1L);
        comentarioGuardado.setHiloId(1L);
        comentarioGuardado.setEstadoRed("Conexion Optima");
        comentarioGuardado.setFechaCreacion(LocalDateTime.now());

        // Simulamos que las validaciones remotas NO lanzan error
        when(usuarioClient.obtenerPorId(1L)).thenReturn(new Object());
        when(hiloClient.obtenerPorId(1L)).thenReturn(new Object());
        when(repository.save(any(Comentario.class))).thenReturn(comentarioGuardado);

        // 2. WHEN (Cuando ejecutamos la creación)
        ComentarioResponseDTO response = service.crearComentario(request);

        // 3. THEN (Entonces verificamos que se asigne el estado óptimo)
        assertNotNull(response);
        assertEquals("Conexion Optima", response.getEstadoRed());
        assertEquals(1L, response.getUsuarioId());
        verify(usuarioClient, times(1)).obtenerPorId(1L);
        verify(hiloClient, times(1)).obtenerPorId(1L);
    }

    // ==========================================
    // 🧪 TEST 2: Validación de Regla de Negocio (Modo Offline)
    // ==========================================
    @Test
    void crearComentario_FallaRemota_AsignaPilotoAnonimo() {
        // 1. GIVEN (Un escenario donde la radio falla o el ms-usuarios está apagado)
        ComentarioRequestDTO request = new ComentarioRequestDTO();
        request.setContenido("Comentando sin internet...");
        request.setUsuarioId(99L);
        request.setHiloId(1L);

        Comentario comentarioGuardadoOffline = new Comentario();
        comentarioGuardadoOffline.setId(2L);
        comentarioGuardadoOffline.setContenido("Comentando sin internet...");
        comentarioGuardadoOffline.setUsuarioId(0L); // El ID anónimo que asigna tu regla
        comentarioGuardadoOffline.setEstadoRed("Conexion inestable, guardado offline");

        // Hacemos que la radio explote a propósito
        when(usuarioClient.obtenerPorId(99L)).thenThrow(new RuntimeException("Servicio apagado"));
        when(repository.save(any(Comentario.class))).thenReturn(comentarioGuardadoOffline);

        // 2. WHEN
        ComentarioResponseDTO response = service.crearComentario(request);

        // 3. THEN (Verificamos que tu "Try-Catch" salvó el comentario)
        assertNotNull(response);
        assertEquals(0L, response.getUsuarioId(), "La regla del piloto anónimo falló");
        assertEquals("Conexion inestable, guardado offline", response.getEstadoRed());
        verify(repository, times(1)).save(any(Comentario.class));
    }

    // ==========================================
    // 🧪 TEST 3: Obtener por ID
    // ==========================================
    @Test
    void obtenerPorId_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        Comentario comentarioMock = new Comentario();
        comentarioMock.setId(id);
        comentarioMock.setContenido("Gran post");
        
        when(repository.findById(id)).thenReturn(Optional.of(comentarioMock));

        // 2. WHEN
        ComentarioResponseDTO response = service.obtenerPorId(id);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Gran post", response.getContenido());
        verify(repository, times(1)).findById(id);
    }

    // ==========================================
    // 🧪 TEST 4: Actualizar Comentario
    // ==========================================
    @Test
    void actualizarComentario_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        ComentarioRequestDTO request = new ComentarioRequestDTO();
        request.setContenido("Texto editado");

        Comentario comentarioExistente = new Comentario();
        comentarioExistente.setId(id);
        comentarioExistente.setContenido("Texto viejo");

        Comentario comentarioActualizado = new Comentario();
        comentarioActualizado.setId(id);
        comentarioActualizado.setContenido("Texto editado");

        when(repository.findById(id)).thenReturn(Optional.of(comentarioExistente));
        when(repository.save(any(Comentario.class))).thenReturn(comentarioActualizado);

        // 2. WHEN
        ComentarioResponseDTO response = service.actualizarComentario(id, request);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Texto editado", response.getContenido());
        verify(repository, times(1)).save(comentarioExistente);
    }

    // ==========================================
    // 🧪 TEST 5: Eliminar Comentario
    // ==========================================
    @Test
    void eliminarComentario_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        // 2. WHEN
        service.eliminarComentario(id);

        // 3. THEN
        verify(repository, times(1)).deleteById(id);
    }
}