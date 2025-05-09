package com.example.cookingina.objects.entity;

public class StoreItem {
    private final String name;
    private final String rawResource;
    private final String cookedResource;
    private final Double preparationTime;
    private final Double sellingPrice;
    private final Double discardCost;
    private final int status;
    private final int height;
    private final int width;
    private final boolean isJuice;



    public StoreItem(String name, String rawResource, String cookedResource, boolean isJuice, Double preparationTime, Double sellingPrice, Double discardCost, int status, int height, int width) {
        this.name = name;
        this.rawResource = rawResource;
        this.cookedResource = cookedResource;
        this.isJuice = isJuice;
        this.preparationTime = preparationTime;
        this.sellingPrice = sellingPrice;
        this.discardCost = discardCost;
        this.status = status;
        this.height = height;
        this.width = width;
    }
    public String getName(){
        return name;
    }
    public String getCookedResource() {return cookedResource;}
    public String getRawResource() {
        return rawResource;
    }
    public Double getPreparationTime() {
        return preparationTime;
    }
    public boolean getIsJuice() {
        return isJuice;
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

