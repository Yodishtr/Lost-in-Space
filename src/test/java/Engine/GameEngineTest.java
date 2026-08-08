package Engine;

import Model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    private Map<String, Room> fakeWorld;
    private Player fakePlayer;
    private GameEngine fakeGameEnginePlaying;
    private GameEngine fakeGameEngineIntro;


    @BeforeEach
    void setUp() {
        fakeWorld = new HashMap<>();
        Room startingRoom = new Room("Start");
        Room itemRoom = new Room("ItemRoom");
        Room enemyRoom = new Room("EnemyRoom");
        fakeWorld.put("Start", startingRoom);
        fakeWorld.put("ItemRoom", itemRoom);
        fakeWorld.put("EnemyRoom", enemyRoom);

        // set up the exits, items and enemy for each room (as applicable)
        for (Map.Entry<String, Room> entry : fakeWorld.entrySet()) {
            String name = entry.getKey();
            Map<String, Room> exitMap = entry.getValue().getExits();
            String[] directions = {"North", "East", "South", "West"};
            switch (name) {
                case "Start":
                    for (String direction : directions) {
                        if (direction.equals("North")) {
                            exitMap.put("North", null);
                        } else if (direction.equals("East")) {
                            exitMap.put("East", enemyRoom);
                        } else if (direction.equals("South")) {
                            exitMap.put("South", itemRoom);
                        } else if (direction.equals("West")) {
                            exitMap.put("West", null);
                        }
                    }
                    break;
                case "ItemRoom":
                    for (String direction : directions) {
                        if (direction.equals("North")) {
                            exitMap.put("North", startingRoom);
                        } else if (direction.equals("East")) {
                            exitMap.put("East", null);
                        } else if (direction.equals("South")) {
                            exitMap.put("South", null);
                        } else if (direction.equals("West")) {
                            exitMap.put("West", null);
                        }
                    }
                    Item tool = new Item("Tool", "A tool to kill enemy", true);
                    itemRoom.addItem(tool);
                    break;
                case "EnemyRoom":
                    for (String direction : directions) {
                        if (direction.equals("North")) {
                            exitMap.put("North", null);
                        } else if (direction.equals("East")) {
                            exitMap.put("East", null);
                        } else if (direction.equals("South")) {
                            exitMap.put("South", null);
                        } else if (direction.equals("West")) {
                            exitMap.put("West", startingRoom);
                        }
                    }
                    Enemy theBadMan = new Enemy("Bad Mon", "Tool");
                    enemyRoom.setOptionalEnemy(theBadMan);
                    break;
            }
        }

        fakePlayer = new Player(startingRoom, 100, "HeroTest");
        fakeGameEnginePlaying = new GameEngine(GameState.PLAYING, fakePlayer);
        fakeGameEngineIntro = new GameEngine(GameState.INTRO, fakePlayer);
    }

    @Test
    @DisplayName("Constructor should initialize the instance attribute fields correctly")
    void testConstructor() {
        GameEngine testGameEngine = new GameEngine(GameState.INTRO, "fakePlayer");
        assertAll("fields correctly initialized",
                () -> assertEquals(GameState.INTRO, testGameEngine.getGameState()),
                () -> assertEquals("fakePlayer", testGameEngine.getPlayer().getName()),
                () -> assertEquals(5, testGameEngine.getRoomMap().size()),
                () -> assertTrue(testGameEngine.hasCommandParser())
                );
    }

    @Test
    @DisplayName("")
}
