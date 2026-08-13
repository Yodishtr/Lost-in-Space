package utilities;

import Model.Command;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SynonymBuilderTest {

    @Test
    @DisplayName("buildSynonyms correctly returns a hashmap for easy commandType access")
    void testBuildSynonymsCorrectly() {
        Map<String, Command.CommandType> synonyms = SynonymBuilder.buildSynonyms("synonymtest.txt");
        assertAll("the synonyms map has the right synonyms referring to the correct command type",
                () -> assertEquals(Command.CommandType.GO, synonyms.get("sprint")),
                () -> assertEquals(Command.CommandType.TAKE, synonyms.get("claim")),
                () -> assertEquals(Command.CommandType.USE, synonyms.get("u")),
                () -> assertEquals(Command.CommandType.LOOK, synonyms.get("inspect")),
                () -> assertEquals(Command.CommandType.INVENTORY, synonyms.get("inv")),
                () -> assertEquals(Command.CommandType.SAVE, synonyms.get("checkpoint"))
        );
    }

    @Test
    @DisplayName("stopWordsBuilder works correctly")
    void testStopWordsBuilder() {
        Set<String> stopWords = SynonymBuilder.stopWordsBuilder("stopwordtest.txt");
        assertAll("the all stop words are recognised",
                () -> assertTrue(stopWords.contains("the")),
                () -> assertTrue(stopWords.contains("a")),
                () -> assertTrue(stopWords.contains("an")),
                () -> assertTrue(stopWords.contains("some")),
                () -> assertTrue(stopWords.contains("any")),
                () -> assertTrue(stopWords.contains("all")),

                // prepositions
                () -> assertTrue(stopWords.contains("at")),
                () -> assertTrue(stopWords.contains("to")),
                () -> assertTrue(stopWords.contains("towards")),

                // Filler Pronouns & Pointers
                () -> assertTrue(stopWords.contains("this")),
                () -> assertTrue(stopWords.contains("these")),

                // Filler Verbs / Helping Auxiliary Verbs
                () -> assertTrue(stopWords.contains("would")),
                () -> assertTrue(stopWords.contains("could"))
                );
    }
}
