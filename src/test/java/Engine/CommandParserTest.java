package Engine;

import Model.Command;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {

    private static CommandParser parser = new CommandParser();

    @BeforeAll
    @DisplayName("Instantiating the parser")
    public static void init() {
        parser = new CommandParser();

    }

    @Test
    @DisplayName("Should return a correctly formed command object")
    void testParserSimpleInput() {
        String toParse = "Go North";
        Command command = parser.parseCommand(toParse);
        assertAll("Simple, Expected input parsing",
                () -> assertEquals(command.getVerb(), Command.CommandType.GO),
                () -> assertTrue(command.hasTarget()),
                () -> assertEquals(command.getTarget(), Optional.ofNullable("north")),
                () -> assertEquals(command.getTarget().get(), "north")
        );
    }

    @Test
    @DisplayName("Should return a command object with the fields rightly initialized")
    void testParserInputWithStopWord() {
        String toParse = "Go to North";
        String toParseAlso = "Go in the North";
        String toParseWithWhiteSpace = "    Go in the to the North    ";
        Command command = parser.parseCommand(toParse);
        Command commandAlso = parser.parseCommand(toParseAlso);
        Command commandWithWhiteSpace = parser.parseCommand(toParseWithWhiteSpace);

        assertAll("to Parse variable",
                () -> assertEquals(Command.CommandType.GO, command.getVerb()),
                () -> assertTrue(command.hasTarget()),
                () -> assertEquals(command.getTarget(), Optional.ofNullable("north")),
                () -> assertEquals("north", command.getTarget().get())
                );

        assertAll("toParseAlso variable",
                () -> assertEquals(Command.CommandType.GO, commandAlso.getVerb()),
                () -> assertTrue(commandAlso.hasTarget()),
                () -> assertEquals(commandAlso.getTarget(), Optional.ofNullable("north")),
                () -> assertEquals("north", commandAlso.getTarget().get())
                );

        assertAll("toParse input with white space",
                () -> assertEquals(Command.CommandType.GO, commandWithWhiteSpace.getVerb()),
                () -> assertTrue(commandWithWhiteSpace.hasTarget()),
                () -> assertEquals(commandWithWhiteSpace.getTarget(), Optional.ofNullable("north")),
                () -> assertEquals("north", commandWithWhiteSpace.getTarget().get())
        );
    }

    @Test
    @DisplayName("Input is a single verb command that has no target and parser should return correctly formed command")
    void testSingleVerbCommands() {
        String toParse = "Inventory";
        String toParseAlso = "Look";
        String toParseWithWhiteSpace = "    Inventory";
        String toParseAlsoWithWhiteSpace = "    Look       ";
        Command command = parser.parseCommand(toParse);
        Command commandAlso = parser.parseCommand(toParseAlso);
        Command commandWithWhiteSpace = parser.parseCommand(toParseWithWhiteSpace);
        Command commandWithAlsoWhiteSpace = parser.parseCommand(toParseAlsoWithWhiteSpace);

        assertAll("Simply Inventory",
                () -> assertEquals(Command.CommandType.INVENTORY, command.getVerb()),
                () -> assertFalse(command.hasTarget()),
                () -> assertEquals(Optional.ofNullable(null), command.getTarget()),
                () -> assertThrows(NoSuchElementException.class, () -> command.getTarget().get())
        );

        assertAll("inventory verb with white space",
                () -> assertEquals(Command.CommandType.INVENTORY, commandWithWhiteSpace.getVerb()),
                () -> assertFalse(commandWithWhiteSpace.hasTarget()),
                () -> assertEquals(Optional.ofNullable(null), commandWithWhiteSpace.getTarget()),
                () -> assertThrows(NoSuchElementException.class, () -> commandWithWhiteSpace.getTarget().get())
                );

        assertAll("Simply Look",
                () -> assertEquals(Command.CommandType.LOOK, commandAlso.getVerb()),
                () -> assertFalse(commandAlso.hasTarget()),
                () -> assertEquals(Optional.ofNullable(null), commandAlso.getTarget()),
                () -> assertThrows(NoSuchElementException.class, () -> commandAlso.getTarget().get())
        );

        assertAll("Look verb with white space",
                () -> assertEquals(Command.CommandType.LOOK, commandWithAlsoWhiteSpace.getVerb()),
                () -> assertFalse(commandWithAlsoWhiteSpace.hasTarget()),
                () -> assertEquals(Optional.ofNullable(null), commandWithAlsoWhiteSpace.getTarget()),
                () -> assertThrows(NoSuchElementException.class, () -> commandWithAlsoWhiteSpace.getTarget().get())
        );
    }

    @Test
    @DisplayName("Return a correctly formatted command object with the target object adjective included")
    void testObjectVerbsAndAdjectives() {
        String toParse = "USE the multitool";
        String toParseAlso = "UsE the to the for a to rusty KEY";
        String toParseWithWhiteSpace = "    UsE the to the for a to     rusty     KEY      ";
        Command command = parser.parseCommand(toParse);
        Command commandAlso = parser.parseCommand(toParseAlso);
        Command commandWithWhiteSpace = parser.parseCommand(toParseWithWhiteSpace);

        assertAll("Simple use tool command",
                () -> assertEquals(Command.CommandType.USE, command.getVerb()),
                () -> assertTrue(command.hasTarget()),
                () -> assertEquals(Optional.of("multitool"), command.getTarget()),
                () -> assertEquals("multitool", command.getTarget().get())
        );

        assertAll("to parse also variable into a command",
                () -> assertEquals(Command.CommandType.USE, commandAlso.getVerb()),
                () -> assertTrue(commandAlso.hasTarget()),
                () -> assertEquals(Optional.of("rusty key"), commandAlso.getTarget()),
                () -> assertEquals("rusty key", commandAlso.getTarget().get())
        );

        assertAll("to parse also with white space variable",
                () -> assertEquals(Command.CommandType.USE, commandWithWhiteSpace.getVerb()),
                () -> assertTrue(commandWithWhiteSpace.hasTarget()),
                () -> assertEquals("rusty key", commandWithWhiteSpace.getTarget().get())
                );
    }

    @Test
    @DisplayName("return a correctly formed command object from illicit inputs")
    void testWrongInputs() {
        // a setup to isolate the error being in the parse method and not from the synonym builder object for only stop
        // words test
        // verdict: yes the issue was isolated to be in the synonym builder and that will be tested after the engine
        // layer. reminder to remove the setter for the stopword set in command parser and the next few lines creating
        // the hashset
        List<String> craplist = new ArrayList<>();
        craplist.add("the");
        craplist.add("to");
        craplist.add("in");
        craplist.add("of");
        craplist.add("a");
        craplist.add("for");
        craplist.add("while");
        Set<String> stopword = new HashSet<>(craplist);
        parser.setStopWordSet(stopword);

        String toParse = " the to the for a while to the the";
        String toParseAlso = " dance/wrong verb";
        String randomPunct = "the. for. a! to, the?";
        String emptyParse = "";

        Command command = parser.parseCommand(toParse);
        Command commandAlso = parser.parseCommand(toParseAlso);
        Command commandWithPunct = parser.parseCommand(randomPunct);
        Command emptyCommand = parser.parseCommand(emptyParse);

        assertAll("only stop words",
                () -> assertEquals(Command.CommandType.UNKNOWN, command.getVerb()),
                () -> assertFalse(command.hasTarget()),
                () -> assertThrows(NoSuchElementException.class, () -> command.getTarget().get())
                );

        assertAll("stop words with random punctuation",
                () -> assertEquals(Command.CommandType.UNKNOWN, commandWithPunct.getVerb()),
                () -> assertFalse(commandWithPunct.hasTarget()),
                () -> assertThrows(NoSuchElementException.class, () -> commandWithPunct.getTarget().get())
        );

        assertAll("No verb provided",
                () -> assertEquals(Command.CommandType.UNKNOWN, commandAlso.getVerb()),
                () -> assertFalse(commandAlso.hasTarget()),
                () -> assertThrows(NoSuchElementException.class, () -> commandAlso.getTarget().get())
                );

        assertAll("Empty",
                () -> assertEquals(Command.CommandType.UNKNOWN, emptyCommand.getVerb()),
                () -> assertFalse(emptyCommand.hasTarget()),
                () -> assertThrows(NoSuchElementException.class, () -> emptyCommand.getTarget().get())
        );
    }

    @Test
    @DisplayName("Synonym resolution")
    void testSynonymResolution() {
        String toParse = "walk North!";
        Command command = parser.parseCommand(toParse);
        assertAll("Synonym Resolution",
                () -> assertEquals(Command.CommandType.GO, command.getVerb()),
                () -> assertTrue(command.hasTarget()),
                () -> assertEquals("north", command.getTarget().get())
        );
    }
}
