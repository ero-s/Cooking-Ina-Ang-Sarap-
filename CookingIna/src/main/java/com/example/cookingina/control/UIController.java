package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class UIController extends Component {
    public final StoreItem storeItem;
    public final Equipment equipment;
    public final double originalX;
    public final double originalY;
    public DraggableComponent draggable;
    public boolean wasDragging = false;

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
                .viewWithBBox( FXGL.texture("trash_close.png", 120,140))
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


    public static void spawnCookedIngredient(StoreItem storeItem, Equipment equipment, double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.INGREDIENT)
                .at(x, y)
                .viewWithBBox( FXGL.texture(storeItem.getCookedResource(), 40,40))
                .with(new DraggableComponent())
                .with(new CollidableComponent(true))
                .with(new UIController(storeItem, equipment,x, y))
                .buildAndAttach();
    }

    public static void spawnEquipment(Equipment equipment, double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.EQUIPMENT)
                .at(x, y)
                .viewWithBBox(FXGL.texture(equipment.getEmptyResource(), 150, 150))
                .with(new CollidableComponent(true))
                .buildAndAttach();
        equipment.setLayoutX((int) x);
        equipment.setLayoutY((int) y);
    }
}