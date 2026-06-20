# Guía de Pruebas Unitarias - Foro JDM

## 📋 Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Estructura de Pruebas](#estructura-de-pruebas)
3. [Ejecutar Pruebas](#ejecutar-pruebas)
4. [Cobertura de Código](#cobertura-de-código)
5. [Mejores Prácticas](#mejores-prácticas)

## Descripción General

Las pruebas unitarias validan la lógica de negocio de cada microservicio de forma aislada, utilizando mocks para las dependencias externas.

### Frameworks Utilizados
- **JUnit 5**: Framework de testing
- **Mockito**: Creación de objetos mock
- **Spring Boot Test**: Integración con Spring

### Cobertura Requerida
- **Mínimo**: 80% de cobertura de código
- **Herramienta**: JaCoCo (Java Code Coverage)

## Estructura de Pruebas

### Patrón Given-When-Then

Todas las pruebas siguen el patrón **Given-When-Then**:

```java
@Test
@DisplayName("Descripción clara del caso de prueba")
void testCasoEspecifico() {
    // GIVEN - Preparar datos de prueba y mocks
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNombre("Test");
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

    // WHEN - Ejecutar la acción a probar
    UsuarioResponseDTO resultado = usuarioService.obtenerUsuario(1L);

    // THEN - Verificar los resultados
    assertNotNull(resultado);
    assertEquals("Test", resultado.getNombre());
    verify(usuarioRepository, times(1)).findById(1L);
}
```

### Estructura de Carpetas

```
src/test/java/
└── com/foro/[nombre_microservicio]/
    ├── service/
    │   ├── UsuarioServiceTest.java
    │   ├── HiloServiceTest.java
    │   └── [OtroService]Test.java
    ├── controller/
    │   ├── UsuarioControllerTest.java
    │   └── [OtroController]Test.java
    └── repository/
        └── [OtroRepository]Test.java
```

## Ejecutar Pruebas

### 1. Ejecutar Todas las Pruebas del Proyecto

```bash
mvn clean test
```

### 2. Ejecutar Pruebas de un Microservicio Específico

```bash
cd ms-usuarios
mvn clean test
```

### 3. Ejecutar una Clase de Pruebas Específica

```bash
mvn test -Dtest=UsuarioServiceTest
```

### 4. Ejecutar un Método de Prueba Específico

```bash
mvn test -Dtest=UsuarioServiceTest#testCrearUsuarioExitoso
```

### 5. Ejecutar Pruebas con Patrón

```bash
mvn test -Dtest=*ServiceTest
```

### 6. Ver Resultados en la Consola

```bash
mvn clean test -e
```

## Cobertura de Código

### Generar Reporte JaCoCo

```bash
# Para todo el proyecto
mvn clean test jacoco:report

# Para un microservicio específico
cd ms-usuarios
mvn clean test jacoco:report
```

### Ver Reporte JaCoCo

```bash
# Después de ejecutar las pruebas
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target\site\jacoco\index.html  # Windows
```

### Umbral de Cobertura

El proyecto está configurado para fallar si la cobertura es menor al 80%:

```xml
<!-- Configuración en pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Mejores Prácticas

### 1. Nombres Descriptivos

❌ **No recomendado**:
```java
@Test
void test1() { ... }

void testUser() { ... }
```

✅ **Recomendado**:
```java
@Test
@DisplayName("Dado un usuario válido, cuando se crea, debe retornar ID correcto")
void testCrearUsuarioExitoso() { ... }
```

### 2. Una Asercción por Prueba (Cuando sea Posible)

❌ **No recomendado**:
```java
void testCrearUsuario() {
    UsuarioResponseDTO resultado = usuarioService.crear(...);
    assertEquals("Test", resultado.getNombre());
    assertEquals("test@mail.com", resultado.getEmail());
    assertEquals(1L, resultado.getId());
}
```

✅ **Recomendado**:
```java
void testCrearUsuarioRetornaNombre() {
    // ...
    assertEquals("Test", resultado.getNombre());
}

void testCrearUsuarioRetornaEmail() {
    // ...
    assertEquals("test@mail.com", resultado.getEmail());
}

void testCrearUsuarioRetornaId() {
    // ...
    assertEquals(1L, resultado.getId());
}
```

### 3. Usar @BeforeEach para Preparar Datos

```java
class UsuarioServiceTest {
    
    private UsuarioService usuarioService;
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de cada prueba
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    void testCaso1() { ... }

    @Test
    void testCaso2() { ... }
}
```

### 4. Verificar Interacciones con Mockito

```java
// Verificar que se llamó exactamente una vez
verify(usuarioRepository, times(1)).save(any(Usuario.class));

// Verificar que se llamó al menos una vez
verify(usuarioRepository, atLeastOnce()).save(any(Usuario.class));

// Verificar que nunca se llamó
verify(usuarioRepository, never()).delete(any(Usuario.class));

// Verificar argumentos específicos
verify(usuarioRepository).save(argThat(u -> u.getNombre().equals("Test")));
```

### 5. Casos de Prueba Esenciales por Servicio

Para cada servicio CRUD, implementar:

#### CREATE
- ✅ Crear con datos válidos
- ✅ Crear sin datos opcionales (valores por defecto)
- ❌ Crear con datos inválidos (lanzar excepción)

#### READ
- ✅ Obtener registro existente
- ❌ Obtener registro inexistente (lanzar excepción)

#### UPDATE
- ✅ Actualizar registro existente
- ❌ Actualizar registro inexistente (lanzar excepción)

#### DELETE
- ✅ Eliminar registro existente
- ❌ Eliminar registro inexistente (lanzar excepción)

### 6. Pruebas de Validación

```java
@Test
@DisplayName("Debe validar email correcto")
void testValidarEmailValido() {
    assertTrue(usuarioService.esEmailValido("test@mail.com"));
}

@Test
@DisplayName("Debe rechazar email inválido")
void testValidarEmailInvalido() {
    assertFalse(usuarioService.esEmailValido("invalid-email"));
}
```

### 7. Pruebas de Excepciones

```java
// Opción 1: assertThrows
@Test
void testObtenerUsuarioNoEncontrado() {
    when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
    
    assertThrows(RuntimeException.class, 
        () -> usuarioService.obtenerUsuario(999L));
}

// Opción 2: assertThrowsExactly (más específica)
@Test
void testObtenerUsuarioNoEncontrado() {
    when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
    
    assertThrowsExactly(UsuarioNoEncontradoException.class,
        () -> usuarioService.obtenerUsuario(999L));
}
```

## Ejemplos por Microservicio

### ms-usuarios

**Archivo**: `src/test/java/com/foro/ms_usuarios/service/UsuarioServiceTest.java`

Pruebas:
- ✅ Crear usuario con equipo
- ✅ Crear usuario sin equipo (asignar "Piloto Independiente")
- ✅ Obtener usuario por ID
- ✅ Actualizar usuario
- ✅ Eliminar usuario
- ✅ Encriptar contraseña
- ✅ Validación de datos

### ms-hilos

**Archivo**: `src/test/java/com/foro/ms_hilos/service/HiloServiceTest.java`

Pruebas:
- ✅ Crear hilo
- ✅ Obtener hilos por categoría
- ✅ Actualizar estado de hilo
- ✅ Eliminar hilo
- ✅ Incrementar contador de visualizaciones

### ms-comentarios

**Archivo**: `src/test/java/com/foro/ms_comentarios/service/ComentarioServiceTest.java`

Pruebas:
- ✅ Crear comentario
- ✅ Validar longitud mínima
- ✅ Obtener comentarios de un hilo
- ✅ Actualizar comentario
- ✅ Eliminar comentario

## Troubleshooting

### Problema: Las pruebas no se ejecutan

```bash
# Solución: Asegurar que los archivos terminen en *Test.java
# Archivos válidos:
# - UsuarioServiceTest.java ✅
# - UsuarioService_Test.java ✅
# - UsuarioServiceTests.java ✅
#
# Archivos inválidos:
# - UsuarioServiceSpec.java ❌
# - UsuarioServiceCheck.java ❌
```

### Problema: Mocks no funcionan

```java
// Asegurar usar @ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private UsuarioService usuarioService;
}

// O usar MockitoAnnotations.openMocks(this)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}
```

### Problema: Cobertura baja

```bash
# 1. Escribir más pruebas
# 2. Cubrir casos de excepción
# 3. Probar métodos privados (indirectamente)
# 4. Ver reporte JaCoCo para identificar líneas no cubiertas

mvn clean test jacoco:report
# Ver target/site/jacoco/index.html
```

## Recursos Adicionales

- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)

---

**Última actualización**: Junio 2025
