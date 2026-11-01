import React, { Suspense } from 'react';
import Link from 'next/link';
import { ArrowLeft, Trophy, Shield, Users, Medal, Loader2 } from 'lucide-react';
import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Admiralty Roll // Public Naval Leaderboard',
  description: 'Official FleetGrid Admiralty Roll. Top tactical combat victories and distinguished naval officers ranked by career merit points.',
  alternates: {
    canonical: '/leaderboard',
  },
};

interface MatchScore {
  gameId: string;
  playerUsername: string;
  points: number;
  difficulty: string;
  durationSeconds: number;
  movesCount: number;
  result: string;
  createdAt: string;
}

interface PlayerRanking {
  username: string;
  totalPoints: number;
  totalVictories: number;
  totalMatches: number;
  winRatio: number;
  navalRank: string;
}

async function getLeaderboardData(): Promise<{ matches: MatchScore[]; players: PlayerRanking[] }> {
  const apiUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

  try {
    const [matchesRes, playersRes] = await Promise.all([
      fetch(`${apiUrl}/leaderboard/matches`, { next: { revalidate: 300 } }),
      fetch(`${apiUrl}/leaderboard/players`, { next: { revalidate: 300 } }),
    ]);

    const matches = matchesRes.ok ? await matchesRes.json() : [];
    const players = playersRes.ok ? await playersRes.json() : [];

    return { matches, players };
  } catch {
    // Si la API esta en arranque en frio durante el build estatico, devolver lista inicial vacia
    return { matches: [], players: [] };
  }
}

/**
 * Componente asincrono que recupera y renderiza las tablas del escalafon militar.
 */
async function LeaderboardTables() {
  const { matches, players } = await getLeaderboardData();

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      {/* Top Officers Table */}
      <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
        <div className="flex items-center gap-2 text-[#00e5ff] border-b border-[#1a2d4f] pb-2">
          <Users className="w-4 h-4" />
          <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
            Top Fleet Officers
          </h2>
        </div>

        {players.length === 0 ? (
          <p className="text-xs text-[#7892b0] py-4 text-center">No officer service records compiled yet.</p>
        ) : (
          <div className="space-y-2">
            {players.map((p, idx) => (
              <div key={idx} className="flex items-center justify-between text-xs p-2 bg-[#050b14] border border-[#1a2d4f]">
                <div className="flex items-center gap-2">
                  <span className="font-bold text-[#ffaa00]">#{idx + 1}</span>
                  <span className="text-white font-bold">{p.username}</span>
                  <span className="text-[10px] text-[#00ff66] border border-[#00ff66]/30 px-1">{p.navalRank}</span>
                </div>
                <div className="text-right">
                  <div className="text-[#00ff66] font-bold">{p.totalPoints} PTS</div>
                  <div className="text-[10px] text-[#7892b0]">{p.totalVictories} W / {p.totalMatches} M</div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Top Matches Table */}
      <div className="p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical space-y-3">
        <div className="flex items-center gap-2 text-[#ffaa00] border-b border-[#1a2d4f] pb-2">
          <Medal className="w-4 h-4" />
          <h2 className="font-['Press_Start_2P'] text-xs uppercase tracking-wider">
            Top Tactical Matches
          </h2>
        </div>

        {matches.length === 0 ? (
          <p className="text-xs text-[#7892b0] py-4 text-center">No individual battle scores recorded yet.</p>
        ) : (
          <div className="space-y-2">
            {matches.map((m, idx) => (
              <div key={idx} className="flex items-center justify-between text-xs p-2 bg-[#050b14] border border-[#1a2d4f]">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-[#ffaa00]">#{idx + 1}</span>
                    <span className="text-white font-bold">{m.playerUsername}</span>
                    <span className="text-[10px] text-[#00e5ff]">[{m.difficulty}]</span>
                  </div>
                  <div className="text-[10px] text-[#7892b0]">{m.movesCount} shots &bull; {m.durationSeconds}s</div>
                </div>
                <div className="text-[#00ff66] font-bold">{m.points} PTS</div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function LeaderboardSkeleton() {
  return (
    <div className="p-8 text-center text-[#7892b0] flex flex-col items-center justify-center gap-2">
      <Loader2 className="w-6 h-6 animate-spin text-[#00ff66]" />
      <span className="text-xs uppercase tracking-wider">Synchronizing Admiralty Roll telemetry...</span>
    </div>
  );
}

/**
 * Pagina publica indexable del Escalafon Militar (Admiralty Roll).
 * Renderizada en servidor con Suspense e ISR (5 min) para garantizar optimizacion SEO sin despertar la base de datos.
 */
export default function LeaderboardPage() {
  return (
    <div className="min-h-dvh flex flex-col bg-[#050b14] text-[#e2ecf7] p-4 sm:p-6 md:p-8 font-mono">
      <div className="max-w-5xl mx-auto w-full space-y-6">
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
            DOC REF: NAV-ROLL-PUBLIC-REV3 &bull; ISR CACHED (5 MIN)
          </div>
        </div>

        {/* Title */}
        <div className="flex items-center gap-3">
          <Trophy className="w-8 h-8 text-[#00ff66]" />
          <div>
            <h1 className="font-['Press_Start_2P'] text-sm sm:text-base text-[#00ff66] glow-radar tracking-wider">
              FLEETGRID // ADMIRALTY ROLL
            </h1>
            <p className="text-xs text-[#8fa5bf] mt-1">
              Official tactical naval records, distinguished combat engagements and fleet officer rankings.
            </p>
          </div>
        </div>

        <Suspense fallback={<LeaderboardSkeleton />}>
          <LeaderboardTables />
        </Suspense>

        {/* Footer Note */}
        <div className="p-3 bg-[#070e1a] border border-[#1a2d4f] text-[11px] text-[#7892b0] flex items-center gap-2">
          <Shield className="w-4 h-4 text-[#00ff66] shrink-0" />
          <span>
            Rankings are cached across the Vercel Edge Network and refreshed every 5 minutes (Incremental Static Regeneration). Combat results are verified by the Spring Boot serverless backend.
          </span>
        </div>
      </div>
    </div>
  );
}
