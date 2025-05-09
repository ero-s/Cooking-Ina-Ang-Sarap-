package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.WaitingItem;
import com.example.cookingina.objects.entity.Container;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;
import com.example.cookingina.objects.entity.container.MangoContainer;
import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
import com.example.cookingina.objects.entity.equipment.Fryer;
import com.example.cookingina.objects.entity.equipment.MangoDisplayer;
import com.example.cookingina.objects.entity.storeItem.Calamansi_Juice;
import com.example.cookingina.objects.entity.storeItem.QuekQuek;
import javafx.application.Platform;
import javafx.geometry.Point2D;
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

    private static List<Fryer> fryers;
    private static List<MangoDisplayer> mangoDisplayer;

    public UIController(StoreItem storeItem, Equipment equipment, double originalX, double originalY) {
        this.storeItem = storeItem;
        this.equipment = equipment;
        this.originalX = originalX;
        this.originalY = originalY;
    }

    public static void setFryers(List<Fryer> fryerList) {
        fryers = fryerList;
    }
    public static void setMangoDisplayer(List<MangoDisplayer> mangoDisplayerList) { mangoDisplayer = mangoDisplayerList;
    }

    // Spawing Container and container item to the equipment
    public static void spawnContainerForEquipment(StoreItem storeItem, List<? extends Equipment> equipmentList, double containerX, double containerY, int containerWidth, int containerHeight, int ingredientWidth, int ingredientHeight) {
        entityBuilder()
                .type(CookingInaMain.EntityType.CONTAINER)
                .at(containerX, containerY)
                .viewWithBBox(FXGL.texture(storeItem.getContainer(), containerWidth, containerHeight))
                .with(new CollidableComponent(true))
                .buildAndAttach()
                .getViewComponent()
                .addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (equipmentList == null || equipmentList.isEmpty()) {
                        System.out.println("No equipment initialized!");
                        return;
                    }

                    for (Equipment equipment : equipmentList) {
                        if (equipment.isUnlocked() && !equipment.isOccupied()) {
                            double spawnX = equipment.getLayoutX() + 20; // adjust offset if needed
                            double spawnY = equipment.getLayoutY() + 30;

                            spawnRawIngredient1(storeItem, equipment, spawnX, spawnY, ingredientWidth, ingredientHeight);

                            equipment.setOccupied(true);
                            break;
                        }
                    }
                });
    }

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

    public static void spawnTrashcan(double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.TRASH)
                .at(x, y)
                .viewWithBBox(FXGL.texture("trashcan.png", 120, 140))
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }

    public static void spawnContainer(StoreItem storeItem, Equipment equipment, double x, double y, double width, double height) {
        entityBuilder()
                .type(CookingInaMain.EntityType.CONTAINER)
                .at(x, y)
                .viewWithBBox(FXGL.texture(storeItem.getContainer(), width, height))
                .with(new CollidableComponent(true))
                .buildAndAttach()
                .getViewComponent()
                .addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.EQUIPMENT)
                            .stream()
                            .findFirst()
                            .ifPresent(station -> {
                                int slotIndex = equipment.getNextAvailableSlot();
                                if (slotIndex != -1) {
                                    double spawnX = station.getX() + 12 + (slotIndex * 25);
                                    double spawnY = station.getY() + 40;

                                    var ingredientEntity = entityBuilder()
                                            .type(CookingInaMain.EntityType.INGREDIENT)
                                            .at(spawnX, spawnY)
                                            .viewWithBBox(FXGL.texture(storeItem.getRawResource(), width, height))
                                            .with(new CollidableComponent(true))
                                            .buildAndAttach();

                                    ingredientEntity.addComponent(new CookingComponent(
                                            storeItem.getPreparationTime(),
                                            storeItem,
                                            equipment,
                                            slotIndex
                                    ));

                                    equipment.occupySlot(slotIndex);
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
                .viewWithBBox(FXGL.texture(storeItem.getRawResource(), 40, 40))
                .with(new DraggableComponent())
                .with(new CollidableComponent(true))
                .with(new UIController(storeItem, equipment, x, y))
                .buildAndAttach();
    }

    public static void spawnRawIngredient1(StoreItem storeItem, Equipment equipment, double x, double y, int width, int height) {

                    var ingredientEntity = entityBuilder()
                            .type(CookingInaMain.EntityType.INGREDIENT)
                            .at(x, y)
                            .viewWithBBox(FXGL.texture(storeItem.getRawResource(), width, height))
                            .with(new DraggableComponent())
                            .with(new CollidableComponent(true))
                            .with(new UIController(storeItem, equipment, x, y)) // Your custom UI controller
                            .buildAndAttach();

                    // Add the CookingComponent (cooking time behavior)
                    ingredientEntity.addComponent(new CookingComponent(
                            storeItem.getPreparationTime(),  // preparation time from StoreItem
                            storeItem,                       // the StoreItem object itself
                            equipment,                       // equipment where it's cooked
                            -1                                // slot index (can be -1 if you don't use slots)
                    ));

    }


    public static void spawnCookedIngredient(StoreItem cookedItem, Equipment equipment, double x, double y, double width, double height) {
        if (cookedItem.getDescription().contains("juice")) {
            if (juiceTray.size() < MAX_JUICE_ON_TRAY) {
                Entity juice = spawnJuiceEntity(cookedItem, equipment, x, y, width, height);
                juiceTray.add(juice);
            } else {
                System.out.println("Juice tray full! Adding juice to waiting queue.");
                waitingJuiceQueue.add(new WaitingItem(cookedItem, equipment, x, y, width, height, null));
            }
        } else {
            entityBuilder()
                    .type(CookingInaMain.EntityType.INGREDIENT)
                    .at(x, y)
                    .viewWithBBox(FXGL.texture(cookedItem.getCookedResource(), 40, 40))
                    .with(new DraggableComponent())
                    .with(new CollidableComponent(true))
                    .with(new UIController(cookedItem, equipment, x, y))
                    .buildAndAttach();
        }
    }

    private static boolean isInsideTray(double x, double y) {
        return x >= 480 && x <= 640 && y >= 150 && y <= 260;
    }

    private static Entity spawnJuiceEntity(StoreItem cookedItem, Equipment equipment, double x, double y, double width, double height) {
        Entity juice = FXGL.entityBuilder()
                .at(x, y)
                .view(FXGL.texture(cookedItem.getCookedResource(), (int) width, (int) height))
                .with(new DraggableComponent())
                .buildAndAttach();

        juice.getViewComponent().getChildren().get(0).setOnMouseClicked(e -> {
            juice.removeFromWorld();
            juiceTray.remove(juice);
            checkJuiceQueue();
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
        FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.EQUIPMENT)
                .at(x, y)
                .viewWithBBox(FXGL.texture(equipment.getEmptyResource(), width, height))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        equipment.setLayoutX((int) x);
        equipment.setLayoutY((int) y);
    }

    public static void spawnContainer(Container container, double x, double y, int width, int height) {
        FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.CONTAINER)
                .at(x, y)
                .viewWithBBox(FXGL.texture(container.getRawResource(), width, height))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        container.setLayoutX((int) x);
        container.setLayoutY((int) y);
    }

    public static void spawnContainer1(Container container, double x, double y, int width, int height) {
        // Create the container entity
        var entity = FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.CONTAINER)
                .at(x, y)
                .viewWithBBox(FXGL.texture(container.getRawResource(), width, height))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        // Store the initial position
        final Point2D initialPosition = new Point2D(x, y);

        // Add CookingComponent to the container to manage cooking and dragging behavior
        //CookingComponent cookingComponent = new CookingComponent(30.0, null, null, 0);  // Example values
        //entity.addComponent(cookingComponent);

        // Add draggable component for drag functionality
        entity.addComponent(new DraggableComponent());

        // Set the container's layout position
        container.setLayoutX((int) x);
        container.setLayoutY((int) y);

        // Logic to handle return to initial position when not clicked
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            // Check if the container is clicked on a specific place (i.e., if it's dropped somewhere meaningful)
            boolean isClicked = false;

            // You can add a check here if it's dropped in a valid location or not
            // Example: Check if it's dropped on a plate or a valid target

            if (!isClicked) {
                // Return the entity to its initial position if not clicked
                entity.setPosition(initialPosition);
                System.out.println("Container returned to its original position.");
            }
        });
    }





    public static void spawnInvisibleEquipment(Equipment equipment, double x, double y, int width, int height) {
        Entity invisibleEntity = FXGL.entityBuilder()
                .at(x, y)
                .bbox(new HitBox("CLICK_BOX", BoundingShape.box(width, height)))
                .zIndex(1)
                .with(new CollidableComponent(true))
                .buildAndAttach();

        Texture texture = FXGL.texture(equipment.getEmptyResource());
        texture.setOpacity(0.01);
        invisibleEntity.getViewComponent().addChild(texture);

        texture.setFitWidth(width);
        texture.setFitHeight(height);

        texture.setMouseTransparent(false);
        texture.setOnMouseClicked(e -> {
            System.out.println("CLICK DETECTED on invisible entity!");
            System.out.println("Equipment resource: " + equipment.getEmptyResource());
            BeverageDispenser dispenser = (BeverageDispenser) equipment;

            switch (dispenser.getDescription()) {
                case "calamansi juice":
                    spawnJuiceCup("calamansiJuice_finishedProduct.png", x + 40, y + 220, createJuiceItem("calamansi juice", "calamansiJuice_finishedProduct.png"), dispenser);
                    break;
                case "buko juice":
                    spawnJuiceCup("bukoJuice_finishedProduct.png", x + 40, y + 220, createJuiceItem("buko juice", "bukoJuice_finishedProduct.png"), dispenser);
                    break;
                case "orange juice":
                    spawnJuiceCup("orangeJuice_finishedProduct.png", x + 45, y + 220, createJuiceItem("orange juice", "orangeJuice_finishedProduct.png"), dispenser);
                    break;
            }
        });
    }

    private static Calamansi_Juice createJuiceItem(String description, String resource) {
        return new Calamansi_Juice(
                "none",
                resource,
                resource,
                description,
                15.0, 12.99, 2.0, 1
        );
    }

    public static void spawnJuiceCup(String image, double x, double y, StoreItem juiceItem, Equipment equipment) {
        FXGL.entityBuilder()
                .at(x, y)
                .type(CookingInaMain.EntityType.INGREDIENT)
                .viewWithBBox(FXGL.texture(image, 60, 110))
                .with(new DraggableComponent())
                .with(new CollidableComponent(true))
                .with(new CookingComponent(
                        juiceItem.getPreparationTime(),
                        juiceItem,
                        equipment,
                        1
                ))
                .buildAndAttach();
    }
}
