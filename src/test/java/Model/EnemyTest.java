package Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    @Test
    @DisplayName("Constructor with all parameters")
    void testConstructorWithAllParameters() {
        Enemy e = new Enemy("E", "e", "i", "o");
        assertAll("First Constructor",
                () -> assertEquals("E", e.getName()),
                () -> assertEquals("e", e.getRequiredItemName()),
                () -> assertFalse(e.isResolved()),
                () -> assertEquals("i", e.getFailureMessage()),
                () -> assertEquals("o", e.getSuccessMessage())
                );
    }

    @Test
    @DisplayName("Enemy marked resolved stays resolved")
    void testEnemyResolved() {
        Enemy enemy = new Enemy("You", "Woo");
        assertFalse(enemy.isResolved());
        enemy.setResolved(true);
        assertTrue(enemy.isResolved());
    }

}
