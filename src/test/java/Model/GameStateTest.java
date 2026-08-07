package Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStateTest {

    @Test
    @DisplayName("Enum Constructor intializes attribute correctly")
    void testGetStateFromString(){
        assertAll("Constructor Ascertainment",
                () -> assertEquals(GameState.INTRO, GameState.getStateFromString("Intro")),
                () -> assertEquals(GameState.INTRO, GameState.getStateFromString("INTRO")),
                () -> assertEquals(GameState.INTRO, GameState.getStateFromString("intro")),
                () -> assertEquals(GameState.INTRO, GameState.getStateFromString("    intro    ")),
                () -> assertEquals(GameState.UNKNOWN, GameState.getStateFromString(null)),
                () -> assertEquals(GameState.UNKNOWN, GameState.getStateFromString(""))
        );
    }

    @Test
    @DisplayName("Enum attribute fields should return the correct value")
    void testConstructorAndGetter() {
        assertAll("Enum attribute fields",
                () -> assertEquals("intro", GameState.INTRO.getState()),
                () -> assertEquals("playing", GameState.PLAYING.getState()),
                () -> assertEquals("won", GameState.WON.getState()),
                () -> assertEquals("lost", GameState.LOST.getState()),
                () -> assertEquals("unknown", GameState.UNKNOWN.getState())
                );
    }
}
