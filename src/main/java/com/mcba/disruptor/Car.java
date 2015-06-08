package com.mcba.disruptor;


/**
 * Created by Hichame EL KHALFI on 01/06/2015.
 */

public class Car {
    private COLOR color = COLOR.WHITE;
    private POWER power = POWER.COLD_FUSION;
    private String transportMethod = "";
    private String customerNorificationMethod = "";

    public COLOR getColor() {
        return color;
    }

    public void setColor(COLOR color) {
        this.color = color;
    }

    public POWER getPower() {
        return power;
    }

    public void setPower(POWER power) {
        this.power = power;
    }

    public String getTransportMethod() {
        return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }

    public String getCustomerNorificationMethod() {
        return customerNorificationMethod;
    }

    public void setCustomerNorificationMethod(String customerNorificationMethod) {
        this.customerNorificationMethod = customerNorificationMethod;
    }

    @Override
    public String toString() {
        return "Car{" +
                "color=" + color +
                ", power=" + power +
                ", transportMethod='" + transportMethod + '\'' +
                ", customerNorificationMethod='" + customerNorificationMethod + '\'' +
                '}';
    }

    public enum POWER {
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