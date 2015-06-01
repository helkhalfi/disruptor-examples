package com.mcba.disruptor;


/**
 * Created by Hichame EL KHALFI on 01/06/2015.
 */

public class Car {

    private COLOR color = COLOR.WHITE;
    private String name = null;
    private POWER power = POWER.NONE;

    public COLOR getColor() {
        return color;
    }

    public void setColor(COLOR color) {
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

    public enum POWER {
        NONE,
        ELECTRIC,
        DIESEL,
        GASOLINE,
        COLD_FUSION
    }

    public enum COLOR {
        BLUE,
        RED,
        WHITE,
        BLACK,
        GREEN
    }
}