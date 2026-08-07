package Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    @DisplayName("Constructor with initializes the fields correctly")
    void testConstructorWithAllParameters() {
        Room room = new Room("You", "Woo");
        Player player = new Player(room, 100, "Harri Puttar");
        assertAll("Constructor constructing",
                () -> assertEquals(room, player.getCurrentRoom()),
                () -> assertEquals(new HashMap<String, Item>(), player.getInventory()),
                () -> assertEquals(100, player.getHealth()),
                () -> assertEquals(0, player.getEnemiesDefeated()),
                () -> assertEquals("Harri Puttar", player.getName())
        );
    }

    @Test
    @DisplayName("Should show item was grabbed - good for handleTake")
    void testShowItemGrabbed() {
        Room room = new Room("You", "Woo");
        Item item = new Item("Dee Item", "Reeeel Bad Mon", true);
        Player player = new Player(room, 100, "Big Bad Mon");
        player.addItemToInventory(item);
        assertTrue(player.getInventory().containsKey("Dee Item"));
        assertTrue(player.getInventory().containsValue(item));
        player.removeItemFromInventory(item);
        assertFalse(player.getInventory().containsKey("Dee Item"));
        assertFalse(player.getInventory().containsValue(item));
    }

    @Test
    @DisplayName("Player has defeated enemies")
    void testPlayerHasDefeatedEnemies() {
        Room room = new Room("You", "Woo");
        Player player = new Player(room, 100, "Big Bad Mon");
        assertEquals(0, player.getEnemiesDefeated());
        player.addEnemyDefeated();
        assertEquals(1, player.getEnemiesDefeated());
    }
}
