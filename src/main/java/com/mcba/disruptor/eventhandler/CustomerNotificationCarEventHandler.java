package com.mcba.disruptor.eventhandler;

import com.lmax.disruptor.EventHandler;
import com.mcba.disruptor.CarEvent;

/**
 * Created by Hichame EL KHALFI on 01/06/2015.
 */
public class CustomerNotificationCarEventHandler implements EventHandler<CarEvent> {

    private String notificationMethod;

    public CustomerNotificationCarEventHandler(final String notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public void onEvent(CarEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.get().setCustomerNorificationMethod(notificationMethod);
    }
}
