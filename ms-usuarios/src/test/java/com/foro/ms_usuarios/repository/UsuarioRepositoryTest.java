package com.foro.ms_usuarios.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.foro.ms_usuarios.model.Usuario;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    // Sobrescribe la configuración global para forzar el uso de sintaxis H2
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class UsuarioRepositoryTest {

    @Autowired 
    private UsuarioRepository repository;

    @Test
    public void testGuardarYBuscarUsuario() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername("Drifter_99");
        nuevoUsuario.setEmail("drift@zerogrip.com");
        nuevoUsuario.setPassword("12345");
        nuevoUsuario.setRango("Amateur");
        nuevoUsuario.setCorte("Estándar");
        nuevoUsuario.setEquipo("Zero Grip Society");

        Usuario usuarioGuardado = repository.save(nuevoUsuario);

        assertNotNull(usuarioGuardado, "El usuario guardado no debería ser nulo");
        assertNotNull(usuarioGuardado.getId(), "La base de datos no asignó un ID");
        assertEquals("Drifter_99", usuarioGuardado.getUsername());
    }
}