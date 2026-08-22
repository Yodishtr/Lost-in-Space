package Engine;

import IO.SaveManager;
import Model.*;
import Model.Command.CommandType;
import dto.CommandResult;
import dto.SaveData;
import utilities.StoriesBuilder;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class GameEngine {

    private GameState gameState;
    private Player player;
    private Map<String, Room> roomsMap;
    private CommandParser commandParser;

    public GameEngine(GameState gameState) {
        this.gameState = gameState;
    }

    // this constructor should be used to instantiate the game engine object
    public GameEngine() {
        this.commandParser = new CommandParser();
        this.roomsMap = WorldBuilder.buildWorld();
    }

    public GameEngine(GameState gameState, Player player) {
        this.commandParser = new CommandParser();
        this.gameState = gameState;
        this.roomsMap = WorldBuilder.buildWorld();
        this.player = player;
    }

    public CommandResult startNewGame(String playerName) {
        this.player = new Player(roomsMap.get("Airlock"), 100, playerName, 2, 2);
        if (gameState == GameState.INTRO) {
            Map<String, String> introMap = StoriesBuilder.getIntro("storylines.txt");
            List<String> introLines = new ArrayList<>();
            for (String line : introMap.values()) {
                String[] splitLine = line.split("\\.");
                Collections.addAll(introLines, splitLine);
            }
            gameState = GameState.PLAYING;
            return new CommandResult(introLines, true, false,
                    "airlock", "Image");
        } else {
            return new CommandResult(Arrays.asList(
                    "Game state incorrect. It should be Playing.".split("\\p{Punct}")),
                    false, false, "airlock", "Image");
        }
    }

    public CommandResult loadGame(Path currentPath) {

        Optional<SaveData> potentialSaveData = SaveManager.load(currentPath);
        if (potentialSaveData.isEmpty()) {
            return new CommandResult(Arrays.asList("No save file found. Please start new game."
                    .split("\\p{Punct}")), false, false, "", "");
        } else {
            Player savedPlayer = potentialSaveData.get().getCurrentPlayer();
            GameState savedGameState = potentialSaveData.get().getCurrentGameState();
            Map<String, Map<String, String>> savedWorldInfo = potentialSaveData.get().getCurrentWorld();

            // re-establishing the player info, game state, and world map
            this.player = savedPlayer;
            this.gameState = savedGameState;
            for (Map.Entry<String, Map<String, String>> entry : savedWorldInfo.entrySet()) {
                String roomName = entry.getKey();
                List<Item> currentItem = new ArrayList<>();
                boolean isEnemyPresent = false;
                Map<String, String> savedRoomInfo = entry.getValue();
                for (Map.Entry<String, String> secondEntry : savedRoomInfo.entrySet()) {
                    if (secondEntry.getKey().equals("items")) {
                        String[] splitString = secondEntry.getValue().split(",");
                        for (String s : splitString) {
                            if (!s.isBlank()) {
                                currentItem.add(new Item(s.trim()));
                            }
                        }
                    } else if (secondEntry.getKey().equals("enemy_present")) {
                        isEnemyPresent = Boolean.parseBoolean(secondEntry.getValue());
                    }
                }
                recreateRoom(roomName, currentItem, isEnemyPresent);
            }
            return new CommandResult(Arrays.asList("Saved game successfully loaded.".split("\\p{Punct}")),
                    true, false, getRoomImageAssetName(player.getCurrentRoom().getName()),
                    "image");
        }
    }

    private void recreateRoom(String roomName, List<Item> itemStillPresent, boolean isEnemyPresent) {
        Room realRoom = roomsMap.get(roomName);
        Set<String> stillPresentSet = itemStillPresent.stream().map(s -> s.getName()).collect(Collectors.toSet());
        realRoom.getItemList().removeIf(item -> !stillPresentSet.contains(item.getName()));
        if (realRoom.getEnemyPresent() && !isEnemyPresent) {
            realRoom.setEnemyPresent(false);
            realRoom.setOptionalEnemy(Optional.empty());
        }
    }

    public CommandResult processCommand(String commandInput) {
        Command command = commandParser.parseCommand(commandInput);
        if (gameState == GameState.PLAYING) {
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
            String wronglyCalled = "This is a clear wrong usage of this method.\nBecause the game state is " +
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
        String direction = optionalTarget.get().toUpperCase();
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
            String movingIntoNextRoomMessage = "You head to the " + targetRoomName;
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
                    roomImageAssetName, "Image", false, false, false, false);
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
                        roomImageAssetName, "Image", true, true,
                        true, false);
            }
        }
        String noItemfoundMessage = "There are no " + object + " in the room.";
        String[] commandMessage = noItemfoundMessage.split("\\p{Punct}");
        return new CommandResult(Arrays.asList(commandMessage), false, false,
                roomImageAssetName, "Image", false, false, false, false);
    }

    public CommandResult handleUse(Command command) {
        Room playerCurrentRoom = player.getCurrentRoom();
        Optional<String> optionalTarget = command.getTarget();
        String roomImageAssetName = getRoomImageAssetName(playerCurrentRoom.getName());
        if (optionalTarget.isEmpty()) {
            String noItemsToUse = player.getName() + " did not specify an object to use from his inventory.";
            String[] commandMessage = noItemsToUse.split("\\p{Punct}");
            return new CommandResult(Arrays.asList(commandMessage), false, false,
                    roomImageAssetName, "Image", false, false,
                    false, false);
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
                        roomImageAssetName, "Image", false, false,
                        false, false);
            } else if (!numberWinCondition && playerCurrentRoom.getName().equals("Airlock")) {
                String notEnoughItemMessage = player.getName() + " needs to collect the necessary items then " +
                        "head to the Airlock.";
                String[] commandMessage = notEnoughItemMessage.split("\\p{Punct}");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image", false, false,
                        false, false);
            } else if (numberWinCondition && !playerCurrentRoom.getName().equals("Airlock")) {
                String notInTheRightRoom = player.getName() + " is not in the right room.\nThe escape pod can only be " +
                        "accessed by the Airlock.";
                String[] commandMessage = notInTheRightRoom.split("\\n");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image", false, false,
                        false, false);
            } else {
                return gameOutcome(GameState.WON);
            }
        }

        boolean present = false;
        for (Item item : player.getInventory().values()) {
            if (object.trim().toLowerCase().contains(item.getName().trim().toLowerCase())) {
                present = true;
            }
        }
        if (!present) {
            String notInInventory = object + " is not in the inventory.\n";
            String[] commandMessage = notInInventory.split("\\n");
            return new CommandResult(Arrays.asList(commandMessage), false, false,
                    roomImageAssetName, "Image", false, false,
                    false, false);
        } else {
            if (playerCurrentRoom.getOptionalEnemy().isPresent()) {
                Enemy enemyInTheRoom = playerCurrentRoom.getOptionalEnemy().get();
                String fatalItem = enemyInTheRoom.getRequiredItemName().trim().toLowerCase();
                if (object.contains(fatalItem)) {
                    enemyInTheRoom.setResolved(true);
                    player.addEnemyDefeated();
                    String[] successMessage = enemyInTheRoom.getSuccessMessage().split("\\p{Punct}");
                    return new CommandResult(Arrays.asList(successMessage), false, false,
                            roomImageAssetName, "Image", false, true,
                            false, true);
                } else {
                    String[] failureMessage = enemyInTheRoom.getFailureMessage().split("\\p{Punct}");
                    player.setHealth(player.getHealth() - 50);
                    if (player.getHealth() <= 0) {
                        return gameOutcome(GameState.LOST);
                    }
                    return new CommandResult(Arrays.asList(failureMessage), false, false,
                            roomImageAssetName, "Image", false, false,
                            true, true);
                }
            } else {
                String randomUseMessage = player.getName() + " used " + object + ".Nothing happened.";
                String[] commandMessage = randomUseMessage.split("\\p{Punct}");
                return new CommandResult(Arrays.asList(commandMessage), false, false,
                        roomImageAssetName, "Image", false, false,
                        false, true);

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
        lookAroundMessage.append(currentRoom.getDescription()).append("\n");
        List<Item> items = currentRoom.getItemList();
        lookAroundMessage.append(currentRoom.getName())
                .append(" currently has ")
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


    public Map<String, Map<String, String>> saveWorld(Map<String, Room> currentWorld) {
        Map<String, Map<String, String>> currentWorldInfo = new HashMap<>();
        for (Map.Entry<String, Room> entry : currentWorld.entrySet()) {
            String roomName = entry.getKey();
            Room currentRoom = entry.getValue();
            Map<String, String> currentRoomInfo = new HashMap<>();
            StringBuilder itemList = new StringBuilder();
            for (Item itemInRoom : currentRoom.getItemList()){
                itemList.append(itemInRoom.getName()).append(", ");
            }
            currentRoomInfo.put("items", itemList.toString());
            currentRoomInfo.put("enemy_present", String.valueOf(currentRoom.getOptionalEnemy().isPresent()));
            currentWorldInfo.put(roomName, currentRoomInfo);
        }
        return currentWorldInfo;
    }

    public CommandResult handleSave() {
        SaveData saveData = new SaveData();
        saveData.setCurrentPlayer(player);
        saveData.setCurrentGameState(gameState);
        saveData.setCurrentWorld(saveWorld(roomsMap));
        int saveSuccess = SaveManager.save(saveData);
        if (saveSuccess == 1) {
            return new CommandResult(Arrays.asList("Game Saved SuccessFully!\n".split("\\n")),
                    false, false, getRoomImageAssetName(player.getCurrentRoom().getName()),
                    "image");
        } else {
            return new CommandResult(Arrays.asList("Game could not be saved!\n".split("\\n")),
                    false, false, getRoomImageAssetName(player.getCurrentRoom().getName()),
                    "image");
        }
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

    // tests command parser combo with handleUnknown
    public CommandResult handleUnknown(CommandType command) {
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Map<String, Room> getRoomMap() {
        return roomsMap;
    }

    public void setRoomsMap(Map<String, Room> roomsMap) {
        this.roomsMap = roomsMap;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
