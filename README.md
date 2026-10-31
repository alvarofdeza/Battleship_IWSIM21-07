# Battleship Platform - Core Engine & Enterprise Backend

![Java](https://img.shields.io/badge/Java-17%2F22-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?style=for-the-badge&logo=openapiinitiative&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-5.10.2-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)
![Tests](https://img.shields.io/badge/Tests-72%20Passed-success?style=for-the-badge)

Implementacion modular en Java del clasico juego de combate naval Hundir la Flota (Battleship). Inicialmente desarrollado en el ambito academico para la asignatura Fundamentos de Ingenieria del Software en la Universidad Politecnica de Madrid (UPM - ETSISI), este proyecto ha sido transformado en una plataforma profesional desacoplada con **Clean Architecture**, **Spring Boot 3**, **Spring Security con JWT**, **persistencia relacional Spring Data JPA** y **algoritmos avanzados de Inteligencia Artificial**.

---

## 1. Caracteristicas Principales

* **Arquitectura Multi-Modulo Maven:**
  * `battleship-core`: Motor de dominio puro sin frameworks invasivos, jerarquia de naves, validacion de impactos y niebla de guerra.
  * `battleship-server`: Backend enterprise con Spring Boot 3, inyeccion de dependencias, API REST y seguridad stateless.
* **Tres Niveles de Inteligencia Artificial (Strategy Pattern):**
  * **Facil (BasicAlgorithm):** Distribucion pseudoaleatoria uniforme entre casillas inexploradas.
  * **Normal (NormalAlgorithm - Hunt & Target):** Patron de paridad de ajedrez `(row + col) % 2 == 0` en modo caza; exploracion ortogonal y continuidad de linea en modo objetivo.
  * **Experto (ExpertAlgorithm - Probability Density Heatmap):** Superposicion probabilistica matricial de barcos vivos sobre 10x10 con multiplicador ponderado x50 en vecindades de impactos activos.
* **Seguridad Moderna y Autenticacion JWT:**
  * Sustitucion del mock LDAP por Spring Security 6 stateless con JSON Web Tokens (JJWT 0.12.5).
  * Cifrado criptografico seguro de contraseñas mediante `BCryptPasswordEncoder`.
  * Filtro de seguridad `JwtAuthenticationFilter` y endpoints protegidos con rol de usuario.
* **Documentacion Interactiva OpenAPI 3 / Swagger UI:**
  * Especificacion viva disponible en tiempo real en `/swagger-ui.html` con soporte Bearer Token.
* **Suite Automatizada de 72 Tests (100% Cobertura de Integracion y Dominio):**
  * 56 tests en `core` y 16 tests de integracion con MockMvc en `server`.

---

## 2. Documentacion Tecnica y Modelado

* [modelado/ARQUITECTURA_Y_DISENO.md](modelado/ARQUITECTURA_Y_DISENO.md): Diagramas de componentes, arquitectura enterprise Spring Boot 3, diagrama de clases, diagrama entidad-relacion (ERD) y catalogo de endpoints REST.
* [pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md](pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md): Matriz de trazabilidad y reporte de los 72 tests automatizados (JUnit 5, Mockito, Spring Security Test, MockMvc).
* [AGENTS.md](AGENTS.md): Hoja de ruta (Roadmap), Definition of Done (DoD) y registro de hitos del proyecto.

---

## 3. Pila Tecnologica

* **Lenguaje:** Java 17 / 22
* **Framework Backend:** Spring Boot 3.2.5 (Spring MVC, Spring Data JPA, Spring Security 6, Spring Validation)
* **Documentacion API:** SpringDoc OpenAPI 2.5.0 / Swagger UI 5.13.0
* **Seguridad & Tokens:** JJWT 0.12.5, BCrypt
* **Bases de Datos:** H2 Database (desarrollo y testing en memoria), PostgreSQL (configurado para produccion)
* **Build & Dependency Management:** Apache Maven Wrapper (`mvnw` / `mvnw.cmd`)
* **Testing:** JUnit 5 Jupiter 5.10.2, MockMvc, Mockito 5.11, AssertJ 3.25

---

## 4. Guia de Inicio Rapido

### Requisitos Previos
* Java Development Kit (JDK) 17 o superior.

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
.\mvnw.cmd -pl server spring-boot:run

# En Linux o macOS:
./mvnw -pl server spring-boot:run
```

Una vez levantado, accede a:
* **Swagger UI Interactivo:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Consola H2 Database:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:battleship_db`, User: `sa`, Password: *(vacio)*)

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

