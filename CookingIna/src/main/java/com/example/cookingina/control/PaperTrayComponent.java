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
