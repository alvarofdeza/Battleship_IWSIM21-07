# Plan y Reporte de Pruebas Automatizadas - Battleship Platform

Este documento consolida la documentacion de pruebas de caja blanca y caja negra, proporcionando la matriz de trazabilidad y el reporte de ejecucion automatizada mediante **JUnit 5**, **Mockito** y **AssertJ**.

---

## 1. Resumen Ejecutivo de Ejecucion

| Metrica | Valor Registrado |
| :--- | :--- |
| **Total de Tests Automatizados** | **101** (59 core + 42 backend) |
| **Tests Fallidos** | **0** |
| **Errores de Ejecucion** | **0** |
| **Tests Omitidos / Skipped** | **0** |
| **Tiempo de Ejecucion Total** | **~11.5 segundos** |
| **Frameworks de Testing** | JUnit 5 (Jupiter 5.10.2), Spring Boot Starter Test, Spring Security Test, MockMvc, Mockito 5.11, AssertJ 3.25 |
| **Herramienta de Compilacion** | Apache Maven Wrapper (`./mvnw` / `mvnw.cmd`) |

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

### 2.3 Capa de Controladores y Utilidades (`es.upm.etsisi.fis.controller` y `servidor`)

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
| `AuthControllerIntegrationTest` | Registro exitoso de nuevo usuario con codigo 201 y despacho de OTP; rechazo de usuario o email duplicado con error 400; login con credenciales correctas (200 OK) e incorrectas (401); consulta de `/me` con JWT y rechazo 403 sin token; verificacion de correo mediante OTP de 6 digitos; rechazo de codigo OTP invalido o expirado; reenvio de OTP; rechazo de verificacion para email ya verificado (400); rechazo de OTP expirado (400); retorno 404 ante usuario inexistente en reenvio; cambio seguro de password con validacion BCrypt y rechazo de contraseña actual incorrecta. | Integracion MockMvc / Spring Security | [PASS] (14/14) |
| `GameControllerIntegrationTest` | Creacion de partida vs bot con codigo 201; disparo y respuesta con contraataque sincronizado de la IA; trazabilidad del historial cronologico de movimientos; rechazo de coordenadas fuera de limites (10x10); rechazo de disparos duplicados; combate en HARD contra IA experta (`AI-Expert`); error 404 ante partidas inexistentes; ejecucion en Modo Invitado en memoria sin persistir en BD; rechazo 403 al intentar reclamar sin JWT; rechazo de reclamacion para partida no finalizada; error 404 ante reclamacion de sesion inexistente. | Integracion MockMvc / Motor Dominio | [PASS] (10/10) |
| `LeaderboardControllerIntegrationTest` | Consulta publica del ranking de mejores partidas individuales (`/matches`) con badges de victoria y derrota; consulta del ranking de oficiales ordenado por suma de puntos (`/players`); consulta de Top 10 personal del oficial (`/me/matches`); historial cronologico de ultimas 10 batallas (`/me/recent`); estadisticas de carrera militar y rango naval (`/me/stats`); verificacion de persistencia limpia y sembrado benchmark inicial. | Integracion MockMvc / JPA | [PASS] (7/7) |
| `NavalRankCalculatorTest` | Validacion de todos los umbrales de rango naval (Cadete Naval <= 2500, Alferez de Fragata <= 7500, Teniente de Navio <= 15000, Capitan de Navio <= 30000, Almirante de la Flota > 30000) y proteccion ante valores negativos. | Unitaria de Dominio | [PASS] (5/5) |
| `JwtServiceTest` | Emision de tokens JWT firmados con algoritmo HMAC-SHA256, inclusion de claims y verificacion de vigencia temporal; validacion estricta de rechazo ante firmas manipuladas o tokens expirados. | Unitaria de Criptografia | [PASS] (2/2) |
| `DatabaseConfigTest` | Verificacion de DataSource por defecto sin entorno; conversion y sanitizacion de URLs cloud con protocolo `postgres://` y credenciales incrustadas a formato JDBC; preservacion de cadenas `jdbc:postgresql://`. | Unitaria de Infraestructura Cloud | [PASS] (3/3) |
| `HealthControllerIntegrationTest` | Verificacion de endpoint `/healthz` devolviendo codigo 200 OK, servicio identificado y estado UP sin requerir cabeceras de autorizacion. | Integracion MockMvc / Cloud Health | [PASS] (1/1) |

---

### 2.5 Verificacion y Compilacion Estricta del Frontend (`frontend/`)

| Suite / Proceso | Casos de Prueba y Validacion | Herramienta | Estado |
| :--- | :--- | :--- | :---: |
| Verificacion de Tipos TypeScript | Comprobacion estricta sin errores de tipado (`noImplicitAny`, interfaces de dominio, DTOs de API enriquecidos con puntuacion y ranking). | TypeScript Compiler (`tsc`) | [PASS] |
| Build de Produccion Next.js | Compilacion de rutas estaticas, empaquetado de componentes React 19 y optimizacion Turbopack sin advertencias. | Next.js Build Pipeline | [PASS] |
| Modo Invitado & Reclamacion Post-Combate | Inicio inmediato de combate sin barrera de login; calculo de puntuacion en pantalla final con desglose y modal de reclamacion vinculado a registro/login. | React State Machine & API Client | [PASS] |
| Verificacion OTP de 6 Digitos | Componente de 6 cajas de entrada militar con navegacion automatica de cursor, pegado directo de codigo PIN y llamada a Resend API. | Componente `AuthModal.tsx` | [PASS] |
| Expediente de Oficial (Profile) | Despliegue de modal de oficial con rango militar segun puntos de carrera, resumen de victorias/derrotas, Top 10 personal, ultimas 10 batallas y formulario de cambio de credencial. | Componente `ProfileModal.tsx` | [PASS] |

---

### 2.6 Pipeline de Integracion Continua (CI/CD en GitHub Actions)

| Job de Pipeline | Pasos Ejecutados | Entorno | Disparador | Estado |
| :--- | :--- | :--- | :--- | :---: |
| `backend-validation` | Checkout, Setup JDK 17 (Temurin), Cache Maven, `./mvnw clean test -B` (101 tests). | Ubuntu Latest | Push / PR `main` | [CONFIGURADO] |
| `frontend-validation` | Checkout, Setup Node.js 20, Cache npm, `npm ci`, `npx tsc --noEmit`, `npm run build`. | Ubuntu Latest | Push / PR `main` | [CONFIGURADO] |

---

## 3. Instrucciones de Ejecucion y Verificacion

Para ejecutar la suite completa de pruebas desde la raiz del repositorio:

```bash
# En Windows (usando el Maven Wrapper):
.\mvnw.cmd test

# En Linux o macOS:
./mvnw test
```

Para ejecutar una clase de test especifica (ejemplo: algoritmos de IA):

```bash
.\mvnw.cmd test -Dtest=AlgorithmsTest
```
