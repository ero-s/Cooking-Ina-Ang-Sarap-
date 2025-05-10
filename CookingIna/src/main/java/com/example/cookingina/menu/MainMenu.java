package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.database.DatabaseManager;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.time.LocalDateTime;

public class MainMenu extends FXGLMenu {

    private Text messageText;
    private String currentUsername;

    public MainMenu() {
        super(MenuType.MAIN_MENU);
        createMainMenu();
    }

    private void createMainMenu() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> showLoginForm());

        Button btnRegister = new Button("Register");
        btnRegister.setOnAction(e -> showRegisterForm());

        Button btnExit = new Button("Exit");
        btnExit.setOnAction(e -> fireExit());

        messageText = new Text();
        messageText.setFill(Color.RED);

        box.getChildren().addAll(btnLogin, btnRegister, btnExit, messageText);
        centerElements(box);
        getContentRoot().getChildren().setAll(box);
    }

    private void showLoginForm() {
        messageText.setText("");

        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");

        Button btnSubmit = new Button("Login");
        btnSubmit.setOnAction(e -> handleLogin(txtUsername.getText(), txtPassword.getText()));

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> createMainMenu());

        loginBox.getChildren().addAll(
                new Label("Login"),
                txtUsername,
                txtPassword,
                btnSubmit,
                btnBack,
                messageText
        );

        centerElements(loginBox);
        getContentRoot().getChildren().setAll(loginBox);
    }

    private void showRegisterForm() {
        messageText.setText("");

        VBox registerBox = new VBox(15);
        registerBox.setAlignment(Pos.CENTER);

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");

        Button btnSubmit = new Button("Register");
        btnSubmit.setOnAction(e -> handleRegistration(txtUsername.getText(), txtPassword.getText()));

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> createMainMenu());

        registerBox.getChildren().addAll(
                new Label("Register"),
                txtUsername,
                txtPassword,
                btnSubmit,
                btnBack,
                messageText
        );

        centerElements(registerBox);
        getContentRoot().getChildren().setAll(registerBox);
    }

    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (DatabaseManager.validateLogin(username, password)) {
            currentUsername = username;

            int savedLevel = DatabaseManager.getPlayerLevel(username);
            LocalDateTime joinDate = DatabaseManager.getJoinDate(username);

            CookingInaMain game = (CookingInaMain) FXGL.getApp();
            game.setCurrentPlayerLevel(savedLevel);
            game.setJoinDate(joinDate);  // Set the join date

            FXGL.getGameController().startNewGame();
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().resumeEngine();
        } else {
            showError("Invalid username or password");
        }
    }

    private void handleRegistration(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        int result = DatabaseManager.registerUser(username, password);

        if (result == 0) {
            showSuccess("Registration successful! Please login");
            showLoginForm();
        } else if (result == 1) {
            showError("Username already exists");
        } else {
            showError("Registration failed. Please try again");
        }
    }

    private void startGame() {
        int savedLevel = DatabaseManager.getPlayerLevel(currentUsername);
        String displayName = DatabaseManager.getDisplayName(currentUsername);

        CookingInaMain game = (CookingInaMain) FXGL.getApp();
        game.setCurrentPlayerLevel(savedLevel);
        //game.setCurrentDisplayName(displayName);

        FXGL.getGameController().startNewGame();
        FXGL.getSceneService().popSubScene();
        FXGL.getGameController().resumeEngine();
    }

    private void showError(String message) {
        messageText.setFill(Color.RED);
        messageText.setText(message);
    }

    private void showSuccess(String message) {
        messageText.setFill(Color.GREEN);
        messageText.setText(message);
    }

    private void centerElements(VBox box) {
        box.setTranslateX(getAppWidth() / 2 - 150);
        box.setTranslateY(getAppHeight() / 2 - 200);
    }
}
