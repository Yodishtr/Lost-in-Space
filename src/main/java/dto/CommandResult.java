package dto;

import java.util.List;

public class CommandResult {

    private List<String> displayMessage;
    private boolean changeImage;
    private boolean gameOver;
    private String displayAssetPath;
    private String displayAssetType;

    public CommandResult(List<String> displayMessage, boolean changeImage, boolean gameOver, String displayAssetPath,
                         String displayAssetType) {
        this.displayMessage = displayMessage;
        this.changeImage = changeImage;
        this.gameOver = gameOver;
        this.displayAssetPath = displayAssetPath;
        this.displayAssetType = displayAssetType;
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
}
