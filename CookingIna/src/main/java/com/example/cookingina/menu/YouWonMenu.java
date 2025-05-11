package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.database.DatabaseManager;
import com.example.cookingina.model.LevelData;
import com.example.cookingina.session.Session;
import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class YouWonMenu extends FXGLMenu {

    private final String username;
    private final int currentLevel;

    public YouWonMenu(String username, int currentLevel) {
        super(MenuType.GAME_MENU);
        this.username = username;
        this.currentLevel = currentLevel;

        initBackground();

        // Only show next level button if not at max level
        int maxLevel =10;

        Button playAgain = new Button();
        playAgain.setStyle("-fx-background-image: url('assets/textures/playAgain.png');-fx-background-size: cover;-fx-background-color: transparent;-fx-background-position: center center;");
        playAgain.setPrefSize(430, 130);
        playAgain.setOnAction(e -> {

            FXGL.getGameController().startNewGame();
            ((CookingInaMain) FXGL.getApp()).initUI();
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().resumeEngine();
        });

        Button nextLevel = new Button();
        nextLevel.setStyle("-fx-background-image: url('assets/textures/nextLevel.png');-fx-background-size: cover;-fx-background-color: transparent;-fx-background-position: center center;");
        nextLevel.setPrefSize(430, 130);
        nextLevel.setOnAction(e -> {

            // Update both player level and unlock next level
            if(currentLevel == DatabaseManager.getPlayerLevel(username)) {

                DatabaseManager.updatePlayerLevel(username, currentLevel);
                DatabaseManager.unlockLevel(username, currentLevel);
            }

            // Refresh level menu
            FXGL.getSceneService().popSubScene();
            FXGL.getSceneService().pushSubScene(new LevelMenu(username));
        });

        if (currentLevel >= maxLevel) {
            nextLevel.setVisible(false);
        }


        VBox box = new VBox(10, nextLevel, playAgain);
        centerElements(box);

        FadeTransition fade = new FadeTransition(Duration.seconds((double)1.5F), box);
        fade.setFromValue((double)0.0F);
        fade.setToValue((double)1.0F);
        fade.play();

        getContentRoot().getChildren().addAll(box);
    }

    private void centerElements(VBox box) {
        box.setTranslateX(getAppWidth() / 2.0 - 200);
        box.setTranslateY(getAppHeight() / 2.0 + 150);
    }

    protected void initBackground() {
        Texture background = FXGL.texture("bg_youWon.png");
        background.setFitHeight((double)this.getAppHeight());
        background.setFitWidth((double)this.getAppWidth());
        this.getContentRoot().getChildren().add(0, background);
    }


}