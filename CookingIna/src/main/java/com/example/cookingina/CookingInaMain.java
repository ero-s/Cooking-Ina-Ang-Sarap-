package com.example.cookingina;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.example.cookingina.control.UIController;
import com.example.cookingina.objects.entity.TrashCan;
import com.example.cookingina.objects.entity.equipment.Fryer;
import com.example.cookingina.objects.entity.storeItem.QuekQuek;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class CookingInaMain extends GameApplication {

    public enum EntityType {
        INGREDIENT,
        EQUIPMENT,
        CONTAINER,
        TRASH
    }

    public static Text debugText;

    @Override
    protected void initUI() {
        // Create debug text element
        debugText = new Text();
        debugText.setFont(Font.font(14));
        debugText.setFill(Color.WHITE);
        debugText.setTranslateX(10);
        debugText.setTranslateY(20);

        // Add to game scene
        FXGL.getGameScene().addUINode(debugText);
    }

    @Override
    protected void initInput() {
        FXGL.onKeyDown(KeyCode.F1, "Toggle debug", () -> {
            debugText.setVisible(!debugText.isVisible());
        });
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1000);
        settings.setHeight(700);
        settings.setTitle("CookingIna");
        settings.setVersion("0.1");
        settings.setMainMenuEnabled(true);

        settings.setSceneFactory(new SceneFactory() {
            @NotNull
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }
        });
    }

    @Override
    protected void initGame() {
        setBackground();
        setCookingStations();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setBackground() {
        double width = FXGL.getAppWidth();
        double height = FXGL.getAppHeight();

        Texture backgroundTexture = FXGL.getAssetLoader().loadTexture("background.png");
        backgroundTexture.setFitWidth(width);
        backgroundTexture.setFitHeight(height);

        entityBuilder()
                .at(0, 0)
                .view(backgroundTexture)
                .zIndex(-1)
                .buildAndAttach();
    }

    private void setCookingStations(){
        Fryer fryer = new Fryer(
                "emptyPan.png",                         // name
                "usedPan.png",
                1,                                              // type (e.g., 1 = cooking equipment)
                0,                                              // playend (initial value)
                1.5,                                            // speedMultiplier (50% faster cooking)
                500.0,                                          // cost ($500)
                4,                                              // capacity (4 items at once)
                false,                                          // isUnlocked (initially locked)
                "A standard fryer for basic cooking needs");    // description);

        QuekQuek quekquek = new QuekQuek(
                "quekquek_container.png",
                "quekquek.png",                      // raw resource identifier
                "cooked_quekquek.png",                          // cooked resource
                "lami",                                         // description
                15.0,                                           // preparationTime (minutes)
                12.99,                                          // sellingPrice ($)
                2.0,                                            // discardCost ($)
                1);                                             // status (1 = available)

        UIController.spawnEquipment(fryer, 535, 350);
        UIController.spawnContainer(quekquek, fryer, 590, 570);
        UIController.spawnTrashcan(850,560);
    }
}