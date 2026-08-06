package Model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class RoomTest {

    private static Map<String, Room> exits;
    private static List<Item> items;

    @BeforeAll
    public static void setUp() {
        exits = new HashMap<>();
        items = new ArrayList<>();
    }

    @Test
    @DisplayName("Room constructor1 correctly initializes the fields")
    void constructorTakesAllParams() {
        Room room = new Room("Airlock", "Woo", exits, items, Optional.of(null));
        assertAll("Room initialization",
                () -> assertEquals("Airlock", room.getName()),
                () -> assertEquals("Woo", room.getDescription()),
                () -> assertEquals(new HashMap<String, Room>(), room.getExits()),
                () -> assertEquals(new ArrayList<Item>(), room.getItemList()),
                () -> assertEquals(Optional.of(null), room.getOptionalEnemy())
                );
    }
}
