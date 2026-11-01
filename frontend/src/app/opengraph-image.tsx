import { ImageResponse } from 'next/og';

export const alt = 'FleetGrid // Tactical Naval Command';
export const size = {
  width: 1200,
  height: 630,
};
export const contentType = 'image/png';

/**
 * Generador dinamico de la imagen Open Graph para tarjetas de vista previa en redes sociales.
 */
export default async function Image() {
  return new ImageResponse(
    (
      <div
        style={{
          height: '100%',
          width: '100%',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: '#050b14',
          fontFamily: 'monospace',
          border: '16px solid #1a2d4f',
          padding: '40px',
        }}
      >
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '16px',
            marginBottom: '24px',
          }}
        >
          <div
            style={{
              width: '28px',
              height: '28px',
              borderRadius: '50%',
              backgroundColor: '#00ff66',
              boxShadow: '0 0 20px #00ff66',
            }}
          />
          <div
            style={{
              fontSize: '64px',
              fontWeight: 900,
              letterSpacing: '8px',
              color: '#00ff66',
            }}
          >
            FLEETGRID
          </div>
        </div>

        <div
          style={{
            fontSize: '24px',
            letterSpacing: '4px',
            color: '#00e5ff',
            marginBottom: '20px',
          }}
        >
          TACTICAL NAVAL COMMAND // CIC-95
        </div>

        <div
          style={{
            fontSize: '18px',
            color: '#8fa5bf',
            maxWidth: '850px',
            textAlign: 'center',
            lineHeight: 1.6,
          }}
        >
          Pure Domain Naval Simulation &bull; Probability Density Heatmap AI &bull; Spring Boot 3 &bull; Next.js 16
        </div>

        <div
          style={{
            marginTop: '40px',
            padding: '12px 28px',
            border: '2px solid #00ff66',
            color: '#00ff66',
            fontSize: '16px',
            letterSpacing: '3px',
          }}
        >
          DOMAIN: FLEETGRID.ME // STATUS: OPERATIONAL
        </div>
      </div>
    ),
    {
      ...size,
    }
  );
}
