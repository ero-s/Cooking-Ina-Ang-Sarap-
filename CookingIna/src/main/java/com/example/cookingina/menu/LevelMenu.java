package com.example.cookingina.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.database.DatabaseManager;
import com.example.cookingina.model.LevelData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class LevelMenu extends FXGLMenu {

    private int currentLevel;

    public LevelMenu(String currentUsername) {
        super(MenuType.MAIN_MENU);

        Pane levelPane = new Pane();
        levelPane.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());

        ImageView bg = new ImageView(getAssetLoader().loadImage("level_map.png"));
        bg.setFitWidth(FXGL.getAppWidth());
        bg.setFitHeight(FXGL.getAppHeight());
        levelPane.getChildren().add(bg);

        currentLevel = DatabaseManager.getPlayerLevel(currentUsername);
        loadLevelMenu(levelPane, currentLevel);

        getContentRoot().getChildren().add(levelPane);
        System.out.println(currentUsername);
    }

    private void loadLevelMenu(Pane pane, int currentLevel) {
        List<LevelData> levels = DatabaseManager.getAllLevels(currentLevel);

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
                    Map<String, Object> gameParameters = Map.of(
                            "quota", level.targetIncome,
                            "maxCustomers", level.maxCustomers,
                            "time", level.timeLimit
                    );

                    FXGL.getGameController().startNewGame();
                    ((CookingInaMain) FXGL.getApp()).initUI();
                    FXGL.getSceneService().popSubScene();
                    FXGL.getGameController().resumeEngine();

                    System.out.println(level);
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
