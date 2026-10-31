package es.upm.etsisi.fis.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para la jerarquía de naves y habilidades tácticas especiales.
 */
class ShipsTest {

    @Test
    @DisplayName("Carrier tiene longitud 5 y gestiona ciclo de impactos y hundimiento")
    void testCarrier() {
        Carrier carrier = new Carrier();
        assertThat(carrier.getLength()).isEqualTo(5);
        assertThat(carrier.getName()).isEqualTo("Carrier");
        assertThat(carrier.isAlive()).isTrue();
        assertThat(carrier.getHitsTaken()).isZero();

        carrier.registerHit();
        carrier.registerHit();
        assertThat(carrier.getHitsTaken()).isEqualTo(2);
        assertThat(carrier.isAlive()).isTrue();

        carrier.sink();
        assertThat(carrier.isAlive()).isFalse();
        assertThat(carrier.getHitsTaken()).isEqualTo(5);
    }

    @Test
    @DisplayName("Battleship tiene longitud 4 y gestiona disparos de artillería pesada")
    void testBattleship() {
        Battleship battleship = new Battleship();
        assertThat(battleship.getLength()).isEqualTo(4);
        assertThat(battleship.getName()).isEqualTo("Battleship");
        assertThat(battleship.getAvailableArtilleryShots()).isEqualTo(1);

        battleship.reduceArtilleryShots();
        assertThat(battleship.getAvailableArtilleryShots()).isZero();
        battleship.reduceArtilleryShots(); // Límite inferior
        assertThat(battleship.getAvailableArtilleryShots()).isZero();

        for (int i = 0; i < 4; i++) {
            battleship.registerHit();
        }
        assertThat(battleship.isAlive()).isFalse();
    }

    @Test
    @DisplayName("Submarine tiene longitud 3 y puede autorrepararse una vez")
    void testSubmarine() {
        Submarine submarine = new Submarine();
        assertThat(submarine.getLength()).isEqualTo(3);
        assertThat(submarine.getName()).isEqualTo("Submarine");
        assertThat(submarine.isRepaired()).isFalse();

        submarine.registerHit();
        assertThat(submarine.getHitsTaken()).isEqualTo(1);

        submarine.markRepaired();
        assertThat(submarine.isRepaired()).isTrue();
        assertThat(submarine.getHitsTaken()).isZero(); // Restauró un impacto
        assertThat(submarine.isAlive()).isTrue();
    }

    @Test
    @DisplayName("PatrolBoat tiene longitud 2 y activa maniobras de evasión")
    void testPatrolBoat() {
        PatrolBoat patrolBoat = new PatrolBoat();
        assertThat(patrolBoat.getLength()).isEqualTo(2);
        assertThat(patrolBoat.getName()).isEqualTo("PatrolBoat");
        assertThat(patrolBoat.isEvasionActive()).isFalse();

        patrolBoat.activateEvasion();
        assertThat(patrolBoat.isEvasionActive()).isTrue();

        patrolBoat.addPosition(0, 0);
        patrolBoat.addPosition(0, 1);
        assertThat(patrolBoat.getPositions()).hasSize(2);

        patrolBoat.clearPositions();
        assertThat(patrolBoat.getPositions()).isEmpty();
    }
}
