# Battleship Platform - Core Engine & AI Platform

![Java](https://img.shields.io/badge/Java-17%2F22-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-5.10.2-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)
![Tests](https://img.shields.io/badge/Tests-56%20Passed-success?style=for-the-badge)

Implementacion modular en Java del clasico juego de combate naval Hundir la Flota (Battleship). Inicialmente desarrollado en el ambito academico para la asignatura Fundamentos de Ingenieria del Software en la Universidad Politecnica de Madrid (UPM - ETSISI), este proyecto ha sido completamente refactorizado e independizado hacia una plataforma moderna desacoplada con **Clean Architecture**, **Domain-Driven Design (DDD)** y **algoritmos avanzados de Inteligencia Artificial**.

---

## 1. Caracteristicas Principales

* **Motor de Dominio Propio (Java Puro):**
  * Sin dependencias de frameworks ni librerias de terceros en el nucleo del juego.
  * Tablero matricial de 10x10 con niebla de guerra para el atacante y visualizacion de flota para el defensor.
  * Jerarquia de naves con esloras y capacidades tacticas unicas: *Carrier* (5), *Battleship* (4), *Submarine* (3) y *PatrolBoat* (2).
  * Validacion estricta de coordenadas, prevencion de colisiones y despliegue aleatorio automatizado.
* **Tres Niveles de Inteligencia Artificial (Strategy Pattern):**
  * **Facil (BasicAlgorithm):** Distribucion pseudoaleatoria uniforme entre casillas inexploradas.
  * **Normal (NormalAlgorithm - Hunt & Target):** Patron de paridad de tablero de ajedrez `(row + col) % 2 == 0` en modo caza; exploracion ortogonal de vecinos adyacentes y continuidad de linea en modo objetivo.
  * **Experto (ExpertAlgorithm - Probability Density Heatmap):** Superposicion matematica de todas las permutaciones posibles de naves restantes en la cuadricula de 10x10, con ponderacion x50 en vecindades de impactos pendientes.
* **Suite de Pruebas Automatizadas al 100%:**
  * 56 tests unitarios y de integracion ejecutandose en menos de 4 segundos con JUnit 5 Jupiter, Mockito y AssertJ.
* **Persistencia y Control de Acceso (RBAC):**
  * Mapeo objeto-relacional mediante Hibernate ORM y base de datos embebida H2.
  * Roles diferenciados entre Jugador Estandar y Administrador de Sistema.

---

## 2. Arquitectura y Documentacion

Las especificaciones tecnicas completas y diagramas en formato Mermaid se encuentran detallados en:
* [modelado/ARQUITECTURA_Y_DISENO.md](modelado/ARQUITECTURA_Y_DISENO.md): Diagrama de componentes, diagrama de clases del dominio y maquina de estados de combate.
* [pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md](pruebas/PLAN_Y_REPORTE_DE_PRUEBAS.md): Matriz de cobertura y trazabilidad de los 56 tests automatizados.
* [AGENTS.md](AGENTS.md): Hoja de ruta viva (Roadmap), Definition of Done (DoD) y registro de hitos del proyecto.

---

## 3. Pila Tecnologica

* **Lenguaje:** Java 17 / 22
* **Sistema de Construccion:** Apache Maven (con Maven Wrapper `mvnw` / `mvnw.cmd` integrado)
* **Testing:** JUnit 5 Jupiter 5.10.2, Mockito 5.11, AssertJ 3.25
* **Persistencia y Base de Datos:** Hibernate 5.6.14.Final, H2 Database 2.2.220
* **Arquitectura:** Clean Architecture / Layered Architecture / Multi-Module Maven

---

## 4. Guia de Inicio Rapido

### Requisitos Previos
* Java Development Kit (JDK) 17 o superior instalado.

### Ejecucion de la Suite de Pruebas
Puedes compilar y ejecutar los 56 tests automatizados desde la raiz del proyecto sin necesidad de tener Maven instalado en el sistema operativo:

```bash
# En Windows:
.\mvnw.cmd test

# En Linux o macOS:
./mvnw test
```

### Ejecucion de la Aplicacion
Gracias a la configuracion del plugin `exec-maven-plugin`, la aplicacion se ejecuta con un unico comando:

```bash
# En Windows:
.\mvnw.cmd -pl core compile exec:java

# En Linux o macOS:
./mvnw -pl core compile exec:java
```

---

## 5. Perfiles Semilla de Evaluacion

La base de datos contiene los siguientes perfiles de prueba preconfigurados:
* **Jugador 1:** `alvaro@alumnos.upm.es` (Rol Estandar)
* **Jugador 2:** `adrian@alumnos.upm.es` (Rol Estandar)
* **Administrador:** `admin@upm.es` (Privilegios Administrativos)
