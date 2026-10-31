import type { Metadata } from 'next';
import { Press_Start_2P, Share_Tech_Mono } from 'next/font/google';
import { Toaster } from 'sonner';
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
  title: 'USS BATTLESHIP // Combat Information Center',
  description: 'Tactical naval warfare simulation platform with Probability Density AI and Spring Boot 3 backend.',
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
      <body className="bg-[#050b14] text-[#e2ecf7] min-h-screen selection:bg-[#00ff66] selection:text-black antialiased">
        {children}
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
