# FleetGrid Platform - Tactical Naval Command & Combat Engine

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
![Tests](https://img.shields.io/badge/Tests-180%20Passed-success?style=for-the-badge)

> [!NOTE]
> **Evolución del Proyecto:** FleetGrid es la evolución tecnológica avanzada y maduración profesional del proyecto académico original `Battleship_IWSIM21-07`, desarrollado inicialmente para la asignatura Fundamentos de Ingeniería del Software en la Universidad Politécnica de Madrid (UPM - ETSISI).

Plataforma full-stack modular de combate naval táctico (FleetGrid). El proyecto ha sido transformado en una solución desacoplada de alto rendimiento con **Clean Architecture**, **Spring Boot 3**, **Spring Security con JWT**, **servicio transaccional JavaMail (SMTP)**, **persistencia relacional Spring Data JPA**, **algoritmos avanzados de Inteligencia Artificial** e **interfaz táctica web militar retro CRT en Next.js 16**.

---

## Enlaces de Produccion en Vivo (Live Demo)

* **Interfaz Tactica Web (Vercel Edge):** [https://battleship-iwsim-21-07.vercel.app/](https://battleship-iwsim-21-07.vercel.app/)
* **Backend REST API (Render Docker):** [https://battleship-backend-ys61.onrender.com](https://battleship-backend-ys61.onrender.com)
* **Documentacion OpenAPI 3 / Swagger UI:** [https://battleship-backend-ys61.onrender.com/swagger-ui.html](https://battleship-backend-ys61.onrender.com/swagger-ui.html)
* **Monitor de Salud (Health Check):** [https://battleship-backend-ys61.onrender.com/healthz](https://battleship-backend-ys61.onrender.com/healthz)
* **Base de Datos Cloud:** PostgreSQL 16 Serverless en Neon (Region Frankfurt `eu-central-1`, sin caducidad)

---

## 1. Caracteristicas Principales

* **Arquitectura de Tres Capas Desacopladas:**
  * `core/` (`battleship-core`): Motor de dominio puro sin dependencias de frameworks, jerarquia de naves, validacion de impactos y niebla de guerra.
  * `backend/` (`battleship-backend`): Backend enterprise con Spring Boot 3, inyeccion de dependencias, API REST y seguridad stateless.
  * `frontend/`: Aplicacion web reactiva con Next.js 16 (App Router), React 19, Tailwind CSS y estetica militar retro CRT.
* **Interfaz Web Militar de Vanguardia (Combat Information Center):**
  * Diseno tactico naval estilo terminal CIC de la US Navy con escaneo CRT, efectos de vibracion de pantalla y pixel art.
  * Dique seco interactivo para despliegue de flota con rotacion por atajos de teclado (`R` / `Espacio`), autodespliegue aleatorio y modo invitado de acceso rapido.
  * Sintetizador de audio nativo mediante Web Audio API (pings de sonar, disparos de artilleria, salpicaduras de agua, alarmas y fanfarria de victoria) con control de mute.
  * Cuadricula 10x10 con barrido de haz de radar y telemetria en tiempo real tipo teletipo con auto-scroll.
  * Doble escalafon de oficiales (Admiralty Roll): ranking de mejores partidas individuales y ranking de mejores oficiales por puntos acumulados de carrera.
  * Expediente de Oficial (Profile): consulta de estadisticas personales, ultimas 10 partidas, top 10 historico, rango naval y gestion de credenciales.
* **Sistema de Puntuacion Multidimensional y Anti-Farming:**
  * Formula ponderada que premia la dificultad del combate (x1.0 a x2.5), la eficiencia de disparos (proximidad al minimo teorico de 14 impactos) y la velocidad de resolucion.
  * Puntos de consolacion de honor en derrotas completas calculados por barcos enemigos hundidos antes de caer, con proteccion estricta frente a abandonos.
* **Seguridad Moderna, Modo Invitado y Verificacion OTP:**
  * Modo Invitado sin barreras de entrada con opcion de reclamar y persistir la puntuacion al finalizar el combate.
  * Verificacion de correo electronico mediante codigo PIN militar de 6 digitos entregado en bandeja de entrada a traves de servicio de correo transaccional.
  * Seguridad stateless basada en tokens Bearer JWT (JJWT 0.12.5) y contraseñas hasheadas con BCrypt.
* **Documentacion Interactiva OpenAPI 3 / Swagger UI & Health Check:**
  * Especificacion viva disponible en tiempo real en `/swagger-ui.html` con soporte Bearer Token.
  * Endpoint de disponibilidad y monitorizacion cloud `/healthz` para orquestadores y pings keep-alive.
* **Principios SOLID, Clean Code e Inmunidad al Vendor Lock-in:**
  * Diseno desacoplado donde el motor de dominio y la capa de persistencia se comunican exclusivamente mediante abstracciones e interfaces estandar (JPA, JDBC, Spring Security).
  * Ningun modulo depende de APIs propietarias de proveedores de hosting o de base de datos, garantizando portabilidad absoluta entre entornos locales (Docker Compose), bases de datos serverless (Neon) o nubes empresariales (AWS, Render, Railway).
  * Desacoplamiento total de dependencias legacy: sustitucion del antiguo `externals-5.1.jar` por la clase autonoma `ExternalLDAP` con modo silencioso para ejecucion en contenedores.
* **Suite Automatizada de 180 Tests (100% Cobertura de Integracion, Dominio y Politicas de Seguridad):**
  * 59 tests en `core` y 121 tests en `backend` (incluyendo integracion con MockMvc, seguridad JWT, mitigacion de fuerza bruta y politicas de nombres de usuario).

---

## 2. Documentacion Tecnica y Modelado

* [modelado/ARQUITECTURA_Y_DISENO.md](modelado/ARQUITECTURA_Y_DISENO.md): Diagramas de componentes, arquitectura enterprise Spring Boot 3, arquitectura web frontend, diagrama de clases, diagrama entidad-relacion (ERD), catalogo de endpoints REST y topologia DevOps.
* [pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md](pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md): Matriz de trazabilidad y reporte de los 180 tests automatizados (JUnit 5, Mockito, Spring Security Test, MockMvc) y verificacion de compilacion frontend.
* [AGENTS.md](AGENTS.md): Hoja de ruta (Roadmap), Definition of Done (DoD) y registro de hitos del proyecto.
* `legacy/`: Archivo historico de artefactos academicos de la universidad (diagramas originales, pruebas iniciales y PDFs) preservados conforme a las normas de ingenieria.

---

## 3. Pila Tecnologica

* **Backend & Dominio:** Java 17 / 22, Spring Boot 3.2.5 (Spring MVC, Spring Data JPA, Spring Security 6, Spring Validation)
* **Frontend Web:** Next.js 16.4 (App Router, Turbopack), React 19, TypeScript 5, Tailwind CSS v4, Lucide Icons, Sonner, Canvas Confetti
* **Audio y FX:** Web Audio API (sintetizador oscilador nativo sin assets externos)
* **Documentacion API:** SpringDoc OpenAPI 2.5.0 / Swagger UI 5.13.0
* **Seguridad & Tokens:** JJWT 0.12.5, BCrypt, Spring Mail (JavaMailSender / Gmail SMTP `fleetgrid.command@gmail.com`)
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
Para compilar y verificar los 180 tests unitarios y de integracion:

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

### 6.2 Backend REST API (Render) y Monitor Keep-Alive con UptimeRobot
1. Accede a [Render](https://render.com) e inicia sesion con GitHub.
2. Crea un nuevo **Web Service**, vincula este repositorio y selecciona:
   * **Runtime:** Docker (utilizara `backend/Dockerfile`).
   * **Root Directory:** `.` (raiz del proyecto).
   * **Plan:** Free.
3. En la seccion de variables de entorno (**Environment Variables**), configura:
   * `SPRING_PROFILES_ACTIVE`: `prod`
   * `SPRING_DATASOURCE_URL`: *(Cadena de conexion copiada de Neon)*
   * `JWT_SECRET`: *(Cadena secreta aleatoria de al menos 64 caracteres en hexadecimal, o la definida en `.env.example`)*
   * `SPRING_MAIL_HOST`: `smtp.gmail.com`
   * `SPRING_MAIL_PORT`: `587`
   * `SPRING_MAIL_USERNAME`: `fleetgrid.command@gmail.com`
   * `SPRING_MAIL_PASSWORD`: *(Contrasena de aplicacion de 16 caracteres generada en Google)*
   * `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH`: `true`
   * `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE`: `true`
4. Pulsa **Create Web Service**. Render compila la imagen multi-stage y publica la URL HTTPS: `https://battleship-backend-ys61.onrender.com`.
5. **Configuracion de Keep-Alive y Gestion de Cuota (750h/mes):**
   * Render Free proporciona 750 horas de computo al mes compartidas entre todos los servicios de la cuenta. Un servicio 24/7 consume ~744 horas.
   * Para mantener el backend disponible sin que se duerma tras 15 minutos, pero **pudiendo pausarlo en 1 clic** si se despliegan otros proyectos:
     1. Registrate gratis en [UptimeRobot](https://uptimerobot.com).
     2. Pulsa en **Add New Monitor**.
     3. **Monitor Type:** `HTTP(s)`.
     4. **Friendly Name:** `Battleship Backend (Render)`.
     5. **URL:** `https://battleship-backend-ys61.onrender.com/healthz`.
     6. **Monitoring Interval:** Cada `10 minutos` o `12 minutos`.
     7. **Pausa Inteligente de Cuota:** En el panel de UptimeRobot existe un boton de alternancia (*Pause / Resume*). Con un solo clic se pausa el sondeo, permitiendo que Render se duerma tras 15 minutos de inactividad para no consumir horas cuando se trabaje en otros proyectos. *(Nota: La Status Page publica es opcional y puede eliminarse si no se desea mostrar dicho panel publico; lo indispensable para mantener activo el backend es el Monitor de sondeo en si)*.

### 6.3 Frontend Web y Server Warm-Up UX (Vercel)
1. Accede a [Vercel](https://vercel.com) e inicia sesion con GitHub.
2. Importa el repositorio `Battleship_IWSIM21-07`.
3. En la configuracion del proyecto:
   * **Root Directory:** `frontend`
   * **Framework Preset:** Next.js
4. En **Environment Variables**, anade:
   * `NEXT_PUBLIC_API_URL`: `https://battleship-backend-ys61.onrender.com/api/v1`
5. Pulsa **Deploy**. Vercel compila la aplicacion en su red Anycast Edge CDN y genera el enlace publico: [https://battleship-iwsim-21-07.vercel.app/](https://battleship-iwsim-21-07.vercel.app/).
6. El frontend incluye telemetria de deteccion temprana: si el backend estuviese en reposo tras una pausa del monitor, muestra una animacion tactica de arranque del reactor naval mientras Render se reactiva en ~45 segundos.

### 6.4 Rol de Cloudflare (Opcional)
Cloudflare actua como proxy inverso, cortafuegos de aplicaciones web (WAF) y red de distribucion DNS. Su uso es altamente recomendado si se desea vincular un **dominio personalizado propio** (por ejemplo `battleship.dev` o `batallanaval.es`). Para los subdominios gratuitos (`*.vercel.app` y `*.onrender.com`), tanto Vercel como Render ya integran sus propios certificados SSL automaticos y redes CDN Anycast, por lo que Cloudflare no es obligatorio en la fase inicial.

---

## 7. Pipeline de Integracion Continua (GitHub Actions)

El archivo `.github/workflows/ci.yml` ejecuta una suite automatizada de verificacion de calidad en cada `push` o `pull request` dirigido a la rama `main`:
* **Job Backend:** Descarga JDK 17 Temurin, cachea dependencias Maven y ejecuta los 180 tests unitarios y de integracion (`BUILD SUCCESS`).
* **Job Frontend:** Descarga Node 20, verifica tipos estricto con TypeScript (`tsc --noEmit`) y compila la aplicacion Next.js con Turbopack.

---

## 8. Acceso al Sistema y Modo Invitado

La plataforma permite el juego inmediato sin barreras de entrada mediante el **Modo Invitado**, ofreciendo la opcion de reclamar los puntos acumulados al concluir la batalla mediante registro con correo o inicio de sesion con verificacion de correo mediante codigo OTP.

