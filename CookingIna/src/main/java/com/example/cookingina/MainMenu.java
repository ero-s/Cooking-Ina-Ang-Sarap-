package com.example.cookingina;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

// Modified MainMenu class (original remains similar)
public class MainMenu extends FXGLMenu {
    public MainMenu() {
        super(MenuType.MAIN_MENU);

        var play = new Button("Play");
        play.setOnAction(evt -> fireNewGame());

        var exit = new Button("Exit");
        exit.setOnAction(evt -> fireExit());

        VBox box = new VBox(20, play, exit);
        centerElements(box);

        getContentRoot().getChildren().add(box);
    }

    private void centerElements(VBox box) {
        box.setTranslateX(getAppWidth() / 2.0 - 100);
        box.setTranslateY(getAppHeight() / 2.0 - 50);
    }
}