import type { Metadata } from 'next';
import { Press_Start_2P, Share_Tech_Mono } from 'next/font/google';
import { Toaster } from 'sonner';
import { Footer } from '@/components/Footer';
import './globals.css';

const pixelFont = Press_Start_2P({
  weight: '400',
  subsets: ['latin'],
  variable: '--font-pixel',
  display: 'swap',
});

const tacticalFont = Share_Tech_Mono({
  weight: '400',
  subsets: ['latin'],
  variable: '--font-tactical',
  display: 'swap',
});

export const metadata: Metadata = {
  metadataBase: new URL('https://fleetgrid.me'),
  title: {
    default: 'FleetGrid // Tactical Naval Warfare Platform',
    template: '%s | FleetGrid',
  },
  description:
    'Retro tactical naval warfare simulation. Engage Monte Carlo Probability Density AI, track your battle telemetry, and compete on the global Admiralty Roll.',
  keywords: [
    'battleship',
    'naval warfare',
    'tactical strategy game',
    'fleetgrid',
    'probability density AI',
    'monte carlo naval combat',
    'retro military game',
    'admiralty roll',
  ],
  authors: [{ name: 'FleetGrid Command', url: 'https://fleetgrid.me' }],
  creator: 'FleetGrid Command',
  publisher: 'FleetGrid',
  alternates: {
    canonical: '/',
  },
  openGraph: {
    type: 'website',
    locale: 'en_US',
    url: 'https://fleetgrid.me',
    siteName: 'FleetGrid',
    title: 'FleetGrid // Tactical Naval Warfare Platform',
    description:
      'Retro tactical naval warfare simulation with Monte Carlo Probability Density AI and live Admiralty Roll.',
    images: [
      {
        url: '/opengraph-image',
        width: 1200,
        height: 630,
        alt: 'FleetGrid Combat Information Center Tactical Radar',
      },
    ],
  },
  twitter: {
    card: 'summary_large_image',
    title: 'FleetGrid // Tactical Naval Warfare Platform',
    description:
      'Retro tactical naval warfare simulation with Monte Carlo Probability Density AI and live Admiralty Roll.',
    images: ['/twitter-image'],
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      'max-video-preview': -1,
      'max-image-preview': 'large',
      'max-snippet': -1,
    },
  },
  icons: {
    icon: '/favicon.ico',
    apple: '/favicon.ico',
  },
};

/**
 * Esquema de datos estructurados JSON-LD (Schema.org) para clasificar el sitio como VideoGame y Organization.
 */
const jsonLdGame = {
  '@context': 'https://schema.org',
  '@type': 'VideoGame',
  name: 'FleetGrid',
  url: 'https://fleetgrid.me',
  description:
    'Tactical naval warfare simulation platform featuring Monte Carlo Probability Density AI, instant guest combat, and verified officer rankings.',
  genre: ['Strategy', 'Tactical', 'Board Game', 'Simulation'],
  gamePlatform: ['Web Browser', 'Desktop', 'Mobile'],
  applicationCategory: 'Game',
  operatingSystem: 'Any',
  inLanguage: 'en',
  offers: {
    '@type': 'Offer',
    price: '0',
    priceCurrency: 'USD',
    availability: 'https://schema.org/InStock',
  },
  publisher: {
    '@type': 'Organization',
    name: 'FleetGrid Command',
    url: 'https://fleetgrid.me',
    logo: 'https://fleetgrid.me/opengraph-image',
  },
};

const jsonLdOrg = {
  '@context': 'https://schema.org',
  '@type': 'Organization',
  name: 'FleetGrid',
  url: 'https://fleetgrid.me',
  logo: 'https://fleetgrid.me/opengraph-image',
  contactPoint: {
    '@type': 'ContactPoint',
    email: 'security@fleetgrid.me',
    contactType: 'technical support',
  },
};

/**
 * Diseno base de la aplicacion que inyecta las tipografias tacticas, el contenedor de notificaciones y la paleta naval.
 *
 * @param props Contenido hijo de la aplicacion.
 */
export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className={`${pixelFont.variable} ${tacticalFont.variable} dark`}>
      <head>
        <script
          type="application/ld+json"
          dangerouslySetInnerHTML={{ __html: JSON.stringify(jsonLdGame) }}
        />
        <script
          type="application/ld+json"
          dangerouslySetInnerHTML={{ __html: JSON.stringify(jsonLdOrg) }}
        />
      </head>
      <body className="bg-[#050b14] text-[#e2ecf7] min-h-dvh flex flex-col justify-between selection:bg-[#00ff66] selection:text-black antialiased">
        <div className="flex-1 flex flex-col">
          {children}
        </div>
        <Footer />
        <Toaster
          position="top-right"
          theme="dark"
          toastOptions={{
            style: {
              background: '#0a1322',
              border: '2px solid #233b66',
              color: '#00ff66',
              fontFamily: 'var(--font-tactical)',
              fontSize: '13px',
              boxShadow: '0 4px 12px rgba(0,0,0,0.8)',
            },
          }}
        />
      </body>
    </html>
  );
}
