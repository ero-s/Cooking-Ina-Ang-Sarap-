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
import com.example.cookingina.objects.entity.*;
import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
import com.example.cookingina.objects.entity.equipment.Fryer;
import com.example.cookingina.objects.entity.equipment.MangoTray;
import customers.CustomerComponent;
import customers.Order;
import customers.SpeechBubbleComponent;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class UIController extends Component {

    public DraggableComponent draggable;
    public boolean wasDragging = false;

    private static final int MAX_JUICE_ON_TRAY = 3;
    private static final List<Entity> juiceTray = new ArrayList<>();

    private final List<CustomerComponent> components = new ArrayList<>();
    private static final int MAX_CUSTOMERS = 5;
    private static final Random random = new Random();
    private static int mangoCount = 0;
    private static final int MAX_MANGO = 3;

    private static List<Fryer> fryers;
    private static List<MangoTray>  mangoTray;

    public void setFryers(List<Fryer> fryerList) {
        fryers = fryerList;
    }

    public static void setMangoTray(List<MangoTray> mangoTrayList) {
        mangoTray = mangoTrayList;
    }

    // Spawing Container and container item to the equipment
    public void spawnContainerForEquipment(Food storeItem, List<? extends Equipment> equipmentList, double containerX, double containerY, int containerWidth, int containerHeight) {
        entityBuilder()
                .type(CookingInaMain.EntityType.CONTAINER)
                .at(containerX, containerY)
                .viewWithBBox(FXGL.texture(storeItem.getContainerResource(), containerWidth, containerHeight))
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

                            spawnRawIngredient1(storeItem.getItem(), equipment, spawnX, spawnY);

                            equipment.setOccupied(true);
                            break;
                        }
                    }
                });
    }

    private void updateDebugText(String message) {
        Platform.runLater(() -> CookingInaMain.debugText.setText(message));
    }

    public void spawnTrashCan(double x, double y) {
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
                .with(new StoreItemComponent(storeItem))
                .buildAndAttach();
    }

    public static void spawnRawIngredient1(StoreItem storeItem, Equipment equipment, double x, double y) {
        var ingredientEntity = entityBuilder()
                .type(CookingInaMain.EntityType.INGREDIENT)
                .at(x, y)
                .zIndex(50)
                .viewWithBBox(FXGL.texture(storeItem.getRawResource(), storeItem.getWidth(), storeItem.getHeight()))
                .with(new StoreItemComponent(storeItem))
                .buildAndAttach();
        // Add the CookingComponent (cooking time behavior)
        ingredientEntity.addComponent(new CookingComponent(
                storeItem.getPreparationTime(),  // preparation time from StoreItem
                storeItem,                       // the StoreItem object itself
                equipment                        // equipment where it's cooked
        ));

        setHighlight(ingredientEntity, x, y);

    }


    public void spawnReadyIngredient(StoreItem item, Equipment equipment, double x, double y) {
        Entity entity;
        if (item.getIsJuice()) {
            if (juiceTray.size() < MAX_JUICE_ON_TRAY) {
                entity = spawnJuiceEntity(item, equipment, x, y);
                juiceTray.add(entity);
            } else {
                entity = null;
            }
        } else {
            entity = entityBuilder()
                    .type(CookingInaMain.EntityType.INGREDIENT)
                    .at(x, y)
                    .zIndex(50)
                    .viewWithBBox(FXGL.texture(item.getCookedResource(), item.getWidth(), item.getHeight()))
                    .with(new DraggableComponent())
                    .with(new CollidableComponent(true))
                    .with(new OvercookComponent(item, equipment))
                    .with(new StoreItemComponent(item))
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

    private Entity spawnJuiceEntity(StoreItem cookedItem,
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
            } else {
                // Snap back home
                juice.setPosition(homeX, homeY);
            }

            e.consume();
        });

        return juice;
    }

    public void spawnEquipment(Equipment equipment, double x, double y, int width, int height) {
        FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.EQUIPMENT)
                .at(x, y)
                .zIndex(0)
                .viewWithBBox(FXGL.texture(equipment.getEmptyResource(), width, height))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        equipment.setLayoutX((int) x);
        equipment.setLayoutY((int) y);
    }

    public void spawnPaperTray(PaperTray paperTray, double x, double y, int width, int height) {
        Entity entity = FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.PLATE)
                .at(x, y)
                .zIndex(0)
                .viewWithBBox(FXGL.texture(paperTray.getEmptyResource(), width, height))
                .with(new PaperTrayComponent(paperTray))
                .buildAndAttach();
        setHighlight(entity, x, y);

    }

    public void spawnContainer(Food food, double x, double y, int width, int height) {
        FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.CONTAINER)
                .at(x, y)
                .zIndex(-1)
                .viewWithBBox(FXGL.texture(food.getContainerResource(), width, height))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        food.setLayoutX((int) x);
        food.setLayoutY((int) y);
    }

    public void spawnInvisibleEquipment(BeverageDispenser dispenser, double x, double y, int width, int height) {
        Entity invisibleEntity = FXGL.entityBuilder()
                .at(x, y)
                .zIndex(100)
                .type(CookingInaMain.EntityType.CONTAINER)
                .bbox(new HitBox("CLICK_BOX", BoundingShape.box(width, height)))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        Texture texture = FXGL.texture(dispenser.getEmptyResource());
        texture.setOpacity(0.01);
        invisibleEntity.getViewComponent().addChild(texture);

        texture.setFitWidth(width);
        texture.setFitHeight(height);

        texture.setMouseTransparent(false);
        texture.setOnMouseClicked(e -> {
            if(dispenser.isOccupied()) return;
            dispenser.setOccupied(true);
            System.out.println("CLICK DETECTED on invisible entity!");
            System.out.println("Equipment resource: " + dispenser.getEmptyResource());
            spawnJuiceCup(x + 40, y + 220, dispenser.getItem(), dispenser);
        });
    }

    public void spawnJuiceCup(double x, double y, StoreItem juiceItem, Equipment equipment) {
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


    public void spawnCustomerAtRandomIntervals() {
        // Kick off the first scheduling
        scheduleNextSpawn();
    }

    private void scheduleNextSpawn() {
        if (components.size() >= MAX_CUSTOMERS) {
            System.out.println("At cap; pausing spawns.");
            return;
        }

        double delay = 1 + random.nextDouble() * 2;
        FXGL.runOnce(() -> {
            boolean spawned = this.spawnCustomer("customer_image.png");

            if (spawned && components.size() < MAX_CUSTOMERS) {
                // Schedule next only if we’re still under the limit
                scheduleNextSpawn();
            }
        }, Duration.seconds(delay));
    }

    public boolean spawnCustomer(String imageName) {
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

        Entity ent = FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.CUSTOMER)
                .at(startX, y)
                .viewWithBBox(tex)
                .zIndex(-1)
                .with(new CustomerComponent(targetX, y, dir))
                .buildAndAttach();

        CustomerComponent cc = ent.getComponent(CustomerComponent.class);
        components.add(cc);

        System.out.println("Spawned customer → targetX=" + targetX +
                "  Active: " + components.size());
        return true;
    }

    private boolean checkOverlap(double x, int w) {
        return this.components.stream()
                .anyMatch(cc -> Math.abs(cc.getTargetX() - x) < w);
    }

    public static void showOrderBubble(Entity customer, List<Order> orders) {
        // If the customer already has one, remove it first:
        if (customer.hasComponent(SpeechBubbleComponent.class)) {
            customer.removeComponent(SpeechBubbleComponent.class);
        }

        // Attach a fresh bubble component with the current orders:
        customer.addComponent(new SpeechBubbleComponent(orders));
    }
}