package foro.ms_comentarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", url = "http://localhost:8081/api/usuarios")
public interface UsuarioClient {
    @GetMapping("/{id}")
    Object obtenerPorId(@PathVariable("id") Long id);
}
