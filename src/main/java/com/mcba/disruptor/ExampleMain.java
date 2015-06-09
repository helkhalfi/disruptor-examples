package com.mcba.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.mcba.disruptor.eventhandler.SetColorCarEventHandler;
import com.mcba.disruptor.eventhandler.SetPowerCarEventHandler;
import com.mcba.disruptor.eventhandler.SetWheelCarEventHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Hichame EL KHALFI on 09/06/2015.
 */
public class ExampleMain {

    public static void main(String[] args) {
        // 1 - Executor that will be used to construct new threads for consumers
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);

        // 2 - Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 2048;

        // 3 - initialize the Disruptor object
        Disruptor<CarEvent> disruptor = new Disruptor(CarEvent::new, bufferSize, executorService);//java 8 flavor

        disruptor.handleEventsWith(new SetColorCarEventHandler())
                .then(new SetPowerCarEventHandler())
                .then(new SetWheelCarEventHandler())
                .then((CarEvent event, long sequence, boolean endOfBatch) ->
                        System.out.println(String.format("id [%d] with %s", sequence, event.get())));

        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        final RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();

        for (long l = 0; l < 1000; l++) {
            // 1 - get next car sequence (sequence is internal ringbuffer counter)
            long seq = ringBuffer.next();

            //2 - get CarEvent object based on it sequence.
            CarEvent carEvent = ringBuffer.get(seq);

            //3 - set the payload (Car) in the CarEvent.
            carEvent.set(new Car());

            //4 - publish the event using it sequence.
            ringBuffer.publish(seq);
        }
    }
}
