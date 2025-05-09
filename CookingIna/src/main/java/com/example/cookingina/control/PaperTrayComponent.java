//package com.example.cookingina.control;
//
//import com.almasb.fxgl.dsl.FXGL;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.entity.component.Component;
//import com.almasb.fxgl.entity.components.CollidableComponent;
//import com.example.cookingina.CookingInaMain;
//import com.example.cookingina.objects.entity.PaperTray;
//import javafx.geometry.Point2D;
//import javafx.scene.input.MouseEvent;
//
//public class PaperTrayComponent extends Component {
//    private final PaperTray paperTray;
//    private Point2D trayOriginalPos;
//    private Point2D itemOriginalPos;
//    private Point2D itemOffset;
//    private Point2D dragOffset;
//    private Entity boundItem;
//
//    public PaperTrayComponent(PaperTray paperTray) {
//        this.paperTray = paperTray;
//    }
//
//    @Override
//    public void onAdded() {
//        // Enable collision
//        entity.addComponent(new CollidableComponent(true));
//
//        // Store original tray position
//        trayOriginalPos = new Point2D(entity.getX(), entity.getY());
//
//        // Mouse handlers for manual dragging
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onPress);
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onDrag);
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onRelease);
//    }
//
//    private void onPress(MouseEvent e) {
//        // Calculate offset between click point and tray origin
//        dragOffset = new Point2D(e.getSceneX(), e.getSceneY())
//                .subtract(new Point2D(entity.getX(), entity.getY()));
//    }
//
//    private void onDrag(MouseEvent e) {
//        // Update tray position
//        double newX = e.getSceneX() - dragOffset.getX();
//        double newY = e.getSceneY() - dragOffset.getY();
//        entity.setPosition(newX, newY);
//
//        // Move bound item if present
//        if (boundItem != null) {
//            boundItem.setPosition(newX + itemOffset.getX(), newY + itemOffset.getY());
//        }
//    }
//
//    @Override
//    public void onUpdate(double tpf) {
//        // Look for a valid ingredient to bind when none bound
//        if (boundItem == null) {
//            FXGL.getGameWorld()
//                    .getEntitiesByType(CookingInaMain.EntityType.INGREDIENT)
//                    .stream()
//                    .filter(e -> e.getBoundingBoxComponent()
//                            .isCollidingWith(entity.getBoundingBoxComponent()))
//                    .filter(e -> {
//                        boolean cooked = e.hasComponent(CookingComponent.class)
//                                && e.getComponent(CookingComponent.class).getIsCooked();
//                        boolean notBurnt = e.hasComponent(OvercookComponent.class)
//                                && !e.getComponent(OvercookComponent.class).getIsBurnt();
//                        return cooked || notBurnt;
//                    })
//                    .findFirst()
//                    .ifPresent(this::bindItem);
//        }
//    }
//
//    private void bindItem(Entity itemEntity) {
//        boundItem = itemEntity;
//
//        // Record original positions and offset
//        itemOriginalPos = new Point2D(itemEntity.getX(), itemEntity.getY());
//        itemOffset = itemOriginalPos.subtract(new Point2D(entity.getX(), entity.getY()));
//    }
//
//    private void onRelease(MouseEvent event) {
//        boolean served = FXGL.getGameWorld()
//                .getEntitiesByType(CookingInaMain.EntityType.CONTAINER)
//                .stream()
//                .anyMatch(e -> e.getBoundingBoxComponent()
//                        .isCollidingWith(entity.getBoundingBoxComponent()));
//
//        if (!served) {
//            // Snap tray back
//            entity.setPosition(trayOriginalPos);
//
//            // Snap bound item back if exists
//            if (boundItem != null) {
//                boundItem.setPosition(itemOriginalPos);
//            }
//        }
//
//        // Clear binding if not served
//        if (!served) {
//            boundItem = null;
//        }
//    }
//
//    @Override
//    public void onRemoved() {
//        entity.removeComponent(CollidableComponent.class);
//    }
//
//    public PaperTray getTray() {
//        return paperTray;
//    }
//}

