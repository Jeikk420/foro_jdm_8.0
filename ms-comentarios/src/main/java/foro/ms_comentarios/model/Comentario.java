package foro.ms_comentarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comentarios")
public class Comentario {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT") 
    private String contenido;
    
    @Column(nullable = false) 
    private Long usuarioId;
    
    @Column(nullable = false) 
    private Long hiloId;
    
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private String estadoRed;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}