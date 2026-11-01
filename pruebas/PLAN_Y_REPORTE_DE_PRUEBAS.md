# Plan y Reporte de Pruebas Automatizadas - FleetGrid Platform

Este documento consolida la documentacion de pruebas de caja blanca y caja negra, proporcionando la matriz de trazabilidad y el reporte de ejecucion automatizada mediante **JUnit 5**, **Mockito**, **AssertJ**, **Vitest** y **Playwright**.

---

## 1. Resumen Ejecutivo de Ejecucion

| Metrica | Valor Registrado |
| :--- | :--- |
| **Total de Tests en Maven (Backend & Core)** | **204** (59 core + 145 backend) |
| **Total de Tests en Frontend (Vitest)** | **10** (3 suites de componentes y utilidades) |
| **Suites End-to-End (Playwright)** | **3** (Flujos criticos de combate, OTP y reclamacion) |
| **Tests Fallidos** | **0** |
| **Errores de Ejecucion** | **0** |
| **Tests Omitidos / Skipped** | **0** |
| **Tiempo de Ejecucion Maven** | **~11.9 segundos** |
| **Tiempo de Ejecucion Vitest** | **~1.2 segundos** |
| **Frameworks de Testing** | JUnit 5 (Jupiter 5.10.2), Spring Boot Starter Test, Spring Security Test, MockMvc, Mockito 5.11, AssertJ 3.25, Vitest 5.0, Testing Library React 16, Playwright 1.50 |
| **Herramienta de Compilacion** | Apache Maven Wrapper (`./mvnw` / `mvnw.cmd`) y npm |

---

## 2. Matriz de Cobertura y Trazabilidad por Modulo

### 2.1 Motor de Dominio (`es.upm.etsisi.fis.model`)

| Clase de Test | Casos Cubiertos | Metodologia | Estado |
| :--- | :--- | :--- | :---: |
| `BoardTest` | Inicializacion 10x10, colocacion horizontal y vertical, deteccion de colisiones, limites de coordenadas, despliegue aleatorio de flota (14 celdas), secuencia de impactos (Miss, Hit, Sunk, Already attacked), niebla de guerra vs vista de propietario, autoreparacion de submarino. | Caja Negra / Caja Blanca | [PASS] (9/9) |
| `ShipsTest` | Carrier (5), Battleship (4), Submarine (3), PatrolBoat (2). Ciclo de vida de danos, estados de hundimiento, capacidades especiales (`IBattleship`, `ISubmarine`, `IPatrolBoat`). | Caja Negra | [PASS] (4/4) |
| `GameTest` | Creacion de partida, alternancia estricta de turnos, registro de movimientos, condicion de victoria por eliminacion de flota defensora, calculo de puntuacion final, instanciacion de bots con `AiPlayerFactory`. | Integracion de Dominio | [PASS] (4/4) |
| `MovementTest` | Constructor protegido JPA, constructor parametrizado, getters/setters, asociacion con User, clonacion, equals/hashCode y toString. | Unitaria | [PASS] (7/7) |
| `ScoreTest` | Constructor protegido JPA, calculo de puntos, asociacion con User, clonacion, equals/hashCode y toString. | Unitaria | [PASS] (8/8) |
| `UserTest` | Constructor protegido, getters/setters, asociacion con movimientos y puntuaciones, igualdad por ID (`equals`/`hashCode`), validacion de acciones especiales. | Unitaria | [PASS] (8/8) |

---

### 2.2 Algoritmos de Inteligencia Artificial (`es.upm.etsisi.fis.model.algorithm`)

| Clase de Test | Casos de Prueba Verificados | Metodologia | Estado |
| :--- | :--- | :--- | :---: |
| `AlgorithmsTest` | **BasicAlgorithm:** Disparos aleatorios dentro del tablero, nunca ataca celdas descubiertas, gestion de tableros nulos o vacios.<br>**NormalAlgorithm (Hunt & Target):** Patron de paridad de ajedrez `(r + c) % 2 == 0` en modo caza; expansion ortogonal inmediata tras primer impacto; extension sobre los extremos de la linea en impactos consecutivos alineados.<br>**ExpertAlgorithm (Heatmap):** Densidad probabilistica maxima sobre vecinos de impactos pendientes; preferencia de celdas centrales en tablero despejado; exclusion estricta de fallos ('M') y naves hundidas ('S'). | Caja Blanca Algoritmica | [PASS] (5/5) |

---

### 2.3 Capa de Controladores y Utilidades Legacy (`es.upm.etsisi.fis.controller` y `servidor`)

