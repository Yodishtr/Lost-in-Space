package Model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class RoomTest {


    @Test
    @DisplayName("Room constructor1 correctly initializes the fields")
    void testConstructorTakesAllParams() {
        Map<String, Room> exits = new HashMap<>();
        List<Item> items = new ArrayList<>();
        Room room = new Room("Airlock", "Woo", exits, items, Optional.ofNullable(null));
        assertAll("Room initialization",
                () -> assertEquals("Airlock", room.getName()),
                () -> assertEquals("Woo", room.getDescription()),
                () -> assertEquals(new HashMap<String, Room>(), room.getExits()),
                () -> assertEquals(new ArrayList<Item>(), room.getItemList()),
                () -> assertEquals(Optional.ofNullable(null), room.getOptionalEnemy())
                );
    }

    @Test
    @DisplayName("Second room constructor initializes the fields correctly")
    void testConstructorTakesSecondParams() {
        Map<String, Room> exits = new HashMap<>();
        List<Item> items = new ArrayList<>();
        Room room = new Room("Airlock", "Woo");
        assertAll("Room initialization 2",
                () -> assertEquals("Airlock", room.getName()),
                () -> assertEquals("Woo", room.getDescription()),
                () -> assertEquals(exits, room.getExits()),
                () -> assertEquals(items, room.getItemList()),
                () -> assertEquals(Optional.ofNullable(null), room.getOptionalEnemy())
        );
    }

    @Test
    @DisplayName("Third room constructor initializes the fields correctly")
    void testConstructorTakesThirdParams() {
        Map<String, Room> exits = new HashMap<>();
        List<Item> items = new ArrayList<>();
        Room room = new Room("Airlock");
        assertAll("Room initialization 3",
                () -> assertEquals("Airlock", room.getName()),
                () -> assertEquals(null, room.getDescription()),
                () -> assertEquals(exits, room.getExits()),
                () -> assertEquals(items, room.getItemList()),
                () -> assertEquals(Optional.ofNullable(null), room.getOptionalEnemy())
        );
    }

    @Test
    @DisplayName("Adding and removing items from room behaves correctly")
    void testItemAddedThenRemoved() {
        Room room = new Room("Airlock", "Woo");
        Item random = new Item("Random", "Random", true);
        room.addItem(random);
        assertTrue(room.getItemList().contains(random));
        room.removeItem(random);
        assertFalse(room.getItemList().contains(random));
    }

    @Test
    @DisplayName("Creating a safe room and a danger room, the latter containing an enemy")
    void testRoomWithEnemy() {
        Room safeRoom = new Room("Airlock", "Woo");
        Room dangerRoom = new Room("Danger", "Woo2");
        Enemy danger = new Enemy("Chonk", "Coconut");
        dangerRoom.setOptionalEnemy(danger);
        assertTrue(safeRoom.getOptionalEnemy().isEmpty());
        assertFalse(dangerRoom.getOptionalEnemy().isEmpty());
        assertEquals(dangerRoom.getOptionalEnemy().get(), danger);
    }

    @Test
    @DisplayName("Should return the neighbouring room for valid exit and empty/null for invalid exit")
    void testExitsRoomMap() {
        Room room = new Room("Airlock", "Woo");
        Room westRoom = new Room("Woo", "Woo2");
        Room eastRoom = new Room("Airlock", "Woo");
        room.addExit("West", westRoom);
        room.addExit("East", eastRoom);
        room.addExit("North", null);
        room.addExit("South", null);
        westRoom.addExit("East", room);
        eastRoom.addExit("West", room);
        assertAll("Room exits testing",
                () -> assertEquals(westRoom, room.getExits().get("West")),
                () -> assertEquals(eastRoom, room.getExits().get("East")),
                () -> assertEquals(room, westRoom.getExits().get("East")),
                () -> assertEquals(room, eastRoom.getExits().get("West")),
                () -> assertEquals(null, room.getExits().get("North")),
                () -> assertEquals(null, room.getExits().get("South"))
        );
    }
}
