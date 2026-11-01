import { MetadataRoute } from 'next';

/**
 * Generador dinamico del mapa del sitio sitemap.xml para indexacion en buscadores.
 *
 * @returns Lista de rutas publicas canónicas con sus prioridades de indexacion.
 */
export default function sitemap(): MetadataRoute.Sitemap {
  const baseUrl = 'https://fleetgrid.me';
  const now = new Date();

  return [
    {
      url: baseUrl,
      lastModified: now,
      changeFrequency: 'daily',
      priority: 1.0,
    },
    {
      url: `${baseUrl}/leaderboard`,
      lastModified: now,
      changeFrequency: 'hourly',
      priority: 0.9,
    },
    {
      url: `${baseUrl}/rules`,
      lastModified: now,
      changeFrequency: 'weekly',
      priority: 0.8,
    },
    {
      url: `${baseUrl}/faq`,
      lastModified: now,
      changeFrequency: 'weekly',
      priority: 0.7,
    },
    {
      url: `${baseUrl}/privacy`,
      lastModified: now,
      changeFrequency: 'monthly',
      priority: 0.5,
    },
  ];
}
