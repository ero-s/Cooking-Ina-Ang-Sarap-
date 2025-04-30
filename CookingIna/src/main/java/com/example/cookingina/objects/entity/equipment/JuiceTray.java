package com.example.cookingina.objects.entity.equipment;

import com.example.cookingina.objects.entity.Equipment;

public class JuiceTray extends Equipment {
    public static int juice_count;
    public JuiceTray(
            String emptyResource,
            String usedResource,
            int type,
            int playend,
            double speedMultiplier,
            double cost,
            int capacity,
            boolean isUnlocked,
            String description
    ) {
        super(emptyResource,usedResource, type, speedMultiplier, cost, capacity, isUnlocked, description);
    }

    public boolean isTrayFull(){
        if(juice_count == 3) return true;
        return false;
    }

    public boolean isTrayEmpty(){
        if(juice_count == 0) return true;
        return false;
    }
}
