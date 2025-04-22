package com.example.cookingina;

import com.almasb.fxgl.entity.Entity;
import com.example.cookingina.objects.entity.Equipment;
import com.example.cookingina.objects.entity.StoreItem;

import java.util.*;

public class WaitingItem {
    public StoreItem item;
    public Equipment equipment;
    public double x, y, width, height;
    public Entity pendingEntity;

    public WaitingItem(StoreItem item, Equipment equipment, double x, double y, double width, double height, Entity pendingEntity) {
        this.item = item;
        this.equipment = equipment;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pendingEntity = pendingEntity;
    }
}

