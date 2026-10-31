# Battleship Platform - Engineering & Agent Roadmap

Este documento define la arquitectura, normas de desarrollo, Definition of Done (DoD) y la hoja de ruta viva (Roadmap) de hitos del proyecto Battleship.

---

## 1. Normas de Desarrollo y Definition of Done (DoD)

Para dar por finalizado cualquier hito, se deben cumplir estrictamente las siguientes condiciones:

1. **Documentación Exhaustiva y Profesional (Sin Emojis):**
   * Todo componente, módulo y regla de negocio debe estar debidamente documentado.
   * Queda expresamente prohibido el uso de emojis en cualquier archivo de documentación técnica o código.
   * La documentación debe redactarse en espanol tecnico impecable.
   * La arquitectura debe estar representada mediante diagramas modernos en formato Mermaid.
   * Cuando existan APIs HTTP, deben documentarse interactivamente con OpenAPI / Swagger UI.

2. **Politica Estricta de Idiomas:**
   * **Codigo fuente 100% en ingles:** Nombres de clases, interfaces, métodos, variables, atributos, paquetes, nombres de carpetas y mensajes o salidas por consola.
   * **Documentacion y JavaDoc 100% en espanol:** Especificaciones técnicas en Markdown, bloques JavaDoc en clases e interfaces, y comentarios explicativos de algoritmos.

3. **Suite de Tests Automatizada al 100%:**
   * Cobertura de pruebas unitarias y de integración para toda la lógica implementada (78 tests automatizados: 58 en core y 20 en backend).
   * La suite de tests de Maven debe ejecutarse sin un solo fallo ni error (`BUILD SUCCESS`).
   * No se permiten dependencias de tests basadas en trucos de reflexión frágiles ni estados sucios entre ejecuciones.

4. **Higiene de Repositorio y Auditoria de .gitignore:**
   * Al inicio y al cierre de cada hito se debe auditar exhaustivamente el archivo `.gitignore`.
   * Los archivos binarios generados (bases de datos locales `.mv.db`, `.trace.db`, clases compiladas `.class`, empaquetados `.jar` y carpetas `target/`) deben permanecer permanentemente excluidos del control de versiones.

5. **Preservacion Segura de Artefactos Legacy:**
   * Las carpetas y archivos heredados de la universidad (`modelado/` y `pruebas/`) no se eliminaran hasta que exista su version equivalente y superada en formato Markdown/Mermaid en la nueva documentacion.

6. **Principios SOLID, Clean Code y Desacoplamiento de Proveedores (Vendor Lock-in Immunity):**
   * El dominio debe permanecer agnostico a frameworks de infraestructura y a proveedores cloud concretos.
   * Se debe cumplir estrictamente el principio de responsabilidad unica (SRP) e inversion de dependencias (DIP), asegurando que la base de datos se consuma como PostgreSQL estandar mediante interfaces JDBC / Spring Data JPA, sin acoplar la logica de negocio a SDKs o servicios propietarios de terceros.
   * Toda decision arquitectonica debe priorizar la mantenibilidad a largo plazo, la alta cohesion, el bajo acoplamiento y la total portabilidad entre entornos de ejecucion.

7. **Control Estricto de Despliegue Git (Push Exclusivamente Manual por el Desarrollador):**
   * Queda terminantemente prohibido que el asistente AI ejecute comandos `git push` o `git push origin main`.
   * En el entorno de desarrollo conviven multiples identidades y credenciales de Git que requieren seleccion y autorizacion manual interactiva por parte del usuario.
   * La labor del agente se limita a la preparacion local de commits (staging, generacion de commit semantico descriptivo y comprobacion de estado limpio en `git status`).
   * La orden de subida a ramas remotas (`git push origin main`) debe facilitarse siempre en texto plano al desarrollador para su ejecucion manual soberana.

---

## 2. Master Roadmap de Hitos

### [COMPLETADO] Hito 1: Motor de Dominio Independiente, Limpieza y Refactorizacion (battleship-core)
*Objetivo:* Eliminar la dependencia de los JAR docentes (`Battleship-1.11.jar`), construir un motor de juego puro en Java 17/22 con Clean Architecture, algoritmos de IA (Basic, Hunt & Target, Heatmap probabilistico x50), refactorizacion 100% al ingles y tests unitarios automatizados.

- [x] **Ingenieria Inversa y Desacoplamiento:** Extraccion de contratos e interfaces del JAR docente y sustitucion total de dependencias obsoletas.
- [x] **Motor de Dominio Core (Clean Architecture):** Tablero 10x10, validacion de impactos, naves especializadas, prevencion de colisiones y niebla de guerra.
- [x] **Motor de Inteligencia Artificial (Strategy Pattern):** Niveles Facil (aleatorio), Normal (paridad de ajedrez y caza ortogonal) y Experto (superposicion probabilistica matricial).
- [x] **Estandarizacion Linguistica y Build:** Codigo 100% en ingles, JavaDoc en espanol, Maven Wrapper y 56 tests unitarios en verde.

