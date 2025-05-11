//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.session.Session;
import com.example.cookingina.user.UserCredentials;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;

public class MainMenu extends FXGLMenu {
    private UserCredentials uc;
    public MainMenu() {
        super(MenuType.MAIN_MENU);
        this.initBackground();
        this.initMenuUI();
    }

    private void initMenuUI() {
        // Play Button
        Button btnPlay = new Button();
        btnPlay.setStyle("-fx-background-image: url('assets/textures/play_button.png');-fx-background-size: cover;-fx-background-color: transparent;-fx-background-position: center center;");
        btnPlay.setPrefWidth(300.0);
        btnPlay.setPrefHeight(200.0);
        btnPlay.setOnAction(e -> {
            String user = Session.getUsername();
            FXGL.getSceneService().pushSubScene(new LevelMenu(user));
        });

        // Logout Button
        Button btnLogout = new Button();
        btnLogout.setStyle("-fx-background-image: url('assets/textures/logout_button.png');-fx-background-size: cover;-fx-background-color: transparent;-fx-background-position: center center;");
        btnLogout.setPrefWidth(320.0);
        btnLogout.setPrefHeight(110.0);
        btnLogout.setOnAction(e -> {
            // Clear credentials and session
            new File("credentials.ser").delete();
            uc = new UserCredentials("","");
            uc.save();
            Session.clear();
            // Return to login
            FXGL.getSceneService().pushSubScene(new LoginMenu());
        });

        // Exit Button
        Button btnExit = new Button();
        btnExit.setStyle("-fx-background-image: url('assets/textures/exit_button.png');-fx-background-size: cover;-fx-background-color: transparent;-fx-background-position: center center;");
        btnExit.setPrefWidth(190.0);
        btnExit.setPrefHeight(110.0);
        btnExit.setOnAction(evt -> FXGL.getGameController().exit());

        VBox vbox = new VBox(20.0, btnPlay, btnLogout, btnExit);
        vbox.setAlignment(Pos.CENTER);

        Pane root = this.getContentRoot();
        root.getChildren().add(vbox);

        vbox.layoutXProperty().bind(root.widthProperty().subtract(vbox.widthProperty()).divide(2));
        vbox.layoutYProperty().bind(root.heightProperty().subtract(vbox.heightProperty()).divide(2));
    }

    // initBackground() remains unchanged
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
