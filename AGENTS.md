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

### [COMPLETADO] Hito 1: Motor de Dominio Independiente e Inteligencia Artificial
*Objetivo:* Eliminar la dependencia de los JAR docentes (`Battleship-1.11.jar`) y construir un motor de juego puro en Java moderno con Clean Architecture, algoritmos de IA y tests unitarios al 100%.

- [x] **Ingenieria Inversa:** Analisis y extraccion de contratos e interfaces del JAR docente.
- [x] **Limpieza de Build (Maven):**
  - [x] Integracion de Maven Wrapper (`mvnw` / `mvnw.cmd`) con Maven 3.9.11 para compilar sin dependencias de entorno.
  - [x] Eliminacion de dependencias obsoletas de JUnit 3.8.1 y 4.13.2 de `pom.xml`.
  - [x] Integracion de JUnit 5 Jupiter (5.10.2), Mockito (5.11.0) y AssertJ (3.25.3).
  - [x] Eliminacion total de la dependencia `etsisi:Battleship:1.11`.
  - [x] Exclusion de `*.mv.db` y `*.trace.db` de Git en `.gitignore`.
- [x] **Motor de Dominio Core (Java 17/22 Puro, Clean Architecture):**
  - [x] Casillas con estados de impacto y contenido.
  - [x] Jerarquia de naves con capacidades tacticas especiales.
  - [x] Tablero de juego de 10x10 con validacion de limites, prevencion de solapamientos, despliegue aleatorio y niebla de guerra.
  - [x] Maquina de estados de la partida con control de turnos, registro de movimientos y calculo de puntuacion.
- [x] **Motor de Inteligencia Artificial (Strategy Pattern):**
  - [x] Nivel Facil: Disparos aleatorios sobre casillas inexploradas.
  - [x] Nivel Normal (Hunt & Target): Modo caza con paridad de ajedrez y modo objetivo con exploracion ortogonal y continuidad de eje.
  - [x] Nivel Experto (Probability Density Heatmap): Superposicion de permutaciones posibles de barcos vivos sobre la cuadricula de 10x10 con ponderacion x50 en vecindades de impactos pendientes.

---

### [COMPLETADO] Hito 1.5: Estandarizacion Linguistica, Refactorizacion en Ingles y Configuracion de Ejecucion
*Objetivo:* Adaptar todo el codigo fuente a estandares internacionales de ingenieria (100% ingles), documentacion JavaDoc en espanol, simplificacion de ejecucion Maven y supresion total de emojis.

- [x] **Renombrado de Directorios:** Reestructuracion del directorio `construccion/` a `core/`.
- [x] **Configuracion Maven Multi-Modulo:**
  - [x] Creacion del `pom.xml` padre en la raiz del proyecto para compilar y testear con `.\mvnw.cmd test` directamente desde el raiz.
  - [x] Configuracion de `exec-maven-plugin` con clase principal `es.upm.etsisi.fis.App` para ejecucion limpia con `.\mvnw.cmd -pl core compile exec:java`.
- [x] **Refactorizacion Completa a Ingles:**
  - [x] Dominio: `Board`, `Cell`, `Ship`, `Carrier`, `Battleship`, `Submarine`, `PatrolBoat`, `Game`, `IPlayer`, `AiPlayer`, `AiPlayerFactory`, `Movement`, `Score`.
  - [x] Algoritmos: `AiAlgorithm`, `BasicAlgorithm`, `NormalAlgorithm`, `ExpertAlgorithm`.
  - [x] Controladores y Vistas: `GameController`, `GameView`, `BaseView`, `AuthView`, `UsersView`.
  - [x] Salidas de consola y textos de usuario traducidos a ingles (`"Miss"`, `"Hit"`, `"Sunk"`, `"Victory for ..."`).
- [x] **Documentacion JavaDoc en Espanol:** Cobertura de documentacion JavaDoc en todas las clases, interfaces y metodos del dominio.
- [x] **Suite de Tests Actualizada:** 56 tests unitarios pasando en verde al 100% en menos de 4 segundos.
- [x] **Supresion de Emojis:** Eliminacion estricta de emojis en todos los documentos tecnicos del repositorio.
- [x] **Auditoria de Git:** Verificacion de `.gitignore` para evitar tracking de archivos binarios de bases de datos.

---

### [COMPLETADO] Hito 2: Backend Enterprise con Spring Boot 3 & REST API
*Objetivo:* Transformar la aplicacion monolitica en un backend desacoplado, modular y moderno con Spring Boot 3, Spring Data JPA, JWT y documentacion interactiva OpenAPI.

