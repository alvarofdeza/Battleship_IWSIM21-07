'use client';

import React from 'react';
import { Analytics } from '@vercel/analytics/next';
import { SpeedInsights } from '@vercel/speed-insights/next';

/**
 * Componente modular para la captura desacoplada de metricas y rendimiento web (Vercel Metrics).
 *
 * Implementado bajo el principio de desacoplamiento e inmunidad contra vendor lock-in:
 * - Solo se monta e inicializa si la variable publica NEXT_PUBLIC_ENABLE_VERCEL_METRICS tiene el valor estricto 'true'.
 * - En cualquier otro caso (entornos locales, tests o ejecucion en otras nubes), retorna null sin inyectar scripts en el DOM ni consumir ancho de banda o peticiones.
 * - Para migrar a otra solucion (Umami, Cloudflare Web Analytics, Plausible), unicamente se debe sustituir el contenido de este archivo sin tocar el resto de la plataforma.
 *
 * @returns Fragmento con Analytics y SpeedInsights si esta habilitado, o null en caso contrario.
 */
export const WebMetrics: React.FC = () => {
  const isMetricsEnabled = process.env.NEXT_PUBLIC_ENABLE_VERCEL_METRICS === 'true';

  if (!isMetricsEnabled) {
    return null;
  }

  return (
    <>
      <Analytics />
      <SpeedInsights />
    </>
  );
};
