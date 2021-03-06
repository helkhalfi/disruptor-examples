package com.mcba.disruptor.eventhandler;

import com.lmax.disruptor.EventHandler;
import com.mcba.disruptor.Car;
import com.mcba.disruptor.CarEvent;

import java.util.Random;

/**
 * Created by Hichame EL KHALFI on 01/06/2015.
 */
public class SetPowerCarEventHandler implements EventHandler<CarEvent> {

    private static final Random random = new Random();

    public void onEvent(CarEvent event, long sequence, boolean endOfBatch) throws Exception {

        random.ints(0, Car.POWER.values().length - 1)
                .limit(1)
                .forEach(i -> event.get().setPower(Car.POWER.values()[i]));

    }
}
