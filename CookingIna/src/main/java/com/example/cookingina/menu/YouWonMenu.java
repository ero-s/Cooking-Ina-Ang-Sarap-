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

        // Only show next level button if not at max level
        int maxLevel =10;

        Button playAgain = new Button("Play Again");
        playAgain.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20;");
        playAgain.setOnAction(e -> {
            FXGL.getGameController().startNewGame();
            ((CookingInaMain) FXGL.getApp()).initUI();
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().resumeEngine();
        });

        Button nextLevel = new Button("Next Level");
        nextLevel.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20;");
        nextLevel.setOnAction(e -> {

            // Update both player level and unlock next level
            DatabaseManager.updatePlayerLevel(username, currentLevel);
            DatabaseManager.unlockLevel(username, currentLevel);

            // Refresh level menu
            FXGL.getSceneService().popSubScene();
            FXGL.getSceneService().pushSubScene(new LevelMenu(username));
        });

        if (currentLevel >= maxLevel) {
            nextLevel.setVisible(false);
        }


        VBox box = new VBox(20, victoryText, nextLevel, playAgain);
        centerElements(box);

        getContentRoot().getChildren().addAll(bg, box);
    }

    private void centerElements(VBox box) {
        box.setTranslateX(getAppWidth() / 2.0 - 100);
        box.setTranslateY(getAppHeight() / 2.0 - 100);
    }
}