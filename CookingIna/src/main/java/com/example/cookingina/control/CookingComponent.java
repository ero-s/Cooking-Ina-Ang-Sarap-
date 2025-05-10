package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class CookingComponent extends Component {
    private enum State { RAW, COOKED, OVERCOOKED }
    private State state = State.RAW;
    private double timer;
    private final double totalTime;
    private final StoreItem storeItem;
    private final Equipment equipment;
    private ProgressBar progressBar;
    private boolean isCooked = false;
    private boolean isPaused = false;
    private Point2D position;
    private UIController uc = new UIController();

    public CookingComponent(double preparationTime, StoreItem storeItem, Equipment equipment) {
        this.totalTime = preparationTime;
        this.timer = preparationTime;
        this.storeItem = storeItem;
        this.equipment = equipment;
    }

    @Override
    public void onAdded() {
        // Set up progress bar
        progressBar = new ProgressBar();
        progressBar.setWidth(50);
        progressBar.setHeight(10);
        progressBar.setTranslateY(-20);
        progressBar.setTranslateX(-20);
        progressBar.setFill(Color.LIMEGREEN);

        entity.getViewComponent().addChild(progressBar);
        equipment.setOccupied(true);

        // Remember pan position
        position = new Point2D(entity.getX(), entity.getY());

        // Enable dragging and pause while dragging
        entity.addComponent(new DraggableComponent());
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> isPaused = true);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, e -> handleClickListener());
    }

    @Override
    public void onUpdate(double tpf) {
        if (isCooked || isPaused) return;
        timer -= tpf;

        // Update progress bar
        progressBar.setCurrentValue(totalTime - timer);
        progressBar.setMinValue(0);
        progressBar.setMaxValue(totalTime);

        if (timer <= 0) {
            isCooked = true;
            entity.getViewComponent().clearChildren();

            // Spawn ready item
            uc.spawnReadyIngredient(storeItem, equipment, position.getX(), position.getY());

            // Attempt immediate placement
            boolean placed = handleClickListener();
            if (!placed) {
                // Return to pan
                entity.setPosition(position);
                isPaused = false;
            }
        }
    }

    private boolean handleClickListener() {
        boolean onTrash = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.TRASH)
                .stream()
                .anyMatch(e -> e.getBoundingBoxComponent()
                        .isCollidingWith(entity.getBoundingBoxComponent()));
        if (onTrash) {
            equipment.setOccupied(false);
            entity.removeFromWorld();
            return true;
        }

        // Snap back if not trash
        entity.setPosition(position);
        isPaused = false;
        return false;
    }

    /**
     * Returns whether the item is cooked. Used by OrderComponent removal filter.
     */
    public boolean isCooked() {
        return isCooked;
    }

    public Equipment getEquipment() {
        return equipment;
    }
}