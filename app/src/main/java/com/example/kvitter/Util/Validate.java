package com.example.kvitter.Util;

public class Validate {
    public boolean state;
    public String name;

    public Validate(String name, boolean state) {
        this.name = name;
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
