package com.example.cookingina;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import com.example.cookingina.control.UIController;
import com.example.cookingina.objects.entity.PaperTray;
import com.example.cookingina.objects.entity.TrashCan;
import com.example.cookingina.objects.entity.container.*;
import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
import com.example.cookingina.objects.entity.equipment.Fryer;
import com.example.cookingina.objects.entity.equipment.TrashBin;
import com.example.cookingina.objects.entity.storeItem.Hotdog;
import com.example.cookingina.objects.entity.storeItem.QuekQuek;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import customers.SpeechBubbleComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class CookingInaMain extends GameApplication {

    private final List<Fryer> fryers = new ArrayList<>();
    private final List<PaperTray> paperTrays = new ArrayList<>();
    public UIController uc = new UIController();


    public enum EntityType {
        INGREDIENT,
        EQUIPMENT,
        CONTAINER,
        TRASH,
        PLATE,
        CUSTOMER,
        SPEECH_BUBBLE
    }

    public static Text debugText;
    private ProgressBar timerBar;
    private Timeline timerTimeline;
    private static final double TOTAL_TIME = 60.0; // seconds

    @Override
    protected void initUI() {
        // Create debug text element
        debugText = new Text();
        debugText.setFont(Font.font(14));
        debugText.setFill(Color.WHITE);
        debugText.setTranslateX(10);
        debugText.setTranslateY(20);
        setProgressBar();

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

        for (Entity entity : FXGL.getGameWorld().getEntitiesCopy()) {
            entity.removeFromWorld();
        }
        uc = null;
        resetGameState();
        setBackground();
        spawnAssets();
        startTimer();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("income", 0); // initialize 'income' to avoid crash
    }


    private void setBackground() {
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
    private void spawnAssets(){
        uc = new UIController();
        for(int i = 1; i <= 6; i++){
            fryers.add(new Fryer(
                    "frying_pan.png",                         // name
                    "usedPan.png",
                    i,                                              // type (e.g., 1 = cooking equipment)
                    0,                                              // playend (initial value)
                    1.5,                                            // speedMultiplier (50% faster cooking)
                    500.0,                                          // cost ($500)
                    1,                                              // capacity (4 items at once)
                    false,                                          // isUnlocked (initially locked)
                    "A standard fryer for basic cooking needs"    // description);
            ));
        }

        for(int i = 0; i < 6; i++){
            paperTrays.add(new PaperTray(
                    "frying_pan.png",                         // name
                    "papertray.png",
                    180, 130
            ));
        }

        UIController.setFryers(fryers);

// ================= SELLING ITEMS ENTITTY =================
        uc.spawnCustomerAtRandomIntervals();

        QuekQuek quekquek = new QuekQuek(
                "rawQuekquek_container.png",
                "rawQuekquek.png",                      // raw resource identifier
                "cooked_quek-quek.png",                          // cooked resource
                "quekquek",                                         // description
                15.0,                                           // preparationTime (minutes)
                12.99,                                          // sellingPrice ($)
                2.0,                                            // discardCost ($)
                1,
                80,
                80
        );                                             // status (1 = available)

        Hotdog hotdog = new Hotdog(
                "rawHotdog_container.png",
                "rawHotdog.png",
                "rawHotdog.png",
                "hotdog",
                15.0,
                15.00,
                3.0,
                1,
                80,
                80
        );
// ================= CONTAINER ENTITY =================

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

        Bagoong bagoong = new Bagoong(
                "hipon_bottle.png",
                "" ,
                "bagoong hipon"
        );

        SaltContainer salt = new SaltContainer(
                "salt_bottle.png",
                "",
                "salt"
        );




        for(Fryer fryer : fryers){
            if(fryer.getType() == 1){
                uc.spawnEquipment(fryer, 1035, 490, 180, 130);
            } else if (fryer.getType() == 2){
                uc.spawnEquipment(fryer, 895, 490, 180, 130);
            } else if (fryer.getType() == 3){
                uc.spawnEquipment(fryer, 755, 490, 180, 130);
            } else if (fryer.getType() == 4) {
                uc.spawnEquipment(fryer, 1045, 590, 175, 130);
            } else if (fryer.getType() == 5){
                uc.spawnEquipment(fryer, 895, 590, 175, 130);
            } else {
                uc.spawnEquipment(fryer, 745, 590, 175, 130);
            }
        }

        for(int i = 0; i < 6; i++){
            PaperTray paperTray = paperTrays.get(i);
            if(i == 0){
                uc.spawnPaperTray(paperTray, 1090, 750, 180, 130);
            }else if(i == 1){
                uc.spawnPaperTray(paperTray, 895, 750, 180, 130);
            }else if(i == 2){
                uc.spawnPaperTray(paperTray, 695, 750, 180, 130);
            }else if(i == 3){
                uc.spawnPaperTray(paperTray, 1090, 840, 175, 130);
            }else if(i == 4){
                uc.spawnPaperTray(paperTray, 895, 840, 175, 130);
            }else if(i == 5){
                uc.spawnPaperTray(paperTray, 695, 840, 175, 130);
            }
        }

        // Add trashCan asset
        TrashBin trashBin = new TrashBin(
                "trash_closed.png",                         // name
                "trash_open.png",
                1,
                0,
                0,
                0,
                false,
                "orange juice");

        uc.spawnContainerForEquipment(quekquek, fryers, 870, 980, 190, 120);
        uc.spawnContainerForEquipment(hotdog, fryers, 1080, 980, 190, 120);

        //DISPENSER EQUIPMENT
        uc.spawnEquipment(calamansiDispenser, 230, 300, 150, 340);
        uc.spawnEquipment(bukoDispenser, 130, 400, 150, 340);
        uc.spawnEquipment(orangeDispenser, 30, 500, 150, 340);
        uc.spawnTrashCan(100,950);

        //DISPENSER INVISIBLE
        uc.spawnInvisibleEquipment(calamansiDispenser, 230, 300, 150, 340);
        uc.spawnInvisibleEquipment(bukoDispenser, 130, 400, 150, 340);
        uc.spawnInvisibleEquipment(orangeDispenser, 30, 500, 150, 340);

        //CONTAINER
        uc.spawnContainer(mangoBasket, 1650, 720, 230, 230);
        uc.spawnContainer(tempuraContainer, 650, 980, 190, 120);
        uc.spawnContainer(quekquekContainer, 870, 980, 190, 120);
        uc.spawnContainer(hotdogContainer, 1080, 980, 190, 120);
        uc.spawnContainer(cucumberContainer, 1340, 720, 130, 100);
        uc.spawnContainer(gusoContainer, 1390, 820, 140, 110);
        uc.spawnContainer(spicySauce, 1270, 460, 60, 160);
        uc.spawnContainer(sweetSauce, 1330, 550, 60, 160);
        uc.spawnContainer(bagoong, 1500, 650, 54, 100);
        uc.spawnContainer(salt, 1550, 720, 54, 100);
    }
    private void resetGameState() {
        // Reset any scores, timers, or game state variables
        FXGL.getWorldProperties().setValue("score", 0);

        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }


    private void startTimer() {
        // Configure your bar

        // Build a Timeline that fires every 1 second
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Step the bar by +1 each second
            double next = timerBar.getCurrentValue() + 1;
            timerBar.setCurrentValue(next);
        }));

        // After 60 ticks (i.e. bar reaches 60) stop and end game
        timerTimeline.setCycleCount((int) TOTAL_TIME);
        timerTimeline.setOnFinished(e -> endGame());

        // Start ticking
        timerTimeline.play();
    }

    private void endGame() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }

        // Show game over menu
        FXGL.getSceneService().pushSubScene(new GameOverMenu());

        // Optional: Pause the game engine if needed
        FXGL.getGameController().pauseEngine();
    }

    private void setProgressBar() {
        // Timer progress bar
        timerBar = new ProgressBar();
        timerBar.setWidth(800);
        timerBar.setHeight(40);
        // Center horizontally
        timerBar.setTranslateX((FXGL.getAppWidth() - 800) / 2.0);
        timerBar.setTranslateY(10);
        timerBar.setCurrentValue(0);
        timerBar.setMinValue(0);
        timerBar.setMaxValue(TOTAL_TIME);

        // Visual styling
        timerBar.setFill(Color.LIME);
        timerBar.setBackgroundFill(Color.GRAY);
        timerBar.setLabelFill(Color.WHITE);

        // Add to game scene
        FXGL.getGameScene().addUINode(timerBar);
    }

    @Override
    protected void initPhysics() {
        // existing collision handlers...

        // New handler: when an INGREDIENT collides with a CUSTOMER
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(
                CookingInaMain.EntityType.INGREDIENT,
                CookingInaMain.EntityType.CUSTOMER) {

            @Override
            protected void onCollisionBegin(Entity ingredient, Entity customer) {
                // 1) Find the SpeechBubbleComponent
                if (!customer.hasComponent(SpeechBubbleComponent.class)) {
                    return; // no bubble to update
                }
                SpeechBubbleComponent bubble = customer.getComponent(SpeechBubbleComponent.class);
                // 2) Determine which Order this ingredient matches
                //    Assumes your ingredient entity has a UserData or component exposing its name
                String servedName = ingredient.getString("itemName");
                // or: ingredient.getComponent(SomeComponent.class).getStoreItem().getDescription();
                FXGL.getGameScene().addUINode(timerBar);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}