import { test, expect } from '@playwright/test';

test.describe('FleetGrid Tactical Naval Command - Critical User Journeys', () => {

  test('Flujo 1: Partida rapida - Colocacion de flota en dique seco y ejecucion de disparos sincronizados', async ({ page }) => {
    // Mock del endpoint healthz
    await page.route('**/healthz', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ status: 'UP', message: 'FleetGrid Mainframe Operational' }),
      });
    });

    // Mock del inicio de partida
    await page.route('**/api/v1/games', async (route) => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 201,
          contentType: 'application/json',
          body: JSON.stringify({
            gameId: 'mock-session-001',
            player1: 'Guest',
            player2: 'Admiralty AI (HARD)',
            status: 'IN_PROGRESS',
            myFleetGrid: Array(10).fill('..........'),
            enemyFogGrid: Array(10).fill('..........'),
          }),
        });
      }
    });

    // Mock del disparo
    await page.route('**/api/v1/games/mock-session-001/shots', async (route) => {
      const fogWithHit = Array(10).fill('..........');
      fogWithHit[0] = 'H.........';
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          hit: true,
          sunk: false,
          isFinished: false,
          status: 'IN_PROGRESS',
          winner: null,
          aiCounterAttack: { row: 2, col: 2, hit: false, sunk: false },
          myFleetGrid: Array(10).fill('..........'),
          enemyFogGrid: fogWithHit,
          newLogs: [
            { moveNumber: 1, shooter: 'Guest', target: 'A1', result: 'HIT', sunkShipType: null },
            { moveNumber: 2, shooter: 'Admiralty AI', target: 'C3', result: 'MISS', sunkShipType: null },
          ],
        }),
      });
    });

    await page.goto('/');

    // 1. Verificar titulo y presencia del dique seco
    await expect(page.locator("h2:has-text('FLEET ANCHORAGE')")).toBeVisible();

    // 2. Autodespliegue de flota tactica
    const autoDeployBtn = page.getByRole('button', { name: /AUTO-DEPLOY/i });
    await expect(autoDeployBtn).toBeVisible();
    await autoDeployBtn.click();

    // 3. Iniciar combate como invitado
    const commenceBtn = page.getByRole('button', { name: /COMMENCE COMBAT/i });
    await expect(commenceBtn).toBeVisible();
    await commenceBtn.click();

    // 4. Verificar transicion a la fase de combate (Targeting Radar visible)
    await expect(page.locator("h3:has-text('TARGETING RADAR')")).toBeVisible();

    // 5. Efectuar disparo en casilla A1 (atk-cell-0-0)
    const cellA1 = page.locator('#atk-cell-0-0');
    await expect(cellA1).toBeVisible();
    await cellA1.click();

    // 6. Verificar que la celda se actualiza y el log de telemetria registra el disparo
    await expect(page.locator('text=HIT')).toBeVisible();
  });

  test('Flujo 2: Resolucion de combate como invitado y modal de reclamacion de puntos', async ({ page }) => {
    await page.route('**/healthz', async (route) => {
      await route.fulfill({ status: 200, body: JSON.stringify({ status: 'UP' }) });
    });

    await page.route('**/api/v1/games', async (route) => {
      await route.fulfill({
        status: 201,
        body: JSON.stringify({
          gameId: 'mock-session-win',
          player1: 'Guest',
          player2: 'Admiralty AI (HARD)',
          status: 'IN_PROGRESS',
          myFleetGrid: Array(10).fill('..........'),
          enemyFogGrid: Array(10).fill('..........'),
        }),
      });
    });

    // Simular disparo que hunde la ultima nave y otorga la victoria
    await page.route('**/api/v1/games/mock-session-win/shots', async (route) => {
      await route.fulfill({
        status: 200,
        body: JSON.stringify({
          hit: true,
          sunk: true,
          isFinished: true,
          status: 'FINISHED',
          winner: 'Guest',
          points: 1850,
          movesCount: 22,
          durationSeconds: 95,
          result: 'VICTORY',
          aiCounterAttack: null,
          myFleetGrid: Array(10).fill('..........'),
          enemyFogGrid: Array(10).fill('HHHHHHHHHH'),
          newLogs: [
            { moveNumber: 22, shooter: 'Guest', target: 'B4', result: 'SUNK', sunkShipType: 'PATROL_BOAT' },
          ],
        }),
      });
    });

    await page.goto('/');

    // Autodesplegar y comenzar
    await page.getByRole('button', { name: /AUTO-DEPLOY/i }).click();
    await page.getByRole('button', { name: /COMMENCE COMBAT/i }).click();

    // Disparar en A1 para ganar
    await page.locator('#atk-cell-0-0').click();

    // Verificar pantalla de fin de combate con desglose de puntos
    await expect(page.locator("h2:has-text('COMBAT OPERATION COMPLETE')")).toBeVisible();
    await expect(page.locator("text=1850")).toBeVisible();

    // Boton de reclamar puntos para invitados
    const claimBtn = page.getByRole('button', { name: /CLAIM POINTS & RECORD ON ADMIRALTY ROLL/i });
    await expect(claimBtn).toBeVisible();
  });

  test('Flujo 3: Verificacion militar OTP y gestion de credenciales de oficial', async ({ page }) => {
    await page.route('**/healthz', async (route) => {
      await route.fulfill({ status: 200, body: JSON.stringify({ status: 'UP' }) });
    });

    await page.goto('/');

    // Abrir modal de autorizacion
    const authBtn = page.getByRole('button', { name: /OFFICER LOGIN/i });
    if (await authBtn.isVisible()) {
      await authBtn.click();
    } else {
      // Si esta el boton de SignIn en Header
      await page.locator("button:has-text('LOGIN')").first().click();
    }

    // Verificar presencia del modal militar
    await expect(page.locator("span:has-text('NAVAL SECURITY CLEARANCE')")).toBeVisible();

    // Conmutar a Nueva Comision (Registro)
    await page.getByRole('button', { name: /NEW COMMISSION/i }).click();
    await expect(page.getByPlaceholderText('e.g. ghost_01, alpha-7')).toBeVisible();

    // Conmutar a Recuperacion de Credenciales
    await page.getByRole('button', { name: /OFFICER SIGN IN/i }).click();
    await page.getByRole('button', { name: /FORGOT PASSWORD\?/i }).click();
    await expect(page.locator("span:has-text('CREDENTIALS RECOVERY')")).toBeVisible();
  });

});
