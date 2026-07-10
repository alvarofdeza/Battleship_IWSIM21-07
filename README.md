# Battleship Game - Enterprise Software Engineering

A modular Java implementation of the classic Battleship game, developed for the Software Engineering curriculum at Universidad Politécnica de Madrid (UPM - ETSISI). The project focuses on Component-Based Software Architecture and strict separation of concerns.

## Tech Stack & Environment
* **Language:** Java 17
* **Build System:** Maven
* **Architecture:** Layered & Component-Based Architecture (Core module: `construccion`)
* **Dependency Management:** Multi-source resolution handling local artifacts via `/libs`

## Project Structure
* `/construccion`: Primary source code, business logic, and UI/Controller layers.
* `/libs`: Local repository housing custom dependencies (`etsisi2:externals`).
* `pom.xml`: Root Maven configuration managing build lifecycles.

## Key Features
* **Game Simulation:** Turn-based multiplayer state machinery with coordinate validation.
* **Role-Based Access Control (RBAC):** Distinct privileges separating standard players from administrators.
* **Administrative Analytics:** Admin accounts can monitor global system metrics and player logs.

## Sandbox Accounts
The environment contains three seeded profiles for evaluation purposes:
* **Standard Player 1:** alvaro@alumnos.upm.es (Non-admin, 26 points)
* **Standard Player 2:** adrian@alumnos.upm.es (Non-admin, 58 points)
* **System Administrator:** admin@upm.es (Full Admin Privileges)

Note: If downloading the project as a .zip archive, the local embedded database will initialize empty.

## Build and Installation

### Prerequisites
* Java Development Kit (JDK) 17
* Apache Maven 3.8+

### Local Setup
1. Clone the repository:
   ```bash
   git clone [https://github.com/alvarofdeza/Battleship_IWSIM21-07.git](https://github.com/alvarofdeza/Battleship_IWSIM21-07.git)
