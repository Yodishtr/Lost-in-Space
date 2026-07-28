package Engine;

import Model.Command;
import Model.GameState;
import Model.Player;
import Model.Room;
import dto.CommandResult;
import utilities.StoriesBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        }
    }
}
