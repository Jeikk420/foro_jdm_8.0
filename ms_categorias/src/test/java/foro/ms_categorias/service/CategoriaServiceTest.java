package foro.ms_categorias.service;

import foro.ms_categorias.dto.CategoriaRequestDTO;
import foro.ms_categorias.dto.CategoriaResponseDTO;
import foro.ms_categorias.model.Categoria;
import foro.ms_categorias.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository; // Simulamos la base de datos (Mockito)

    @InjectMocks
    private CategoriaService service; // Inyectamos la base de datos falsa en tu servicio real

    @Test
    void crearCategoria_Exitoso() {
        // 1. GIVEN (Dado este escenario...)
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre("JDM");
        request.setDescripcion("Autos japoneses de los 90s");

        Categoria categoriaGuardada = new Categoria(1L, "JDM", "Autos japoneses de los 90s");
        
        // Le decimos al Mockito qué responder cuando intenten guardar
        when(repository.save(any(Categoria.class))).thenReturn(categoriaGuardada);

        // 2. WHEN (Cuando ejecutamos el método...)
        CategoriaResponseDTO response = service.crearCategoria(request);

        // 3. THEN (Entonces verificamos los resultados...)
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("JDM", response.getNombre());
        verify(repository, times(1)).save(any(Categoria.class)); // Verificamos que se llamó al save 1 vez
    }

    @Test
    void obtenerPorId_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        Categoria categoriaMock = new Categoria(id, "Muscle", "Autos americanos V8");
        when(repository.findById(id)).thenReturn(Optional.of(categoriaMock));

        // 2. WHEN
        CategoriaResponseDTO response = service.obtenerPorId(id);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Muscle", response.getNombre());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void obtenerPorId_NoEncontrado() {
        // 1. GIVEN
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // 2. WHEN & THEN (Verificamos que lance la excepción si no existe)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.obtenerPorId(id);
        });
        
        assertEquals("Categoria no encontrada con ID: 99", exception.getMessage());
    }

    @Test
    void eliminarCategoria_Exitoso() {
        // 1. GIVEN
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        // 2. WHEN
        service.eliminarCategoria(id);

        // 3. THEN
        verify(repository, times(1)).deleteById(id);
    }
}
