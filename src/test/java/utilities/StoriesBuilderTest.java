package utilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class StoriesBuilderTest {

    @Test
    @DisplayName("getIntro method correctly loads the intros from the storyline test")
    void testAllThreeIntrosReceived() {
        Map<String, String> introLookupTest = StoriesBuilder.getIntro("storylinetest.txt");
        assertAll("All 3 intros are correct",
                () -> assertTrue(introLookupTest.containsKey("INTRO_1")),
                () -> assertTrue(introLookupTest.containsKey("INTRO_2")),
                () -> assertTrue(introLookupTest.containsKey("INTRO_3")),
                () -> assertEquals("Emergency klaxons blare through the cold steel hull, " +
                        "painting the Airlock in pulsing crimson light. " +
                        "Atmospheric pressure is dropping rapidly.", introLookupTest.get("INTRO_1")),
                () -> assertEquals("The automated defense systems have gone haywire, and " +
                        "corrupted crewmates " +
                        "roam the outer bays. Your escape vessel is prepped, " +
                        "but crucial supplies are missing.", introLookupTest.get("INTRO_2")),
                () -> assertEquals("You must venture into the station's four bays, secure the " +
                        "essential survival components, deal with high-threat hazards, " +
                        "and return to the Airlock to escape.", introLookupTest.get("INTRO_3"))
        );
    }

    @Test
    @DisplayName("getIntro method throws the correct Exception")
    void testExceptionThrown() {
        assertThrows(IllegalStateException.class, () ->
                StoriesBuilder.getIntro("swonwlwefnmlkjwnm.txt"));
    }
}
