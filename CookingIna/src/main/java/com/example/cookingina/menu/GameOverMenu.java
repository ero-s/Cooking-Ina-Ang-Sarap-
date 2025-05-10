package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameOverMenu extends FXGLMenu {

    public GameOverMenu() {
        super(MenuType.GAME_MENU);

        Rectangle bg = new Rectangle(getAppWidth(), getAppHeight(), Color.WHITE);

        Button retry = new Button("Retry");
        retry.setOnAction(e -> {
            FXGL.getGameController().startNewGame();
            ((CookingInaMain) FXGL.getApp()).initUI();
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().resumeEngine();
        });

        Button exitToMain = new Button("Main Menu");
        exitToMain.setOnAction(e -> {
            // Properly exit current game to reset state
            FXGL.getGameController().gotoMainMenu();
        });

        VBox box = new VBox(20, retry, exitToMain);
        centerElements(box);

        getContentRoot().getChildren().addAll(bg, box);
    }

    private void centerElements(VBox box) {
        box.setTranslateX(getAppWidth() / 2.0 - 100);
        box.setTranslateY(getAppHeight() / 2.0 - 50);
    }
}