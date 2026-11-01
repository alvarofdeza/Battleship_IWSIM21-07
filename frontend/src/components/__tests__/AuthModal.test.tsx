import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { AuthModal } from '../AuthModal';

// Mock de audio y api
vi.mock('@/lib/sound', () => ({
  sounds: {
    playClick: vi.fn(),
    playSonarPing: vi.fn(),
    playShipSunkAlarm: vi.fn(),
    playVictoryFanfare: vi.fn(),
  },
}));

vi.mock('@/lib/api', () => ({
  api: {
    login: vi.fn(),
    register: vi.fn(),
    verifyEmail: vi.fn(),
    resendCode: vi.fn(),
    forgotPassword: vi.fn(),
    resetPassword: vi.fn(),
  },
}));

vi.mock('sonner', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('AuthModal Component', () => {
  const onClose = vi.fn();
  const onSuccess = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('no renderiza nada si isOpen es false', () => {
    const { container } = render(
      <AuthModal isOpen={false} onClose={onClose} onSuccess={onSuccess} />
    );
    expect(container.firstChild).toBeNull();
  });

  it('renderiza la interfaz de inicio de sesion por defecto', () => {
    render(<AuthModal isOpen={true} onClose={onClose} onSuccess={onSuccess} initialMode="LOGIN" />);

    expect(screen.getByText('NAVAL SECURITY CLEARANCE')).toBeDefined();
    expect(screen.getByPlaceholderText('officer@navy.mil')).toBeDefined();
    expect(screen.getByPlaceholderText('••••••••')).toBeDefined();
    expect(screen.getByRole('button', { name: /AUTHORIZE ENTRY/i })).toBeDefined();
  });

  it('permite conmutar a la pantalla de reclutamiento (REGISTER)', () => {
    render(<AuthModal isOpen={true} onClose={onClose} onSuccess={onSuccess} initialMode="LOGIN" />);

    const recruitTab = screen.getByRole('button', { name: /NEW COMMISSION/i });
    fireEvent.click(recruitTab);

    expect(screen.getByPlaceholderText('e.g. ghost_01, alpha-7')).toBeDefined();
    expect(screen.getByRole('button', { name: /DISPATCH COMMISSION & GET PIN/i })).toBeDefined();
  });

  it('distribuye los 6 digitos al pegar un codigo PIN completo', async () => {
    const { api } = await import('@/lib/api');
    vi.mocked(api.register).mockResolvedValue({
      token: 'jwt-mock',
      type: 'Bearer',
      username: 'cadet01',
      email: 'cadet01@fleetgrid.me',
      role: 'ROLE_USER',
      emailVerified: false,
    });

    render(<AuthModal isOpen={true} onClose={onClose} onSuccess={onSuccess} initialMode="REGISTER" />);

    // Rellenar formulario y enviar registro
    fireEvent.change(screen.getByPlaceholderText('e.g. ghost_01, alpha-7'), { target: { value: 'cadet01' } });
    fireEvent.change(screen.getByPlaceholderText('officer@navy.mil'), { target: { value: 'cadet01@fleetgrid.me' } });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'password123' } });

    fireEvent.click(screen.getByRole('button', { name: /DISPATCH COMMISSION & GET PIN/i }));

    // Se transiciona a la vista VERIFY
    const title = await screen.findByText('SECURITY OTP VERIFICATION');
    expect(title).toBeDefined();

    // Comprobar presencia de las 6 casillas OTP
    const inputs = screen.getAllByRole('textbox');
    expect(inputs).toHaveLength(6);

    // Pegar codigo de 6 digitos en el primer input
    fireEvent.paste(inputs[0], {
      clipboardData: {
        getData: () => '852074',
      },
    });

    // Comprobar que cada digito se coloco en su casilla correspondiente
    expect((inputs[0] as HTMLInputElement).value).toBe('8');
    expect((inputs[1] as HTMLInputElement).value).toBe('5');
    expect((inputs[2] as HTMLInputElement).value).toBe('2');
    expect((inputs[3] as HTMLInputElement).value).toBe('0');
    expect((inputs[4] as HTMLInputElement).value).toBe('7');
    expect((inputs[5] as HTMLInputElement).value).toBe('4');
  });
});
