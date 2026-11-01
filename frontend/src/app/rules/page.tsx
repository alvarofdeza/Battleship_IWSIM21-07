import React from 'react';
import Link from 'next/link';
import { ArrowLeft, BookOpen, Shield, Crosshair, Cpu, Award } from 'lucide-react';
import type { Metadata } from 'next';
import { FLEET_SHIPS } from '@/lib/types';
import { PixelShip } from '@/components/PixelShips';

export const metadata: Metadata = {
  title: 'Combat Manual & Rules',
  description: 'Tactical naval engagement guidelines, 10x10 coordinate grid, vessel specifications, AI algorithms and scoring formula.',
  alternates: {
    canonical: '/rules',
  },
};

/**
 * Pagina de manual de combate naval y especificacion de reglas tacticas (CIC Manual).
 * Renderizada de forma estatica en servidor para maxima velocidad e indexacion SEO.
 */
export default function RulesPage() {
  return (
    <div className="min-h-dvh flex flex-col bg-[#050b14] text-[#e2ecf7] p-4 sm:p-6 md:p-8 font-mono">
      <div className="max-w-4xl mx-auto w-full space-y-6">
        {/* Navigation Header */}
        <div className="flex items-center justify-between border-b-2 border-[#1a2d4f] pb-4">
          <Link
            href="/"
            className="flex items-center gap-2 text-xs font-bold text-[#00ff66] hover:underline bezel-tactical-btn px-3 py-1.5 border border-[#00ff66]/40"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>RETURN TO COMBAT INFORMATION CENTER</span>
          </Link>
          <div className="text-[10px] text-[#7892b0] uppercase tracking-wider hidden sm:block">
            DOC REF: NAV-CIC-MANUAL-REV4
          </div>
        </div>

        {/* Title */}
        <div className="flex items-center gap-3">
          <BookOpen className="w-8 h-8 text-[#00ff66]" />
          <div>
            <h1 className="font-['Press_Start_2P'] text-sm sm:text-base text-[#00ff66] glow-radar tracking-wider">
              FLEETGRID // COMBAT MANUAL & RULES
            </h1>
            <p className="text-xs text-[#8fa5bf] mt-1">
              Tactical naval engagement guidelines, vessel specifications and scoring telemetry.
            </p>
          </div>
        </div>

        {/* Section 1: The 10x10 Coordinate Grid */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
          <div className="flex items-center gap-2 text-[#00e5ff]">
            <Crosshair className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              1. Tactical Theater Coordinates
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            Combat takes place on a 10x10 alphanumeric matrix. Rows are designated A through J, and columns are indexed 1 through 10. Fog of War obscures enemy coordinates until artillery shells are dispatched.
          </p>
          <ul className="list-disc list-inside text-xs text-[#8fa5bf] space-y-1">
            <li><strong className="text-[#00e5ff]">Blue Splash (~, M):</strong> Water strike. Coordinates are confirmed clear of enemy tonnage.</li>
            <li><strong className="text-[#ff3344]">Red Flame (H):</strong> Direct hit. Enemy vessel hull sustained severe ordnance damage.</li>
            <li><strong className="text-[#ff3344]">Red Skull (S):</strong> Sunk vessel. All hull sectors of the ship have been eliminated.</li>
          </ul>
        </div>

        {/* Section 2: Naval Vessel Manifest */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
          <div className="flex items-center gap-2 text-[#ffaa00]">
            <Shield className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              2. Authorized Fleet Hull Classes
            </h2>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
            {FLEET_SHIPS.map((ship) => (
              <div key={ship.type} className="p-3 border border-[#1a2d4f] bg-[#050b14] flex items-center justify-between">
                <div>
                  <div className="font-['Press_Start_2P'] text-[10px] text-white">
                    {ship.name}
                  </div>
                  <div className="text-[10px] text-[#7892b0] mt-1">
                    Size: {ship.size} cells &bull; Designation: [{ship.letter}]
                  </div>
                </div>
                <div className="w-20 h-5">
                  <PixelShip type={ship.type} horizontal={true} />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Section 3: AI Strategic Algorithms */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
          <div className="flex items-center gap-2 text-[#00ff66]">
            <Cpu className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              3. Adversary AI Computational Models
            </h2>
          </div>
          <div className="space-y-2 text-xs text-[#8fa5bf]">
            <div>
              <strong className="text-[#00ff66]">EASY (Recruit):</strong> Employs uniform random shot distribution across unexplored coordinates.
            </div>
            <div>
              <strong className="text-[#ffaa00]">NORMAL (Standard):</strong> Hunt mode uses checkerboard parity to maximize hit probability; switches to targeted orthogonal expansion upon first confirmed hit.
            </div>
            <div>
              <strong className="text-[#ff3344]">HARD (Expert):</strong> Monte Carlo Probability Density estimation. Simulates every valid placement configuration for surviving vessels across unexplored waters with 50x weighting around confirmed hits.
            </div>
          </div>
        </div>

        {/* Section 4: Scoring Formula */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
          <div className="flex items-center gap-2 text-[#ffd700]">
            <Award className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              4. Admiralty Score Formulation
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            Scores are computed on the Spring Boot 3 mainframe and verified against anti-farming algorithms:
          </p>
          <div className="p-3 bg-[#050b14] border border-[#1a2d4f] text-[11px] text-[#00ff66] space-y-1">
            <div>VICTORY SCORE = ((1,000 + bonus_shots) * difficulty_multiplier) + bonus_time</div>
            <div>DEFEAT CONSOLATION = (Destroyed enemy hull points: Carrier 250, Battleship 200, Submarine 150, Patrol Boat 100) * difficulty_multiplier</div>
          </div>
        </div>
      </div>
    </div>
  );
}
