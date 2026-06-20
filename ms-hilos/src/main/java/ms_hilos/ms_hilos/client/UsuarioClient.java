package ms_hilos.ms_hilos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "ms-usuarios", url = "http://localhost:8081/api/usuarios")
public interface UsuarioClient {

    
    @GetMapping("/{id}")
    Object obtenerUsuarioPorId(@PathVariable("id") Long id);
}