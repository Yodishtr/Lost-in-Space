package Engine;

import Model.*;
import Model.Command.CommandType;
import dto.CommandResult;
import utilities.StoriesBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class GameEngine {

    private GameState gameState;
    private final Player player;
    private final Map<String, Room> roomsMap;
    private final CommandParser commandParser;

    public GameEngine(GameState gameState, String playerName) {
        this.commandParser = new CommandParser();
        this.gameState = gameState;
        this.roomsMap = WorldBuilder.buildWorld();
        this.player = new Player(roomsMap.get("Airlock"), 100, playerName, 2,
                2);
    }

    public GameEngine(GameState gameState, Player player) {
        this.commandParser = new CommandParser();
        this.gameState = gameState;
        this.roomsMap = WorldBuilder.buildWorld();
        this.player = player;
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
            gameState = GameState.PLAYING;
            return new CommandResult(introLines, true, false,
                    "/images/airlock.png", "image");
        } else if (gameState == GameState.PLAYING) {
            CommandType verbCommand = command.getVerb();
            switch (verbCommand) {
                case CommandType.GO:
                    return handleGo(command);
                case CommandType.TAKE:
                    return handleTake(command);
                case CommandType.LOOK:
                    return handleLook(command);
                case CommandType.USE:
                    return handleUse(command);
                case CommandType.INVENTORY:
                    return handleInventory(command);
                case CommandType.SAVE:
                    // helper needs to be implemented once save manager is done
                    return handleSave();
                case CommandType.UNKNOWN:
                    return handleUnknown();
            }
        }
        return gameOutcome(gameState);
    }

    public CommandResult gameOutcome(GameState currentGameState) {
        if (currentGameState == GameState.WON){
            gameState = GameState.WON;
            String winnerMessage = "Congratulations, you won!\nNow you can finally go back home.";
            String[] splitWinMessage = winnerMessage.split("\\n");
            return new CommandResult(Arrays.asList(splitWinMessage), true, true,
                    "/images/won.png", "Image");
        } else if (currentGameState == GameState.LOST){
            gameState = GameState.LOST;
            String lostMessage = "You Died!\nYour remaining crew was waiting in the escape pod.\n" +
                    "They will perish.";
            String[] splitLostMessage = lostMessage.split("\\n");
            return new CommandResult(Arrays.asList(splitLostMessage), true, true,
                    "/images/lost.png", "Image");
        } else {
            String wronglyCalled = "This is a clear wrong use of this method.\nBecause the game state is " +
                    gameState.toString();
            String[] splitWronglyCalledMessage = wronglyCalled.split("\\n");
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

    public CommandResult handleGo(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        Optional<String> optionalTarget = command.getTarget();
        String currentRoomName = playerCurrentRoom.getName();
        String roomImageAssetName = getRoomImageAssetName(currentRoomName);
        if (optionalTarget.isEmpty()) {
            String displayMessage = player.getName() + " did not specify a direction (North, South, East, West).\n" +
                    "Roaming around the room.";
            String[] splitDisplayMessage = displayMessage.split("\\n");

            return new CommandResult(Arrays.asList(splitDisplayMessage), false, false,
                    roomImageAssetName, "Image");
        }
        String direction = optionalTarget.get();
        Room targetRoom = playerCurrentRoom.getExits().get(direction);
        boolean enemyDefeated = playerCurrentRoom.getOptionalEnemy().
                map(enemy -> enemy.isResolved())
                .orElse(false);
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
            String[] wallMessage = "You cannot go there.\nThere is a wall.".split("\\n");
            return new CommandResult(Arrays.asList(wallMessage), false, false,
                    roomImageAssetName, "Image");
        } else if (targetRoom != null && enemyDefeated){
            String targetRoomName = targetRoom.getName();
            String targetRoomImageAssetName = getRoomImageAssetName(targetRoomName);
            String movingIntoNextRoomMessage = "You head to the " + targetRoomName;
            String[] commandMessage = movingIntoNextRoomMessage.split("\\n");
            player.setCurrentRoom(targetRoom);
            return new CommandResult(Arrays.asList(commandMessage), true, false,
                    targetRoomImageAssetName, "Image");
        } else {
            String targetRoomName = targetRoom.getName();
            String targetRoomImageAssetName = getRoomImageAssetName(targetRoomName);
            String movingIntoNextRoomMessage = "You head to the cunt " + targetRoomName;
            String[] commandMessage = movingIntoNextRoomMessage.split("\\n");
            player.setCurrentRoom(targetRoom);
            return new CommandResult(Arrays.asList(commandMessage), true, false,
                    targetRoomImageAssetName, "Image");
        }
    }

    public CommandResult handleTake(Command command) {
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
        String object = optionalTarget.get().trim().toLowerCase();
        Iterator<Item> itemIterator = playerCurrentRoom.getItemList().iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (object.contains(item.getName().trim().toLowerCase())) {
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
            boolean numberWinCondition = (count == player.getNumberOfItemsRequiredToWin()) &&
                    (player.getEnemiesDefeated() == player.getEnemiesToKillToWin());
            if (!numberWinCondition && !playerCurrentRoom.getName().equals("Airlock")) {
                String cantLeaveYetMessage = player.getName() + " cannot leave yet.\nYou do not have the required " +
                        "items and have not eliminated the threats yet.";
                String[] commandMessage = cantLeaveYetMessage.split("\\n");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image");
            } else if (!numberWinCondition && playerCurrentRoom.getName().equals("Airlock")) {
                String notEnoughItemMessage = player.getName() + " needs to collect the necessary items then " +
                        "head to the Airlock";
                String[] commandMessage = notEnoughItemMessage.split("\\p{Punct}");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image");
            } else if (numberWinCondition && !playerCurrentRoom.getName().equals("Airlock")) {
                String notInTheRightRoom = player.getName() + " is not in the right room.\nThe escape pod can only be " +
                        "accessed by the Airlock.";
                String[] commandMessage = notInTheRightRoom.split("\\n");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image");
            } else {
                return gameOutcome(GameState.WON);
            }
        }

        boolean present = false;
        for (Item item : player.getInventory().values()) {
            if (object.contains(item.getName().trim().toLowerCase())) {
                present = true;
            }
        }
        if (!present) {
            String notInInventory = object + " is not in the inventory.\n";
            String[] commandMessage = notInInventory.split("\\n");
            return new CommandResult(Arrays.asList(commandMessage), false, false,
                    roomImageAssetName, "Image");
        } else {
            if (playerCurrentRoom.getOptionalEnemy().isPresent()) {
                Enemy enemyInTheRoom = playerCurrentRoom.getOptionalEnemy().get();
                String fatalItem = enemyInTheRoom.getRequiredItemName();
                if (object.contains(fatalItem)) {
                    enemyInTheRoom.setResolved(true);
                    player.addEnemyDefeated();
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
            } else {
                String randomUseMessage = player.getName() + " used " + object + ".Nothing happened.";
                String[] commandMessage = randomUseMessage.split("\\p{Punct}");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image");

            }
        }
    }

    public CommandResult handleLook(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        String roomImageAssetName = getRoomImageAssetName(playerCurrentRoom.getName());
        String lookMessage = buildLookAroundMessage(playerCurrentRoom);
        String[] commandMessage = lookMessage.split("\\n");
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
                .append(" items.\n");

        if (!items.isEmpty()) {
            lookAroundMessage.append("Items are: ");
            String itemNames = items.stream().map(Item::getName).collect(Collectors.joining("\n"));
            lookAroundMessage.append(itemNames).append(".");
        }
        return lookAroundMessage.toString();
    }

    public CommandResult handleInventory(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        String roomAssetImageName = getRoomImageAssetName(playerCurrentRoom.getName());
        String inventoryDescriptionMessage = inventoryMessage(player.getInventory());
        String[] commandMessage = inventoryDescriptionMessage.split("\\n");
        return new CommandResult(Arrays.asList(commandMessage), false, false,
                roomAssetImageName, "Image");
    }

    private String inventoryMessage(Map<String, Item> inventoryMap) {
        StringBuilder inventoryMessage = new StringBuilder();
        inventoryMessage.append(player.getName() + " has ").append(inventoryMap.size()).append(" items.\n");
        if (!inventoryMap.isEmpty()) {
            inventoryMessage.append("Items are: \n");
            String itemNamesAndDescription = player.getInventory().entrySet().stream().map(item -> {
                StringBuilder itemMessage = new StringBuilder();
                String itemName = item.getKey();
                String itemDescription = item.getValue().getDescription();
                itemMessage.append(itemName).append(":").append(itemDescription);
                return itemMessage.toString();
                    }).
                    collect(Collectors.joining("\n"));
            inventoryMessage.append(itemNamesAndDescription).append(".");
        }
        return inventoryMessage.toString();
    }

    public CommandResult handleSave() {
        System.out.println("To Implement: player" + player.getName() + " wants game progress to be saved");
        String saved = "Game not saved because save manager not implemented yet";
        String[] commandMessage = saved.split("\\p{Punct}");
        Room playerCurrentRoom = player.getCurrentRoom();
        String roomImageAssetName = getRoomImageAssetName(playerCurrentRoom.getName());
        return new CommandResult(Arrays.asList(commandMessage), false, false,
                roomImageAssetName, "Image");
    }

    public CommandResult handleUnknown() {
        Room playerCurrentRoom = player.getCurrentRoom();
        String roomImageAssetName = getRoomImageAssetName(playerCurrentRoom.getName());
        String unknownCommand = "I don't understand that command.\n" +
                "Try actions like 'go north', 'take [item]', 'use [item]', 'look', or 'inventory'.";
        String[] commandMessage = unknownCommand.split("\\n");
        return new CommandResult(Arrays.asList(commandMessage), false, false,
                roomImageAssetName, "Image");
    }


    // getters (mostly to test for constructor)
    public CommandParser getCommandParser() {
        return commandParser;
    }

    public boolean hasCommandParser() {
        return commandParser != null;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<String, Room> getRoomMap() {
        return roomsMap;
    }

    public GameState getGameState() {
        return gameState;
    }
}
