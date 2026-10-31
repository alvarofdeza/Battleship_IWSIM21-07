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