- [x] **Arquitectura Multi-Modulo Maven:**
  - [x] Modulo `battleship-backend` (directorio `backend/`) configurado con Spring Boot 3.2.5 BOM y Java 17.
  - [x] Integracion limpia con `battleship-core` aislando las dependencias legacy de Hibernate 5 para evitar conflictos de classpath.
  - [x] Configuracion de perfiles `application.yml` (H2 en memoria con consola activa) y `application-prod.yml` (PostgreSQL).
- [x] **Autenticacion y Seguridad Moderna (Spring Security 6 + JWT):**
  - [x] Sustitucion definitiva del mock LDAP de la universidad por seguridad stateless basada en tokens Bearer JWT firmados con HMAC-SHA256.
  - [x] Implementacion de `JwtService`, `JwtAuthenticationFilter`, `UserDetailsServiceImpl` y cifrado de contraseñas mediante `BCryptPasswordEncoder`.
  - [x] Politica CORS abierta para integracion fluida con clientes SPA (React / Next.js) y despliegues en Vercel.
- [x] **Persistencia Empresarial (Spring Data JPA):**
  - [x] Entidades de dominio mapeadas a tablas relacionales: `UserEntity`, `GameRecordEntity`, `MoveRecordEntity`, `LeaderboardScoreEntity`.
  - [x] Repositorios desacoplados con metodos de consulta especificos (`UserRepository`, `GameRecordRepository`, `MoveRecordRepository`, `LeaderboardScoreRepository`).
  - [x] Sembrado automatico de usuarios iniciales en arranque (`admin`, `alvaro`, `adrian`).
- [x] **Capa de Controladores REST y Manejador Global de Errores:**
  - [x] `AuthController`: Registro (`/register`), Login (`/login`) y Perfil del usuario autenticado (`/me`).
  - [x] `GameController`: Creacion de partida vs IA (`/games`), consulta de estado con niebla de guerra (`/games/{id}`), disparo humano con contraataque sincronizado de la IA (`/games/{id}/shots`), e historial de ataques (`/games/{id}/history`).
  - [x] `LeaderboardController`: Top 10 personal (`/leaderboard/personal`) y ranking mundial (`/leaderboard/global`).
  - [x] `GlobalExceptionHandler` con estandarizacion de errores HTTP 400 Bad Request, 401 Unauthorized, 403 Forbidden y 404 Not Found (`ResourceNotFoundException`).
- [x] **Documentacion Interactiva OpenAPI 3 / Swagger UI:**
  - [x] Integracion de `springdoc-openapi-starter-webmvc-ui` 2.5.0.
  - [x] Configuracion de esquema de seguridad Bearer JWT en Swagger (`OpenApiConfig`).
  - [x] Swagger UI disponible en `/swagger-ui.html` y esquema JSON en `/v3/api-docs`.
- [x] **Suite de Pruebas de Integracion MockMvc y Configuracion:**
  - [x] 20 tests automatizados en `battleship-backend` cubriendo flujos de autenticacion, reglas de combate, niebla de guerra, disparos duplicados, limites, clasificaciones, health check (`/healthz`) y resiliencia de conexion cloud.
  - [x] Total de 78 tests automatizados pasando al 100% en todo el repositorio.

---

