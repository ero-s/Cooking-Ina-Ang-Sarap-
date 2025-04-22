package com.example.cookingina.objects.entity;

public class StoreItem {
    private final String container;
    private final String rawResource;
    private final String cookedResource;
    private final String description;
    private final Double preparationTime;
    private final Double sellingPrice;
    private final Double discardCost;
    private final int status;


    public StoreItem(String container, String rawResource, String cookedResource, String description, Double preparationTime, Double sellingPrice, Double discardCost, int status) {
        this.container = container;
        this.rawResource = rawResource;
        this.cookedResource = cookedResource;
        this.description = description;
        this.preparationTime = preparationTime;
        this.sellingPrice = sellingPrice;
        this.discardCost = discardCost;
        this.status = status;
    }

    public String getCookedResource() {return cookedResource;}
    public String getRawResource() {
        return rawResource;
    }

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
}