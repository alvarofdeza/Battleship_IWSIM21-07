# Arquitectura y Diseño Tecnico - Battleship Platform

Este documento actualiza y consolida las especificaciones tecnicas de arquitectura, modelado del dominio y patrones de diseno aplicados en la plataforma, sustituyendo los diagramas estaticos previos mediante especificaciones en formato Mermaid.

---

## 1. Diagrama de Componentes (Clean Architecture)

El sistema estructura sus responsabilidades siguiendo principios de Clean Architecture y separacion de incumbencias.

```mermaid
flowchart TD
    subgraph PresentationLayer ["Capa de Presentacion / UI"]
        GV["GameView (Consola)"]
        AV["AuthView / UsersView (Swing)"]
    end

    subgraph ControllerLayer ["Capa de Controladores / Aplicacion"]
        GC["GameController"]
        UC["UserController"]
        AC["AuthController"]
        DC["DataController"]
    end

    subgraph DomainLayer ["Capa de Dominio Core"]
        GAME["Game (State Machine)"]
        BOARD["Board (10x10 Grid)"]
        CELL["Cell"]
        FLEET["Ships (Carrier, Battleship, Submarine, PatrolBoat)"]
        AI_STRAT["AiAlgorithm (Strategy Pattern)"]
        BASIC["BasicAlgorithm"]
        NORMAL["NormalAlgorithm (Hunt & Target)"]
        EXPERT["ExpertAlgorithm (Probability Density Heatmap)"]
    end

    subgraph InfrastructureLayer ["Capa de Persistencia e Infraestructura"]
        ORM["Hibernate Session & Transactions"]
        H2[(Base de Datos H2)]
    end

    PresentationLayer --> ControllerLayer
    ControllerLayer --> DomainLayer
    ControllerLayer --> InfrastructureLayer
    InfrastructureLayer --> H2
    AI_STRAT --> BASIC
    AI_STRAT --> NORMAL
    AI_STRAT --> EXPERT
    GAME --> BOARD
    BOARD --> CELL
    BOARD --> FLEET
```

---

## 2. Diagrama de Clases del Dominio

Representacion de la jerarquia de naves, participantes y contratos del dominio.

```mermaid
classDiagram
    class IPlayer {
        <<interface>>
        +takeTurn(char[][] enemyBoardView) int[]
        +getName() String
        +addMovement(IMovement m)
        +addScore(IScore s)
        +acceptSpecialAction(ShipSpecialAction action, int uses) boolean
    }

    class User {
        -String id
        -String name
        -boolean isAdmin
        -List~Movement~ movements
        -List~Score~ scores
    }

    class AiPlayer {
        -String name
        -Difficulty difficulty
        -AiAlgorithm algorithm
        +takeTurn(char[][] enemyBoardView) int[]
    }

    class Ship {
        <<abstract>>
        #int length
        #int hitsTaken
        #boolean alive
        #List~int[]~ positions
        +isAlive() boolean
        +registerHit()
        +sink()
        +restoreOneHit()
        +getName()* String
        +executeSpecialAction(Game g)*
    }

    class Carrier {
        +Carrier()
        +getName() String
    }

    class Battleship {
        -int availableArtilleryShots
        +reduceArtilleryShots()
        +getAvailableArtilleryShots() int
    }

    class Submarine {
        -boolean repaired
        +markRepaired()
        +isRepaired() boolean
    }

    class PatrolBoat {
        -boolean evasionActive
        +activateEvasion()
        +isEvasionActive() boolean
    }

    class IBattleship {
        <<interface>>
        +getAvailableArtilleryShots() int
        +reduceArtilleryShots()
    }

    class ISubmarine {
        <<interface>>
        +markRepaired()
        +isRepaired() boolean
    }

    class IPatrolBoat {
        <<interface>>
        +activateEvasion()
        +isEvasionActive() boolean
    }

    IPlayer <|.. User
    IPlayer <|.. AiPlayer
    Ship <|-- Carrier
    Ship <|-- Battleship
    Ship <|-- Submarine
    Ship <|-- PatrolBoat
    IBattleship <|.. Battleship
    ISubmarine <|.. Submarine
    IPatrolBoat <|.. PatrolBoat
```

---

## 3. Maquina de Estados de Partida e Impactos

