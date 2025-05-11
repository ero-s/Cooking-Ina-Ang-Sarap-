package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getGameController;

public class MainMenu extends FXGLMenu {
    public MainMenu() {
        super(MenuType.MAIN_MENU);

        initBackground();
        initMenuUI();
    }
    private void initMenuUI() {
        // Play Button
        Button btnPlay = createGameButton("PLAY", "#2ecc71");
        btnPlay.setPrefSize(300, 100);
        btnPlay.setOnAction(e -> {
            FXGL.getGameController().startNewGame();
            ((CookingInaMain) FXGL.getApp()).initUI();
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().resumeEngine();
        });

        // Exit Button
        Button btnExit = createGameButton("EXIT", "#e74c3c");
        btnExit.setPrefSize(200, 80);
        btnExit.setOnAction(evt -> getGameController().exit());

        VBox vbox = new VBox(30, btnPlay, btnExit);
        vbox.setAlignment(Pos.CENTER);

        Pane root = getContentRoot();
        root.getChildren().add(vbox);

        vbox.layoutXProperty().bind(
                root.widthProperty()
                        .subtract(vbox.widthProperty())
                        .divide(2)
        );
        vbox.layoutYProperty().bind(
                root.heightProperty()
                        .subtract(vbox.heightProperty())
                        .divide(2)
        );
    }

    private Button createGameButton(String text, String color) {
        Button button = new Button();
        button.setGraphic(createButtonContent( text));

        String baseStyle = "-fx-background-color: " + color + ";"
                + "-fx-background-radius: 15;"
                + "-fx-border-radius: 15;"
                + "-fx-border-width: 0;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);"
                + "-fx-cursor: hand;"
                + "-fx-padding: 15 30 15 30;";

        button.setStyle(baseStyle);

        // Hover effects
        button.setOnMouseEntered(e -> {
            button.setStyle(baseStyle
                    + "-fx-border-width: 3;"
                    + "-fx-border-color: white;"
                    + "-fx-background-color: derive(" + color + ", -20%);");
        });

        button.setOnMouseExited(e -> {
            button.setStyle(baseStyle);
        });

        return button;
    }



    private StackPane createButtonContent( String text) {
        StackPane container = new StackPane();
        VBox content = new VBox(10, createButtonText(text));
        content.setAlignment(Pos.CENTER);
        container.getChildren().add(content);
        return container;
    }

    private Text createButtonText(String text) {
        Text textNode = new Text(text);

        // Modern font stack with fallbacks
        String fontStyle = "-fx-font-family: 'Segoe UI Black', 'Lucida Calligraphy', 'Roboto', sans-serif;"
                + "-fx-font-size: 26px;"
                + "-fx-font-weight: 700;"
                + "-fx-letter-spacing: 1.2px;"
                + "-fx-fill: white;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0.5, 1, 1);";

        textNode.setStyle(fontStyle);
        return textNode;
    }


    protected void initBackground(){
        Texture background = FXGL.texture("main_menu.png");
        background.setFitHeight(getAppHeight());
        background.setFitWidth(getAppWidth());

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), background);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        getContentRoot().getChildren().add(0, background);
    }
}