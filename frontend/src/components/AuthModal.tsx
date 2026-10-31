'use client';

import React, { useState, useRef } from 'react';
import { X, Lock, Key, Mail, ShieldAlert, CheckCircle2, RotateCw } from 'lucide-react';
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
  /** Modo inicial con el que se abre el modal. */
  initialMode?: 'LOGIN' | 'REGISTER';
}

/**
 * Modal tactico de autenticacion militar con verificacion OTP de 6 digitos (Resend API)
 * y gestion de credenciales sin bypass docente.
 */
export const AuthModal: React.FC<AuthModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
  initialMode = 'LOGIN',
}) => {
  const [isRegister, setIsRegister] = useState(initialMode === 'REGISTER');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  // Estado para la fase de verificacion OTP
  const [isVerifying, setIsVerifying] = useState(false);
  const [otpDigits, setOtpDigits] = useState(['', '', '', '', '', '']);
  const [resending, setResending] = useState(false);
  const digitRefs = useRef<(HTMLInputElement | null)[]>([]);

  // Cerrar modal al presionar Escape
  React.useEffect(() => {
    if (!isOpen) return;
    const handleGlobalKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };
    window.addEventListener('keydown', handleGlobalKeyDown);
    return () => window.removeEventListener('keydown', handleGlobalKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const handleOtpChange = (index: number, value: string) => {
    const cleanVal = value.replace(/[^0-9]/g, '');
    const newDigits = [...otpDigits];

    if (cleanVal.length > 1) {
      // Manejo de pegado (Paste) de 6 digitos
      const pasted = cleanVal.slice(0, 6).split('');
      for (let i = 0; i < 6; i++) {
        newDigits[i] = pasted[i] || '';
      }
      setOtpDigits(newDigits);
      const nextFocus = Math.min(pasted.length, 5);
      digitRefs.current[nextFocus]?.focus();
      return;
    }

    newDigits[index] = cleanVal;
    setOtpDigits(newDigits);

    if (cleanVal && index < 5) {
      digitRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !otpDigits[index] && index > 0) {
      digitRefs.current[index - 1]?.focus();
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    sounds.playClick();
    setLoading(true);

    const cleanUsername = username.trim();
    const cleanEmail = email.trim().toLowerCase();

    try {
      if (isRegister) {
        if (!cleanEmail.includes('@')) {
          toast.error('VALID EMAIL REQUIRED FOR NAVAL DISPATCH');
          setLoading(false);
          return;
        }
        if (cleanUsername.length < 3) {
          toast.error('CALLSIGN MUST BE AT LEAST 3 CHARACTERS');
          setLoading(false);
          return;
        }
        if (password.length < 6) {
          toast.error('ACCESS CODE MUST BE AT LEAST 6 CHARACTERS');
          setLoading(false);
          return;
        }
        await api.register(cleanUsername, cleanEmail, password);
        sounds.playSonarPing();
        toast.success(`MILITARY PIN TRANSMITTED TO ${cleanEmail.toUpperCase()}`);
        setIsVerifying(true);
      } else {
        if (!cleanEmail.includes('@')) {
          toast.error('VALID EMAIL REQUIRED FOR SIGN IN');
          setLoading(false);
          return;
        }
        const res = await api.login(cleanEmail, password);
        toast.success(`CLEARANCE ACCEPTED // WELCOME BACK, ${res.username.toUpperCase()}`);
        onSuccess({
          username: res.username,
          email: res.email,
          role: res.role,
          emailVerified: res.emailVerified,
        });
        onClose();
      }
    } catch (err: unknown) {
      sounds.playShipSunkAlarm();
      const msg = err instanceof Error ? err.message : 'ACCESS DENIED: VERIFY CLEARANCE CREDENTIALS';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    const pin = otpDigits.join('');
    if (pin.length !== 6) {
      toast.error('ENTER COMPLETE 6-DIGIT MILITARY PIN');
      return;
    }

    sounds.playClick();
    setLoading(true);

    try {
      const res = await api.verifyEmail(email || username, pin);
      sounds.playVictoryFanfare();
      toast.success(`COMMISSION VERIFIED // WELCOME, OFFICER ${res.username.toUpperCase()}`);
      onSuccess({
        username: res.username,
        email: res.email,
        role: res.role,
        emailVerified: true,
      });
      onClose();
    } catch (err: unknown) {
      sounds.playShipSunkAlarm();
      const msg = err instanceof Error ? err.message : 'INVALID PIN: TRANSMISSION REJECTED';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleResendOtp = async () => {
    sounds.playClick();
    setResending(true);
    try {
      await api.resendCode(email || username);
      toast.success('NEW 6-DIGIT ACCESS PIN TRANSMITTED');
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'FAILED TO RESEND PIN';
      toast.error(msg);
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/85 backdrop-blur-sm p-4">
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
            {isVerifying ? 'SECURITY OTP VERIFICATION' : 'NAVAL SECURITY CLEARANCE'}
          </span>
        </div>

        <p className="text-xs text-[#8fa5bf] mb-5 font-mono">
          {isVerifying
            ? `Transmission dispatched to ${email || username}. Enter the 6-digit military PIN to authenticate your officer commission.`
            : 'Enter authorized officer credentials to record victories in the Admiralty mainframe.'}
        </p>

        {isVerifying ? (
          /* Step 2: OTP 6-Digit PIN Entry */
          <form onSubmit={handleVerifyOtp} className="space-y-5">
            <div>
              <label className="block text-xs font-mono text-[#00ff66] mb-3 uppercase tracking-wider text-center">
                [ 6-DIGIT MILITARY PIN ]
              </label>
              <div className="flex justify-between gap-2 max-w-xs mx-auto">
                {otpDigits.map((digit, idx) => (
                  <input
                    key={idx}
                    ref={(el) => {
                      digitRefs.current[idx] = el;
                    }}
                    type="text"
                    inputMode="numeric"
                    maxLength={1}
                    value={digit}
                    onChange={(e) => handleOtpChange(idx, e.target.value)}
                    onKeyDown={(e) => handleKeyDown(idx, e)}
                    className="w-11 h-12 text-center text-xl font-bold font-mono bg-[#050b14] border-2 border-[#1a2d4f] focus:border-[#00ff66] text-[#00ff66] focus:outline-none transition-colors"
                    autoFocus={idx === 0}
                  />
                ))}
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 px-4 bg-[#00ff66] text-black font-mono font-bold text-xs tracking-widest hover:bg-[#00cc52] transition-colors disabled:opacity-50 bezel-tactical-btn uppercase flex items-center justify-center gap-2"
            >
              <CheckCircle2 className="w-4 h-4" />
              {loading ? 'AUTHENTICATING PIN...' : 'CONFIRM CLEARANCE'}
            </button>

            <div className="flex items-center justify-between text-xs font-mono pt-2 border-t border-[#1a2d4f]">
              <button
                type="button"
                onClick={handleResendOtp}
                disabled={resending}
                className="text-[#ffaa00] hover:underline flex items-center gap-1"
              >
                <RotateCw className={`w-3.5 h-3.5 ${resending ? 'animate-spin' : ''}`} />
                {resending ? 'TRANSMITTING...' : 'RESEND PIN'}
              </button>

              <button
                type="button"
                onClick={() => {
                  sounds.playClick();
                  setIsVerifying(false);
                }}
                className="text-[#8fa5bf] hover:text-white"
              >
                &larr; BACK
              </button>
            </div>
          </form>
        ) : (
          /* Step 1: Login / Register Form */
          <>
            {/* Mode Selector */}
            <div className="flex border-b border-[#1a2d4f] mb-5">
              <button
                type="button"
                onClick={() => {
                  sounds.playClick();
                  setIsRegister(false);
                }}
                className={`flex-1 py-2 text-center font-mono text-xs font-bold transition-colors ${
                  !isRegister
                    ? 'border-b-2 border-[#00ff66] text-[#00ff66] bg-[#00ff66]/10'
                    : 'text-[#8fa5bf] hover:text-white'
                }`}
              >
                OFFICER SIGN IN
              </button>
              <button
                type="button"
                onClick={() => {
                  sounds.playClick();
                  setIsRegister(true);
                }}
                className={`flex-1 py-2 text-center font-mono text-xs font-bold transition-colors ${
                  isRegister
                    ? 'border-b-2 border-[#00ff66] text-[#00ff66] bg-[#00ff66]/10'
                    : 'text-[#8fa5bf] hover:text-white'
                }`}
              >
                NEW COMMISSION
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Registration: Callsign / Username */}
              {isRegister && (
                <div>
                  <label className="block text-xs font-mono text-[#8fa5bf] mb-1 uppercase tracking-wider">
                    Callsign / Username
                  </label>
                  <div className="relative">
                    <Key className="w-4 h-4 text-[#536780] absolute left-3 top-2.5" />
                    <input
                      type="text"
                      required
                      minLength={3}
                      maxLength={20}
                      pattern="^[a-zA-Z0-9_-]+$"
                      value={username}
                      onChange={(e) => setUsername(e.target.value)}
                      placeholder="e.g. ghost_01, alpha-7"
                      className="w-full bg-[#050b14] border border-[#1a2d4f] focus:border-[#00ff66] pl-9 pr-3 py-2 text-xs font-mono text-white focus:outline-none transition-colors"
                    />
                  </div>
                  <span className="text-[10px] text-[#536780] font-mono mt-1 block">
                    Letters, numbers, underscores (_), and hyphens (-) allowed (3-20 chars). Displayed on Admiralty Roll.
                  </span>
                </div>
              )}

              {/* Login and Registration: Official Email */}
              <div>
                <label className="block text-xs font-mono text-[#8fa5bf] mb-1 uppercase tracking-wider">
                  Official Email
                </label>
                <div className="relative">
                  <Mail className="w-4 h-4 text-[#536780] absolute left-3 top-2.5" />
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="officer@navy.mil"
                    className="w-full bg-[#050b14] border border-[#1a2d4f] focus:border-[#00ff66] pl-9 pr-3 py-2 text-xs font-mono text-white focus:outline-none transition-colors"
                  />
                </div>
                <span className="text-[10px] text-[#536780] font-mono mt-1 block">
                  {isRegister
                    ? 'A 6-digit confirmation PIN will be dispatched to this address.'
                    : 'Enter your registered email address to authorize command access.'}
                </span>
              </div>

              <div>
                <label className="block text-xs font-mono text-[#8fa5bf] mb-1 uppercase tracking-wider">
                  Access Code / Password
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 text-[#536780] absolute left-3 top-2.5" />
                  <input
                    type="password"
                    required
                    minLength={6}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full bg-[#050b14] border border-[#1a2d4f] focus:border-[#00ff66] pl-9 pr-3 py-2 text-xs font-mono text-white focus:outline-none transition-colors"
                  />
                </div>
                {isRegister && (
                  <span className="text-[10px] text-[#536780] font-mono mt-1 block">
                    Minimum 6 characters required for security clearance.
                  </span>
                )}
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full mt-2 py-2.5 px-4 bg-[#00ff66] text-black font-mono font-bold text-xs tracking-widest hover:bg-[#00cc52] transition-colors disabled:opacity-50 bezel-tactical-btn uppercase flex items-center justify-center gap-2"
              >
                {loading
                  ? 'TRANSMITTING CREDENTIALS...'
                  : isRegister
                  ? 'DISPATCH COMMISSION & GET PIN'
                  : 'AUTHORIZE ENTRY'}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
};
