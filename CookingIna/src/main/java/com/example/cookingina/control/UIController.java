//package com.example.cookingina.control;
//
//import com.almasb.fxgl.dsl.FXGL;
//import com.almasb.fxgl.dsl.components.DraggableComponent;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.entity.component.Component;
//import com.almasb.fxgl.entity.components.CollidableComponent;
//import com.almasb.fxgl.physics.BoundingShape;
//import com.almasb.fxgl.physics.HitBox;
//import com.almasb.fxgl.texture.Texture;
//import com.example.cookingina.CookingInaMain;
//import com.example.cookingina.WaitingItem;
//import com.example.cookingina.objects.entity.Container;
//import com.example.cookingina.objects.entity.Equipment;
//import com.example.cookingina.objects.entity.StoreItem;
//import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
//import com.example.cookingina.objects.entity.equipment.Fryer;
//import com.example.cookingina.objects.entity.storeItem.Calamansi_Juice;
//import customers.CustomerComponent;
//import javafx.application.Platform;
//import javafx.geometry.Point2D;
//import javafx.scene.input.MouseEvent;
//import javafx.util.Duration;
//
//
//import java.util.*;
//import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
//
//public class UIController extends Component {
//
//    public final StoreItem storeItem;
//    public final Equipment equipment;
//    public final double originalX;
//    public final double originalY;
//
//    public DraggableComponent draggable;
//    public boolean wasDragging = false;
//
//    private static List<Fryer> fryers;
//
//    private static final List<CustomerComponent> components = new ArrayList<>();
//    private static final int MAX_CUSTOMERS = 5;
//    private static final Random random = new Random();
//
//    public UIController(StoreItem storeItem, Equipment equipment, double originalX, double originalY) {
//        this.storeItem = storeItem;
//        this.equipment = equipment;
//        this.originalX = originalX;
//        this.originalY = originalY;
//    }
//
//    public static void setFryers(List<Fryer> fryerList) {
//        fryers = fryerList;
//    }
//
//    // Spawing Container and container item to the equipment
//    public static void spawnContainerForEquipment(StoreItem storeItem, List<? extends Equipment> equipmentList, double containerX, double containerY, int containerWidth, int containerHeight, int ingredientWidth, int ingredientHeight) {
//        entityBuilder()
//                .type(CookingInaMain.EntityType.CONTAINER)
//                .at(containerX, containerY)
//                .viewWithBBox(FXGL.texture(storeItem.getContainer(), containerWidth, containerHeight))
//                .with(new CollidableComponent(true))
//                .buildAndAttach()
//                .getViewComponent()
//                .addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//                    if (equipmentList == null || equipmentList.isEmpty()) {
//                        System.out.println("No equipment initialized!");
//                        return;
//                    }
//
//                    for (Equipment equipment : equipmentList) {
//                        if (equipment.isUnlocked() && !equipment.isOccupied()) {
//                            double spawnX = equipment.getLayoutX() + 20; // adjust offset if needed
//                            double spawnY = equipment.getLayoutY() + 30;
//
//                            spawnRawIngredient1(storeItem, equipment, spawnX, spawnY, ingredientWidth, ingredientHeight);
//
//                            equipment.setOccupied(true);
//                            break;
//                        }
//                    }
//                });
//    }
//
//
//    @Override
//    public void onAdded() {
//        draggable = entity.getComponent(DraggableComponent.class);
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
//            String debugMessage = "Drag ended at: " + entity.getX() + ", " + entity.getY();
//            updateDebugText(debugMessage);
//        });
//    }
//
//    private void updateDebugText(String message) {
//        Platform.runLater(() -> CookingInaMain.debugText.setText(message));
//    }
//
//    public static void spawnTrashcan(double x, double y) {
//        entityBuilder()
//                .type(CookingInaMain.EntityType.TRASH)
//                .at(x, y)
//                .viewWithBBox(FXGL.texture("trashcan.png", 120, 140))
//                .with(new CollidableComponent(true))
//                .buildAndAttach();
//    }
//
//    public static void spawnContainer(StoreItem storeItem, Equipment equipment, double x, double y, double width, double height) {
//        entityBuilder()
//                .type(CookingInaMain.EntityType.CONTAINER)
//                .at(x, y)
//                .viewWithBBox(FXGL.texture(storeItem.getContainer(), width, height))
//                .with(new CollidableComponent(true))
//                .buildAndAttach()
//                .getViewComponent()
//                .addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//                    FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.EQUIPMENT)
//                            .stream()
//                            .findFirst()
//                            .ifPresent(station -> {
//                                int slotIndex = equipment.getNextAvailableSlot();
//                                if (slotIndex != -1) {
//                                    double spawnX = station.getX() + 12 + (slotIndex * 25);
//                                    double spawnY = station.getY() + 40;
//
//                                    var ingredientEntity = entityBuilder()
//                                            .type(CookingInaMain.EntityType.INGREDIENT)
//                                            .at(spawnX, spawnY)
//                                            .viewWithBBox(FXGL.texture(storeItem.getRawResource(), width, height))
//                                            .with(new CollidableComponent(true))
//                                            .buildAndAttach();
//
//                                    ingredientEntity.addComponent(new CookingComponent(
//                                            storeItem.getPreparationTime(),
//                                            storeItem,
//                                            equipment,
//                                            slotIndex
//                                    ));
//
//                                    equipment.occupySlot(slotIndex);
//                                } else {
//                                    System.out.println("Station full!");
//                                }
//                            });
//                });
//    }
//
//    public static void spawnRawIngredient(StoreItem storeItem, Equipment equipment, double x, double y) {
//        entityBuilder()
//                .type(CookingInaMain.EntityType.INGREDIENT)
//                .at(x, y)
//                .viewWithBBox(FXGL.texture(storeItem.getRawResource(), 40, 40))
//                .with(new DraggableComponent())
//                .with(new CollidableComponent(true))
//                .with(new UIController(storeItem, equipment, x, y))
//                .buildAndAttach();
//    }
//
//    public static void spawnRawIngredient1(StoreItem storeItem, Equipment equipment, double x, double y, int width, int height) {
//
//        var ingredientEntity = entityBuilder()
//                .type(CookingInaMain.EntityType.INGREDIENT)
//                .at(x, y)
//                .zIndex(2)
//                .viewWithBBox(FXGL.texture(storeItem.getRawResource(), width, height))
//                .with(new DraggableComponent())
//                .with(new CollidableComponent(true))
//                .with(new UIController(storeItem, equipment, x, y)) // Your custom UI controller
//                .buildAndAttach();
//
//        // Add the CookingComponent (cooking time behavior)
//        ingredientEntity.addComponent(new CookingComponent(
//                storeItem.getPreparationTime(),  // preparation time from StoreItem
//                storeItem,                       // the StoreItem object itself
//                equipment,                       // equipment where it's cooked
//                -1                                // slot index (can be -1 if you don't use slots)
//        ));
//
//    }
//
//
//    public static void spawnCookedIngredient(StoreItem cookedItem, Equipment equipment, double x, double y, double width, double height) {
//
//            entityBuilder()
//                    .type(CookingInaMain.EntityType.INGREDIENT)
//                    .at(x, y)
//                    .zIndex(2)
//                    .viewWithBBox(FXGL.texture(cookedItem.getCookedResource(), 40, 40))
//                    .with(new DraggableComponent())
//                    .with(new CollidableComponent(true))
//                    .with(new UIController(cookedItem, equipment, x, y))
//                    .buildAndAttach();
//    }
//
//    private static boolean isInsideTray(double x, double y) {
//        return x >= 480 && x <= 640 && y >= 150 && y <= 260;
//    }
//
//    public static void spawnEquipment(Equipment equipment, double x, double y, int width, int height) {
//        FXGL.entityBuilder()
//                .type(CookingInaMain.EntityType.EQUIPMENT)
//                .at(x, y)
//                .zIndex(1)
//                .viewWithBBox(FXGL.texture(equipment.getEmptyResource(), width, height))
//                .with(new CollidableComponent(true))
//                .buildAndAttach();
//
//        equipment.setLayoutX((int) x);
//        equipment.setLayoutY((int) y);
//    }
//
//    public static void spawnContainer(Container container, double x, double y, int width, int height) {
//        FXGL.entityBuilder()
//                .type(CookingInaMain.EntityType.CONTAINER)
//                .at(x, y)
//                .zIndex(1)
//                .viewWithBBox(FXGL.texture(container.getRawResource(), width, height))
//                .with(new CollidableComponent(true))
//                .buildAndAttach();
//
//        container.setLayoutX((int) x);
//        container.setLayoutY((int) y);
//    }
//
//    public static void spawnContainer1(Container container, double x, double y, int width, int height) {
//        // Create the container entity
//        var entity = FXGL.entityBuilder()
//                .type(CookingInaMain.EntityType.CONTAINER)
//                .at(x, y)
//                .zIndex(1)
//                .viewWithBBox(FXGL.texture(container.getRawResource(), width, height))
//                .with(new CollidableComponent(true))
//                .buildAndAttach();
//
//        // Store the initial position
//        final Point2D initialPosition = new Point2D(x, y);
//
//        // Add CookingComponent to the container to manage cooking and dragging behavior
//        CookingComponent cookingComponent = new CookingComponent(30.0, null, null, 0);  // Example values
//        entity.addComponent(cookingComponent);
//
//        // Add draggable component for drag functionality
//        entity.addComponent(new DraggableComponent());
//
//        // Set the container's layout position
//        container.setLayoutX((int) x);
//        container.setLayoutY((int) y);
//
//        // Logic to handle return to initial position when not clicked
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
//            // Check if the container is clicked on a specific place (i.e., if it's dropped somewhere meaningful)
//            boolean isClicked = false;
//
//            // You can add a check here if it's dropped in a valid location or not
//            // Example: Check if it's dropped on a plate or a valid target
//
//            if (!isClicked) {
//                // Return the entity to its initial position if not clicked
//                entity.setPosition(initialPosition);
//                System.out.println("Container returned to its original position.");
//            }
//        });
//    }
//
//
//
//
//
//    public static void spawnInvisibleEquipment(Equipment equipment, double x, double y, int width, int height) {
//        Entity invisibleEntity = FXGL.entityBuilder()
//                .at(x, y)
//                .bbox(new HitBox("CLICK_BOX", BoundingShape.box(width, height)))
//                .zIndex(1)
//                .with(new CollidableComponent(true))
//                .buildAndAttach();
//
//        Texture texture = FXGL.texture(equipment.getEmptyResource());
//        texture.setOpacity(0.01);
//        invisibleEntity.getViewComponent().addChild(texture);
//
//        texture.setFitWidth(width);
//        texture.setFitHeight(height);
//
//        texture.setMouseTransparent(false);
//        texture.setOnMouseClicked(e -> {
//            System.out.println("CLICK DETECTED on invisible entity!");
//            System.out.println("Equipment resource: " + equipment.getEmptyResource());
//            BeverageDispenser dispenser = (BeverageDispenser) equipment;
//
//            switch (dispenser.getDescription()) {
//                case "calamansi juice":
//                    spawnJuiceCup("calamansiJuice_finishedProduct.png", x + 40, y + 220, createJuiceItem("calamansi juice", "calamansiJuice_finishedProduct.png"), dispenser);
//                    break;
//                case "buko juice":
//                    spawnJuiceCup("bukoJuice_finishedProduct.png", x + 40, y + 220, createJuiceItem("buko juice", "bukoJuice_finishedProduct.png"), dispenser);
//                    break;
//                case "orange juice":
//                    spawnJuiceCup("orangeJuice_finishedProduct.png", x + 45, y + 220, createJuiceItem("orange juice", "orangeJuice_finishedProduct.png"), dispenser);
//                    break;
//            }
//        });
//    }
//
//    private static Calamansi_Juice createJuiceItem(String description, String resource) {
//        return new Calamansi_Juice(
//                "none",
//                resource,
//                resource,
//                description,
//                15.0, 12.99, 2.0, 1
//        );
//    }
//
//    public static void spawnJuiceCup(String image, double x, double y, StoreItem juiceItem, Equipment equipment) {
//        FXGL.entityBuilder()
//                .at(x, y)
//                .type(CookingInaMain.EntityType.INGREDIENT)
//                .viewWithBBox(FXGL.texture(image, 60, 110))
//                .with(new DraggableComponent())
//                .with(new CollidableComponent(true))
//                .with(new CookingComponent(
//                        juiceItem.getPreparationTime(),
//                        juiceItem,
//                        equipment,
//                        1
//                ))
//                .buildAndAttach();
//    }
//
//    /**
//     * Spawns a single customer off-screen, moving to a non-overlapping X.
//     */
//    public static void spawnCustomerAtRandomIntervals() {
//        // Kick off the first scheduling
//        scheduleNextSpawn();
//    }
//
//    private static void scheduleNextSpawn() {
//        if (components.size() >= MAX_CUSTOMERS) {
//            System.out.println("At cap; pausing spawns.");
//            return;
//        }
//
//        double delay = 1 + random.nextDouble() * 2;
//        FXGL.runOnce(() -> {
//            boolean spawned = spawnCustomer("customer_image.png");
//
//            if (spawned && components.size() < MAX_CUSTOMERS) {
//                // Schedule next only if we’re still under the limit
//                scheduleNextSpawn();
//            }
//        }, Duration.seconds(delay));
//    }
//
//    public static boolean spawnCustomer(String imageName) {
//        int w = 180, h = 200;
//        double y = 320;
//        int sceneW = FXGL.getAppWidth();
//
//        if (components.size() >= MAX_CUSTOMERS) {
//            System.out.println("MAX reached (" + MAX_CUSTOMERS + "); skipping spawn.");
//            return false;
//        }
//
//        // Try to find a non-overlapping targetX
//        double targetX;
//        int maxTries = 10;
//        int attempts = 0;
//
//        do {
//            targetX = random.nextInt(sceneW - w);
//            attempts++;
//        } while (checkOverlap(targetX, w) && attempts < maxTries);
//
//        if (checkOverlap(targetX, w)) {
//            System.out.println("Could not find non-overlapping position after " + maxTries + " tries.");
//            return false;
//        }
//
//        boolean goRight = random.nextBoolean();
//        double startX = goRight ? -w : sceneW + w;
//        String dir = goRight ? "RIGHT" : "LEFT";
//
//        Texture tex = FXGL.texture(imageName, w, h);
//
//        var ent = FXGL.entityBuilder()
//                .type(CookingInaMain.EntityType.CUSTOMER)
//                .at(startX, y)
//                .viewWithBBox(tex)
//                .zIndex(0)
//                .with(new CustomerComponent(targetX, y, dir))
//                .buildAndAttach();
//
//        CustomerComponent cc = ent.getComponent(CustomerComponent.class);
//        components.add(cc);
//
//        System.out.println("Spawned customer → targetX=" + targetX +
//                "  Active: " + components.size());
//        return true;
//    }
//
//    private static boolean checkOverlap(double x, int w) {
//        return components.stream()
//                .anyMatch(cc -> Math.abs(cc.getTargetX() - x) < w);
//    }
//}




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
import com.example.cookingina.objects.entity.*;
import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
import com.example.cookingina.objects.entity.equipment.Fryer;
import com.example.cookingina.objects.entity.storeItem.Calamansi_Juice;
import com.example.cookingina.objects.entity.storeItem.QuekQuek;
import customers.CustomerComponent;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

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

    private static final List<CustomerComponent> components = new ArrayList<>();
    private static final int MAX_CUSTOMERS = 5;
    private static final Random random = new Random();

    private static List<Fryer> fryers;
    private static List<Fryer> paperTrays;

    public UIController(StoreItem storeItem, Equipment equipment, double originalX, double originalY) {
        this.storeItem = storeItem;
        this.equipment = equipment;
        this.originalX = originalX;
        this.originalY = originalY;
    }

    public static void setFryers(List<Fryer> fryerList) {
        fryers = fryerList;
    }

    // Spawing Container and container item to the equipment
    public static void spawnContainerForEquipment(StoreItem storeItem, List<? extends Equipment> equipmentList, double containerX, double containerY, int containerWidth, int containerHeight) {
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

                            spawnRawIngredient1(storeItem, equipment, spawnX, spawnY);

                            equipment.setOccupied(true);
                            break;
                        }
                    }
                });
    }

    private void updateDebugText(String message) {
        Platform.runLater(() -> CookingInaMain.debugText.setText(message));
    }

    public static void spawnTrashCan(double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.TRASH)
                .at(x, y)
                .viewWithBBox(FXGL.texture("trash_open.png", 200, 300))
                .with(new CollidableComponent(true))
                .buildAndAttach();
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

    public static void spawnRawIngredient1(StoreItem storeItem, Equipment equipment, double x, double y) {
        var ingredientEntity = entityBuilder()
                .type(CookingInaMain.EntityType.INGREDIENT)
                .at(x, y)
                .viewWithBBox(FXGL.texture(storeItem.getRawResource(), storeItem.getWidth(), storeItem.getHeight()))
                .with(new UIController(storeItem, equipment, x, y)) // Your custom UI controller
                .buildAndAttach();
        // Add the CookingComponent (cooking time behavior)
        ingredientEntity.addComponent(new CookingComponent(
                storeItem.getPreparationTime(),  // preparation time from StoreItem
                storeItem,                       // the StoreItem object itself
                equipment                        // equipment where it's cooked
        ));

        setHighlight(ingredientEntity, x, y);

    }


    public static void spawnCookedIngredient(StoreItem cookedItem, Equipment equipment, double x, double y) {
        Entity entity;
        if (cookedItem.getDescription().contains("juice")) {
            if (juiceTray.size() < MAX_JUICE_ON_TRAY) {
                entity = spawnJuiceEntity(cookedItem, equipment, x, y);
                juiceTray.add(entity);
            } else {
                entity = null;
                System.out.println("Juice tray full! Adding juice to waiting queue.");
                waitingJuiceQueue.add(new WaitingItem(cookedItem, equipment, x, y, cookedItem.getWidth(), cookedItem.getHeight(), null));
            }
        } else {
            entity = entityBuilder()
                    .type(CookingInaMain.EntityType.INGREDIENT)
                    .at(x, y)
                    .viewWithBBox(FXGL.texture(cookedItem.getCookedResource(), cookedItem.getWidth(), cookedItem.getHeight()))
                    .with(new DraggableComponent())
                    .with(new CollidableComponent(true))
                    .with(new UIController(cookedItem, equipment, x, y))
                    .with(new OvercookComponent(cookedItem, equipment))
                    .buildAndAttach();
        }
        assert entity != null;
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            entity.setPosition(x, y);
        });
        setHighlight(entity, x, y);

    }

    private static boolean isInsideTray(double x, double y) {
        return x >= 480 && x <= 640 && y >= 150 && y <= 260;
    }

    private static Entity spawnJuiceEntity(StoreItem cookedItem,
                                           Equipment equipment,
                                           double x, double y) {
        // Build at (x,y)…
        Entity juice = FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.INGREDIENT)
                .at(x, y)
                .zIndex(100)
                .viewWithBBox(
                        FXGL.texture(
                                cookedItem.getCookedResource(),
                                cookedItem.getWidth(),
                                cookedItem.getHeight()
                        )
                )
                .with(new DraggableComponent())
                .with(new CollidableComponent(true))
                .buildAndAttach();


        juice.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            double homeX = juice.getX();
            double homeY = juice.getY();

            // Check collision with any trash entity
            boolean onTrash = FXGL.getGameWorld()
                    .getEntitiesByType(CookingInaMain.EntityType.TRASH)
                    .stream()
                    .anyMatch(trash ->
                            trash.getBoundingBoxComponent()
                                    .isCollidingWith(juice.getBoundingBoxComponent())
                    );

            if (onTrash) {
                // Remove from world & tray
                juice.removeFromWorld();
                juiceTray.remove(juice);
                checkJuiceQueue();
            } else {
                // Snap back home
                juice.setPosition(homeX, homeY);
            }

            e.consume();
        });

        return juice;
    }


    private static void checkJuiceQueue() {
        if (juiceTray.size() < MAX_JUICE_ON_TRAY && !waitingJuiceQueue.isEmpty()) {
            WaitingItem next = waitingJuiceQueue.poll();
            Entity juice = spawnJuiceEntity(next.item, next.equipment, next.x, next.y);
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

    public static void spawnPaperTray(PaperTray paperTray, double x, double y, int width, int height) {
        Entity entity = FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.PLATE)
                .at(x, y)
                .viewWithBBox(FXGL.texture(paperTray.getEmptyResource(), width, height))
                .with(new PaperTrayComponent(paperTray))
                .zIndex(0)
                .buildAndAttach();
        setHighlight(entity, x, y);

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

    public static void spawnInvisibleEquipment(Equipment equipment, double x, double y, int width, int height) {
        Entity invisibleEntity = FXGL.entityBuilder()
                .at(x, y)
                .type(CookingInaMain.EntityType.CONTAINER)
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
            if(equipment.isOccupied()) return;
            equipment.setOccupied(true);
            System.out.println("CLICK DETECTED on invisible entity!");
            System.out.println("Equipment resource: " + equipment.getEmptyResource());
            BeverageDispenser dispenser = (BeverageDispenser) equipment;

            switch (dispenser.getDescription()) {
                case "calamansi juice":
                    spawnJuiceCup(x + 40, y + 220, createJuiceItem("calamansi juice", "calamansiJuice_finishedProduct.png"), dispenser);
                    break;
                case "buko juice":
                    spawnJuiceCup(x + 40, y + 220, createJuiceItem("buko juice", "bukoJuice_finishedProduct.png"), dispenser);
                    break;
                case "orange juice":
                    spawnJuiceCup(x + 45, y + 220, createJuiceItem("orange juice", "orangeJuice_finishedProduct.png"), dispenser);
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
                15.0, 12.99, 2.0, 1, 110,60
        );
    }

    public static void spawnJuiceCup(double x, double y, StoreItem juiceItem, Equipment equipment) {
        Entity juice = FXGL.entityBuilder()
                .at(x, y)
                .type(CookingInaMain.EntityType.INGREDIENT)
                .viewWithBBox(FXGL.texture(juiceItem.getCookedResource(),juiceItem.getWidth(), juiceItem.getHeight()))
                .with(new CollidableComponent(true))
                .with(new CookingComponent(
                        juiceItem.getPreparationTime(),
                        juiceItem,
                        equipment
                ))
                .zIndex(100)
                .buildAndAttach();

        setHighlight(juice, x, y);
    }

    private static void setHighlight(Entity entity, double x, double y) {
        Node viewNode = entity.getViewComponent().getParent();

// set up the glow
        DropShadow glow = new DropShadow(20, Color.YELLOW);
        viewNode.setPickOnBounds(true);
        viewNode.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> viewNode.setEffect(glow));
        viewNode.addEventHandler(MouseEvent.MOUSE_EXITED,  e -> viewNode.setEffect(null));

// snap-back on release
        viewNode.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> entity.setPosition(x,y));
    }


    public static void spawnCustomerAtRandomIntervals() {
        // Kick off the first scheduling
        scheduleNextSpawn();
    }

    private static void scheduleNextSpawn() {
        if (components.size() >= MAX_CUSTOMERS) {
            System.out.println("At cap; pausing spawns.");
            return;
        }

        double delay = 1 + random.nextDouble() * 2;
        FXGL.runOnce(() -> {
            boolean spawned = spawnCustomer("customer_image.png");

            if (spawned && components.size() < MAX_CUSTOMERS) {
                // Schedule next only if we’re still under the limit
                scheduleNextSpawn();
            }
        }, Duration.seconds(delay));
    }

    public static boolean spawnCustomer(String imageName) {
        int w = 180, h = 200;
        double y = 320;
        int sceneW = FXGL.getAppWidth();

        if (components.size() >= MAX_CUSTOMERS) {
            System.out.println("MAX reached (" + MAX_CUSTOMERS + "); skipping spawn.");
            return false;
        }

        // Try to find a non-overlapping targetX
        double targetX;
        int maxTries = 10;
        int attempts = 0;

        do {
            targetX = random.nextInt(sceneW - w);
            attempts++;
        } while (checkOverlap(targetX, w) && attempts < maxTries);

        if (checkOverlap(targetX, w)) {
            System.out.println("Could not find non-overlapping position after " + maxTries + " tries.");
            return false;
        }

        boolean goRight = random.nextBoolean();
        double startX = goRight ? -w : sceneW + w;
        String dir = goRight ? "RIGHT" : "LEFT";

        Texture tex = FXGL.texture(imageName, w, h);

        var ent = FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.CUSTOMER)
                .at(startX, y)
                .viewWithBBox(tex)
                .zIndex(0)
                .with(new CustomerComponent(targetX, y, dir))
                .buildAndAttach();

        CustomerComponent cc = ent.getComponent(CustomerComponent.class);
        components.add(cc);

        System.out.println("Spawned customer → targetX=" + targetX +
                "  Active: " + components.size());
        return true;
    }

    private static boolean checkOverlap(double x, int w) {
        return components.stream()
                .anyMatch(cc -> Math.abs(cc.getTargetX() - x) < w);
    }
}