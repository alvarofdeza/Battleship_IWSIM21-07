'use client';

import React, { useEffect, useRef } from 'react';
import { Terminal } from 'lucide-react';
import { MoveHistoryItem } from '@/lib/types';

/**
 * Propiedades para el teletipo de telemetria tactica en tiempo real.
 */
interface TelemetryLogProps {
  /** Historial acumulativo de movimientos y ataques de la partida. */
  logs: MoveHistoryItem[];
  /** Nombre del oficial humano para diferenciar eventos. */
  playerName?: string;
  /** Nombre del subsistema oponente de IA. */
  aiName?: string;
}

const ROWS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];

/**
 * Terminal teletipo militar con desplazamiento automatico que registra
 * los impactos, fallos y hundimientos en tiempo real con estetica phosphor verde y ambar.
 */
export const TelemetryLog: React.FC<TelemetryLogProps> = ({ logs, playerName = 'PLAYER', aiName = 'AI' }) => {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.scrollTop = containerRef.current.scrollHeight;
    }
  }, [logs]);

  return (
    <div className="w-full bg-[#060c18] border-2 border-[#1a2d4f] bezel-tactical p-3">
      {/* Terminal Title */}
      <div className="flex items-center justify-between border-b border-[#1a2d4f] pb-1.5 mb-2">
        <div className="flex items-center gap-1.5 text-[#00ff66]">
          <Terminal className="w-4 h-4" />
          <span className="font-['Press_Start_2P'] text-[9px] tracking-wider glow-radar">
            COMBAT TELEMETRY &bull; LOG FEED
          </span>
        </div>
        <span className="text-[10px] font-mono text-[#536780]">
          EVENTS: {logs.length}
        </span>
      </div>

      {/* Log Feed */}
      <div
        ref={containerRef}
        className="h-28 overflow-y-auto space-y-1 font-mono text-[11px] pr-1"
      >
        {logs.length === 0 ? (
          <div className="text-[#536780] italic">
            &gt; Awaiting initial firing solution. Standing by for fleet engagement...
          </div>
        ) : (
          logs.map((move, idx) => {
            const isHuman = move.playerUsername.toLowerCase() === playerName.toLowerCase();
            const coordStr = `${ROWS[move.row]}-${move.col + 1}`;
            const timeStr = move.timestamp ? new Date(move.timestamp).toLocaleTimeString() : `+${idx}s`;

            const isHit = move.result.toLowerCase().includes('hit');
            const isSunk = move.result.toLowerCase().includes('sunk');

            return (
              <div key={idx} className="flex items-start gap-1 leading-tight">
                <span className="text-[#536780]">[{timeStr}]</span>
                <span className={isHuman ? 'text-[#00ff66] font-bold' : 'text-[#ffaa00] font-bold'}>
                  {isHuman ? playerName.toUpperCase() : aiName.toUpperCase()}
                </span>
                <span className="text-[#8fa5bf]">&gt; TARGET [{coordStr}] &rarr;</span>
                <span
                  className={
                    isSunk
                      ? 'text-[#ff3344] font-bold glow-combat'
                      : isHit
                      ? 'text-[#ff3344] font-bold'
                      : 'text-[#00e5ff]'
                  }
                >
                  {move.result.toUpperCase()}
                </span>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
