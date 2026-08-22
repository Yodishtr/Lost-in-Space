package UI;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StartScreen {

    private final Runnable onStartGame;
    private final Runnable onLoadGame;

    public StartScreen(Runnable onStartGame, Runnable onLoadGame) {
        this.onStartGame = onStartGame;
        this.onLoadGame = onLoadGame;
    }

    public Parent getView() {
        Label titleLabel = new Label("SPACESHIP ADVENTURE");
        titleLabel.getStyleClass().add("title-label");

        // Action Buttons
        Button newGameBtn = new Button("New Game");
        newGameBtn.getStyleClass().add("menu-button");
        newGameBtn.setOnAction(e -> onStartGame.run());

        Button loadGameBtn = new Button("Load Game");
        loadGameBtn.getStyleClass().add("menu-button");
        loadGameBtn.setOnAction(e -> onLoadGame.run());

        // Container Setup
        VBox root = new VBox(20, titleLabel, newGameBtn, loadGameBtn);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("start-screen-container");

        return root;
    }
}
