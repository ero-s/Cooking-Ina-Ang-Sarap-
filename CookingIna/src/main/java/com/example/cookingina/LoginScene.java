package com.example.cookingina;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;


public class LoginScene extends SubScene {

    public LoginScene() {

        // === Background image or color ===
        Image backgroundImage = new Image("assets/textures/store_background.png"); // adjust path
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 600, false, false, false, false)
        );
        getRoot().setBackground(new Background(bgImage));

        // === UI elements ===
        Text title = new Text("Login to CookingIna");
        title.setFont(Font.font("Arial", 24));
        title.setFill(Color.WHITE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Text message = new Text();
        message.setFill(Color.YELLOW);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #ff8800; -fx-text-fill: white;");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (isValidLogin(username, password)) {
                message.setText("Login successful!");

                FXGL.getSceneService().popSubScene();

                FXGL.getGameController().gotoMainMenu();
            } else {
                message.setText("Invalid username or password.");
            }
        });

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #0099ff; -fx-text-fill: white;");
        registerButton.setOnAction(e -> {
            getSceneService().pushSubScene(new RegisterScene());
        });

        VBox layout = new VBox(15, title, usernameField, passwordField, loginButton, registerButton, message);
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 20; -fx-background-radius: 10;");
        layout.setTranslateX(250);
        layout.setTranslateY(150);

        getRoot().getChildren().add(layout);
    }

    private boolean isValidLogin(String username, String password) {
        return username.equals("player") && password.equals("1234");
    }
}
