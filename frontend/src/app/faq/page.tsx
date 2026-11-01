'use client';

import React from 'react';
import Link from 'next/link';
import { ArrowLeft, HelpCircle, Server, Mail, ShieldAlert, Trophy, Volume2 } from 'lucide-react';

/**
 * Pagina de Preguntas Frecuentes y Guia Operativa del Sistema (CIC Operations FAQ).
 * Disenada para su posterior indexacion con el mismo estilo visual retro-militar.
 */
export default function FaqPage() {
  return (
    <div className="min-h-dvh flex flex-col bg-[#050b14] text-[#e2ecf7] p-4 sm:p-6 md:p-8 font-mono">
      <div className="max-w-4xl mx-auto w-full space-y-6">
        {/* Navigation Header */}
        <div className="flex items-center justify-between border-b-2 border-[#1a2d4f] pb-4">
          <Link
            href="/"
            className="flex items-center gap-2 text-xs font-bold text-[#ffaa00] hover:underline bezel-tactical-btn px-3 py-1.5 border border-[#ffaa00]/40"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>RETURN TO COMBAT INFORMATION CENTER</span>
          </Link>
          <div className="text-[10px] text-[#7892b0] uppercase tracking-wider hidden sm:block">
            DOC REF: NAV-CIC-FAQ-REV2
          </div>
        </div>

        {/* Title */}
        <div className="flex items-center gap-3">
          <HelpCircle className="w-8 h-8 text-[#ffaa00]" />
          <div>
            <h1 className="font-['Press_Start_2P'] text-sm sm:text-base text-[#ffaa00] glow-amber tracking-wider">
              FLEETGRID // OPERATIONS & FAQ
            </h1>
            <p className="text-xs text-[#8fa5bf] mt-1">
              Command procedures, clearance authentication, infrastructure telemetry and score claiming.
            </p>
          </div>
        </div>

        {/* Question 1: Render Cold Start */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-2">
          <div className="flex items-center gap-2 text-[#00ff66]">
            <Server className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              Why does the first battle take 30-50 seconds to initialize?
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            The Admiralty backend is hosted on cloud PaaS infrastructure that automatically enters low-power standby after 15 minutes of inactivity. When you dispatch a new battle or sign in, the satellite link awakens the container. Subsequent moves and matches respond in sub-100ms.
          </p>
        </div>

        {/* Question 2: Guest Mode and Score Claiming */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-2">
          <div className="flex items-center gap-2 text-[#00e5ff]">
            <Trophy className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              Can I play immediately without creating an account?
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            Yes. FleetGrid operates with zero barriers to entry. You can deploy your fleet and commence combat as a Guest Commander. After the engagement concludes, you can claim your combat score and link it directly to a new or existing commission.
          </p>
        </div>

        {/* Question 3: 6-digit OTP Verification */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-2">
          <div className="flex items-center gap-2 text-[#ffaa00]">
            <Mail className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              How does the 6-digit military PIN verification work?
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            When you register, an official clearance code is transmitted to your registered email address via Brevo HTTPS API. Enter the 6-digit PIN in the naval terminal to activate your commission and appear on the official Admiralty Roll.
          </p>
        </div>

        {/* Question 4: Audio Synthesis */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-2">
          <div className="flex items-center gap-2 text-[#e2ecf7]">
            <Volume2 className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              Are audio files loaded from external servers?
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            No. FleetGrid synthesizes 100% of its naval sound effects in real time using the native Web Audio API (sine, square, and filtered noise oscillators). There are zero third-party audio file downloads, ensuring zero latency and privacy.
          </p>
        </div>

        {/* Question 5: Fair Play & Anti-Farming */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-2">
          <div className="flex items-center gap-2 text-[#ff3344]">
            <ShieldAlert className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              How does the anti-farming defense system work?
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            Aborted or surrendered missions automatically compute with 0 points. Consolation points in defeat require full battle resolution with verified naval ordnance exchanges to prevent artificial point inflation on the Admiralty Roll.
          </p>
        </div>
      </div>
    </div>
  );
}
