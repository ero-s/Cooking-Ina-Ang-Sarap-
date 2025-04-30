package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.ui.Position;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.objects.entity.PaperTray;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

import java.nio.file.attribute.PosixFileAttributes;

public class PaperTrayComponent extends Component {
    private PaperTray paperTray;
    private boolean isOccupied = false;
    private boolean isServed = false;
    private Point2D paperTrayPosition;
    public PaperTrayComponent(PaperTray paperTray) {
        this.paperTray = paperTray;
    }

    @Override
    public void onAdded() {
        entity.addComponent(new CollidableComponent(true));
        entity.addComponent(new DraggableComponent());
        paperTrayPosition = new Point2D(entity.getX(), entity.getY());
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            isServed = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.CONTAINER).stream()
                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));

            if(!isServed) {
                entity.setPosition(paperTrayPosition);
            }
        });




    }
    public void onUpdate() {
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            isServed = FXGL.getGameWorld().getEntitiesByType(CookingInaMain.EntityType.CONTAINER).stream()
                    .anyMatch(e -> e.getBoundingBoxComponent().isCollidingWith(entity.getBoundingBoxComponent()));
            if(!isServed) {
                entity.setPosition(paperTrayPosition);
            }
        });

    }

    public void onRemove() {}

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}
