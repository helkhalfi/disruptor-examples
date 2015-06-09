package com.mcba.disruptor.eventhandler;

import com.lmax.disruptor.EventHandler;
import com.mcba.disruptor.CarEvent;

/**
 * Created by Hichame EL KHALFI on 01/06/2015.
 */
public class SetWheelCarEventHandler implements EventHandler<CarEvent> {


    public void onEvent(CarEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.get().setWheels(4);
    }
}
