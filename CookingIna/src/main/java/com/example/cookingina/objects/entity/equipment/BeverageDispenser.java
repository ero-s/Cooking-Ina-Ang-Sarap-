package com.example.cookingina.objects.entity.equipment;

import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;

public class BeverageDispenser extends Equipment {
    private final StoreItem item;

    public BeverageDispenser(
            StoreItem item,
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

        super(emptyResource,usedResource, type, speedMultiplier, cost, capacity, isUnlocked, description);
        this.item = item;
    }

    public StoreItem getItem(){
        return item;
    }
}