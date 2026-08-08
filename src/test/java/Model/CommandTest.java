package Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {

    @Test
    @DisplayName("Constructor initializes the fields correctly")
    void testConstructorCommand() {
        Command newCommand = new Command(Command.CommandType.GO, "north");
        assertAll("Attribute fields initialization",
                () -> assertEquals(Command.CommandType.GO, newCommand.getVerb()),
                () -> assertTrue(newCommand.hasTarget()),
                () -> assertEquals(Optional.ofNullable("north"), newCommand.getTarget())
                );
    }

    @Test
    @DisplayName("CommandType enum values initialized correctly")
    void testCommandType() {
        assertAll("Enum initialized correctly",
                () -> assertEquals("go", Command.CommandType.GO.getCommand()),
                () -> assertEquals("take", Command.CommandType.TAKE.getCommand()),
                () -> assertEquals("look", Command.CommandType.LOOK.getCommand()),
                () -> assertEquals("use", Command.CommandType.USE.getCommand()),
                () -> assertEquals("inventory", Command.CommandType.INVENTORY.getCommand()),
                () -> assertEquals("save", Command.CommandType.SAVE.getCommand()),
                () -> assertEquals("unknown", Command.CommandType.UNKNOWN.getCommand())
        );
    }

    @Test
    @DisplayName("CommandType input from strings initialized correctly")
    void testCommandTypeInput() {
        assertAll("Input created correct enums",
                () -> assertEquals(Command.CommandType.GO, Command.CommandType.getCommandFromString("GO")),
                () -> assertEquals(Command.CommandType.GO, Command.CommandType.getCommandFromString("   GO")),
                () -> assertEquals(Command.CommandType.GO, Command.CommandType.getCommandFromString("GO     ")),
                () -> assertEquals(Command.CommandType.GO, Command.CommandType.getCommandFromString("go")),
                () -> assertEquals(Command.CommandType.UNKNOWN, Command.CommandType.getCommandFromString("")),
                () -> assertEquals(Command.CommandType.GO, Command.CommandType.getCommandFromString("  go  ")),
                () -> assertEquals(Command.CommandType.UNKNOWN, Command.CommandType.
                        getCommandFromString("kdsjnksjdnlkn"))
        );
    }
}
