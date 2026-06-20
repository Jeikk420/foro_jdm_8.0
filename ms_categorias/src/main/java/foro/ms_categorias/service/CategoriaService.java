package foro.ms_categorias.service;

import foro.ms_categorias.dto.CategoriaRequestDTO;
import foro.ms_categorias.dto.CategoriaResponseDTO;
import foro.ms_categorias.model.Categoria;
import foro.ms_categorias.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {
    
    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository repository;

    // ==========================================
    // 🛠️ CREAR (POST)
    // ==========================================
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        log.info("Creando nueva categoria: " + dto.getNombre());
        
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        Categoria guardada = repository.save(categoria);
        log.info("Categoria guardada con ID: " + guardada.getId());

        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(guardada.getId());
        response.setNombre(guardada.getNombre());
        response.setDescripcion(guardada.getDescripcion());
        
        return response;
    }

    // ==========================================
    // 🔍 BUSCAR (GET)
    // ==========================================
    public CategoriaResponseDTO obtenerPorId(Long id) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con ID: " + id));

        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        
        return response;
    }

    // ==========================================
    // ⚙️ ACTUALIZAR (PUT)
    // ==========================================
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto) {
        log.info("Actualizando categoria con ID: " + id);
        
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con ID: " + id));

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        Categoria actualizada = repository.save(categoria);

        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(actualizada.getId());
        response.setNombre(actualizada.getNombre());
        response.setDescripcion(actualizada.getDescripcion());
        
        return response;
    }

    // ==========================================
    // 🗑️ ELIMINAR (DELETE)
    // ==========================================
    public void eliminarCategoria(Long id) {
        log.info("Eliminando categoria con ID: " + id);
        if (!repository.existsById(id)) {
            throw new RuntimeException("Categoria no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}