import React from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render } from '@testing-library/react';
import { WarmUp } from '../WarmUp';

describe('WarmUp Component', () => {
  const WARMUP_KEY = 'fleetgrid_warmup_timestamp';

  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(JSON.stringify({ status: 'UP' }))));
    sessionStorage.clear();

    // Mock sincrono de requestIdleCallback para jsdom
    window.requestIdleCallback = vi.fn((cb: IdleRequestCallback) => {
      cb({
        didTimeout: false,
        timeRemaining: () => 50,
      });
      return 1;
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
    sessionStorage.clear();
  });

  it('ejecuta peticion /healthz si no existe timestamp en sessionStorage', () => {
    render(<WarmUp />);

    expect(fetch).toHaveBeenCalledTimes(1);
    const calledUrl = vi.mocked(fetch).mock.calls[0][0] as string;
    expect(calledUrl).toContain('/healthz');
  });

  it('no ejecuta peticion si hay una marca de tiempo reciente (< 10 min)', () => {
    sessionStorage.setItem(WARMUP_KEY, Date.now().toString());

    render(<WarmUp />);

    expect(fetch).not.toHaveBeenCalled();
  });

  it('vuelve a ejecutar peticion si la marca de tiempo ha expirado (> 10 min)', () => {
    const expiredTimestamp = Date.now() - (11 * 60 * 1000);
    sessionStorage.setItem(WARMUP_KEY, expiredTimestamp.toString());

    render(<WarmUp />);

    expect(fetch).toHaveBeenCalledTimes(1);
  });
});