| Clase de Test | Casos de Prueba Verificados | Metodologia | Estado |
| :--- | :--- | :--- | :---: |
| `AuthControllerTest` | Instancia Singleton, lectura de lista negra desde resources (`list_raw.txt`), validacion estricta de nombres de usuario (longitud, alfanumericos, nombres restringidos). | Unitaria / Validacion | [PASS] (4/4) |
| `DataControllerTest` | Inicializacion de sesion mock para pruebas (`initForTest`), operaciones de persistencia (`saveOrUpdate`, `delete`, `initializeUserRelations`). | Unitaria / Mock | [PASS] (3/3) |
| `UserControllerTest` | Alta y baja de usuarios, busqueda por ID, listado general de usuarios y claves, caso de usuario inexistente. | Unitaria | [PASS] (4/4) |
| `ExternalLDAPTest` | Instanciacion exitosa, ejecucion segura de `LoginLDAP()` en modo no interactivo/headless retornando null sin excepciones y conmutacion de estado interactivo. | Unitaria / Desacoplamiento Legacy | [PASS] (3/3) |

---

### 2.4 Backend Enterprise & REST API (`es.upm.etsisi.fis.server` en modulo `battleship-backend`)

| Clase de Test | Casos de Prueba Verificados | Metodologia | Estado |
| :--- | :--- | :--- | :---: |
| `AuthControllerIntegrationTest` | Registro exitoso de nuevo usuario con codigo 201 y despacho de OTP; rechazo de usuario duplicado con email distinto (400); rechazo de email duplicado con username distinto (400); rechazo de contrasena menor a 6 caracteres (400); rechazo de formato de email invalido (400); login con credenciales correctas mediante email (200 OK); rechazo de intento de login con username en lugar de email (400); login con password incorrecto (401); consulta de `/me` con JWT y rechazo 403 sin token; verificacion de correo mediante OTP de 6 digitos; rechazo de codigo OTP invalido o expirado; reenvio de OTP; rechazo de verificacion para email ya verificado (400); rechazo de OTP expirado (400); retorno 404 ante usuario inexistente en reenvio; cambio seguro de password con validacion BCrypt; rechazo de contrasena actual incorrecta; rechazo de nueva contrasena menor a 6 caracteres (400); normalizacion de espacios y mayusculas en email; proteccion contra fuerza bruta en OTP con bloqueo a los 5 intentos fallidos; recuperacion de cuenta con forgot-password y reset-password. | Integracion MockMvc / Spring Security | [PASS] (25/25) |
| `UsernamePolicyValidatorTest` | Validacion de nombres permitidos con caracteres alfanumericos, guiones bajos (`_`) y medios (`-`); rechazo estricto de nombres reservados del sistema (admin, system, root, etc.); rechazo de terminos ofensivos y lenguaje de odio en espanol e ingles; deteccion de variantes con leetspeak y separadores; rechazo de cadenas nulas y vacias. | Unitaria de Politica de Usuario | [PASS] (68/68) |
| `GameControllerIntegrationTest` | Creacion de partida vs bot con codigo 201; disparo y respuesta con contraataque sincronizado de la IA; trazabilidad del historial cronologico de movimientos; rechazo de coordenadas fuera de limites (10x10); rechazo de disparos duplicados; combate en HARD contra IA experta (`AI-Expert`); error 404 ante partidas inexistentes; ejecucion en Modo Invitado en memoria sin persistir en BD; rechazo 403 al intentar reclamar sin JWT; rechazo de reclamacion para partida no finalizada; error 404 ante reclamacion de sesion inexistente. | Integracion MockMvc / Motor Dominio | [PASS] (10/10) |
| `LeaderboardControllerIntegrationTest` | Consulta publica del ranking de mejores partidas individuales (`/matches`) con badges de victoria y derrota; consulta del ranking de oficiales ordenado por suma de puntos (`/players`); consulta de Top 10 personal del oficial (`/me/matches`); historial cronologico de ultimas 10 batallas (`/me/recent`); estadisticas de carrera militar y rango naval (`/me/stats`); verificacion de persistencia limpia y sembrado benchmark inicial. | Integracion MockMvc / JPA | [PASS] (7/7) |
| `QueryBudgetIntegrationTest` | Presupuesto de consultas a base de datos (Database Activity Budget): `/healthz` ejecuta exactamente 0 consultas SQL; `/leaderboard/matches` cacheado ejecuta 0 consultas; `/leaderboard/players` cacheado ejecuta 0 consultas; autenticacion JWT servida desde cache Caffeine en memoria ejecuta 0 consultas de carga sobre `server_users`; disparos intermedios de combate ejecutan exactamente 0 consultas SQL. | Integracion Hibernate Statistics | [PASS] (5/5) |
| `LanguagePolicyRegressionTest` | Control de regresion de politica de idiomas: verificacion de ausencia de literales de cadena en espanol en `src/main`, verificacion de ausencia de tildes y caracteres diacriticos en codigo fuente, comprobacion de que ningun valor por defecto apunta a dominios no autorizados (gmail.com). | Integracion de Auditoria de Codigo | [PASS] (3/3) |
| `JwtServiceTest` | Emision de tokens JWT firmados con HMAC-SHA256; extraccion de usuario; invalidacion para usuarios distintos; validacion de clave secreta con al menos 256 bits (32 bytes); rechazo de claves por defecto o debiles en perfil `prod`. | Unitaria Criptografica / Seguridad | [PASS] (5/5) |
| `EmailServiceTest` | Despacho de correos via Brevo API v3; escape seguro de HTML contra inyecciones; adaptador MockEmailSender para entornos locales y de CI; gestion de errores de conexion de red. | Unitaria de Servicio Transaccional | [PASS] (5/5) |
| `OtpCryptoUtilsTest` | Hasheo criptografico SHA-256 de codigos OTP; comparacion en tiempo constante (`MessageDigest.isEqual`) contra ataques de temporizacion; rechazo de discrepancias de longitud y hashes nulos. | Unitaria de Criptografia | [PASS] (4/4) |
| `NavalRankCalculatorTest` | Validacion de todos los umbrales de rango naval (Cadete Naval <= 2500, Alferez de Fragata <= 7500, Teniente de Navio <= 15000, Capitan de Navio <= 30000, Almirante de la Flota > 30000) y proteccion ante valores negativos. | Unitaria de Dominio | [PASS] (5/5) |
| `DatabaseConfigTest` | Verificacion de DataSource por defecto sin entorno; conversion y sanitizacion de URLs cloud con protocolo `postgres://` y credenciales incrustadas a formato JDBC; preservacion de cadenas `jdbc:postgresql://`. | Unitaria de Infraestructura Cloud | [PASS] (7/7) |
| `HealthControllerIntegrationTest` | Verificacion de endpoint `/healthz` devolviendo codigo 200 OK, servicio identificado y estado UP sin requerir cabeceras de autorizacion y sin conexion a base de datos. | Integracion MockMvc / Cloud Health | [PASS] (1/1) |

