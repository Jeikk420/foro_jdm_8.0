# Guía de Despliegue - Foro JDM

## 🚀 Opciones de Despliegue

### 1. Despliegue Local

Ver: [README.md - Ejecutar Localmente](#)

```bash
# Compilar
mvn clean install -DskipTests

# Ejecutar cada servicio
cd eureka-server && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
# ... más servicios
```

### 2. Docker Compose (Recomendado para Desarrollo)

```bash
docker-compose up -d
```

Accesible en:
- Gateway: http://localhost:8080
- Eureka: http://localhost:8761

### 3. Despliegue en Railway.app

#### Paso 1: Crear Cuenta
```
https://railway.app
```

#### Paso 2: Conectar Repositorio GitHub
1. Crear nuevo proyecto
2. Conectar repositorio
3. Seleccionar rama `main`

#### Paso 3: Configurar Variables de Entorno

```env
# MySQL
DATABASE_URL=mysql://user:password@host:3306/foro_db
SPRING_DATASOURCE_URL=jdbc:mysql://host/foro_db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password

# Eureka
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

# Puertos
SERVER_PORT=8000  # Asignado automáticamente
SPRING_PROFILES_ACTIVE=prod
```

#### Paso 4: Deploy Manual

```bash
# Instalar Railway CLI
npm install -g @railway/cli

# Login
railway login

# Desplegar
railway up
```

### 4. Despliegue en Render.com

#### Paso 1: Crear Cuenta
```
https://render.com
```

#### Paso 2: Crear Servicios Web

Para cada microservicio:

1. **Crear nuevo Web Service**
   - Conectar repositorio GitHub
   - Seleccionar rama
   - Nombre: `foro-ms-usuarios`, etc.
   - Entorno: Docker
   - Region: Closest to users

2. **Configurar Ambiente**
   ```
   Build Command: mvn clean install
   Start Command: java -Dserver.port=$PORT -jar target/*.jar
   ```

3. **Variables de Entorno**
   ```env
   SPRING_DATASOURCE_URL=jdbc:mysql://host/foro_db
   SPRING_DATASOURCE_USERNAME=user
   SPRING_DATASOURCE_PASSWORD=pass
   EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-service-url/eureka/
   ```

### 5. Despliegue en AWS (Elastic Beanstalk)

#### Paso 1: Instalar AWS CLI

```bash
pip install awscli
aws configure
```

#### Paso 2: Crear Aplicación

```bash
# Instalar EB CLI
pip install awsebcli

# Inicializar
eb init -p docker foro-jdm --region us-east-1

# Crear entorno
eb create foro-prod --instance-type t2.small
```

#### Paso 3: Desplegar

```bash
# Compilar
mvn clean package -DskipTests

# Desplegar
eb deploy

# Ver logs
eb logs
```

### 6. Despliegue en Google Cloud Run

#### Paso 1: Instalar Cloud SDK

```bash
# https://cloud.google.com/sdk/docs/install
curl https://sdk.cloud.google.com | bash
gcloud init
```

#### Paso 2: Crear Imagen Docker

```bash
# Para cada microservicio
cd ms-usuarios
gcloud builds submit --tag gcr.io/PROJECT_ID/ms-usuarios
```

#### Paso 3: Desplegar

```bash
gcloud run deploy ms-usuarios \
  --image gcr.io/PROJECT_ID/ms-usuarios \
  --region us-central1 \
  --platform managed \
  --allow-unauthenticated \
  --set-env-vars \
  SPRING_DATASOURCE_URL=jdbc:mysql://host/foro_db,\
  SPRING_DATASOURCE_USERNAME=user,\
  SPRING_DATASOURCE_PASSWORD=pass
```

### 7. Despliegue en Azure App Service

#### Paso 1: Crear Recurso

```bash
# Instalar Azure CLI
# https://docs.microsoft.com/en-us/cli/azure/install-azure-cli

az login

# Crear grupo de recursos
az group create --name foro-rg --location eastus

# Crear App Service Plan
az appservice plan create \
  --name foroplan \
  --resource-group foro-rg \
  --sku B1 \
  --is-linux

# Crear Web App
az webapp create \
  --resource-group foro-rg \
  --plan foroplan \
  --name foro-ms-usuarios \
  --runtime "JAVA|17-java17"
```

#### Paso 2: Desplegar JAR

```bash
mvn clean package -DskipTests

# Desplegar JAR
az webapp deployment source config-zip \
  --resource-group foro-rg \
  --name foro-ms-usuarios \
  --src target/ms-usuarios-0.0.1-SNAPSHOT.jar
```

## 📦 Preparación para Producción

### Checklist Pre-Despliegue

- [ ] Todas las pruebas pasan (`mvn clean test`)
- [ ] Cobertura > 80% (`mvn test jacoco:report`)
- [ ] Documentación Swagger actualizada
- [ ] Variables de entorno configuradas
- [ ] Base de datos migrada/creada
- [ ] Logs configurados
- [ ] Monitoreo activado
- [ ] Copias de seguridad habilitadas
- [ ] SSL/TLS configurado
- [ ] CORS configurado correctamente

### Configuraciones de Producción

#### application-prod.properties

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://prod-db-host:3306/foro_db_prod
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate

# Logging
logging.level.root=WARN
logging.level.com.foro=INFO
logging.file.name=/var/log/foro/app.log

# Security
spring.security.require-ssl=true
server.ssl.enabled=true
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}

