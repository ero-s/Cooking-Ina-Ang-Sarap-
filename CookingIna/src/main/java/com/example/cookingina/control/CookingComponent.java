package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class CookingComponent extends Component {
    private enum State { RAW, COOKED, OVERCOOKED }
    private State state = State.RAW;
    private double timer;
    private final double totalTime;
    private final StoreItem cookedStoreItem;
    private final Equipment equipment;
    private ProgressBar progressBar;
    private boolean isCooked = false;
    private boolean isPaused = false; // Flag to pause cooking
    private boolean isDiscarded = false;
    private int slotIndex;
    private Point2D position;

    private static int juiceCount = 0; // Keeps track of no. of juices

    public CookingComponent(double preparationTime, StoreItem cookedStoreItem, Equipment equipment ) {

        this.totalTime = preparationTime;
        this.timer = preparationTime;
        this.cookedStoreItem = cookedStoreItem;
        this.equipment = equipment;
        slotIndex = 0;
    }
    @Override
    public void onAdded() {
        // Create and attach progress bar
        progressBar = new ProgressBar();
        progressBar.setWidth(50);
        progressBar.setHeight(10);
        progressBar.setTranslateY(-20); // Position above entity
        progressBar.setTranslateX(-20); // Position above entity
        progressBar.setFill(Color.LIMEGREEN);
        entity.getViewComponent().addChild(progressBar);
        equipment.setOccupied(true);

        // Store pan position to return later if needed
        position = new Point2D(entity.getX(), entity.getY());

        // Add dragging capability
        entity.addComponent(new DraggableComponent());



        // Pause cooking when dragging starts
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> isPaused = true);

        // Handle drop
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            handleClickListener();
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if(isCooked || isPaused) return;
        timer -= tpf;
        double progress = totalTime - timer;
        progressBar.setCurrentValue(progress);
        progressBar.setMaxValue(totalTime);

        if (timer <= 0) {
            isCooked = true;
            entity.getViewComponent().removeChild(progressBar);
            entity.getViewComponent().clearChildren();
            UIController.spawnCookedIngredient(cookedStoreItem,equipment, position.getX(), position.getY());

            // Attempt placement onto tray or trash immediately
            boolean placed = handleClickListener();
            if (!placed) {
                // If not placed, return to pan
                entity.setPosition(position);
                isPaused = false;
                System.out.println("Returned to pan with cooked item.");
            }
        }
    }

    private boolean handleClickListener() {
        boolean onTrash = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.TRASH).stream()
                .anyMatch(e -> e.getBoundingBoxComponent()
                        .isCollidingWith(entity.getBoundingBoxComponent()));
        if (onTrash) {
            isDiscarded = true;
            equipment.setOccupied(false);
            entity.removeFromWorld();
            System.out.println("Ingredient discarded in trash!");
        } else {
            // Snap back and continue (won't actually resume cooking since isCooked=true)
            entity.setPosition(position);
            isPaused = false;
            System.out.println("Returned to pan area.");
            return true;
        }
        return false;
    }

    @Override
    public void onRemoved() {
        entity.getViewComponent().removeChild(progressBar);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            entity.setPosition(position);
        });
    }

    public boolean getIsCooked(){
        return isCooked;
    }
}