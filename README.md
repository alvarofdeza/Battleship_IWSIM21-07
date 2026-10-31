# Battleship Platform - Core Engine, Enterprise Backend & Web Terminal

![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Java](https://img.shields.io/badge/Java-17%2F22-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-16-000000?style=for-the-badge&logo=nextdotjs&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-v4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?style=for-the-badge&logo=openapiinitiative&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-5.10.2-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)
![Tests](https://img.shields.io/badge/Tests-78%20Passed-success?style=for-the-badge)

Implementacion full-stack modular del clasico juego de combate naval Hundir la Flota (Battleship). Inicialmente desarrollado en el ambito academico para la asignatura Fundamentos de Ingenieria del Software en la Universidad Politecnica de Madrid (UPM - ETSISI), este proyecto ha sido transformado en una plataforma profesional desacoplada con **Clean Architecture**, **Spring Boot 3**, **Spring Security con JWT**, **persistencia relacional Spring Data JPA**, **algoritmos avanzados de Inteligencia Artificial** e **interfaz tactica web militar en Next.js**.

---

## 1. Caracteristicas Principales

* **Arquitectura de Tres Capas Desacopladas:**
  * `core/` (`battleship-core`): Motor de dominio puro sin dependencias de frameworks, jerarquia de naves, validacion de impactos y niebla de guerra.
  * `backend/` (`battleship-backend`): Backend enterprise con Spring Boot 3, inyeccion de dependencias, API REST y seguridad stateless.
  * `frontend/`: Aplicacion web reactiva con Next.js 16 (App Router), React 19, Tailwind CSS y estetica militar retro CRT.
* **Interfaz Web Militar de Vanguardia (Combat Information Center):**
  * Diseno tactico naval estilo terminal CIC de la US Navy con escaneo CRT, efectos de vibracion de pantalla y pixel art.
  * Dique seco interactivo para despliegue de flota con rotacion por atajos de teclado (`R` / `Espacio`), autodespliegue aleatorio y guardia de autenticacion.
  * Sintetizador de audio nativo mediante Web Audio API (pings de sonar, disparos de artilleria, salpicaduras de agua, alarmas y fanfarria de victoria) con control de mute.
  * Cuadricula 10x10 con barrido de haz de radar y telemetria en tiempo real tipo teletipo con auto-scroll.
  * Escalafon de oficiales (Admiralty Roll) con clasificacion global y marcas personales del jugador.
* **Tres Niveles de Inteligencia Artificial (Strategy Pattern):**
  * **Facil (BasicAlgorithm):** Distribucion pseudoaleatoria uniforme entre casillas inexploradas.
  * **Normal (NormalAlgorithm - Hunt & Target):** Patron de paridad de ajedrez `(row + col) % 2 == 0` en modo caza; exploracion ortogonal y continuidad de linea en modo objetivo.
  * **Experto (ExpertAlgorithm - Probability Density Heatmap):** Superposicion probabilistica matricial de barcos vivos sobre 10x10 con multiplicador ponderado x50 en vecindades de impactos activos e inspector visual de densidad.
* **Seguridad Moderna y Autenticacion JWT:**
  * Sustitucion del mock LDAP por Spring Security 6 stateless con JSON Web Tokens (JJWT 0.12.5).
  * Cifrado criptografico seguro de contraseñas mediante `BCryptPasswordEncoder`.
  * Filtro de seguridad `JwtAuthenticationFilter` y endpoints protegidos con rol de usuario.
* **Documentacion Interactiva OpenAPI 3 / Swagger UI & Health Check:**
  * Especificacion viva disponible en tiempo real en `/swagger-ui.html` con soporte Bearer Token.
  * Endpoint de disponibilidad y monitorizacion cloud `/healthz` para orquestadores y pings keep-alive.
* **Principios SOLID, Clean Code e Inmunidad al Vendor Lock-in:**
  * Diseno desacoplado donde el motor de dominio y la capa de persistencia se comunican exclusivamente mediante abstracciones e interfaces estandar (JPA, JDBC, Spring Security).
  * Ningun modulo depende de APIs propietarias de proveedores de hosting o de base de datos, garantizando portabilidad absoluta entre entornos locales (Docker Compose), bases de datos serverless (Neon) o nubes empresariales (AWS, Render, Railway).
  * Desacoplamiento total de dependencias legacy: sustitucion del antiguo `externals-5.1.jar` por la clase autonoma `ExternalLDAP` con modo silencioso para ejecucion en contenedores.
* **Suite Automatizada de 78 Tests (100% Cobertura de Integracion y Dominio):**
  * 58 tests en `core` y 20 tests de integracion y configuracion con MockMvc en `backend`.

---

## 2. Documentacion Tecnica y Modelado

* [modelado/ARQUITECTURA_Y_DISENO.md](modelado/ARQUITECTURA_Y_DISENO.md): Diagramas de componentes, arquitectura enterprise Spring Boot 3, arquitectura web frontend, diagrama de clases, diagrama entidad-relacion (ERD), catalogo de endpoints REST y topologia DevOps.
* [pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md](pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md): Matriz de trazabilidad y reporte de los 78 tests automatizados (JUnit 5, Mockito, Spring Security Test, MockMvc) y verificacion de compilacion frontend.
* [AGENTS.md](AGENTS.md): Hoja de ruta (Roadmap), Definition of Done (DoD) y registro de hitos del proyecto.
* `legacy/`: Archivo historico de artefactos academicos de la universidad (diagramas originales, pruebas iniciales y PDFs) preservados conforme a las normas de ingenieria.

---

## 3. Pila Tecnologica

* **Backend & Dominio:** Java 17 / 22, Spring Boot 3.2.5 (Spring MVC, Spring Data JPA, Spring Security 6, Spring Validation)
* **Frontend Web:** Next.js 16.4 (App Router, Turbopack), React 19, TypeScript 5, Tailwind CSS v4, Lucide Icons, Sonner, Canvas Confetti
* **Audio y FX:** Web Audio API (sintetizador oscilador nativo sin assets externos)
* **Documentacion API:** SpringDoc OpenAPI 2.5.0 / Swagger UI 5.13.0
* **Seguridad & Tokens:** JJWT 0.12.5, BCrypt
* **Bases de Datos:** H2 Database (desarrollo y testing en memoria), PostgreSQL (configurado para produccion)
* **Build & Dependency Management:** Apache Maven Wrapper (`mvnw` / `mvnw.cmd`), npm
* **Testing:** JUnit 5 Jupiter 5.10.2, MockMvc, Mockito 5.11, AssertJ 3.25

---

## 4. Guia de Inicio Rapido

### Requisitos Previos
* Java Development Kit (JDK) 17 o superior.
* Node.js 18 o superior y npm.

### Arranque Rapido en 1 Clic (Desarrollo Local en Windows)
Para iniciar automaticamente el backend Spring Boot y el frontend Next.js en ventanas independientes:
* **Iniciar:** Doble clic en `run-dev.bat` o ejecutar en PowerShell `.\run-dev.ps1`
* **Detener:** Doble clic en `stop-dev.bat` o ejecutar en PowerShell `.\stop-dev.ps1` (o pulsar `Ctrl + C` en cada terminal)

### Ejecucion de la Suite Completa de Tests
Para compilar y verificar los 78 tests unitarios y de integracion:

```bash
# En Windows:
.\mvnw.cmd test

# En Linux o macOS:
./mvnw test
```

### Ejecucion del Servidor Backend (Spring Boot 3)
Para levantar el servidor REST con base de datos H2 y consola Swagger UI:

```bash
# En Windows:
.\mvnw.cmd -pl backend spring-boot:run

# En Linux o macOS:
./mvnw -pl backend spring-boot:run
```

Una vez levantado, accede a:
* **Swagger UI Interactivo:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Consola H2 Database:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:battleship_db`, User: `sa`, Password: *(vacio)*)

### Ejecucion de la Aplicacion Web (Frontend Next.js)
Para levantar la interfaz visual tactica militar en modo desarrollo:

```bash
cd frontend
npm run dev
```

O para verificar el empaquetado de produccion:

```bash
cd frontend
npm run build
```

Accede desde tu navegador a:
* **Consola Tactica Web:** [http://localhost:3000](http://localhost:3000)

### Ejecucion de la Version Consola Legacy (Core)
```bash
# En Windows:
.\mvnw.cmd -pl core compile exec:java

# En Linux o macOS:
./mvnw -pl core compile exec:java
```

---

## 5. Despliegue y Ejecucion con Docker & Docker Compose

La plataforma incluye configuracion multi-contenedor para levantar localmente la base de datos relacional PostgreSQL 16, el backend Spring Boot 3 y la interfaz web Next.js con un unico comando:

```bash
# 1. Crear el archivo de variables de entorno a partir de la plantilla
cp .env.example .env

# 2. Compilar imagenes y levantar los contenedores
docker compose up --build
```

### Servicios Levantados en el Cluster
* **Frontend Web:** [http://localhost:3000](http://localhost:3000) (Next.js 16 Standalone Container)
* **Backend REST API:** [http://localhost:8080](http://localhost:8080) (Spring Boot 3 JRE 17 Container)
* **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Base de Datos PostgreSQL:** `localhost:5432` (Base de datos: `battleship_db`, Usuario: `postgres`)

Para detener y limpiar los contenedores:
```bash
docker compose down
```

---

## 6. Despliegue Cloud en Vivo (Arquitectura de Alta Disponibilidad Gratuita)

Para que la demostracion publica en internet sea **100% gratuita, permanente y sin caducidad de base de datos**, la plataforma implementa una arquitectura desacoplada de alto rendimiento basada en tres servicios cloud especializados:

```
[Usuario / Navegador]
        |
        v (HTTPS / Edge CDN)
 [Vercel: Frontend Next.js 16] ---> (Warm-up Telemetry UX si el servidor despierta)
        |
        v (REST API / Bearer JWT)
 [Render: Backend Docker JRE 17] <--- (Keep-Alive Ping cada 12 min via UptimeRobot)
        |
        v (HikariCP JDBC Pool)
 [Neon: PostgreSQL 16 Serverless] ---> (Persistencia perpetua sin fecha de caducidad)
```

### 6.1 Base de Datos Relacional Permanente (Neon)
1. Accede a [Neon.tech](https://neon.tech) e inicia sesion con tu cuenta de GitHub (100% gratuito sin requerir tarjeta de credito).
2. Crea un proyecto nuevo con base de datos `battleship_db` y PostgreSQL 16. A diferencia de otros proveedores, Neon **no caduca a los 30 dias** y ofrece 0.5 GB de almacenamiento persistente perpetuo.
3. En el panel de control de Neon, copia la cadena de conexion (Connection String).

### 6.2 Backend REST API (Render) y Keep-Alive Ping
1. Accede a [Render](https://render.com) e inicia sesion con GitHub.
2. Crea un nuevo **Web Service**, vincula este repositorio y selecciona:
   * **Runtime:** Docker (utilizara `backend/Dockerfile`).
   * **Root Directory:** `.` (raiz del proyecto).
   * **Plan:** Free.
3. En la seccion de variables de entorno (**Environment Variables**), configura:
   * `SPRING_PROFILES_ACTIVE`: `prod`
   * `SPRING_DATASOURCE_URL`: *(Pega la cadena de conexion copiada de Neon)*
   * `JWT_SECRET`: `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970`
4. Pulsa **Create Web Service**. Render compilara la imagen multi-stage y publicara la URL HTTPS (p. ej. `https://battleship-backend-xxxx.onrender.com`).
5. **Prevencion de Suspension (Keep-Alive):** Para evitar que el contenedor gratuito se duerma tras 15 minutos de inactividad, registra una cuenta gratuita en [UptimeRobot](https://uptimerobot.com) o [Cron-job.org](https://cron-job.org) y anade un monitor de tipo HTTP(s) que envie un `GET` cada 12 minutos a:
   `https://battleship-backend-xxxx.onrender.com/healthz`
   (o alternativamente a `/v3/api-docs`). Esto mantendra el backend despierto 24/7 sin coste alguno.

### 6.3 Frontend Web y Server Warm-Up UX (Vercel)
1. Accede a [Vercel](https://vercel.com) e inicia sesion con GitHub.
2. Importa el repositorio `Battleship_IWSIM21-07`.
3. En la configuracion del proyecto:
   * **Root Directory:** `frontend`
   * **Framework Preset:** Next.js
4. En **Environment Variables**, anade:
   * `NEXT_PUBLIC_API_URL`: `https://battleship-backend-xxxx.onrender.com/api/v1`
5. Pulsa **Deploy**. Vercel compila la aplicacion en su red Edge CDN mundial en ~40 segundos.
6. El frontend incluye un subsistema de deteccion temprana que, si el backend estuviese despertando en frio, muestra una barra de progreso tactica naval ("CIC Reactor Warm-up") informando con precision del estado sin proyectar sensacion de error.

### 6.4 Rol de Cloudflare (Opcional)
Cloudflare actua como proxy inverso, cortafuegos de aplicaciones web (WAF) y red de distribucion DNS. Su uso es altamente recomendado si se desea vincular un **dominio personalizado propio** (por ejemplo `battleship.dev` o `batallanaval.es`). Para los subdominios gratuitos (`*.vercel.app` y `*.onrender.com`), tanto Vercel como Render ya integran sus propios certificados SSL automaticos y redes CDN Anycast, por lo que Cloudflare no es obligatorio en la fase inicial.

---

## 7. Pipeline de Integracion Continua (GitHub Actions)

El archivo `.github/workflows/ci.yml` ejecuta una suite automatizada de verificacion de calidad en cada `push` o `pull request` dirigido a la rama `main`:
* **Job Backend:** Descarga JDK 17 Temurin, cachea dependencias Maven y ejecuta los 78 tests unitarios y de integracion (`BUILD SUCCESS`).
* **Job Frontend:** Descarga Node 20, verifica tipos estricto con TypeScript (`tsc --noEmit`) y compila la aplicacion Next.js con Turbopack.

---

## 8. Credenciales de Prueba Preconfiguradas

Al iniciar la aplicacion, se generan automaticamente los siguientes usuarios semilla:

| Usuario | Email | Password | Rol |
| :--- | :--- | :--- | :--- |
| `admin` | `admin@upm.es` | `admin123` | `ROLE_ADMIN` |
| `alvaro` | `alvaro@alumnos.upm.es` | `password123` | `ROLE_USER` |
| `adrian` | `adrian@alumnos.upm.es` | `password123` | `ROLE_USER` |

