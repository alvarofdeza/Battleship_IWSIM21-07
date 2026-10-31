'use client';

import React from 'react';
import { Volume2, VolumeX, Monitor, Shield, Trophy, User, LogOut } from 'lucide-react';
import { sounds } from '@/lib/sound';
import { UserProfile } from '@/lib/types';

/**
 * Propiedades de configuracion para la barra superior de mando naval.
 */
interface HeaderProps {
  /** Perfil del oficial autenticado o nulo si no ha iniciado sesion. */
  user: UserProfile | null;
  /** Estado de activacion del filtro de pantalla CRT retro. */
  crtActive: boolean;
  /** Alterna la activacion de las lineas de exploracion CRT. */
  onToggleCrt: () => void;
  /** Abre el panel modal de autorizacion / inicio de sesion. */
  onOpenAuth: () => void;
  /** Despliega el escalafon de oficiales y tabla de clasificacion global. */
  onOpenLeaderboard: () => void;
  /** Cierra la sesion del oficial activo. */
  onLogout: () => void;
}

/**
 * Componente de cabecera tactica militar que presenta el estado del sistema,
 * conmutador de audio sintetizado, filtro CRT, clasificaciones y control de sesion.
 */
export const Header: React.FC<HeaderProps> = ({
  user,
  crtActive,
  onToggleCrt,
  onOpenAuth,
  onOpenLeaderboard,
  onLogout,
}) => {
  const [muted, setMuted] = React.useState(sounds.isMuted());

  const handleToggleSound = () => {
    const isNowMuted = sounds.toggleMute();
    setMuted(isNowMuted);
    if (!isNowMuted) {
      sounds.playClick();
    }
  };

  return (
    <header className="w-full bg-[#0a1322] border-b-2 border-[#1a2d4f] px-4 py-2.5 bezel-tactical shadow-lg sticky top-0 z-30">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-3">
        {/* Left: Terminal Identity */}
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 border-2 border-[#00ff66] bg-[#050b14] flex items-center justify-center relative overflow-hidden group">
            <Shield className="w-6 h-6 text-[#00ff66] group-hover:scale-110 transition-transform" />
            <div className="absolute inset-0 bg-[#00ff66]/10 pointer-events-none" />
          </div>

          <div>
            <div className="flex items-center gap-2">
              <span className="font-['Press_Start_2P'] text-xs text-[#00ff66] tracking-wider glow-radar">
                BATTLESHIP // CIC-95
              </span>
              <span className="inline-block w-2 h-2 rounded-full bg-[#00ff66] animate-pulse" />
            </div>
            <div className="text-[11px] text-[#8fa5bf] tracking-widest uppercase">
              US Naval Command Tactical Operations &bull; Spring Boot 3 Engine
            </div>
          </div>
        </div>

        {/* Center: Hardware Status Indicators */}
        <div className="hidden lg:flex items-center gap-4 text-[10px] font-mono border border-[#1a2d4f] bg-[#050b14] px-3 py-1">
          <div className="flex items-center gap-1.5 text-[#00ff66]">
            <span className="w-1.5 h-1.5 rounded-full bg-[#00ff66] animate-ping" />
            <span>RADAR: 525nm ACTIVE</span>
          </div>
          <div className="text-[#1a2d4f]">|</div>
          <div className="flex items-center gap-1.5 text-[#ffaa00]">
            <span className="w-1.5 h-1.5 rounded-full bg-[#ffaa00]" />
            <span>AI: MONTE CARLO DENSITY</span>
          </div>
        </div>

        {/* Right: Controls & User Profile */}
        <div className="flex items-center gap-2">
          {/* CRT Scanline Toggle */}
          <button
            onClick={() => {
              sounds.playClick();
              onToggleCrt();
            }}
            className={`px-2 py-1 text-[11px] font-mono flex items-center gap-1 border transition-all ${
              crtActive
                ? 'border-[#00ff66] text-[#00ff66] bg-[#00ff66]/10'
                : 'border-[#1a2d4f] text-[#536780] hover:text-[#8fa5bf]'
            }`}
            title="Toggle CRT Screen Scanlines"
          >
            <Monitor className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">CRT</span>
          </button>

          {/* Sound Toggle */}
          <button
            onClick={handleToggleSound}
            className={`px-2 py-1 text-[11px] font-mono flex items-center gap-1 border transition-all ${
              !muted
                ? 'border-[#00ff66] text-[#00ff66] bg-[#00ff66]/10'
                : 'border-[#1a2d4f] text-[#ff3344] bg-[#ff3344]/10'
            }`}
            title={muted ? 'Unmute SFX' : 'Mute SFX'}
          >
            {muted ? <VolumeX className="w-3.5 h-3.5" /> : <Volume2 className="w-3.5 h-3.5" />}
            <span className="hidden sm:inline">{muted ? 'MUTED' : 'AUDIO'}</span>
          </button>

          {/* Leaderboard Button */}
          <button
            onClick={() => {
              sounds.playClick();
              onOpenLeaderboard();
            }}
            className="px-2.5 py-1 text-[11px] font-mono flex items-center gap-1 border border-[#ffaa00] text-[#ffaa00] hover:bg-[#ffaa00]/15 bezel-tactical-btn transition-colors"
          >
            <Trophy className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">ADMIRALTY ROLL</span>
          </button>

          {/* Auth Button */}
          {user ? (
            <div className="flex items-center gap-2 border border-[#233b66] bg-[#060c18] px-2.5 py-1">
              <User className="w-3.5 h-3.5 text-[#00ff66]" />
              <span className="text-[11px] text-[#00ff66] font-mono uppercase font-bold tracking-wider">
                {user.username}
              </span>
              <button
                onClick={() => {
                  sounds.playClick();
                  onLogout();
                }}
                className="text-[#ff3344] hover:text-white ml-1 p-0.5"
                title="Disconnect from Command"
              >
                <LogOut className="w-3 h-3" />
              </button>
            </div>
          ) : (
            <button
              onClick={() => {
                sounds.playClick();
                onOpenAuth();
              }}
              className="px-3 py-1 text-[11px] font-mono border-2 border-[#00ff66] text-black bg-[#00ff66] hover:bg-[#00cc52] font-bold tracking-wider bezel-tactical-btn uppercase"
            >
              CLEARANCE LOGIN
            </button>
          )}
        </div>
      </div>
    </header>
  );
};
