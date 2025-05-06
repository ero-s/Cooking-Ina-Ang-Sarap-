package com.example.cookingina.objects.entity.equipment;

import com.example.cookingina.objects.entity.Equipment;

public class TrashBin extends Equipment {
    public TrashBin(String emptyResource, String usedResource, int type, double speedMultiplier, double cost, int capacity, boolean isUnlocked, String description) {
        super(emptyResource, usedResource, type, speedMultiplier, cost, capacity, isUnlocked, description);
    }
}