```mermaid
stateDiagram-v2
    [*] --> Initialization : Despliegue de flota (Board.placeRandomFleet)
    Initialization --> TurnPlayer1 : Flota lista
    
    state TurnPlayer1 {
        [*] --> AwaitCoordinates1
        AwaitCoordinates1 --> EvaluateShot1 : [row, col]
        EvaluateShot1 --> Miss1 : Casilla vacia ('M')
        EvaluateShot1 --> Hit1 : Nave alcanzada viva ('H')
        EvaluateShot1 --> Sunk1 : Ultima casilla de nave ('S')
    }

    TurnPlayer1 --> CheckVictory1
    CheckVictory1 --> GameOver : Flota defensora totalmente destruida
    CheckVictory1 --> TurnPlayer2 : Quedan naves vivas

    state TurnPlayer2 {
        [*] --> ComputeAiShot : AiAlgorithm.getNextAttack()
        ComputeAiShot --> EvaluateShot2
        EvaluateShot2 --> Miss2 : Casilla vacia ('M')
        EvaluateShot2 --> Hit2 : Nave alcanzada viva ('H')
        EvaluateShot2 --> Sunk2 : Nave hundida ('S')
    }

    TurnPlayer2 --> CheckVictory2
    CheckVictory2 --> GameOver : Flota defensora totalmente destruida
    CheckVictory2 --> TurnPlayer1 : Quedan naves vivas

    GameOver --> [*] : Asignacion de puntuacion final e historial
```

---

## 4. Estrategias Algoritmicas de Inteligencia Artificial

```mermaid
flowchart TD
    Start([Turno de la IA]) --> CheckDiff{Nivel de Dificultad}

    subgraph Facil ["BasicAlgorithm (Facil)"]
        CheckDiff -->|EASY| RandomPick["Seleccion pseudoaleatoria entre celdas inexploradas '?'"]
    end

    subgraph Normal ["NormalAlgorithm (Hunt & Target)"]
        CheckDiff -->|NORMAL| CheckHits{¿Existen impactos 'H' sin hundir?}
        CheckHits -->|Si: Modo Target| Orthogonal["Exploracion de vecinos adyacentes (N, S, E, O) o extremos de linea"]
        CheckHits -->|No: Modo Hunt| Checkerboard["Disparo en casillas de paridad (row + col) % 2 == 0"]
    end

    subgraph Experto ["ExpertAlgorithm (Probability Density Heatmap)"]
        CheckDiff -->|HARD| SimulateShips["Simulacion de todas las proyecciones validas de naves restantes"]
        SimulateShips --> FilterCollisions["Descarte de colisiones con fallos 'M' o naves hundidas 'S'"]
        FilterCollisions --> WeightHits["Ponderacion masiva x50 en proyecciones que tocan impactos 'H'"]
        WeightHits --> HeatmapMax["Seleccion de celda '?' con mayor valor acumulado en el heatmap"]
    end

    RandomPick --> Fire([Ejecutar disparo])
    Orthogonal --> Fire
    Checkerboard --> Fire
    HeatmapMax --> Fire
```

---

## 5. Arquitectura Backend Enterprise (Spring Boot 3 & REST API)

El modulo `battleship-backend` implementa una arquitectura desacoplada basada en capas de servicios, repositorios reactivos/JPA y seguridad sin estado (stateless) mediante JWT.

```mermaid
flowchart TD
    subgraph ClientLayer ["Clientes Externos"]
        SWAGGER["Swagger UI / OpenAPI Console"]
        SPA["Frontend SPA (React / Next.js)"]
        TESTS["MockMvc Test Suites"]
    end

    subgraph SecurityBoundary ["Filtro de Seguridad Spring Security"]
        JWT_FILTER["JwtAuthenticationFilter"]
        JWT_SERVICE["JwtService (HMAC-SHA256)"]
        AUTH_MGR["AuthenticationManager / DaoAuthenticationProvider"]
    end

    subgraph RestControllers ["Capa de Controladores REST (@RestController)"]
        AC_REST["AuthController (/api/v1/auth)"]
        GC_REST["GameController (/api/v1/games)"]
        LC_REST["LeaderboardController (/api/v1/leaderboard)"]
        EX_ADVICE["GlobalExceptionHandler (@RestControllerAdvice)"]
    end

    subgraph BusinessServices ["Capa de Negocio (@Service)"]
        AUTH_SRV["AuthService"]
        GAME_SRV["GameService"]
        LEAD_SRV["LeaderboardService"]
    end

    subgraph CoreEngineModule ["Motor de Dominio (battleship-core)"]
        CORE_GAME["Game / Board / Ships / AiPlayer"]
    end

    subgraph DataRepositories ["Capa de Datos (@Repository - Spring Data JPA)"]
        USER_REPO["UserRepository"]
        GAME_REPO["GameRecordRepository"]
        MOVE_REPO["MoveRecordRepository"]
        LEAD_REPO["LeaderboardScoreRepository"]
    end

    subgraph DatabaseLayer ["Base de Datos"]
        H2_DB[(H2 In-Memory DB / Dev & Tests)]
        PG_DB[(PostgreSQL / Production & Docker)]
    end

    ClientLayer -->|Bearer JWT en Header| JWT_FILTER
    JWT_FILTER --> JWT_SERVICE
    JWT_FILTER -->|SecurityContext| RestControllers
    RestControllers --> BusinessServices
    BusinessServices --> CoreEngineModule
    BusinessServices --> DataRepositories
    DataRepositories --> H2_DB
    DataRepositories --> PG_DB
    AUTH_SRV --> AUTH_MGR
```

