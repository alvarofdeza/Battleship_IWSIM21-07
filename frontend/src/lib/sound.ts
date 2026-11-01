/**
 * Gestor y sintetizador de efectos sonoros retro-navales mediante Web Audio API.
 * Genera audio puramente en el cliente mediante osciladores, filtros y generadores de ruido,
 * eliminando cualquier dependencia de archivos multimedia externos.
 */
class SoundManager {
  private ctx: AudioContext | null = null;
  private masterGain: GainNode | null = null;
  private muted: boolean = false;
  private volume: number = 0.3;

  constructor() {
    if (typeof window !== 'undefined') {
      const savedMuted = localStorage.getItem('battleship_sfx_muted');
      this.muted = savedMuted === 'true';

      const savedVol = localStorage.getItem('battleship_sfx_volume');
      if (savedVol !== null) {
        const parsed = parseFloat(savedVol);
        if (!isNaN(parsed) && parsed >= 0 && parsed <= 1) {
          this.volume = parsed;
        }
      }
    }
  }

  /**
   * Inicializa o reanuda el contexto de audio del navegador tras una accion de usuario.
   */
  private initContext() {
    if (typeof window === 'undefined') return;
    if (!this.ctx) {
      const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      if (AudioCtx) {
        this.ctx = new AudioCtx();
      }
    }
    if (this.ctx) {
      if (this.ctx.state === 'suspended') {
        this.ctx.resume();
      }
      if (!this.masterGain) {
        this.masterGain = this.ctx.createGain();
        this.masterGain.gain.setValueAtTime(this.muted ? 0 : this.volume, this.ctx.currentTime);
        this.masterGain.connect(this.ctx.destination);
      }
    }
  }

  /**
   * Indica si los efectos sonoros se encuentran actualmente silenciados.
   *
   * @returns Verdadero si el audio esta silenciado.
   */
  public isMuted(): boolean {
    return this.muted;
  }

  /**
   * Retorna el nivel de volumen master configurado (0.0 a 1.0).
   */
  public getVolume(): number {
    return this.volume;
  }

  /**
   * Modifica el volumen maestro del sintetizador y lo persiste localmente.
   */
  public setVolume(vol: number): void {
    this.volume = Math.max(0, Math.min(1, vol));
    if (typeof window !== 'undefined') {
      localStorage.setItem('battleship_sfx_volume', String(this.volume));
    }
    if (this.masterGain && this.ctx && !this.muted) {
      this.masterGain.gain.setValueAtTime(this.volume, this.ctx.currentTime);
    }
  }

  /**
   * Alterna el estado de silenciado y persiste la preferencia en el almacenamiento local.
   *
   * @returns El nuevo estado de silenciado.
   */
  public toggleMute(): boolean {
    this.muted = !this.muted;
    if (typeof window !== 'undefined') {
      localStorage.setItem('battleship_sfx_muted', String(this.muted));
    }
    if (this.masterGain && this.ctx) {
      this.masterGain.gain.setValueAtTime(this.muted ? 0 : this.volume, this.ctx.currentTime);
    }
    return this.muted;
  }

  /**
   * Conecta un nodo de audio al bus de ganancia master.
   */
  private connectToMaster(node: AudioNode) {
    if (this.masterGain) {
      node.connect(this.masterGain);
    } else if (this.ctx) {
      node.connect(this.ctx.destination);
    }
  }

