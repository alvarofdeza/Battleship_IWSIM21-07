# Battleship Platform - Core Engine, Enterprise Backend & Web Terminal

![Java](https://img.shields.io/badge/Java-17%2F22-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-16-000000?style=for-the-badge&logo=nextdotjs&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-v4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?style=for-the-badge&logo=openapiinitiative&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-5.10.2-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)
![Tests](https://img.shields.io/badge/Tests-72%20Passed-success?style=for-the-badge)

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
* **Documentacion Interactiva OpenAPI 3 / Swagger UI:**
  * Especificacion viva disponible en tiempo real en `/swagger-ui.html` con soporte Bearer Token.
* **Suite Automatizada de 72 Tests (100% Cobertura de Integracion y Dominio):**
  * 56 tests en `core` y 16 tests de integracion con MockMvc en `backend`.

---

## 2. Documentacion Tecnica y Modelado

* [modelado/ARQUITECTURA_Y_DISENO.md](modelado/ARQUITECTURA_Y_DISENO.md): Diagramas de componentes, arquitectura enterprise Spring Boot 3, arquitectura web frontend, diagrama de clases, diagrama entidad-relacion (ERD) y catalogo de endpoints REST.
* [pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md](pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md): Matriz de trazabilidad y reporte de los 72 tests automatizados (JUnit 5, Mockito, Spring Security Test, MockMvc) y verificacion de compilacion frontend.
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

### Ejecucion de la Suite Completa de Tests
Para compilar y verificar los 72 tests unitarios y de integracion:

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

## 5. Credenciales de Prueba Preconfiguradas

Al iniciar la aplicacion, se generan automaticamente los siguientes usuarios semilla:

| Usuario | Email | Password | Rol |
| :--- | :--- | :--- | :--- |
| `admin` | `admin@upm.es` | `admin123` | `ROLE_ADMIN` |
| `alvaro` | `alvaro@alumnos.upm.es` | `password123` | `ROLE_USER` |
| `adrian` | `adrian@alumnos.upm.es` | `password123` | `ROLE_USER` |

