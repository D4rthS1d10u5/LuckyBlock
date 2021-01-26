package com.mcgamer199.luckyblock.customentity;

public class SuperSlime {

    private com.mcgamer199.luckyblock.customentity.EntitySuperSlime[] slimes = new com.mcgamer199.luckyblock.customentity.EntitySuperSlime[64];

    public SuperSlime(com.mcgamer199.luckyblock.customentity.EntitySuperSlime... slimes) {
        this.slimes = slimes;
    }

    public SuperSlime() {
    }

    public void ride() {
        for(int x = 0; x < this.slimes.length; ++x) {
            if (this.slimes[x] != null && this.slimes.length > x + 1 && this.slimes[x + 1] != null) {
                this.slimes[x].getEntity().setPassenger(this.slimes[x + 1].getEntity());
            }
        }

    }

    public void add(com.mcgamer199.luckyblock.customentity.EntitySuperSlime slime) {
        for(int x = 0; x < this.slimes.length; ++x) {
            if (this.slimes[x] == null) {
                this.slimes[x] = slime;
                x = this.slimes.length;
            }
        }

    }

    public EntitySuperSlime[] getSlimes() {
        return this.slimes;
    }
}
