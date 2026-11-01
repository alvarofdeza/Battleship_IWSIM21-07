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

## Configuracion de Infraestructura Cloud y Servicios Externos

A continuacion se detallan las configuraciones operativas aplicadas en cada plataforma y consola web:

* **Namecheap (Registrador de Dominio y DNS Avanzado):**
  - **Dominio registrado:** `fleetgrid.me` (activado mediante GitHub Student Developer Pack con vigencia hasta 2027).
  - **Registros DNS en Host Records (Advanced DNS):**
    - Registro **A**: Host `@` apuntando a `76.76.21.21` (Direccion IP Anycast de Vercel para el dominio apex canonico).
    - Registro **CNAME**: Host `www` apuntando a `cname.vercel-dns.com.` (Redireccion HTTP 301 automatica de `www.fleetgrid.me` a `fleetgrid.me`).
    - Registro **CNAME**: Host `api` apuntando a `battleship-backend-ys61.onrender.com.` (Enrutamiento del subdominio de la API hacia el Web Service de Render).
    - Registro **CNAME (DKIM 1)**: Host `brevo1._domainkey` apuntando a `b1.fleetgrid-me.dkim.brevo.com.` (Firma digital de correo).
    - Registro **CNAME (DKIM 2)**: Host `brevo2._domainkey` apuntando a `b2.fleetgrid-me.dkim.brevo.com.` (Firma digital de correo).
    - Registro **TXT**: Host `@` con valor `google-site-verification=...` (Verificacion de propiedad de dominio en Google Search Console).
  - **Mail Settings (Email Forwarding):** Redireccion de cortesia configurada desde `hello@fleetgrid.me` (Reply-To oficial) hacia el buzon de correo personal del desarrollador.

* **Vercel (Hosting del Frontend Web y Edge CDN):**
  - **Proyecto:** `fleetgrid` enlazado al repositorio GitHub (rama `main`).
  - **Dominios configurados en Vercel:**
    - `fleetgrid.me`: Dominio de produccion canonico.
    - `www.fleetgrid.me`: Configurado con redireccion 301 automatica permanente hacia `fleetgrid.me`.
  - **Variables de Entorno (Project Settings > Environment Variables):** Guardadas obligatoriamente bajo el tipo **Config** (no Secret, requerido por Vercel para variables con prefijo `NEXT_PUBLIC_` expuestas al cliente web).

* **Render (Hosting del Servidor Backend REST API):**
  - **Servicio:** Web Service `battleship-backend` (Frankfurt, plan gratuito, un unico servicio activo para respetar el presupuesto de 750 h/mes).
  - **Custom Domains:** Subdominio `api.fleetgrid.me` registrado y verificado en la seccion **Settings > Custom Domains**, con certificado TLS/SSL aprovisionado de forma automatica por Render.
  - **Variables de Entorno (Environment):** Variables de produccion del backend (`SPRING_PROFILES_ACTIVE`, `SPRING_DATASOURCE_URL`, `JWT_SECRET`, `BREVO_API_KEY`, etc.).

* **Neon (Capa de Persistencia PostgreSQL 16 Serverless):**
  - **Instancia:** Compute serverless en Frankfurt (`ep-silent-waterfall...aws.neon.tech`).
  - **Politica de Suspension:** Modo autosuspend a los 5 minutos de inactividad de conexiones para garantizar consumo cero de computo cuando no hay partidas en curso (presupuesto estricto de 100 CU-h/mes).
  - **Seguridad:** Conexiones cifradas obligatorias via SSL (`sslmode=require`).

* **Brevo (Servicio Transaccional de Correo Electronico):**
  - **Dominio autenticado:** `fleetgrid.me` verificado con firmas criptograficas DKIM vinculadas desde Namecheap.
  - **Remitente autorizado:** `noreply@fleetgrid.me` ("FleetGrid Command"), con Reply-To `hello@fleetgrid.me`.
  - **Credenciales API:** Clave de API v3 generada en la seccion **SMTP y API** para despacho seguro por HTTPS en puerto 443 (sin bloqueo de puertos SMTP).
  - **Subdominio con marca:** Omitido (no requerido para correo transaccional de autenticacion).

