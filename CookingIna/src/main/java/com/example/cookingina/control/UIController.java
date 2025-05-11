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
import com.example.cookingina.objects.entity.equipment.IceCrusher;
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
    private static final List<Entity> juiceTray = new ArrayList<>();
    public final static List<CustomerComponent> components = new ArrayList<>();
    private static final int MAX_CUSTOMERS = 5;
    private static final Random random = new Random();


    // Spawing Container and container item to the equipment
    public void spawnContainerForEquipment(Food storeItem, List<? extends Equipment> equipmentList, double containerX, double containerY, int containerWidth, int containerHeight) {
        entityBuilder()
                .type(CookingInaMain.EntityType.CONTAINER)
                .at(containerX, containerY)
                .viewWithBBox(FXGL.texture(storeItem.getContainerResource(), containerWidth, containerHeight))
                .zIndex(0)
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

    public static void spawnRawIngredient1(Food storeItem, Equipment equipment, double x, double y) {
        var ingredientEntity = entityBuilder()
                .type(CookingInaMain.EntityType.INGREDIENT)
                .at(x, y)
                .zIndex(50)
                .viewWithBBox(FXGL.texture(storeItem.getItem().getRawResource(), storeItem.getItem().getWidth(), storeItem.getItem().getHeight()))
                .with(new StoreItemComponent(storeItem.getItem()))
                .buildAndAttach();
        // Add the CookingComponent (cooking time behavior)
        ingredientEntity.addComponent(new CookingComponent(
                storeItem.getItem().getPreparationTime(),  // preparation time from StoreItem
                storeItem.getItem(),                       // the StoreItem object itself
                equipment                        // equipment where it's cooked
        ));

        setHighlight(ingredientEntity, x, y);
    }


    public void spawnReadyIngredient(StoreItem item, Equipment equipment, double x, double y) {
        Entity entity;
        if (item.getIsJuice() ) {
            entity = spawnJuiceEntity(item, equipment, x, y);

        }else if(item.getName().equals("mango")){
            entity = entityBuilder()
                    .type(CookingInaMain.EntityType.INGREDIENT)
                    .at(x, y)
                    .zIndex(50)
                    .viewWithBBox(FXGL.texture(item.getCookedResource(), item.getWidth(), item.getHeight()))
                    .with(new DraggableComponent())
                    .with(new StoreItemComponent(item))
                    .with(new OrderComponent(equipment))
                    .buildAndAttach();
        }else if(item.getName().equals("halo-halo")){
            entity = entityBuilder()
                    .type(CookingInaMain.EntityType.INGREDIENT)
                    .at(x, y)
                    .zIndex(50)
                    .viewWithBBox(FXGL.texture(item.getCookedResource(), item.getWidth(), item.getHeight()))
                    .with(new DraggableComponent())
                    .with(new StoreItemComponent(item))
                    .with(new OrderComponent(equipment))
                    .buildAndAttach();
        }else{
            entity = entityBuilder()
                    .type(CookingInaMain.EntityType.INGREDIENT)
                    .at(x, y)
                    .zIndex(50)
                    .viewWithBBox(FXGL.texture(item.getCookedResource(), item.getWidth(), item.getHeight()))
                    .with(new DraggableComponent())
                    .with(new OvercookComponent(item, equipment))
                    .with(new StoreItemComponent(item))
                    .buildAndAttach();
        }
        if(entity != null){

            entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
                entity.setPosition(x, y);
            });
            setHighlight(entity, x, y);
        }
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
                .with(new OrderComponent(equipment))
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
                .zIndex(0)
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
            spawnJuiceCup(x + 20, y + 220, dispenser.getItem(), dispenser);
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

    private boolean spawnPaused = false;

    public void spawnCustomerAtRandomIntervals() {
        FXGL.getGameTimer().runAtInterval(this::scheduleNextSpawn, Duration.seconds(1));
    }

    private void scheduleNextSpawn() {

        List<String> imageName = Arrays.asList(
                "Austine", "Krizza", "Kyle", "Sherielyn", "Vince", "Christian", "Shervin"
        );
        double delay = 1 + random.nextDouble() * 2;

        FXGL.runOnce(() -> {
            // Only spawn if game still running and under customer cap
            if (components.size() < MAX_CUSTOMERS) {
                spawnCustomer(imageName);
            }
            // Continue scheduling regardless
            scheduleNextSpawn();
        }, Duration.seconds(delay));
    }

    public boolean spawnCustomer(List<String> imageNames) {
        int w = 200, h = 200;
        double y = 330;
        int sceneW = FXGL.getAppWidth();

        if (components.size() >= MAX_CUSTOMERS) {
            System.out.println("MAX reached (" + MAX_CUSTOMERS + "); skipping spawn.");
            return false;
        }

        if (imageNames.isEmpty()) {
            System.out.println("Image list is empty; cannot spawn customer.");
            return false;
        }

        String selectedImageName = imageNames.get(random.nextInt(imageNames.size()));

        double targetX;
        int maxTries = 10;
        int attempts = 0;
        int minX = Math.min(400, sceneW - w - 350);
        int maxX = Math.max(400, sceneW - w - 350);

        do {
            targetX = random.nextInt(minX, maxX);
            attempts++;
        } while (checkOverlap(targetX, w) && attempts < maxTries);

        if (checkOverlap(targetX, w)) {
            System.out.println("Could not find non-overlapping position after " + maxTries + " tries.");
            return false;
        }

        boolean goRight = random.nextBoolean();
        double startX = goRight ? -w : sceneW + w;
        String dir = goRight ? "RIGHT" : "LEFT";

        Entity ent = entityBuilder()
                .type(CookingInaMain.EntityType.CUSTOMER)
                .at(startX, y)
                .viewWithBBox(FXGL.texture(selectedImageName + ".png", w, h))
                .zIndex(-1)
                .with(new CustomerComponent(targetX, y, dir))
                .buildAndAttach();

        CustomerComponent cc = ent.getComponent(CustomerComponent.class);
        components.add(cc);

        System.out.println("Spawned customer → targetX=" + targetX + "  Active: " + components.size());
        return true;
    }

    private boolean checkOverlap(double x, int width) {
        return components.stream()
                .anyMatch(cc -> Math.abs(cc.getTargetX() - x) < width);
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