---

## 6. Modelo Entidad-Relacion (Spring Data JPA)

```mermaid
erDiagram
    USERS ||--o{ GAME_RECORDS : plays
    USERS ||--o{ LEADERBOARD_SCORES : achieves
    GAME_RECORDS ||--o{ MOVE_RECORDS : logs

    USERS {
        Long id PK
        String username UK
        String email UK
        String password
        String role
        LocalDateTime createdAt
    }

    GAME_RECORDS {
        String gameId PK
        String player1Username
        String player2Username
        String gameMode
        String aiDifficulty
        String status
        String winnerUsername
        LocalDateTime createdAt
        LocalDateTime finishedAt
    }

    MOVE_RECORDS {
        Long id PK
        String gameId FK
        String playerUsername
        int rowNumber
        int colNumber
        String result
        LocalDateTime timestamp
    }

    LEADERBOARD_SCORES {
        Long id PK
        String playerUsername FK
        Long points
        String gameId
        LocalDateTime achievedAt
    }
```

---

## 7. Catalogo de Endpoints REST (OpenAPI 3 / Swagger)

| Metodo | Ruta | Descripcion | Autenticacion |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Registro de nuevo jugador | Publico |
| `POST` | `/api/v1/auth/login` | Inicio de sesion y obtencion de Bearer JWT | Publico |
| `GET` | `/api/v1/auth/me` | Consulta de perfil del usuario en sesion | Bearer JWT |
| `POST` | `/api/v1/games` | Creacion de nueva partida vs IA (EASY, NORMAL, HARD) | Bearer JWT |
| `GET` | `/api/v1/games/{gameId}` | Estado de partida con niebla de guerra para el rival | Bearer JWT |
| `POST` | `/api/v1/games/{gameId}/shots` | Disparo humano y contraataque reactivo de IA | Bearer JWT |
| `GET` | `/api/v1/games/{gameId}/history` | Trazabilidad completa de disparos de la partida | Bearer JWT |
| `GET` | `/api/v1/leaderboard/personal` | Top 10 puntuaciones individuales del usuario | Bearer JWT |
| `GET` | `/api/v1/leaderboard/global` | Ranking global con los mejores resultados | Bearer JWT |
| `GET` | `/swagger-ui.html` | Interfaz interactiva de documentacion OpenAPI | Publico |
| `GET` | `/v3/api-docs` | Especificacion JSON OpenAPI 3.0 | Publico |
| `GET` | `/h2-console` | Consola web de administracion de base de datos H2 | Publico (Dev) |

---

## 8. Arquitectura Frontend Web (Next.js 16, React 19, Tailwind CSS)

El modulo `frontend/` implementa una interfaz de usuario reactiva, responsive y de estetica militar retro CRT ("Combat Information Center"), conectada directamente con la API REST del backend mediante JWT.

```mermaid
flowchart TD
    subgraph Browser ["Navegador del Usuario (Desktop / Mobile)"]
        UI["Pagina Principal (page.tsx)"]
        CRT["Filtro CRT Scanlines & Screen Shake"]
        AUDIO["Sintetizador Web Audio API (sound.ts)"]
    end

    subgraph StateMachine ["Maquina de Estados del Cliente"]
        DEP["Fase DEPLOYMENT (FleetPlacement.tsx)"]
        COMB["Fase COMBAT (CombatGrid.tsx & TelemetryLog.tsx)"]
        OVER["Fase GAMEOVER (Resumen de Bajas y Citacion)"]
    end

    subgraph Modals ["Componentes Modales de Gestion"]
        AUTH_M["AuthModal.tsx (Login / Registro / Guardia 403)"]
        LEAD_M["LeaderboardModal.tsx (Admiralty Roll)"]
    end

    subgraph ApiClient ["Capa de Comunicacion HTTP (api.ts)"]
        FETCH["Fetch API Wrapper con Bearer Token"]
        STORAGE["Session / Local Storage (JWT)"]
    end

    subgraph BackendRestApi ["Backend REST API (battleship-backend)"]
        SPRING_API["Controladores Spring Boot 3 (:8080)"]
    end

    UI --> CRT
    UI --> AUDIO
    UI --> StateMachine
    UI --> Modals
    DEP -->|Valida credenciales & Naves| ApiClient
    COMB -->|Envio de disparos y telemetria| ApiClient
    AUTH_M -->|Autenticacion HMAC-SHA256| ApiClient
    LEAD_M -->|Consulta de records| ApiClient
    ApiClient --> STORAGE
    ApiClient -->|Peticiones HTTP JSON| SPRING_API
```
