package com.example.cookingina.control;

import com.almasb.fxgl.entity.component.Component;
import com.example.cookingina.objects.entity.Equipment;

public class EquipmentComponent extends Component {
    private Equipment sourceEquipment;

    public EquipmentComponent(Equipment equipment) {
        this.sourceEquipment = equipment;
    }

    public Equipment getEquipment() {
        return sourceEquipment;
    }
}
