package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class OvercookComponent extends Component {
    private double overcookTime;
    private double totalTime;
    private final StoreItem cookedStoreItem;
    private final Equipment equipment;
    private ProgressBar progressBar;
    private boolean isCooked = true;
    private boolean isPaused = false; // Flag to pause cooking
    private boolean isDiscarded = false;
    private int slotIndex;
    private Point2D position;

    public OvercookComponent(StoreItem cookedStoreItem, Equipment equipment) {
        this.overcookTime = 30;
        this.totalTime = overcookTime;
        this.cookedStoreItem = cookedStoreItem;
        this.equipment = equipment;
    }

    public void onAdded(){
        progressBar = new ProgressBar();
        progressBar.setWidth(50);
        progressBar.setHeight(10);
        progressBar.setTranslateY(-20); // Position above entity
        progressBar.setTranslateX(-20); // Position above entity
        progressBar.setFill(Color.RED);
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
            boolean onPlate = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.PLATE).stream()
                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));

            // Check if dropped on a trash can
            boolean onTrash = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.TRASH).stream()
                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));

            if (onPlate && isCooked) {
                equipment.setOccupied(false);
                entity.removeFromWorld();
                System.out.println("Ingredient placed on plate!");
                // Optionally: Add logic to mark as served
            } else if (onTrash) {
                //FXGL.play("throw.wav"); // Optional: play sound
                isDiscarded = true;
                equipment.setOccupied(false);
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
    public void onUpdate(double tpf) {
        if(isPaused) return;

        overcookTime -= tpf;
        double progress = totalTime - overcookTime;
        progressBar.setCurrentValue(progress);
        progressBar.setMaxValue(totalTime);

        if (overcookTime <= 0) {
            isCooked = true;
            // Only update the texture and remove from world if not discarded
            if (!isDiscarded) {
                // Update to cooked form
                entity.getViewComponent().clearChildren();
                entity.getViewComponent().removeChild(progressBar);
                //burnt item asset
                entity.getViewComponent().addChild(FXGL.texture(cookedStoreItem.getCookedResource(), entity.getWidth(), entity.getHeight()));
            }
            boolean onPlate = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.PLATE).stream()
                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));

            // Check if dropped on a trash can
            boolean onTrash = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.TRASH).stream()
                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));

            if (onPlate && isCooked) {
                equipment.setOccupied(false);
                entity.removeFromWorld();
                System.out.println("Ingredient placed on plate!");
                // Optionally: Add logic to mark as served
            }else if (onTrash) {
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
        }

    }
    public void onRemoved(){
        entity.getViewComponent().removeChild(progressBar);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            entity.setPosition(position);
        });
    }
}
