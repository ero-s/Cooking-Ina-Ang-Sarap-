package com.example.cookingina.objects.entity.equipment;

import com.example.cookingina.objects.entity.Equipment;

public class BeverageDispenser extends Equipment {
    public BeverageDispenser(
        String emptyResource,
        String usedResource,
        int type,
        int playend,
        double speedMultiplier,
        double cost,
        int capacity,
        boolean isUnlocked,
        String description
    ) {
        super(emptyResource,usedResource, type, playend, speedMultiplier, cost, capacity, isUnlocked, description);
    }
}
