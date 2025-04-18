package com.example.cookingina.objects.entity;

public class StoreItem {
    private final String resource;
    private final String description;
    private final Double preparationTime;
    private final Double sellingPrice;
    private final Double discardCost;
    private final int status;


    public StoreItem(String resource, String description, Double preparationTime, Double sellingPrice, Double discardCost, int status) {
        this.resource = resource;
        this.description = description;
        this.preparationTime = preparationTime;
        this.sellingPrice = sellingPrice;
        this.discardCost = discardCost;
        this.status = status;
    }

    public String getResource() {
        return resource;
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
