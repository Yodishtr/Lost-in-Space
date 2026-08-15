package IO;

import Model.GameState;
import Model.Item;
import Model.Player;
import Model.Room;
import dto.SaveData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SaveManager {

    public static Integer save(SaveData saveData) {
        Map<String, String> playerSaveInfo = savePlayer(saveData.getCurrentPlayer());
        String gameStateSaveInfo = saveGameState(saveData.getCurrentGameState());
        Map<String, Map<String, String>> worldSaveInfo = saveWorld(saveData.getCurrentWorld());

        // build the path for the save file and the directory to house it
        Path currentPath = resolveFilePath();
        try {
            Files.createDirectories(currentPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(currentPath, StandardCharsets.UTF_8)) {
                // player save
                for (String key : playerSaveInfo.keySet()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(key).append("=").append(playerSaveInfo.get(key)).append("\n)");
                    writer.write(sb.toString());
                }
                // current game state
                StringBuilder gameStateSaveInfoBuilder = new StringBuilder();
                gameStateSaveInfoBuilder.append("game_state=").append(gameStateSaveInfo).append("\n");
                writer.write(gameStateSaveInfoBuilder.toString());

                // world save info

            }
        } catch (IOException io) {
            io.printStackTrace();
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

    private static Map<String, Map<String, String>> saveWorld(Map<String, Room> currentWorld) {
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
}
