package com.example.cookingina;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import com.example.cookingina.control.UIController;

import com.example.cookingina.menu.GameOverMenu;
import com.example.cookingina.menu.MainMenu;
import com.example.cookingina.objects.entity.*;
import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
import com.example.cookingina.objects.entity.equipment.Fryer;
import com.example.cookingina.objects.entity.equipment.MangoTray;
import com.example.cookingina.objects.entity.equipment.TrashBin;
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
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppWidth;
import static com.example.cookingina.objects.entity.ContainerTypeFactory.TYPE.*;
import static com.example.cookingina.objects.entity.ContainerTypeFactory.TYPE.SALT;

public class CookingInaMain extends GameApplication {

    private final List<Fryer> fryers = new ArrayList<>();
    private final List<PaperTray> paperTrays = new ArrayList<>();
    private final List<MangoTray> mangoTrays = new ArrayList<>();
    private boolean isGameActive = false;
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
    private static final double TOTAL_TIME = 10.0; // seconds

    @Override
    public void initUI() {
        // Create debug text element
        debugText = new Text();
        debugText.setFont(Font.font(14));
        debugText.setFill(Color.WHITE);
        debugText.setTranslateX(10);
        debugText.setTranslateY(20);
        setProgressBar();

        // Add to game scene
        FXGL.getGameScene().addUINode(debugText);

        // --- FXGL income bar ---
        ProgressBar incomeBar = new ProgressBar();
        incomeBar.setWidth(200);
        incomeBar.setHeight(20);
        incomeBar.setTranslateX(getAppWidth() - 220); // 20px from right edge
        incomeBar.setTranslateY(20);                 // 20px from top

        incomeBar.setMinValue(0);
        incomeBar.setMaxValue(100);

        // Bind world property 'income' -> bar value
        FXGL.getWorldProperties().intProperty("income")
                .addListener((obs, oldVal, newVal) -> {
                    incomeBar.setCurrentValue(newVal.doubleValue());
                });

        FXGL.getGameScene().addUINode(incomeBar);
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
               "frying_pan.png",
               "usedPan.png",
               i,
                    0,
                    1.5,
                    500.0,
                    1,
                    false,
                    "A standard fryer for basic cooking needs"
            ));
        }

        for(int i = 1; i <= 3; i++){
            mangoTrays.add(new MangoTray(
                    "assets.png",
                    "usedPan.png",
                    i,
                    0,
                    2.5,
                    0.0,
                    1,
                    false,
                    "A tray for mango"
            ));
        }

        for(int i = 0; i < 6; i++){
            paperTrays.add(new PaperTray(
                    "frying_pan.png",                         // name
                    "papertray.png",
                    180, 130
            ));
        }

        uc.setFryers(fryers);
// ================= SELLING ITEMS ENTITTY =================
        uc.spawnCustomerAtRandomIntervals();
