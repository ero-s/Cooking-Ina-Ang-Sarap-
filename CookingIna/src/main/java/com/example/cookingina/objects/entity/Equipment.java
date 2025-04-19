package com.example.cookingina.objects.entity;

public class Equipment {
    protected String emptyResource;



    protected String usedResource;

    protected int type;
    protected int playend;
    protected double speedMultiplier;
    protected double cost;
    protected int capacity;
    protected boolean isUnlocked;
    protected String description;



    protected int spaceTaken;


    private int layoutX;
    private int layoutY;

    public Equipment(String emptyResource, String usedResource, int type, int playend, double speedMultiplier,
                     double cost, int capacity, boolean isUnlocked, String description) {
        this.emptyResource = emptyResource;
        this.usedResource = usedResource;
        this.type = type;
        this.playend = playend;
        this.speedMultiplier = speedMultiplier;
        this.cost = cost;
        this.capacity = capacity;
        this.isUnlocked = isUnlocked;
        this.description = description;
        this.layoutX = 0;
        this.layoutY = 0;
        spaceTaken = 0;
    }

    public String getEmptyResource() {
        return emptyResource;
    }
    public String getUsedResource() {return usedResource;}
    public int getCapacity() {return capacity;}
    public void addSpaceTaken(){spaceTaken++;}
    public void subtractSpaceTaken(){
        spaceTaken--;
    }
    public void setLayoutX(int layoutX) {
        this.layoutX = layoutX;
    }
    public void setLayoutY(int layoutY) {
        this.layoutY = layoutY;
    }
    public int getLayoutX() {
        return layoutX;
    }
    public int getLayoutY() {
        return layoutY;
    }
    public int getSpaceTaken() {return spaceTaken;}
}
