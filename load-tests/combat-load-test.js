import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

// Metricas personalizadas de telemetria naval
const shotLatency = new Trend('naval_shot_duration');
const matchSuccessRate = new Rate('combat_success_rate');

export const options = {
  stages: [
    { duration: '15s', target: 5 },   // Rampa de subida: 5 comandantes
    { duration: '30s', target: 20 },  // Carga sostenida: 20 comandantes simultaneos
    { duration: '15s', target: 0 },   // Rampa de bajada
  ],
  thresholds: {
    // El 95% de las peticiones debe completarse en menos de 200 ms
    http_req_duration: ['p(95)<200'],
    // Menos del 1% de peticiones fallidas
    http_req_failed: ['rate<0.01'],
    // Tasa de exito en combate > 99%
    combat_success_rate: ['rate>0.99'],
  },
};

const BASE_URL = __ENV.TARGET_URL || 'http://localhost:8080/api/v1';

/**
 * Escenario de combate naval: simula a un comandante jugando una batalla completa
 * disparando proyectiles cada 2 segundos hasta la destruccion de una flota.
 */
export default function () {
  const headers = { 'Content-Type': 'application/json' };

  // 1. Despliegue de partida (Guest o Autenticado)
  const createPayload = JSON.stringify({ aiDifficulty: 'NORMAL' });
  const createRes = http.post(`${BASE_URL}/games`, createPayload, { headers });

  const createSuccess = check(createRes, {
    'partida creada con exito (HTTP 201)': (r) => r.status === 201,
    'gameId presente en respuesta': (r) => JSON.parse(r.body).gameId !== undefined,
  });

  if (!createSuccess) {
    matchSuccessRate.add(false);
    return;
  }

  const game = JSON.parse(createRes.body);
  const gameId = game.gameId;

  let isFinished = false;
  let row = 0;
  let col = 0;
  let turns = 0;
  const maxTurns = 50; // Limite defensivo para no prolongar artificialmente la prueba

  // 2. Bucle de disparo de artilleria naval
  while (!isFinished && turns < maxTurns) {
    const shotStart = Date.now();
    const shotPayload = JSON.stringify({ row, col });

    const shotRes = http.post(`${BASE_URL}/games/${gameId}/shots`, shotPayload, { headers });
    shotLatency.add(Date.now() - shotStart);

    const shotCheck = check(shotRes, {
      'disparo procesado en un solo viaje (HTTP 200)': (r) => r.status === 200,
      'tableros sincronizados en respuesta': (r) => {
        const body = JSON.parse(r.body);
        return body.myFleetGrid !== undefined && body.enemyFogGrid !== undefined;
      },
    });

    if (!shotCheck) {
      matchSuccessRate.add(false);
      break;
    }

    const shotData = JSON.parse(shotRes.body);
    isFinished = shotData.finished;

    // Avanzar coordenadas
    col++;
    if (col >= 10) {
      col = 0;
      row++;
      if (row >= 10) row = 0;
    }
    turns++;

    // Pausa militar reglamentaria entre salvas de artilleria (2 segundos)
    sleep(2);
  }

  matchSuccessRate.add(true);
}