---

### [COMPLETADO] Hito 2: Backend Enterprise con Spring Boot 3 & REST API
*Objetivo:* Transformar la aplicacion monolitica en un backend desacoplado, modular y moderno con Spring Boot 3, Spring Data JPA, JWT y documentacion interactiva OpenAPI.

- [x] **Arquitectura Multi-Modulo Maven:** Modulo `battleship-backend` configurado con Spring Boot 3.2.5 BOM y Java 17 aislando las dependencias legacy.
- [x] **Autenticacion y Seguridad Moderna (Spring Security 6 + JWT):** Seguridad stateless basada en tokens Bearer JWT firmados con HMAC-SHA256, `BCryptPasswordEncoder` y politica CORS para Next.js en Vercel.
- [x] **Persistencia Empresarial (Spring Data JPA):** Entidades relacionales (`UserEntity`, `GameRecordEntity`, `MoveRecordEntity`, `LeaderboardScoreEntity`) con PostgreSQL y H2.
- [x] **Controladores REST y Manejador Global de Errores:** Endpoints de autenticacion (`/api/v1/auth`), combate vs IA (`/api/v1/games`) y clasificaciones (`/api/v1/leaderboard`).
- [x] **Documentacion Interactiva OpenAPI 3 / Swagger UI:** Interfaz Swagger disponible en `/swagger-ui.html` y esquema JSON en `/v3/api-docs`.
- [x] **Suite de Pruebas de Integracion MockMvc:** Cobertura de flujos de combate, niebla de guerra, seguridad y persistencia.

---

### [COMPLETADO] Hito 3: Frontend Full-Stack Moderno (Web UI con Next.js & Tailwind CSS)
*Objetivo:* Crear una interfaz web visual de vanguardia, totalmente reactiva, interactiva y responsive, conectada a la API REST de Spring Boot.

- [x] **Aplicacion Next.js 16 (App Router, Turbopack, React 19, Tailwind CSS v4, Lucide Icons).**
- [x] **Diseno UI/UX Tactico Naval:** Estetica militar retro CRT (Combat Information Center) con paleta US Navy y Share Tech Mono.
- [x] **Dique Seco Interactivo:** Colocacion de flota con rotacion por atajos de teclado (`R` / Espacio), deteccion de colisiones y auto-despliegue aleatorio.
- [x] **Tablero de Combate Reactivo:** Niebla de guerra, animaciones de radar, telemetria teletipo e inspector de heatmap en tiempo real.
- [x] **Sintetizador de Audio Nativo:** Web Audio API con osciladores de sonido militar sin archivos externos (pings de sonar, detonaciones, agua, fanfarrias).
- [x] **Escalafon de Oficiales (Admiralty Roll):** Visualizacion de records mundiales y personales.

---

### [COMPLETADO] Hito 4: DevOps, Docker & Despliegue Cloud en Vivo
*Objetivo:* Publicar la plataforma completa en internet con Live Demo publica de maximo impacto para portfolio.

- [x] **Contenedores Docker Multi-Stage:** Empaquetado ligero de Spring Boot (Temurin 17 JRE Alpine) y Next.js 16 (Standalone Node 20 Alpine) con usuarios no privilegiados.
- [x] **Cluster Local Docker Compose:** Orquestacion de PostgreSQL 16, backend y frontend con healthcheck y volumenes persistentes.
- [x] **Desacoplamiento Total Legacy:** Sustitucion de `externals-5.1.jar` por `ExternalLDAP.java` autonomo con deteccion headless y suite unitaria.
- [x] **Endpoint de Salud Operativo (`/healthz`):** `HealthController` con pase en `SecurityConfig` para orquestadores y pings keep-alive.
- [x] **Infraestructura Cloud Permanente Gratuita:** Base de datos en Neon PostgreSQL (Frankfurt), backend en Render y frontend en Vercel Edge CDN.
- [x] **Pipeline CI/CD (GitHub Actions):** Validacion de 78 tests de Maven y build estricto de Next.js en cada push a `main`.
- [x] **Gestion de Cuota de Computo (750h/mes):** Monitor keep-alive en UptimeRobot (`/healthz`) con conmutador de pausa de un solo clic.

---

### [EN CURSO] Hito 5: Gamificacion Tactica, Doble Ranking, Modo Invitado, Verificacion OTP y Perfil de Oficial
*Objetivo:* Refinar la experiencia de usuario, permitir juego inmediato sin barreras de entrada (Modo Invitado con reclamacion post-combate), verificacion de correo mediante codigo PIN de 6 digitos (API Resend), sistema de puntuacion multidimensional (victoria y consolacion en derrota) y doble ranking en Admiralty Roll con perfil de oficial.