* **Google Search Console y Bing Webmaster Tools (SEO e Indexacion):**
  - **Google Search Console:** Verificacion de propiedad completada por registro DNS TXT. Sitemap enviado y verificado con exito en `sitemap.xml` con las 5 URLs canonicas descubiertas (`/`, `/leaderboard`, `/rules`, `/faq`, `/privacy`).
  - **Bing Webmaster Tools:** Importacion directa de sitemaps y verificacion de propiedad sincronizada desde Google Search Console en un solo paso.

* **UptimeRobot (Monitor Keep-Alive de Disponibilidad):**
  - **Monitor:** HTTP(s) cada 5 minutos apuntando al endpoint estatico `https://api.fleetgrid.me/healthz`.
  - **Aislamiento de BD:** `/healthz` responde en memoria pura sin tocar la base de datos ni el pool HikariCP, manteniendo a Render activo 24/7 mientras Neon se suspende tras 5 minutos sin actividad de usuarios reales.

---

## Catalogo Maestro de Variables de Entorno

| Variable | Modulo | Plataforma / Panel de Configuracion | Descripcion | Obligatoria en Prod | Valor Predeterminado Local / Ejemplo |
|---|---|---|---|---|---|
| `PORT` | Backend | Render (Variable interna del sistema) | Puerto HTTP de escucha asignado por el orquestador | Si (automatica) | `8080` (en local) |
| `SPRING_PROFILES_ACTIVE` | Backend | Render (`Environment`) | Perfil activo de Spring Boot (`prod` o `dev`) | Si | `dev` |
| `SPRING_DATASOURCE_URL` | Backend | Render (`Environment`) | Cadena de conexion PostgreSQL (Neon Frankfurt con SSL) | Si | `jdbc:h2:mem:battleship_db;...` |
| `SPRING_DATASOURCE_USERNAME` | Backend | Render (`Environment`) | Usuario de BD *(opcional si esta contenido en la URL)* | No (autoextraido) | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Backend | Render (`Environment`) | Contrasena de BD *(opcional si esta contenida en la URL)* | No (autoextraido) | *(vacio)* |
| `JWT_SECRET` | Backend | Render (`Environment`) | Clave criptografica HMAC-SHA256 (minimo 256 bits) | Si | Clave dev de prueba |
| `BREVO_API_KEY` | Backend | Render (`Environment`) | Clave de API v3 de Brevo para envio transaccional OTP | Si | *(vacio, activa mock local)* |
| `BREVO_SENDER_EMAIL` | Backend | Render (`Environment`) | Remitente oficial de correos autorizados | Si | `noreply@fleetgrid.me` |
| `MAIL_PROVIDER` | Backend | Render (`Environment`) | Proveedor de correo (`brevo`, `smtp`, `mock`) | No | `mock` en dev, `brevo` en prod |
| `CORS_ALLOWED_ORIGINS` | Backend | Render (`Environment`) | Origenes autorizados para intercambio de recursos CORS | Si | `http://localhost:3000` |
| `NEXT_PUBLIC_API_URL` | Frontend | Vercel (`Environment Variables`, tipo `Config`) | URL base publica de la API REST para el cliente web | Si | `http://localhost:8080/api/v1` |
| `NEXT_PUBLIC_ENABLE_VERCEL_METRICS` | Frontend | Vercel (`Environment Variables`, tipo `Config`) | Habilita analiticas y telemetria Web Vitals en Vercel | No | `false` |

---

## Ejecucion de Pruebas Automatizadas

```bash
# Pruebas de dominio y backend (JUnit 5, Mockito, Spring Security Test)
./mvnw clean test -B

# Pruebas de frontend y verificacion de tipos
cd frontend && npm run test && npx tsc --noEmit
```
