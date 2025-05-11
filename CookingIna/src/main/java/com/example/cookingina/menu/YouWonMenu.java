package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.database.DatabaseManager;
import com.example.cookingina.model.LevelData;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

public class YouWonMenu extends FXGLMenu {

    private final String username;
    private final int currentLevel;

    public YouWonMenu(String username, int currentLevel) {
        super(MenuType.GAME_MENU);
        this.username = username;
        this.currentLevel = currentLevel;

        // Gold background
        Rectangle bg = new Rectangle(getAppWidth(), getAppHeight(), Color.GOLD.deriveColor(1, 1, 1, 1));

        // Victory text
        Text victoryText = new Text("You Won!");
        victoryText.setFont(Font.font("Verdana", 48));
        victoryText.setFill(Color.DARKGREEN);

        // Next Level Button (only show if not max level)
        Button nextLevel = new Button("Next Level");
        nextLevel.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 20;");
        nextLevel.setOnAction(e -> {
            int newLevel = currentLevel + 1;
            DatabaseManager.updatePlayerLevel(username, newLevel);
            FXGL.getSceneService().popSubScene();
            FXGL.getSceneService().pushSubScene(new LevelMenu(username));
        });

        // Only show next level button if not at max level
        int maxLevel = DatabaseManager.getPlayerLevel(username);
        if (currentLevel >= maxLevel) {
            nextLevel.setVisible(false);
        }

        Button playAgain = new Button("Play Again");
        playAgain.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20;");
        playAgain.setOnAction(e -> {
            FXGL.getGameController().startNewGame();
            ((CookingInaMain) FXGL.getApp()).initUI();
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().resumeEngine();
        });

        Button levelMenu = new Button("Next Level");
        levelMenu.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20;");
        levelMenu.setOnAction(e -> {

            int newLevel = currentLevel + 1;
            LevelMenu lv = new LevelMenu(username);

            CookingInaMain game = new CookingInaMain();
            game.setCurrLevel(newLevel);
            lv.levels.get(newLevel).unlocked = true;
            DatabaseManager.updatePlayerLevel(username, newLevel);
            FXGL.getSceneService().pushSubScene(new LevelMenu(username));

        });

        VBox box = new VBox(20, victoryText, nextLevel, playAgain, levelMenu);
        centerElements(box);

        getContentRoot().getChildren().addAll(bg, box);
    }

    private void centerElements(VBox box) {
        box.setTranslateX(getAppWidth() / 2.0 - 100);
        box.setTranslateY(getAppHeight() / 2.0 - 100);
    }
}