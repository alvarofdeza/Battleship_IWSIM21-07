'use client';

import React, { useState, useEffect, useCallback } from 'react';
import confetti from 'canvas-confetti';
import { toast } from 'sonner';
import {
  Crosshair,
  Shield,
  Activity,
  RotateCcw,
  Eye,
  EyeOff,
  Radio,
  Award,
  CheckCircle2,
  Trophy,
} from 'lucide-react';

import { Header } from '@/components/Header';
import { AuthModal } from '@/components/AuthModal';
import { FleetPlacement } from '@/components/FleetPlacement';
import { CombatGrid } from '@/components/CombatGrid';
import { TelemetryLog } from '@/components/TelemetryLog';
import { LeaderboardModal } from '@/components/LeaderboardModal';
import { ProfileModal } from '@/components/ProfileModal';

import { api } from '@/lib/api';
import { sounds } from '@/lib/sound';
import {
  AiDifficulty,
  GameSummary,
  MoveHistoryItem,
  PlacedShip,
  UserProfile,
  UserStats,
} from '@/lib/types';

/**
 * Componente principal de la sala de mando tactica (Combat Information Center).
 * Coordina el modo invitado sin barreras, despliegue de flota, combate interactivo,
 * sistema de puntuacion multidimensional, reclamacion de partidas y expediente militar.
 */
