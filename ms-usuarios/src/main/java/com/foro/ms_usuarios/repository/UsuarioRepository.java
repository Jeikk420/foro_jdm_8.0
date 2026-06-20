package com.foro.ms_usuarios.repository;

import com.foro.ms_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring Boot crea automáticamente las consultas por nosotros
    boolean existsByUsername(String username);
}
