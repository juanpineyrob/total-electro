# Manual de Instalación - Total Electro

## 1. Requisitos del Sistema

### 1.1 Requisitos Mínimos

#### Hardware
- **Procesador**: Intel Core i3 o AMD equivalente (2.0 GHz o superior)
- **Memoria RAM**: 4 GB mínimo, 8 GB recomendado
- **Espacio en Disco**: 2 GB de espacio libre
- **Red**: Conexión a Internet para descargar dependencias

#### Software
- **Sistema Operativo**: 
  - Windows 10/11 (64-bit)
  - macOS 10.15 o superior
  - Linux (Ubuntu 20.04+, CentOS 8+, etc.)
- **Java**: OpenJDK 17 o Oracle JDK 17
- **Maven**: Versión 3.8.0 o superior
- **Navegador Web**: Chrome 90+, Firefox 88+, Edge 90+, Safari 14+

### 1.2 Requisitos Recomendados

#### Hardware
- **Procesador**: Intel Core i5 o AMD Ryzen 5 (3.0 GHz o superior)
- **Memoria RAM**: 8 GB o más
- **Espacio en Disco**: 5 GB de espacio libre
- **Red**: Conexión de banda ancha

#### Software
- **IDE**: IntelliJ IDEA, Eclipse, o VS Code
- **Git**: Para control de versiones
- **Docker**: Para contenerización (opcional)

## 2. Instalación de Prerrequisitos

### 2.1 Instalación de Java 17

#### Windows
1. **Descargar OpenJDK 17**:
   - Visite: https://adoptium.net/
   - Descargue la versión para Windows x64
   - Ejecute el instalador y siga las instrucciones

2. **Configurar Variables de Entorno**:
   - Abra "Variables de Entorno del Sistema"
   - En "Variables del Sistema", agregue:
     - **JAVA_HOME**: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot`
     - **Path**: Agregue `%JAVA_HOME%\bin`

3. **Verificar Instalación**:
   ```cmd
   java -version
   javac -version
   ```

#### macOS
1. **Usando Homebrew**:
   ```bash
   brew install openjdk@17
   ```

2. **Configurar PATH**:
   ```bash
   echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
   source ~/.zshrc
   ```

3. **Verificar Instalación**:
   ```bash
   java -version
   javac -version
   ```

#### Linux (Ubuntu/Debian)
1. **Instalar OpenJDK 17**:
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk
   ```

2. **Verificar Instalación**:
   ```bash
   java -version
   javac -version
   ```

### 2.2 Instalación de Maven

#### Windows
1. **Descargar Maven**:
   - Visite: https://maven.apache.org/download.cgi
   - Descargue el archivo binario (apache-maven-x.x.x-bin.zip)
   - Extraiga en `C:\Program Files\Apache\maven`

2. **Configurar Variables de Entorno**:
   - Agregue a las variables del sistema:
     - **MAVEN_HOME**: `C:\Program Files\Apache\maven`
     - **Path**: Agregue `%MAVEN_HOME%\bin`

3. **Verificar Instalación**:
   ```cmd
   mvn -version
   ```

#### macOS
1. **Usando Homebrew**:
   ```bash
   brew install maven
   ```

2. **Verificar Instalación**:
   ```bash
   mvn -version
   ```

#### Linux (Ubuntu/Debian)
1. **Instalar Maven**:
   ```bash
   sudo apt update
   sudo apt install maven
   ```

2. **Verificar Instalación**:
   ```bash
   mvn -version
   ```

### 2.3 Instalación de Git (Opcional)

#### Windows
1. Descargue desde: https://git-scm.com/download/win
2. Ejecute el instalador con configuración por defecto

#### macOS
```bash
brew install git
```

#### Linux
```bash
sudo apt install git
```

## 3. Descarga y Configuración del Proyecto

### 3.1 Clonar el Repositorio

#### Opción 1: Clonar con Git
```bash
git clone https://github.com/your-username/total-electro-final.git
cd total-electro-final
```

#### Opción 2: Descargar ZIP
1. Descargue el archivo ZIP del proyecto
2. Extraiga en una carpeta de su elección
3. Abra terminal/cmd en la carpeta extraída

### 3.2 Verificar Estructura del Proyecto

