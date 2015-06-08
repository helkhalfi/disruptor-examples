package com.mcba.disruptor.workhandler;

import com.lmax.disruptor.WorkHandler;
import com.mcba.disruptor.CarEvent;

/**
 * Created by Hichame EL KHALFI on 08/06/2015.
 */
public class DeliveryCarWorkHandler implements WorkHandler<CarEvent> {


    private String transportMethod;

    public DeliveryCarWorkHandler(final String transportMethod) {
        this.transportMethod = transportMethod;
    }

    @Override
    public void onEvent(CarEvent event) throws Exception {
        event.get().setTransportMethod(transportMethod);
    }
}
