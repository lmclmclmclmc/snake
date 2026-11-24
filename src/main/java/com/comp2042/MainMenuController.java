package com.comp2042;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;

public class MainMenuController {

    @FXML
    private ChoiceBox<Level> levelChoice;
    @FXML
    private Label levelDescription;
    @FXML
    private Label levelBestScore;
    @FXML
    private CheckBox adaptiveSpeed;

    private Scene gameScene;

    private final ViewManager viewManager = Main.getViewManager();
    private final HighScores scores = new HighScores(
            Path.of(System.getProperty("user.home"), ".comp2042-snake", "scores.properties"));

    @FXML
    private void initialize() {
        levelChoice.getItems().setAll(Level.values());
        levelChoice.valueProperty().addListener((observable, previous, selected) ->
                updateLevelDetails(selected));
        levelChoice.setValue(Level.CLASSIC);
    }

    private void updateLevelDetails(Level selected) {
        levelDescription.setText(selected == null ? "" : selected.description());
        levelBestScore.setText(selected == null ? "" : "Personal best: " + scores.best(selected));
    }

    public void handleStart() {
        Level level = levelChoice.getValue();
        if (level == null) {
            return;
        }
        viewManager.initGameStage();

        Stage gameStage = viewManager.getGameStage();
        boolean adaptive = adaptiveSpeed.isSelected();
        gameStage.setTitle("Snake Game | " + level + (adaptive ? " | Adaptive speed" : ""));
        launchSnake(gameStage, level, adaptive);
        gameStage.setScene(gameScene);
        gameStage.show();
        viewManager.getPrimaryStage().hide();
    }

    private void launchSnake(Stage gameStage, Level level, boolean adaptive) {
        Game game = new Game(level, adaptive);
        Obstacle obstacle = new Obstacle(level);
        Snake snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        Coin coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);

        GameView gameView = new GameView(game, scores.best(level));
        GameController controller = new GameController(snek, coin, game, gameView, obstacle, gameStage, () -> {
            updateLevelDetails(levelChoice.getValue());
            viewManager.getPrimaryStage().show();
            gameStage.close();
        }, points -> scores.record(level, points));
        gameStage.setOnHidden(event -> controller.finishRun());

        StackPane gamePane = new StackPane();
        gameScene = new Scene(gamePane, game.getWidth() * game.getMultiplier(), game.getHeight() * game.getMultiplier());

        gameScene.setOnKeyPressed(controller::handle);

        gameView.makeScene(gameScene, snek, coin, obstacle);
        gamePane.getChildren().addAll(gameView.gridCanvas, gameView.gameOverLabel, gameView.gameWonLabel, gameView.scoreLabel);
        gamePane.setPadding(new Insets(20));
        gamePane.setAlignment(gameView.gameOverLabel, Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.gameWonLabel, Pos.TOP_CENTER);
        gamePane.setAlignment(gameView.scoreLabel, Pos.BOTTOM_CENTER);
    }

}
