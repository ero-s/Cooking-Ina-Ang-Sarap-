package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getGameController;

public class MainMenu extends FXGLMenu {
    public MainMenu() {
        super(MenuType.MAIN_MENU);

        initBackground();
        initMenuUI();
    }

    private void initMenuUI() {
        Button btnPlay = new Button();
        btnPlay.setOnAction(evt -> fireNewGame());
        btnPlay.setStyle("-fx-background-image: url('assets/textures/play_button.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;"  +
                "-fx-background-position: center center;");
        btnPlay.setPrefWidth(300);
        btnPlay.setPrefHeight(200);

        Button btnExit = new Button();
        btnExit.setOnAction(evt -> getGameController().exit());
        btnExit.setStyle("-fx-background-image: url('assets/textures/exit_button.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;"  +
                "-fx-background-position: center center;");
        btnExit.setPrefWidth(190);
        btnExit.setPrefHeight(110);
        Button play = new Button("Play");
        play.setOnAction(e -> {
            FXGL.getGameController().startNewGame();
            ((CookingInaMain) FXGL.getApp()).initUI();
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().resumeEngine();
        });

        VBox vbox = new VBox(20, btnPlay, btnExit);
        vbox.setTranslateX(getAppWidth() / 2.0 - 100);
        vbox.setTranslateY(getAppHeight() / 2.0 - 80);
        vbox.setAlignment(Pos.CENTER);

        getContentRoot().getChildren().add(vbox);

    }


    protected void initBackground(){
        Texture background = FXGL.texture("main_menu.png");
        background.setFitHeight(getAppHeight());
        background.setFitWidth(getAppWidth());

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), background);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        getContentRoot().getChildren().add(0, background);
    }
}