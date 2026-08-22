package dto;

import java.util.List;

public class CommandResult {

    private List<String> displayMessage;
    private boolean changeImage;
    private boolean gameOver;
    private String displayAssetPath;
    private String displayAssetType;
    private boolean itemPickedUp;
    private boolean enemyKilled;
    private boolean enemyAttacked;
    private boolean usedItem;

    public CommandResult(List<String> displayMessage, boolean changeImage, boolean gameOver, String displayAssetPath,
                         String displayAssetType) {
        this.displayMessage = displayMessage;
        this.changeImage = changeImage;
        this.gameOver = gameOver;
        this.displayAssetPath = displayAssetPath;
        this.displayAssetType = displayAssetType;
    }

    public CommandResult(List<String> displayMessage, boolean changeImage, boolean gameOver, String displayAssetPath, String displayAssetType,
                         boolean itemPickedUp, boolean enemyKilled, boolean enemyAttacked, boolean usedItem) {
        this.displayMessage = displayMessage;
        this.changeImage = changeImage;
        this.gameOver = gameOver;
        this.displayAssetPath = displayAssetPath;
        this.displayAssetType = displayAssetType;
        this.itemPickedUp = itemPickedUp;
        this.enemyKilled = enemyKilled;
        this.enemyAttacked = enemyAttacked;
        this.usedItem = usedItem;
    }

    // Getters
    public List<String> getDisplayMessage() {
        return displayMessage;
    }

    public boolean isChangeImage() {
        return changeImage;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getDisplayAssetPath() {
        return displayAssetPath;
    }

    public String getDisplayAssetType() {
        return displayAssetType;
    }

    public boolean isItemPickedUp() {
        return itemPickedUp;
    }

    public boolean isEnemyKilled() {
        return enemyKilled;
    }

    public boolean isEnemyAttacked() {
        return enemyAttacked;
    }

    // Setters
    public void setDisplayMessage(List<String> displayMessage) {
        this.displayMessage = displayMessage;
    }

    public void setChangeImage(boolean changeImage) {
        this.changeImage = changeImage;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setDisplayAssetPath(String displayAssetPath) {
        this.displayAssetPath = displayAssetPath;
    }

    public void setDisplayAssetType(String displayAssetType) {
        this.displayAssetType = displayAssetType;
    }

    public void setItemPickedUp(boolean itemPickedUp) {
        this.itemPickedUp = itemPickedUp;
    }

    public void setEnemyKilled(boolean enemyKilled) {
        this.enemyKilled = enemyKilled;
    }

    public void setEnemyAttacked(boolean enemyAttacked) {
        this.enemyAttacked = enemyAttacked;
    }
}
