import {
  AuthResponse,
  GameSummary,
  MatchScoreItem,
  MoveHistoryItem,
  PlayerRankingItem,
  ScoreItem,
  ShotResponse,
  UserProfile,
  UserStats,
} from './types';

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

    let slowTimer: ReturnType<typeof setTimeout> | null = null;
    if (typeof window !== 'undefined') {
      slowTimer = setTimeout(() => {
        window.dispatchEvent(new CustomEvent('fleetgrid:coldstart', { detail: { isSlow: true } }));
      }, 3000);
    }

    let response: Response;
    try {
      response = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers,
      });
    } finally {
      if (slowTimer) {
        clearTimeout(slowTimer);
      }
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new CustomEvent('fleetgrid:coldstart', { detail: { isSlow: false } }));
      }
    }

    if (!response.ok) {
      let errorMessage = `HTTP Error ${response.status}: ${response.statusText}`;

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
   * Inicia sesion en el backend mediante correo electronico y contrasena.
   */
  public async login(email: string, password: string): Promise<AuthResponse> {
    const data = await this.request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
    this.setToken(data.token);
    return data;
  }

  /**
   * Registra una nueva cuenta de jugador en el servidor y despacha el correo de verificacion OTP.
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
   * Verifica el correo del usuario mediante el PIN militar de 6 digitos.
   */
  public async verifyEmail(identifier: string, code: string): Promise<AuthResponse> {
    const data = await this.request<AuthResponse>('/auth/verify-email', {
      method: 'POST',
      body: JSON.stringify({ identifier, code }),
    });
    this.setToken(data.token);
    return data;
  }

  /**
   * Solicita el reenvio de un nuevo PIN militar de 6 digitos.
   */
  public async resendCode(identifier: string): Promise<void> {
    await this.request<void>('/auth/resend-code', {
      method: 'POST',
      body: JSON.stringify({ identifier }),
    });
  }

  /**
   * Solicita el restablecimiento de contrasena despachando un PIN de 6 digitos al correo.
   */
  public async forgotPassword(identifier: string): Promise<{ message: string }> {
    return this.request<{ message: string }>('/auth/forgot-password', {
      method: 'POST',
      body: JSON.stringify({ identifier }),
    });
  }

  /**
   * Restablece la contrasena del oficial tras validar el PIN militar de 6 digitos.
   */
  public async resetPassword(identifier: string, code: string, newPassword: string): Promise<{ message: string }> {
    return this.request<{ message: string }>('/auth/reset-password', {
      method: 'POST',
      body: JSON.stringify({ identifier, code, newPassword }),
    });
  }

  /**
   * Actualiza la contrasena del oficial tras validar la credencial actual.
   */
  public async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await this.request<void>('/auth/password', {
      method: 'PUT',
      body: JSON.stringify({ currentPassword, newPassword }),
    });
  }

  /**
   * Obtiene la informacion del perfil del oficial autenticado.
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
   */
  public async createGame(aiDifficulty: string = 'NORMAL'): Promise<GameSummary> {
    return this.request<GameSummary>('/games', {
      method: 'POST',
      body: JSON.stringify({ aiDifficulty }),
    });
  }

  /**
   * Consulta el estado actual de una partida con niebla de guerra.
   */
  public async getGame(gameId: string): Promise<GameSummary> {
    return this.request<GameSummary>(`/games/${gameId}`);
  }

  /**
   * Ejecuta un disparo tactico sobre las coordenadas indicadas del tablero enemigo.
   */
  public async fireShot(gameId: string, row: number, col: number): Promise<ShotResponse> {
    return this.request<ShotResponse>(`/games/${gameId}/shots`, {
      method: 'POST',
      body: JSON.stringify({ row, col }),
    });
  }

  /**
   * Reclama una partida finalizada como invitado vinculandola a la cuenta autenticada.
   */
  public async claimGame(gameId: string): Promise<MatchScoreItem> {
    return this.request<MatchScoreItem>(`/games/${gameId}/claim`, {
      method: 'POST',
    });
  }

  // --- Endpoints de Clasificaciones y Expedientes ---

  /**
   * Top 10 mejores partidas individuales disputadas en la plataforma.
   */
  public async getTopMatches(): Promise<MatchScoreItem[]> {
    return this.request<MatchScoreItem[]>('/leaderboard/matches');
  }

  /**
   * Top 10 oficiales de la marina por suma total de puntos acumulados.
   */
  public async getTopPlayers(): Promise<PlayerRankingItem[]> {
    return this.request<PlayerRankingItem[]>('/leaderboard/players');
  }

  /**
   * Top 10 mejores partidas del oficial autenticado.
   */
  public async getMyMatches(): Promise<MatchScoreItem[]> {
    return this.request<MatchScoreItem[]>('/leaderboard/me/matches');
  }

  /**
   * Ultimas 10 partidas disputadas por el oficial autenticado.
   */
  public async getMyRecentMatches(): Promise<MatchScoreItem[]> {
    return this.request<MatchScoreItem[]>('/leaderboard/me/recent');
  }

  /**
   * Balance de carrera y rango naval del oficial autenticado.
   */
  public async getMyStats(): Promise<UserStats> {
    return this.request<UserStats>('/leaderboard/me/stats');
  }

  /**
   * Consulta compatible con versiones anteriores del top personal.
   */
  public async getPersonalLeaderboard(): Promise<ScoreItem[]> {
    return this.request<ScoreItem[]>('/leaderboard/personal');
  }

  /**
   * Consulta compatible con versiones anteriores del top global.
   */
  public async getGlobalLeaderboard(): Promise<ScoreItem[]> {
    return this.request<ScoreItem[]>('/leaderboard/global');
  }
}

export const api = new ApiClient();
