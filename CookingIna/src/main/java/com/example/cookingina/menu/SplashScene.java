package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.getSceneService;

public class SplashScene extends FXGLMenu {
    public SplashScene() {
        super(MenuType.MAIN_MENU);

        // Black background
        Rectangle background = new Rectangle(getAppWidth(), getAppHeight(), Color.BLACK);
        getContentRoot().getChildren().add(background);

        // Splash image or fallback text
        try {
            ImageView splashImage = new ImageView(new Image(
                    getClass().getResourceAsStream("/assets/textures/splash_scene.png")
            ));
            splashImage.setFitWidth(getAppWidth());
            splashImage.setFitHeight(getAppHeight());
            getContentRoot().getChildren().add(splashImage);
        } catch (Exception e) {
            getContentRoot().getChildren().add(new Text("Loading..."));
        }

        // After 3 seconds, transition to MainMenu
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(evt -> {
            // Push your MainMenu scene
            getSceneService().pushSubScene(new LoginMenu());
        });
        delay.play();
    }
}
