'use client';

import React, { useState, useEffect } from 'react';
import {
  X,
  Shield,
  Trophy,
  History,
  Lock,
  CheckCircle2,
  KeyRound,
  RefreshCw,
} from 'lucide-react';
import { api } from '@/lib/api';
import { sounds } from '@/lib/sound';
import { toast } from 'sonner';
import { MatchScoreItem, UserProfile, UserStats } from '@/lib/types';
import { useFocusTrap } from '@/lib/useFocusTrap';

/**
 * Propiedades para el modal del expediente militar del oficial.
 */
interface ProfileModalProps {
  isOpen: boolean;
  onClose: () => void;
  user: UserProfile;
}

/**
 * Componente modal de Expediente de Oficial (Profile):
 * Muestra el rango naval, puntos acumulados de carrera, ratio de victorias,
 * top 10 mejores partidas personales, historial reciente de 10 combates y cambio seguro de contrasena.
 */
export const ProfileModal: React.FC<ProfileModalProps> = ({ isOpen, onClose, user }) => {
  const [activeTab, setActiveTab] = useState<'BEST' | 'RECENT' | 'SECURITY'>('BEST');
  const [stats, setStats] = useState<UserStats | null>(null);
  const [bestMatches, setBestMatches] = useState<MatchScoreItem[]>([]);
  const [recentMatches, setRecentMatches] = useState<MatchScoreItem[]>([]);
  const [loading, setLoading] = useState(false);

  const modalRef = useFocusTrap<HTMLDivElement>(isOpen, onClose);

  // Formulario de cambio de contrasena
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordLoading, setPasswordLoading] = useState(false);

  useEffect(() => {
    if (!isOpen) return;
    const handleGlobalKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };
    window.addEventListener('keydown', handleGlobalKeyDown);
    return () => window.removeEventListener('keydown', handleGlobalKeyDown);
  }, [isOpen, onClose]);

  useEffect(() => {
    if (!isOpen) return;
    let isCancelled = false;

    React.startTransition(() => {
      setLoading(true);
    });

    const loadProfileData = async () => {
      try {
        const [s, best, recent] = await Promise.all([
          api.getMyStats(),
          api.getMyMatches(),
          api.getMyRecentMatches(),
        ]);
        if (!isCancelled) {
          setStats(s);
          setBestMatches(best);
          setRecentMatches(recent);
        }
      } catch {
        // En caso de incidencia en red o sesion expirada
      } finally {
        if (!isCancelled) {
          setLoading(false);
        }
      }
    };

    loadProfileData();

    return () => {
      isCancelled = true;
    };
  }, [isOpen]);

  if (!isOpen) return null;

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    sounds.playClick();

    if (newPassword.length < 6) {
      toast.error('NEW ACCESS CODE MUST BE AT LEAST 6 CHARACTERS');
      return;
    }

    if (newPassword !== confirmPassword) {
      toast.error('NEW PASSWORDS DO NOT MATCH');
      return;
    }

    setPasswordLoading(true);
    try {
      await api.changePassword(currentPassword, newPassword);
      sounds.playSonarPing();
      toast.success('SECURITY CREDENTIALS UPDATED SUCCESSFULLY');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err: unknown) {
      sounds.playShipSunkAlarm();
      const msg = err instanceof Error ? err.message : 'FAILED TO UPDATE PASSWORD';
      toast.error(msg);
    } finally {
      setPasswordLoading(false);
    }
  };

  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/85 backdrop-blur-sm p-4">
      <div
        ref={modalRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="profile-modal-title"
        className="w-full max-w-3xl bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical p-6 relative shadow-2xl text-left max-h-[90vh] overflow-y-auto"
      >
        {/* Close Button */}
        <button
          onClick={() => {
            sounds.playClick();
            onClose();
          }}
          className="absolute top-4 right-4 text-[#8fa5bf] hover:text-[#ff3344] transition-colors p-1"
          aria-label="Close officer dossier"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Officer Dossier Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-[#1a2d4f] mb-5">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 border-2 border-[#38bdf8] bg-[#050b14] flex items-center justify-center shrink-0">
              <Shield className="w-7 h-7 text-[#38bdf8]" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 id="profile-modal-title" className="font-['Press_Start_2P'] text-xs text-white uppercase tracking-wider">
                  {user.username}
                </h2>
                <span className="px-2 py-0.5 text-[9px] font-mono font-bold bg-[#00ff66]/15 text-[#00ff66] border border-[#00ff66]/40 rounded flex items-center gap-1">
                  <CheckCircle2 className="w-2.5 h-2.5" />
                  VERIFIED
                </span>
              </div>
              <div className="text-xs text-[#38bdf8] font-mono font-bold mt-1 flex items-center gap-1">
                <span>RANK:</span>
                <span>{stats?.navalRank || 'Naval Cadet'}</span>
              </div>
            </div>
          </div>

          <div className="text-right font-mono text-xs text-[#8fa5bf]">
            <div>SERVICE DOSSIER #NAV-{user.id || '01'}</div>
            <div className="text-[11px] text-[#536780]">{user.email}</div>
          </div>
        </div>

        {/* Career Summary Stat Cards */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-6">
          <div className="border border-[#1a2d4f] bg-[#050b14] p-3 text-center">
            <div className="text-[10px] font-mono text-[#8fa5bf] uppercase">Career Points</div>
            <div className="text-lg font-bold font-mono text-[#ffaa00] mt-1">
              {(stats?.totalPoints || 0).toLocaleString()}
            </div>
          </div>

          <div className="border border-[#1a2d4f] bg-[#050b14] p-3 text-center">
            <div className="text-[10px] font-mono text-[#8fa5bf] uppercase">Engagements</div>
            <div className="text-lg font-bold font-mono text-white mt-1">
              {stats?.totalGames || 0}
            </div>
          </div>

          <div className="border border-[#1a2d4f] bg-[#050b14] p-3 text-center">
            <div className="text-[10px] font-mono text-[#8fa5bf] uppercase">Victories</div>
            <div className="text-lg font-bold font-mono text-[#00ff66] mt-1">
              {stats?.totalWins || 0}
            </div>
          </div>

          <div className="border border-[#1a2d4f] bg-[#050b14] p-3 text-center">
            <div className="text-[10px] font-mono text-[#8fa5bf] uppercase">Win Rate</div>
            <div className="text-lg font-bold font-mono text-[#38bdf8] mt-1">
              {stats?.winRate || 0}%
            </div>
          </div>
        </div>

        {/* Sub-tabs */}
        <div className="flex border-b border-[#1a2d4f] mb-4">
          <button
            onClick={() => {
              sounds.playClick();
              setActiveTab('BEST');
            }}
            className={`py-2 px-3 font-mono text-xs font-bold transition-colors flex items-center gap-1.5 ${
              activeTab === 'BEST'
                ? 'border-b-2 border-[#ffaa00] text-[#ffaa00] bg-[#ffaa00]/10'
                : 'text-[#8fa5bf] hover:text-white'
            }`}
          >
            <Trophy className="w-3.5 h-3.5" />
            TOP 10 MATCHES
          </button>

          <button
            onClick={() => {
              sounds.playClick();
              setActiveTab('RECENT');
            }}
            className={`py-2 px-3 font-mono text-xs font-bold transition-colors flex items-center gap-1.5 ${
              activeTab === 'RECENT'
                ? 'border-b-2 border-[#00ff66] text-[#00ff66] bg-[#00ff66]/10'
                : 'text-[#8fa5bf] hover:text-white'
            }`}
          >
            <History className="w-3.5 h-3.5" />
            RECENT 10 MATCHES
          </button>

          <button
            onClick={() => {
              sounds.playClick();
              setActiveTab('SECURITY');
            }}
            className={`py-2 px-3 font-mono text-xs font-bold transition-colors flex items-center gap-1.5 ${
              activeTab === 'SECURITY'
                ? 'border-b-2 border-[#38bdf8] text-[#38bdf8] bg-[#38bdf8]/10'
                : 'text-[#8fa5bf] hover:text-white'
            }`}
          >
            <KeyRound className="w-3.5 h-3.5" />
            SECURITY
          </button>
        </div>

        {/* Tab 1: Personal Top 10 Matches */}
        {activeTab === 'BEST' && (
          <div className="border border-[#1a2d4f] bg-[#050b14] overflow-hidden">
            <div className="overflow-x-auto max-h-[300px]">
              <table className="w-full text-left font-mono text-xs">
                <thead className="bg-[#0e1d35] text-[#8fa5bf] uppercase border-b border-[#1a2d4f] sticky top-0">
                  <tr>
                    <th className="py-2 px-3">#</th>
                    <th className="py-2 px-3 text-right">Points</th>
                    <th className="py-2 px-3 text-center">Result</th>
                    <th className="py-2 px-3 text-center">Difficulty</th>
                    <th className="py-2 px-3 text-right">Shots</th>
                    <th className="py-2 px-3 text-right">Duration</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#122340]">
                  {loading ? (
                    <tr>
                      <td colSpan={6} className="text-center py-6 text-[#38bdf8] font-mono text-xs">
                        <span className="inline-flex items-center gap-2 animate-pulse">
                          <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                          <span>SYNCHRONIZING SERVICE CITATIONS...</span>
                        </span>
                      </td>
                    </tr>
                  ) : bestMatches.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="text-center py-6 text-[#536780]">
                        NO RECORDS REGISTERED YET. ENGAGE IN COMBAT TO LOG CITATIONS.
                      </td>
                    </tr>
                  ) : (
                    bestMatches.map((m, idx) => {
                      const isVic = m.result === 'VICTORY';
                      return (
                        <tr key={m.id || idx} className="hover:bg-[#0c1930] transition-colors">
                          <td className="py-2 px-3 text-[#8fa5bf]">#{idx + 1}</td>
                          <td className="py-2 px-3 text-right font-bold text-[#00ff66]">
                            {m.points.toLocaleString()} PTS
                          </td>
                          <td className="py-2 px-3 text-center">
                            <span
                              className={`px-2 py-0.5 text-[10px] font-bold rounded ${
                                isVic
                                  ? 'bg-[#00ff66]/15 text-[#00ff66] border border-[#00ff66]/30'
                                  : 'bg-[#ff3344]/15 text-[#ff3344] border border-[#ff3344]/30'
                              }`}
                            >
                              {isVic ? 'VICTORY' : 'DEFEAT'}
                            </span>
                          </td>
                          <td className="py-2 px-3 text-center text-[#8fa5bf]">{m.difficulty}</td>
                          <td className="py-2 px-3 text-right text-[#8fa5bf]">{m.movesCount}</td>
                          <td className="py-2 px-3 text-right text-[#8fa5bf]">
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

        {/* Tab 2: Recent 10 Matches */}
        {activeTab === 'RECENT' && (
          <div className="border border-[#1a2d4f] bg-[#050b14] overflow-hidden">
            <div className="overflow-x-auto max-h-[300px]">
              <table className="w-full text-left font-mono text-xs">
                <thead className="bg-[#0e1d35] text-[#8fa5bf] uppercase border-b border-[#1a2d4f] sticky top-0">
                  <tr>
                    <th className="py-2 px-3">Match</th>
                    <th className="py-2 px-3 text-right">Points</th>
                    <th className="py-2 px-3 text-center">Result</th>
                    <th className="py-2 px-3 text-center">Difficulty</th>
                    <th className="py-2 px-3 text-right">Shots</th>
                    <th className="py-2 px-3 text-right">Duration</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#122340]">
                  {loading ? (
                    <tr>
                      <td colSpan={6} className="text-center py-6 text-[#38bdf8] font-mono text-xs">
                        <span className="inline-flex items-center gap-2 animate-pulse">
                          <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                          <span>SYNCHRONIZING SERVICE CITATIONS...</span>
                        </span>
                      </td>
                    </tr>
                  ) : recentMatches.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="text-center py-6 text-[#536780]">
                        NO RECENT MATCHES LOGGED.
                      </td>
                    </tr>
                  ) : (
                    recentMatches.map((m, idx) => {
                      const isVic = m.result === 'VICTORY';
                      return (
                        <tr key={m.id || idx} className="hover:bg-[#0c1930] transition-colors">
                          <td className="py-2 px-3 text-[#8fa5bf] font-bold">#{m.gameId}</td>
                          <td className="py-2 px-3 text-right font-bold text-[#ffaa00]">
                            {m.points.toLocaleString()} PTS
                          </td>
                          <td className="py-2 px-3 text-center">
                            <span
                              className={`px-2 py-0.5 text-[10px] font-bold rounded ${
                                isVic
                                  ? 'bg-[#00ff66]/15 text-[#00ff66] border border-[#00ff66]/30'
                                  : 'bg-[#ff3344]/15 text-[#ff3344] border border-[#ff3344]/30'
                              }`}
                            >
                              {isVic ? 'VICTORY' : 'DEFEAT'}
                            </span>
                          </td>
                          <td className="py-2 px-3 text-center text-[#8fa5bf]">{m.difficulty}</td>
                          <td className="py-2 px-3 text-right text-[#8fa5bf]">{m.movesCount}</td>
                          <td className="py-2 px-3 text-right text-[#8fa5bf]">
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

        {/* Tab 3: Security & Password Change */}
        {activeTab === 'SECURITY' && (
          <form onSubmit={handlePasswordChange} className="space-y-4 max-w-md mx-auto py-2">
            <div>
              <label className="block text-xs font-mono text-[#8fa5bf] mb-1 uppercase tracking-wider">
                Current Access Code
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 text-[#536780] absolute left-3 top-2.5" />
                <input
                  type="password"
                  required
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full bg-[#050b14] border border-[#1a2d4f] focus:border-[#38bdf8] pl-9 pr-3 py-2 text-base sm:text-xs font-mono text-white focus:outline-none transition-colors"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-mono text-[#8fa5bf] mb-1 uppercase tracking-wider">
                New Access Code
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 text-[#7892b0] absolute left-3 top-2.5" />
                <input
                  type="password"
                  required
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  placeholder="Minimum 6 characters..."
                  className="w-full bg-[#050b14] border border-[#1a2d4f] focus:border-[#38bdf8] pl-9 pr-3 py-2 text-base sm:text-xs font-mono text-white focus:outline-none transition-colors"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-mono text-[#8fa5bf] mb-1 uppercase tracking-wider">
                Confirm New Access Code
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 text-[#7892b0] absolute left-3 top-2.5" />
                <input
                  type="password"
                  required
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="Confirm new password..."
                  className="w-full bg-[#050b14] border border-[#1a2d4f] focus:border-[#38bdf8] pl-9 pr-3 py-2 text-base sm:text-xs font-mono text-white focus:outline-none transition-colors"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={passwordLoading}
              className="w-full mt-2 py-2.5 px-4 bg-[#38bdf8] text-black font-mono font-bold text-xs tracking-widest hover:bg-[#0ea5e9] transition-colors disabled:opacity-50 bezel-tactical-btn uppercase flex items-center justify-center gap-2"
            >
              {passwordLoading ? 'UPDATING CREDENTIALS...' : 'UPDATE ACCESS CREDENTIALS'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};
