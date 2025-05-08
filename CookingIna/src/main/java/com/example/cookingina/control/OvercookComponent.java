package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.PaperTray;
import com.example.cookingina.objects.entity.StoreItem;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Optional;

public class OvercookComponent extends Component {
    private static final double OVERCOOK_DURATION = 30.0;
    private double remainingTime;
    private final StoreItem cookedStoreItem;
    private boolean isBurnt = false;
    private final Equipment equipment;
    private ProgressBar progressBar;
    private boolean isPaused = false;
    private boolean isDiscarded = false;
    private Point2D initialPosition;

    public OvercookComponent(StoreItem cookedStoreItem, Equipment equipment) {
        this.cookedStoreItem = cookedStoreItem;
        this.equipment = equipment;
        this.remainingTime = OVERCOOK_DURATION;
    }

    @Override
    public void onAdded() {
        // Initialize progress bar
        progressBar = new ProgressBar();
        progressBar.setWidth(50);
        progressBar.setHeight(10);
        progressBar.setTranslateX(-25);
        progressBar.setTranslateY(-20);
        progressBar.setFill(Color.RED);
        entity.getViewComponent().addChild(progressBar);

        // Store initial pan position
        initialPosition = new Point2D(entity.getX(), entity.getY());

        // Enable dragging
        entity.addComponent(new DraggableComponent());

        // Pause on drag start
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> isPaused = true);

        // Handle drop
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (!handlePlacement()) {
                // Return to pan and resume
                entity.setPosition(initialPosition);
                isPaused = false;
                System.out.println("Returned to pan, resume cooking.");
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if (isPaused) return;

        remainingTime -= tpf;
        progressBar.setCurrentValue(OVERCOOK_DURATION - remainingTime);
        progressBar.setMaxValue(OVERCOOK_DURATION);

        if (remainingTime <= 0) {
            // Cooking done (burnt)
            isPaused = true;
            isBurnt = true;
            entity.getViewComponent().removeChild(progressBar);
            entity.getViewComponent().clearChildren();
            entity.getViewComponent().addChild(
                    FXGL.texture(cookedStoreItem.getCookedResource(), (int)entity.getWidth(), (int)entity.getHeight())
            );

            // Attempt placement onto tray or trash immediately
            boolean placed = handlePlacement();
            if (!placed) {
                // If not placed, return to pan
                entity.setPosition(initialPosition);
                isPaused = false;
                System.out.println("Returned to pan with cooked item.");
            }
        }
    }

    @Override
    public void onRemoved() {
        // Clean up progress bar
        if (progressBar != null) {
            entity.getViewComponent().removeChild(progressBar);
        }
    }

    /**
     * @return true if we placed the cooked item (on a plate) or discarded it (in trash),
     *         false if neither—so caller can snap it back to the pan.
     */
    private boolean handlePlacement() {
        // 1) Try to drop on any plate
        for (Entity plateEnt : FXGL.getGameWorld()
                .getEntitiesByType(CookingInaMain.EntityType.PLATE)) {

            if (plateEnt.getBoundingBoxComponent()
                    .isCollidingWith(entity.getBoundingBoxComponent())) {

                PaperTrayComponent ptc = plateEnt.getComponent(PaperTrayComponent.class);
                PaperTray tray = ptc.getTray();

                if (!tray.isOccupied() && !isBurnt) {
                    // mark equipment free
                    equipment.setOccupied(false);

                    // spawn the cooked item at *this* plate's coordinates
                    int spawnX = (int) plateEnt.getX() + (int)(plateEnt.getWidth()  - cookedStoreItem.getWidth())  / 2;
                    int spawnY = (int) plateEnt.getY() + (int)(plateEnt.getHeight() - cookedStoreItem.getHeight()) / 2;

                    tray.addStoreItem(cookedStoreItem, spawnX, spawnY);
                    tray.setOccupied(true);

                    entity.removeFromWorld();
                    System.out.println("✔ Placed on plate at (" + plateEnt.getX() + "," + plateEnt.getY() + ")");
                    return true;
                }
            }
        }

        // 2) Otherwise, check trash
        boolean hitTrash = FXGL.getGameWorld()
                .getEntitiesByType(CookingInaMain.EntityType.TRASH)
                .stream()
                .anyMatch(tr -> tr.getBoundingBoxComponent()
                        .isCollidingWith(entity.getBoundingBoxComponent()));
        if (hitTrash) {
            isDiscarded = true;
            equipment.setOccupied(false);
            entity.removeFromWorld();
            System.out.println("🗑  Discarded in trash");
            return true;
        }

        return false;
    }

    public boolean getIsBurnt() {
        return isBurnt;
    }

}