---

### 2.5 Pruebas Unitarias y de Componentes del Frontend (`frontend/src/components/__tests__`)

| Archivo de Test | Casos de Prueba Verificados | Herramienta | Estado |
| :--- | :--- | :--- | :---: |
| `WebMetrics.test.tsx` | Renderizado condicional de `<Analytics />` y `<SpeedInsights />` unicamente cuando `NEXT_PUBLIC_ENABLE_VERCEL_METRICS === 'true'`; retorno nulo (0 bytes, 0 peticiones) cuando la variable esta ausente o en false. | Vitest / Testing Library | [PASS] (3/3) |
| `WarmUp.test.tsx` | Disparo en segundo plano de peticion `/healthz` si no existe marca en `sessionStorage`; supresion de peticiones repetitivas si la marca es reciente (< 10 min); reactivacion automatica tras expiracion de la sesion. | Vitest / Testing Library | [PASS] (3/3) |
| `AuthModal.test.tsx` | Renderizado tactico de inicio de sesion; conmutacion a nueva comision (registro); distribucion atomica de los 6 digitos militares al pegar un codigo PIN completo; atributos de accesibilidad (`role="dialog"`, `aria-labelledby`). | Vitest / Testing Library | [PASS] (4/4) |

---

### 2.6 Pruebas End-to-End (E2E) con Playwright (`frontend/e2e`)

| Suite E2E | Flujo de Usuario y Criterio de Aceptacion | Herramienta | Estado |
| :--- | :--- | :--- | :---: |
| `combat-flow.spec.ts` (Flujo 1) | **Partida Rapida:** Despliegue de flota con `AUTO-DEPLOY`, inicio de combate como invitado, transicion al panel tactico `TARGETING RADAR`, ejecucion de disparo y respuesta con registro en log de telemetria. | Playwright Chromium | [CONFIGURADO] |
| `combat-flow.spec.ts` (Flujo 2) | **Resolucion & Reclamacion:** Combate completado con victoria, visualizacion de pantalla final con desglose de puntos y boton interactivo de reclamacion para inscribirse en el Admiralty Roll. | Playwright Chromium | [CONFIGURADO] |
| `combat-flow.spec.ts` (Flujo 3) | **Seguridad Militar & Credenciales:** Apertura de modal de autorizacion, transicion a registro con validacion de campos y transicion a recuperacion de credenciales con codigo PIN de 6 casillas. | Playwright Chromium | [CONFIGURADO] |

---

### 2.7 Pipeline de Integracion Continua (CI/CD en GitHub Actions)

| Job de Pipeline | Pasos Ejecutados | Entorno | Disparador | Estado |
| :--- | :--- | :--- | :--- | :---: |
| `backend-validation` | Checkout, Setup JDK 17 (Temurin), Cache Maven, `./mvnw clean test -B` (204 tests automatizados). | Ubuntu Latest | Push / PR `main` | [ACTUALIZADO] |
| `frontend-validation` | Checkout, Setup Node.js 20, Cache npm, `npm ci`, `npm run lint`, `npx tsc --noEmit`, `npm run test`, `npm run build`. | Ubuntu Latest | Push / PR `main` | [ACTUALIZADO] |

---

## 3. Instrucciones de Ejecucion y Verificacion

Para ejecutar la suite completa de pruebas desde la raiz del repositorio:

```bash
# Backend y Core (Maven):
.\mvnw.cmd test

# Frontend Unitario y Componentes (Vitest):
cd frontend && npm run test

# Frontend End-to-End (Playwright):
cd frontend && npm run test:e2e

# Frontend Lint y Compilacion:
cd frontend && npm run lint && npm run build
```
