package com.mcgamer199.luckyblock.lb;

public class DropOption {

    private Object[] values;
    private final String name;

    public DropOption(String name, Object[] values) {
        this.values = values;
        this.name = name;
    }

    public Object[] getValues() {
        return this.values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public String getName() {
        return this.name;
    }

    public void addValue(Object value) {
        for (int x = 0; x < this.values.length; ++x) {
            if (this.values[x] == null) {
                this.values[x] = value;
                return;
            }
        }
    }

    public void removeValue(Object value) {
        for (int x = 0; x < this.values.length; ++x) {
            if (this.values[x] != null && this.values[x] == value) {
                this.values[x] = null;
                return;
            }
        }
    }

    public void removeValue(int num) {
        if (this.values[num] != null) {
            this.values[num] = null;
        }
    }

    public void removeValues() {
        for (int x = 0; x < this.values.length; ++x) {
            if (this.values[x] != null) {
                this.values[x] = null;
            }
        }
    }
}
