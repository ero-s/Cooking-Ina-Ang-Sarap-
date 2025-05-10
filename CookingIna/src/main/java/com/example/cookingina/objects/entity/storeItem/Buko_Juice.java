package com.example.cookingina.objects.entity.storeItem;

import com.example.cookingina.objects.entity.StoreItem;

public class Buko_Juice extends StoreItem {
    public Buko_Juice(String container, String rawResource, String cookedResource, String description, String burntResource, Double preparationTime, Double sellingPrice, Double discardCost, int status, int height, int width) {
        super(container, rawResource, cookedResource, burntResource, description, preparationTime, sellingPrice, discardCost, status, height, width);
    }
}
