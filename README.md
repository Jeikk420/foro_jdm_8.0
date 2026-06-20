# Foro JDM - Arquitectura de Microservicios

Sistema de foro para comunidad JDM (Japanese Domestic Market) implementado con arquitectura de microservicios en Spring Boot.

## 📋 Contenido del Proyecto

### Microservicios (6 + 2 Gateway + Eureka)

1. **ms-usuarios** - Gestión de usuarios del foro
2. **ms-hilos** - Gestión de hilos/temas de conversación
3. **ms-comentarios** - Gestión de comentarios en hilos
4. **ms-reacciones** - Gestión de reacciones (likes, etc.)
5. **ms-notificaciones** - Gestión de notificaciones
6. **ms_categorias** - Gestión de categorías del foro
7. **eureka-server** - Servidor de registro de servicios
8. **api-gateway** - Puerta de entrada centralizada

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────┐
│         Clientes HTTP / REST                │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│         API Gateway (Puerto 8080)           │
│      - Enrutamiento de solicitudes          │
│      - CORS globalizado                     │
│      - Balanceo de carga                    │
└────────────────────┬────────────────────────┘
                     │
    ┌────────────────┼────────────────┐
    │                │                │
┌───▼────┐     ┌─────▼──┐      ┌─────▼──┐
│Eureka  │     │Microser│      │Microser│
│Server  │     │vicios  │      │vicios  │
│8761    │     │8081-86 │      │8081-86 │
└────────┘     └────────┘      └────────┘
    ▲              ▲                 ▲
    │              │                 │
    └──────────────┼─────────────────┘
                   │
            ┌──────▼──────┐
            │   MySQL     │
            │  Base Datos │
            └─────────────┘
```

## 🚀 Requisitos Técnicos

- **Java**: 17+
- **Spring Boot**: 3.3.0
- **Spring Cloud**: 2024.0.0
- **Maven**: 3.8.1+
- **Docker**: 20.10+
- **MySQL**: 8.0+
- **Git**: 2.30+

## 🛠️ Configuración e Instalación

### 1. Clonar el Repositorio

```bash
git clone <url-repositorio>
cd foro_jdm_6.0-main
```

### 2. Compilar el Proyecto

```bash
mvn clean install -DskipTests
```

### 3. Ejecutar con Docker Compose

```bash
docker-compose up -d
```

Esto iniciará:
- MySQL (puerto 3306)
- Eureka Server (puerto 8761)
- API Gateway (puerto 8080)
- Todos los microservicios (puertos 8081-8086)

### 4. Ejecutar Localmente (sin Docker)

#### Paso 1: Iniciar Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```

#### Paso 2: Iniciar API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

#### Paso 3: Iniciar Microservicios (en terminales separadas)
```bash
cd ms-usuarios && mvn spring-boot:run
cd ms-hilos && mvn spring-boot:run
cd ms-comentarios && mvn spring-boot:run
cd ms-reacciones && mvn spring-boot:run
cd ms-notificaciones && mvn spring-boot:run
cd ms_categorias && mvn spring-boot:run
```

## 📚 Documentación API

### Swagger/OpenAPI

Cada microservicio expone documentación Swagger en:

- **ms-usuarios**: http://localhost:8081/swagger-ui.html
- **ms-hilos**: http://localhost:8082/swagger-ui.html
- **ms-comentarios**: http://localhost:8083/swagger-ui.html
- **ms-reacciones**: http://localhost:8084/swagger-ui.html
- **ms-notificaciones**: http://localhost:8085/swagger-ui.html
- **ms_categorias**: http://localhost:8086/swagger-ui.html

**A través del Gateway**:
- Gateway (principal): http://localhost:8080/swagger-ui.html

### Rutas del API Gateway

| Servicio | Ruta | Puerto Original |
|----------|------|-----------------|
| Usuarios | `/api/usuarios/**` | 8081 |
| Hilos | `/api/hilos/**` | 8082 |
| Categorías | `/api/categorias/**` | 8083 |
| Comentarios | `/api/comentarios/**` | 8084 |
| Reacciones | `/api/reacciones/**` | 8085 |
| Notificaciones | `/api/notificaciones/**` | 8086 |

## 🧪 Pruebas Unitarias

