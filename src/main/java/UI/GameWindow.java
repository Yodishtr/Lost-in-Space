package UI;

import Engine.GameEngine;

import dto.CommandResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class GameWindow {

    private BorderPane root;
    private final GameEngine gameEngine;
    private final Runnable onReturnToMenu;
    private TextArea consoleOutput;
    private TextField inputField;
    private ImageView roomImageView;
    private List<String> currentLines = new ArrayList<>();
    private int currentLineIndex = 0;
    private String initialImagePath;

    public GameWindow(GameEngine gameEngine, Runnable onReturnToMenu, CommandResult introInitialResult) {
        this.gameEngine = gameEngine;
        this.onReturnToMenu = onReturnToMenu;

        if (introInitialResult != null) {
            this.currentLines = introInitialResult.getDisplayMessage();
            this.initialImagePath = introInitialResult.getDisplayAssetPath();
        }
    }

    public Parent getView() {
        this.root = new BorderPane();
        root.setPadding(new Insets(15));

        roomImageView = new ImageView();
        roomImageView.setFitWidth(400);
        roomImageView.setPreserveRatio(true);
        VBox imageContainer = new VBox(roomImageView);
        imageContainer.setAlignment(Pos.CENTER);
        root.setTop(imageContainer);

        consoleOutput = new TextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setWrapText(true);
        consoleOutput.getStyleClass().add("console-output");
        root.setCenter(consoleOutput);

        inputField = new TextField();
        inputField.setPromptText("Type a command (e.g., 'go north', 'look')...");

        Button sendBtn = new Button("Execute / Next");

        inputField.setOnAction(e -> handleInput());
        sendBtn.setOnAction(e -> handleInput());

        HBox inputRow = new HBox(10, inputField, sendBtn);
        HBox.setHgrow(inputField, javafx.scene.layout.Priority.ALWAYS);
        inputRow.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(inputRow);

        if (!currentLines.isEmpty()) {
            advanceLineDisplay();
        }
        if (this.initialImagePath != null) {
            updateRoomImage(this.initialImagePath);
        }
//        if (gameEngine.getPlayer() != null && gameEngine.getPlayer().getCurrentRoom() != null) {
//            String initialRoomName = gameEngine.getPlayer().getCurrentRoom().getName();
//            updateRoomImage(initialRoomName.toLowerCase().replace(" ", "_"));
//        }

        return root;
    }

    private void handleInput() {
        if (currentLineIndex < currentLines.size()) {
            advanceLineDisplay();
            return;
        }

        String commandText = inputField.getText().trim();
        inputField.clear();

        if (!commandText.isEmpty()) {
            CommandResult commandResult = gameEngine.processCommand(commandText);
            handleCommandResult(commandResult);
        }
    }


    private void handleCommandResult(CommandResult commandResult) {
        this.currentLines = commandResult.getDisplayMessage();
        this.currentLineIndex = 0;

        if (commandResult.isChangeImage() && commandResult.getDisplayAssetPath() != null) {
            updateRoomImage(commandResult.getDisplayAssetPath());
        }

        advanceLineDisplay();

        if (commandResult.isGameOver()){
            inputField.setDisable(true);
            advanceLineDisplay();
            showReturnToMenuButton();
        }
    }

    private void showReturnToMenuButton() {
        Button menuBtn = new Button("Return to Main Menu");
        menuBtn.getStyleClass().add("menu-button");
        menuBtn.setMaxWidth(Double.MAX_VALUE); // Stretch full width
        menuBtn.setOnAction(e -> onReturnToMenu.run());

        HBox gameOverRow = new HBox(menuBtn);
        gameOverRow.setAlignment(Pos.CENTER);
        gameOverRow.setPadding(new Insets(10, 0, 0, 0));

        // Smoothly swap bottom layout component
        root.setBottom(gameOverRow);
    }

    private void advanceLineDisplay() {
        if (currentLineIndex < currentLines.size()) {
            consoleOutput.appendText(currentLines.get(currentLineIndex) + "\n");
            currentLineIndex++;
            consoleOutput.setScrollTop(Double.MAX_VALUE);
        }
    }

    private void updateRoomImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return;

        String formattedPath = imagePath;
        if (!formattedPath.startsWith("/")) {
            formattedPath = "/images/" + formattedPath;
        }
        if (!formattedPath.endsWith(".png")) {
            formattedPath = formattedPath + ".png";
        }
        try {
            Image img = new Image(getClass().getResourceAsStream(formattedPath));
            roomImageView.setImage(img);
        } catch (Exception e) {
            // Fallback or logger statement if asset is missing
            System.err.println("Could not load image: " + imagePath);
        }
    }
}
