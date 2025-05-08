//package com.example.cookingina.control;
//
//import com.almasb.fxgl.dsl.FXGL;
//import com.almasb.fxgl.dsl.components.DraggableComponent;
//import com.almasb.fxgl.entity.component.Component;
//import com.almasb.fxgl.ui.ProgressBar;
//import com.example.cookingina.CookingInaMain;
//import com.example.cookingina.objects.entity.Equipment;
//import com.example.cookingina.objects.entity.StoreItem;
//import javafx.scene.paint.Color;
//import javafx.geometry.Point2D;
//import javafx.scene.input.MouseEvent;
//
//
//public class CookingComponent extends Component {
//    private double timer;
//    private final double totalTime;
//    private final StoreItem cookedStoreItem;
//    private final Equipment equipment;
//    private ProgressBar progressBar;
//    private boolean isCooked = false;
//    private boolean isPaused = false; // Flag to pause cooking
//    private boolean isDiscarded = false;
//    private int slotIndex;
//    private Point2D position;
//
//    private static int juiceCount = 0; // Keeps track of no. of juices
//
//    public CookingComponent(double preparationTime, StoreItem cookedStoreItem, Equipment equipment, int slotIndex) {
//        this.totalTime = preparationTime;
//        this.timer = preparationTime;
//        this.cookedStoreItem = cookedStoreItem;
//        this.equipment = equipment;
//        this.slotIndex = slotIndex;
//    }
//
//    @Override
//    public void onAdded() {
//        // Create and attach progress bar
//        progressBar = new ProgressBar();
//        progressBar.setWidth(50);
//        progressBar.setHeight(10);
//        progressBar.setTranslateY(-20); // Position above entity
//        progressBar.setTranslateX(-20); // Position above entity
//        progressBar.setFill(Color.LIMEGREEN);
//
//        entity.getViewComponent().addChild(progressBar);
//
//        // Store pan position to return later if needed
//        position = new Point2D(entity.getX(), entity.getY());
//
//        // Add dragging capability
//        entity.addComponent(new DraggableComponent());
//
//        // Pause cooking when dragging starts
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
//            isPaused = true;
//        });
//
//        // Resume or discard based on drop target
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
//            // Check if dropped on a plate
//            boolean onPlate = false;
////                    FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.PLATE).stream()
////                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));
//
//            // Check if dropped on a trash can
//            boolean onTrash = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.TRASH).stream()
//                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));
//
//            if (onPlate) {
//                equipment.freeSlot(slotIndex);
//                entity.removeComponent(CookingComponent.class); // Stop cooking
//                System.out.println("Ingredient placed on plate!");
//                // Optionally: Add logic to mark as served
//            } else if (onTrash) {
//                //FXGL.play("throw.wav"); // Optional: play sound
//                isDiscarded = true;
//                equipment.freeSlot(slotIndex);
//                entity.removeFromWorld(); // Remove from game
//                System.out.println("Ingredient discarded in trash!");
//            } else {
//                // Return to pan and resume cooking
//                entity.setPosition(position);
//                isPaused = false;
//                System.out.println("Returned to pan, resume cooking.");
//            }
//        });
//    }
//
//
////    @Override
////    public void onUpdate(double tpf) {
////        timer -= tpf;
////
////        // Update progress (reverse calculation: 1.0 -> 0.0)
////        double progress = totalTime - timer;
////        progressBar.setCurrentValue(progress); // If using 0-100 scale
////        progressBar.setMinValue(0);
////        progressBar.setMaxValue(totalTime);
////
////        if(timer <= 0) {
////            // Replace texture by updating the view
////            entity.getViewComponent().clearChildren();
////            if(cookedStoreItem.getDescription().contains("juice")){
////                entity.getViewComponent().addChild(FXGL.texture(cookedStoreItem.getCookedResource(), 40, 40));
////            }else {
////                entity.getViewComponent().addChild(FXGL.texture(cookedStoreItem.getCookedResource(), 40, 40));
////            }
////            // Remove raw ingredient
////            entity.removeFromWorld();
//    ////            FXGL.play("cooking-done.wav");
////        }
////    }
//
//    @Override
//    public void onUpdate(double tpf) {
//        // Don’t run if paused, already cooked, or discarded
//        if (isPaused || isCooked || isDiscarded)
//            return;
//
//        // Advance the timer and update the progress bar
//        timer -= tpf;
//        double progress = totalTime - timer;
//        progressBar.setMinValue(0);
//        progressBar.setMaxValue(totalTime);
//        progressBar.setCurrentValue(progress);
//
//        // When time’s up…
//        if (timer <= 0) {
//            isCooked = true;
//
//            // If we have no cookedStoreItem or we’ve discarded it, skip the texture change
//            if (cookedStoreItem != null && !isDiscarded) {
//                String desc = cookedStoreItem.getDescription().toLowerCase();
//                int w, h;
//
//                // Pick size by description
//                if (desc.contains("quekquek") || desc.contains("hotdog")) {
//                    w = h = 80;
//                } else if (desc.contains("tempura")) {
//                    w = h = 120;
//                } else if (desc.contains("juice")) {
//                    w = 50; h = 80;
//                } else {
//                    // default cooked item size
//                    w = h = 40;
//                }
//
//                // Swap out the texture
//                entity.getViewComponent().clearChildren();
//                entity.getViewComponent().addChild(
//                        FXGL.texture(cookedStoreItem.getCookedResource(), w, h)
//                );
//            }
//
//            // If it was discarded, remove it now
//            if (isDiscarded) {
//                entity.removeFromWorld();
//            }
//        }
//    }
//
//    @Override
//    public void onRemoved() {
//        double currX = entity.getX();
//        double currY = entity.getY();
//        entity.getViewComponent().removeChild(progressBar);
//        if (!isDiscarded) {
//            UIController.spawnCookedIngredient(cookedStoreItem, equipment, currX, currY, 40, 40);
//        }
//    }
//
//}

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