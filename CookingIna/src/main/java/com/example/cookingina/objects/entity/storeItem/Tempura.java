package com.example.cookingina.objects.entity.storeItem;

import com.example.cookingina.objects.entity.StoreItem;

public class Tempura extends StoreItem {
    public Tempura(String container, String rawResource, String cookedResource, String description, Double preparationTime, Double sellingPrice, Double discardCost, int status) {
        super(container, rawResource, cookedResource, description, preparationTime, sellingPrice, discardCost, status);
    }
}