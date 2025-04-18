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


    @Override
    public void onAdded() {
        draggable = entity.getComponent(DraggableComponent.class);
        String debugMessage = "Drag ended at: " + entity.getX() + ", " + entity.getY();
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (isOverStation()) {
                FXGL.getGameWorld().getCollidingEntities(entity).stream()
                        .filter(e -> e.isType(CookingInaMain.EntityType.STATION_NO_USE))
                        .findFirst()
                        .ifPresent(station -> {
                            // Get station position
                            double stationX = station.getX();
                            double stationY = station.getY();

                            // Remove empty station
                            station.removeFromWorld();

                            // Spawn used station at the same position
                            equipment.addSpaceTaken();
                            spawnUsedStation(equipment, stationX, stationY);
                        });
                entity.removeFromWorld();
                spawnIngredient(storeItem, equipment, originalX, originalY);
                updateDebugText(debugMessage);
            }
        });
    }


    private void updateDebugText(String message) {
        Platform.runLater(() -> CookingInaMain.debugText.setText(message));
    }

    private boolean isOverStation() {
        return FXGL.getGameWorld()
                .getEntitiesInRange(entity.getBoundingBoxComponent().range(10, 10))
                .stream()
                .anyMatch(e -> e.isType(CookingInaMain.EntityType.STATION_NO_USE));
    }

    public static void spawnIngredient(StoreItem storeItem, Equipment equipment, double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.INGREDIENT)
                .at(x, y)
                .viewWithBBox( FXGL.texture(storeItem.getResource(), 40,40))
                .with(new DraggableComponent())
                .with(new CollidableComponent(true))
                .with(new UIController(storeItem, equipment,x, y))
                .buildAndAttach();
    }

    public static void spawnEmptyStation(Equipment equipment, double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.STATION_NO_USE)
                .at(x, y)
                .viewWithBBox(FXGL.texture(equipment.getEmptyResource(), 120, 120))
                .with(new CollidableComponent(true))
                .buildAndAttach();
        equipment.setLayoutX((int) x);
        equipment.setLayoutY((int) y);
    }


    public static void spawnUsedStation(Equipment equipment, double x, double y) {
        entityBuilder()
                .type(CookingInaMain.EntityType.STATION_USED)
                .at(x, y)
                .viewWithBBox(FXGL.texture(equipment.getUsedResource(), 120, 120))
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }
}
