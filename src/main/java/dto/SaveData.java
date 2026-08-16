package dto;

import Model.GameState;
import Model.Player;
import Model.Room;

import java.util.Map;

public class SaveData {
    // a dto to record player game data for save files
    private Player currentPlayer;
    private Map<String, Map<String, String>> currentWorld;
    private GameState currentGameState;

    public SaveData(Player currentPlayer, Map<String, Map<String, String>> currentWorld, GameState currentGameState) {
        this.currentPlayer = currentPlayer;
        this.currentWorld = currentWorld;
        this.currentGameState = currentGameState;
    }

    public SaveData() {}

    // Getters
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Map<String, Map<String, String>> getCurrentWorld() {
        return currentWorld;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    // Setters
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setCurrentWorld(Map<String, Map<String, String>> currentWorld) {
        this.currentWorld = currentWorld;
    }

    public void setCurrentGameState(GameState currentGameState) {
        this.currentGameState = currentGameState;
    }
}
