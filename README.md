# FleetGrid Platform - Tactical Naval Command & Combat Engine

[![Continuous Integration](https://github.com/alvaroaabad/Battleship_IWSIM21-07/actions/workflows/ci.yml/badge.svg)](https://github.com/alvaroaabad/Battleship_IWSIM21-07/actions/workflows/ci.yml)

> [!NOTE]
> **Origen Academico y Evolucion:** FleetGrid es la evolucion tecnologica y maduracion profesional del proyecto de software universitario original `Battleship_IWSIM21-07` (UPM - ETSISI).

FleetGrid es una plataforma web modular de combate naval tactico de alto rendimiento, disenada bajo los principios de Clean Architecture. Combina un motor de dominio puro en Java, un backend enterprise en Spring Boot 3 con seguridad stateless por tokens JWT y persistencia en PostgreSQL serverless, y una interfaz web militar interactiva en Next.js 16 con estetica retro CRT y sintetizador nativo Web Audio API.

---

## Enlaces de Produccion en Vivo

* **Plataforma Web (Dominio Canonico):** [https://fleetgrid.me](https://fleetgrid.me)
* **API REST Backend:** [https://api.fleetgrid.me](https://api.fleetgrid.me)
* **Documentacion Interactiva OpenAPI / Swagger UI:** [https://api.fleetgrid.me/swagger-ui.html](https://api.fleetgrid.me/swagger-ui.html)
* **Monitor de Disponibilidad (Health Check):** [https://api.fleetgrid.me/healthz](https://api.fleetgrid.me/healthz)

---

## Estructura del Proyecto

* **`core/` (`battleship-core`):** Motor de simulacion de combate naval, tablero 10x10, niebla de guerra y algoritmos de IA (Basic, Normal y Probabilistic Heatmap).
* **`backend/` (`battleship-backend`):** API REST Spring Boot 3, autenticacion JWT, cache Caffeine, control de tasa Bucket4j y capa de persistencia JPA.
* **`frontend/`:** Interfaz web tactica en Next.js 16 (App Router, Turbopack, React 19, Tailwind CSS v4) con dique seco interactivo y telemetria en tiempo real.
* **`modelado/`:** Documentacion tecnica de diseno, diagramas de arquitectura y catalogo de endpoints en [modelado/ARQUITECTURA_Y_DISENO.md](modelado/ARQUITECTURA_Y_DISENO.md).
* **`pruebas/`:** Plan maestro de aseguramiento de calidad y matriz de ejecucion en [pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md](pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md).
* **`AGENTS.md`:** Fuente unica de verdad con normativas de ingenieria, restricciones de infraestructura y hoja de ruta en [AGENTS.md](AGENTS.md).

---

## Guia de Inicio Rapido

### Opcion A: Despliegue con Docker Compose (Recomendado)

```bash
# 1. Clonar el repositorio y copiar la plantilla de variables
cp .env.example .env

# 2. Compilar e iniciar los servicios en segundo plano
docker compose up -d --build

# 3. Acceder al frontend en http://localhost:3000 y al backend en http://localhost:8080
```

### Opcion B: Ejecucion Local para Desarrollo

Requiere Java 17+ y Node.js 20+.

```bash
# Terminal 1: Backend Spring Boot
./mvnw spring-boot:run -pl backend

# Terminal 2: Frontend Next.js
cd frontend && npm install && npm run dev
```

---

## Variables de Entorno Principales

| Variable | Modulo | Descripcion | Valor Predeterminado Local |
|---|---|---|---|
| `PORT` | Backend | Puerto del servidor HTTP | `8080` |
| `SPRING_PROFILES_ACTIVE` | Backend | Perfil de configuracion Spring | `dev` |
| `SPRING_DATASOURCE_URL` | Backend | Cadena de conexion JDBC | `jdbc:h2:mem:battleship_db;...` |
| `JWT_SECRET` | Backend | Clave secreta para firma de tokens JWT | Clave local de prueba |
| `BREVO_API_KEY` | Backend | Clave de API transaccional de Brevo | Vacia (activa mock offline) |
| `BREVO_SENDER_EMAIL` | Backend | Remitente oficial de correos | `noreply@fleetgrid.me` |
| `NEXT_PUBLIC_API_URL` | Frontend | URL base para peticiones REST | `http://localhost:8080/api/v1` |

---

## Ejecucion de Pruebas Automatizadas

```bash
# Pruebas de dominio y backend (JUnit 5, Mockito, Spring Security Test)
./mvnw clean test -B

# Pruebas de frontend y verificacion de tipos
cd frontend && npm run test && npx tsc --noEmit
```
