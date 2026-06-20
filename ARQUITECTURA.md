# Arquitectura de Microservicios - Foro JDM

## 📐 Diagrama General

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENTES (Web/Mobile)                        │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
        ┌────────────────────────────────────┐
        │      API GATEWAY (Puerto 8080)     │
        │  - Enrutamiento centralizado       │
        │  - Autenticación global (JWT)      │
        │  - Rate Limiting                   │
        │  - CORS                            │
        │  - Balanceo de carga               │
        └────────────────────┬───────────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
            ▼                ▼                ▼
    ┌───────────────┐  ┌──────────────┐  ┌──────────────┐
    │ EUREKA SERVER │  │  MICROSERV.  │  │  MICROSERV.  │
    │  (8761)       │  │   (8081-86)  │  │   (8081-86)  │
    │ - Registro    │  │ - Usuarios   │  │ - Hilos      │
    │ - Discovery   │  │ - Comentarios│  │ - Categorías │
    │ - Health Check│  │ - Reacciones │  │ - etc...     │
    └───────────────┘  └──────────────┘  └──────────────┘
            ▲                  ▲                  ▲
            │                  │                  │
            └──────────────────┼──────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │    MYSQL 8.0        │
                    │  - Persistencia     │
                    │  - Transacciones    │
                    │  - Integridad       │
                    └─────────────────────┘
```

## 🏗️ Patrones de Diseño

### 1. Patrón CSR (Controller-Service-Repository)

```java
// CONTROLLER: Orquestación y validación HTTP
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @PostMapping
    @Operation(summary = "Crear usuario")
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// SERVICE: Lógica de negocio
@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        // Validaciones
        // Transformaciones
        // Reglas de negocio
        Usuario usuario = new Usuario();
        // ... mapping ...
        Usuario guardado = usuarioRepository.save(usuario);
        return mapToDTO(guardado);
    }
}

// REPOSITORY: Acceso a datos
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByEquipo(String equipo);
}
```

### 2. Patrón DTO (Data Transfer Object)

```java
// REQUEST DTO: Datos que recibe el servidor
@Data
@Validated
public class UsuarioRequestDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
}

// RESPONSE DTO: Datos que envía el servidor
@Data
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String email;
    // Nunca incluir password
}
```

### 3. Patrón Open/Closed (Extensible)

```java
// Interfaz base para toda respuesta
@Data
public abstract class BaseResponseDTO {
    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}

// Implementación específica
public class UsuarioResponseDTO extends BaseResponseDTO {
    private String username;
    private String email;
}
```

## 🔄 Comunicación entre Microservicios

### OpenFeign (Client HTTP declarativo)

```java
// Cliente para consumir ms-usuarios desde otro servicio
@FeignClient(name = "ms-usuarios", url = "http://ms-usuarios:8081")
public interface UsuarioClient {
    
    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO obtenerUsuario(@PathVariable Long id);
    
    @PostMapping("/api/usuarios")
    UsuarioResponseDTO crearUsuario(@RequestBody UsuarioRequestDTO dto);
}

// Uso en otro servicio
@Service
public class ComentarioService {
    
    @Autowired
    private UsuarioClient usuarioClient;
    
    public void crearComentario(ComentarioRequestDTO dto) {
        // Verificar que el usuario existe
        UsuarioResponseDTO usuario = usuarioClient.obtenerUsuario(dto.getUsuarioId());
        
        // Crear comentario
        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario.getId());
        // ...
    }
}
```

### Configuración de Fallback (Circuit Breaker)

```java
@FeignClient(
    name = "ms-usuarios", 
    url = "http://ms-usuarios:8081",
    fallback = UsuarioClientFallback.class
)
public interface UsuarioClient {
    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO obtenerUsuario(@PathVariable Long id);
}

// Implementación de fallback
@Component
public class UsuarioClientFallback implements UsuarioClient {
    @Override
    public UsuarioResponseDTO obtenerUsuario(Long id) {
        // Retornar respuesta por defecto o error amigable
        UsuarioResponseDTO fallback = new UsuarioResponseDTO();
        fallback.setId(id);
        fallback.setUsername("Usuario no disponible");
        return fallback;
    }
}
```

## 🔐 Seguridad

### 1. Autenticación con JWT

```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

### 2. Encriptación de Contraseñas

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// En servicio
public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
    Usuario usuario = new Usuario();
    usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
    return usuarioRepository.save(usuario);
}
```

### 3. Validación de Entrada

```java
@RestController
public class UsuarioController {
    
    @PostMapping("/usuarios")
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        // Spring valida automáticamente
        // @NotBlank, @Email, @Size, etc.
    }
}

