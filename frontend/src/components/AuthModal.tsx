'use client';

import React, { useState } from 'react';
import { X, Lock, Key, Mail, ShieldAlert } from 'lucide-react';
import { api } from '@/lib/api';
import { sounds } from '@/lib/sound';
import { toast } from 'sonner';
import { UserProfile } from '@/lib/types';

/**
 * Propiedades de configuracion para el modal de autorizacion de mando.
 */
interface AuthModalProps {
  /** Indica si el modal se encuentra desplegado en pantalla. */
  isOpen: boolean;
  /** Funcion para cerrar el modal de autorizacion. */
  onClose: () => void;
  /** Callback ejecutado tras completar con exito el login o registro. */
  onSuccess: (user: UserProfile) => void;
}

/**
 * Modal tactico de autenticacion militar que permite a los oficiales
 * iniciar sesion con credenciales JWT o registrar nuevas patentes de corso.
 */
export const AuthModal: React.FC<AuthModalProps> = ({ isOpen, onClose, onSuccess }) => {
  const [isRegister, setIsRegister] = useState(false);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    sounds.playClick();
    setLoading(true);

    try {
      if (isRegister) {
        if (!email.includes('@')) {
          toast.error('VALID EMAIL REQUIRED FOR NAVAL DISPATCH');
          setLoading(false);
          return;
        }
        const res = await api.register(username, email, password);
        toast.success(`COMMISSION GRANTED // WELCOME, ${res.username.toUpperCase()}`);
        onSuccess({ username: res.username, email: res.email, role: res.role });
      } else {
        const res = await api.login(username, password);
        toast.success(`CLEARANCE ACCEPTED // WELCOME BACK, ${res.username.toUpperCase()}`);
        onSuccess({ username: res.username, email: res.email, role: res.role });
      }
      onClose();
    } catch (err: unknown) {
      sounds.playShipSunkAlarm();
      const msg = err instanceof Error ? err.message : 'ACCESS DENIED: VERIFY CLEARANCE CREDENTIALS';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const fillQuickCredentials = (user: string, pass: string) => {
    sounds.playClick();
    setIsRegister(false);
    setUsername(user);
    setPassword(pass);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
      <div className="w-full max-w-md bg-[#0a1322] border-2 border-[#1a2d4f] bezel-tactical p-6 relative shadow-2xl text-left">
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
        <div className="flex items-center gap-2 mb-2 text-[#00ff66]">
          <ShieldAlert className="w-5 h-5" />
          <span className="font-['Press_Start_2P'] text-[11px] tracking-wider glow-radar">
            NAVAL SECURITY CLEARANCE
          </span>
        </div>
        <p className="text-xs text-[#8fa5bf] mb-6 font-mono">
          Enter authorized officer credentials to synchronize scores with the Admiralty mainframe.
        </p>

        {/* Quick Demo Fillers for Evaluators */}
        <div className="mb-5 p-2.5 border border-[#1a2d4f] bg-[#050b14] text-[11px] font-mono">
          <div className="text-[#ffaa00] font-bold mb-1.5 flex items-center gap-1">
            <span>&bull;</span> QUICK TEST CREDENTIALS:
          </div>
          <div className="flex flex-wrap gap-1.5">
            <button
              type="button"
              onClick={() => fillQuickCredentials('admin', 'admin123')}
              className="px-2 py-0.5 border border-[#ffaa00]/60 text-[#ffaa00] hover:bg-[#ffaa00]/15"
            >
              ADMIRAL [admin]
            </button>
            <button
              type="button"
              onClick={() => fillQuickCredentials('alvaro', 'password123')}
              className="px-2 py-0.5 border border-[#00ff66]/60 text-[#00ff66] hover:bg-[#00ff66]/15"
            >
              COMMANDER [alvaro]
            </button>
            <button
              type="button"
              onClick={() => fillQuickCredentials('adrian', 'password123')}
              className="px-2 py-0.5 border border-[#00e5ff]/60 text-[#00e5ff] hover:bg-[#00e5ff]/15"
            >
              LIEUTENANT [adrian]
            </button>
          </div>
        </div>

        {/* Tab Toggle */}
        <div className="flex border-b border-[#1a2d4f] mb-4">
          <button
            type="button"
            onClick={() => {
              sounds.playClick();
              setIsRegister(false);
            }}
            className={`flex-1 py-1.5 font-mono text-xs font-bold transition-colors ${
              !isRegister
                ? 'border-b-2 border-[#00ff66] text-[#00ff66] bg-[#00ff66]/5'
                : 'text-[#536780] hover:text-[#8fa5bf]'
            }`}
          >
            OFFICER LOGIN
          </button>
          <button
            type="button"
            onClick={() => {
              sounds.playClick();
              setIsRegister(true);
            }}
            className={`flex-1 py-1.5 font-mono text-xs font-bold transition-colors ${
              isRegister
                ? 'border-b-2 border-[#00ff66] text-[#00ff66] bg-[#00ff66]/5'
                : 'text-[#536780] hover:text-[#8fa5bf]'
            }`}
          >
            NEW COMMISSION
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-[10px] text-[#8fa5bf] font-mono uppercase mb-1">
              Officer Callsign (Username)
            </label>
            <div className="relative">
              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="e.g. Admiral_Nimitz"
                className="w-full bg-[#050b14] border border-[#1a2d4f] px-3 py-2 pl-9 text-xs text-[#00ff66] font-mono focus:outline-none focus:border-[#00ff66]"
              />
              <Key className="w-4 h-4 text-[#536780] absolute left-2.5 top-2.5" />
            </div>
          </div>

          {isRegister && (
            <div>
              <label className="block text-[10px] text-[#8fa5bf] font-mono uppercase mb-1">
                Dispatch Frequency (Email)
              </label>
              <div className="relative">
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="officer@fleet.navy.mil"
                  className="w-full bg-[#050b14] border border-[#1a2d4f] px-3 py-2 pl-9 text-xs text-[#00ff66] font-mono focus:outline-none focus:border-[#00ff66]"
                />
                <Mail className="w-4 h-4 text-[#536780] absolute left-2.5 top-2.5" />
              </div>
            </div>
          )}

          <div>
            <label className="block text-[10px] text-[#8fa5bf] font-mono uppercase mb-1">
              Clearance Cipher (Password)
            </label>
            <div className="relative">
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-[#050b14] border border-[#1a2d4f] px-3 py-2 pl-9 text-xs text-[#00ff66] font-mono focus:outline-none focus:border-[#00ff66]"
              />
              <Lock className="w-4 h-4 text-[#536780] absolute left-2.5 top-2.5" />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-2.5 bg-[#00ff66] hover:bg-[#00cc52] text-black font-['Press_Start_2P'] text-[10px] tracking-wider bezel-tactical-btn uppercase mt-2 font-bold disabled:opacity-50"
          >
            {loading ? 'AUTHENTICATING...' : isRegister ? 'COMMISSION RECRUIT' : 'GRANT CLEARANCE'}
          </button>
        </form>
      </div>
    </div>
  );
};
