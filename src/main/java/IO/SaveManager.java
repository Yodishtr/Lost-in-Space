package IO;

import Model.GameState;
import Model.Item;
import Model.Player;
import Model.Room;
import dto.SaveData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SaveManager {

    private final static Integer ROOM_NUMBER = 5;

    public static Integer save(SaveData saveData) {
        Map<String, String> playerSaveInfo = savePlayer(saveData.getCurrentPlayer());
        String gameStateSaveInfo = saveGameState(saveData.getCurrentGameState());
        Map<String, Map<String, String>> worldSaveInfo = saveData.getCurrentWorld();

        // build the path for the save file and the directory to house it
        Path currentPath = resolveFilePath();
        String sectionSeparator = "*******************************************************";
        try {
            Files.createDirectories(currentPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(currentPath, StandardCharsets.UTF_8)) {
                // player save
                for (String key : playerSaveInfo.keySet()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(key).append("=").append(playerSaveInfo.get(key)).append(System.lineSeparator());
                    writer.write(sb.toString());
                }
                // section separator
                writer.write(sectionSeparator);
                writer.newLine();

                // current game state
                StringBuilder gameStateSaveInfoBuilder = new StringBuilder();
                gameStateSaveInfoBuilder.append("game_state=").append(gameStateSaveInfo).append(System.lineSeparator());
                writer.write(gameStateSaveInfoBuilder.toString());

                // Section separator
                writer.write(sectionSeparator);
                writer.newLine();

                // world save info
                for (String roomName : worldSaveInfo.keySet()) {
                    // need to make the necessary changes for this
                    StringBuilder roomNameBuilder = new StringBuilder();
                    roomNameBuilder.append("room_name").append(roomName).append(System.lineSeparator());
                    writer.write(roomNameBuilder.toString());
                    for (String roomInfo : worldSaveInfo.get(roomName).keySet()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(roomInfo).append("=").append(worldSaveInfo.get(roomName).get(roomInfo))
                                .append(System.lineSeparator());
                        writer.write(sb.toString());
                    }
                    writer.write(sectionSeparator);
                    writer.newLine();
                }
                return 1;
            }
        } catch (IOException io) {
            io.printStackTrace();
            return 0;
        }
    }

    public static Optional<SaveData> load () {
        Path currentPath = resolveFilePath();
        if (!Files.exists(currentPath)) {
            return Optional.empty();
        } else {
            try (BufferedReader reader = Files.newBufferedReader(currentPath, StandardCharsets.UTF_8)) {
                Player savedPlayer = new Player();
                String line;
                while ((line = reader.readLine()) != null && !line.startsWith("*")) {
                    String[] args = line.split("=", 2);
                    if (args[0].equals("name")) {
                        savedPlayer.setName(args[1]);
                    }
                    else if (args[0].equals("health")) {
                        savedPlayer.setHealth(Integer.parseInt(args[1]));
                    }
                    else if (args[0].equals("current_room")) {
                        savedPlayer.setCurrentRoom(new Room(args[1]));
                    }
                    else if (args[0].equals("inventory")) {
                        String[] items = args[1].split(",");
                        for (String item : items) {
                            savedPlayer.addItemToInventory(new Item(item.trim()));
                        }
                    }
                    else if (args[0].equals("items_to_win")) {
                        savedPlayer.setNumberOfItemsRequiredToWin(Integer.parseInt(args[1]));
                    }
                    else if (args[0].equals("enemies_defeated")) {
                        savedPlayer.setEnemiesToKillToWin(Integer.parseInt(args[1]));
                    }
                }
                reader.readLine();
                line = reader.readLine();
                String[] gameStateSaveInfo = line.split("=", 2);
                GameState currentGameState = GameState.valueOf(gameStateSaveInfo[0]);
                reader.readLine();

                Map<String, Map<String, String>> savedGameWorldInfo = new HashMap<>();
                for (int i = 0; i < ROOM_NUMBER; i++) {
                    Map<String, String> roomInfo = new HashMap<>();
                    String roomName = "";
                    while (line != null && !line.startsWith("*")) {
                        String[] args = line.split("=", 2);
                        if (args[0].equals("room_name")) {
                            roomName = args[1].trim();
                        } else if (args[0].equals("items")) {
                            roomInfo.put("items", args[1]);
                        } else if (args[0].equals("enemy_present")) {
                            roomInfo.put("enemy_present", args[1]);
                        }
                    }
                    savedGameWorldInfo.put(roomName, roomInfo);
                    reader.readLine();
                }
                SaveData saveData = new SaveData();
                saveData.setCurrentPlayer(savedPlayer);
                saveData.setCurrentGameState(currentGameState);
                saveData.setCurrentWorld(savedGameWorldInfo);
                return Optional.ofNullable(saveData);

            } catch (IOException io) {
                io.printStackTrace();
                return Optional.empty();
            }
        }
    }

    private static Path resolveFilePath() {
        String currentHomeDir = System.getProperty("user.home");
        String currentOS = System.getProperty("os.name");
        Path path;
        if (currentOS.toLowerCase().contains("mac")) {
            path = Path.of(currentHomeDir, "Library", "Application Support", "Lost-In-Space", "save.txt");
            return path;
        } else if (currentOS.toLowerCase().contains("win")) {
            path = Path.of(currentHomeDir, "AppData", "Roaming", "Lost-In-Space", "save.txt");
            return path;
        } else {
            path = Path.of(currentHomeDir, ".config", "Lost-In-Space", "save.txt");
            return path;
        }
    }

    private static Map<String, String> savePlayer(Player player) {
        Map<String, String> playerInfo = new HashMap<>();
        playerInfo.put("name", player.getName());
        playerInfo.put("health", String.valueOf(player.getHealth()));
        playerInfo.put("current_room", player.getCurrentRoom().getName());

        // inventory list
        StringBuilder sb = new StringBuilder();
        for (String itemName : player.getInventory().keySet()) {
            sb.append(itemName).append(", ");
        }
        playerInfo.put("inventory", sb.toString());
        playerInfo.put("items_to_win", String.valueOf(player.getNumberOfItemsRequiredToWin()));
        playerInfo.put("enemies_defeated", String.valueOf(player.getEnemiesDefeated()));
        return playerInfo;
    }

    private static String saveGameState(GameState gameState) {
        return gameState.getState();
    }


    // this method needs to be added to gameEngine instead.

}
