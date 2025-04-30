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
import com.example.cookingina.objects.entity.TrashCan;
import com.example.cookingina.objects.entity.equipment.BeverageDispenser;
import com.example.cookingina.objects.entity.equipment.Fryer;
import com.example.cookingina.objects.entity.storeItem.Calamansi_Juice;
import com.example.cookingina.objects.entity.storeItem.QuekQuek;
import javafx.application.Platform;
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
                            .with(new CollidableComponent(true))
                            .with(new UIController(storeItem, equipment, x, y)) // Your custom UI controller
                            .buildAndAttach();

                    // Add the CookingComponent (cooking time behavior)
                    ingredientEntity.addComponent(new CookingComponent(
                            storeItem.getPreparationTime(),  // preparation time from StoreItem
                            storeItem,                       // the StoreItem object itself
                            equipment                        // equipment where it's cooked
                    ));

    }


    public static void spawnCookedIngredient(StoreItem cookedItem, Equipment equipment, double x, double y) {
        if (cookedItem.getDescription().contains("juice")) {
            if (juiceTray.size() < MAX_JUICE_ON_TRAY) {
                Entity juice = spawnJuiceEntity(cookedItem, equipment, x, y);
                juiceTray.add(juice);
            } else {
                System.out.println("Juice tray full! Adding juice to waiting queue.");
                waitingJuiceQueue.add(new WaitingItem(cookedItem, equipment, x, y, cookedItem.getWidth(), cookedItem.getHeight(), null));
            }
        } else {
            Entity entity = entityBuilder()
                    .type(CookingInaMain.EntityType.INGREDIENT)
                    .at(x, y)
                    .viewWithBBox(FXGL.texture(cookedItem.getCookedResource(), cookedItem.getWidth(), cookedItem.getHeight()))
                    .with(new DraggableComponent())
                    .with(new CollidableComponent(true))
                    .with(new UIController(cookedItem, equipment, x, y))
                    .buildAndAttach();
            entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
                entity.setPosition(x, y);

            });
        }
    }

    private static boolean isInsideTray(double x, double y) {
        return x >= 480 && x <= 640 && y >= 150 && y <= 260;
    }

    private static Entity spawnJuiceEntity(StoreItem cookedItem, Equipment equipment, double x, double y) {
        Entity juice = FXGL.entityBuilder()
                .at(x, y)
                .view(FXGL.texture(cookedItem.getCookedResource(), cookedItem.getWidth(), cookedItem.getHeight()))
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
                15.0, 12.99, 2.0, 1, 110,60
        );
    }

    public static void spawnJuiceCup(String image, double x, double y, StoreItem juiceItem, Equipment equipment) {
        FXGL.entityBuilder()
                .at(x, y)
                .type(CookingInaMain.EntityType.INGREDIENT)
                .viewWithBBox(FXGL.texture(image, juiceItem.getWidth(), juiceItem.getHeight()))
                .with(new CollidableComponent(true))
                .with(new CookingComponent(
                        juiceItem.getPreparationTime(),
                        juiceItem,
                        equipment
                ))
                .zIndex(100)
                .buildAndAttach();
    }
}