# Performance
server.tomcat.threads.max=200
server.tomcat.max-connections=1000
spring.jpa.properties.hibernate.jdbc.batch_size=20

# Actuator
management.endpoints.web.exposure.include=health,metrics
management.endpoint.health.show-details=when-authorized
```

### Docker para Producción

#### Dockerfile Optimizado

```dockerfile
# Build stage
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

## 🔒 Seguridad en Producción

### 1. Variables Sensibles

```bash
# NUNCA comitear credenciales
# Usar .env files o secrets management

# Ejemplo: .env (en .gitignore)
DB_PASSWORD=xxxxx
JWT_SECRET=xxxxx
API_KEY=xxxxx
```

### 2. HTTPS/SSL

```properties
server.ssl.enabled=true
server.ssl.key-store=/path/to/keystore.jks
server.ssl.key-store-password=${SSL_PASSWORD}
server.ssl.key-store-type=JKS
```

### 3. CORS Seguro

```yaml
# application-prod.yml
cors:
  allowed-origins: https://app.example.com
  allowed-methods: GET,POST,PUT,DELETE
  allowed-headers: Content-Type,Authorization
  allow-credentials: true
```

### 4. Rate Limiting

```java
@Configuration
public class RateLimitingConfig {
    @Bean
    public ServletFilter rateLimitFilter() {
        // Implementar rate limiting
        return (request, response, chain) -> {
            // Limitar a 100 requests por minuto
            chain.doFilter(request, response);
        };
    }
}
```

## 📊 Monitoreo en Producción

### Prometheus Metrics

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### ELK Stack (Elasticsearch, Logstash, Kibana)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

## 🔄 CI/CD Pipeline

### GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean install -DskipTests
      - name: Run Tests
        run: mvn test
      - name: Push to Docker Registry
        run: |
          docker build -t myregistry/foro-ms-usuarios .
          docker push myregistry/foro-ms-usuarios
```

## 📝 Rollback

### Si algo sale mal

```bash
# Railway
railway down  # Volver a versión anterior

# AWS Elastic Beanstalk
eb abort  # Abortar despliegue actual
eb swap   # Intercambiar ambiente

# Docker
docker run -d -p 8080:8080 myregistry/foro:v1.0.0  # Versión anterior
```

## 📞 Soporte

Para problemas:
1. Revisar logs: `docker-compose logs -f [service]`
2. Verificar salud: `curl http://localhost:8080/actuator/health`
3. Contactar equipo de desarrollo

---

**Última actualización**: Junio 2025
