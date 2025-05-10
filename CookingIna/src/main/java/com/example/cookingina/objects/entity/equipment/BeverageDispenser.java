package com.example.cookingina.objects.entity.equipment;

import com.example.cookingina.objects.entity.ContainerType;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;

public class BeverageDispenser extends Equipment implements ContainerType {
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
        item.setIsJuice(isJuice());
    }

    @Override
    public StoreItem getItem(){
        return item;
    }

    @Override
    public boolean isJuice() {
        return true;
    }
}