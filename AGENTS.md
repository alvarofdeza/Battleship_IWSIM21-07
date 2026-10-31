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
   * Cobertura de pruebas unitarias y de integración para toda la lógica implementada.
   * La suite de tests de Maven debe ejecutarse sin un solo fallo ni error (`BUILD SUCCESS`).
   * No se permiten dependencias de tests basadas en trucos de reflexión frágiles ni estados sucios entre ejecuciones.

4. **Higiene de Repositorio y Auditoria de .gitignore:**
   * Al inicio y al cierre de cada hito se debe auditar exhaustivamente el archivo `.gitignore`.
   * Los archivos binarios generados (bases de datos locales `.mv.db`, `.trace.db`, clases compiladas `.class`, empaquetados `.jar` y carpetas `target/`) deben permanecer permanentemente excluidos del control de versiones.

5. **Preservacion Segura de Artefactos Legacy:**
   * Las carpetas y archivos heredados de la universidad (`modelado/` y `pruebas/`) no se eliminaran hasta que exista su version equivalente y superada en formato Markdown/Mermaid en la nueva documentacion.

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
  - [x] Modulo `battleship-server` configurado con Spring Boot 3.2.5 BOM y Java 17.
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
- [x] **Suite de Pruebas de Integracion MockMvc:**
  - [x] 16 tests automatizados en `battleship-server` cubriendo flujos de autenticacion, reglas de combate, niebla de guerra, disparos duplicados, limites y clasificaciones.
  - [x] Total de 72 tests automatizados pasando al 100% en todo el repositorio.

---

### [PENDIENTE] Hito 3: Multijugador Real-Time con WebSockets
*Objetivo:* Permitir partidas en tiempo real entre dos jugadores remotos.
- [ ] Servidor WebSocket con protocolo STOMP / SockJS.
- [ ] Gestion de salas de juego (Game Rooms) y Matchmaking.
- [ ] Sincronizacion de eventos de disparo, turnos y fin de juego en milisegundos.
- [ ] Manejo de desconexiones y reconexiones.

---

### [PENDIENTE] Hito 4: Frontend Full-Stack Moderno (Web UI)
*Objetivo:* Crear una interfaz web visual, reactiva e interactiva.
- [ ] SPA en React / Next.js con Tailwind CSS.
- [ ] Colocacion de barcos con Drag & Drop y rotacion.
- [ ] Tablero de combate interactivo con animaciones de radar, agua y explosiones.
- [ ] Modo Solitario (vs IA Facil / Normal / Experta) y Modo Multijugador en vivo.
- [ ] Historial de partidas y Leaderboard interactivo.

---

### [PENDIENTE] Hito 5: DevOps, Despliegue Cloud & Presentacion
*Objetivo:* Publicar la plataforma con Live Demo publica y calidad para portfolio.
- [ ] Dockerfile multi-stage y `docker-compose.yml` para levantar la aplicacion completa localmente.
- [ ] Pipeline CI/CD con GitHub Actions (compilacion, tests, analisis estatico).
- [ ] Despliegue del Frontend en Vercel.
- [ ] Despliegue del Backend + PostgreSQL en Render / Railway.
- [ ] README.md final de nivel profesional con arquitectura C4/Mermaid, insignias tecnicas y enlace Live Demo.
