import { MetadataRoute } from 'next';

/**
 * Generador dinamico del manifiesto web PWA para instalacion y soporte en dispositivos moviles.
 *
 * @returns Metadatos del manifiesto web.
 */
export default function manifest(): MetadataRoute.Manifest {
  return {
    name: 'FleetGrid - Tactical Naval Command',
    short_name: 'FleetGrid',
    description: 'Tactical naval warfare simulation platform featuring Probability Density AI and retro CRT aesthetic.',
    start_url: '/',
    display: 'standalone',
    background_color: '#050b14',
    theme_color: '#00ff66',
    icons: [
      {
        src: '/favicon.ico',
        sizes: 'any',
        type: 'image/x-icon',
      },
    ],
  };
}
