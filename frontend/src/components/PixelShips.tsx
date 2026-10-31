import React from 'react';
import { ShipType } from '@/lib/types';

/**
 * Propiedades de renderizado para buques navales en arte pixelado SVG.
 */
interface PixelShipProps {
  /** Tipo de buque (CARRIER, BATTLESHIP, SUBMARINE o PATROL_BOAT). */
  type: ShipType;
  /** Estado de hundimiento para aplicar la paleta de destruccion en rojo y oxido. */
  sunk?: boolean;
  /** Clases CSS adicionales. */
  className?: string;
  /** Orientacion del navio (horizontal por defecto, o vertical). */
  horizontal?: boolean;
}

/**
 * Renderizador vectorial SVG de siluetas de navios militares con estetica retro pixel art.
 * Incluye portaaviones con pista de aterrizaje, acorazados con torretas y submarinos con periscopio.
 */
export const PixelShip: React.FC<PixelShipProps> = ({
  type,
  sunk = false,
  className = '',
  horizontal = true,
}) => {
  const getShipDetails = () => {
    switch (type) {
      case 'CARRIER':
        return {
          name: 'Carrier (CV-65)',
          size: 5,
          color: sunk ? '#551515' : '#2a446c',
          border: sunk ? '#ff3344' : '#4f72a6',
          deck: sunk ? '#3a1111' : '#1e3352',
          renderSvg: () => (
            <svg viewBox="0 0 100 24" className="w-full h-full pixel-art" preserveAspectRatio="none">
              {/* Hull Base */}
              <polygon points="5,4 95,4 98,12 92,20 8,20 2,12" fill={sunk ? '#401010' : '#1e3352'} stroke={sunk ? '#ff3344' : '#658bbd'} strokeWidth="1.5" />
              {/* Flight Deck */}
              <rect x="8" y="7" width="84" height="10" fill={sunk ? '#250808' : '#15253d'} />
              {/* Runway Yellow Dashes */}
              <line x1="12" y1="12" x2="88" y2="12" stroke={sunk ? '#772222' : '#ffaa00'} strokeWidth="1.5" strokeDasharray="4 4" />
              {/* Island / Control Tower */}
              <rect x="65" y="2" width="12" height="6" fill={sunk ? '#661111' : '#3d5c8a'} stroke={sunk ? '#ff3344' : '#8ab2e6'} strokeWidth="1" />
              <circle cx="70" cy="5" r="1" fill={sunk ? '#ff3344' : '#00ff66'} />
            </svg>
          ),
        };
      case 'BATTLESHIP':
        return {
          name: 'Battleship (BB-63)',
          size: 4,
          color: sunk ? '#551515' : '#2c436b',
          border: sunk ? '#ff3344' : '#5277ab',
          renderSvg: () => (
            <svg viewBox="0 0 80 24" className="w-full h-full pixel-art" preserveAspectRatio="none">
              {/* Armored Hull */}
              <polygon points="6,6 74,6 78,12 72,18 8,18 2,12" fill={sunk ? '#401010' : '#1c2f4d'} stroke={sunk ? '#ff3344' : '#658bbd'} strokeWidth="1.5" />
              {/* Forward Heavy Turret */}
              <rect x="18" y="9" width="10" height="6" fill={sunk ? '#661111' : '#33507d'} />
              <line x1="8" y1="12" x2="18" y2="12" stroke={sunk ? '#ff3344' : '#99bce8'} strokeWidth="2" />
              {/* Midship Bridge Tower */}
              <rect x="36" y="5" width="12" height="14" fill={sunk ? '#551111' : '#284169'} stroke={sunk ? '#ff3344' : '#7ea4d9'} strokeWidth="1" />
              {/* Aft Turret */}
              <rect x="54" y="9" width="10" height="6" fill={sunk ? '#661111' : '#33507d'} />
              <line x1="64" y1="12" x2="74" y2="12" stroke={sunk ? '#ff3344' : '#99bce8'} strokeWidth="2" />
            </svg>
          ),
        };
      case 'SUBMARINE':
        return {
          name: 'Submarine (SSN-774)',
          size: 3,
          color: sunk ? '#551515' : '#1b3b54',
          border: sunk ? '#ff3344' : '#3e769e',
          renderSvg: () => (
            <svg viewBox="0 0 60 22" className="w-full h-full pixel-art" preserveAspectRatio="none">
              {/* Cylindrical Hull */}
              <ellipse cx="30" cy="11" rx="27" ry="8" fill={sunk ? '#330d0d' : '#102538'} stroke={sunk ? '#ff3344' : '#457f9c'} strokeWidth="1.5" />
              {/* Conning Tower / Sail */}
              <rect x="22" y="3" width="12" height="6" fill={sunk ? '#551515' : '#1a4163'} stroke={sunk ? '#ff3344' : '#68a8cc'} strokeWidth="1" />
              {/* Periscope Mast */}
              <line x1="28" y1="0" x2="28" y2="4" stroke={sunk ? '#ff3344' : '#00e5ff'} strokeWidth="1.5" />
              {/* Sonar Sensor Dome */}
              <circle cx="8" cy="11" r="2.5" fill={sunk ? '#ff3344' : '#00ff66'} />
            </svg>
          ),
        };
      case 'PATROL_BOAT':
        return {
          name: 'Patrol Boat (PB-105)',
          size: 2,
          color: sunk ? '#551515' : '#234a66',
          border: sunk ? '#ff3344' : '#4f8ba6',
          renderSvg: () => (
            <svg viewBox="0 0 40 20" className="w-full h-full pixel-art" preserveAspectRatio="none">
              {/* Fast V-Hull */}
              <polygon points="4,6 36,6 39,10 34,16 6,16 1,10" fill={sunk ? '#380f0f' : '#16334a'} stroke={sunk ? '#ff3344' : '#5796b3'} strokeWidth="1.5" />
              {/* Wheelhouse Cabin */}
              <rect x="14" y="4" width="12" height="7" fill={sunk ? '#551515' : '#225075'} stroke={sunk ? '#ff3344' : '#7fbada'} strokeWidth="1" />
              {/* Bow Autocannon */}
              <line x1="6" y1="7" x2="1" y2="5" stroke={sunk ? '#ff3344' : '#99cfe6'} strokeWidth="1.5" />
            </svg>
          ),
        };
    }
  };

  const ship = getShipDetails();

  return (
    <div
      className={`relative flex items-center justify-center ${
        !horizontal ? 'rotate-90 origin-center' : ''
      } ${className}`}
      title={`${ship.name} (${ship.size} celdas)${sunk ? ' - DESTROYED' : ''}`}
    >
      {ship.renderSvg()}
      {sunk && (
        <div className="absolute inset-0 flex items-center justify-center bg-black/40 backdrop-blur-[0.5px]">
          <span className="font-['Press_Start_2P'] text-[9px] text-[#ff3344] tracking-widest px-1 py-0.5 border border-[#ff3344] bg-black/80 font-bold glow-combat">
            SUNK
          </span>
        </div>
      )}
    </div>
  );
};