La estructura del proyecto debe ser:
```
total-electro-final/
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── resources/
│   │   └── webapp/
│   └── test/
├── docs/
├── pom.xml
├── README.md
└── mvnw
```

### 3.3 Configuración del Entorno

#### Configurar Base de Datos (H2)
El proyecto usa H2 en memoria por defecto. No se requiere configuración adicional.

#### Configurar Puerto (Opcional)
Si el puerto 8081 está ocupado, modifique `application.properties`:
```properties
server.port=8082
```

## 4. Compilación y Ejecución

### 4.1 Compilar el Proyecto

#### Verificar Dependencias
```bash
mvn dependency:resolve
```

#### Compilar
```bash
mvn clean compile
```

#### Ejecutar Tests
```bash
mvn test
```

### 4.2 Ejecutar la Aplicación

#### Opción 1: Usando Maven
```bash
mvn spring-boot:run
```

#### Opción 2: Usando JAR
```bash
mvn clean package
java -jar target/totalelectro-0.0.1-SNAPSHOT.jar
```

#### Opción 3: Usando Maven Wrapper
```bash
./mvnw spring-boot:run
```

### 4.3 Verificar que la Aplicación Esté Funcionando

1. **Abrir Navegador**: http://localhost:8081
2. **Verificar Página Principal**: Debe mostrar la página de Total Electro
3. **Verificar Base de Datos**: http://localhost:8081/h2-console
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **Username**: `sa`
   - **Password**: (dejar vacío)

## 5. Configuración de Desarrollo

### 5.1 Configurar IDE

#### IntelliJ IDEA
1. **Abrir Proyecto**:
   - File → Open → Seleccionar carpeta del proyecto
   - Importar como proyecto Maven

2. **Configurar SDK**:
   - File → Project Structure → Project
   - Project SDK: Java 17
   - Project language level: 17

3. **Configurar Run Configuration**:
   - Run → Edit Configurations
   - + → Spring Boot
   - Main class: `com.totalelectro.TotalelectroApplication`
   - JRE: 17

#### Eclipse
1. **Importar Proyecto**:
   - File → Import → Maven → Existing Maven Projects
   - Seleccionar carpeta del proyecto

2. **Configurar JRE**:
   - Project Properties → Java Build Path → Libraries
   - Add Library → JRE System Library → JavaSE-17

#### VS Code
1. **Instalar Extensiones**:
   - Extension Pack for Java
   - Spring Boot Extension Pack

2. **Abrir Proyecto**:
   - File → Open Folder → Seleccionar carpeta del proyecto

### 5.2 Configuración de Debug

#### Configurar Breakpoints
1. Abra cualquier archivo Java en el IDE
2. Haga clic en el margen izquierdo para establecer breakpoints
3. Ejecute en modo debug

#### Configurar Logs
Modifique `application.properties`:
```properties
logging.level.com.totalelectro=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 6. Configuración de Producción

### 6.1 Cambiar Base de Datos

#### PostgreSQL
1. **Agregar Dependencia** en `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. **Configurar** en `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/totalelectro
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

#### MySQL
1. **Agregar Dependencia** en `pom.xml`:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. **Configurar** en `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/totalelectro
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### 6.2 Configurar Seguridad

#### Cambiar Contraseñas por Defecto
1. Modifique `import.sql`:
```sql
-- Cambiar contraseña del admin
UPDATE users SET password = '$2a$10$new_hashed_password' WHERE email = 'admin@totalelectro.com';
```

2. O use el endpoint de cambio de contraseña en la aplicación

#### Configurar HTTPS
1. **Generar Certificado**:
```bash
keytool -genkeypair -alias totalelectro -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore totalelectro.p12 -validity 3650
```

2. **Configurar** en `application.properties`:
```properties
server.ssl.key-store=classpath:totalelectro.p12
server.ssl.key-store-password=your_password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=totalelectro
```

### 6.3 Configurar Logs

#### Archivo de Logs
```properties
logging.file.name=logs/totalelectro.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.level.root=INFO
logging.level.com.totalelectro=INFO
```

