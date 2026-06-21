# Foro JDM - Zero Grip Society (Arquitectura de Microservicios)

##  Descripción del Proyecto
Este proyecto es el backend de una plataforma orientada a la comunidad automotriz y cultura JDM (Drift, Touge, Stance). Está construido bajo una **Arquitectura de Microservicios** utilizando Spring Boot, garantizando escalabilidad, alta disponibilidad y un enrutamiento seguro.

##  Autor
* **Jeicov Díaz Astorga** - *Desarrollo Full Stack, Infraestructura, Testing y Despliegue Remoto*

##  Stack Tecnológico
* **Framework:** Spring Boot 3.3.0 / Java 17
* **Base de Datos:** MySQL
* **Arquitectura:** Spring Cloud (Netflix Eureka, API Gateway)
* **Contenedores:** Docker & Docker Compose
* **Despliegue Remoto (Cloud):** Railway
* **Documentación:** Swagger (OpenAPI 3)
* **Testing:** JUnit 5 & Mockito (Estructura Given-When-Then con +80% de cobertura)

## Arquitectura del Sistema
El ecosistema está compuesto por 6 microservicios independientes y operativos:
1. **eureka-server:** Servidor de descubrimiento (Service Discovery).
2. **api-gateway:** Pasarela de enrutamiento y filtro de peticiones (CORS, Headers).
3. **ms-usuarios:** Gestión de perfiles, pilotos y equipos del foro.
4. **ms-categorias:** Administración de las categorías de discusión automotriz.
5. **ms-hilos:** Gestión de temas de discusión (Topics).
6. **ms-comentarios:** Gestión de interacciones y respuestas de los usuarios.

##  Entorno Remoto (Producción)
La arquitectura se encuentra completamente funcional y desplegada en la nube mediante contenedores en Railway.
* **URL Pública API Gateway:** `https://feisty-exploration-production-c900.up.railway.app`
* **Documentación Técnica (Swagger UI):** `https://imaginative-caring-production.up.railway.app/swagger-ui/index.html`

##  Instrucciones de Despliegue Local

### Requisitos Previos
* Docker Desktop ejecutándose.
* Java 17 y Maven.

### Pasos para levantar el entorno
1. Clonar este repositorio en tu máquina local.
2. Levantar el contenedor de la base de datos MySQL (Puerto 3307) utilizando Docker Compose:

```bash
docker-compose up -d
```

3. Compilar y ejecutar los microservicios en el siguiente orden estricto (utilizando terminales separadas):
   * Primero: `eureka-server`
   * Segundo: `api-gateway`
   * Tercero: Microservicios de dominio (ej: `ms-usuarios`)
   
   Comando de ejecución (dentro de la carpeta de cada servicio):

```bash
mvnw spring-boot:run
```

##  Pruebas Unitarias
El proyecto cuenta con pruebas unitarias implementadas para validar las reglas de negocio, simulando la capa de persistencia con Mockito. Para ejecutarlas localmente y verificar el estado *BUILD SUCCESS*:

```bash
mvnw test
```
