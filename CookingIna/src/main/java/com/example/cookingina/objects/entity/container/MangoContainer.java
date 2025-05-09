package com.example.cookingina.objects.entity.container;

import com.example.cookingina.objects.entity.Container;

public class MangoContainer extends Container {
    public boolean isUnlocked;
    public boolean isOccupied;

    public MangoContainer(
            String rawResource,
            String foodItemResource,
            String description,
            boolean isUnlocked,
            boolean isOccupied
    ) {
        super(rawResource, foodItemResource, description);
        this.isUnlocked = isUnlocked;
        this.isOccupied = isOccupied;
    }

    public void setUnclocked(boolean unclocked) {
        this.isUnlocked = unclocked;
    }

    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public boolean isOccupied() {
        return isOccupied;
    }
}