'use client';

import React, { useState, useEffect } from 'react';
import { X, Trophy, Medal, Award, RefreshCw, UserCheck } from 'lucide-react';
import { api } from '@/lib/api';
import { sounds } from '@/lib/sound';
import { ScoreItem } from '@/lib/types';

/**
 * Propiedades del cuadro modal de clasificaciones navales y records.
 */
interface LeaderboardModalProps {
  /** Indica si la ventana modal debe visualizarse en primer plano. */
  isOpen: boolean;
  /** Funcion invocada para cerrar la ventana modal. */
  onClose: () => void;
  /** Determina si el jugador tiene sesion activa para consultar sus marcas personales. */
  isLoggedIn: boolean;
}

/**
 * Componente modal de clasificaciones y cuadro de honor naval (Admiralty Roll).
 * Muestra el Top 10 global y el historial de puntuaciones del oficial en servicio.
 */
export const LeaderboardModal: React.FC<LeaderboardModalProps> = ({ isOpen, onClose, isLoggedIn }) => {
  const [activeTab, setActiveTab] = useState<'GLOBAL' | 'PERSONAL'>('GLOBAL');
  const [globalScores, setGlobalScores] = useState<ScoreItem[]>([]);
  const [personalScores, setPersonalScores] = useState<ScoreItem[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchScores = React.useCallback(async () => {
    setLoading(true);
    try {
      const g = await api.getGlobalLeaderboard();
      setGlobalScores(g);

      if (isLoggedIn) {
        const p = await api.getPersonalLeaderboard();
        setPersonalScores(p);
      }
    } catch {
      // Fallback sample data if backend is offline
      setGlobalScores([
        { id: 1, username: 'Admiral_Nimitz', points: 96, gameId: 'g1', recordedAt: new Date().toISOString() },
        { id: 2, username: 'alvaro', points: 90, gameId: 'g2', recordedAt: new Date().toISOString() },
        { id: 3, username: 'Captain_Drake', points: 84, gameId: 'g3', recordedAt: new Date().toISOString() },
        { id: 4, username: 'adrian', points: 78, gameId: 'g4', recordedAt: new Date().toISOString() },
        { id: 5, username: 'Sub_Commander', points: 72, gameId: 'g5', recordedAt: new Date().toISOString() },
      ]);
    } finally {
      setLoading(false);
    }
  }, [isLoggedIn]);

  useEffect(() => {
    if (isOpen) {
      fetchScores();
    }
  }, [isOpen, fetchScores]);

  if (!isOpen) return null;

  const currentList = activeTab === 'GLOBAL' ? globalScores : personalScores;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
      <div className="w-full max-w-xl bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical p-5 relative shadow-2xl">
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
            ADMIRALTY HONOR ROLL &bull; NAVAL CITATIONS
          </h2>
        </div>
        <p className="text-xs text-[#8fa5bf] font-mono mb-4">
          Recorded battle efficiency ratings and victories achieved against AI naval forces.
        </p>

        {/* Tabs & Refresh */}
        <div className="flex items-center justify-between border-b border-[#1a2d4f] mb-4">
          <div className="flex gap-2">
            <button
              onClick={() => {
                sounds.playClick();
                setActiveTab('GLOBAL');
              }}
              className={`py-1.5 px-3 font-mono text-xs font-bold transition-colors ${
                activeTab === 'GLOBAL'
                  ? 'border-b-2 border-[#ffaa00] text-[#ffaa00] bg-[#ffaa00]/10'
                  : 'text-[#536780] hover:text-[#8fa5bf]'
              }`}
            >
              GLOBAL CITATIONS
            </button>
            {isLoggedIn && (
              <button
                onClick={() => {
                  sounds.playClick();
                  setActiveTab('PERSONAL');
                }}
                className={`py-1.5 px-3 font-mono text-xs font-bold transition-colors ${
                  activeTab === 'PERSONAL'
                    ? 'border-b-2 border-[#ffaa00] text-[#ffaa00] bg-[#ffaa00]/10'
                    : 'text-[#536780] hover:text-[#8fa5bf]'
                }`}
              >
                PERSONAL COMMISSIONS
              </button>
            )}
          </div>

          <button
            onClick={() => {
              sounds.playClick();
              fetchScores();
            }}
            disabled={loading}
            className="p-1 text-[#8fa5bf] hover:text-[#00ff66] transition-colors"
            title="Refresh High Scores"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>

        {/* Scores Table */}
        <div className="border border-[#1a2d4f] bg-[#050b14] overflow-hidden">
          <table className="w-full text-left font-mono text-xs">
            <thead>
              <tr className="border-b border-[#1a2d4f] bg-[#081220] text-[#8fa5bf] text-[10px] uppercase">
                <th className="py-2 px-3">Rank</th>
                <th className="py-2 px-3">Officer Callsign</th>
                <th className="py-2 px-3">Efficiency Points</th>
                <th className="py-2 px-3">Date Recorded</th>
              </tr>
            </thead>
            <tbody>
              {currentList.length === 0 ? (
                <tr>
                  <td colSpan={4} className="py-6 text-center text-[#536780] italic">
                    No naval commendations recorded yet. Win a match to secure a ranking!
                  </td>
                </tr>
              ) : (
                currentList.map((score, index) => {
                  const rank = index + 1;
                  return (
                    <tr
                      key={score.id || index}
                      className="border-b border-[#1a2d4f]/40 hover:bg-[#101e36]/40 transition-colors"
                    >
                      <td className="py-2 px-3 font-bold">
                        {rank === 1 ? (
                          <span className="flex items-center gap-1 text-[#ffaa00]">
                            <Medal className="w-4 h-4 text-[#ffaa00]" /> 1ST
                          </span>
                        ) : rank === 2 ? (
                          <span className="flex items-center gap-1 text-[#e2ecf7]">
                            <Medal className="w-4 h-4 text-[#e2ecf7]" /> 2ND
                          </span>
                        ) : rank === 3 ? (
                          <span className="flex items-center gap-1 text-[#d97736]">
                            <Award className="w-4 h-4 text-[#d97736]" /> 3RD
                          </span>
                        ) : (
                          <span className="text-[#536780]">#{rank}</span>
                        )}
                      </td>
                      <td className="py-2 px-3 font-bold text-[#00ff66] flex items-center gap-1.5">
                        <UserCheck className="w-3.5 h-3.5 text-[#536780]" />
                        <span>{score.username.toUpperCase()}</span>
                      </td>
                      <td className="py-2 px-3 font-bold text-[#ffaa00]">
                        {score.points} PTS
                      </td>
                      <td className="py-2 px-3 text-[#536780] text-[10px]">
                        {score.recordedAt ? new Date(score.recordedAt).toLocaleDateString() : 'Active'}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
