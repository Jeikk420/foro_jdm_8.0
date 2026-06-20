package ms_hilos.ms_hilos.service;

import ms_hilos.ms_hilos.client.UsuarioClient;
import ms_hilos.ms_hilos.dto.HiloRequestDTO;
import ms_hilos.ms_hilos.dto.HiloResponseDTO;
import ms_hilos.ms_hilos.model.Hilo;
import ms_hilos.ms_hilos.repository.HiloRepository;
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
public class HiloServiceTest {

    @Mock
    private HiloRepository repository; // Simulamos la base de datos de Hilos

    @Mock
    private UsuarioClient usuarioClient; // Simulamos la radio para validar usuarios

    @InjectMocks
    private HiloService service; // El motor real de hilos

    // ==========================================
    // 🧪 TEST 1: Creación Exitosa (Radio OK, Regla de negocio normal)
    // ==========================================
    @Test
    void crearHilo_Exitoso_ConClima() {
        // 1. GIVEN
        HiloRequestDTO request = new HiloRequestDTO();
        request.setTitulo("Mi primer drift");
        request.setContenido("Fue increíble...");
        request.setUsuarioId(1L);
        request.setCategoriaId(2L);
        request.setClima("Lluvioso");

        Hilo hiloGuardado = new Hilo();
        hiloGuardado.setId(1L);
        hiloGuardado.setTitulo("Mi primer drift");
        hiloGuardado.setContenido("Fue increíble...");
        hiloGuardado.setUsuarioId(1L);
        hiloGuardado.setCategoriaId(2L);
        hiloGuardado.setClima("Lluvioso");
        hiloGuardado.setFechaCreacion(LocalDateTime.now());

        // Simulamos que el usuario SI existe en el otro microservicio
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(new Object());
        when(repository.save(any(Hilo.class))).thenReturn(hiloGuardado);

        // 2. WHEN
        HiloResponseDTO response = service.crearHilo(request);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Mi primer drift", response.getTitulo());
        assertEquals("Lluvioso", response.getClima());
        verify(usuarioClient, times(1)).obtenerUsuarioPorId(1L);
        verify(repository, times(1)).save(any(Hilo.class));
    }

    // ==========================================
    // 🧪 TEST 2: Creación Exitosa (Regla de Negocio: Clima Vacío)
    // ==========================================
    @Test
    void crearHilo_Exitoso_SinClima_AsignaDesconocido() {
        // 1. GIVEN
        HiloRequestDTO request = new HiloRequestDTO();
        request.setTitulo("Consulta de motor");
        request.setContenido("¿Qué aceite recomiendan?");
        request.setUsuarioId(1L);
        request.setCategoriaId(2L);
        request.setClima(""); // Provocamos la regla de negocio

        Hilo hiloGuardado = new Hilo();
        hiloGuardado.setId(2L);
        hiloGuardado.setClima("Desconocido"); // El resultado esperado de la regla

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(new Object());
        when(repository.save(any(Hilo.class))).thenReturn(hiloGuardado);

        // 2. WHEN
        HiloResponseDTO response = service.crearHilo(request);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Desconocido", response.getClima(), "La regla del clima falló");
        verify(repository, times(1)).save(any(Hilo.class));
    }

    // ==========================================
    // 🧪 TEST 3: Creación Falla (Usuario No Existe o Radio Apagada)
    // ==========================================
    @Test
    void crearHilo_Falla_UsuarioNoExiste() {
        // 1. GIVEN
        HiloRequestDTO request = new HiloRequestDTO();
        request.setUsuarioId(99L); // ID inexistente

        // Simulamos que la radio lanza una excepción o devuelve null
        when(usuarioClient.obtenerUsuarioPorId(99L)).thenThrow(new RuntimeException("Error remoto"));

        // 2. WHEN & 3. THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.crearHilo(request);
        });

        assertEquals("No se puede crear el hilo: El usuario ingresado no existe o ms-usuarios está apagado.", exception.getMessage());
        verify(repository, never()).save(any(Hilo.class)); // Verificamos que NUNCA se guardó el hilo
    }

    // ==========================================
    // 🧪 TEST 4: Obtener por ID Exitoso
    // ==========================================
    @Test
    void obtenerPorId_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        Hilo hiloMock = new Hilo();
        hiloMock.setId(id);
        hiloMock.setTitulo("Ruta a Farellones");
        
        when(repository.findById(id)).thenReturn(Optional.of(hiloMock));

        // 2. WHEN
        HiloResponseDTO response = service.obtenerPorId(id);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Ruta a Farellones", response.getTitulo());
        verify(repository, times(1)).findById(id);
    }

    // ==========================================
    // 🧪 TEST 5: Actualizar Exitoso
    // ==========================================
    @Test
    void actualizarHilo_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        HiloRequestDTO request = new HiloRequestDTO();
        request.setTitulo("Título Editado");
        request.setClima("Soleado");

        Hilo hiloExistente = new Hilo();
        hiloExistente.setId(id);
        hiloExistente.setTitulo("Título Viejo");

        Hilo hiloActualizado = new Hilo();
        hiloActualizado.setId(id);
        hiloActualizado.setTitulo("Título Editado");
        hiloActualizado.setClima("Soleado");

        when(repository.findById(id)).thenReturn(Optional.of(hiloExistente));
        when(repository.save(any(Hilo.class))).thenReturn(hiloActualizado);

        // 2. WHEN
        HiloResponseDTO response = service.actualizarHilo(id, request);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Título Editado", response.getTitulo());
        verify(repository, times(1)).save(hiloExistente);
    }

    // ==========================================
    // 🧪 TEST 6: Eliminar Exitoso
    // ==========================================
    @Test
    void eliminarHilo_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        // 2. WHEN
        service.eliminarHilo(id);

        // 3. THEN
        verify(repository, times(1)).deleteById(id);
    }
}