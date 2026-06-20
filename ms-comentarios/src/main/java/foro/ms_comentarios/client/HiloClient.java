package foro.ms_comentarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-hilos", url = "http://localhost:8082/api/hilos")
public interface HiloClient {
    @GetMapping("/{id}")
    Object obtenerPorId(@PathVariable("id") Long id);
}