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


    public CookingComponent(double preparationTime, StoreItem cookedStoreItem, Equipment equipment, int slotIndex) {
        this.totalTime = preparationTime;
        this.timer = preparationTime;
        this.cookedStoreItem = cookedStoreItem;
        this.equipment = equipment;
        this.slotIndex = slotIndex;
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

        // Store pan position to return later if needed
        position = new Point2D(entity.getX(), entity.getY());

        // Add dragging capability
        entity.addComponent(new DraggableComponent());

        // Pause cooking when dragging starts
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            isPaused = true;
        });

        // Resume or discard based on drop target
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            // Check if dropped on a plate
            boolean onPlate = false;
//                    FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.PLATE).stream()
//                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));

            // Check if dropped on a trash can
            boolean onTrash = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.TRASH).stream()
                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));

            if (onPlate) {
                equipment.freeSlot(slotIndex);
                entity.removeComponent(CookingComponent.class); // Stop cooking
                System.out.println("Ingredient placed on plate!");
                // Optionally: Add logic to mark as served
            } else if (onTrash) {
                //FXGL.play("throw.wav"); // Optional: play sound
                isDiscarded = true;
                equipment.freeSlot(slotIndex);
                entity.removeFromWorld(); // Remove from game
                System.out.println("Ingredient discarded in trash!");
            } else {
                // Return to pan and resume cooking
                entity.setPosition(position);
                isPaused = false;
                System.out.println("Returned to pan, resume cooking.");
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if (isPaused || isCooked || isDiscarded)
            return;

        timer -= tpf;

        double progress = totalTime - timer;
        progressBar.setCurrentValue(progress);
        progressBar.setMinValue(0);
        progressBar.setMaxValue(totalTime);

        if (timer <= 0) {
            isCooked = true;
            // Only update the texture and remove from world if not discarded
            if (!isDiscarded) {
                // Update to cooked form
                entity.getViewComponent().clearChildren();
                entity.getViewComponent().addChild(FXGL.texture(cookedStoreItem.getCookedResource(), 40, 40));
            }
            // After cooking is complete, don't remove it yet unless it's discarded
            if (isDiscarded) {
                entity.removeFromWorld(); // Remove entity if discarded
            }
        }
    }

    @Override
    public void onRemoved() {
        double currX = entity.getX();
        double currY = entity.getY();
        entity.getViewComponent().removeChild(progressBar);
        if (!isDiscarded) {
            UIController.spawnCookedIngredient(cookedStoreItem, equipment, currX, currY);
        }
    }
}
