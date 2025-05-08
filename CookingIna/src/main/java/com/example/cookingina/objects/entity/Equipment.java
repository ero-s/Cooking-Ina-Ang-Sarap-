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
    protected boolean isOccupied;
    protected String description;
    protected int spaceTaken;
    private int layoutX;
    private int layoutY;
    private final boolean[] slots;

    public Equipment(String emptyResource, String usedResource, int type, double speedMultiplier,
                     double cost, int capacity, boolean isUnlocked, String description) {
        this.emptyResource = emptyResource;
        this.usedResource = usedResource;
        this.type = type;
        this.playend = playend;
        this.speedMultiplier = speedMultiplier;
        this.cost = cost;
        this.capacity = capacity;
        this.isUnlocked = isUnlocked;
        this.isOccupied = false;
        this.description = description;
        this.layoutX = 0;
        this.layoutY = 0;
        this.spaceTaken = 0;
        this.slots = new boolean[capacity];
    }

    public String getDescription(){
        return description;
    }

    public int getNextAvailableSlot() {
        for (int i = 0; i < slots.length; i++) {
            if (!slots[i]) {
                return i;
            }
        }
        return -1;
    }

    public void occupySlot(int index) {
        slots[index] = true;
    }

    public void freeSlot(int index) {
        slots[index] = false;
    }

    public boolean isSlotOccupied(int index) {
        return slots[index];
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
    public int getType(){return type;}
    public void setOccupied(boolean isOccupied){this.isOccupied = isOccupied;}
    public boolean isOccupied(){return isOccupied;}
    public void setUnlocked(boolean set){this.isUnlocked = set;}
    public boolean isUnlocked(){return isUnlocked;}
}