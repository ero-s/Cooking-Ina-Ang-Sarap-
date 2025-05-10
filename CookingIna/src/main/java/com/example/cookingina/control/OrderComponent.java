package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.Equipment;
import customers.CustomerComponent;
import customers.Order;
import customers.SpeechBubbleComponent;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.Iterator;

public class OrderComponent extends Component {

    private final Equipment equipment;
    private Point2D trayOriginalPos;

    public OrderComponent(Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public void onAdded() {
        // Enable collisions and dragging
        entity.addComponent(new CollidableComponent(true));
        entity.addComponent(new DraggableComponent());
        trayOriginalPos = entity.getPosition();

        // Listen for release to attempt serving
        entity.getViewComponent()
                .addEventHandler(MouseEvent.MOUSE_RELEASED, this::onRelease);
    }

    private void onRelease(MouseEvent event) {
        boolean served = false;

        for (Entity customer : FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.CUSTOMER)) {
            if (customer.isColliding(entity) && customer.hasComponent(SpeechBubbleComponent.class)) {
                served = true;

                // Free the equipment immediately
                equipment.setOccupied(false);

                // Serve one matching order, update UI and income
                serveCustomer(customer);

                // Remove this ready-item entity
                entity.removeFromWorld();

                // Also remove any cooking entity tied to this equipment
                FXGL.getGameWorld()
                        .getEntitiesByComponent(CookingComponent.class)
                        .stream()
                        .filter(e -> e.getComponent(CookingComponent.class).getEquipment() == equipment)
                        .forEach(Entity::removeFromWorld);

                break;
            }
        }

        if (!served) {
            // Snap back if not served
            entity.setPosition(trayOriginalPos);

            // Re-enable collision
            entity.addComponent(new CollidableComponent(true));
        }
    }

    private void serveCustomer(Entity customer) {
        String servedItem = getServedItemFromTexture();
        CustomerComponent cc = customer.getComponent(CustomerComponent.class);
        SpeechBubbleComponent sb = customer.getComponent(SpeechBubbleComponent.class);

        for (Iterator<Order> it = cc.getOrders().iterator(); it.hasNext(); ) {
            Order o = it.next();
            if (o.getItem().equals(servedItem)) {
                if (o.getQuantity() > 1) {
                    o.decrement();
                } else {
                    it.remove();
                }

                sb.markServed(servedItem);
                int price = getPrice(servedItem);
                FXGL.inc("income", price);
                sb.showPricePopup(price);
                break;
            }
        }
    }

    private int getPrice(String itemName) {
        switch (itemName) {
            case "mango_ready":
            case "calamansiJuice_finishedProduct":
            case "bukoJuice_finishedProduct":
            case "orangeJuice_finishedProduct":
            case "halo_halo":
                return 15;
            default:
                return 0;
        }
    }

    private String getServedItemFromTexture() {
        ImageView iv = (ImageView) entity.getViewComponent().getChildren().get(0);
        String url = iv.getImage().getUrl();

        if (url.contains("calamansiJuice_finishedProduct"))     return "calamansiJuice_finishedProduct";
        if (url.contains("bukoJuice_finishedProduct"))          return "bukoJuice_finishedProduct";
        if (url.contains("orangeJuice_finishedProduct"))        return "orangeJuice_finishedProduct";
        if (url.contains("mango_ready"))                        return "mango_ready";
        if (url.contains("halo_halo"))                        return "halo_halo";

        return "";
    }
}