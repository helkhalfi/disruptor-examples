package com.mcba.disruptor;

public class CarEvent {

    private String color = null;
    private String name = null;
    private POWER power = POWER.NONE;
    public enum POWER {
        NONE,
        ELECTRIC,
        DIESEL,
        GASOLINE,
        COLD_FUSION;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public POWER getPower() {
        return power;
    }

    public void setPower(POWER power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "CarEvent{" +
                "color='" + color + '\'' +
                ", name='" + name + '\'' +
                ", power=" + power +
                '}';
    }
}