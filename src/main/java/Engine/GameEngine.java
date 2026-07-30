package Engine;

import Model.Command;
import Model.Command.CommandType;
import Model.GameState;
import Model.Player;
import Model.Room;
import dto.CommandResult;
import utilities.StoriesBuilder;

import java.util.*;

public class GameEngine {

    private GameState gameState;
    private Player player;
    private final Map<String, Room> roomsMap;
    private final CommandParser commandParser;

    public GameEngine(GameState gameState, String playerName) {
        this.commandParser = new CommandParser();
        this.gameState = gameState;
        this.roomsMap = WorldBuilder.buildWorld();
        this.player = new Player(roomsMap.get("Airlock"), 100, playerName);
    }

    public CommandResult processCommand(String commandInput) {
        Command command = commandParser.parseCommand(commandInput);
        if (gameState == GameState.INTRO) {
            Map<String, String> introMap = StoriesBuilder.getIntro("storylines.txt");
            List<String> introLines = new ArrayList<>();
            for (String line : introMap.values()) {
                String[] splitLine = line.split(".");
                Collections.addAll(introLines, splitLine);
            }
            return new CommandResult(introLines, true, false,
                    "/images/airlock.png", "image");
        } else if (gameState == GameState.WON) {
            String winnerMessage = "Congratulations, you won! Now you can finally go back home.";
            String[] splitWinMessage = winnerMessage.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(splitWinMessage), true, true,
                    "/images/won.png", "image");
        } else if (gameState == GameState.LOST) {
            String lostMessage = "Sorry, you lost! You ruined the your remaining crew waiting in the escape pod. " +
                    "They will perish.";
            String[] splitLostMessage = lostMessage.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(splitLostMessage), true, true,
                    "/images/lost.png", "image");
        } else if (gameState == GameState.PLAYING) {
            CommandType verbCommand = command.getVerb();
            switch (verbCommand) {
                case CommandType.GO:
                    // private helper function for the go verb
                    break;
                case CommandType.TAKE:
                    // private helper function for the take verb
                    break;
                case CommandType.LOOK:
                    // private helper function for the look verb
                    break;
                case CommandType.USE:
                    // private helper function for the use verb
                    break;
                case CommandType.INVENTORY:
                    // private helper function for the inventory verb
                    break;
                case CommandType.SAVE:
                    // private helper function for the save verb
                    break;
                case CommandType.UNKNOWN:
                    // private helper function for the unknown verb
                    break;
            }
        }
    }

    private CommandResult handleGo(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        Optional<String> optionalTarget = command.getTarget();
        if (optionalTarget.isEmpty()) {
            String displayMessage = player.getName() + " did not specify a direction (North, South, East, West). " +
                    "Roaming around the room.";
            String[] splitDisplayMessage = displayMessage.split("\\p{Punct}");
            String currentRoomName = playerCurrentRoom.getName();
            String[] roomNameSplit = currentRoomName.split("\\s+");
            String roomImageAssetName;
            if (roomNameSplit.length >= 2) {
                Arrays.setAll(roomNameSplit, i -> roomNameSplit[i].toLowerCase());
                roomImageAssetName = String.join("_", roomNameSplit);
            } else {
                roomImageAssetName = currentRoomName.toLowerCase();
            }
            return new CommandResult(Arrays.asList(splitDisplayMessage), false, false,
                    roomImageAssetName, "Image");
        }
        String direction = optionalTarget.get();
        Room targetRoom = playerCurrentRoom.getExits().get(direction);
        boolean enemyDefeated = playerCurrentRoom.getOptionalEnemy().get().isResolved();
        if (targetRoom == null && !enemyDefeated) {
            String[] failureMessageSplit = playerCurrentRoom.getOptionalEnemy().get().getFailureMessage().split(
                    "\\p{Punct}");
            player.setHealth(player.getHealth()-100);
            gameState = GameState.LOST;
        }

    }
}
