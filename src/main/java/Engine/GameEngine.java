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

    public GameEngine(GameState gameState, Player player) {
        this.gameState = gameState;
        this.player = player;
        this.roomsMap = WorldBuilder.buildWorld();
    }

    public CommandResult processCommand(Command command) {
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
            String lostMessage = "Sorry, you lost! You ruined the fate of Earth. The disease can now spread.";
            String[] splitLostMessage = lostMessage.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(splitLostMessage), true, true,
                    "/images/lost.png", "image");
        } else if (gameState == GameState.PLAYING) {
            CommandType verbCommand = command.getVerb();
            switch (verbCommand) {
                case CommandType.GO:
                    break;
                case CommandType.TAKE:
                    break;
                case CommandType.LOOK:
                    break;
                case CommandType.USE:
                    break;
                case CommandType.INVENTORY:
                    break;
                case CommandType.SAVE:
                    break;
                case CommandType.UNKNOWN:
                    break;
            }
        }
    }
}