### Ejecutar Todas las Pruebas

```bash
mvn clean test
```

### Ejecutar Pruebas de un Microservicio

```bash
cd ms-usuarios
mvn clean test
```

### Cobertura de Código

Ver reportes JaCoCo en:
```
target/site/jacoco/index.html
```

**Cobertura Requerida**: Mínimo 80%

### Pruebas Implementadas

- ✅ UsuarioServiceTest (ms-usuarios)
- ✅ HiloServiceTest (ms-hilos)
- ✅ ComentarioServiceTest (ms-comentarios)
- ✅ ReaccionServiceTest (ms-reacciones)
- ✅ NotificacionServiceTest (ms-notificaciones)
- ✅ CategoriaServiceTest (ms_categorias)

**Estructura de Pruebas**: Given-When-Then

**Frameworks**: JUnit 5, Mockito

## 🐳 Docker

### Construir Imágenes

```bash
docker build -t foro-jdm/ms-usuarios ms-usuarios
docker build -t foro-jdm/ms-hilos ms-hilos
# ... repetir para cada microservicio
```

### Docker Compose

Archivo: `docker-compose.yml`

```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener todos
docker-compose down
```

## 📊 Estructura de Carpetas

```
foro_jdm_6.0-main/
├── eureka-server/
│   ├── src/
│   │   ├── main/java/
│   │   └── test/java/
│   ├── pom.xml
│   └── Dockerfile
├── api-gateway/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── ms-usuarios/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/foro/ms_usuarios/
│   │   │   │       ├── controller/
│   │   │   │       ├── service/
│   │   │   │       ├── repository/
│   │   │   │       ├── model/
│   │   │   │       ├── dto/
│   │   │   │       ├── config/
│   │   │   │       └── exception/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/java/
│   │       └── com/foro/ms_usuarios/service/
│   ├── pom.xml
│   └── Dockerfile
├── ms-hilos/
├── ms-comentarios/
├── ms-reacciones/
├── ms-notificaciones/
├── ms_categorias/
├── docker-compose.yml
└── README.md
```

## 🔒 Seguridad

- ✅ Encriptación de contraseñas con BCrypt
- ✅ Spring Security configurado
- ✅ Validación de datos con JSR-380
- ✅ CORS configurado en API Gateway

## 📋 Patrón de Diseño

Todos los microservicios siguen el patrón **CSR (Controller-Service-Repository)**:

```
Controller (Orquestación)
    ↓
Service (Lógica de Negocio)
    ↓
Repository (Acceso a Datos)
    ↓
Model/Entity (Entidad JPA)
```

## 🔄 Comunicación entre Microservicios

- **HTTP REST** con OpenFeign
- **Descubrimiento de Servicios** con Eureka
- **Balanceo de Carga** automático

Ejemplo:
```java
@FeignClient("ms-usuarios")
public interface UsuarioClient {
    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO obtenerUsuario(@PathVariable Long id);
}
```

## 📈 Monitoreo

### Eureka Server
```
http://localhost:8761
```

Ver todos los servicios registrados y su estado.

## 🚢 Despliegue Remoto

### Opciones de Hosting
- Railway.app
- Render.com
- AWS EC2
- Google Cloud Run
- Azure App Service

### Variables de Entorno
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/foro_db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
```

## 📝 Commits y Versionado

El proyecto utiliza commits semánticos:
```
feat: nueva funcionalidad
fix: corrección de bug
test: agregar/modificar tests
docs: cambios en documentación
refactor: refactorización de código
```

## 👥 Equipo de Desarrollo

Desarrollado por: **[Nombres del Equipo]**

## 📄 Licencia

MIT License - Ver LICENSE.md

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/mi-feature`)
3. Commit tus cambios (`git commit -am 'Agregar mi feature'`)
4. Push a la rama (`git push origin feature/mi-feature`)
5. Abre un Pull Request

## ❓ Soporte

Para preguntas o problemas, contactar al equipo de desarrollo.

## 📞 Información de Contacto

- **Email**: soporte@forojdm.com
- **Documentación**: wiki/
- **Issues**: GitHub Issues

---

**Última actualización**: Junio 2025
**Versión**: 1.0.0
**Estado**: Production Ready ✅
