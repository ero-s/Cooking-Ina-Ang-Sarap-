package com.example.cookingina.objects.entity.equipment;

import com.example.cookingina.objects.entity.Equipment;

public class Fryer extends Equipment {
    public Fryer(String emptyResource, String usedResource, int type, int playend, double speedMultiplier,
                 double cost, int capacity, boolean isUnlocked, String description, int textureWidth, int textureHeight) {
        super(emptyResource, usedResource, type, playend, speedMultiplier, cost, capacity, isUnlocked, description, textureWidth, textureHeight);
    }
}
