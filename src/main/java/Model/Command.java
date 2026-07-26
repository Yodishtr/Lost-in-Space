package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Command {

    public enum CommandType {
        GO("go"),
        TAKE("take"),
        LOOK("look"),
        USE("use"),
        INVENTORY("inventory"),
        SAVE("save"),
        UNKNOWN("unknown");

        private final String command;

        CommandType(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }

        private static final Map<String, CommandType> commands = new HashMap<>();
        static {
            for (CommandType commandType : CommandType.values()) {
                commands.put(commandType.getCommand(), commandType);
            }
        }

        public static CommandType getCommandFromString(String command) {
            if (commands.containsKey(command)) {
                return commands.get(command);
            }
            String cleanedInput = command.trim().toLowerCase();
            return commands.getOrDefault(cleanedInput, UNKNOWN);
        }
    }

    private final CommandType verb;
    private final String target;

    public Command(CommandType verb, String target) {
        this.verb = verb;
        this.target = target;
    }

    // Getters
    public CommandType getVerb() {
        return verb;
    }

    public Optional<String> getTarget() {
        return Optional.ofNullable(target);
    }

    public boolean hasTarget() {
        return target != null && !target.isEmpty();
    }
}
