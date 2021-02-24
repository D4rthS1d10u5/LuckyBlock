package com.mcgamer199.luckyblock.customentity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperSlime {

    @Getter
    private final List<CustomEntitySuperSlime> slimes = new ArrayList<>();

    public SuperSlime(CustomEntitySuperSlime... slimes) {
        this.slimes.addAll(Arrays.asList(slimes));
    }

    public SuperSlime() {}

    public void ride() {
        for (int i = 0; i < slimes.size(); i++) {
            if(slimes.size() > (i+1)) {
                CustomEntitySuperSlime vehicle = slimes.get(i);
                CustomEntitySuperSlime passenger = slimes.get(i+1);
                if(vehicle.isValid() && passenger.isValid()) {
                    vehicle.getLinkedEntity().addPassenger(passenger.getLinkedEntity());
                }
            }
        }
    }

    public void add(CustomEntitySuperSlime slime) {
        slimes.add(slime);
    }
}
