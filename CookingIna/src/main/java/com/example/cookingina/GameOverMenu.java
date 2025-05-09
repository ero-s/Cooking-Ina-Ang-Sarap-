package com.example.cookingina;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameOverMenu extends FXGLMenu {

    public GameOverMenu() {
        super(MenuType.GAME_MENU);

        // Create a white background rectangle
        Rectangle bg = new Rectangle(getAppWidth(), getAppHeight(), Color.WHITE);

        Button retry = new Button("Retry");
        retry.setOnAction(e -> {
            // Exit current game session
            FXGL.getGameController().startNewGame();

            // Force recreation of UI elements
            ((CookingInaMain) FXGL.getApp()).initUI();

            // Remove the game over menu
            FXGL.getSceneService().popSubScene();

            // Resume the game in case it was paused
            FXGL.getGameController().resumeEngine();
        });

        Button exitToMain = new Button("Main Menu");
        exitToMain.setOnAction(e -> fireExitToMainMenu());

        VBox box = new VBox(20, retry, exitToMain);
        centerElements(box);

        // Add background first, then UI elements
        getContentRoot().getChildren().addAll(bg, box);
    }

    private void centerElements(VBox box) {
        box.setTranslateX(getAppWidth() / 2.0 - 100);
        box.setTranslateY(getAppHeight() / 2.0 - 50);
    }
}