- [x] **Fase 1: Motor de Puntuacion Multidimensional y Endpoints de Doble Ranking en Backend:**
  - [x] Implementacion de la formula de victoria: `((1000 + bonus_shots) * mult_diff) + bonus_time`.
  - [x] Sistema de puntos de consolacion en derrotas completas por barcos enemigos hundidos (Carrier 250, Battleship 200, Submarine 150, PatrolBoat 100) multiplicado por dificultad.
  - [x] Blindaje anti-farming: abandonos computan con 0 puntos; los puntos de consolacion exigen resolucion completa del combate.
  - [x] Ampliacion del modelo de datos con `difficulty`, `durationSeconds`, `movesCount`, `result` (`VICTORY`/`DEFEAT`) y `points`.
  - [x] Endpoints de doble ranking en `LeaderboardController`:
    - `GET /api/v1/leaderboard/matches`: Top 10 mejores partidas individuales.
    - `GET /api/v1/leaderboard/players`: Top 10 jugadores ordenados por suma de puntos acumulados (`SUM(points)`).
  - [x] Endpoints de expediente personal:
    - `GET /api/v1/leaderboard/me/matches`: Top 10 mejores partidas del usuario logueado.
    - `GET /api/v1/leaderboard/me/recent`: Ultimas 10 partidas cronologicas.
    - `GET /api/v1/leaderboard/me/stats`: Puntos acumulados, total de victorias, derrotas y calculo de rango naval.
  - [x] Endpoint de gestion de credenciales en `AuthController`: `PUT /api/v1/auth/password` con comprobacion de contrasena actual y validacion BCrypt.

- [x] **Fase 2: Modo Invitado (Guest Mode) & Reclamacion Post-Combate:**
  - [x] Eliminacion del bloque de credenciales de prueba en `AuthModal`.
  - [x] Generacion de partidas de invitado con identificador efimero en memoria sin persistir en base de datos.
  - [x] Pantalla de fin de partida militar con desglose de puntos y boton de reclamacion: *"Reclamar Puntos e Inscribirse"*.
  - [x] Endpoint `POST /api/v1/games/{id}/claim` para transferir la partida jugada como invitado a la cuenta del usuario tras login o registro.

- [x] **Fase 3: Verificacion de Correo con Codigo OTP de 6 Digitos (Resend API):**
  - [x] Integracion con la API de Resend para envio de correos transaccionales con plantilla tactica naval.
  - [x] Generacion y validacion de codigos OTP de 6 digitos con expiracion a los 10 minutos.
  - [x] Campo `email_verified` en `UserEntity` y endpoint de verificacion `POST /api/v1/auth/verify-email`.
  - [x] Componente modal en el frontend con 6 casillas para introduccion del codigo PIN militar.

- [x] **Fase 4: Interfaz Web: Doble Ranking en Admiralty Roll & Expediente de Oficial (Profile):**
  - [x] Conmutador en Admiralty Roll: *"Top Partidas"* vs *"Top Oficiales"* con insignias `VICTORY` y `DEFEAT`.
  - [x] Nueva vista/pestana `Profile` (Expediente de Oficial):
    - Rango militar naval por puntos (Cadete, Alferez, Teniente, Capitan, Almirante de la Flota).
    - Estadisticas globales de carrera (Puntos totales, Victorias, Derrotas, Ratio de acierto).
    - Tablas de Top 10 personal y Ultimas 10 partidas.
    - Formulario de cambio de contrasena.
  - [x] Visualizacion del rango y puntuacion total acumulada en la barra superior de navegacion.

- [ ] **Fase 5: Saneamiento de Base de Datos en Neon, Suite de Tests y Despliegue:**
  - [x] Sembrado de record de honor de referencia para `alvaro` y aislamiento en base de datos.
  - [x] Suite de pruebas automatizada ampliada cubriendo los nuevos endpoints y calculos matematicos (91 tests passing).
  - [ ] Despliegue en produccion en Render y Vercel (tras push manual del desarrollador).

---

### [PENDIENTE] Hito 6: Multijugador Real-Time con WebSockets (Extension Avanzada)
*Objetivo:* Permitir partidas en tiempo real entre dos jugadores remotos conectando la interfaz web ya operativa.
- [ ] Servidor WebSocket con protocolo STOMP / SockJS en Spring Boot.
- [ ] Gestion de salas de juego (Game Rooms) con codigos de invitacion para compartir con amigos.
- [ ] Sincronizacion bidireccional en milisegundos de disparos, cambios de turno y fin de partida.
- [ ] Manejo de desconexiones y reconexiones con tiempo de gracia.