  /**
   * Reproduce un sonido mecanico breve simulando la pulsacion de un conmutador fisico de terminal.
   */
  public playClick() {
    if (this.muted) return;
    this.initContext();
    if (!this.ctx) return;

    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();

    osc.type = 'square';
    osc.frequency.setValueAtTime(800, this.ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(400, this.ctx.currentTime + 0.03);

    gain.gain.setValueAtTime(0.08, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + 0.03);

    osc.connect(gain);
    this.connectToMaster(gain);

    osc.start();
    osc.stop(this.ctx.currentTime + 0.03);
  }

  /**
   * Reproduce un impulso acustico de alta frecuencia que simula el eco de un sonar naval.
   */
  public playSonarPing() {
    if (this.muted) return;
    this.initContext();
    if (!this.ctx) return;

    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(1400, this.ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(1350, this.ctx.currentTime + 0.4);

    gain.gain.setValueAtTime(0.12, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + 0.4);

    osc.connect(gain);
    this.connectToMaster(gain);

    osc.start();
    osc.stop(this.ctx.currentTime + 0.4);
  }

  /**
   * Reproduce el sonido de detonacion y lanzamiento de un proyectil de artilleria naval.
   */
  public playArtilleryLaunch() {
    if (this.muted) return;
    this.initContext();
    if (!this.ctx) return;

    const bufferSize = this.ctx.sampleRate * 0.2;
    const buffer = this.ctx.createBuffer(1, bufferSize, this.ctx.sampleRate);
    const data = buffer.getChannelData(0);
    for (let i = 0; i < bufferSize; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noise = this.ctx.createBufferSource();
    noise.buffer = buffer;

    const filter = this.ctx.createBiquadFilter();
    filter.type = 'bandpass';
    filter.frequency.setValueAtTime(300, this.ctx.currentTime);
    filter.frequency.exponentialRampToValueAtTime(80, this.ctx.currentTime + 0.2);

    const gain = this.ctx.createGain();
    gain.gain.setValueAtTime(0.25, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + 0.2);

    noise.connect(filter);
    filter.connect(gain);
    this.connectToMaster(gain);

    noise.start();
  }

  /**
   * Reproduce un sonido de chapoteo sordo correspondiente a un proyectil impactando en agua.
   */
  public playWaterSplash() {
    if (this.muted) return;
    this.initContext();
    if (!this.ctx) return;

    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(450, this.ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(120, this.ctx.currentTime + 0.25);

    gain.gain.setValueAtTime(0.15, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + 0.25);

    osc.connect(gain);
    this.connectToMaster(gain);

    osc.start();
    osc.stop(this.ctx.currentTime + 0.25);
  }

  /**
   * Reproduce una explosion contundente con crujido sintetizado al impactar sobre un navio enemigo.
   */
  public playExplosion() {
    if (this.muted) return;
    this.initContext();
    if (!this.ctx) return;

    const bufferSize = this.ctx.sampleRate * 0.45;
    const buffer = this.ctx.createBuffer(1, bufferSize, this.ctx.sampleRate);
    const data = buffer.getChannelData(0);
    for (let i = 0; i < bufferSize; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noise = this.ctx.createBufferSource();
    noise.buffer = buffer;

    const filter = this.ctx.createBiquadFilter();
    filter.type = 'lowpass';
    filter.frequency.setValueAtTime(800, this.ctx.currentTime);
    filter.frequency.exponentialRampToValueAtTime(60, this.ctx.currentTime + 0.45);

    const osc = this.ctx.createOscillator();
    osc.type = 'sawtooth';
    osc.frequency.setValueAtTime(150, this.ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(30, this.ctx.currentTime + 0.45);

    const gain = this.ctx.createGain();
    gain.gain.setValueAtTime(0.35, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + 0.45);

    noise.connect(filter);
    filter.connect(gain);
    osc.connect(gain);
    this.connectToMaster(gain);

    noise.start();
    osc.start();
    osc.stop(this.ctx.currentTime + 0.45);
  }

  /**
   * Reproduce la alarma de sirena militar de doble tono que indica el hundimiento de un barco.
   */
  public playShipSunkAlarm() {
    if (this.muted) return;
    this.initContext();
    if (!this.ctx) return;

    for (let i = 0; i < 2; i++) {
      const osc = this.ctx.createOscillator();
      const gain = this.ctx.createGain();

      osc.type = 'square';
      const startTime = this.ctx.currentTime + i * 0.25;

      osc.frequency.setValueAtTime(600, startTime);
      osc.frequency.setValueAtTime(450, startTime + 0.12);

      gain.gain.setValueAtTime(0.15, startTime);
      gain.gain.exponentialRampToValueAtTime(0.001, startTime + 0.22);

      osc.connect(gain);
      this.connectToMaster(gain);

      osc.start(startTime);
      osc.stop(startTime + 0.22);
    }
  }

  /**
   * Reproduce un arpegio ascendente de 8 bits que celebra la victoria de la partida.
   */
  public playVictoryFanfare() {
    if (this.muted) return;
    this.initContext();
    if (!this.ctx) return;

    const notes = [261.63, 329.63, 392.0, 523.25]; // C4, E4, G4, C5
    notes.forEach((freq, idx) => {
      if (!this.ctx) return;
      const osc = this.ctx.createOscillator();
      const gain = this.ctx.createGain();

      osc.type = 'triangle';
      const startTime = this.ctx.currentTime + idx * 0.12;

      osc.frequency.setValueAtTime(freq, startTime);

      gain.gain.setValueAtTime(0.2, startTime);
      gain.gain.exponentialRampToValueAtTime(0.001, startTime + (idx === notes.length - 1 ? 0.6 : 0.2));

      osc.connect(gain);
      this.connectToMaster(gain);

      osc.start(startTime);
      osc.stop(startTime + (idx === notes.length - 1 ? 0.6 : 0.2));
    });
  }
}

export const sounds = new SoundManager();
