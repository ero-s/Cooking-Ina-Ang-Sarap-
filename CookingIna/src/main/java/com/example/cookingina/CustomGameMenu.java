package com.example.cookingina;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CustomGameMenu extends FXGLMenu {

    public CustomGameMenu(MenuType type) {
        super(type);

        // Create the menu text
        Text title = new Text("Game Paused");
        title.setStyle("-fx-font-size: 32px; -fx-fill: white;");

        // Slider for sound settings
        Text soundLabel = new Text("Sound Volume");
        Slider soundSlider = new Slider(0, 100, 50); // Min, Max, Default value
        soundSlider.setBlockIncrement(5); // Step size for the slider
        soundSlider.setStyle("-fx-pref-width: 200px;");

        // Create the buttons
        Button resumeButton = new Button("Resume");
        resumeButton.setOnAction(e -> fireResume());

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> fireSettings());

        Button exitButton = new Button("Exit to Main Menu");
        exitButton.setOnAction(e -> fireExitToMainMenu());

        // Arrange the buttons in a vertical box
        VBox menuBox = new VBox(20, title, resumeButton, settingsButton, exitButton);
        menuBox.setAlignment(Pos.CENTER);

        // Set the position for the menu
        menuBox.setTranslateX(FXGL.getAppWidth() / 2 - 150); // Center horizontally
        menuBox.setTranslateY(FXGL.getAppHeight() / 2 - 150); // Center vertically

        // Add the menu to the scene
        getContentRoot().getChildren().add(menuBox);
    }

    private void fireSettings() {
    }
}
