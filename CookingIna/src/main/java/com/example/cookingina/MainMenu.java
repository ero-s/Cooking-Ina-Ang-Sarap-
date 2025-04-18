package com.example.cookingina;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

public class MainMenu extends FXGLMenu {
    public MainMenu() {
        super(MenuType.MAIN_MENU);

        // you can use FXGL's built‑in MenuButton class for styling
        var play = new Button("Play");
        play.setOnAction(evt -> fireNewGame());  // triggers initGame()

        // optionally add more buttons (Options, Exit, etc.)
        var exit = new Button("Exit");
        exit.setOnAction(evt -> fireExit());

        // layout them vertically with a bit of spacing
        VBox box = new VBox(20, play, exit);
        box.setTranslateX( getAppWidth() / 2.0 - 100 );
        box.setTranslateY( getAppHeight() / 2.0 - 50 );

        getContentRoot().getChildren().add(box);
    }
}
