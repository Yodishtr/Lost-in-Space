package Engine;

import IO.SaveManager;
import Model.*;
import dto.CommandResult;
import dto.SaveData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            String[] directions = {"NORTH", "EAST", "SOUTH", "WEST"};
            switch (name) {
                case "Airlock":
                    for (String direction : directions) {
                        if (direction.equals("NORTH")) {
                            exitMap.put("NORTH", null);
                        } else if (direction.equals("EAST")) {
                            exitMap.put("EAST", enemyRoom);
                        } else if (direction.equals("SOUTH")) {
                            exitMap.put("SOUTH", itemRoom);
                        } else if (direction.equals("WEST")) {
                            exitMap.put("WEST", null);
                        }
                    }
                    break;
                case "Cargo Bay":
                    for (String direction : directions) {
                        if (direction.equals("NORTH")) {
                            exitMap.put("NORTH", startingRoom);
                        } else if (direction.equals("EAST")) {
                            exitMap.put("EAST", null);
                        } else if (direction.equals("SOUTH")) {
                            exitMap.put("SOUTH", null);
                        } else if (direction.equals("WEST")) {
                            exitMap.put("WEST", null);
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
                        if (direction.equals("NORTH")) {
                            exitMap.put("NORTH", null);
                        } else if (direction.equals("EAST")) {
                            exitMap.put("EAST", null);
                        } else if (direction.equals("SOUTH")) {
                            exitMap.put("SOUTH", null);
                        } else if (direction.equals("WEST")) {
                            exitMap.put("WEST", startingRoom);
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
        GameEngine testGameEngine = new GameEngine(GameState.INTRO);
        assertAll("fields correctly initialized",
                () -> assertEquals(GameState.INTRO, testGameEngine.getGameState()));
    }

    @Nested
    @DisplayName("handleGo Helper method")
    class HandleGoHelperMethod {
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
    }

   @Nested
   @DisplayName("handleTake helper method")
   class HandleTakeHelperMethod {
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
                   () -> assertTrue(fakeWorld.get("Cargo Bay").getItemList().size() == 1),
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
   }



    // need to test handleUse, handleLook, handleInventory, handleSave and handleUnknown
    @Nested
    @DisplayName("handleUse helper method")
    class HandleUseHelperMethod {
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

        @Test
        @DisplayName("handleUse with target being escape pod while being in airlock but no win condition")
        void testUseEscapePodWithAirlockButNoWinCondition() {
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "escape pod");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("no enemies + no winning items but player in airlock",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals(fakePlayer.getName() + " needs to collect the necessary items then " +
                            "head to the Airlock", commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("handleUse with target being escape pod while being in airlock but no win condition")
        void testAirlockNoEnemiesButWinningItem() {
            // got winning item but no enemies defeated
            fakePlayer.addItemToInventory(new Item("Shiny", true));
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "escape pod");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("no enemies killed but got winning item and in airlock",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals(fakePlayer.getName() + " needs to collect the necessary items then " +
                            "head to the Airlock", commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("handleUse with target being escape pod while being in airlock but no win condition")
        void testAirlockEnemiesKilledButNoWinningItem() {
            // killed enemies but no winning item
            fakePlayer.addEnemyDefeated();
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "escape pod");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("enemies killed but got no winning item and in airlock",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals(fakePlayer.getName() + " needs to collect the necessary items then " +
                            "head to the Airlock", commandResult.getDisplayMessage().getFirst()));
        }

        @Test
        @DisplayName("handleUse with target being escape pod but now have winning condition except not in airlock")
        void testWinningConditionButNoAirlock() {
            fakePlayer.setCurrentRoom(fakeWorld.get("Cargo Bay"));
            fakePlayer.addEnemyDefeated();
            fakePlayer.addItemToInventory(new Item("Shiny", true));
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "escape pod");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("Winning Condition but not in airlock",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals(fakePlayer.getName() + " is not in the right room.",
                            commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("handleUse with target being escape pod and winning condition and airlock all good")
        void testWinningConditionAndAirlockAllGood() {
            fakePlayer.addEnemyDefeated();
            fakePlayer.addItemToInventory(new Item("Shiny", true));
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "escape pod");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("Game Won",
                    () -> assertTrue(commandResult.isGameOver()),
                    () -> assertTrue(commandResult.isChangeImage()),
                    () -> assertEquals("Congratulations, you won!", commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("handleUse with a tool but no enemies nearby to use it on")
        void testToolWithNoEnemiesNearby() {
            fakePlayer.addItemToInventory(new Item("Shiny", true));
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "shiny");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("no enemies nearby to use tool on",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals(fakePlayer.getName() + " used " + "shiny",
                            commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("handleUse with a tool with enemy nearby and correct tool to kill it")
        void testKillEnemyWithRightTool() {
            fakePlayer.addItemToInventory(new Item("Tool", "A tool to kill enemy", true));
            fakePlayer.setCurrentRoom(fakeWorld.get("Armory"));
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "tool");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("Enemy killed",
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertEquals("You not Woo", commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("handleUse with an item that cannot kill nearby enemy")
        void testCannotKillEnemyButDontDieYourself() {
            fakePlayer.addItemToInventory(new Item("Shiny", false));
            fakePlayer.setCurrentRoom(fakeWorld.get("Armory"));
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "shiny");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("enemy beats the breaks off you",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals("You Woo", commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("handleUse with an item that cannot kill nearby enemy who then kills you")
        void testCannotKillEnemyAndEnemyKillsYou() {
            fakePlayer.addItemToInventory(new Item("Shiny", false));
            fakePlayer.setCurrentRoom(fakeWorld.get("Armory"));
            fakePlayer.setHealth(50);
            Command.CommandType useCommand = Command.CommandType.USE;
            Command command = new Command(useCommand, "shiny");
            CommandResult commandResult = fakeGameEnginePlaying.handleUse(command);
            assertAll("got cracked by enemy",
                    () -> assertTrue(commandResult.isChangeImage()),
                    () -> assertTrue(commandResult.isGameOver()),
                    () -> assertEquals("You Died!", commandResult.getDisplayMessage().getFirst())
            );
        }
    }

    @Nested
    @DisplayName("gameOutcome method")
    class GameOutcomeMethod {
        @Test
        @DisplayName("gameOutcome method returns correct value for when it is called with gamestates unknown not lost or " +
                "won")
        void testUnknownGameState() {
            CommandResult outcome = fakeGameEnginePlaying.gameOutcome(GameState.UNKNOWN);
            assertAll("Unknown game state",
                    () -> assertFalse(outcome.isGameOver()),
                    () -> assertFalse(outcome.isChangeImage()),
                    () -> assertEquals("This is a clear wrong usage of this method.",
                            outcome.getDisplayMessage().getFirst()),
                    () -> assertEquals("", outcome.getDisplayAssetPath()),
                    () -> assertEquals("", outcome.getDisplayAssetType())
            );
        }

        @Test
        @DisplayName("gameOutcome method returns correct value when called with game state playing")
        void testGameStatePlaying() {
            CommandResult outcome = fakeGameEnginePlaying.gameOutcome(GameState.PLAYING);
            assertAll("playing game state",
                    () -> assertFalse(outcome.isChangeImage()),
                    () -> assertFalse(outcome.isGameOver()),
                    () -> assertEquals("This is a clear wrong usage of this method.",
                            outcome.getDisplayMessage().getFirst()),
                    () -> assertEquals("", outcome.getDisplayAssetPath()),
                    () -> assertEquals("", outcome.getDisplayAssetType())
            );
        }

        @Test
        @DisplayName("gameOutcome method returns correct value when called with game state intro")
        void testGameStateIntro() {
            CommandResult outcome = fakeGameEnginePlaying.gameOutcome(GameState.INTRO);
            assertAll("intro game state",
                    () -> assertFalse(outcome.isChangeImage()),
                    () -> assertFalse(outcome.isGameOver()),
                    () -> assertEquals("This is a clear wrong usage of this method.",
                            outcome.getDisplayMessage().getFirst()),
                    () -> assertEquals("", outcome.getDisplayAssetPath()),
                    () -> assertEquals("", outcome.getDisplayAssetType())
            );
        }

        @Test
        @DisplayName("gameOutcome method returns correct value when called with game state won")
        void testGameStateWon() {
            CommandResult outcome = fakeGameEnginePlaying.gameOutcome(GameState.WON);
            assertAll("Won game state",
                    () -> assertTrue(outcome.isGameOver()),
                    () -> assertTrue(outcome.isChangeImage()),
                    () -> assertEquals("Congratulations, you won!", outcome.getDisplayMessage().getFirst()),
                    () -> assertEquals("/images/won.png", outcome.getDisplayAssetPath()),
                    () -> assertEquals("Image", outcome.getDisplayAssetType())
            );
        }

        @Test
        @DisplayName("gameOutcome method returns correct value when called with game state lost")
        void testGameStateLost() {
            CommandResult outcome = fakeGameEnginePlaying.gameOutcome(GameState.LOST);
            assertAll("Lost game state",
                    () -> assertTrue(outcome.isGameOver()),
                    () -> assertTrue(outcome.isChangeImage()),
                    () -> assertEquals("You Died!", outcome.getDisplayMessage().getFirst()),
                    () -> assertEquals("/images/lost.png", outcome.getDisplayAssetPath()),
                    () -> assertEquals("Image", outcome.getDisplayAssetType())
            );
        }
    }


    @Test
    @DisplayName("handleUnknown method")
    void testHandleUnknown() {
        CommandParser parser = fakeGameEnginePlaying.getCommandParser();
        Command command = parser.parseCommand("iwjnfkjanf osjnfkgjsnfk");
        Command.CommandType commandType = command.getVerb();
        CommandResult commandResult = fakeGameEnginePlaying.handleUnknown(commandType);
        assertAll("unknown command",
                () -> assertFalse(commandResult.isGameOver()),
                () -> assertFalse(commandResult.isChangeImage()),
                () -> assertEquals("I don't understand that command.",
                        commandResult.getDisplayMessage().getFirst())
                );
    }

    @Nested
    @DisplayName("handleInventory method")
    class HandleInventoryHelperMethod {
        @Test
        @DisplayName("handleInventory with an empty inventory and correct command formation")
        void testEmptyInventory() {
            Command.CommandType commandType = Command.CommandType.INVENTORY;
            Command command = new Command(commandType, null);
            CommandResult commandResult = fakeGameEnginePlaying.handleInventory(command);
            assertAll("empty inventory",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals(fakePlayer.getName() + " has " + fakePlayer.getInventory().size() +
                            " items.", commandResult.getDisplayMessage().getFirst()),
                    () -> assertEquals("airlock", commandResult.getDisplayAssetPath()),
                    () -> assertEquals("Image", commandResult.getDisplayAssetType())
            );
        }

        @Test
        @DisplayName("handleInventory with an item in the inventory and correct command formation")
        void testInventory() {
            fakePlayer.addItemToInventory(new Item("Shiny", false));
            Command.CommandType commandType = Command.CommandType.INVENTORY;
            Command command = new Command(commandType, null);
            CommandResult commandResult = fakeGameEnginePlaying.handleInventory(command);
            assertAll("one inventory",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertTrue(fakePlayer.getInventory().size() == 1),
                    () -> assertEquals(fakePlayer.getName() + " has " + fakePlayer.getInventory().size() +
                            " items.", commandResult.getDisplayMessage().getFirst()),
                    () -> assertEquals("airlock", commandResult.getDisplayAssetPath()),
                    () -> assertEquals("Image", commandResult.getDisplayAssetType())
            );
        }

        @Test
        @DisplayName("handleInventory with an empty inventory and then a non-empty inventory but both have wrongly " +
                "formed commands")
        void testWronglyFormedInventoryCommand() {
            Command.CommandType commandType = Command.CommandType.INVENTORY;
            Command command = new Command(commandType, "my bag");
            CommandResult commandResult = fakeGameEnginePlaying.handleInventory(command);
            assertAll("empty inventory",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals(fakePlayer.getName() + " has " + fakePlayer.getInventory().size() +
                            " items.", commandResult.getDisplayMessage().getFirst()),
                    () -> assertEquals("airlock", commandResult.getDisplayAssetPath()),
                    () -> assertEquals("Image", commandResult.getDisplayAssetType())
            );

            fakePlayer.addItemToInventory(new Item("Shiny", false));
            CommandResult commandResult2 = fakeGameEnginePlaying.handleInventory(command);
            assertAll("empty inventory",
                    () -> assertFalse(commandResult2.isChangeImage()),
                    () -> assertFalse(commandResult2.isGameOver()),
                    () -> assertEquals(fakePlayer.getName() + " has " + fakePlayer.getInventory().size() +
                            " items.", commandResult2.getDisplayMessage().getFirst()),
                    () -> assertEquals("airlock", commandResult2.getDisplayAssetPath()),
                    () -> assertEquals("Image", commandResult2.getDisplayAssetType())
            );
        }
    }


    @Nested
    @DisplayName("handleLook helper method")
    class HandleLookHelperMethod {
        @Test
        @DisplayName("handleLook with an empty room but well formed command")
        void testEmptyRoomCommand() {
            Command.CommandType commandType = Command.CommandType.LOOK;
            Command command = new Command(commandType, null);
            CommandResult commandResult = fakeGameEnginePlaying.handleLook(command);
            assertAll("empty room",
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertTrue(fakePlayer.getCurrentRoom().getName().equals("Airlock")),
                    () -> assertTrue(fakePlayer.getCurrentRoom().getItemList().size() == 0),
                    () -> assertEquals(fakePlayer.getCurrentRoom().getName() + " currently has " +
                                    fakePlayer.getCurrentRoom().getItemList().size() + " items.",
                            commandResult.getDisplayMessage().get(1)),
                    () -> assertEquals("airlock", commandResult.getDisplayAssetPath()),
                    () -> assertEquals("Image", commandResult.getDisplayAssetType())
            );
        }

        @Test
        @DisplayName("handleLook with a room with an item but well formed command")
        void testLookWithANonEmptyRoom() {
            fakePlayer.setCurrentRoom(fakeWorld.get("Cargo Bay"));
            Command.CommandType commandType = Command.CommandType.LOOK;
            Command command = new Command(commandType, null);
            CommandResult commandResult = fakeGameEnginePlaying.handleLook(command);
            assertAll("non-empty room",
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertTrue(fakePlayer.getCurrentRoom().getName().equals("Cargo Bay")),
                    () -> assertTrue(fakePlayer.getCurrentRoom().getItemList().size() == 2),
                    () -> assertEquals(fakePlayer.getCurrentRoom().getName() + " currently has " +
                                    fakePlayer.getCurrentRoom().getItemList().size() + " items.",
                            commandResult.getDisplayMessage().get(1)),
                    () -> assertEquals("cargo_bay", commandResult.getDisplayAssetPath()),
                    () -> assertEquals("Image", commandResult.getDisplayAssetType())
            );
        }

        @Test
        @DisplayName("handleLook with a non empty room but a wrongly formed command")
        void testWronglyFormedRoomCommand() {
            fakePlayer.setCurrentRoom(fakeWorld.get("Cargo Bay"));
            Command.CommandType commandType = Command.CommandType.LOOK;
            Command command = new Command(commandType, "ougabougaksjnfkjsn");
            CommandResult commandResult = fakeGameEnginePlaying.handleLook(command);
            assertAll("non-empty room",
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertTrue(fakePlayer.getCurrentRoom().getName().equals("Cargo Bay")),
                    () -> assertTrue(fakePlayer.getCurrentRoom().getItemList().size() == 2),
                    () -> assertEquals(fakePlayer.getCurrentRoom().getName() + " currently has " +
                                    fakePlayer.getCurrentRoom().getItemList().size() + " items.",
                            commandResult.getDisplayMessage().get(1)),
                    () -> assertEquals("cargo_bay", commandResult.getDisplayAssetPath()),
                    () -> assertEquals("Image", commandResult.getDisplayAssetType())
            );
        }
    }


    @Nested
    @DisplayName("processCommand method")
    class ProcessCommandMethod {
        @Test
        @DisplayName("processCommand method returns correct dto with gamestate currently being playing")
        void testProcessCommandWhilePlaying() {
            CommandResult commandResult = fakeGameEnginePlaying.processCommand("venture South.");
            assertAll("venture south",
                    () -> assertTrue(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals("You head to the " + fakePlayer.getCurrentRoom().getName(),
                            commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("random input")
        void testRandomInput() {
            CommandResult commandResult = fakeGameEnginePlaying.processCommand("nfkljasdn noksdfjn");
            assertAll("random input",
                    () -> assertFalse(commandResult.isChangeImage()),
                    () -> assertFalse(commandResult.isGameOver()),
                    () -> assertEquals("I don't understand that command.",
                            commandResult.getDisplayMessage().getFirst())
            );
        }

        @Test
        @DisplayName("handle inventory method correctly")
        void testInventoryInput() {
            fakeGameEnginePlaying.setRoomsMap(fakeWorld);
            fakeGameEnginePlaying.processCommand("go South");
            fakeGameEnginePlaying.processCommand("take Tool");
            CommandResult commandResult = fakeGameEnginePlaying.processCommand("inventory");
            assertAll("one inventory",
                    () -> assertFalse(commandResult.isChangeImage(), "image change is not false"),
                    () -> assertFalse(commandResult.isGameOver(), "game over is not false"),
                    () -> assertTrue(fakePlayer.getInventory().size() == 1, "inventory is not 1"),
                    () -> assertEquals(fakePlayer.getName() + " has " + fakePlayer.getInventory().size() +
                            " items.", commandResult.getDisplayMessage().getFirst()),
                    () -> assertEquals("cargo_bay", commandResult.getDisplayAssetPath()),
                    () -> assertEquals("Image", commandResult.getDisplayAssetType())
            );
        }
    }


}
