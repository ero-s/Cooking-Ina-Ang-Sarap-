package com.example.cookingina.objects.entity;

public class StoreItem {
    private final String container;
    private final String rawResource;
    private final String cookedResource;
    private final String burntResource;
    private final String description;
    private final Double preparationTime;
    private final Double sellingPrice;
    private final Double discardCost;
    private final int status;
    private final int height;
    private final int width;


    public StoreItem(String container, String rawResource, String cookedResource, String burntResource, String description, Double preparationTime, Double sellingPrice, Double discardCost, int status, int height, int width) {
        this.container = container;
        this.rawResource = rawResource;
        this.cookedResource = cookedResource;
        this.burntResource = burntResource;
        this.description = description;
        this.preparationTime = preparationTime;
        this.sellingPrice = sellingPrice;
        this.discardCost = discardCost;
        this.status = status;
        this.height = height;
        this.width = width;
    }

    public String getCookedResource() {return cookedResource;}

    public String getRawResource() {
        return rawResource;
    }

    public String getBurntResource() {return burntResource;}

    public String getContainer() {
        return container;
    }

    public String getDescription() {
        return description;
    }

    public Double getPreparationTime() {
        return preparationTime;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public Double getDiscardCost() {
        return discardCost;
    }

    public int getStatus() {
        return status;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

