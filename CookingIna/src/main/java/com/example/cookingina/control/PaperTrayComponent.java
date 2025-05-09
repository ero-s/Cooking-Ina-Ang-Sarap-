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
            return "/assets/textures/papertray.png";
        }

        String food = item.getComponent(StoreItemComponent.class)
                .getStoreItem().getDescription()
                .toLowerCase().replace(" ", "_");

        switch (food) {
            case "quekquek":         return "/assets/textures/papertray_cooked_quekquek.png";
            case "hotdog":           return "/assets/textures/papertray_hotdog.png";
            case "tempura":          return "/assets/textures/papertray_tempura.png";
            case "calamansi_juice":  return "/assets/textures/papertray_calamansi_juice.png";
            default:                 return "/assets/textures/papertray.png";
        }
    }

    private void changeTrayTexture(String imagePath) {
        ImageView iv = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        iv.setFitWidth(entity.getWidth());
        iv.setFitHeight(entity.getHeight());
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(iv);
    }

    private void onRelease(MouseEvent event) {
        boolean handled = false;

        // 1) Serve to Customer
        for (Entity customer :
                FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.CUSTOMER)) {
            if (customer.isColliding(entity) && textureChanged) {
                handled = true;
                serveCustomer(customer);
                break;
            }
        }

        // 2) If not served, check Equipment or Container
        if (!handled) {
            boolean onEquipment = FXGL.getGameWorld()
                    .getEntitiesByType(CookingInaMain.EntityType.EQUIPMENT)
                    .stream().anyMatch(e -> e.isColliding(entity));
            boolean onContainer = FXGL.getGameWorld()
                    .getEntitiesByType(CookingInaMain.EntityType.CONTAINER)
                    .stream().anyMatch(e -> e.isColliding(entity));

            if (onEquipment || onContainer) {
                // Snap back but do NOT reset texture
                entity.setPosition(trayOriginalPos);
                handled = true;
            }
        }

        // 3) Anything else: snap back AND reset
        if (!handled) {
            entity.setPosition(trayOriginalPos);
            resetTrayTexture();
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
                FXGL.inc("income", price);
                sb.showPricePopup(price);

                // only here do we reset texture
                resetTrayTexture();
            }
        });
    }


    private int getPrice(String itemName) {
        switch (itemName) {
            case "cooked_kwek-kwek": return 10;
            case "cooked_hotdog":     return 15;
            case "cooked_tempura":    return 12;
            case "calamansi_juice":   return 8;
            default:                  return 0;
        }
    }

    private String getServedItemFromTexture() {
        if (!textureChanged) return "";

        ImageView iv = (ImageView)entity.getViewComponent().getChildren().get(0);
        String url = iv.getImage().getUrl();

        if (url.contains("quekquek"))      return "cooked_kwek-kwek";
        if (url.contains("hotdog"))        return "cooked_hotdog";
        if (url.contains("tempura"))       return "cooked_tempura";
        if (url.contains("calamansi_juice")) return "calamansi_juice";

        return "";
    }

    private void resetTrayTexture() {
        if (textureChanged) {
            changeTrayTexture("/assets/textures/papertray.png");
            textureChanged = false;
        }
    }

    public PaperTray getTray() {
        return paperTray;
    }
}
