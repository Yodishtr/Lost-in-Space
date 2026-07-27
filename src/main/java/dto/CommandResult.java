package dto;

public class CommandResult {

    private String displayMessage;
    private boolean imageChanged;
    private boolean gameOver;
    private String displayAssetPath;
    private String displayAssetType;

    public CommandResult(String displayMessage, boolean imageChanged, boolean gameOver, String displayAssetPath,
                         String displayAssetType) {
        this.displayMessage = displayMessage;
        this.imageChanged = imageChanged;
        this.gameOver = gameOver;
        this.displayAssetPath = displayAssetPath;
        this.displayAssetType = displayAssetType;
    }

    // Getters
    public String getDisplayMessage() {
        return displayMessage;
    }

    public boolean isImageChanged() {
        return imageChanged;
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
    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public void setImageChanged(boolean imageChanged) {
        this.imageChanged = imageChanged;
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
