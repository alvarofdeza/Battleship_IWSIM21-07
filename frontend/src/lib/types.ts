/**
 * Niveles de dificultad admitidos por los algoritmos de Inteligencia Artificial.
 */
export type AiDifficulty = 'EASY' | 'NORMAL' | 'HARD';

/**
 * Tipos de buques de guerra disponibles en el despliegue de flota.
 */
export type ShipType = 'CARRIER' | 'BATTLESHIP' | 'SUBMARINE' | 'PATROL_BOAT';

/**
 * Definicion de caracteristicas tecnicas de cada tipo de barco.
 */
export interface ShipDefinition {
  type: ShipType;
  name: string;
  size: number;
  letter: string;
}

/**
 * Manifiesto estandar de los cuatro buques reglamentarios de la marina de combate.
 */
export const FLEET_SHIPS: ShipDefinition[] = [
  { type: 'CARRIER', name: 'Carrier', size: 5, letter: 'A' },
  { type: 'BATTLESHIP', name: 'Battleship', size: 4, letter: 'B' },
  { type: 'SUBMARINE', name: 'Submarine', size: 3, letter: 'S' },
  { type: 'PATROL_BOAT', name: 'Patrol Boat', size: 2, letter: 'D' },
];

/**
 * Representacion de un barco posicionado sobre la cuadricula tactica del jugador.
 */
export interface PlacedShip {
  type: ShipType;
  row: number;
  col: number;
  size: number;
  horizontal: boolean;
  hits: number;
  sunk: boolean;
}

/**
 * Datos del perfil del oficial autenticado devueltos por el endpoint /api/v1/auth/me.
 */
export interface UserProfile {
  id?: number;
  username: string;
  email: string;
  role: string;
}

/**
 * Respuesta del servidor tras un inicio de sesion o registro exitoso.
 */
export interface AuthResponse {
  token: string;
  type: string;
  username: string;
  email: string;
  role: string;
}

/**
 * Resumen del estado de la partida transmitido por el backend Spring Boot.
 */
export interface GameSummary {
  gameId: string;
  player1: string;
  player2: string;
  status: 'IN_PROGRESS' | 'FINISHED';
  currentTurn?: string;
  finished: boolean;
  winner?: string | null;
  myFleetGrid: string[];
  enemyFogGrid: string[];
}

/**
 * Respuesta detallada tras la ejecucion de un disparo humano y el contraataque de la IA.
 */
export interface ShotResponse {
  gameId: string;
  playerRow: number;
  playerCol: number;
  playerShotResult: 'Hit' | 'Water' | 'Sunk' | 'Already attacked';
  aiCounterAttacked: boolean;
  aiRow?: number;
  aiCol?: number;
  aiShotResult?: string;
  finished: boolean;
  winner?: string | null;
  currentTurn?: string | null;
}

/**
 * Registro individual de disparo en el historial cronologico de la partida.
 */
export interface MoveHistoryItem {
  id: number;
  gameId: string;
  playerUsername: string;
  row: number;
  col: number;
  result: string;
  timestamp: string;
}

/**
 * Entrada en la tabla de clasificaciones y puntuaciones de honor de la marina.
 */
export interface ScoreItem {
  id: number;
  username: string;
  points: number;
  gameId: string;
  recordedAt: string;
}
