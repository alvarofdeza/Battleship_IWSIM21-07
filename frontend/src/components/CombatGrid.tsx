'use client';

import React from 'react';
import { Crosshair, Flame, Droplets, Skull, HelpCircle } from 'lucide-react';

/**
 * Propiedades de configuracion para la cuadricula de combate tactico de 10x10.
 */
interface CombatGridProps {
  /** Titulo del panel tactico (p. ej. DEFENSIVE ANCHORAGE o TARGETING RADAR). */
  title: string;
  /** Subtitulo explicativo con indicaciones operativas. */
  subtitle: string;
  /** Representacion matricial de 10 cadenas de 10 caracteres cada una. */
  grid: string[];
  /** Indica si la cuadricula corresponde a las aguas enemigas sujetas a niebla de guerra. */
  isAttackGrid?: boolean;
  /** Callback ejecutado al pulsar sobre una casilla inexplorada para disparar. */
  onFireShot?: (row: number, col: number) => void;
  /** Deshabilita la interaccion tactil y de raton durante el turno del adversario. */
  disabled?: boolean;
  /** Coordenadas del ultimo disparo recibido o ejecutado para resalte visual. */
  lastAttackCoord?: { row: number; col: number } | null;
  /** Activa la animacion visual del barrido de haz de radar verde/rojo. */
  showRadarSweep?: boolean;
  /** Matriz opcional con las densidades de probabilidad calculadas por la IA para depuracion. */
  heatmapOverlay?: number[][];
}

const ROWS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
const COLS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

/**
 * Cuadricula tactica naval interactiva de 10x10 casillas.
 * Renderiza impactos, fallos en agua, buques aliados y niebla de guerra con estetica militar.
 */
