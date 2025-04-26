package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.WaitingItem;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;
import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
import com.example.cookingina.objects.entity.storeItem.Calamansi_Juice;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class UIController extends Component {
    public final StoreItem storeItem;
    public final Equipment equipment;
    public final double originalX;
    public final double originalY;
    public DraggableComponent draggable;
    public boolean wasDragging = false;

    private static final int MAX_JUICE_ON_TRAY = 3;
    private static final List<Entity> juiceTray = new ArrayList<>();
    private static final Queue<WaitingItem> waitingJuiceQueue = new LinkedList<>();




    public UIController(StoreItem storeItem, Equipment equipment, double originalX, double originalY) {
        this.storeItem = storeItem;
        this.equipment = equipment;
        this.originalX = originalX;
        this.originalY = originalY;
    }


    // Method takes care of entity collision upon dragging and mouse release
    @Override
    public void onAdded() {
        draggable = entity.getComponent(DraggableComponent.class);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            String debugMessage = "Drag ended at: " + entity.getX() + ", " + entity.getY();
            updateDebugText(debugMessage);
        });
    }

    private void updateDebugText(String message) {
        Platform.runLater(() -> CookingInaMain.debugText.setText(message));
    }

    public static void spawnTrashcan(double x, double y){
        entityBuilder()
                .type(CookingInaMain.EntityType.TRASH)
                .at(x, y)
                .viewWithBBox( FXGL.texture("trashcan.png", 120,140))
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }

    public static void spawnContainer(StoreItem storeItem, Equipment equipment, double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.CONTAINER)
                .at(x, y)
                .viewWithBBox(FXGL.texture(storeItem.getContainer(), 85, 75))
                .with(new CollidableComponent(true))
                .buildAndAttach()
                .getViewComponent()
                .addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.EQUIPMENT).stream()
                            .findFirst()
                            .ifPresent(station -> {
                                int slotIndex = equipment.getNextAvailableSlot();
                                if (slotIndex != -1) {
                                    double spawnX = station.getX() + 12 + (slotIndex * 25);
                                    double spawnY = station.getY() + 40;

                                    // Spawn the raw ingredient
                                    var ingredientEntity = entityBuilder()
                                            .type(CookingInaMain.EntityType.INGREDIENT)
                                            .at(spawnX, spawnY)
                                            .viewWithBBox(FXGL.texture(storeItem.getRawResource(), 40, 40))
                                            .with(new CollidableComponent(true))
                                            .buildAndAttach();

                                    // Add cooking logic directly
                                    ingredientEntity.addComponent(new CookingComponent(
                                            storeItem.getPreparationTime(),
                                            storeItem,
                                            equipment,
                                            slotIndex // <--- Important: track which slot is taken
                                    ));

                                    equipment.occupySlot(slotIndex); // Mark slot as occupied
                                } else {
                                    System.out.println("Station full!");
                                }
                            });
                });
    }


    public static void spawnRawIngredient(StoreItem storeItem, Equipment equipment, double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.INGREDIENT)
                .at(x, y)
                .viewWithBBox( FXGL.texture(storeItem.getRawResource(), 40,40))
                .with(new DraggableComponent())
                .with(new CollidableComponent(true))
                .with(new UIController(storeItem, equipment,x, y))
                .buildAndAttach();
    }

    public static void spawnCookedIngredient(StoreItem cookedItem, Equipment equipment, double x, double y, double width, double height) {
        if (cookedItem.getDescription().contains("juice")) {
            if (juiceTray.size() < MAX_JUICE_ON_TRAY) {
                Entity juice = spawnJuiceEntity(cookedItem, equipment, x, y, width, height);
                juiceTray.add(juice);
            } else {
                System.out.println("Juice tray full! Adding juice to waiting queue.");
                waitingJuiceQueue.add(new WaitingItem(cookedItem, equipment, x, y, width, height, null /* or replace null properly */));
            }
        } else {
            entityBuilder()
                    .type(CookingInaMain.EntityType.INGREDIENT)
                    .at(x, y)
                    .viewWithBBox( FXGL.texture(cookedItem.getCookedResource(), 40,40))
                    .with(new DraggableComponent())
                    .with(new CollidableComponent(true))
                    .with(new UIController(cookedItem, equipment,x, y))
                    .buildAndAttach();
        }

    }

    private static boolean isInsideTray(double x, double y) {
        // Adjust these bounds to exactly match your tray’s rectangle
        return x >= 480 && x <= 640
                && y >= 150 && y <= 260;
    }

    private static Entity spawnJuiceEntity(StoreItem cookedItem, Equipment equipment, double x, double y, double width, double height) {
        Entity juice = FXGL.entityBuilder()
                .at(x, y)
                .view(FXGL.texture(cookedItem.getCookedResource(), (int) width, (int) height))
                .with(new DraggableComponent()) // optional
                .buildAndAttach();

        // Add click to remove from tray
        juice.getViewComponent().getChildren().get(0).setOnMouseClicked(e -> {
            juice.removeFromWorld();
            juiceTray.remove(juice);
            checkJuiceQueue(); // Check and spawn next in queue
        });

        return juice;
    }

    private static void checkJuiceQueue() {
        if (juiceTray.size() < MAX_JUICE_ON_TRAY && !waitingJuiceQueue.isEmpty()) {
            WaitingItem next = waitingJuiceQueue.poll();
            Entity juice = spawnJuiceEntity(next.item, next.equipment, next.x, next.y, next.width, next.height);
            juiceTray.add(juice);
        }
    }

    public static void spawnEquipment(Equipment equipment, double x, double y, int width, int height) {
        Entity stationEntity = FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.EQUIPMENT)
                .at(x, y)
                .viewWithBBox(FXGL.texture(equipment.getEmptyResource(), width, height))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        equipment.setLayoutX((int) x);
        equipment.setLayoutY((int) y);

        // Click listener on Juice Dispenser
        stationEntity.getViewComponent().addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            if (equipment instanceof BeverageDispenser) {
                BeverageDispenser dispenser = (BeverageDispenser) equipment;

                switch (dispenser.getDescription()) {
                    case "calamansi juice":
                        Calamansi_Juice calamansiJuice = new Calamansi_Juice(
                                "none",
                                "dragonfruit_juice_fillingup.png",
                                "dragonfruit_juice_done.png",
                                "calamansi juice",
                                15.0, 12.99, 2.0, 1);
                        spawnJuiceCup("dragonfruit_juice_fillingup.png", x + 25, y + 185, calamansiJuice, dispenser);
                        break;

                    case "buko juice":
                        Calamansi_Juice bukoJuice = new Calamansi_Juice(
                                "none",
                                "mangojuice_fillingup.png",
                                "mangojuice_done.png",
                                "buko juice",
                                15.0, 12.99, 2.0, 1);
                        spawnJuiceCup("mangojuice_fillingup.png", x + 25, y + 185, bukoJuice, dispenser);
                        break;

                    case "orange juice":
                        Calamansi_Juice orangeJuice = new Calamansi_Juice(
                                "none",
                                "nestea_juice_fillingup.png",
                                "nestea_juice_done.png",
                                "orange juice",
                                15.0, 12.99, 2.0, 1);
                        spawnJuiceCup("nestea_juice_fillingup.png", x + 25, y + 185, orangeJuice, dispenser);
                        break;
                }
            }
        });
    }

    public static void spawnJuiceCup(String image, double x, double y, StoreItem juiceItem, Equipment equipment) {
        FXGL.entityBuilder()
                .at(x, y)
                .type(CookingInaMain.EntityType.INGREDIENT)
                .viewWithBBox(FXGL.texture(image, 50, 70))
                .with(new CookingComponent(
                        juiceItem.getPreparationTime(),
                        juiceItem,
                        equipment,
                        1
                ))
                .buildAndAttach();
    }

}
