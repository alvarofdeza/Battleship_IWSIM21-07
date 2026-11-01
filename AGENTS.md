# FleetGrid Platform - Engineering & Agent Roadmap

> [!NOTE]
> **Trazabilidad y Origen Academico:** FleetGrid es la evolucion tecnologica y maduracion profesional del proyecto de software universitario original `Battleship_IWSIM21-07` (UPM - ETSISI).

Este documento define la arquitectura, normas de desarrollo, Definition of Done (DoD) y la hoja de ruta viva (Roadmap) de hitos de la plataforma FleetGrid.

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
   * Cobertura de pruebas unitarias y de integracion para toda la logica implementada (180 tests automatizados: 59 en core y 121 en backend).
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

8. **Auditoria Continua de Casos Limite, Resiliencia y Blindaje de Ciberseguridad (Defensive Engineering):**
   * En cada iteracion o modificacion de codigo, el agente debe anticipar y buscar proactivamente casos limite (edge cases), condiciones de carrera, entradas malformadas, colisiones de eventos de teclado/UI y posibles vectores de vulnerabilidad (OWASP Top 10, CWE).
   * **Tolerancia Cero a la Exposicion de Secretos:** Queda terminantemente prohibido exponer, hardcodear o subir al repositorio claves de API, secretos JWT, credenciales de base de datos, credenciales SMTP o cadenas de conexion con contrasenas reales. Todo secreto debe residir exclusivamente en variables de entorno o archivos `.env` ignorados por `.gitignore`.
   * Se deben auditar y mitigar activamente: ataques de fuerza bruta en codigos de verificacion (bloqueo transaccional por umbral de intentos fallidos), vectores de denegacion de servicio algoritmico (DoS por hashing de contrasenas infinitas limitando longitudes maximas a 50 caracteres), inyecciones XSS / SQL mediante validacion estricta de esquemas (`@Pattern`, `@Size`), manipulacion de tokens y duplicidades transaccionales (proteccion contra replay en reclamaciones de partidas mediante comprobaciones en BD y memoria).
   * Ninguna excepcion no controlada debe fugarse al cliente; todas las anomalias deben ser interceptadas y estandarizadas en manejadores globales con codigos HTTP y mensajes semanticos controlados.
   * Toda nueva restriccion o blindaje debe contar obligatoriamente con su correspondiente test automatizado de integracion o unidad.

---

## 2. Master Roadmap de Hitos

### [COMPLETADO] Fundamentos y Evolucion Arquitectonica (Hitos 1 a 4)
*Resumen de construccion del nucleo, backend enterprise, interfaz tactica y DevOps cloud:*

- [x] **Motor de Dominio Puro (battleship-core):** Clean Architecture en Java 17/22, tablero 10x10, niebla de guerra, jerarquia de buques de guerra, algoritmos de IA (Basic, Hunt & Target, Heatmap probabilistico x50) y tests unitarios.
- [x] **Backend Enterprise (battleship-backend):** Spring Boot 3.2.5, Spring Security 6 stateless con JWT, Spring Data JPA con PostgreSQL, documentacion interactiva OpenAPI 3 / Swagger UI y tests de integracion MockMvc.
- [x] **Frontend Web Tactico (frontend):** Next.js 16 (App Router, Turbopack, React 19, Tailwind CSS v4), estetica militar CRT (Combat Information Center), dique seco de colocacion de flota, tablero reactivo y sintetizador de audio nativo Web Audio API.
- [x] **DevOps, Docker & Cloud:** Dockerfiles multi-stage con usuarios no privilegiados, Docker Compose local, base de datos Neon PostgreSQL (Frankfurt), backend en Render, frontend en Vercel Edge CDN con dominio oficial `fleetgrid.me`, pipeline CI/CD en GitHub Actions y monitor keep-alive en UptimeRobot (`/healthz`).

---

