//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.example.cookingina.session.Session;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainMenu extends FXGLMenu {
    public MainMenu() {
        super(MenuType.MAIN_MENU);
        this.initBackground();
        this.initMenuUI();
    }

    private void initMenuUI() {
        Button btnPlay = new Button();
        btnPlay.setOnAction((evt) -> this.fireNewGame());
        btnPlay.setStyle("-fx-background-image: url('assets/textures/play_button.png');-fx-background-size: cover;-fx-background-color: transparent;-fx-background-position: center center;");
        btnPlay.setPrefWidth((double)300.0F);
        btnPlay.setPrefHeight((double)200.0F);
        Button btnExit = new Button();
        btnExit.setOnAction((evt) -> FXGL.getGameController().exit());
        btnExit.setStyle("-fx-background-image: url('assets/textures/exit_button.png');-fx-background-size: cover;-fx-background-color: transparent;-fx-background-position: center center;");
        btnExit.setPrefWidth((double)190.0F);
        btnExit.setPrefHeight((double)110.0F);
        btnPlay.setOnAction((e) -> {
            String user = Session.getUsername();
            FXGL.getSceneService().pushSubScene(new LevelMenu(user));
        });
        VBox vbox = new VBox((double)20.0F, new Node[]{btnPlay, btnExit});
        vbox.setAlignment(Pos.CENTER);
        Pane root = this.getContentRoot();
        root.getChildren().add(vbox);
        vbox.layoutXProperty().bind(root.widthProperty().subtract(vbox.widthProperty()).divide(2));
        vbox.layoutYProperty().bind(root.heightProperty().subtract(vbox.heightProperty()).divide(2));
    }

    protected void initBackground() {
        Texture background = FXGL.texture("main_menu.png");
        background.setFitHeight((double)this.getAppHeight());
        background.setFitWidth((double)this.getAppWidth());
        FadeTransition fade = new FadeTransition(Duration.seconds((double)1.5F), background);
        fade.setFromValue((double)0.0F);
        fade.setToValue((double)1.0F);
        fade.play();
        this.getContentRoot().getChildren().add(0, background);
    }
}
