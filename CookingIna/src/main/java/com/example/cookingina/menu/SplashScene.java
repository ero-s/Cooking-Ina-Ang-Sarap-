package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.database.DatabaseManager;
import com.example.cookingina.user.UserCredentials;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalDateTime;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getSceneService;

/**
 * Splash scene that displays for 3 seconds then always transitions to the LoginMenu.
 */
public class SplashScene extends FXGLMenu {
    public SplashScene() {
        super(MenuType.MAIN_MENU);

        // Black background fallback
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

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(evt -> {
            UserCredentials creds = UserCredentials.load();

            if (creds == null) {
                // No saved credentials -> login
                getSceneService().pushSubScene(new LoginMenu());

            } else if (DatabaseManager.validateLogin(
                    creds.getUsername(), creds.getPassword())) {
                // Auto-login success
                int level = DatabaseManager.getPlayerLevel(creds.getUsername());
                LocalDateTime joinDate = DatabaseManager.getJoinDate(creds.getUsername());

                CookingInaMain.setCurrentPlayerLevel(level);
                CookingInaMain.setJoinDate(joinDate);
                getSceneService().pushSubScene(new MainMenu());

            } else {
                // Invalid saved credentials -> clear and go to login
                new File("credentials.ser").delete();
                getSceneService().pushSubScene(new LoginMenu());
            }
        });
        delay.play();
    }
}

