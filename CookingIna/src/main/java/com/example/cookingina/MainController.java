package com.example.cookingina;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {
    public Button btnRegister;
    public Button btnLogin;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void onRegisterButtonClick(ActionEvent actionEvent) {
    }

    public void onLoginButtonClick(ActionEvent actionEvent) {
    }
}