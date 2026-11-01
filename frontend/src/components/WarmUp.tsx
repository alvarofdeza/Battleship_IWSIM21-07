'use client';

import { useEffect } from 'react';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

/**
 * Componente ligero para el despertar en segundo plano del contenedor backend en Render (Cold Start Warm-Up).
 *
 * Principios operativos:
 * - Se ejecuta en tiempo ocioso del navegador (requestIdleCallback) para tener impacto cero en First Contentful Paint (FCP) y LCP.
 * - Registra en sessionStorage una marca de tiempo con TTL de 10 minutos para evitar peticiones repetitivas en la misma sesion.
 * - Lanza un GET silencioso al endpoint /healthz (el cual no toca la base de datos Neon).
 */
export const WarmUp: React.FC = () => {
  useEffect(() => {
    if (typeof window === 'undefined') return;

    const WARMUP_KEY = 'fleetgrid_warmup_timestamp';
    const WARMUP_TTL_MS = 10 * 60 * 1000; // 10 minutos

    try {
      const lastWarmup = sessionStorage.getItem(WARMUP_KEY);
      if (lastWarmup) {
        const elapsed = Date.now() - parseInt(lastWarmup, 10);
        if (elapsed < WARMUP_TTL_MS) {
          return;
        }
      }
    } catch {
      // Si sessionStorage no esta disponible (ej. modo privado estricto), continuar con el ping
    }

    const triggerPing = () => {
      // El endpoint healthz reside en el servidor raiz (sin prefijo /api/v1)
      const healthzUrl = API_BASE.replace(/\/api\/v1\/?$/, '') + '/healthz';

      fetch(healthzUrl, {
        method: 'GET',
        cache: 'no-store',
        mode: 'cors',
      })
        .then(() => {
          try {
            sessionStorage.setItem(WARMUP_KEY, Date.now().toString());
          } catch {
            // Ignorar errores de almacenamiento local
          }
        })
        .catch(() => {
          // Fallo silencioso si la conexion aun esta inicializandose
        });
    };

    if ('requestIdleCallback' in window) {
      window.requestIdleCallback(() => triggerPing(), { timeout: 3000 });
    } else {
      setTimeout(triggerPing, 1000);
    }
  }, []);

  return null;
};
