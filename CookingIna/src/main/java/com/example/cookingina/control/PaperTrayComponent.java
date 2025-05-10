//package com.example.cookingina.control;
//
//import com.almasb.fxgl.dsl.FXGL;
//import com.almasb.fxgl.dsl.components.DraggableComponent;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.entity.component.Component;
//import com.almasb.fxgl.entity.components.CollidableComponent;
//import com.example.cookingina.CookingInaMain;
//import com.example.cookingina.objects.entity.PaperTray;
//import com.example.cookingina.objects.entity.StoreItem;
//import customers.CustomerComponent;
//import customers.Order;
//import customers.SpeechBubbleComponent;
//import javafx.animation.FadeTransition;
//import javafx.animation.TranslateTransition;
//import javafx.geometry.Point2D;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.scene.paint.Color;
//import javafx.scene.input.MouseEvent;
//import javafx.util.Duration;
//
//
//public class PaperTrayComponent extends Component {
//    private final PaperTray paperTray;
//    private Point2D trayOriginalPos;
//    private Point2D itemOriginalPos;
//    private Point2D itemOffset;
//    private Point2D dragOffset;
//    private Entity boundItem;
//    private boolean isOccupied = false;
//    private boolean wasItemBurnt = false;
//
//
//    private boolean textureChanged = false;
//    private CollidableComponent collidableComponent;
//
//    public PaperTrayComponent(PaperTray paperTray) {
//        this.paperTray = paperTray;
//        this.isOccupied = false; // Default to unoccupied
//    }
//
//    @Override
//    public void onAdded() {
//        entity.addComponent(new CollidableComponent(true));
//        entity.addComponent(new DraggableComponent());
//        trayOriginalPos = entity.getPosition();
//
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onPress);
//        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onRelease);
//    }
//
//    private void onPress(MouseEvent e) {
//        dragOffset = new Point2D(e.getSceneX(), e.getSceneY()).subtract(entity.getPosition());
//        collidableComponent = entity.getComponent(CollidableComponent.class);
//        entity.removeComponent(CollidableComponent.class);
//    }
//
//    @Override
//    public void onUpdate(double tpf) {
//        if (boundItem == null && !textureChanged) {
//            FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.INGREDIENT).stream()
//                    .filter(e -> e.isColliding(entity))
//                    .filter(this::isValidItem)
//                    .findFirst()
//                    .ifPresent(this::bindItem);
//        }
//    }
//
//    private boolean isValidItem(Entity item) {
//        boolean cooked     = item.hasComponent(OvercookComponent.class)
//                && !item.getComponent(OvercookComponent.class).getIsBurnt();
//        boolean overcooked = item.hasComponent(OvercookComponent.class)
//                && item.getComponent(OvercookComponent.class).getIsBurnt();
//        return cooked || overcooked;
//    }
//
//    private void bindItem(Entity item) {
//        boundItem = item;
//        itemOriginalPos = item.getPosition();
//        itemOffset = itemOriginalPos.subtract(entity.getPosition());
//
//        String texturePath = getTextureForItem(item);
//        changeTrayTexture(texturePath);
//
//        setIsOccupied(true);
//        paperTray.setOccupied(true);
//        textureChanged = true;
//
//        // Capture burnt state when binding the item
//        if (item.hasComponent(OvercookComponent.class)) {
//            OvercookComponent oc = item.getComponent(OvercookComponent.class);
//            wasItemBurnt = oc.getIsBurnt();
//            oc.getEquipment().setOccupied(false);
//        } else {
//            wasItemBurnt = false;
//        }
//
//        FXGL.getGameWorld().removeEntity(item);
//    }
//
//    private String getTextureForItem(Entity item) {
//        StoreItem s = item.getComponent(StoreItemComponent.class).getStoreItem();
//
//        // If it's burnt
//        if (item.hasComponent(OvercookComponent.class)
//                && item.getComponent(OvercookComponent.class).getIsBurnt()) {
//
//            switch (s.getBurntResource().toLowerCase().trim()) {
//                case "overcooked_quekquek.png":  return "papertray_overcooked_quekquek.png";
//                case "overcooked_hotdog.png":    return "papertray_overcooked_hotdog.png";
//                case "overcooked_tempura.png":   return "papertray_overcooked_tempura.png";
//                default:                         return "papertray_overcooked_generic.png";
//            }
//        }
//
//        // Otherwise cooked
//        switch (s.getCookedResource().toLowerCase().trim()) {
//            case "cooked_quekquek.png":    return "papertray_cooked_quekquek.png";
//            case "cooked_hotdog.png":      return "papertray_cooked_hotdog.png";
//            case "cooked_tempura.png":     return "papertray_cooked_tempura.png";
//            case "calamansi_juice.png":    return "papertray_calamansi_juice.png";
//            default:                       return "papertray.png";
//        }
//    }
//
//    private void onRelease(MouseEvent event) {
//        boolean served = false;
//
//        // Temporarily re-add CollidableComponent for collision checks
//        boolean wasCollidableTemporarilyAdded = false;
//        if (collidableComponent != null && !entity.hasComponent(CollidableComponent.class)) {
//            entity.addComponent(collidableComponent);
//            wasCollidableTemporarilyAdded = true;
//        }
//
//        for (Entity customer : FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.CUSTOMER)) {
//            if (customer.isColliding(entity) && textureChanged) {
//                served = true;
//
//                // Get the item name from the texture (e.g., "cooked_quekquek")
//                String servedItemName = getServedItemNameFromTexture();
//                if (servedItemName.isEmpty()) break;
//
//                CustomerComponent customerComponent = customer.getComponent(CustomerComponent.class);
//                SpeechBubbleComponent speechBubble = customer.getComponent(SpeechBubbleComponent.class);
//
//                // Match order by item name (e.g., "cooked_quekquek")
//                Order matchedOrder = customerComponent.getOrders().stream()
//                        .filter(o -> o.getItem().equals(servedItemName))
//                        .findFirst()
//                        .orElse(null);
//
//                if (matchedOrder != null) {
//                    if (matchedOrder.getQuantity() > 1) {
//                        matchedOrder.decrement();
//                    } else {
//                        customerComponent.getOrders().remove(matchedOrder);
//                        speechBubble.markServed(servedItemName);
//                    }
//
//                    // Handle payment/deduction
//                    serveCustomer(customer, servedItemName); // Pass item name to serveCustomer
//
//                    resetTrayTexture();
//                    setIsOccupied(false);
//                    paperTray.setOccupied(false);
//                }
//                break;
//            }
//        }
//
//        if (wasCollidableTemporarilyAdded) {
//            entity.removeComponent(CollidableComponent.class);
//        }
//
//        if (!served) {
//            entity.setPosition(trayOriginalPos);
//        }
//
//        if (collidableComponent != null) {
//            entity.addComponent(collidableComponent);
//        }
//
//        FXGL.getGameTimer().runOnceAfter(() -> {
//            if (boundItem != null) {
//                boundItem.removeFromWorld();
//                boundItem = null;
//            }
//        }, Duration.seconds(0.2));
//    }
//
//    private void changeTrayTexture(String imageName) {
//        Image fxglImage = FXGL.image(imageName);
//        ImageView newView = new ImageView(fxglImage);
//        newView.setFitWidth(entity.getWidth());
//        newView.setFitHeight(entity.getHeight());
//
//        entity.getViewComponent().clearChildren();
//        entity.getViewComponent().addChild(newView);
//    }
//
//    private void serveCustomer(Entity customer, String servedItemName) {
//        CustomerComponent cc = customer.getComponent(CustomerComponent.class);
//
//        customer.getComponentOptional(SpeechBubbleComponent.class).ifPresent(sb -> {
//            int price = getPrice(servedItemName);
//            if (wasItemBurnt) {
//                sb.showDeductionPopup(price); // Deduct full price for burnt items
//            } else {
//                sb.showPricePopup(price); // Add adjusted price based on patience
//            }
//        });
//    }
//
//    private int getPrice(String itemName) {
//        switch (itemName) {
//            case "cooked_quekquek": return 10;
//            case "cooked_hotdog":   return 15;
//            case "cooked_tempura":  return 12;
//            case "calamansi_juice": return 8;
//            default: return 0;
//        }
//    }
//
//    private int getPrice(StoreItem item) {
//        if (item == null) return 0;  // In case no valid item was passed
//
//        return item.getSellingPrice().intValue();  // Return the selling price as an integer
//    }
//
//
//    private String getServedItemNameFromTexture() {
//        if (!textureChanged) return "";
//
//        ImageView iv = (ImageView) entity.getViewComponent().getChildren().get(0);
//        String url = iv.getImage().getUrl().toLowerCase();
//
//        if (url.contains("overcooked_quekquek")) return "cooked_quekquek";
//        if (url.contains("overcooked_hotdog"))   return "cooked_hotdog";
//        if (url.contains("overcooked_tempura"))  return "cooked_tempura";
//        if (url.contains("cooked_quekquek"))     return "cooked_quekquek";
//        if (url.contains("cooked_hotdog"))       return "cooked_hotdog";
//        if (url.contains("cooked_tempura"))      return "cooked_tempura";
//        if (url.contains("calamansi_juice"))     return "calamansi_juice";
//
//        return "";
//    }
//
//
//    private void resetTrayTexture() {
//        if (textureChanged) {
//            changeTrayTexture("papertray.png");
//            textureChanged = false;
//        }
//    }
//
//    private void setIsOccupied(boolean isOccupied) {
//        this.isOccupied = isOccupied;
//    }
//
//    public PaperTray getTray() {
//        return paperTray;
//    }
//
//    public boolean getIsOccupied() {
//        return isOccupied;
//    }
//}

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
    private Point2D dragOffset;
    private Entity boundItem;
    private boolean wasItemBurnt = false;
    private boolean textureChanged = false;
    private CollidableComponent collidableComponent;

    public PaperTrayComponent(PaperTray paperTray) {
        this.paperTray = paperTray;
    }

    @Override
    public void onAdded() {
        entity.addComponent(new CollidableComponent(true));
        entity.addComponent(new DraggableComponent());
        trayOriginalPos = entity.getPosition();

        entity.getViewComponent()
                .addEventHandler(MouseEvent.MOUSE_PRESSED, this::onPress);
        entity.getViewComponent()
                .addEventHandler(MouseEvent.MOUSE_RELEASED, this::onRelease);
    }

    private void onPress(MouseEvent e) {
        dragOffset = new Point2D(e.getSceneX(), e.getSceneY())
                .subtract(entity.getPosition());
        collidableComponent = entity.getComponent(CollidableComponent.class);
        entity.removeComponent(CollidableComponent.class);
    }

    @Override
    public void onUpdate(double tpf) {
        if (boundItem == null && !textureChanged) {
            FXGL.getGameWorld()
                    .getEntitiesByType(CookingInaMain.EntityType.INGREDIENT)
                    .stream()
                    .filter(e -> e.isColliding(entity) && e.hasComponent(OvercookComponent.class))
                    .findFirst()
                    .ifPresent(this::bindItem);
        }
    }

    private void bindItem(Entity item) {
        boundItem = item;
        OvercookComponent oc = item.getComponent(OvercookComponent.class);
        wasItemBurnt = oc.getIsBurnt();

        // Swap to cooked or overcooked tray texture
        String texture = wasItemBurnt
                ? getOvercookedTexture(item)
                : getCookedTexture(item);
        changeTrayTexture(texture);

        textureChanged = true;
        paperTray.setOccupied(true);

        // Free up the cooking equipment and remove the ingredient
        oc.getEquipment().setOccupied(false);
        FXGL.getGameWorld().removeEntity(item);
    }

    private void onRelease(MouseEvent event) {
        boolean served = false;

        // Restore collidable for this check
        if (collidableComponent != null
                && !entity.hasComponent(CollidableComponent.class)) {
            entity.addComponent(collidableComponent);
        }

        // Attempt to serve any overlapping customer
        for (Entity customer : FXGL.getGameWorld()
                .getEntitiesByType(CookingInaMain.EntityType.CUSTOMER)) {
            if (customer.isColliding(entity) && textureChanged) {
                served = true;

                String servedItem = getServedItemFromTexture();
                CustomerComponent cc = customer.getComponent(CustomerComponent.class);
                SpeechBubbleComponent sb = customer.getComponent(SpeechBubbleComponent.class);

                // 1) Find matching Order
                Order order = cc.getOrders().stream()
                        .filter(o -> o.getItem().equals(servedItem))
                        .findFirst()
                        .orElse(null);

                if (order != null) {
                    // 2) Decrement/remove
                    order.decrement();
                    if (order.getQuantity() <= 0) {
                        cc.getOrders().remove(order);
                    }

                    // 3) Remove one icon
                    sb.markServed(servedItem);

                    // 4) Show popup
                    int price = getPrice(servedItem);
                    if (wasItemBurnt) {
                        sb.showDeductionPopup(price);
                    } else {
                        sb.showPricePopup(price);
                    }
                }

                // 5) Reset the tray
                resetTray();
                break;
            }
        }

        // Snap back if not served
        if (!served) {
            entity.setPosition(trayOriginalPos);
        }

        // Delay removal of boundItem to allow texture change to display
        if (boundItem != null) {
            FXGL.getGameTimer().runOnceAfter(() -> {
                boundItem.removeFromWorld();
                boundItem = null;
            }, Duration.seconds(0.2));
        }
    }

    private void resetTray() {
        changeTrayTexture("papertray.png");
        textureChanged = false;
        paperTray.setOccupied(false);
    }

    private String getServedItemFromTexture() {
        ImageView iv = (ImageView) entity.getViewComponent()
                .getChildren().get(0);
        String url = iv.getImage().getUrl().toLowerCase();
        if (url.contains("quekquek"))         return "cooked_quekquek";
        if (url.contains("hotdog"))           return "cooked_hotdog";
        if (url.contains("tempura"))          return "cooked_tempura";
        if (url.contains("calamansi_juice"))  return "calamansi_juice";
        return "";
    }

    private int getPrice(String itemName) {
        switch (itemName) {
            case "cooked_quekquek": return 10;
            case "cooked_hotdog":   return 15;
            case "cooked_tempura":  return 12;
            case "calamansi_juice": return 8;
            default:                return 0;
        }
    }

    private String getCookedTexture(Entity item) {
        StoreItem s = item.getComponent(StoreItemComponent.class).getStoreItem();
        switch (s.getCookedResource().toLowerCase().trim()) {
            case "cooked_quekquek.png": return "papertray_cooked_quekquek.png";
            case "cooked_hotdog.png":   return "papertray_cooked_hotdog.png";
            case "cooked_tempura.png":  return "papertray_cooked_tempura.png";
            case "calamansi_juice.png": return "papertray_calamansi_juice.png";
            default:                    return "papertray.png";
        }
    }

    private String getOvercookedTexture(Entity item) {
        StoreItem s = item.getComponent(StoreItemComponent.class).getStoreItem();
        switch (s.getBurntResource().toLowerCase().trim()) {
            case "overcooked_quekquek.png": return "papertray_overcooked_quekquek.png";
            case "overcooked_hotdog.png":   return "papertray_overcooked_hotdog.png";
            case "overcooked_tempura.png":  return "papertray_overcooked_tempura.png";
            default:                        return "papertray_overcooked_generic.png";
        }
    }

    private void changeTrayTexture(String imageName) {
        Image img = FXGL.image(imageName);
        ImageView iv = new ImageView(img);
        iv.setFitWidth(entity.getWidth());
        iv.setFitHeight(entity.getHeight());
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(iv);
    }

    public PaperTray getTray(){
        return paperTray;
    }
}