package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.PaperTray;
import com.example.cookingina.objects.entity.StoreItem;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class PaperTrayComponent extends Component {
    private final PaperTray paperTray;
    private Point2D trayOriginalPos;
    private Point2D itemOriginalPos;
    private Point2D itemOffset;
    private Point2D dragOffset;
    private Entity boundItem;

    private boolean textureChanged = false;
    private CollidableComponent collidableComponent;

    public PaperTrayComponent(PaperTray paperTray) {
        this.paperTray = paperTray;
    }

    @Override
    public void onAdded() {
        entity.addComponent(new CollidableComponent(true));
        trayOriginalPos = entity.getPosition();

        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onPress);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onDrag);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onRelease);
    }

    private void onPress(MouseEvent e) {
        dragOffset = new Point2D(e.getSceneX(), e.getSceneY()).subtract(entity.getPosition());
        collidableComponent = entity.getComponent(CollidableComponent.class);
        entity.removeComponent(CollidableComponent.class);
    }

    private void onDrag(MouseEvent e) {
        Point2D newPos = new Point2D(e.getSceneX(), e.getSceneY()).subtract(dragOffset);
        entity.setPosition(newPos);

        if (boundItem != null && itemOffset != null) {
            boundItem.setPosition(newPos.add(itemOffset));
        }
    }

    @Override
    public void onUpdate(double tpf) {
        // Only check for new items if no item is bound and texture hasn't been changed
        if (boundItem == null && !textureChanged) {
            FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.INGREDIENT).stream()
                    .filter(e -> e.isColliding(entity))
                    .filter(this::isValidItem)
                    .findFirst()
                    .ifPresent(this::bindItem);
        }
    }

    private boolean isValidItem(Entity item) {
        boolean cooked = item.hasComponent(CookingComponent.class)
                && item.getComponent(CookingComponent.class).getIsCooked();
        boolean notBurnt = item.hasComponent(OvercookComponent.class)
                && !item.getComponent(OvercookComponent.class).getIsBurnt();
        return cooked || notBurnt;
    }

    private void bindItem(Entity item) {
        boundItem = item;
        itemOriginalPos = item.getPosition();
        itemOffset = itemOriginalPos.subtract(entity.getPosition());

        if (!textureChanged) {
            String texturePath = getTextureForItem(item);
            changeTrayTexture(texturePath);
            textureChanged = true;
        }

        FXGL.getGameWorld().removeEntity(item);
    }

    private String getTextureForItem(Entity item) {
        if (!item.hasComponent(StoreItemComponent.class)) {
            System.err.println("Item missing StoreItemComponent: " + item);
            return "assets/textures/papertray.png"; // fallback texture
        }

        StoreItem storeItem = item.getComponent(StoreItemComponent.class).getStoreItem();
        String foodType = storeItem.getDescription().toLowerCase().trim();

        switch (foodType) {
            case "quekquek": return "assets/textures/papertray_quekquek.png";
            case "hotdog": return "assets/textures/papertray_hotdog.png";
            case "tempura": return "assets/textures/papertray_tempura.png";
            case "calamansi juice": return "assets/textures/papertray_calamansi_juice.png";
            default: return "assets/textures/papertray.png";
        }
    }

    private void changeTrayTexture(String imagePath) {
        ImageView newView = new ImageView(new Image(imagePath));
        newView.setFitWidth(entity.getWidth());
        newView.setFitHeight(entity.getHeight());
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(newView);
    }

    private void onRelease(MouseEvent event) {
        boolean served = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.CONTAINER).stream()
                .anyMatch(e -> e.isColliding(entity));

        if (!served) {
            // Snap back to original position and reset texture
            entity.setPosition(trayOriginalPos);
//            resetTrayTexture();
        } else {
            // Remove the bound item but keep the texture changed
            FXGL.getGameTimer().runOnceAfter(() -> {
                if (boundItem != null) {
                    boundItem.removeFromWorld();
                    boundItem = null;
                    textureChanged = false; // Allow new items to be bound
                }
            }, Duration.seconds(0.2));
        }

        if (collidableComponent != null) {
            entity.addComponent(collidableComponent);
        }
    }

//    private void resetTrayTexture() {
//        if (textureChanged) {
//            changeTrayTexture("assets/textures/papertray.png");
//            textureChanged = false;
//        }
//    }

    public PaperTray getTray() {
        return paperTray;
    }
}