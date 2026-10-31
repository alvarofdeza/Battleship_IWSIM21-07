'use client';

import React, { useState, useEffect } from 'react';
import { RotateCw, Shuffle, Trash2, Crosshair, Anchor, ShieldCheck, Lock } from 'lucide-react';
import { FLEET_SHIPS, PlacedShip, ShipDefinition, AiDifficulty } from '@/lib/types';
import { PixelShip } from './PixelShips';
import { sounds } from '@/lib/sound';
import { toast } from 'sonner';

/**
 * Propiedades de entrada para el componente de despliegue de flota.
 */
interface FleetPlacementProps {
  /** Callback invocado para arrancar la partida con las naves desplegadas y la dificultad seleccionada. */
  onStartGame: (ships: PlacedShip[], difficulty: AiDifficulty) => void;
  /** Estado de carga durante la comunicacion con la API. */
  loading: boolean;
  /** Indica si el jugador ha iniciado sesion con credenciales validas. */
  isLoggedIn: boolean;
  /** Callback para abrir el modal de autenticacion. */
  onOpenAuth: () => void;
}

const ROWS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
const COLS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

/**
 * Componente interactivo de dique seco naval para la fase de colocacion tactica de barcos.
 * Permite rotacion dinamica, deteccion automatica de solapamientos y autodespliegue aleatorio.
 */
