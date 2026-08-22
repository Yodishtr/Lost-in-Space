package Engine;

import Model.Room;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBuilderTest {

    private static Map<String, Room> worldMap;

    @BeforeAll
    @DisplayName("Create the spaceship")
    public static void init() {
        worldMap = WorldBuilder.buildWorld();
    }

    @Test
    @DisplayName("Check if the number of rooms created is correct")
    void testRoomCount() {
        assertAll("Room initialization",
                () -> assertEquals(5, worldMap.size()),
                () -> assertTrue(worldMap.containsKey("Airlock")),
                () -> assertTrue(worldMap.containsKey("Armory")),
                () -> assertTrue(worldMap.containsKey("Cargo Bay")),
                () -> assertTrue(worldMap.containsKey("Engineering")),
                () -> assertTrue(worldMap.containsKey("Medical Bay"))
        );
    }

    @Test
    @DisplayName("Confirm exits are set up correct, ie they are either bidirectional or null")
    void testExits() {
        Map<String, Room> airlockExits = worldMap.get("Airlock").getExits();
        Map<String, Room> armoryExits = worldMap.get("Armory").getExits();
        Map<String, Room> cargoBayExits = worldMap.get("Cargo Bay").getExits();
        Map<String, Room> engineeringExits = worldMap.get("Engineering").getExits();
        Map<String, Room> medicalBayExits = worldMap.get("Medical Bay").getExits();

        assertAll("Airlock Exits",
                () -> assertEquals("Cargo Bay", airlockExits.get("NORTH").getName()),
                () -> assertEquals("Medical Bay", airlockExits.get("EAST").getName()),
                () -> assertEquals("Armory", airlockExits.get("SOUTH").getName()),
                () -> assertEquals("Engineering", airlockExits.get("WEST").getName())
        );

        assertAll("Armory Exits",
                () -> assertEquals("Airlock", armoryExits.get("NORTH").getName()),
                () -> assertEquals("Medical Bay", armoryExits.get("EAST").getName()),
                () -> assertThrows(NullPointerException.class, () -> armoryExits.get("SOUTH").getName()),
                () -> assertEquals("Engineering", armoryExits.get("WEST").getName())
        );

        assertAll("Cargo Bay Exits",
                () -> assertThrows(NullPointerException.class, () -> cargoBayExits.get("NORTH").getName()),
                () -> assertEquals("Medical Bay", cargoBayExits.get("EAST").getName()),
                () -> assertEquals("Airlock", cargoBayExits.get("SOUTH").getName()),
                () -> assertEquals("Engineering", cargoBayExits.get("WEST").getName())
        );

        assertAll("Engineering Bay Exits",
                () -> assertEquals("Cargo Bay", engineeringExits.get("NORTH").getName()),
                () -> assertEquals("Airlock", engineeringExits.get("EAST").getName()),
                () -> assertEquals("Armory", engineeringExits.get("SOUTH").getName()),
                () -> assertThrows(NullPointerException.class, () -> engineeringExits.get("WEST").getName())
                );

        assertAll("Medical Bay Exits",
                () -> assertEquals("Cargo Bay", medicalBayExits.get("NORTH").getName()),
                () -> assertThrows(NullPointerException.class, () -> medicalBayExits.get("EAST").getName()),
                () -> assertEquals("Armory", medicalBayExits.get("SOUTH").getName()),
                () -> assertEquals("Airlock", medicalBayExits.get("WEST").getName())
        );
    }


    @Test
    @DisplayName("Confirm Enemies are in correct rooms and there are no enemies in safe rooms")
    void testEnemies() {
        Room airlock = worldMap.get("Airlock");
        Room armory = worldMap.get("Armory");
        Room cargoBay = worldMap.get("Cargo Bay");
        Room engineering = worldMap.get("Engineering");
        Room medicalBay = worldMap.get("Medical Bay");

        assertAll("Enemy Presence",
                () -> assertTrue(airlock.getOptionalEnemy().isEmpty()),
                () -> assertTrue(armory.getOptionalEnemy().isPresent()),
                () -> assertTrue(cargoBay.getOptionalEnemy().isEmpty()),
                () -> assertTrue(engineering.getOptionalEnemy().isEmpty()),
                () -> assertTrue(medicalBay.getOptionalEnemy().isPresent())
        );
    }

    @Test
    @DisplayName("Ensure Items are placed in the right rooms")
    void testEnsureItems() {
        Room airlock = worldMap.get("Airlock");
        Room armory = worldMap.get("Armory");
        Room cargoBay = worldMap.get("Cargo Bay");
        Room engineering = worldMap.get("Engineering");
        Room medicalBay = worldMap.get("Medical Bay");

        assertAll("Items presence",
                () -> assertTrue(airlock.getItemList().isEmpty()),
                () -> assertTrue(armory.getItemList().isEmpty()),
                () -> assertFalse(cargoBay.getItemList().isEmpty()),
                () -> assertFalse(engineering.getItemList().isEmpty()),
                () -> assertTrue(medicalBay.getItemList().isEmpty())
                );
    }
}