export const CombatGrid: React.FC<CombatGridProps> = ({
  title,
  subtitle,
  grid,
  isAttackGrid = false,
  onFireShot,
  disabled = false,
  lastAttackCoord,
  showRadarSweep = false,
  heatmapOverlay,
}) => {
  const [focusedCell, setFocusedCell] = React.useState<{ r: number; c: number }>({ r: 0, c: 0 });

  const handleCellKeyDown = (e: React.KeyboardEvent, r: number, c: number, canClick: boolean) => {
    let nextR = r;
    let nextC = c;

    if (e.key === 'ArrowUp') {
      e.preventDefault();
      nextR = Math.max(0, r - 1);
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      nextR = Math.min(9, r + 1);
    } else if (e.key === 'ArrowLeft') {
      e.preventDefault();
      nextC = Math.max(0, c - 1);
    } else if (e.key === 'ArrowRight') {
      e.preventDefault();
      nextC = Math.min(9, c + 1);
    } else if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      if (canClick && onFireShot) {
        onFireShot(r, c);
      }
      return;
    } else {
      return;
    }

    setFocusedCell({ r: nextR, c: nextC });
    const targetId = `${isAttackGrid ? 'atk' : 'def'}-cell-${nextR}-${nextC}`;
    const nextElem = document.getElementById(targetId);
    nextElem?.focus();
  };

  return (
    <div className="flex flex-col items-center p-2.5 sm:p-4 bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical w-full">
      {/* Grid Title Bar */}
      <div className="w-full border-b border-[#1a2d4f] pb-2 mb-3 flex items-center justify-between">
        <div>
          <div className="flex items-center gap-2">
            <span
              className={`w-2 h-2 rounded-full ${
                isAttackGrid ? 'bg-[#ff3344] animate-ping' : 'bg-[#00ff66]'
              }`}
            />
            <h3
              className={`font-['Press_Start_2P'] text-[10px] sm:text-[11px] tracking-wider ${
                isAttackGrid ? 'text-[#ff3344] glow-combat' : 'text-[#00ff66] glow-radar'
              }`}
            >
              {title}
            </h3>
          </div>
          <span className="text-[10px] text-[#8fa5bf] font-mono leading-tight block mt-0.5">{subtitle}</span>
        </div>

        {isAttackGrid && (
          <span className="text-[9px] font-mono px-2 py-0.5 border border-[#ff3344] text-[#ff3344] bg-[#ff3344]/10 uppercase font-bold tracking-widest hidden sm:inline">
            FOG OF WAR
          </span>
        )}
      </div>

      {/* 10x10 Coordinate Matrix */}
      <div
        role="grid"
        aria-label={title}
        className="w-full max-w-[440px] mx-auto relative p-1 sm:p-2 border border-[#1a2d4f] bg-[#050b14] shadow-2xl overflow-hidden"
      >
        {/* Radar Sweep Effect (Only for attack grid when active) */}
        {showRadarSweep && isAttackGrid && <div className="radar-beam" />}

        {/* Column Numbers 1-10 */}
        <div role="row" className="grid grid-cols-11 mb-1 text-center font-mono text-[10px] sm:text-xs text-[#00ff66] font-bold gap-0.5 sm:gap-1">
          <div className="flex items-center justify-center text-[#7892b0]">#</div>
          {COLS.map((c) => (
            <div key={c} className="flex items-center justify-center">
              {c}
            </div>
          ))}
        </div>

        {/* Rows A-J */}
        <div className="space-y-0.5 sm:space-y-1">
          {ROWS.map((rowLabel, r) => {
            const rowStr = grid[r] || '??????????';

            return (
              <div role="row" key={rowLabel} className="grid grid-cols-11 items-center gap-0.5 sm:gap-1">
                {/* Row Letter Header */}
                <div className="flex items-center justify-center font-mono text-[10px] sm:text-xs text-[#00ff66] font-bold">
                  {rowLabel}
                </div>

                {/* 10 Cells */}
                {COLS.map((_, c) => {
                  const cellChar = rowStr[c] || '?';
                  const isLastTarget = lastAttackCoord?.row === r && lastAttackCoord?.col === c;
                  const heatVal = heatmapOverlay && heatmapOverlay[r] ? heatmapOverlay[r][c] : 0;
                  const isFocused = focusedCell.r === r && focusedCell.c === c;

                  // Evaluate cell appearance based on character
                  let cellContent: React.ReactNode = null;
                  let cellBg = 'bg-[#0a1322] border-[#1b2d4f]';
                  let canClick = false;
                  let cellAriaLabel = `Sector ${rowLabel}${c + 1}`;

                  if (isAttackGrid) {
                    if (cellChar === '?') {
                      canClick = !disabled;
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Unexplored waters, click to fire artillery`;
                      cellBg =
                        'bg-[#0a1322] border-[#162744] hover:bg-[#ff3344]/15 hover:border-[#ff3344] hover:shadow-[0_0_8px_rgba(255,51,68,0.5)] cursor-crosshair';
                      cellContent = (
                        <div className="opacity-0 group-hover:opacity-100 transition-opacity">
                          <Crosshair className="w-3.5 h-3.5 text-[#ff3344]" />
                        </div>
                      );

                      if (heatVal > 0) {
                        cellBg = `border-[#ffaa00]/40 ${cellBg}`;
                        cellContent = (
                          <span className="text-[8px] font-mono text-[#ffaa00] font-bold">
                            {heatVal}
                          </span>
                        );
                      }
                    } else if (cellChar === 'M' || cellChar === '~') {
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Water splash, artillery missed`;
                      cellBg = 'bg-[#0b2438] border-[#00e5ff]/50';
                      cellContent = (
                        <div className="flex items-center justify-center animate-ripple">
                          <Droplets className="w-3.5 h-3.5 text-[#00e5ff]" />
                        </div>
                      );
                    } else if (cellChar === 'H') {
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Confirmed direct hit, fire in hull`;
                      cellBg = 'bg-[#401217] border-[#ff3344] shadow-[0_0_10px_rgba(255,51,68,0.6)]';
                      cellContent = <Flame className="w-4 h-4 text-[#ff3344] animate-pulse" />;
                    } else if (cellChar === 'S') {
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Enemy vessel destroyed and sunk`;
                      cellBg = 'bg-[#29090c] border-[#ff3344]';
                      cellContent = <Skull className="w-4 h-4 text-[#ff3344]" />;
                    }
                  } else {
                    // DEFENSIVE GRID (Player's own ships)
                    if (cellChar === 'H') {
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Friendly vessel hit`;
                      cellBg = 'bg-[#401217] border-[#ff3344] shadow-[0_0_10px_rgba(255,51,68,0.6)]';
                      cellContent = <Flame className="w-4 h-4 text-[#ff3344] animate-pulse" />;
                    } else if (cellChar === 'S') {
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Friendly ship sunk`;
                      cellBg = 'bg-[#26070a] border-[#ff3344] shadow-[0_0_10px_rgba(255,51,68,0.4)]';
                      cellContent = <Skull className="w-4 h-4 text-[#ff3344]" />;
                    } else if (cellChar === 'M') {
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Adversary splash in water`;
                      cellBg = 'bg-[#0b2438] border-[#00e5ff]/40';
                      cellContent = <Droplets className="w-3.5 h-3.5 text-[#00e5ff]" />;
                    } else if (cellChar === 'B' || cellChar === 'A' || cellChar === 'D') {
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Allied ${cellChar} ship stationed`;
                      cellBg = 'bg-[#1b3b5c] border-[#4f8ba6] shadow-inner';
                      cellContent = (
                        <span className="font-mono text-xs font-bold text-[#e2ecf7]">{cellChar}</span>
                      );
                    } else {
                      cellAriaLabel = `Sector ${rowLabel}${c + 1}: Calm friendly waters`;
                      cellBg = 'bg-[#060e1c] border-[#162744]';
                      cellContent = <span className="text-[#1a2d4f] text-[10px]">&bull;</span>;
                    }
                  }

                  return (
                    <button
                      id={`${isAttackGrid ? 'atk' : 'def'}-cell-${r}-${c}`}
                      role="gridcell"
                      aria-label={cellAriaLabel}
                      tabIndex={isFocused ? 0 : -1}
                      key={`${r}-${c}`}
                      type="button"
                      disabled={!canClick}
                      onFocus={() => setFocusedCell({ r, c })}
                      onKeyDown={(e) => handleCellKeyDown(e, r, c, canClick)}
                      onClick={() => {
                        if (canClick && onFireShot) {
                          onFireShot(r, c);
                        }
                      }}
                      className={`group aspect-square w-full max-w-[40px] border transition-colors flex items-center justify-center font-mono relative touch-manipulation select-none focus:outline-none focus:ring-2 focus:ring-[#00ff66] focus:z-30 ${cellBg} ${
                        isLastTarget ? 'ring-2 ring-[#00ff66] ring-offset-1 ring-offset-black z-20' : ''
                      }`}
                      title={`Sector [${rowLabel}-${c + 1}]`}
                    >
                      {cellContent}
                      {/* Last attack indicator ring */}
                      {isLastTarget && (
                        <div className="absolute inset-0 border border-[#00ff66] pointer-events-none animate-ping" />
                      )}
                    </button>
                  );
                })}
              </div>
            );
          })}
        </div>
      </div>

      {/* Grid Legend Bar */}
      <div className="mt-2.5 flex flex-wrap items-center justify-center gap-3 text-[10px] font-mono text-[#8fa5bf]">
        <div className="flex items-center gap-1">
          <Droplets className="w-3 h-3 text-[#00e5ff]" />
          <span>WATER</span>
        </div>
        <div className="flex items-center gap-1">
          <Flame className="w-3 h-3 text-[#ff3344]" />
          <span>HIT</span>
        </div>
        <div className="flex items-center gap-1">
          <Skull className="w-3 h-3 text-[#ff3344]" />
          <span>SUNK</span>
        </div>
        {isAttackGrid && (
          <div className="flex items-center gap-1 text-[#ffaa00]">
            <HelpCircle className="w-3 h-3" />
            <span>FOG</span>
          </div>
        )}
      </div>
    </div>
  );
};