### [EN CURSO] Hito 5: Gamificacion Tactica, Doble Ranking, Modo Invitado, Verificacion OTP y Perfil de Oficial
*Objetivo:* Refinar la experiencia de usuario, permitir juego inmediato sin barreras de entrada (Modo Invitado con reclamacion post-combate), verificacion de correo mediante codigo PIN de 6 digitos, sistema de puntuacion multidimensional (victoria y consolacion en derrota) y doble ranking en Admiralty Roll con perfil de oficial.

- [x] **Fase 1: Motor de Puntuacion Multidimensional y Endpoints de Doble Ranking en Backend:**
  - [x] Implementacion de la formula de victoria: `((1000 + bonus_shots) * mult_diff) + bonus_time`.
  - [x] Sistema de puntos de consolacion en derrotas completas por barcos enemigos hundidos (Carrier 250, Battleship 200, Submarine 150, PatrolBoat 100) multiplicado por dificultad.
  - [x] Blindaje anti-farming: abandonos computan con 0 puntos; los puntos de consolacion exigen resolucion completa del combate (al menos 4 movimientos y destruccion mutua).
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
  - [x] Eliminacion del bloque obsoleto de credenciales de prueba en `AuthModal`.
  - [x] Generacion de partidas de invitado con identificador efimero en memoria sin persistir en base de datos.
  - [x] Pantalla de fin de partida militar con desglose de puntos y boton de reclamacion: *"Reclamar Puntos e Inscribirse"*.
  - [x] Endpoint `POST /api/v1/games/{id}/claim` para transferir la partida jugada como invitado a la cuenta del usuario tras login o registro.

- [x] **Fase 3: Verificacion de Correo con Codigo OTP de 6 Digitos:**
  - [x] Servicio transaccional HTTP via Brevo API v3 (HTTPS Puerto 443, inmune al bloqueo de puertos SMTP en nubes PaaS como Render) con plantilla tactica naval y mock offline para dev/CI.
  - [x] Generacion y validacion de codigos OTP de 6 digitos con expiracion a los 10 minutos.
  - [x] Campos `email_verified`, `verification_otp` y `otp_expires_at` en `UserEntity` y endpoint `POST /api/v1/auth/verify-email`.
  - [x] Componente modal en el frontend con 6 casillas para introduccion del codigo PIN militar.

- [x] **Fase 4: Interfaz Web: Doble Ranking en Admiralty Roll & Expediente de Oficial (Profile):**
  - [x] Conmutador en Admiralty Roll: *"Top Partidas"* vs *"Top Oficiales"* con insignias `VICTORY` y `DEFEAT`.
  - [x] Nueva vista/modal `Profile` (Expediente de Oficial):
    - Rango militar naval por puntos (Cadete, Alferez, Teniente, Capitan, Almirante de la Flota).
    - Estadisticas globales de carrera (Puntos totales, Victorias, Derrotas, Ratio de acierto).
    - Tablas de Top 10 personal y Ultimas 10 partidas.
    - Formulario de cambio de contrasena.
  - [x] Visualizacion del rango y puntuacion total acumulada en la barra superior de navegacion.

- [ ] **Fase 5: Saneamiento de Base de Datos en Neon, Suite de Tests y Despliegue:**
  - [x] Sembrado de record de honor de referencia para `alvaro` y aislamiento en base de datos.
  - [x] Suite de pruebas automatizada ampliada cubriendo los nuevos endpoints, calculos matematicos y despacho transaccional HTTP (185 tests passing: 59 en core y 126 en backend).
  - [ ] Despliegue en produccion en Render y Vercel (tras push manual del desarrollador).

---

### [PENDIENTE] Hito 6: Multijugador Real-Time con WebSockets (Extension Avanzada)
*Objetivo:* Permitir partidas en tiempo real entre dos jugadores remotos conectando la interfaz web ya operativa.
- [ ] Servidor WebSocket con protocolo STOMP / SockJS en Spring Boot.
- [ ] Gestion de salas de juego (Game Rooms) con codigos de invitacion para compartir con amigos.
- [ ] Sincronizacion bidireccional en milisegundos de disparos, cambios de turno y fin de partida.
- [ ] Manejo de desconexiones y reconexiones con tiempo de gracia.
