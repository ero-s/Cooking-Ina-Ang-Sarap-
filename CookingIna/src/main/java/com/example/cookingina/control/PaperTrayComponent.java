package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
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
    private boolean isOccupied = false;

    private boolean textureChanged = false;
    private CollidableComponent collidableComponent;

    public PaperTrayComponent(PaperTray paperTray) {
        this.paperTray = paperTray;
        this.isOccupied = false; // Default to unoccupied
    }

    @Override
    public void onAdded() {
        entity.addComponent(new CollidableComponent(true));
        entity.addComponent(new DraggableComponent());
        trayOriginalPos = entity.getPosition();

        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onPress);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onRelease);
    }

    private void onPress(MouseEvent e) {
        dragOffset = new Point2D(e.getSceneX(), e.getSceneY()).subtract(entity.getPosition());
        collidableComponent = entity.getComponent(CollidableComponent.class);
        entity.removeComponent(CollidableComponent.class);
    }

    @Override
    public void onUpdate(double tpf) {
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

        String texturePath = getTextureForItem(item);
        changeTrayTexture(texturePath);

        setIsOccupied(true);
        paperTray.setOccupied(true);
        textureChanged = true;

        if (item.hasComponent(OvercookComponent.class)) {
            OvercookComponent oc = item.getComponent(OvercookComponent.class);
            oc.getEquipment().setOccupied(false);
        }
        FXGL.getGameWorld().removeEntity(item);
    }

    private String getTextureForItem(Entity item) {
        if (!item.hasComponent(StoreItemComponent.class)) {
            System.err.println("Item missing StoreItemComponent: " + item);
            return "assets/textures/papertray.png";
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
                String servedItem = getServedItemFromTexture();

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

                    resetTrayTexture();
                    setIsOccupied(false);
                    paperTray.setOccupied(false);

                    FXGL.inc("income", getPrice(servedItem));
                    break;
                }
            }
        }

        if (!served) {
            entity.setPosition(trayOriginalPos);
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
            case "cooked_kwek-kwek": return 10;
            case "cooked_hotdog": return 15;
            case "cooked_tempura": return 12;
            case "calamansi_juice": return 8;
            default: return 0;
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

    public boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }
}