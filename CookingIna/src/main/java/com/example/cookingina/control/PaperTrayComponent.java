package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.PaperTray;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class PaperTrayComponent extends Component {
    private final PaperTray paperTray;
    private Point2D trayOriginalPos;
    private Point2D itemOriginalPos;
    private Point2D itemOffset;
    private Point2D dragOffset;
    private Entity boundItem;

    public PaperTrayComponent(PaperTray paperTray) {
        this.paperTray = paperTray;
    }

    @Override
    public void onAdded() {
        // Enable collision
        entity.addComponent(new CollidableComponent(true));

        // Store original tray position
        trayOriginalPos = new Point2D(entity.getX(), entity.getY());

        // Mouse handlers for manual dragging
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onPress);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onDrag);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onRelease);
    }

    private void onPress(MouseEvent e) {
        // Calculate offset between click point and tray origin
        dragOffset = new Point2D(e.getSceneX(), e.getSceneY())
                .subtract(new Point2D(entity.getX(), entity.getY()));
    }

    private void onDrag(MouseEvent e) {
        // Update tray position
        double newX = e.getSceneX() - dragOffset.getX();
        double newY = e.getSceneY() - dragOffset.getY();
        entity.setPosition(newX, newY);

        // Move bound item if present
        if (boundItem != null) {
            boundItem.setPosition(newX + itemOffset.getX(), newY + itemOffset.getY());
        }
    }

    @Override
    public void onUpdate(double tpf) {
        // Look for a valid ingredient to bind when none bound
        if (boundItem == null) {
            FXGL.getGameWorld()
                    .getEntitiesByType(CookingInaMain.EntityType.INGREDIENT)
                    .stream()
                    .filter(e -> e.getBoundingBoxComponent()
                            .isCollidingWith(entity.getBoundingBoxComponent()))
                    .filter(e -> {
                        boolean cooked = e.hasComponent(CookingComponent.class)
                                && e.getComponent(CookingComponent.class).getIsCooked();
                        boolean notBurnt = e.hasComponent(OvercookComponent.class)
                                && !e.getComponent(OvercookComponent.class).getIsBurnt();
                        return cooked || notBurnt;
                    })
                    .findFirst()
                    .ifPresent(this::bindItem);
        }
    }

    private void bindItem(Entity itemEntity) {
        boundItem = itemEntity;

        // Record original positions and offset
        itemOriginalPos = new Point2D(itemEntity.getX(), itemEntity.getY());
        itemOffset = itemOriginalPos.subtract(new Point2D(entity.getX(), entity.getY()));
    }

    private void onRelease(MouseEvent event) {
        boolean served = FXGL.getGameWorld()
                .getEntitiesByType(CookingInaMain.EntityType.CONTAINER)
                .stream()
                .anyMatch(e -> e.getBoundingBoxComponent()
                        .isCollidingWith(entity.getBoundingBoxComponent()));

        if (!served) {
            // Snap tray back
            entity.setPosition(trayOriginalPos);

            // Snap bound item back if exists
            if (boundItem != null) {
                boundItem.setPosition(itemOriginalPos);
            }
        }

        // Clear binding if not served
        if (!served) {
            boundItem = null;
        }
    }

    @Override
    public void onRemoved() {
        entity.removeComponent(CollidableComponent.class);
    }

    public PaperTray getTray() {
        return paperTray;
    }
}