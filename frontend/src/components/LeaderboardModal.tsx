'use client';

import React, { useState, useEffect } from 'react';
import { X, Trophy, Medal, Award, RefreshCw, Users, Swords, Shield } from 'lucide-react';
import { api } from '@/lib/api';
import { sounds } from '@/lib/sound';
import { MatchScoreItem, PlayerRankingItem } from '@/lib/types';

/**
 * Propiedades del cuadro modal de clasificaciones navales y records.
 */
interface LeaderboardModalProps {
  /** Indica si la ventana modal debe visualizarse en primer plano. */
  isOpen: boolean;
  /** Funcion invocada para cerrar la ventana modal. */
  onClose: () => void;
}

/**
 * Componente modal de clasificaciones navales (Admiralty Honor Roll) con doble ranking:
 * 1. Top 10 Mejores Partidas individuales (con distintivo VICTORY / DEFEAT).
 * 2. Top 10 Mejores Oficiales ordenados por suma acumulada de puntos de carrera militar.
 */
export const LeaderboardModal: React.FC<LeaderboardModalProps> = ({ isOpen, onClose }) => {
  const [activeTab, setActiveTab] = useState<'MATCHES' | 'OFFICERS'>('MATCHES');
  const [topMatches, setTopMatches] = useState<MatchScoreItem[]>([]);
  const [topOfficers, setTopOfficers] = useState<PlayerRankingItem[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchRankings = React.useCallback(async () => {
    setLoading(true);
    try {
      const [matches, officers] = await Promise.all([
        api.getTopMatches(),
        api.getTopPlayers(),
      ]);
      setTopMatches(matches);
      setTopOfficers(officers);
    } catch {
      // Fallback si la API no estuviera accesible
      setTopMatches([]);
      setTopOfficers([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isOpen) {
      fetchRankings();
    }
  }, [isOpen, fetchRankings]);

  if (!isOpen) return null;

  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const getRankBadge = (rankIndex: number) => {
    if (rankIndex === 0) return <Medal className="w-4 h-4 text-[#ffd700]" />;
    if (rankIndex === 1) return <Medal className="w-4 h-4 text-[#c0c0c0]" />;
    if (rankIndex === 2) return <Medal className="w-4 h-4 text-[#cd7f32]" />;
    return <Award className="w-4 h-4 text-[#536780]" />;
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/85 backdrop-blur-sm p-4">
      <div className="w-full max-w-2xl bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical p-5 relative shadow-2xl text-left">
        {/* Close Button */}
        <button
          onClick={() => {
            sounds.playClick();
            onClose();
          }}
          className="absolute top-4 right-4 text-[#8fa5bf] hover:text-[#ff3344] transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Title */}
        <div className="flex items-center gap-2 mb-2 text-[#ffaa00]">
          <Trophy className="w-5 h-5" />
          <h2 className="font-['Press_Start_2P'] text-xs tracking-wider glow-amber">
            ADMIRALTY HONOR ROLL &bull; DUAL ESCALAFON
          </h2>
        </div>
        <p className="text-xs text-[#8fa5bf] font-mono mb-4">
          Official ranking of combat citations, naval efficiency and decorated Fleet Commanders.
        </p>

        {/* Tabs & Refresh */}
        <div className="flex items-center justify-between border-b border-[#1a2d4f] mb-4">
          <div className="flex gap-2">
            <button
              onClick={() => {
                sounds.playClick();
                setActiveTab('MATCHES');
              }}
              className={`py-2 px-3 font-mono text-xs font-bold transition-colors flex items-center gap-1.5 ${
                activeTab === 'MATCHES'
                  ? 'border-b-2 border-[#ffaa00] text-[#ffaa00] bg-[#ffaa00]/10'
                  : 'text-[#8fa5bf] hover:text-white'
              }`}
            >
              <Swords className="w-3.5 h-3.5" />
              TOP 10 MATCHES
            </button>
            <button
              onClick={() => {
                sounds.playClick();
                setActiveTab('OFFICERS');
              }}
              className={`py-2 px-3 font-mono text-xs font-bold transition-colors flex items-center gap-1.5 ${
                activeTab === 'OFFICERS'
                  ? 'border-b-2 border-[#00ff66] text-[#00ff66] bg-[#00ff66]/10'
                  : 'text-[#8fa5bf] hover:text-white'
              }`}
            >
              <Users className="w-3.5 h-3.5" />
              TOP 10 OFFICERS (CAREER POINTS)
            </button>
          </div>

          <button
            onClick={() => {
              sounds.playClick();
              fetchRankings();
            }}
            disabled={loading}
            className="text-[#8fa5bf] hover:text-[#00ff66] p-1.5 transition-colors disabled:opacity-50"
            title="Refresh Rankings"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>

        {/* Tab 1: Top 10 Matches */}
        {activeTab === 'MATCHES' && (
          <div className="border border-[#1a2d4f] bg-[#050b14] overflow-hidden">
            <div className="overflow-x-auto max-h-[380px]">
              <table className="w-full text-left font-mono text-xs">
                <thead className="bg-[#0e1d35] text-[#8fa5bf] uppercase border-b border-[#1a2d4f] sticky top-0">
                  <tr>
                    <th className="py-2.5 px-3">#</th>
                    <th className="py-2.5 px-3">Officer</th>
                    <th className="py-2.5 px-3 text-right">Points</th>
                    <th className="py-2.5 px-3 text-center">Result</th>
                    <th className="py-2.5 px-3 text-center">Difficulty</th>
                    <th className="py-2.5 px-3 text-right">Shots</th>
                    <th className="py-2.5 px-3 text-right">Time</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#122340]">
                  {topMatches.length === 0 ? (
                    <tr>
                      <td colSpan={7} className="text-center py-8 text-[#536780]">
                        NO COMBAT CITATIONS LOGGED IN ADMIRALTY ARCHIVES YET.
                      </td>
                    </tr>
                  ) : (
                    topMatches.map((m, index) => {
                      const isVictory = m.result === 'VICTORY';
                      return (
                        <tr key={m.id || index} className="hover:bg-[#0c1930] transition-colors">
                          <td className="py-2.5 px-3 font-bold text-[#8fa5bf]">
                            <div className="flex items-center gap-1.5">
                              {getRankBadge(index)}
                              <span>#{index + 1}</span>
                            </div>
                          </td>
                          <td className="py-2.5 px-3 font-bold text-white uppercase tracking-wider">
                            {m.username}
                          </td>
                          <td className="py-2.5 px-3 text-right text-[#00ff66] font-bold">
                            {m.points.toLocaleString()} PTS
                          </td>
                          <td className="py-2.5 px-3 text-center">
                            <span
                              className={`px-2 py-0.5 text-[10px] font-bold tracking-wider rounded ${
                                isVictory
                                  ? 'bg-[#00ff66]/15 text-[#00ff66] border border-[#00ff66]/40'
                                  : 'bg-[#ff3344]/15 text-[#ff3344] border border-[#ff3344]/40'
                              }`}
                            >
                              {isVictory ? 'VICTORY' : 'DEFEAT'}
                            </span>
                          </td>
                          <td className="py-2.5 px-3 text-center text-[#8fa5bf]">
                            {m.difficulty || 'NORMAL'}
                          </td>
                          <td className="py-2.5 px-3 text-right text-[#8fa5bf]">
                            {m.movesCount || '-'}
                          </td>
                          <td className="py-2.5 px-3 text-right text-[#8fa5bf]">
                            {m.durationSeconds ? formatDuration(m.durationSeconds) : '-'}
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Tab 2: Top 10 Officers */}
        {activeTab === 'OFFICERS' && (
          <div className="border border-[#1a2d4f] bg-[#050b14] overflow-hidden">
            <div className="overflow-x-auto max-h-[380px]">
              <table className="w-full text-left font-mono text-xs">
                <thead className="bg-[#0e1d35] text-[#8fa5bf] uppercase border-b border-[#1a2d4f] sticky top-0">
                  <tr>
                    <th className="py-2.5 px-3">#</th>
                    <th className="py-2.5 px-3">Officer</th>
                    <th className="py-2.5 px-3 text-right">Total Points</th>
                    <th className="py-2.5 px-3 text-center">Battles</th>
                    <th className="py-2.5 px-3 text-center">Victories</th>
                    <th className="py-2.5 px-3 text-right">Naval Rank</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#122340]">
                  {topOfficers.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="text-center py-8 text-[#536780]">
                        NO FLEET COMMANDERS REGISTERED YET.
                      </td>
                    </tr>
                  ) : (
                    topOfficers.map((o, index) => (
                      <tr key={o.username || index} className="hover:bg-[#0c1930] transition-colors">
                        <td className="py-2.5 px-3 font-bold text-[#8fa5bf]">
                          <div className="flex items-center gap-1.5">
                            {getRankBadge(index)}
                            <span>#{index + 1}</span>
                          </div>
                        </td>
                        <td className="py-2.5 px-3 font-bold text-white uppercase tracking-wider">
                          {o.username}
                        </td>
                        <td className="py-2.5 px-3 text-right text-[#ffaa00] font-bold">
                          {o.totalPoints.toLocaleString()} PTS
                        </td>
                        <td className="py-2.5 px-3 text-center text-[#8fa5bf]">
                          {o.totalGames}
                        </td>
                        <td className="py-2.5 px-3 text-center text-[#00ff66]">
                          {o.totalWins}
                        </td>
                        <td className="py-2.5 px-3 text-right">
                          <span className="text-[#38bdf8] font-bold flex items-center justify-end gap-1">
                            <Shield className="w-3 h-3 text-[#38bdf8]" />
                            {o.navalRank}
                          </span>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        <div className="mt-4 pt-3 border-t border-[#1a2d4f] flex items-center justify-between text-[11px] font-mono text-[#536780]">
          <span>FLEET ADMIRALTY ARCHIVE // NEON POSTGRESQL</span>
          <span>100% SECURE MILITARY LEDGER</span>
        </div>
      </div>
    </div>
  );
};
