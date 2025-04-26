package com.example.cookingina;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.example.cookingina.control.UIController;
import com.example.cookingina.objects.entity.container.*;
import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
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
        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setTitle("CookingIna");
        settings.setVersion("0.1");
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
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
        Fryer fryer = new Fryer(
                "frying_pan.png",                         // name
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

        BeverageDispenser calamansiDispenser = new BeverageDispenser(
                "calamansiJuice_dispenser.png",                         // name
                "dragonfruit_juice_done.png",
                1,
                0,
                1.5,
                500.0,
                4,
                false,
                "calamansi juice");

        BeverageDispenser bukoDispenser = new BeverageDispenser(
                "bukoJuice_dispenser.png",                         // name
                "mangojuice_done.png",
                1,
                0,
                1.5,
                500.0,
                4,
                false,
                "buko juice");

        BeverageDispenser orangeDispenser = new BeverageDispenser(
                "orangeJuice_dispenser.png",                         // name
                "nestea_juice_done.png",
                1,
                0,
                1.5,
                500.0,
                4,
                false,
                "orange juice");

        MangoBasket mangoBasket = new MangoBasket(
                "mango_container.png",
                "",
                "mango basket"
        );

        HotdogContainer hotdogContainer = new HotdogContainer(
                "rawHotdog_container.png",
                "",
                "hotdog container"
        );

        QuekquekContainer quekquekContainer = new QuekquekContainer(
                "rawQuekquek_container.png",
                "",
                "Quekquek container"
        );

        TempuraContainer tempuraContainer = new TempuraContainer(
                "rawTempura_container.png",
                "",
                "Tempura container"
        );

        CucumberContainer cucumberContainer = new CucumberContainer(
                "cucumberGarnish_container.png",
                "",
                "cucumber garnish"
        );

        GusoContainer gusoContainer = new GusoContainer(
                "gusoGarnish_container.png",
                "",
                "guso garnish"
        );

        SpicySauce spicySauce = new SpicySauce(
                "spicy_sauce.png",
                "",
                "spicy sauce"
        );

        SweetSauce sweetSauce = new SweetSauce(
                "sweet_sauce.png",
                "",
                "sweet sauce"
        );

        // FRYING PAN EQUIPMENT
        UIController.spawnEquipment(fryer, 1035, 490, 180, 130);
        UIController.spawnEquipment(fryer, 895, 490, 180, 130);
        UIController.spawnEquipment(fryer, 755, 490, 180, 130);
        UIController.spawnEquipment(fryer, 1045, 590, 175, 130);
        UIController.spawnEquipment(fryer, 895, 590, 175, 130);
        UIController.spawnEquipment(fryer, 745, 590, 175, 130);

        //DISPENSER EQUIPMENT
        UIController.spawnEquipment(calamansiDispenser, 230, 300, 150, 340);
        UIController.spawnEquipment(bukoDispenser, 130, 400, 150, 340);
        UIController.spawnEquipment(orangeDispenser, 30, 500, 150, 340);

        //DISPENSER INVISIBLE
        UIController.spawnInvisibleEquipment(calamansiDispenser, 230, 300, 150, 340);
        UIController.spawnInvisibleEquipment(bukoDispenser, 130, 400, 150, 340);
        UIController.spawnInvisibleEquipment(orangeDispenser, 30, 500, 150, 340);

        //CONTAINER
        UIController.spawnContainer(mangoBasket, 1650, 720, 230, 230);
        UIController.spawnContainer(tempuraContainer, 650, 980, 190, 120);
        UIController.spawnContainer(quekquekContainer, 870, 980, 190, 120);
        UIController.spawnContainer(hotdogContainer, 1080, 980, 190, 120);
        UIController.spawnContainer(cucumberContainer, 1340, 720, 130, 100);
        UIController.spawnContainer(gusoContainer, 1390, 820, 140, 110);
        UIController.spawnContainer(spicySauce, 1270, 460, 60, 160);
        UIController.spawnContainer(sweetSauce, 1330, 550, 60, 160);
    } 

    public static void main(String[] args) {
        launch(args);
    }

    public void setBackground() {
        double width = FXGL.getAppWidth();
        double height = FXGL.getAppHeight();

        Texture backgroundTexture = FXGL.getAssetLoader().loadTexture("final_background.png");
        backgroundTexture.setFitWidth(width);
        backgroundTexture.setFitHeight(height);

        entityBuilder()
                .at(0, 0)
                .view(backgroundTexture)
                .zIndex(-1)
                .buildAndAttach();
    }
}