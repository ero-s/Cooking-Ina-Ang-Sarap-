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
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
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

//    private boolean isValidItem(Entity item) {
//        boolean cooked = item.hasComponent(CookingComponent.class)
//                && item.getComponent(CookingComponent.class).getIsCooked();
//        boolean notBurnt = item.hasComponent(OvercookComponent.class)
//                && !item.getComponent(OvercookComponent.class).getIsBurnt();
//        return cooked || notBurnt;
//    }

    private boolean isValidItem(Entity item) {
        boolean cooked     = item.hasComponent(CookingComponent.class)
                && item.getComponent(CookingComponent.class).getIsCooked();
        boolean overcooked = item.hasComponent(OvercookComponent.class)
                && item.getComponent(OvercookComponent.class).getIsBurnt();
        return cooked || overcooked;
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

//    private String getTextureForItem(Entity item) {
//        if (!item.hasComponent(StoreItemComponent.class)) {
//            System.err.println("Item missing StoreItemComponent: " + item);
//            return "papertray.png";
//        }
//
//        StoreItem storeItem = item.getComponent(StoreItemComponent.class).getStoreItem();
//        String foodType = storeItem.getDescription().toLowerCase().trim();
//
//        switch (foodType) {
//            case "quekquek": return "papertray_cooked_quekquek.png";
//            case "hotdog": return "papertray_cooked_hotdog.png";
//            case "tempura": return "papertray_cooked_tempura.png";
//            case "calamansi juice": return "papertray_calamansi_juice.png";
//            default: return "papertray.png";
//        }
//    }

    private String getTextureForItem(Entity item) {
        StoreItem s = item.getComponent(StoreItemComponent.class).getStoreItem();

        // If it's burnt
        if (item.hasComponent(OvercookComponent.class)
                && item.getComponent(OvercookComponent.class).getIsBurnt()) {

            switch (s.getBurntResource().toLowerCase().trim()) {
                case "overcooked_quekquek.png":  return "papertray_overcooked_quekquek.png";
                case "overcooked_hotdog.png":    return "papertray_overcooked_hotdog.png";
                case "overcooked_tempura.png":   return "papertray_overcooked_tempura.png";
                default:                         return "papertray_overcooked_generic.png";
            }
        }

        // Otherwise cooked
        switch (s.getCookedResource().toLowerCase().trim()) {
            case "cooked_quekquek.png":    return "papertray_cooked_quekquek.png";
            case "cooked_hotdog.png":      return "papertray_cooked_hotdog.png";
            case "cooked_tempura.png":     return "papertray_cooked_tempura.png";
            case "calamansi_juice.png":    return "papertray_calamansi_juice.png";
            default:                       return "papertray.png";
        }
    }

    private void changeTrayTexture(String imageName) {
        Image fxglImage = FXGL.image(imageName);
        ImageView newView = new ImageView(fxglImage);
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
                serveCustomer(customer);
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

        // restore collidability
        if (collidableComponent != null) {
            entity.addComponent(collidableComponent);
        }

        // remove bound item after a short delay
        FXGL.getGameTimer().runOnceAfter(() -> {
            if (boundItem != null) {
                boundItem.removeFromWorld();
                boundItem = null;
            }
        }, Duration.seconds(0.2));
    }

    private void serveCustomer(Entity customer) {
        String servedItem = getServedItemFromTexture();
        CustomerComponent cc = customer.getComponent(CustomerComponent.class);

        customer.getComponentOptional(SpeechBubbleComponent.class).ifPresent(sb -> {
            Order toHandle = cc.getOrders().stream()
                    .filter(o -> o.getItem().equals(servedItem))
                    .findFirst().orElse(null);

            if (toHandle != null) {
                if (toHandle.getQuantity() > 1) {
                    toHandle.decrement();
                } else {
                    cc.getOrders().remove(toHandle);
                }

                sb.markServed(servedItem);

                int price = getPrice(servedItem);
                sb.showPricePopup(price);

                // only here do we reset texture
                resetTrayTexture();
            }
        });
    }


    private int getPrice(String itemName) {
        switch (itemName) {
            case "cooked_quekquek": return 10;
            case "cooked_hotdog": return 15;
            case "cooked_tempura": return 12;
            case "calamansi_juice": return 8;
            default: return 0;
        }
    }


    private String getServedItemFromTexture() {
        if (!textureChanged) return "";

        ImageView iv = (ImageView)entity.getViewComponent().getChildren().get(0);
        String url = iv.getImage().getUrl();

        if (url.contains("quekquek"))      return "cooked_quekquek";
        if (url.contains("hotdog"))        return "cooked_hotdog";
        if (url.contains("tempura"))       return "cooked_tempura";
        if (url.contains("calamansi_juice")) return "calamansi_juice";

        return "";
    }

    private void resetTrayTexture() {
        if (textureChanged) {
            changeTrayTexture("papertray.png");
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