import React from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render } from '@testing-library/react';
import { WebMetrics } from '../WebMetrics';

// Mock de los componentes de Vercel para comprobar su montaje condicional sin llamadas de red reales
vi.mock('@vercel/analytics/next', () => ({
  Analytics: () => <div data-testid="vercel-analytics" />,
}));

vi.mock('@vercel/speed-insights/next', () => ({
  SpeedInsights: () => <div data-testid="vercel-speed-insights" />,
}));

describe('WebMetrics Component', () => {
  const originalEnv = process.env.NEXT_PUBLIC_ENABLE_VERCEL_METRICS;

  afterEach(() => {
    process.env.NEXT_PUBLIC_ENABLE_VERCEL_METRICS = originalEnv;
  });

  it('renders null when NEXT_PUBLIC_ENABLE_VERCEL_METRICS is not set', () => {
    delete process.env.NEXT_PUBLIC_ENABLE_VERCEL_METRICS;
    const { container } = render(<WebMetrics />);
    expect(container.firstChild).toBeNull();
  });

  it('renders null when NEXT_PUBLIC_ENABLE_VERCEL_METRICS is explicitly "false"', () => {
    process.env.NEXT_PUBLIC_ENABLE_VERCEL_METRICS = 'false';
    const { container } = render(<WebMetrics />);
    expect(container.firstChild).toBeNull();
  });

  it('renders Analytics and SpeedInsights when NEXT_PUBLIC_ENABLE_VERCEL_METRICS is "true"', () => {
    process.env.NEXT_PUBLIC_ENABLE_VERCEL_METRICS = 'true';
    const { getByTestId } = render(<WebMetrics />);
    expect(getByTestId('vercel-analytics')).toBeDefined();
    expect(getByTestId('vercel-speed-insights')).toBeDefined();
  });
});
