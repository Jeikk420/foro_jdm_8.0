package foro.ms_reacciones.service;

import foro.ms_reacciones.client.HiloClient;
import foro.ms_reacciones.client.UsuarioClient;
import foro.ms_reacciones.dto.ReaccionRequestDTO;
import foro.ms_reacciones.dto.ReaccionResponseDTO;
import foro.ms_reacciones.model.Reaccion;
import foro.ms_reacciones.repository.ReaccionRepository;
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
public class ReaccionServiceTest {

    @Mock
    private ReaccionRepository repository; // Simulamos la BD

    @Mock
    private UsuarioClient usuarioClient; // Simulamos la radio a Usuarios

    @Mock
    private HiloClient hiloClient; // Simulamos la radio a Hilos

    @InjectMocks
    private ReaccionService service; // El motor real

    // ==========================================
    // 🧪 TEST 1: Creación Exitosa (Radios OK)
    // ==========================================
    @Test
    void crearReaccion_Exitoso() {
        // 1. GIVEN
        ReaccionRequestDTO request = new ReaccionRequestDTO();
        request.setTipo("Like");
        request.setUsuarioId(1L);
        request.setHiloId(1L);

        Reaccion reaccionGuardada = new Reaccion();
        reaccionGuardada.setId(1L);
        reaccionGuardada.setTipo("Like");
        reaccionGuardada.setUsuarioId(1L);
        reaccionGuardada.setHiloId(1L);
        reaccionGuardada.setFechaCreacion(LocalDateTime.now());

        // Simulamos que las radios NO lanzan error (los IDs existen)
        when(usuarioClient.obtenerPorId(1L)).thenReturn(null); 
        when(hiloClient.obtenerPorId(1L)).thenReturn(null);
        when(repository.save(any(Reaccion.class))).thenReturn(reaccionGuardada);

        // 2. WHEN
        ReaccionResponseDTO response = service.crearReaccion(request);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Like", response.getTipo());
        // Verificamos que el sistema sí llamó a las radios para validar
        verify(usuarioClient, times(1)).obtenerPorId(1L);
        verify(hiloClient, times(1)).obtenerPorId(1L);
        verify(repository, times(1)).save(any(Reaccion.class));
    }

    // ==========================================
    // 🧪 TEST 2: Falla por Usuario o Hilo Inválido
    // ==========================================
    @Test
    void crearReaccion_FallaValidacionRemota() {
        // 1. GIVEN
        ReaccionRequestDTO request = new ReaccionRequestDTO();
        request.setTipo("Like");
        request.setUsuarioId(99L); // ID que no existe
        request.setHiloId(1L);

        // Simulamos que la radio lanza una excepción al buscar el usuario 99
        when(usuarioClient.obtenerPorId(99L)).thenThrow(new RuntimeException("Not Found"));

        // 2. WHEN & 3. THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.crearReaccion(request);
        });
        
        // Verificamos que el Try-Catch de tu Service atajó el error correctamente
        assertEquals("No se pudo procesar la reacción. Usuario o Hilo inválido.", exception.getMessage());
        
        // Verificamos que la reacción NUNCA se guardó en la BD
        verify(repository, never()).save(any(Reaccion.class)); 
    }

    // ==========================================
    // 🧪 TEST 3: Búsqueda Exitosa
    // ==========================================
    @Test
    void obtenerPorId_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        Reaccion reaccionMock = new Reaccion();
        reaccionMock.setId(id);
        reaccionMock.setTipo("Me Encanta");
        reaccionMock.setUsuarioId(2L);
        reaccionMock.setHiloId(1L);
        
        when(repository.findById(id)).thenReturn(Optional.of(reaccionMock));

        // 2. WHEN
        ReaccionResponseDTO response = service.obtenerPorId(id);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Me Encanta", response.getTipo());
        verify(repository, times(1)).findById(id);
    }
}