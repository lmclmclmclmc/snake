package com.comp2042;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private ChoiceBox<Level> levelChoice;
    @FXML
    private Label levelDescription;

    private Scene gameScene;

    private final ViewManager viewManager = Main.getViewManager();

    @FXML
    private void initialize() {
        levelChoice.getItems().setAll(Level.values());
        levelChoice.valueProperty().addListener((observable, previous, selected) ->
                levelDescription.setText(selected == null ? "" : selected.description()));
        levelChoice.setValue(Level.CLASSIC);
    }

    public void handleStart() {
        Level level = levelChoice.getValue();
        if (level == null) {
            return;
        }
        viewManager.initGameStage();

        Stage gameStage = viewManager.getGameStage();
        gameStage.setTitle("Snake Game | " + level);
        launchSnake(gameStage, level);
        gameStage.setScene(gameScene);
        gameStage.show();
        viewManager.getPrimaryStage().hide();
    }

    private void launchSnake(Stage gameStage, Level level) {
        Game game = new Game(level);
        Obstacle obstacle = new Obstacle(level);
        Snake snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        Coin coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);

        GameView gameView = new GameView(game);
        GameController controller = new GameController(snek, coin, game, gameView, obstacle, gameStage, () -> {
            viewManager.getPrimaryStage().show();
            gameStage.close();
        });
        gameStage.setOnHidden(event -> controller.stop());

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
