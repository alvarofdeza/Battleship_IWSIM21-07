import { MetadataRoute } from 'next';

/**
 * Generador dinamico de directivas de rastreo robots.txt para motores de busqueda.
 *
 * @returns Configuracion estandar de robots.
 */
export default function robots(): MetadataRoute.Robots {
  return {
    rules: [
      {
        userAgent: '*',
        allow: '/',
        disallow: ['/api/'],
      },
    ],
    sitemap: 'https://fleetgrid.me/sitemap.xml',
  };
}
