package IO;

import Model.GameState;
import Model.Item;
import Model.Player;
import Model.Room;
import dto.SaveData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SaveManagerTest {


    @Test
    @DisplayName("test before setting up the mock save.txt")
    void testLoadNoSaveFile() {
        Optional<SaveData> potentialSaveData = SaveManager.load();
        assertTrue(potentialSaveData.isEmpty());
    }

    @Test
    @DisplayName("testing save method")
    void testSave() {
        Path testPath = Path.of("/tmp/Lost-In-Space-Test/save.txt");
        Player currentPlayer = new Player(new Room("Airlock"), 100, "Big Mon", 2, 2);
        currentPlayer.addItemToInventory(new Item("Zambo"));
        GameState currentGameState = GameState.PLAYING;

        /* Constructing a two room world and one got an item and the other not. */
        Map<String, Map<String, String>> outerMap = new HashMap<>();
        Map<String, String> innerMapAirlock = new HashMap<>();
        innerMapAirlock.put("items", "Bloc");
        innerMapAirlock.put("enemy_present", Boolean.toString(false));
        Map<String, String> innerMapRanch = new HashMap<>();
        innerMapRanch.put("items", "Ranch");
        innerMapRanch.put("enemy_present", Boolean.toString(true));
        outerMap.put("Airlock", innerMapAirlock);
        outerMap.put("Ranch", innerMapRanch);

        // constructing the save data dto
        SaveData justMadeSaveData = new SaveData(currentPlayer, outerMap, currentGameState);

        Integer result = SaveManager.save(justMadeSaveData, testPath);
        assertAll("game is successfully saved",
                () -> assertTrue(result == 1),
                () -> assertTrue(Files.exists(testPath))
                );

    }


    @Test
    @DisplayName("test load method by using the same save file as previous test")
    void testLoad() {
        Path testPath = Path.of("/tmp/Lost-In-Space-Test/save.txt");
        Optional<SaveData> potentialSaveData = SaveManager.load(testPath);
        assertAll("loads the save file correctly",
                () -> assertTrue(potentialSaveData.isPresent(), "save data absent"),
                () -> assertEquals("Big Mon", potentialSaveData.get().getCurrentPlayer().getName()),
                () -> assertEquals(100, potentialSaveData.get().getCurrentPlayer().getHealth()),
                () -> assertEquals("Airlock", potentialSaveData.get().getCurrentPlayer().
                        getCurrentRoom().getName()),
                () -> assertTrue(potentialSaveData.get().getCurrentPlayer().getInventory().containsKey("Zambo"),
                        "cannot retrieve the item from player"),
                () -> assertTrue(potentialSaveData.get().getCurrentGameState() == GameState.PLAYING,
                        "game state is not PLAYING"),
                () -> assertTrue(potentialSaveData.get().getCurrentWorld().containsKey("Ranch"),
                        "cannot retrieve the item Ranch from room"),
                () -> assertEquals("Ranch", potentialSaveData.get().getCurrentWorld().
                        get("Ranch").get("items"), "cannot find items from room"),
                () -> assertTrue(potentialSaveData.get().getCurrentWorld().containsKey("Airlock"),
                        "cannot find airlock room")
                );
    }
}
