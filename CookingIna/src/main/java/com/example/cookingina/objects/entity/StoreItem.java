package com.example.cookingina.objects.entity;

public class StoreItem {
    private final String name;
    private final String rawResource;
    private final String cookedResource;
    private final String burntResource;
    private final Double preparationTime;
    private final Double sellingPrice;
    private final Double discardCost;
    private final int status;
    private final int height;
    private final int width;
    private final int unlockLevel;
    private boolean isAvailable;
    private boolean isJuice;


    public StoreItem(String name, String rawResource, String cookedResource, Double preparationTime, Double sellingPrice, Double discardCost, int height, int width, int unlockLevel) {
        this.name = name;
        this.rawResource = rawResource;
        this.cookedResource = cookedResource;
        this.burntResource = burntResource;
        this.preparationTime = preparationTime;
        this.sellingPrice = sellingPrice;
        this.discardCost = discardCost;
        this.isAvailable = false;
        this.height = height;
        this.width = width;
        this.unlockLevel = unlockLevel;
    }

    public String getName(){
        return name;
    }
    public String getCookedResource() {return cookedResource;}

    public String getRawResource() {
        return rawResource;
    }

    public String getBurntResource() {return burntResource;}

    public Double getPreparationTime() {
        return preparationTime;
    }
    public void setIsJuice(boolean isJuice){
        this.isJuice = isJuice;
    }
    public boolean getIsJuice(){
        return isJuice;
    }
    public Double getSellingPrice() {
        return sellingPrice;
    }
    public Double getDiscardCost() {
        return discardCost;
    }
    public boolean getAvailable() {
        return isAvailable;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getUnlockLevel() {
        return unlockLevel;
    }
    public void updateAvailability(int currentPlayerLevel) {
        this.isAvailable = (currentPlayerLevel >= unlockLevel);
    }
    public boolean getIsAvailable() {
        return isAvailable;
    }
}

