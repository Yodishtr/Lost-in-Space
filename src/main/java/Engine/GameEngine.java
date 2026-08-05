package Engine;

import Model.*;
import Model.Command.CommandType;
import dto.CommandResult;
import utilities.StoriesBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class GameEngine {

    private GameState gameState;
    private Player player;
    private final Map<String, Room> roomsMap;
    private final CommandParser commandParser;

    public GameEngine(GameState gameState, String playerName) {
        this.commandParser = new CommandParser();
        this.gameState = gameState;
        this.roomsMap = WorldBuilder.buildWorld();
        this.player = new Player(roomsMap.get("Airlock"), 100, playerName);
    }

    public CommandResult processCommand(String commandInput) {
        Command command = commandParser.parseCommand(commandInput);
        if (gameState == GameState.INTRO) {
            Map<String, String> introMap = StoriesBuilder.getIntro("storylines.txt");
            List<String> introLines = new ArrayList<>();
            for (String line : introMap.values()) {
                String[] splitLine = line.split(".");
                Collections.addAll(introLines, splitLine);
            }
            return new CommandResult(introLines, true, false,
                    "/images/airlock.png", "image");
        } else if (gameState == GameState.PLAYING) {
            CommandType verbCommand = command.getVerb();
            switch (verbCommand) {
                case CommandType.GO:
                    // private helper function for the go verb
                    return handleGo(command);
                case CommandType.TAKE:
                    // private helper function for the take verb
                    return handleTake(command);
                    break;
                case CommandType.LOOK:
                    // private helper function for the look verb
                    return handleLook(command);
                    break;
                case CommandType.USE:
                    // private helper function for the use verb
                    return handleUse(command);
                    break;
                case CommandType.INVENTORY:
                    // private helper function for the inventory verb
                    break;
                case CommandType.SAVE:
                    // private helper function for the save verb
                    break;
                case CommandType.UNKNOWN:
                    // private helper function for the unknown verb
                    break;
            }
        }
    }

    public CommandResult gameOutcome(GameState currentGameState) {
        if (currentGameState == GameState.WON){
            gameState = GameState.WON;
            String winnerMessage = "Congratulations, you won! Now you can finally go back home.";
            String[] splitWinMessage = winnerMessage.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(splitWinMessage), true, true,
                    "/images/won.png", "image");
        } else if (currentGameState == GameState.LOST){
            gameState = GameState.LOST;
            String lostMessage = "You Died! Your remaining crew was waiting in the escape pod. " +
                    "They will perish.";
            String[] splitLostMessage = lostMessage.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(splitLostMessage), true, true,
                    "/images/lost.png", "image");
        } else {
            String wronglyCalled = "This is a clear wrong use of this method. because the game state is " +
                    gameState.toString();
            String[] splitWronglyCalledMessage = wronglyCalled.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(splitWronglyCalledMessage), false, false,
                    "", "");
        }
    }


    // a private helper that returns the required asset image name for display by ui
    private String getRoomImageAssetName(String roomName) {
        String[] roomNameSplit = roomName.split("\\s+");
        String roomImageAssetName;
        if (roomNameSplit.length >= 2) {
            Arrays.setAll(roomNameSplit, i -> roomNameSplit[i].toLowerCase());
            roomImageAssetName = String.join("_", roomNameSplit);
        } else {
            roomImageAssetName = roomName.toLowerCase();
        }
        return roomImageAssetName;
    }

    private CommandResult handleGo(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        Optional<String> optionalTarget = command.getTarget();
        String currentRoomName = playerCurrentRoom.getName();
        String roomImageAssetName = getRoomImageAssetName(currentRoomName);
        if (optionalTarget.isEmpty()) {
            String displayMessage = player.getName() + " did not specify a direction (North, South, East, West). " +
                    "Roaming around the room.";
            String[] splitDisplayMessage = displayMessage.split("\\p{Punct}");

            return new CommandResult(Arrays.asList(splitDisplayMessage), false, false,
                    roomImageAssetName, "Image");
        }
        String direction = optionalTarget.get();
        Room targetRoom = playerCurrentRoom.getExits().get(direction);
        boolean enemyDefeated = playerCurrentRoom.getOptionalEnemy().get().isResolved();
        if (targetRoom == null && !enemyDefeated) {
            String[] failureMessageSplit = playerCurrentRoom.getOptionalEnemy().get().getFailureMessage().split(
                    "\\p{Punct}");
            player.setHealth(player.getHealth()-50);
            if (player.getHealth() <= 0) {
                return gameOutcome(GameState.LOST);
            }
            return new CommandResult(Arrays.asList(failureMessageSplit), false, false,
                    roomImageAssetName, "Image");
        } else if (targetRoom == null && enemyDefeated){
            String[] wallMessage = "You cannot go there. There is a wall.".split("\\p{Punct}");
            return new CommandResult(Arrays.asList(wallMessage), false, false,
                    roomImageAssetName, "Image");
        } else if (targetRoom != null && enemyDefeated){
            String targetRoomName = targetRoom.getName();
            String targetRoomImageAssetName = getRoomImageAssetName(targetRoomName);
            String movingIntoNextRoomMessage = "You head to the " + targetRoomName;
            String[] commandMessage = movingIntoNextRoomMessage.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(commandMessage), true, false,
                    targetRoomImageAssetName, "Image");
        } else {
            String targetRoomName = targetRoom.getName();
            String targetRoomImageAssetName = getRoomImageAssetName(targetRoomName);
            String movingIntoNextRoomMessage = "You head to the " + targetRoomName;
            String[] commandMessage = movingIntoNextRoomMessage.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(commandMessage), true, true,
                    targetRoomImageAssetName, "Image");
        }
    }

    private CommandResult handleTake(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        Optional<String> optionalTarget = command.getTarget();
        String roomImageAssetName = getRoomImageAssetName(playerCurrentRoom.getName());
        if (optionalTarget.isEmpty()) {
            String displayNoTargetSpecifiedMessage = player.getName() + " did not specify an object to take " +
                    "from the room.";
            String[] displayMessageArray = displayNoTargetSpecifiedMessage.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(displayMessageArray), false, false,
                    roomImageAssetName, "Image");
        }
        String object = optionalTarget.get();
        Iterator<Item> itemIterator = playerCurrentRoom.getItemList().iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (object.contains(item.getName())) {
                player.addItemToInventory(item);
                itemIterator.remove();
                String foundItemMessage = player.getName() + " took " + item.getName();
                String[] commandMessage = foundItemMessage.split("\\p{Punct}");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image");
            }
        }
        String noItemfoundMessage = "There are no " + object + " in the room.";
        String[] commandMessage = noItemfoundMessage.split("\\p{Punct}");
        return new CommandResult(Arrays.asList(commandMessage), false, false,
                roomImageAssetName, "Image");
    }

    public CommandResult handleUse(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        Optional<String> optionalTarget = command.getTarget();
        String roomImageAssetName = getRoomImageAssetName(playerCurrentRoom.getName());
        if (optionalTarget.isEmpty()) {
            String noItemsToUse = player.getName() + " did not specify an object to use from his inventory.";
            String[] commandMessage = noItemsToUse.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(commandMessage), false, false,
                    roomImageAssetName, "Image");
        }
        String object = optionalTarget.get();
        if (object.equals("escape pod")){
            int count = 0;
            for (Item item : player.getInventory().values()) {
                if (item.isRequiredToWin()) {
                    count++;
                }
            }
            if (count < 2 && !playerCurrentRoom.getName().equals("Airlock")) {
                String cantLeaveYetMessage = player.getName() + " cannot leave yet. You do not have the required " +
                        "items.";
                String[] commandMessage = cantLeaveYetMessage.split("\\p{Punct}");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image");
            } else if (count < 2 && playerCurrentRoom.getName().equals("Airlock")) {
                String notEnoughItemMessage = player.getName() + " needs to collect the necessary items then " +
                        "head to the Airlock";
                String[] commandMessage = notEnoughItemMessage.split("\\p{Punct}");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image");
            } else if (count == 2 && !playerCurrentRoom.getName().equals("Airlock")) {
                String notInTheRightRoom = player.getName() + " is not in the right room. The escape pod can only be " +
                        "accessed by the Airlock.";
                String[] commandMessage = notInTheRightRoom.split("\\p{Punct}");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image");
            } else {
                return gameOutcome(GameState.WON);
            }
        }

        if (playerCurrentRoom.getOptionalEnemy().isPresent()) {
            Enemy enemyInTheRoom = playerCurrentRoom.getOptionalEnemy().get();
            String fatalItem = enemyInTheRoom.getRequiredItemName();
            if (object.contains(fatalItem)) {
                enemyInTheRoom.setResolved(true);
                String[] successMessage = enemyInTheRoom.getSuccessMessage().split("\\p{Punct}");
                return new CommandResult(Arrays.asList(successMessage), false, false,
                        roomImageAssetName, "Image");
            } else {
                String[] failureMessage = enemyInTheRoom.getFailureMessage().split("\\p{Punct}");
                player.setHealth(player.getHealth() - 50);
                if (player.getHealth() <= 0) {
                    return gameOutcome(GameState.LOST);
                }
                return new CommandResult(Arrays.asList(failureMessage), false, false,
                        roomImageAssetName, "Image");
            }
        }

        String randomUseMessage = player.getName() + " used " + object;
        String[] commandMessage = randomUseMessage.split("\\p{Punct}");
        return new CommandResult(Arrays.asList(commandMessage), false, false,
                roomImageAssetName, "Image");

    }

    private CommandResult handleLook(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        String roomImageAssetName = getRoomImageAssetName(playerCurrentRoom.getName());
        String lookMessage = buildLookAroundMessage(playerCurrentRoom);
        String[] commandMessage = lookMessage.split("\\p{Punct}");
        return new CommandResult(Arrays.asList(commandMessage), false, false,
                roomImageAssetName, "Image");
    }

    private String buildLookAroundMessage(Room currentRoom) {
        StringBuilder lookAroundMessage = new StringBuilder();
        lookAroundMessage.append(currentRoom.getDescription()).append(" ");
        List<Item> items = currentRoom.getItemList();
        lookAroundMessage.append(currentRoom.getName())
                .append(" currently has  ")
                .append(items.size())
                .append(" items.");

        if (!items.isEmpty()) {
            lookAroundMessage.append("Items are ");
            String itemNames = items.stream().map(Item::getName).collect(Collectors.joining(", "));
            lookAroundMessage.append(itemNames).append(".");
        }
        return lookAroundMessage.toString();
    }

    private CommandResult handleInventory(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        String roomAssetImageName = getRoomImageAssetName(playerCurrentRoom.getName());
        String inventoryDescriptionMessage = inventoryMessage(player.getInventory());
    }

    private String inventoryMessage(Map<String, Item> inventoryMap) {
        StringBuilder inventoryMessage = new StringBuilder();
        inventoryMessage.append(player.getName() + " has ").append(inventoryMap.size()).append(" items.");
        if (!inventoryMap.isEmpty()) {
            // use entrySet to stream the map and get both the item name and their description in a single string
        }
    }
}