export const FleetPlacement: React.FC<FleetPlacementProps> = ({
  onStartGame,
  loading,
  isLoggedIn,
  onOpenAuth,
}) => {
  const [placedShips, setPlacedShips] = useState<PlacedShip[]>([]);
  const [selectedShipIndex, setSelectedShipIndex] = useState<number | null>(0);
  const [horizontal, setHorizontal] = useState(true);
  const [hoverCell, setHoverCell] = useState<{ r: number; c: number } | null>(null);
  const [difficulty, setDifficulty] = useState<AiDifficulty>('HARD');

  // Control de atajos de teclado (R o barra espaciadora para rotacion)
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Ignorar el atajo si el foco se encuentra en un campo de texto o elemento editable
      const target = e.target as HTMLElement | null;
      if (
        target &&
        (target.tagName === 'INPUT' ||
          target.tagName === 'TEXTAREA' ||
          target.tagName === 'SELECT' ||
          target.isContentEditable)
      ) {
        return;
      }

      if (e.key === 'r' || e.key === 'R' || e.key === ' ') {
        e.preventDefault();
        setHorizontal((prev) => !prev);
        sounds.playClick();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  const unplacedShips = FLEET_SHIPS.filter(
    (ship) => !placedShips.some((p) => p.type === ship.type)
  );

  /**
   * Valida si un barco puede ser ubicado en las coordenadas dadas sin colisiones ni salirse del tablero.
   */
  const isPositionValid = (ship: ShipDefinition, r: number, c: number, isHoriz: boolean): boolean => {
    if (isHoriz) {
      if (c + ship.size > 10) return false;
    } else {
      if (r + ship.size > 10) return false;
    }

    for (let i = 0; i < ship.size; i++) {
      const curR = isHoriz ? r : r + i;
      const curC = isHoriz ? c + i : c;

      for (const p of placedShips) {
        if (p.type === ship.type) continue;
        for (let j = 0; j < p.size; j++) {
          const pr = p.horizontal ? p.row : p.row + j;
          const pc = p.horizontal ? p.col + j : p.col;
          if (curR === pr && curC === pc) return false;
        }
      }
    }
    return true;
  };

  /**
   * Gestiona el clic sobre una casilla para colocar o retirar una nave.
   */
  const handleCellClick = (r: number, c: number) => {
    const existing = placedShips.find((p) => {
      for (let j = 0; j < p.size; j++) {
        const pr = p.horizontal ? p.row : p.row + j;
        const pc = p.horizontal ? p.col + j : p.col;
        if (r === pr && c === pc) return true;
      }
      return false;
    });

    if (existing) {
      sounds.playClick();
      setPlacedShips((prev) => prev.filter((p) => p.type !== existing.type));
      const defIdx = FLEET_SHIPS.findIndex((s) => s.type === existing.type);
      setSelectedShipIndex(defIdx);
      return;
    }

    if (unplacedShips.length === 0) return;
    const currentShip = selectedShipIndex !== null ? FLEET_SHIPS[selectedShipIndex] : unplacedShips[0];
    if (!currentShip || placedShips.some((p) => p.type === currentShip.type)) return;

    if (!isPositionValid(currentShip, r, c, horizontal)) {
      sounds.playShipSunkAlarm();
      toast.error('INVALID POSITION: OUT OF BOUNDS OR COLLISION');
      return;
    }

    sounds.playSonarPing();
    const newPlaced: PlacedShip = {
      type: currentShip.type,
      row: r,
      col: c,
      size: currentShip.size,
      horizontal,
      hits: 0,
      sunk: false,
    };

    const nextPlaced = [...placedShips, newPlaced];
    setPlacedShips(nextPlaced);

    const remaining = FLEET_SHIPS.filter((s) => !nextPlaced.some((p) => p.type === s.type));
    if (remaining.length > 0) {
      const nextIdx = FLEET_SHIPS.findIndex((s) => s.type === remaining[0].type);
      setSelectedShipIndex(nextIdx);
    } else {
      setSelectedShipIndex(null);
    }
  };

  /**
   * Algoritmo de autodespliegue aleatorio valido para los 4 barcos reglamentarios.
   */
  const handleAutoDeploy = () => {
    sounds.playSonarPing();
    const newFleet: PlacedShip[] = [];

    for (const ship of FLEET_SHIPS) {
      let placed = false;
      let attempts = 0;

      while (!placed && attempts < 200) {
        attempts++;
        const isHoriz = Math.random() < 0.5;
        const maxR = isHoriz ? 10 : 10 - ship.size;
        const maxC = isHoriz ? 10 - ship.size : 10;
        const r = Math.floor(Math.random() * maxR);
        const c = Math.floor(Math.random() * maxC);

        let collides = false;
        for (let i = 0; i < ship.size; i++) {
          const curR = isHoriz ? r : r + i;
          const curC = isHoriz ? c + i : c;

          for (const p of newFleet) {
            for (let j = 0; j < p.size; j++) {
              const pr = p.horizontal ? p.row : p.row + j;
              const pc = p.horizontal ? p.col + j : p.col;
              if (curR === pr && curC === pc) collides = true;
            }
          }
        }

        if (!collides) {
          newFleet.push({
            type: ship.type,
            row: r,
            col: c,
            size: ship.size,
            horizontal: isHoriz,
            hits: 0,
            sunk: false,
          });
          placed = true;
        }
      }
    }

    setPlacedShips(newFleet);
    setSelectedShipIndex(null);
    toast.success('FLEET AUTO-DEPLOYED IN TACTICAL FORMATION');
  };

  /**
   * Limpia el tablero y devuelve todos los barcos al dique seco.
   */
  const handleClear = () => {
    sounds.playClick();
    setPlacedShips([]);
    setSelectedShipIndex(0);
  };

  const getCellOccupant = (r: number, c: number): PlacedShip | null => {
    for (const p of placedShips) {
      for (let j = 0; j < p.size; j++) {
        const pr = p.horizontal ? p.row : p.row + j;
        const pc = p.horizontal ? p.col + j : p.col;
        if (r === pr && c === pc) return p;
      }
    }
    return null;
  };

  const getCellGhostState = (r: number, c: number) => {
    if (!hoverCell || selectedShipIndex === null) return null;
    const currentShip = FLEET_SHIPS[selectedShipIndex];
    if (!currentShip || placedShips.some((p) => p.type === currentShip.type)) return null;

    const inRange = horizontal
      ? r === hoverCell.r && c >= hoverCell.c && c < hoverCell.c + currentShip.size
      : c === hoverCell.c && r >= hoverCell.r && r < hoverCell.r + currentShip.size;

    if (!inRange) return null;

    const valid = isPositionValid(currentShip, hoverCell.r, hoverCell.c, horizontal);
    return valid ? 'valid' : 'invalid';
  };

  const allPlaced = placedShips.length === FLEET_SHIPS.length;

  return (
    <div className="w-full max-w-5xl mx-auto my-4 p-4 md:p-6 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical">
      {/* Cabecera del Dique Seco */}
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between border-b border-[#1a2d4f] pb-4 mb-6 gap-3">
        <div>
          <div className="flex items-center gap-2">
            <Anchor className="w-5 h-5 text-[#00ff66]" />
            <h2 className="font-['Press_Start_2P'] text-sm text-[#00ff66] tracking-wider glow-radar">
              FLEET ANCHORAGE &bull; TACTICAL DEPLOYMENT
            </h2>
          </div>
          <p className="text-xs text-[#8fa5bf] font-mono mt-1">
            Deploy your 4 naval vessels on the 10x10 grid. Press <kbd className="px-1.5 py-0.5 bg-[#1b2d4f] text-[#00ff66] rounded text-[10px]">[R]</kbd> or <kbd className="px-1.5 py-0.5 bg-[#1b2d4f] text-[#00ff66] rounded text-[10px]">[Space]</kbd> to rotate.
          </p>
        </div>

        {/* Acciones Rapidas */}
        <div className="flex items-center gap-2 w-full md:w-auto">
          <button
            onClick={() => {
              sounds.playClick();
              setHorizontal((prev) => !prev);
            }}
            className="flex-1 md:flex-none px-3 py-1.5 text-xs font-mono border border-[#ffaa00] text-[#ffaa00] hover:bg-[#ffaa00]/10 flex items-center justify-center gap-1.5 bezel-tactical-btn"
          >
            <RotateCw className="w-3.5 h-3.5" />
            <span>{horizontal ? 'HORIZONTAL' : 'VERTICAL'}</span>
          </button>
          <button
            onClick={handleAutoDeploy}
            className="flex-1 md:flex-none px-3 py-1.5 text-xs font-mono border border-[#00ff66] text-[#00ff66] hover:bg-[#00ff66]/10 flex items-center justify-center gap-1.5 bezel-tactical-btn"
          >
            <Shuffle className="w-3.5 h-3.5" />
            <span>AUTO-DEPLOY</span>
          </button>
          {placedShips.length > 0 && (
            <button
              onClick={handleClear}
              className="p-1.5 border border-[#ff3344] text-[#ff3344] hover:bg-[#ff3344]/10 bezel-tactical-btn"
              title="Clear all deployed vessels"
            >
              <Trash2 className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Panel Izquierdo: Manifiesto de Naves y Dificultad (5 columnas) */}
        <div className="lg:col-span-5 space-y-4">
          <div className="p-3 border border-[#1a2d4f] bg-[#050b14]">
            <span className="text-[10px] font-mono text-[#8fa5bf] uppercase tracking-wider block mb-2 font-bold">
              Naval Vessel Manifest ({placedShips.length} / 4 Positioned)
            </span>

            <div className="space-y-2">
              {FLEET_SHIPS.map((ship, idx) => {
                const isPlaced = placedShips.some((p) => p.type === ship.type);
                const isSelected = selectedShipIndex === idx;

                return (
                  <div
                    key={ship.type}
                    onClick={() => {
                      if (!isPlaced) {
                        sounds.playClick();
                        setSelectedShipIndex(idx);
                      }
                    }}
                    className={`p-2.5 border transition-all cursor-pointer flex items-center justify-between ${
                      isPlaced
                        ? 'border-[#1b2d4f] bg-[#070e1a]/60 opacity-60'
                        : isSelected
                        ? 'border-2 border-[#00ff66] bg-[#00ff66]/10'
                        : 'border-[#1a2d4f] hover:border-[#8fa5bf] bg-[#0a1322]'
                    }`}
                  >
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-['Press_Start_2P'] text-[10px] text-[#e2ecf7]">
                          {ship.name}
                        </span>
                        {isPlaced && (
                          <span className="text-[9px] font-mono text-[#00ff66] border border-[#00ff66] px-1">
                            DEPLOYED
                          </span>
                        )}
                      </div>
                      <div className="text-[10px] font-mono text-[#8fa5bf] mt-0.5">
                        Length: {ship.size} cells &bull; Code: {ship.letter}
                      </div>
                    </div>

                    <div className="w-24 h-6">
                      <PixelShip type={ship.type} horizontal={true} />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Selector de Algoritmo de Inteligencia Artificial */}
          <div className="p-3 border border-[#1a2d4f] bg-[#050b14]">
            <span className="text-[10px] font-mono text-[#8fa5bf] uppercase tracking-wider block mb-2 font-bold">
              Adversary AI Strategic Algorithm
            </span>

            <div className="grid grid-cols-3 gap-2">
              {(['EASY', 'NORMAL', 'HARD'] as AiDifficulty[]).map((level) => (
                <button
                  key={level}
                  type="button"
                  onClick={() => {
                    sounds.playClick();
                    setDifficulty(level);
                  }}
                  className={`p-2 text-center border font-mono transition-all text-xs font-bold ${
                    difficulty === level
                      ? level === 'HARD'
                        ? 'border-[#ff3344] bg-[#ff3344]/15 text-[#ff3344] glow-combat'
                        : level === 'NORMAL'
                        ? 'border-[#ffaa00] bg-[#ffaa00]/15 text-[#ffaa00] glow-amber'
                        : 'border-[#00ff66] bg-[#00ff66]/15 text-[#00ff66] glow-radar'
                      : 'border-[#1a2d4f] text-[#536780] hover:text-[#8fa5bf]'
                  }`}
                >
                  <div className="text-[10px]">{level}</div>
                  <div className="text-[8px] font-normal text-[#8fa5bf] mt-0.5">
                    {level === 'HARD' ? 'Heatmap' : level === 'NORMAL' ? 'Hunt & Target' : 'Random'}
                  </div>
                </button>
              ))}
            </div>

            <p className="text-[10px] text-[#8fa5bf] font-mono mt-2 leading-relaxed">
              {difficulty === 'HARD'
                ? 'Expert level: Monte Carlo Probability Density Heatmap evaluates all possible hull placements with 50x weighting.'
                : difficulty === 'NORMAL'
                ? 'Standard level: Hunt mode uses checkerboard parity; switches to orthogonal expansion upon impact.'
                : 'Recruit level: Uniform random shot distribution across unexplored coordinates.'}
            </p>
          </div>

          {/* Boton de Accion con Soporte para Modo Invitado y Modo Autenticado */}
          <div className="space-y-2">
            <button
              onClick={() => onStartGame(placedShips, difficulty)}
              disabled={!allPlaced || loading}
              className={`w-full py-3.5 font-['Press_Start_2P'] text-[10px] sm:text-[11px] tracking-widest bezel-tactical-btn font-bold flex items-center justify-center gap-2 uppercase transition-all ${
                allPlaced
                  ? !isLoggedIn
                    ? 'bg-[#ffaa00] text-black hover:bg-[#e69900] shadow-[0_0_20px_rgba(255,170,0,0.35)]'
                    : 'bg-[#00ff66] text-black hover:bg-[#00cc52] shadow-[0_0_20px_rgba(0,255,102,0.4)]'
                  : 'bg-[#1b2d4f] text-[#536780] cursor-not-allowed border border-[#1a2d4f]'
              }`}
            >
              {loading ? (
                'TRANSMITTING TACTICAL DATA...'
              ) : allPlaced ? (
                <>
                  <Crosshair className="w-4 h-4" />
                  <span>
                    {!isLoggedIn ? 'COMMENCE COMBAT (GUEST)' : 'COMMENCE COMBAT'}
                  </span>
                </>
              ) : (
                `DEPLOY ALL SHIPS (${4 - placedShips.length} LEFT)`
              )}
            </button>

            {!isLoggedIn && (
              <p className="text-[10px] text-center font-mono text-[#8fa5bf]">
                Playing as Guest Commander. You can claim and record your citation in the Admiralty Roll after battle.
              </p>
            )}
          </div>
        </div>

        {/* Panel Derecho: Cuadricula 10x10 Interactiva (7 columnas) */}
        <div className="lg:col-span-7 flex flex-col items-center">
          <div className="p-3 border-2 border-[#1a2d4f] bg-[#050b14] inline-block shadow-2xl">
            {/* Cabecera de Columnas 1-10 */}
            <div className="grid grid-cols-11 mb-1 text-center font-mono text-xs text-[#00ff66] font-bold">
              <div className="w-8 h-6 flex items-center justify-center text-[#536780]">#</div>
              {COLS.map((c) => (
                <div key={c} className="w-8 sm:w-9 md:w-10 h-6 flex items-center justify-center">
                  {c}
                </div>
              ))}
            </div>

            {/* Filas de la Cuadricula A-J */}
            <div className="space-y-1">
              {ROWS.map((rowLabel, r) => (
                <div key={rowLabel} className="grid grid-cols-11 items-center">
                  <div className="w-8 h-8 sm:w-9 sm:h-9 md:w-10 md:h-10 flex items-center justify-center font-mono text-xs text-[#00ff66] font-bold">
                    {rowLabel}
                  </div>

                  {COLS.map((_, c) => {
                    const occupant = getCellOccupant(r, c);
                    const ghost = getCellGhostState(r, c);

                    let cellBg = 'bg-[#0a1322] border-[#1b2d4f] hover:border-[#00ff66]';
                    if (occupant) {
                      cellBg = 'bg-[#1b3b5c] border-[#4f8ba6] shadow-inner';
                    }
                    if (ghost === 'valid') {
                      cellBg = 'bg-[#00ff66]/30 border-[#00ff66] shadow-[0_0_8px_rgba(0,255,102,0.5)]';
                    } else if (ghost === 'invalid') {
                      cellBg = 'bg-[#ff3344]/30 border-[#ff3344] shadow-[0_0_8px_rgba(255,51,68,0.5)]';
                    }

                    return (
                      <button
                        key={`${r}-${c}`}
                        type="button"
                        onMouseEnter={() => setHoverCell({ r, c })}
                        onMouseLeave={() => setHoverCell(null)}
                        onClick={() => handleCellClick(r, c)}
                        className={`w-8 h-8 sm:w-9 sm:h-9 md:w-10 md:h-10 border transition-all flex items-center justify-center font-mono text-[10px] relative ${cellBg}`}
                        title={`Sector [${rowLabel}-${c + 1}]`}
                      >
                        {occupant && (
                          <span className="font-bold text-[#e2ecf7] text-[10px] z-10 pointer-events-none">
                            {FLEET_SHIPS.find((s) => s.type === occupant.type)?.letter}
                          </span>
                        )}
                        <div className="absolute inset-0 border border-white/[0.02] pointer-events-none" />
                      </button>
                    );
                  })}
                </div>
              ))}
            </div>
          </div>

          <div className="mt-3 flex items-center gap-2 text-xs font-mono text-[#8fa5bf]">
            <ShieldCheck className="w-4 h-4 text-[#00ff66]" />
            <span>Click to place &bull; Click placed ship to return to drydock</span>
          </div>
        </div>
      </div>
    </div>
  );
};
