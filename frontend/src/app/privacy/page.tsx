import React from 'react';
import Link from 'next/link';
import { ArrowLeft, ShieldCheck, Lock, Database, UserX, FileText } from 'lucide-react';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Privacy Policy & Data Protection',
  description: 'FleetGrid data processing, telemetry disclosure, and officer privacy rights.',
  alternates: {
    canonical: '/privacy',
  },
};

/**
 * Pagina de politica de privacidad y tratamiento de datos personales (CIC Privacy Directive).
 * Renderizada en servidor de forma estatica e indexable para motores de busqueda.
 */
export default function PrivacyPage() {
  return (
    <div className="min-h-dvh flex flex-col bg-[#050b14] text-[#e2ecf7] p-4 sm:p-6 md:p-8 font-mono">
      <div className="max-w-4xl mx-auto w-full space-y-6">
        {/* Navigation Header */}
        <div className="flex items-center justify-between border-b-2 border-[#1a2d4f] pb-4">
          <Link
            href="/"
            className="flex items-center gap-2 text-xs font-bold text-[#00e5ff] hover:underline bezel-tactical-btn px-3 py-1.5 border border-[#00e5ff]/40"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>RETURN TO COMBAT INFORMATION CENTER</span>
          </Link>
          <div className="text-[10px] text-[#7892b0] uppercase tracking-wider hidden sm:block">
            DOC REF: NAV-CIC-PRIVACY-REV1
          </div>
        </div>

        {/* Title */}
        <div className="flex items-center gap-3">
          <ShieldCheck className="w-8 h-8 text-[#00e5ff]" />
          <div>
            <h1 className="font-['Press_Start_2P'] text-sm sm:text-base text-[#00e5ff] glow-radar tracking-wider">
              FLEETGRID // PRIVACY & TELEMETRY
            </h1>
            <p className="text-xs text-[#8fa5bf] mt-1">
              Data minimization principles, operational telemetry disclosure, and naval officer rights.
            </p>
          </div>
        </div>

        {/* Section 1: Data Minimization */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
          <div className="flex items-center gap-2 text-[#00ff66]">
            <Lock className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              1. Principle of Strict Data Minimization
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            FleetGrid adheres to defensive data engineering principles. We collect and store only the minimal technical data required to authenticate naval commanders, preserve combat scores on the Admiralty Roll, and secure our network infrastructure against abuse. We do not sell, rent, or monetize officer data, nor do we deploy third-party advertising trackers.
          </p>
        </div>

        {/* Section 2: Collected Data */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
          <div className="flex items-center gap-2 text-[#ffaa00]">
            <Database className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              2. Data Collected & Tactical Purpose
            </h2>
          </div>
          <div className="space-y-3 text-xs text-[#8fa5bf]">
            <div>
              <strong className="text-white">Commander Accounts:</strong>
              <p className="mt-0.5">
                When you commission an account, we record your chosen callsign (username) and email address. The email is utilized strictly for transmitting the 6-digit military verification PIN and account password recovery OTPs. Passwords are never stored in plaintext; they are transformed via salted BCrypt hashing.
              </p>
            </div>
            <div>
              <strong className="text-white">Combat Engagement Telemetry:</strong>
              <p className="mt-0.5">
                Upon match completion, the system records engagement metrics: AI difficulty level, match duration, total move count, outcome (Victory or Defeat), and calculated admiralty points. Guest battles are held ephemerally in RAM and are not persisted unless explicitly claimed.
              </p>
            </div>
            <div>
              <strong className="text-white">Performance Telemetry:</strong>
              <p className="mt-0.5">
                Anonymous operational metrics (page load times, Core Web Vitals) may be processed to identify latency bottlenecks and maintain service reliability.
              </p>
            </div>
          </div>
        </div>

        {/* Section 3: Data Retention & Security */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
          <div className="flex items-center gap-2 text-[#00e5ff]">
            <FileText className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              3. Data Retention & Infrastructure
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            Persistent records reside in PostgreSQL storage hosted within the European Union (Frankfurt region). Temporary verification tokens (OTP) expire automatically after 10 minutes and are stored using cryptographic SHA-256 hashes.
          </p>
        </div>

        {/* Section 4: Officer Rights & Deletion */}
        <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
          <div className="flex items-center gap-2 text-[#ff3344]">
            <UserX className="w-4 h-4" />
            <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
              4. Officer Rights & Account Deletion
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] leading-relaxed">
            Any naval officer may request complete erasure of their account, registered email, and associated match history from the Admiralty Roll at any time. To execute account decommissioning, transmit a formal request from your registered email address to:
          </p>
          <div className="p-2.5 bg-[#050b14] border border-[#ff3344]/40 text-[#ff3344] text-xs font-bold">
            COMMUNICATION DESK: security@fleetgrid.me
          </div>
          <p className="text-xs text-[#7892b0]">
            For coordinated vulnerability disclosure, consult our security specification at{' '}
            <Link href="/.well-known/security.txt" className="text-[#00e5ff] underline hover:text-[#00ff66]">
              /.well-known/security.txt
            </Link>.
          </p>
        </div>
      </div>
    </div>
  );
}
