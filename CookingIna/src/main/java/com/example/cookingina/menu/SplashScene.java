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

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getSceneService;

/**
 * Splash scene that displays for 3 seconds then always transitions to the LoginMenu.
 */
public class SplashScene extends FXGLMenu {
    public SplashScene() {
        super(MenuType.MAIN_MENU);

        // Build static UI (background + splash image or fallback)
        Rectangle background = new Rectangle(getAppWidth(), getAppHeight(), Color.BLACK);
        getContentRoot().getChildren().add(background);

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
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Schedule transition every time the scene is created or re-entered
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(evt -> getSceneService().pushSubScene(new LoginMenu()));
        delay.play();
    }
}