// ================= CONTAINER ENTITY =================
        ContainerType orangeDispenser = ContainerTypeFactory.create(ORANGE_JUICE);
        ContainerType calamansiDispenser = ContainerTypeFactory.create(CALAMANSI_JUICE);
        ContainerType bukoDispenser = ContainerTypeFactory.create(BUKO_JUICE);
        ContainerType mangoBasket = ContainerTypeFactory.create(MANGO);
        ContainerType hotdogFood = ContainerTypeFactory.create(HOTDOG);
        ContainerType quekquekFood = ContainerTypeFactory.create(QUEKQUEK);
        ContainerType tempuraFood = ContainerTypeFactory.create(TEMPURA);
        ContainerType cucumberFood = ContainerTypeFactory.create(CUCUMBER);
        ContainerType gusoFood = ContainerTypeFactory.create(GUSO);
        ContainerType spicySauce = ContainerTypeFactory.create(SPICY_SAUCE);
        ContainerType sweetSauce = ContainerTypeFactory.create(SWEET_SAUCE);
        ContainerType bagoong = ContainerTypeFactory.create(BAGOONG);
        ContainerType salt = ContainerTypeFactory.create(SALT);

        // Add trashCan asset
        TrashBin trashBin = new TrashBin(
                "trash_closed.png",                         // name
                "trash_open.png",
                1,
                0,
                0,
                0,
                false,
                "orange juice"
        );

        // MANGO TRAY POSITIONS
        int[][] mangoTrayPositions = {
                {1540, 430}, {1640, 500}, {1740, 570}, // Position of 1, 2, and 3.
        };

        for (int i = 0; i < mangoTrays.size(); i++) {
            MangoTray tray = mangoTrays.get(i);
            int[] pos = mangoTrayPositions[i];
            uc.spawnEquipment(tray, pos[0], pos[1], 150, 130);
        }

        // FRYER POSITIONS
        int[][] fryerPositions = {
                {1035, 505}, {895, 505}, {755, 505}, // Position of 1, 2, and 3.
                {1045, 605}, {895, 605}, {745, 605} // Position of 4, 5, and 6.
        };

        for (int i = 0; i < fryers.size(); i++) {
            Fryer fryer = fryers.get(i);
            int[] pos = fryerPositions[i];
            uc.spawnEquipment(fryer, pos[0], pos[1], 190, 140);
        }

        // PAPER TRAY POSITIONS
        int[][] trayPositions = {
                {1090, 750, 180, 130},
                {895, 750, 180, 130},
                {695, 750, 180, 130},
                {1090, 840, 175, 130},
                {895, 840, 175, 130},
                {695, 840, 175, 130}
        };

        for (int i = 0; i < paperTrays.size(); i++) {
            PaperTray tray = paperTrays.get(i);
            int[] pos = trayPositions[i];
            uc.spawnPaperTray(tray, pos[0], pos[1], pos[2], pos[3]);
        }

        uc.spawnContainerForEquipment((Food) quekquekFood, fryers, 870, 980, 190, 120);
        uc.spawnContainerForEquipment((Food) hotdogFood, fryers, 1080, 980, 190, 120);
        uc.spawnContainerForEquipment((Food) tempuraFood, fryers, 650, 980, 190, 120);
        uc.spawnContainerForEquipment((Food) mangoBasket, mangoTrays, 1630, 720, 270, 250);

        //DISPENSER EQUIPMENT
        uc.spawnEquipment((Equipment) calamansiDispenser, 230, 300, 150, 340);
        uc.spawnEquipment((Equipment) bukoDispenser, 130, 400, 150, 340);
        uc.spawnEquipment((Equipment) orangeDispenser, 30, 500, 150, 340);
        uc.spawnTrashCan(100,950);

        //DISPENSER INVISIBLE
        uc.spawnInvisibleEquipment((BeverageDispenser) calamansiDispenser, 230, 300, 150, 340);
        uc.spawnInvisibleEquipment((BeverageDispenser) bukoDispenser, 130, 400, 150, 340);
        uc.spawnInvisibleEquipment((BeverageDispenser) orangeDispenser, 30, 500, 150, 340);

        //CONTAINER
        uc.spawnContainer((Food) cucumberFood, 1340, 720, 130, 100);
        uc.spawnContainer((Food) gusoFood, 1390, 820, 140, 110);
        uc.spawnContainer((Food) spicySauce, 1270, 460, 60, 160);
        uc.spawnContainer((Food) sweetSauce, 1330, 550, 60, 160);
        uc.spawnContainer((Food) bagoong, 1500, 650, 54, 100);
        uc.spawnContainer((Food) salt, 1550, 720, 54, 100);
    }
    private void resetGameState() {
        // Clear all entities
        FXGL.getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);

        // Reset game world properties
        FXGL.getWorldProperties().setValue("score", 0);
        FXGL.getWorldProperties().setValue("income", 0);

        // Clear equipment lists
        fryers.clear();
        paperTrays.clear();
        mangoTrays.clear();

        // Reset UI controller
        uc = new UIController();
        // Flag game state
        isGameActive = false;
    }


    private void startTimer() {
        isGameActive = true;

        FXGL.getGameTimer().runAtInterval(() -> {
            if (isGameActive) {
                double next = timerBar.getCurrentValue() + 1;
                timerBar.setCurrentValue(next);

                if (next >= TOTAL_TIME) {
                    endGame();
                }
            }
        }, Duration.seconds(1));
    }

    private void endGame() {
        isGameActive = false;

        // Show game over menu
        FXGL.getSceneService().pushSubScene(new GameOverMenu());

        // Full reset
        resetGameState();

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