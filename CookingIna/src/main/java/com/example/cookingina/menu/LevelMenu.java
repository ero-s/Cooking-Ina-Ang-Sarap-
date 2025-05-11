package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.database.DatabaseManager;
import com.example.cookingina.model.LevelData;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class LevelMenu extends FXGLMenu {

    private int currentLevel;
    public CookingInaMain game = new CookingInaMain();
    public List<LevelData> levels = DatabaseManager.getAllLevels(game.getPlayerLevel());


    public LevelMenu(String currentUsername) {
        super(MenuType.MAIN_MENU);

        Pane levelPane = new Pane();
        levelPane.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());

        // Background
        ImageView bg = new ImageView(getAssetLoader().loadImage("level_map.png"));
        bg.setFitWidth(FXGL.getAppWidth());
        bg.setFitHeight(FXGL.getAppHeight());
        levelPane.getChildren().add(bg);

        // Levels
        currentLevel = DatabaseManager.getPlayerLevel(currentUsername);
        loadLevelMenu(levelPane, currentLevel);

        // Back Button
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-font-size: 16px; "
                + "-fx-padding: 8 15; "
                + "-fx-background-color: #4CAF50; "
                + "-fx-text-fill: white; "
                + "-fx-background-radius: 5; "
                + "-fx-cursor: hand;");

        // Position in top-left corner
        backButton.setLayoutX(20);
        backButton.setLayoutY(20);

        // Action to return to main menu
        backButton.setOnAction(e -> FXGL.getSceneService().popSubScene());

        // Add button to the pane (after background but before levels)
        levelPane.getChildren().add(backButton);

        getContentRoot().getChildren().add(levelPane);
    }

    private void loadLevelMenu(Pane pane, int currentLevel) {

        double x = 100;
        double y = 150;
        int count = 0;

        for (LevelData level : levels) {
            ImageView levelImage = new ImageView();
            String imgPath = "level" + level.levelId
                    + (level.unlocked ? "_unlocked.png" : "_locked.png");
            levelImage.setImage(getAssetLoader().loadImage(imgPath));
            levelImage.setFitWidth(375);
            levelImage.setFitHeight(375);
            levelImage.setLayoutX(x);
            levelImage.setLayoutY(y);

            if (level.unlocked) {
                levelImage.setOnMouseClicked(e -> {
                    FXGL.set("quota", level.targetIncome);
                    FXGL.set("maxCustomers", level.maxCustomers);
                    FXGL.set("time", level.timeLimit);

                    FXGL.getGameController().startNewGame();
                    ((CookingInaMain) FXGL.getApp()).initUI();
                    FXGL.getSceneService().popSubScene();
                    FXGL.getGameController().resumeEngine();
                });
            } else {
                levelImage.setOpacity(0.8);
            }

            pane.getChildren().add(levelImage);

            count++;
            x += 360;

            if (count % 5 == 0) {
                x = 100;
                y += 375 + 50;
            }
        }
    }
}