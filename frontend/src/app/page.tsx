'use client';

import React, { useState, useEffect, useCallback } from 'react';
import confetti from 'canvas-confetti';
import { toast } from 'sonner';
import { Crosshair, Shield, Activity, RotateCcw, Eye, EyeOff, Radio } from 'lucide-react';

import { Header } from '@/components/Header';
import { AuthModal } from '@/components/AuthModal';
import { FleetPlacement } from '@/components/FleetPlacement';
import { CombatGrid } from '@/components/CombatGrid';
import { TelemetryLog } from '@/components/TelemetryLog';
import { LeaderboardModal } from '@/components/LeaderboardModal';

import { api } from '@/lib/api';
import { sounds } from '@/lib/sound';
import { AiDifficulty, GameSummary, MoveHistoryItem, PlacedShip, UserProfile } from '@/lib/types';

/**
 * Componente principal de la sala de mando tactica (Combat Information Center).
 * Coordina las fases de despliegue de flota, combate interactivo y evaluacion de records.
 */
export default function Home() {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [phase, setPhase] = useState<'DEPLOYMENT' | 'COMBAT' | 'GAMEOVER'>('DEPLOYMENT');
  const [crtActive, setCrtActive] = useState<boolean>(true);
  const [isAuthOpen, setIsAuthOpen] = useState<boolean>(false);
  const [isLeaderboardOpen, setIsLeaderboardOpen] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);

  // Estado de la partida activa
  const [game, setGame] = useState<GameSummary | null>(null);
  const [aiDifficulty, setAiDifficulty] = useState<AiDifficulty>('HARD');
  const [logs, setLogs] = useState<MoveHistoryItem[]>([]);
  const [lastAttack, setLastAttack] = useState<{ row: number; col: number } | null>(null);
  const [isAiThinking, setIsAiThinking] = useState<boolean>(false);
  const [screenShake, setScreenShake] = useState<boolean>(false);

  // Pestana activa en dispositivos moviles: Ataque o Defensa
  const [mobileTab, setMobileTab] = useState<'ATTACK' | 'DEFENSE'>('ATTACK');
  const [showAiHeatmap, setShowAiHeatmap] = useState<boolean>(false);

  // Verificacion de sesion en el montaje del componente
  useEffect(() => {
    const checkUser = async () => {
      if (api.getToken()) {
        try {
          const profile = await api.getMe();
          setUser(profile);
        } catch {
          api.logout();
        }
      }
    };
    checkUser();
  }, []);

  /**
   * Activa el efecto visual de vibracion sismica en la pantalla tras un impacto critico.
   */
  const triggerShake = () => {
    setScreenShake(true);
    setTimeout(() => setScreenShake(false), 380);
  };

  /**
   * Valida la autorizacion de mando e inicializa la sesion de combate contra el adversario IA.
   * Si el usuario no ha iniciado sesion, detiene la operacion y solicita credenciales.
   *
   * @param _ships Lista de buques colocados en el tablero defensivo.
   * @param difficulty Nivel de dificultad estrategica de la IA enemiga.
   */
  const handleStartGame = async (_ships: PlacedShip[], difficulty: AiDifficulty) => {
    if (!user) {
      sounds.playShipSunkAlarm();
      toast.error('OFFICER CLEARANCE REQUIRED: PLEASE SIGN IN TO TRANSMIT FLEET ORDERS');
      setIsAuthOpen(true);
      return;
    }

    sounds.playSonarPing();
    setLoading(true);
    setAiDifficulty(difficulty);

    try {
      const summary = await api.createGame(difficulty);
      setGame(summary);
      setLogs([]);
      setLastAttack(null);
      setPhase('COMBAT');
      setMobileTab('ATTACK');
      toast.success(`FLEET DEPLOYED &bull; ENGAGING ENEMY [AI-${difficulty}]`);
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

      // Evaluate human player shot audio & visual response
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

      // Refresh game state and history
      const [updatedSummary, updatedHistory] = await Promise.all([
        api.getGame(game.gameId),
        api.getGameHistory(game.gameId),
      ]);

      setGame(updatedSummary);
      setLogs(updatedHistory);

      // Check victory / game over
      if (shot.finished) {
        setPhase('GAMEOVER');
        if (shot.winner === game.player1) {
          sounds.playVictoryFanfare();
          confetti({
            particleCount: 120,
            spread: 80,
            origin: { y: 0.6 },
            colors: ['#00ff66', '#00e5ff', '#ffaa00'],
          });
          toast.success('DECISIVE NAVAL VICTORY! ENEMY FLEET ANNIHILATED!');
        } else {
          sounds.playShipSunkAlarm();
          toast.error('DEFEAT &bull; YOUR FLEET HAS BEEN SUNK');
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
   * Restablece el teatro de operaciones al fondeadero para un nuevo despliegue de flota.
   */
  const handleResetToDeployment = () => {
    sounds.playClick();
    setPhase('DEPLOYMENT');
    setGame(null);
    setLogs([]);
    setLastAttack(null);
  };

  return (
    <div className={`min-h-screen flex flex-col bg-[#050b14] ${crtActive ? 'crt-overlay' : ''} ${screenShake ? 'shake-hit' : ''}`}>
      {/* Barra superior de mando naval */}
      <Header
        user={user}
        crtActive={crtActive}
        onToggleCrt={() => setCrtActive((prev) => !prev)}
        onOpenAuth={() => setIsAuthOpen(true)}
        onOpenLeaderboard={() => setIsLeaderboardOpen(true)}
        onLogout={() => {
          api.logout();
          setUser(null);
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

            {/* Mobile View Tab Switcher (Visible on small screens) */}
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

            {/* Combat Theaters (Desktop: Side by Side | Mobile: Tab Toggled) */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 items-start">
              {/* Defense Grid: Own Vessels */}
              <div className={`${mobileTab === 'DEFENSE' ? 'block' : 'hidden lg:block'}`}>
                <CombatGrid
                  title="DEFENSIVE ANCHORAGE"
                  subtitle="Friendly fleet positions & incoming adversary artillery damage"
                  grid={game.myFleetGrid}
                  isAttackGrid={false}
                />
              </div>

              {/* Attack Grid: Enemy Radar (Fog of War) */}
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

        {/* Phase 3: Game Over Modal Overlay */}
        {phase === 'GAMEOVER' && game && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/85 backdrop-blur-md p-4">
            <div className="w-full max-w-md bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical p-6 text-center space-y-4 shadow-2xl">
              <div className="w-16 h-16 mx-auto border-2 flex items-center justify-center rounded-full bg-[#050b14] border-[#00ff66]">
                <Activity className="w-8 h-8 text-[#00ff66]" />
              </div>

              <h2
                className={`font-['Press_Start_2P'] text-sm tracking-wider ${
                  game.winner === game.player1
                    ? 'text-[#00ff66] glow-radar'
                    : 'text-[#ff3344] glow-combat'
                }`}
              >
                {game.winner === game.player1
                  ? 'VICTORY &bull; ADMIRALTY CITATION'
                  : 'VESSEL LOST &bull; MISSION FAILED'}
              </h2>

              <p className="text-xs text-[#8fa5bf] font-mono leading-relaxed">
                {game.winner === game.player1
                  ? `Outstanding strategic leadership, Officer ${game.player1.toUpperCase()}. Enemy fleet completely annihilated in sector #${game.gameId}.`
                  : `Your flagship and defense grid have succumbed to enemy artillery barrage in sector #${game.gameId}.`}
              </p>

              <div className="p-3 border border-[#1a2d4f] bg-[#050b14] font-mono text-xs flex justify-around">
                <div>
                  <div className="text-[10px] text-[#536780]">TOTAL SHOTS</div>
                  <div className="text-sm font-bold text-[#e2ecf7]">{logs.length}</div>
                </div>
                <div>
                  <div className="text-[10px] text-[#536780]">DIFFICULTY</div>
                  <div className="text-sm font-bold text-[#ffaa00]">{aiDifficulty}</div>
                </div>
              </div>

              <div className="flex gap-2">
                <button
                  onClick={handleResetToDeployment}
                  className="flex-1 py-2.5 bg-[#00ff66] hover:bg-[#00cc52] text-black font-['Press_Start_2P'] text-[10px] tracking-wider bezel-tactical-btn font-bold uppercase"
                >
                  NEW ENGAGEMENT
                </button>
                <button
                  onClick={() => setIsLeaderboardOpen(true)}
                  className="px-3 py-2.5 border border-[#ffaa00] text-[#ffaa00] hover:bg-[#ffaa00]/15 font-mono text-xs font-bold"
                >
                  ROLL
                </button>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Modals */}
      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        onSuccess={(profile) => setUser(profile)}
      />

      <LeaderboardModal
        isOpen={isLeaderboardOpen}
        onClose={() => setIsLeaderboardOpen(false)}
        isLoggedIn={!!user}
      />
    </div>
  );
}
