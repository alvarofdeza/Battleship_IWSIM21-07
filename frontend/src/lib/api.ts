import { AuthResponse, GameSummary, MoveHistoryItem, ScoreItem, ShotResponse, UserProfile } from './types';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

/**
 * Cliente HTTP para interactuar con la API REST de Spring Boot.
 * Gestiona de forma centralizada los tokens de autenticacion JWT Bearer.
 */
class ApiClient {
  private token: string | null = null;

  constructor() {
    if (typeof window !== 'undefined') {
      this.token = localStorage.getItem('battleship_jwt_token');
    }
  }

  /**
   * Almacena o elimina el token JWT en memoria y en el almacenamiento local del navegador.
   *
   * @param token Cadena con el token Bearer o null para limpiar la sesion.
   */
  public setToken(token: string | null) {
    this.token = token;
    if (typeof window !== 'undefined') {
      if (token) {
        localStorage.setItem('battleship_jwt_token', token);
      } else {
        localStorage.removeItem('battleship_jwt_token');
      }
    }
  }

  /**
   * Recupera el token JWT actual de la sesion.
   *
   * @returns El token Bearer vigente o null si no se ha iniciado sesion.
   */
  public getToken(): string | null {
    if (!this.token && typeof window !== 'undefined') {
      this.token = localStorage.getItem('battleship_jwt_token');
    }
    return this.token;
  }

  /**
   * Ejecuta peticiones fetch con inyeccion automatica de cabeceras de autorizacion y gestion de errores.
   *
   * @param endpoint Ruta relativa del recurso REST.
   * @param options Opciones de configuracion de la peticion HTTP.
   * @returns Promesa con los datos deserializados del tipo solicitado.
   */
  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...(options.headers as Record<string, string>),
    };

    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE}${endpoint}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      let errorMessage = `HTTP Error ${response.status}: ${response.statusText}`;

      // Manejo intuitivo y especifico para accesos denegados por falta de autenticacion
      if (response.status === 403 || response.status === 401) {
        errorMessage = 'OFFICER CLEARANCE REQUIRED: PLEASE SIGN IN TO PROCEED';
      } else {
        try {
          const errorJson = await response.json();
          if (errorJson.message) {
            errorMessage = errorJson.message;
          }
        } catch {
          // Utilizar mensaje por defecto si la respuesta no contiene JSON
        }
      }
      throw new Error(errorMessage);
    }

    if (response.status === 204) {
      return {} as T;
    }

    return response.json();
  }

  // --- Endpoints de Autenticacion ---

  /**
   * Inicia sesion en el backend mediante nombre de usuario y contraseña.
   *
   * @param username Nombre de usuario o correo electronico.
   * @param password Contraseña en texto plano.
   * @returns Datos de sesion y token JWT generado.
   */
  public async login(username: string, password: string): Promise<AuthResponse> {
    const data = await this.request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
    this.setToken(data.token);
    return data;
  }

  /**
   * Registra una nueva cuenta de jugador en el servidor.
   *
   * @param username Nombre de usuario unico.
   * @param email Correo electronico institucional o de contacto.
   * @param password Contraseña de seguridad.
   * @returns Datos de la cuenta creada y token inicial.
   */
  public async register(username: string, email: string, password: string): Promise<AuthResponse> {
    const data = await this.request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password }),
    });
    this.setToken(data.token);
    return data;
  }

  /**
   * Obtiene la informacion del perfil del oficial autenticado.
   *
   * @returns Datos del usuario actual segun el token Bearer.
   */
  public async getMe(): Promise<UserProfile> {
    return this.request<UserProfile>('/auth/me');
  }

  /**
   * Cierra la sesion activa eliminando el token JWT.
   */
  public logout() {
    this.setToken(null);
  }

  // --- Endpoints del Juego ---

  /**
   * Crea una nueva partida en solitario contra la Inteligencia Artificial.
   *
   * @param aiDifficulty Nivel de dificultad algoritmica (EASY, NORMAL, HARD).
   * @returns Resumen de la partida con el identificador y tableros iniciales.
   */
  public async createGame(aiDifficulty: string = 'NORMAL'): Promise<GameSummary> {
    return this.request<GameSummary>('/games', {
      method: 'POST',
      body: JSON.stringify({ aiDifficulty }),
    });
  }

  /**
   * Consulta el estado actual de una partida con niebla de guerra.
   *
   * @param gameId Identificador alfanumerico de la partida.
   * @returns Resumen del estado actual del combate.
   */
  public async getGame(gameId: string): Promise<GameSummary> {
    return this.request<GameSummary>(`/games/${gameId}`);
  }

  /**
   * Ejecuta un disparo tactico sobre las coordenadas indicadas del tablero enemigo.
   *
   * @param gameId Identificador de la partida.
   * @param row Numero de fila (0-9).
   * @param col Numero de columna (0-9).
   * @returns Resultado del disparo propio y del contraataque automatico de la IA.
   */
  public async fireShot(gameId: string, row: number, col: number): Promise<ShotResponse> {
    return this.request<ShotResponse>(`/games/${gameId}/shots`, {
      method: 'POST',
      body: JSON.stringify({ row, col }),
    });
  }

  /**
   * Recupera la cronologia completa de disparos efectuados durante la partida.
   *
   * @param gameId Identificador de la partida.
   * @returns Lista de movimientos ordenados cronologicamente.
   */
  public async getGameHistory(gameId: string): Promise<MoveHistoryItem[]> {
    return this.request<MoveHistoryItem[]>(`/games/${gameId}/history`);
  }

  // --- Endpoints de Clasificaciones ---

  /**
   * Consulta el historial de las diez mejores puntuaciones del oficial en sesion.
   *
   * @returns Lista de puntuaciones personales.
   */
  public async getPersonalLeaderboard(): Promise<ScoreItem[]> {
    return this.request<ScoreItem[]>('/leaderboard/personal');
  }

  /**
   * Consulta el ranking global de honor con las maximas puntuaciones registradas.
   *
   * @returns Lista global de mejores marcas navales.
   */
  public async getGlobalLeaderboard(): Promise<ScoreItem[]> {
    return this.request<ScoreItem[]>('/leaderboard/global');
  }
}

export const api = new ApiClient();
