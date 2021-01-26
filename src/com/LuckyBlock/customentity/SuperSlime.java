package com.LuckyBlock.customentity;

public class SuperSlime {

    private EntitySuperSlime[] slimes = new EntitySuperSlime[64];

    public SuperSlime(EntitySuperSlime... slimes) {
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

    public void add(EntitySuperSlime slime) {
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
