package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.example.cookingina.session.Session;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

        btnPlay.setOnAction(e -> {
            String user = Session.getUsername();
            FXGL.getSceneService().pushSubScene(new LevelMenu(user));
        });

        VBox vbox = new VBox(20, btnPlay, btnExit);
        vbox.setAlignment(Pos.CENTER);

        Pane root = getContentRoot();
        root.getChildren().add(vbox);

        vbox.layoutXProperty().bind(
                root.widthProperty()
                        .subtract(vbox.widthProperty())
                        .divide(2)
        );
        vbox.layoutYProperty().bind(
                root.heightProperty()
                        .subtract(vbox.heightProperty())
                        .divide(2)
        );
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