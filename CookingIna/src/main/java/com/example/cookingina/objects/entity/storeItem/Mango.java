package com.example.cookingina.objects.entity.storeItem;

import com.example.cookingina.objects.entity.StoreItem;

public class Mango extends StoreItem {
    public Mango(String container, String rawResource, String cookedResource, String burntResource, String description, Double preparationTime, Double sellingPrice, Double discardCost, int status, int height, int width) {
        super(container, rawResource, cookedResource, burntResource, description, preparationTime, sellingPrice, discardCost, status, height, width);
    }
}