package ms_hilos.ms_hilos.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HiloResponseDTO {
    private Long id;
    private String titulo;
    private String contenido;
    private Long usuarioId;
    private Long categoriaId;
    private LocalDateTime fechaCreacion;
    private String clima;
}