package Engine;

import Model.*;
import dto.CommandResult;
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
        Room startingRoom = new Room("Airlock");
        Room itemRoom = new Room("Cargo Bay");
        Room enemyRoom = new Room("Armory");
        fakeWorld.put("Airlock", startingRoom);
        fakeWorld.put("Cargo Bay", itemRoom);
        fakeWorld.put("Armory", enemyRoom);

        // set up the exits, items and enemy for each room (as applicable)
        for (Map.Entry<String, Room> entry : fakeWorld.entrySet()) {
            String name = entry.getKey();
            Map<String, Room> exitMap = entry.getValue().getExits();
            String[] directions = {"North", "East", "South", "West"};
            switch (name) {
                case "Airlock":
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
                case "Cargo Bay":
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
                    Item anotherTool = new Item("Spare Wrench", "Wonk Wonk and the bolts come off",
                            true);
                    itemRoom.addItem(tool);
                    itemRoom.addItem(anotherTool);
                    break;
                case "Armory":
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
                    theBadMan.setFailureMessage("You Woo.");
                    theBadMan.setSuccessMessage("You not Woo.");
                    enemyRoom.setOptionalEnemy(theBadMan);
                    break;
            }
        }

        fakePlayer = new Player(startingRoom, 100, "HeroTest", 1,
                1);
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
    @DisplayName("handleGo method moves player in the correct room when given the right direction.")
    void testHandleGoCorrectInput() {
        // player told to go south from starting room
        Command.CommandType goCommand = Command.CommandType.GO;
        Command command = new Command(goCommand, "South");
        CommandResult commandResult = fakeGameEnginePlaying.handleGo(command);
        assertAll("Command Result Fields initialized",
                () -> assertTrue(commandResult.isChangeImage()),
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertEquals("cargo_bay", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertEquals("You head to the Cargo Bay", commandResult.getDisplayMessage().getFirst()),
                () -> assertEquals("Cargo Bay", fakePlayer.getCurrentRoom().getName())
                );
    }

    @Test
    @DisplayName("handleGo method was not provided with a direction.")
    void testNoDirectionProvided() {
        Command.CommandType goCommand = Command.CommandType.GO;
        Command command = new Command(goCommand, null);
        CommandResult commandResult = fakeGameEnginePlaying.handleGo(command);
        assertAll("No Direction Given.",
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertEquals("airlock", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertEquals(fakePlayer.getName() + " did not specify a direction (North, South, East, West).",
                        commandResult.getDisplayMessage().getFirst()),
                () -> assertEquals("Roaming around the room.", commandResult.getDisplayMessage().get(1))
                );
    }

    @Test
    @DisplayName("handleGo method provided with incorrect direction while player health full with enemy in room")
    void testEnemyRoomAndWrongDirection() {
        fakePlayer.setCurrentRoom(fakeWorld.get("Armory"));
        Command.CommandType goCommand = Command.CommandType.GO;
        Command command = new Command(goCommand, "South");
        CommandResult commandResult = fakeGameEnginePlaying.handleGo(command);
        assertAll("Enemy room at full health",
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertEquals("armory", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertEquals("You Woo", commandResult.getDisplayMessage().getFirst()),
                () -> assertTrue(fakePlayer.getHealth() == 50)
                );
    }

    @Test
    @DisplayName("handleGo method provided with incorrect direction while player health low with enemy in room")
    void testEnemyRoomAndWrongDirectionAndLowHealth() {
        fakePlayer.setCurrentRoom(fakeWorld.get("Armory"));
        fakePlayer.setHealth(10);
        Command.CommandType goCommand = Command.CommandType.GO;
        Command command = new Command(goCommand, "South");
        CommandResult commandResult = fakeGameEnginePlaying.handleGo(command);
        assertAll("Enemy room at low health",
                () -> assertTrue(commandResult.isChangeImage()),
                () -> assertTrue(commandResult.isGameOver()),
                () -> assertEquals("/images/lost.png", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertEquals("You Died!", commandResult.getDisplayMessage().getFirst())
        );
    }

    @Test
    @DisplayName("handleGo method provided with incorrect direction while player in enemy room but enemy defeated")
    void testEnemyRoomWithNoEnemyButWrongDirection() {
        fakePlayer.setCurrentRoom(fakeWorld.get("Armory"));
        fakeWorld.get("Armory").getOptionalEnemy().get().setResolved(true);
        Command.CommandType goCommand = Command.CommandType.GO;
        Command command = new Command(goCommand, "South");
        CommandResult commandResult = fakeGameEnginePlaying.handleGo(command);
        assertAll("Enemy Room with no enemy",
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertEquals("armory", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertEquals("You cannot go there.", commandResult.getDisplayMessage().getFirst()),
                () -> assertEquals("There is a wall.", commandResult.getDisplayMessage().get(1))
                );
    }

    @Test
    @DisplayName("handleGo method provided with correct direction while in enemy room but enemy is not defeated")
    void testCorrectDirectionInEnemyRoom() {
        fakePlayer.setCurrentRoom(fakeWorld.get("Armory"));
        fakeWorld.get("Armory").getOptionalEnemy().get().setResolved(true);
        Command.CommandType goCommand = Command.CommandType.GO;
        Command command = new Command(goCommand, "West");
        CommandResult commandResult = fakeGameEnginePlaying.handleGo(command);
        assertAll("Escaping the enemy",
                () -> assertTrue(commandResult.isChangeImage()),
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertEquals("airlock", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertEquals("You head to the Airlock", commandResult.getDisplayMessage().getFirst()),
                () -> assertEquals("Airlock", fakePlayer.getCurrentRoom().getName())
                );
    }

    @Test
    @DisplayName("handleTake method provided with no target object to take in the item room ie cargo bay")
    void testTakeWithoutObject() {
        Command.CommandType takeCommand = Command.CommandType.TAKE;
        Command command = new Command(takeCommand, null);
        CommandResult commandResult = fakeGameEnginePlaying.handleTake(command);
        assertAll("No object specified",
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertEquals("airlock", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertEquals(fakePlayer.getName() + " did not specify an object to take from the room",
                        commandResult.getDisplayMessage().getFirst()),
                () -> assertTrue(fakePlayer.getInventory().isEmpty())
                );
    }

    @Test
    @DisplayName("handleTake method provided with target object while in an item room ie cargo bay")
    void testTakeWithObject() {
        fakePlayer.setCurrentRoom(fakeWorld.get("Cargo Bay"));
        Command.CommandType takeCommand = Command.CommandType.TAKE;
        Command command = new Command(takeCommand, "tool");
        CommandResult commandResult = fakeGameEnginePlaying.handleTake(command);
        assertAll("Object specified",
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertEquals("cargo_bay", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertTrue(fakeWorld.get("Cargo Bay").getItemList().isEmpty()),
                () -> assertEquals(fakePlayer.getName() + " took " + "Tool",
                        commandResult.getDisplayMessage().getFirst()),
                () -> assertFalse(fakePlayer.getInventory().isEmpty())
                );
    }

    @Test
    @DisplayName("handleTake method provided with descriptive target name")
    void testTakeWithAdjectiveObject() {
        fakePlayer.setCurrentRoom(fakeWorld.get("Cargo Bay"));
        Command.CommandType takeCommand = Command.CommandType.TAKE;
        Command command = new Command(takeCommand, "rusty old cranky tool");
        Command command2 = new Command(takeCommand, "       cranky old malevolent spare wrench       ");
        CommandResult commandResult = fakeGameEnginePlaying.handleTake(command);
        CommandResult commandResult2 = fakeGameEnginePlaying.handleTake(command2);
        assertAll("Rusty old cranky tool",
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertEquals("cargo_bay", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertTrue(fakeWorld.get("Cargo Bay").getItemList().isEmpty()),
                () -> assertFalse(fakePlayer.getInventory().isEmpty()),
                () -> assertEquals(fakePlayer.getName() + " took " + "Tool",
                        commandResult.getDisplayMessage().getFirst())
                );

        assertAll("Spare Wrench",
                () -> assertFalse(commandResult2.isChangeImage()),
                () -> assertFalse(commandResult2.isGameOver()),
                () -> assertEquals("cargo_bay", commandResult2.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult2.getDisplayAssetType()),
                () -> assertTrue(fakeWorld.get("Cargo Bay").getItemList().isEmpty()),
                () -> assertFalse(fakePlayer.getInventory().isEmpty()),
                () -> assertEquals(fakePlayer.getName() + " took " + "Spare Wrench",
                        commandResult2.getDisplayMessage().getFirst())
                );
    }

    @Test
    @DisplayName("handleTake method in a room with no items")
    void testTakeInRoomWithNoItems() {
        Command.CommandType takeCommand = Command.CommandType.TAKE;
        Command command = new Command(takeCommand, "rusty old cranky tool");
        CommandResult commandResult = fakeGameEnginePlaying.handleTake(command);
        assertAll("No items whatsoever in the room",
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertEquals("airlock", commandResult.getDisplayAssetPath()),
                () -> assertEquals("Image", commandResult.getDisplayAssetType()),
                () -> assertEquals("There are no rusty old cranky tool in the room",
                        commandResult.getDisplayMessage().getFirst()),
                () -> assertTrue(fakePlayer.getInventory().isEmpty())
        );
    }

    // need to test handleUse, handleLook, handleInventory, handleSave and handleUnknown
    @Test
    @DisplayName("handleUse method with no object specified")
    void testUseNothing() {
        Command.CommandType useCommand = Command.CommandType.USE;
        Command command = new Command(useCommand, null);
        CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
        assertAll("Nothing to use specified",
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertEquals(fakePlayer.getName() +
                        " did not specify an object to use from his inventory",
                        commandResult.getDisplayMessage().getFirst())
                );
    }

    @Test
    @DisplayName("handleUse with an object not present in inventory provided")
    void testUseWithObjectNotInInventory() {
        Command.CommandType useCommand = Command.CommandType.USE;
        Command command = new Command(useCommand, "baja blast");
        CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
        assertAll("Object not in inventory",
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertEquals("baja blast is not in the inventory.",
                        commandResult.getDisplayMessage().getFirst())
                );
    }

    @Test
    @DisplayName("handleUse with target being escape pod while not being in the airlock & no winning items and " +
            "no enemies killed")
    void testUseEscapePod() {
        fakePlayer.setCurrentRoom(fakeWorld.get("Cargo Bay"));
        Command.CommandType useCommand = Command.CommandType.USE;
        Command command = new Command(useCommand, "escape pod");
        CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
        assertAll("trying to escape with no enemies defeated and no winning items",
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertEquals(fakePlayer.getName() + " cannot leave yet.",
                        commandResult.getDisplayMessage().getFirst())
                );
    }
}
