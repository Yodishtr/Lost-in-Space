package UI;

import Engine.GameEngine;
import Model.GameState;
import dto.CommandResult;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

public class SpaceshipApp extends Application {

    private Stage primaryStage;
    private GameEngine engine;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.engine = new GameEngine();
        this.engine.setGameState(GameState.INTRO);

        showStartScreen();

        this.primaryStage.show();
    }

    private void showStartScreen() {
        StartScreen startScreen = new StartScreen(
                this::handleNewGame,
                this::handleLoadGame
        );
        Scene scene = new Scene(startScreen.getView(), 800, 600);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    private void handleNewGame() {
        CommandResult introResult = engine.startNewGame("Astronaut");
        showGameWindow(introResult);
    }

    private void handleLoadGame() {
        Path savePath = Path.of("save.ser");
        CommandResult loadGameResult = engine.loadGame(savePath);
        showGameWindow(loadGameResult);
    }

    private void showGameWindow(CommandResult introResult) {
        GameWindow gameWindow = new GameWindow(
                engine,
                this::showStartScreen,
                introResult
        );
        Scene scene = new Scene(gameWindow.getView(), 900, 700);
        applyStylesheet(scene);

        primaryStage.setScene(scene);
    }

    private void applyStylesheet(Scene scene) {
        try {
            String cssPath = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Stylesheet could not be loaded: /style.css");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
