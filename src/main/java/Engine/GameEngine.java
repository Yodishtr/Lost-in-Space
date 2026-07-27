package Engine;

import Model.Command;
import Model.GameState;
import Model.Player;
import Model.Room;
import dto.CommandResult;

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
            // should have an application resource where you read from it to
            // introduct the story
        }
    }
}
