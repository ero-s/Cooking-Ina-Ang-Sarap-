package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.database.DatabaseManager;
import com.example.cookingina.session.Session;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;

public class LoginMenu extends FXGLMenu {

    private Text messageText;
    private String currentUsername;

    public LoginMenu() {
        super(MenuType.MAIN_MENU);
        initBackground();
        createMainMenu();
    }

    private void createMainMenu() {
        VBox box = new VBox(30);
        box.setAlignment(Pos.CENTER); // Center contents inside the VBox

        Button btnLogin = new Button();
        btnLogin.setOnAction(e -> showLoginForm());
        btnLogin.setStyle("-fx-background-image: url('assets/textures/login_button.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;" +
                "-fx-background-position: center center;");
        btnLogin.setPrefSize(450, 160);

        Button btnRegister = new Button();
        btnRegister.setOnAction(e -> showRegisterForm());
        btnRegister.setStyle("-fx-background-image: url('assets/textures/register_button.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;" +
                "-fx-background-position: center center;");
        btnRegister.setPrefSize(370, 130);

        Button btnExit = new Button();
        btnExit.setOnAction(e -> fireExit());
        btnExit.setStyle("-fx-background-image: url('assets/textures/exit_button_login.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;" +
                "-fx-background-position: center center;");
        btnExit.setPrefSize(260, 90);

        messageText = new Text();
        messageText.setFill(Color.RED);

        box.getChildren().addAll(btnLogin, btnRegister, btnExit, messageText);

        StackPane root = new StackPane();
        root.setPrefSize(getAppWidth(), getAppHeight());
        root.getChildren().add(box);
        StackPane.setAlignment(box, Pos.CENTER);

        Texture bg = FXGL.texture("store_background.png");
        bg.setFitWidth(getAppWidth());
        bg.setFitHeight(getAppHeight());

        getContentRoot().getChildren().setAll(bg, root);
    }


    private void showLoginForm() {
        messageText.setText("");

        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);

        TextField txtUsername = createStyledTextField("Username");
        PasswordField txtPassword = createStyledPasswordField("Password");

        Button btnSubmit = new Button();
        btnSubmit.setOnAction(e -> handleLogin(txtUsername.getText(), txtPassword.getText()));
        btnSubmit.setStyle("-fx-background-image: url('assets/textures/login_button.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;" +
                "-fx-background-position: center center;");
        btnSubmit.setPrefSize(280, 100);

        Button btnBack = new Button();
        btnBack.setOnAction(e -> createMainMenu());
        btnBack.setStyle("-fx-background-image: url('assets/textures/back_button.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;" +
                "-fx-background-position: center center;");
        btnBack.setPrefSize(240, 80);

        loginBox.getChildren().addAll(txtUsername, txtPassword, btnSubmit, btnBack, messageText);

        VBox container = new VBox(loginBox);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: transparent; -fx-padding: 40");
        container.setMaxWidth(500);

        StackPane root = new StackPane();
        root.setPrefSize(getAppWidth(), getAppHeight());
        root.getChildren().add(container);
        StackPane.setAlignment(container, Pos.CENTER);

        Texture bg = FXGL.texture("store_background.png");
        bg.setFitWidth(getAppWidth());
        bg.setFitHeight(getAppHeight());

        getContentRoot().getChildren().setAll(bg, root);
    }

    private void showRegisterForm() {
        messageText.setText("");

        VBox registerBox = new VBox(15);
        registerBox.setAlignment(Pos.CENTER);

        TextField txtUsername = createStyledTextField("Username");
        PasswordField txtPassword = createStyledPasswordField("Password");

        Button btnSubmit = new Button();
        btnSubmit.setOnAction(e -> handleRegistration(txtUsername.getText(), txtPassword.getText()));
        btnSubmit.setStyle("-fx-background-image: url('assets/textures/register_button.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;" +
                "-fx-background-position: center center;");
        btnSubmit.setPrefSize(290, 100);

        Button btnBack = new Button();
        btnBack.setOnAction(e -> createMainMenu());
        btnBack.setStyle("-fx-background-image: url('assets/textures/back_button.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-color: transparent;" +
                "-fx-background-position: center center;");
        btnBack.setPrefSize(240, 80);

        registerBox.getChildren().addAll(
                new Label(),
                txtUsername,
                txtPassword,
                btnSubmit,
                btnBack,
                messageText
        );

        VBox container = new VBox(registerBox);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: transparent; -fx-padding: 40");
        container.setMaxWidth(500);

        StackPane root = new StackPane();
        root.setPrefSize(getAppWidth(), getAppHeight());
        root.getChildren().add(container);
        StackPane.setAlignment(container, Pos.CENTER);

        Texture bg = FXGL.texture("store_background.png");
        bg.setFitWidth(getAppWidth());
        bg.setFitHeight(getAppHeight());

        getContentRoot().getChildren().setAll(bg, root);;
    }

    private TextField createStyledTextField(String hint) {
        TextField field = new TextField();
        field.setPromptText(hint);
        field.setStyle(
                "-fx-background-image: url('assets/textures/input_field.png');" +
                        "-fx-background-size: stretch;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-color: transparent;" +
                        "-fx-background-position: center center;" +
                        "-fx-text-fill: black;" +
                        "-fx-prompt-text-fill: gray;" +
                        "-fx-padding: 20;" +
                        "-fx-font-family: 'Tahoma';" +
                        "-fx-font-size: 21px;"
        );
        field.setPrefSize(400, 100);
        return field;
    }

    private PasswordField createStyledPasswordField(String hint) {
        PasswordField field = new PasswordField();
        field.setPromptText(hint);
        field.setStyle(
                "-fx-background-image: url('assets/textures/input_field.png');" +
                        "-fx-background-size: stretch;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-color: transparent;" +
                        "-fx-background-position: center center;" +
                        "-fx-text-fill: black;" +
                        "-fx-prompt-text-fill: gray;" +
                        "-fx-padding: 20;" +
                        "-fx-font-family: 'Tahoma';" +
                        "-fx-font-size: 21px;"
        );
        field.setPrefSize(400, 100);
        return field;
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
            game.setJoinDate(joinDate);
            Session.setUsername(username);
            FXGL.getSceneService().pushSubScene(new MainMenu());
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

    private void showError(String message) {
        messageText.setFill(Color.RED);
        messageText.setText(message);
    }

    private void showSuccess(String message) {
        messageText.setFill(Color.GREEN);
        messageText.setText(message);
    }

    private void centerElements(VBox box) {
        StackPane wrapper = new StackPane(box);
        wrapper.setPrefSize(getAppWidth(), getAppHeight());
        wrapper.setAlignment(Pos.CENTER);

        getContentRoot().getChildren().setAll(wrapper);
    }

    protected void initBackground() {
        Texture background = FXGL.texture("store_background.png");
        background.setFitWidth(getAppWidth());
        background.setFitHeight(getAppHeight());

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), background);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        getContentRoot().getChildren().add(0, background);
    }
}
