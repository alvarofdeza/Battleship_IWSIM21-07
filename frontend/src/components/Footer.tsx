'use client';

import React from 'react';
import Link from 'next/link';
import { Terminal, Shield, BookOpen, HelpCircle, Activity, Trophy, ShieldCheck } from 'lucide-react';

/**
 * Componente de pie de pagina táctico naval (Combat Information Center).
 * Provee enlaces hacia las paginas de manual operativo, reglas, rankings publicos y privacidad.
 */
export const Footer: React.FC = () => {
  return (
    <footer className="w-full bg-[#070e1a] border-t-2 border-[#1a2d4f] px-4 py-4 bezel-tactical text-xs font-mono mt-auto">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
        {/* Terminal Info */}
        <div className="flex items-center gap-2 text-[#7892b0]">
          <Terminal className="w-4 h-4 text-[#00ff66]" />
          <span>
            FLEETGRID // CIC-95 &bull; NAVAL OPERATIONS TERMINAL &bull; ETSISI UPM
          </span>
        </div>

        {/* Subsystem Telemetry */}
        <div className="flex items-center gap-3 text-[11px] text-[#7892b0]">
          <span className="flex items-center gap-1 text-[#00ff66]">
            <Activity className="w-3 h-3 text-[#00ff66]" />
            <span>NEON DB FRANKFURT</span>
          </span>
          <span className="text-[#1a2d4f]">|</span>
          <span className="flex items-center gap-1 text-[#00e5ff]">
            <Shield className="w-3 h-3 text-[#00e5ff]" />
            <span>SPRING BOOT 3</span>
          </span>
        </div>

        {/* Navigation Links */}
        <nav aria-label="Tactical manual and legal navigation" className="flex flex-wrap items-center gap-4 text-xs">
          <Link
            href="/leaderboard"
            className="text-[#8fa5bf] hover:text-[#ffd700] transition-colors flex items-center gap-1"
          >
            <Trophy className="w-3.5 h-3.5 text-[#ffd700]" />
            <span>ADMIRALTY ROLL</span>
          </Link>
          <Link
            href="/rules"
            className="text-[#8fa5bf] hover:text-[#00ff66] transition-colors flex items-center gap-1"
          >
            <BookOpen className="w-3.5 h-3.5 text-[#00ff66]" />
            <span>TACTICAL RULES</span>
          </Link>
          <Link
            href="/faq"
            className="text-[#8fa5bf] hover:text-[#ffaa00] transition-colors flex items-center gap-1"
          >
            <HelpCircle className="w-3.5 h-3.5 text-[#ffaa00]" />
            <span>OPERATIONS FAQ</span>
          </Link>
          <Link
            href="/privacy"
            className="text-[#8fa5bf] hover:text-[#00e5ff] transition-colors flex items-center gap-1"
          >
            <ShieldCheck className="w-3.5 h-3.5 text-[#00e5ff]" />
            <span>PRIVACY</span>
          </Link>
        </nav>
      </div>
    </footer>
  );
};