export default function Home() {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [userStats, setUserStats] = useState<UserStats | null>(null);
  const [phase, setPhase] = useState<'DEPLOYMENT' | 'COMBAT' | 'GAMEOVER'>('DEPLOYMENT');
  const [crtActive, setCrtActive] = useState<boolean>(true);
  const [isAuthOpen, setIsAuthOpen] = useState<boolean>(false);
  const [isLeaderboardOpen, setIsLeaderboardOpen] = useState<boolean>(false);
  const [isProfileOpen, setIsProfileOpen] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);

  // Estado de la partida activa
  const [game, setGame] = useState<GameSummary | null>(null);
  const [aiDifficulty, setAiDifficulty] = useState<AiDifficulty>('HARD');
  const [logs, setLogs] = useState<MoveHistoryItem[]>([]);
  const [lastAttack, setLastAttack] = useState<{ row: number; col: number } | null>(null);
  const [isAiThinking, setIsAiThinking] = useState<boolean>(false);
  const [screenShake, setScreenShake] = useState<boolean>(false);

  // Telemetria final de combate y reclamacion de puntos
  const [finalPoints, setFinalPoints] = useState<number | null>(null);
  const [finalMoves, setFinalMoves] = useState<number | null>(null);
  const [finalDuration, setFinalDuration] = useState<number | null>(null);
  const [isVictory, setIsVictory] = useState<boolean>(false);
  const [isClaimed, setIsClaimed] = useState<boolean>(false);
  const [isClaiming, setIsClaiming] = useState<boolean>(false);

  // Pestana activa en dispositivos moviles: Ataque o Defensa
  const [mobileTab, setMobileTab] = useState<'ATTACK' | 'DEFENSE'>('ATTACK');
  const [showAiHeatmap, setShowAiHeatmap] = useState<boolean>(false);

  // Carga de estadisticas de carrera del oficial
  const fetchUserStats = useCallback(async () => {
    try {
      const stats = await api.getMyStats();
      setUserStats(stats);
    } catch {
      setUserStats(null);
    }
  }, []);

  // Verificacion de sesion en el montaje del componente
  useEffect(() => {
    const checkUser = async () => {
      if (api.getToken()) {
        try {
          const profile = await api.getMe();
          setUser(profile);
          fetchUserStats();
        } catch {
          api.logout();
          setUser(null);
          setUserStats(null);
        }
      }
    };
    checkUser();
  }, [fetchUserStats]);

  /**
   * Activa el efecto visual de vibracion sismica en la pantalla tras un impacto critico.
   */
  const triggerShake = () => {
    setScreenShake(true);
    setTimeout(() => setScreenShake(false), 380);
  };

  /**
   * Inicializa la sesion de combate contra el bot de IA.
   * Admite juego inmediato tanto para oficiales autenticados como en Modo Invitado.
   *
   * @param _ships Lista de buques colocados en el tablero defensivo.
   * @param difficulty Nivel de dificultad estrategica de la IA enemiga.
   */
  const handleStartGame = async (_ships: PlacedShip[], difficulty: AiDifficulty) => {
    sounds.playSonarPing();
    setLoading(true);
    setAiDifficulty(difficulty);
    setIsClaimed(false);
    setFinalPoints(null);

    try {
      const summary = await api.createGame(difficulty);
      setGame(summary);
      setLogs([]);
      setLastAttack(null);
      setPhase('COMBAT');
      setMobileTab('ATTACK');

      if (!user) {
        toast.info(`GUEST ENGAGEMENT &bull; ENGAGING ADVERSARY AI [${difficulty}]`);
      } else {
        toast.success(`FLEET DEPLOYED &bull; COMBAT ORDERS TRANSMITTED [AI-${difficulty}]`);
      }
    } catch (err: unknown) {
      sounds.playShipSunkAlarm();
      const msg = err instanceof Error ? err.message : 'FAILED TO INITIALIZE ENGAGEMENT';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Envia una coordenada de ataque por artilleria naval y sincroniza el contraataque de la IA.
   *
   * @param row Indice de fila (0 a 9).
   * @param col Indice de columna (0 a 9).
   */
  const handleFireShot = async (row: number, col: number) => {
    if (!game || isAiThinking || game.finished) return;

    sounds.playArtilleryLaunch();
    setLastAttack({ row, col });
    setIsAiThinking(true);

    try {
      const shot = await api.fireShot(game.gameId, row, col);

      // Evaluar sonido y efectos visuales segun el impacto
      if (shot.playerShotResult === 'Hit') {
        sounds.playExplosion();
        triggerShake();
        toast.error(`DIRECT HIT ON ENEMY VESSEL AT [${String.fromCharCode(65 + row)}-${col + 1}]!`);
      } else if (shot.playerShotResult === 'Sunk') {
        sounds.playShipSunkAlarm();
        triggerShake();
        toast.error('CONFIRMED: ENEMY VESSEL SUNK BY PRECISION STRIKE!');
      } else {
        sounds.playWaterSplash();
      }

      // Actualizar estado del juego e historial
      const [updatedSummary, updatedHistory] = await Promise.all([
        api.getGame(game.gameId),
        api.getGameHistory(game.gameId),
      ]);

      setGame(updatedSummary);
      setLogs(updatedHistory);

      // Comprobar fin de la batalla naval
      if (shot.finished) {
        setPhase('GAMEOVER');
        const humanWon = shot.winner === game.player1;
        setIsVictory(humanWon);
        setFinalPoints(shot.points || 0);
        setFinalMoves(shot.movesCount || updatedHistory.length);
        setFinalDuration(shot.durationSeconds || 0);

        if (humanWon) {
          sounds.playVictoryFanfare();
          confetti({
            particleCount: 130,
            spread: 85,
            origin: { y: 0.6 },
            colors: ['#00ff66', '#00e5ff', '#ffaa00'],
          });
          toast.success('DECISIVE NAVAL VICTORY! ENEMY FLEET ANNIHILATED!');
        } else {
          sounds.playShipSunkAlarm();
          toast.error('DEFEAT &bull; YOUR FLEET HAS BEEN SUNK');
        }

        // Si el oficial estaba autenticado, refrescar su expediente de inmediato
        if (user) {
          fetchUserStats();
        }
      }
    } catch (err: unknown) {
      sounds.playShipSunkAlarm();
      const msg = err instanceof Error ? err.message : 'ORDNANCE FAILURE';
      toast.error(msg);
    } finally {
      setIsAiThinking(false);
    }
  };

  /**
   * Reclama la partida jugada como invitado y la asocia a la cuenta autenticada.
   */
  const handleClaimPoints = async () => {
    if (!game) return;
    sounds.playClick();
    setIsClaiming(true);

    try {
      await api.claimGame(game.gameId);
      setIsClaimed(true);
      sounds.playVictoryFanfare();
      toast.success(`POINTS CLAIMED SUCCESSFULLY // RECORDED IN YOUR SERVICE RECORD`);
      fetchUserStats();
    } catch (err: unknown) {
      sounds.playShipSunkAlarm();
      const msg = err instanceof Error ? err.message : 'FAILED TO CLAIM SCORE';
      toast.error(msg);
    } finally {
      setIsClaiming(false);
    }
  };

  /**
   * Callback tras un login o registro exitoso.
   */
  const handleAuthSuccess = async (profile: UserProfile) => {
    setUser(profile);
    setIsAuthOpen(false);

    // Si estabamos en pantalla de fin de partida como invitado, reclamar de inmediato
    if (phase === 'GAMEOVER' && game && !isClaimed) {
      try {
        await api.claimGame(game.gameId);
        setIsClaimed(true);
        sounds.playVictoryFanfare();
        toast.success(`POINTS CLAIMED SUCCESSFULLY // LINKED TO ${profile.username.toUpperCase()}`);
      } catch {
        // Ignorar si ya fue reclamada
      }
    }
    fetchUserStats();
  };

  /**
   * Restablece el teatro de operaciones al fondeadero para un nuevo despliegue de flota.
   */
  const handleResetToDeployment = () => {
    sounds.playClick();
    setPhase('DEPLOYMENT');
    setGame(null);
    setLogs([]);
    setLastAttack(null);
    setFinalPoints(null);
    setIsClaimed(false);
  };

  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className={`min-h-dvh flex-1 flex flex-col bg-[#050b14] ${crtActive ? 'crt-overlay' : ''} ${screenShake ? 'shake-hit' : ''}`}>
      {/* Barra superior de mando naval */}
      <Header
        user={user}
        stats={userStats}
        crtActive={crtActive}
        onToggleCrt={() => setCrtActive((prev) => !prev)}
        onOpenAuth={() => setIsAuthOpen(true)}
        onOpenLeaderboard={() => setIsLeaderboardOpen(true)}
        onOpenProfile={() => setIsProfileOpen(true)}
        onLogout={() => {
          api.logout();
          setUser(null);
          setUserStats(null);
          toast.info('OFFICER LOGGED OUT');
        }}
      />

      {/* Teatro de mando principal */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-3 sm:p-4 md:p-6 flex flex-col">
        {/* Fase 1: Fondeadero y despliegue tactico de flota */}
        {phase === 'DEPLOYMENT' && (
          <FleetPlacement
            onStartGame={handleStartGame}
            loading={loading}
            isLoggedIn={!!user}
            onOpenAuth={() => setIsAuthOpen(true)}
          />
        )}

        {/* Phase 2: Active Combat Information Center */}
        {(phase === 'COMBAT' || phase === 'GAMEOVER') && game && (
          <div className="space-y-4 flex-1 flex flex-col">
            {/* Combat HUD Header */}
            <div className="p-3 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical flex flex-wrap items-center justify-between gap-3 text-xs font-mono">
              <div className="flex items-center gap-3">
                <div className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full bg-[#ffaa00] animate-pulse" />
                  <span className="font-['Press_Start_2P'] text-[10px] text-[#ffaa00]">
                    SECTOR #{game.gameId}
                  </span>
                </div>
                <div className="text-[#8fa5bf]">
                  ADMIRAL: <strong className="text-[#00ff66]">{game.player1.toUpperCase()}</strong> vs{' '}
                  <strong className="text-[#ff3344]">{game.player2.toUpperCase()}</strong>
                </div>
              </div>

              {/* Status and Action Buttons */}
              <div className="flex items-center gap-2">
                {/* AI Heatmap Overlay Toggle */}
                {aiDifficulty === 'HARD' && (
                  <button
                    onClick={() => {
                      sounds.playClick();
                      setShowAiHeatmap((prev) => !prev);
                    }}
                    className={`px-2.5 py-1 text-[10px] border flex items-center gap-1 transition-all ${
                      showAiHeatmap
                        ? 'border-[#ffaa00] text-[#ffaa00] bg-[#ffaa00]/15'
                        : 'border-[#1a2d4f] text-[#536780] hover:text-[#8fa5bf]'
                    }`}
                    title="Toggle AI Monte Carlo Heatmap Density Telemetry"
                  >
                    {showAiHeatmap ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
                    <span>HEATMAP TELEMETRY</span>
                  </button>
                )}

                <button
                  onClick={handleResetToDeployment}
                  className="px-2.5 py-1 text-[10px] border border-[#ff3344] text-[#ff3344] hover:bg-[#ff3344]/15 flex items-center gap-1 bezel-tactical-btn"
                >
                  <RotateCcw className="w-3.5 h-3.5" />
                  <span>ABORT MISSION</span>
                </button>
              </div>
            </div>

            {/* Mobile View Tab Switcher */}
            <div className="grid grid-cols-2 lg:hidden gap-2">
              <button
                onClick={() => {
                  sounds.playClick();
                  setMobileTab('ATTACK');
                }}
                className={`py-2 font-['Press_Start_2P'] text-[9px] border flex items-center justify-center gap-1.5 ${
                  mobileTab === 'ATTACK'
                    ? 'border-[#ff3344] bg-[#ff3344]/20 text-[#ff3344] glow-combat'
                    : 'border-[#1a2d4f] bg-[#0a1322] text-[#536780]'
                }`}
              >
                <Crosshair className="w-3.5 h-3.5" />
                <span>ATTACK RADAR</span>
              </button>
              <button
                onClick={() => {
                  sounds.playClick();
                  setMobileTab('DEFENSE');
                }}
                className={`py-2 font-['Press_Start_2P'] text-[9px] border flex items-center justify-center gap-1.5 ${
                  mobileTab === 'DEFENSE'
                    ? 'border-[#00ff66] bg-[#00ff66]/20 text-[#00ff66] glow-radar'
                    : 'border-[#1a2d4f] bg-[#0a1322] text-[#536780]'
                }`}
              >
                <Shield className="w-3.5 h-3.5" />
                <span>OWN FLEET</span>
              </button>
            </div>

            {/* Combat Theaters */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 items-start">
              {/* Defense Grid */}
              <div className={`${mobileTab === 'DEFENSE' ? 'block' : 'hidden lg:block'}`}>
                <CombatGrid
                  title="DEFENSIVE ANCHORAGE"
                  subtitle="Friendly fleet positions & incoming adversary artillery damage"
                  grid={game.myFleetGrid}
                  isAttackGrid={false}
                />
              </div>

              {/* Attack Grid */}
              <div className={`${mobileTab === 'ATTACK' ? 'block' : 'hidden lg:block'}`}>
                <CombatGrid
                  title="TARGETING RADAR &bull; ENEMY WATERS"
                  subtitle="Select unattacked coordinates to dispatch heavy artillery strike"
                  grid={game.enemyFogGrid}
                  isAttackGrid={true}
                  onFireShot={handleFireShot}
                  disabled={isAiThinking || phase === 'GAMEOVER'}
                  lastAttackCoord={lastAttack}
                  showRadarSweep={true}
                />
              </div>
            </div>

            {/* AI Calculation Status Banner */}
            {isAiThinking && (
              <div className="p-2 border border-[#ffaa00] bg-[#ffaa00]/10 flex items-center justify-center gap-2 text-xs font-mono text-[#ffaa00] animate-pulse">
                <Radio className="w-4 h-4 animate-spin" />
                <span className="font-bold tracking-wider">
                  ADVERSARY AI IS RUNNING PROBABILITY DENSITY ESTIMATION &bull; STAND BY...
                </span>
              </div>
            )}

            {/* Real-Time Telemetry Feed */}
            <TelemetryLog
              logs={logs}
              playerName={game.player1}
              aiName={game.player2}
            />
          </div>
        )}

        {/* Phase 3: Game Over Modal Overlay with Scoring Breakdown & Score Claiming */}
        {phase === 'GAMEOVER' && game && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/85 backdrop-blur-md p-4">
            <div className="w-full max-w-lg bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical p-6 text-center space-y-4 shadow-2xl">
              <div
                className={`w-16 h-16 mx-auto border-2 flex items-center justify-center rounded-full bg-[#050b14] ${
                  isVictory ? 'border-[#00ff66]' : 'border-[#ff3344]'
                }`}
              >
                <Activity className={`w-8 h-8 ${isVictory ? 'text-[#00ff66]' : 'text-[#ff3344]'}`} />
              </div>

              <h2
                className={`font-['Press_Start_2P'] text-sm tracking-wider ${
                  isVictory ? 'text-[#00ff66] glow-radar' : 'text-[#ff3344] glow-combat'
                }`}
              >
                {isVictory
                  ? 'VICTORY &bull; ADMIRALTY CITATION'
                  : 'VESSEL LOST &bull; DEFEAT OF HONOR'}
              </h2>

              <p className="text-xs text-[#8fa5bf] font-mono leading-relaxed">
                {isVictory
                  ? `Strategic mastery confirmed, Commander ${game.player1.toUpperCase()}. Enemy naval division annihilated in sector #${game.gameId}.`
                  : `Your flagship has been sunk in sector #${game.gameId}. Consolation citations awarded for enemy tonnage destroyed.`}
              </p>

              {/* Multi-Dimensional Score Breakdown Card */}
              <div className="border border-[#1a2d4f] bg-[#050b14] p-4 font-mono text-xs text-left space-y-2">
                <div className="flex justify-between items-center pb-2 border-b border-[#1a2d4f]">
                  <span className="text-[#8fa5bf]">BATTLE SCORE EARNED:</span>
                  <span className="text-base font-bold text-[#ffaa00]">
                    {finalPoints !== null ? finalPoints.toLocaleString() : 0} PTS
                  </span>
                </div>

                <div className="grid grid-cols-3 gap-2 pt-1 text-center">
                  <div>
                    <div className="text-[10px] text-[#536780]">SHOTS FIRED</div>
                    <div className="font-bold text-white mt-0.5">{finalMoves || logs.length}</div>
                  </div>
                  <div>
                    <div className="text-[10px] text-[#536780]">DIFFICULTY</div>
                    <div className="font-bold text-[#ffaa00] mt-0.5">{aiDifficulty}</div>
                  </div>
                  <div>
                    <div className="text-[10px] text-[#536780]">DURATION</div>
                    <div className="font-bold text-[#38bdf8] mt-0.5">
                      {finalDuration ? formatDuration(finalDuration) : '-'}
                    </div>
                  </div>
                </div>
              </div>

              {/* Guest Score Claiming Box */}
              {!user && !isClaimed && (
                <div className="p-3 border-2 border-[#ffaa00] bg-[#ffaa00]/10 text-center space-y-2 bezel-tactical">
                  <div className="text-[#ffaa00] font-['Press_Start_2P'] text-[10px]">
                    GUEST ENGAGEMENT &bull; {finalPoints?.toLocaleString()} PTS
                  </div>
                  <p className="text-xs text-[#8fa5bf] font-mono leading-relaxed">
                    Register now to log your combat score in the Admiralty Roll and unlock your naval officer rank.
                  </p>
                  <button
                    onClick={() => setIsAuthOpen(true)}
                    className="w-full py-2.5 bg-[#ffaa00] hover:bg-[#e69900] text-black font-mono font-bold text-xs uppercase bezel-tactical-btn flex items-center justify-center gap-1.5 shadow-[0_0_15px_rgba(255,170,0,0.35)]"
                  >
                    <Award className="w-4 h-4" />
                    CLAIM POINTS & ENLIST
                  </button>
                </div>
              )}

              {/* Authenticated user claiming (if played as guest and logged in) */}
              {user && !isClaimed && game.player1 === 'Guest' && (
                <div className="p-3 border-2 border-[#00ff66] bg-[#00ff66]/10 text-center space-y-2">
                  <p className="text-xs text-[#8fa5bf] font-mono">
                    Active session as <strong className="text-white">{user.username}</strong>. Link these {finalPoints} PTS to your service record.
                  </p>
                  <button
                    onClick={handleClaimPoints}
                    disabled={isClaiming}
                    className="w-full py-2 bg-[#00ff66] hover:bg-[#00cc52] text-black font-mono font-bold text-xs uppercase bezel-tactical-btn flex items-center justify-center gap-1.5"
                  >
                    <CheckCircle2 className="w-4 h-4" />
                    {isClaiming ? 'LINKING...' : 'LINK POINTS TO SERVICE RECORD'}
                  </button>
                </div>
              )}

              {/* Confirmed claim feedback */}
              {isClaimed && (
                <div className="p-2 border border-[#00ff66] bg-[#00ff66]/10 text-[#00ff66] font-mono text-xs flex items-center justify-center gap-2">
                  <CheckCircle2 className="w-4 h-4" />
                  <span>SCORE RECORDED IN THE ADMIRALTY ROLL</span>
                </div>
              )}

              {/* Action buttons */}
              <div className="flex gap-2 pt-2">
                <button
                  onClick={handleResetToDeployment}
                  className="flex-1 py-2.5 bg-[#00ff66] hover:bg-[#00cc52] text-black font-['Press_Start_2P'] text-[10px] tracking-wider bezel-tactical-btn font-bold uppercase"
                >
                  NEW ENGAGEMENT
                </button>
                <button
                  onClick={() => setIsLeaderboardOpen(true)}
                  className="px-4 py-2.5 border border-[#ffaa00] text-[#ffaa00] hover:bg-[#ffaa00]/15 font-mono text-xs font-bold flex items-center gap-1.5"
                >
                  <Trophy className="w-4 h-4" />
                  <span>ESCALAFON</span>
                </button>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Modales del sistema */}
      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        onSuccess={handleAuthSuccess}
      />

      <LeaderboardModal
        isOpen={isLeaderboardOpen}
        onClose={() => setIsLeaderboardOpen(false)}
      />

      {user && (
        <ProfileModal
          isOpen={isProfileOpen}
          onClose={() => {
            setIsProfileOpen(false);
            fetchUserStats();
          }}
          user={user}
        />
      )}
    </div>
  );
}