// En DTOs
@Data
public class UsuarioRequestDTO {
    @NotBlank(message = "El usuario no puede estar vacío")
    @Size(min = 3, max = 50)
    private String username;
    
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
```

## 📊 Base de Datos

### Modelo Relacional

```
USUARIOS
├── id (PK)
├── username (UNIQUE)
├── email (UNIQUE)
├── password (encriptada)
├── rango
├── corte
├── equipo
├── created_at
└── updated_at

HILOS
├── id (PK)
├── titulo
├── descripcion
├── usuario_id (FK → USUARIOS)
├── categoria_id (FK → CATEGORIAS)
├── visualizaciones
├── created_at
└── updated_at

CATEGORIAS
├── id (PK)
├── nombre (UNIQUE)
├── descripcion
└── updated_at

COMENTARIOS
├── id (PK)
├── contenido
├── usuario_id (FK → USUARIOS)
├── hilo_id (FK → HILOS)
├── created_at
└── updated_at

REACCIONES
├── id (PK)
├── tipo (LIKE, DISLIKE)
├── usuario_id (FK → USUARIOS)
├── comentario_id (FK → COMENTARIOS)
└── created_at

NOTIFICACIONES
├── id (PK)
├── tipo (NUEVO_COMENTARIO, NUEVA_REACCION)
├── usuario_id (FK → USUARIOS)
├── referencia_id
├── leida
└── created_at
```

### Consultas Importantes

```sql
-- Hilos populares (más visualizaciones)
SELECT h.id, h.titulo, h.visualizaciones 
FROM hilos h 
ORDER BY h.visualizaciones DESC 
LIMIT 10;

-- Usuarios más activos
SELECT u.id, u.username, COUNT(c.id) as comentarios
FROM usuarios u
LEFT JOIN comentarios c ON u.id = c.usuario_id
GROUP BY u.id
ORDER BY comentarios DESC;

-- Hilos sin respuestas
SELECT h.id, h.titulo
FROM hilos h
LEFT JOIN comentarios c ON h.id = c.hilo_id
WHERE c.id IS NULL;
```

## 🧪 Pruebas

### Estrategia de Testing

```
┌─────────────────────────────────┐
│   Unit Tests (80%)              │
│ - ServiceTest (aislado)         │
│ - Mocks de dependencias         │
│ - JUnit 5 + Mockito             │
└─────────────────────────────────┘
              ▼
┌─────────────────────────────────┐
│   Integration Tests (15%)       │
│ - ControllerTest + BD           │
│ - @SpringBootTest               │
│ - TestContainers (BD real)      │
└─────────────────────────────────┘
              ▼
┌─────────────────────────────────┐
│   E2E Tests (5%)                │
│ - API Gateway test              │
│ - Postman/RestAssured           │
│ - Flujo completo del usuario    │
└─────────────────────────────────┘
```

## 📈 Performance

### Optimizaciones

1. **Índices de BD**
```sql
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_hilos_categoria ON hilos(categoria_id);
CREATE INDEX idx_comentarios_hilo ON comentarios(hilo_id);
```

2. **Paginación**
```java
@GetMapping("/usuarios")
public Page<UsuarioResponseDTO> listar(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    return usuarioService.listar(PageRequest.of(page, size));
}
```

3. **Caché**
```java
@Cacheable("usuarios")
public UsuarioResponseDTO obtenerPorId(Long id) {
    return usuarioService.obtenerPorId(id);
}

@CacheEvict("usuarios")
public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
    return usuarioService.actualizar(id, dto);
}
```

4. **Batch Processing**
```java
// En pom.xml
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.jdbc.fetch_size=50
```

## 🚀 Despliegue

### Configuración por Entorno

```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/foro_db_dev
  jpa:
    hibernate:
      ddl-auto: create-drop
  h2:
    console:
      enabled: true

# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-host:3306/foro_db
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
```

### Escalabilidad

```
Nivel 1: Servicio único (desarrollo)
    └─ Un servidor + BD

Nivel 2: Múltiples instancias (staging)
    └─ N servidores + LB + BD replicada

Nivel 3: Producción
    ├─ Múltiples instancias por servicio
    ├─ Load Balancer
    ├─ DB Master-Slave
    ├─ Redis Cache
    ├─ CDN para assets
    └─ Monitoring (Prometheus, ELK)
```

## 📚 Referencias

- [12 Factor App](https://12factor.net/)
- [Microservices Patterns](https://microservices.io/)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [REST API Best Practices](https://restfulapi.net/)

---

**Última actualización**: Junio 2025
