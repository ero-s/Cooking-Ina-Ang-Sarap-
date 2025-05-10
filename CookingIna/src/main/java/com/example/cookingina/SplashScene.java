package com.example.cookingina;

import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.scene.SubScene;
import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class SplashScene extends SubScene {
    public SplashScene() {
        try {
            System.out.println("Creating splash screen...");

            // Basic fallback content
            Rectangle background = new Rectangle(getAppWidth(), getAppHeight(), Color.BLACK);
            getContentRoot().getChildren().add(background);

            // Try loading image
            ImageView splashImage = new ImageView(new Image(
                    getClass().getResourceAsStream("/assets/textures/splash_scene.png")
            ));
            splashImage.setFitWidth(getAppWidth());
            splashImage.setFitHeight(getAppHeight());
            getContentRoot().getChildren().add(splashImage);

            System.out.println("Splash resources loaded successfully");
        } catch (Exception e) {
            System.err.println("SPLASH SCREEN ERROR:");
            e.printStackTrace();

            // Essential fallback
            getContentRoot().getChildren().add(new Text("Loading..."));
        }
    }
}