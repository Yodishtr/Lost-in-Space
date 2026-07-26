package Model;

import java.util.HashMap;
import java.util.Map;

public enum GameState {
    INTRO("intro"),
    PLAYING("playing"),
    WON("won"),
    LOST("lost"),
    UNKNOWN("unknown");

    private final String state;

    GameState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    private static final Map<String, GameState> lookup = new HashMap<>();

    static {
        for (GameState state : GameState.values()) {
            lookup.put(state.getState(), state);
        }
    }

    public static GameState getStateFromString(String state) {
        if (state == null || state.isEmpty()) {
            return GameState.UNKNOWN;
        }
        String cleanStringState = state.trim().toLowerCase();
        return lookup.getOrDefault(cleanStringState, GameState.UNKNOWN);
    }
}
