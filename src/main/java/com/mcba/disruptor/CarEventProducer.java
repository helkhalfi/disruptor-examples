package com.mcba.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 * Created by Hichame EL KHALFI on 01/06/2015.
 */
public class CarEventProducer {

    private static final EventTranslatorOneArg<CarEvent, Car> TRANSLATOR = (CarEvent event, long sequence, Car bb) -> event.set(bb);
    private final RingBuffer<CarEvent> ringBuffer;


    public CarEventProducer(final RingBuffer<CarEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(final CarEvent carEvent) {
/*
        //Old fashion to do it.
        long sequence = ringBuffer.next();// #1 claim next slot number
        try {
            CarEvent event = ringBuffer.get(sequence);// #2 get slot from it's number
            event.set(carEvent.get()); // #3 populate slot
        }finally {
            ringBuffer.publish(sequence);// #4 publish slot by it's number
        }
*/
        System.out.println("fisish" + carEvent);
        ringBuffer.publishEvent(TRANSLATOR, carEvent.get());
    }


}
