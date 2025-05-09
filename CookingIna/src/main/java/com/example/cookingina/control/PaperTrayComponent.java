package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.PaperTray;
import com.example.cookingina.objects.entity.StoreItem;
import customers.CustomerComponent;
import customers.Order;
import customers.SpeechBubbleComponent;
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
        boolean served = false;

        for (Entity customer : FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.CUSTOMER)) {
            if (customer.isColliding(entity) && textureChanged) {
                served = true;

                // Identify the item served
                String servedItem = getServedItemFromTexture();

                // Access customer component and orders
                CustomerComponent customerComponent = customer.getComponent(CustomerComponent.class);
                SpeechBubbleComponent speechBubble = customer.getComponent(SpeechBubbleComponent.class);

                Order matchedOrder = null;
                for (Order order : customerComponent.getOrders()) {
                    if (order.getItem().equals(servedItem)) {
                        matchedOrder = order;
                        break;
                    }
                }

                if (matchedOrder != null) {
                    if (matchedOrder.getQuantity() > 1) {
                        matchedOrder.decrement();
                    } else {
                        customerComponent.getOrders().remove(matchedOrder);
                        speechBubble.markServed(servedItem);
                    }

                    // Revert tray texture
                    resetTrayTexture();

                    // ✅ Increment income for served item
                    FXGL.inc("income", getPrice(servedItem));

                    // Optional: remove customer or mark as done
                    if (customerComponent.getOrders().isEmpty()) {
                        // FXGL.getGameWorld().removeEntity(customer); // if needed
                    }

                    break; // only serve one customer per release
                }
            }
        }

        if (!served) {
            // Snap back to original position and reset texture
            entity.setPosition(trayOriginalPos);
//            resetTrayTexture();
        }

        if (collidableComponent != null) {
            entity.addComponent(collidableComponent);
        }

        FXGL.getGameTimer().runOnceAfter(() -> {
            if (boundItem != null) {
                boundItem.removeFromWorld();
                boundItem = null;
            }
        }, Duration.seconds(0.2));
    }

    private int getPrice(String itemName) {
        switch (itemName) {
            case "cooked_kwek-kwek":
                return 10;
            case "cooked_hotdog":
                return 15;
            case "cooked_tempura":
                return 12;
            case "calamansi_juice":
                return 8;
            default:
                return 0;
        }
    }


    private String getServedItemFromTexture() {
        if (!textureChanged) return "";

        ImageView iv = (ImageView) entity.getViewComponent().getChildren().get(0);
        String url = iv.getImage().getUrl();

        if (url.contains("quekquek")) return "cooked_kwek-kwek";
        if (url.contains("hotdog")) return "cooked_hotdog";
        if (url.contains("tempura")) return "cooked_tempura";
        if (url.contains("calamansi_juice")) return "calamansi_juice";

        return "";
    }


    private void resetTrayTexture() {
        if (textureChanged) {
            changeTrayTexture("assets/textures/papertray.png");
            textureChanged = false;
        }
    }

    public PaperTray getTray() {
        return paperTray;
    }
}