#### Logs Externos (Logback)
Crear `src/main/resources/logback-spring.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/totalelectro.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/totalelectro.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

## 7. Despliegue

### 7.1 Crear JAR Ejecutable

```bash
mvn clean package -DskipTests
```

El JAR se creará en: `target/totalelectro-0.0.1-SNAPSHOT.jar`

### 7.2 Ejecutar en Producción

```bash
java -jar totalelectro-0.0.1-SNAPSHOT.jar
```

#### Con Configuración Personalizada
```bash
java -jar totalelectro-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 7.3 Usando Docker (Opcional)

#### Crear Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/totalelectro-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

#### Construir y Ejecutar
```bash
docker build -t totalelectro .
docker run -p 8081:8081 totalelectro
```

### 7.4 Usando Docker Compose

#### Crear docker-compose.yml
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - db
  
  db:
    image: postgres:13
    environment:
      POSTGRES_DB: totalelectro
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

#### Ejecutar
```bash
docker-compose up -d
```

## 8. Monitoreo y Mantenimiento

### 8.1 Health Checks

#### Endpoint de Salud
- **URL**: http://localhost:8081/actuator/health
- **Respuesta**: Estado de la aplicación y dependencias

#### Configurar Actuator
Agregar en `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 8.2 Backup de Base de Datos

#### H2 (Desarrollo)
```bash
# El backup se hace automáticamente al cerrar la aplicación
# Para backup manual, use la consola H2
```

#### PostgreSQL
```bash
pg_dump -h localhost -U username -d totalelectro > backup.sql
```

#### MySQL
```bash
mysqldump -h localhost -u username -p totalelectro > backup.sql
```

### 8.3 Actualizaciones

#### Actualizar Dependencias
```bash
mvn versions:display-dependency-updates
mvn versions:use-latest-versions
```

#### Actualizar Código
```bash
git pull origin main
mvn clean package
```

## 9. Solución de Problemas

### 9.1 Problemas Comunes

#### Error: Puerto ya en uso
**Solución**:
```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux/macOS
lsof -i :8081
kill -9 <PID>
```

#### Error: Java no encontrado
**Solución**:
1. Verificar que Java 17 esté instalado
2. Verificar variables de entorno JAVA_HOME y PATH
3. Reiniciar terminal/IDE

#### Error: Maven no encontrado
**Solución**:
1. Verificar que Maven esté instalado
2. Verificar variables de entorno MAVEN_HOME y PATH
3. Usar Maven Wrapper: `./mvnw`

#### Error: Base de datos no conecta
**Solución**:
1. Verificar configuración en `application.properties`
2. Verificar que la base de datos esté ejecutándose
3. Verificar credenciales

### 9.2 Logs de Error

#### Ver Logs en Tiempo Real
```bash
# Linux/macOS
tail -f logs/totalelectro.log

# Windows
Get-Content logs/totalelectro.log -Wait
```

#### Logs de Spring Boot
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.totalelectro=DEBUG"
```

### 9.3 Performance

#### Optimizar JVM
```bash
java -Xms512m -Xmx2g -jar totalelectro-0.0.1-SNAPSHOT.jar
```

#### Configurar Connection Pool
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

## 10. Credenciales por Defecto

### 10.1 Usuarios de Prueba

#### Administrador
- **Email**: admin@totalelectro.com
- **Contraseña**: pass1234
- **Rol**: ADMIN

#### Usuarios Regulares
- **Email**: juan@ejemplo.com
- **Contraseña**: pass1234
- **Rol**: USER

- **Email**: ana@ejemplo.com
- **Contraseña**: pass1234
- **Rol**: USER

### 10.2 Base de Datos
- **URL**: jdbc:h2:mem:testdb
- **Usuario**: sa
- **Contraseña**: (vacía)
- **Consola**: http://localhost:8081/h2-console

## 11. Contacto y Soporte

### 11.1 Información de Contacto
- **Email**: soporte@totalelectro.com
- **Documentación**: docs/
- **Issues**: GitHub Issues

### 11.2 Recursos Adicionales
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **H2 Database**: http://www.h2database.com/
- **Thymeleaf**: https://www.thymeleaf.org/
- **Bootstrap**: https://getbootstrap.com/

## 12. Changelog

### Versión 1.0.0 (Actual)
- Sistema de e-commerce completo
- Gestión de usuarios y productos
- Carrito de compras
- Sistema de envíos
- Panel administrativo
- Reportes en PDF
- Sistema de preguntas y reseñas 