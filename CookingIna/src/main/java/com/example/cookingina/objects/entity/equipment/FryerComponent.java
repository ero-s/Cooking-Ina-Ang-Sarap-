package com.example.cookingina.objects.entity.equipment;

import com.almasb.fxgl.entity.component.Component;

public class FryerComponent extends Component {
    private final Fryer fryer;

    public FryerComponent(Fryer fryer) {
        this.fryer = fryer;
    }

    public Fryer getFryer() {
        return fryer;
    }
}