### [COMPLETADO] Hito 3: Frontend Full-Stack Moderno (Web UI con Next.js & Tailwind CSS)
*Objetivo:* Crear una interfaz web visual de vanguardia, totalmente reactiva, interactiva y responsive, conectada a la API REST de Spring Boot.
- [x] Inicializacion de aplicacion frontend en Next.js (App Router, TypeScript, Tailwind CSS v4, Lucide Icons).
- [x] Diseno UI/UX tactico militar con estetica Dark Mode pulida (paleta naval US Navy, tipografia pixelada Press Start 2P y monoespaciada Share Tech Mono).
- [x] Gestion de estado global y sesion con almacenamiento seguro de tokens JWT Bearer (`ApiClient`).
- [x] Modulo interactivo de despliegue de flota: colocacion de barcos con deteccion de colisiones, rotacion (tecla R / Space) y auto-despliegue aleatorio.
- [x] Guardia de autenticacion previa a combate (HTTP 403 preventivo): si el usuario no ha iniciado sesion al desplegar flota, emite advertencia de rango en ingles (`"OFFICER CLEARANCE REQUIRED: PLEASE SIGN IN TO TRANSMIT FLEET ORDERS"`), activa alarma sonora y despliega el modal de autorizacion.
- [x] Tablero de combate interactivo con niebla de guerra, animaciones de barrido de radar, efectos de agua en fallos y explosiones en impactos/hundimientos.
- [x] Selector de dificultad para enfrentarse a la Inteligencia Artificial (Facil, Normal, Experta con algoritmo de densidad probabilistica) e inspector de telemetria heatmap.
- [x] Pantalla de clasificaciones y records mundiales consumiendo el endpoint de Leaderboard (Admiralty Roll).
- [x] Optimizacion responsive integral para ordenadores de sobremesa, tablets y dispositivos moviles con conmutador tactico de pestanas.
- [x] Sintetizador nativo de audio Web Audio API con efectos navales discretos y control de silenciado (Mute).
- [x] Documentacion TSDoc exhaustiva en espanol en todos los componentes y utilidades del frontend (`frontend/src/`).
- [x] Estandarizacion de estructura de carpetas limpia (`core/`, `backend/`, `frontend/`, `legacy/`).
- [x] Preservacion segura de artefactos academicos legacy en `legacy/modelado/` y `legacy/pruebas/`.

---

### [EN CURSO] Hito 4: DevOps, Docker & Despliegue Cloud en Vivo
*Objetivo:* Publicar la plataforma completa en internet con Live Demo publica de maximo impacto para portfolio.
- [x] Dockerfile multi-stage optimizado para empaquetar el backend Spring Boot con Eclipse Temurin JDK/JRE 17 Alpine y usuario no privilegiado (`backend/Dockerfile`).
- [x] Dockerfile multi-stage para el frontend Next.js 16 con empaquetado standalone ultra-ligero y usuario no privilegiado (`frontend/Dockerfile`).
- [x] Archivo `docker-compose.yml` multi-servicio (Compose Spec v2) para levantar localmente el frontend, backend y PostgreSQL 16 con healthcheck y volumen persistente.
- [x] Plantilla `.env.example` y archivos `.dockerignore` exhaustivos para aislamiento de entornos de desarrollo y produccion.
- [x] Configuracion de resiliencia de conexion cloud en `DatabaseConfig.java` compatible con PostgreSQL en Neon, Render, Railway y Docker.
- [x] Desacoplamiento total de dependencias legacy: sustitucion del JAR `externals-5.1.jar` por la clase autonoma `ExternalLDAP.java` con deteccion headless y suite unitaria `ExternalLDAPTest`.
- [x] Endpoint de salud operativo `/healthz` (`HealthController`) con pase libre en `SecurityConfig` e integracion probada en `HealthControllerIntegrationTest`.
- [x] Arquitectura de despliegue cloud permanente y sin coste: base de datos perpetua en Neon (sin caducidad de 30 dias), backend en Render con keep-alive ping (UptimeRobot) y frontend en Vercel con telemetria warm-up.
- [x] Archivo de infraestructura como codigo `render.yaml` para despliegue automatizado con Docker en Render.
- [x] Despliegue del backend en Render finalizado con exito y enlazado a Neon en Frankfurt (`eu-central-1`).
- [x] Pipeline de integracion continua (CI/CD) con GitHub Actions (`.github/workflows/ci.yml`) que valida los 78 tests de Maven y compila el frontend en cada commit a `main`.
- [x] README.md profesional con arquitectura de despliegue en Mermaid, insignias tecnicas (CI, Docker, Next.js, Spring Boot, PostgreSQL) y guia de ejecucion paso a paso.
- [ ] Despliegue del frontend en Vercel conectando `NEXT_PUBLIC_API_URL` a la URL publica HTTPS de Render.
- [ ] Configuracion de monitor keep-alive en UptimeRobot enviando peticiones cada 12 minutos a `/healthz`.

---

### [PENDIENTE] Hito 5: Multijugador Real-Time con WebSockets (Extension Avanzada)
*Objetivo:* Permitir partidas en tiempo real entre dos jugadores remotos conectando la interfaz web ya operativa.
- [ ] Servidor WebSocket con protocolo STOMP / SockJS en Spring Boot.
- [ ] Gestion de salas de juego (Game Rooms) con codigos de invitacion para compartir con amigos.
- [ ] Sincronizacion bidireccional en milisegundos de disparos, cambios de turno y fin de partida.
- [ ] Manejo de desconexiones y reconexiones con tiempo de gracia.
