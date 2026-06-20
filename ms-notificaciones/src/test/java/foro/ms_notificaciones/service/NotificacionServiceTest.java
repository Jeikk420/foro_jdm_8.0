package foro.ms_notificaciones.service;

import foro.ms_notificaciones.client.UsuarioClient;
import foro.ms_notificaciones.dto.NotificacionRequestDTO;
import foro.ms_notificaciones.dto.NotificacionResponseDTO;
import foro.ms_notificaciones.model.Notificacion;
import foro.ms_notificaciones.repository.NotificacionRepository;
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
public class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repository; // Simulamos la base de datos

    @Mock
    private UsuarioClient usuarioClient; // Simulamos la radio para validar al usuario receptor

    @InjectMocks
    private NotificacionService service; // El motor real de notificaciones

    // ==========================================
    // 🧪 TEST 1: Creación Exitosa (Radio OK)
    // ==========================================
    @Test
    void crearNotificacion_Exitoso() {
        // 1. GIVEN
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setMensaje("Tienes un nuevo comentario en tu hilo de JDM");
        request.setUsuarioId(1L);

        Notificacion notificacionGuardada = new Notificacion();
        notificacionGuardada.setId(1L);
        notificacionGuardada.setMensaje("Tienes un nuevo comentario en tu hilo de JDM");
        notificacionGuardada.setUsuarioId(1L);
        notificacionGuardada.setLeida(false); // Por defecto nace sin leer
        notificacionGuardada.setFechaCreacion(LocalDateTime.now());

        // Simulamos que el usuario SI existe en el ms-usuarios
        when(usuarioClient.obtenerPorId(1L)).thenReturn(new Object());
        when(repository.save(any(Notificacion.class))).thenReturn(notificacionGuardada);

        // 2. WHEN
        NotificacionResponseDTO response = service.crearNotificacion(request);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Tienes un nuevo comentario en tu hilo de JDM", response.getMensaje());
        assertFalse(response.isLeida()); // Verificamos que esté como "no leída"
        verify(usuarioClient, times(1)).obtenerPorId(1L);
        verify(repository, times(1)).save(any(Notificacion.class));
    }

    // ==========================================
    // 🧪 TEST 2: Falla por Usuario Inexistente
    // ==========================================
    @Test
    void crearNotificacion_Falla_UsuarioNoExiste() {
        // 1. GIVEN
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setMensaje("Alerta del sistema");
        request.setUsuarioId(99L); // ID fantasma

        // Simulamos que la radio estalla
        when(usuarioClient.obtenerPorId(99L)).thenThrow(new RuntimeException("Error remoto"));

        // 2. WHEN & 3. THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.crearNotificacion(request);
        });

        assertEquals("No se puede enviar la notificación. Usuario inválido.", exception.getMessage());
        verify(repository, never()).save(any(Notificacion.class)); // Garantizamos que no guardó basura en la BD
    }

    // ==========================================
    // 🧪 TEST 3: Marcar notificación como leída (PUT)
    // ==========================================
    @Test
    void actualizarNotificacion_MarcarComoLeida_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setMensaje("Mensaje viejo");
        request.setLeida(true); // La acción principal: marcar como leída

        Notificacion notificacionExistente = new Notificacion();
        notificacionExistente.setId(id);
        notificacionExistente.setMensaje("Mensaje viejo");
        notificacionExistente.setLeida(false); // En la BD está como no leída

        Notificacion notificacionActualizada = new Notificacion();
        notificacionActualizada.setId(id);
        notificacionActualizada.setMensaje("Mensaje viejo");
        notificacionActualizada.setLeida(true); // El resultado esperado

        when(repository.findById(id)).thenReturn(Optional.of(notificacionExistente));
        when(repository.save(any(Notificacion.class))).thenReturn(notificacionActualizada);

        // 2. WHEN
        NotificacionResponseDTO response = service.actualizarNotificacion(id, request);

        // 3. THEN
        assertNotNull(response);
        assertTrue(response.isLeida(), "La notificación no se marcó como leída correctamente");
        verify(repository, times(1)).save(notificacionExistente);
    }

    // ==========================================
    // 🧪 TEST 4: Eliminar Notificación
    // ==========================================
    @Test
    void eliminarNotificacion_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        // 2. WHEN
        service.eliminarNotificacion(id);

        // 3. THEN
        verify(repository, times(1)).deleteById(id);
    }
}