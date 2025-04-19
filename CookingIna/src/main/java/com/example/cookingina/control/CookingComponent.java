package com.example.cookingina.control;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.ProgressBar;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;
import javafx.scene.paint.Color;

public class CookingComponent extends Component {
    private double timer;
    private final double totalTime;
    private final StoreItem cookedStoreItem;
    private final Equipment equipment;
    private ProgressBar progressBar;

    public CookingComponent(double preparationTime, StoreItem cookedStoreItem, Equipment equipment) {
        this.totalTime = preparationTime;
        this.timer = preparationTime;
        this.cookedStoreItem = cookedStoreItem;
        this.equipment = equipment;
    }

    @Override
    public void onAdded() {
        // Create and attach progress bar
        progressBar = new ProgressBar();
        progressBar.setWidth(50);
        progressBar.setHeight(10);
        progressBar.setTranslateY(-20); // Position above entity
        progressBar.setTranslateX(-20); // Position above entity
        progressBar.setFill(Color.LIMEGREEN);

        entity.getViewComponent().addChild(progressBar);
    }

    @Override
    public void onUpdate(double tpf) {
        timer -= tpf;

        // Update progress (reverse calculation: 1.0 -> 0.0)
        double progress = totalTime - timer;
        progressBar.setCurrentValue(progress); // If using 0-100 scale
        progressBar.setMinValue(0);
        progressBar.setMaxValue(totalTime);

        if(timer <= 0) {
            // Replace texture by updating the view
            entity.getViewComponent().clearChildren();
            entity.getViewComponent().addChild(FXGL.texture(cookedStoreItem.getCookedResource(), 40, 40));
            // Remove raw ingredient
            entity.removeFromWorld();

//            FXGL.play("cooking-done.wav");
        }
    }

    @Override
    public void onRemoved() {
        double currX = entity.getX();
        double currY = entity.getY();
        entity.getViewComponent().removeChild(progressBar);
        UIController.spawnCookedIngredient(cookedStoreItem,equipment, currX, currY);
    }
}
