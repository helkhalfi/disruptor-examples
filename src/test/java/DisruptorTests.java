import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.Disruptor;
import com.mcba.disruptor.Car;
import com.mcba.disruptor.CarEvent;
import com.mcba.disruptor.eventhandler.PrintCarEventHandler;
import com.mcba.disruptor.eventhandler.SetColorCarEventHandler;
import com.mcba.disruptor.eventhandler.SetPowerCarEventHandler;
import com.mcba.disruptor.eventhandler.SetWheelCarEventHandler;
import com.mcba.disruptor.workhandler.DeliveryCarWorkHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hichame on 29/05/15.
 */
public class DisruptorTests {

    private ExecutorService executorService;
    private Disruptor<CarEvent> disruptor;

    @Before
    public void init() {
        // 1 - Executor that will be used to construct new threads for consumers
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);

        // 2 - Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 2048;

        // 3 - initialize the Disruptor object
        disruptor = new Disruptor<>(CarEvent::new, bufferSize, executorService);//java 8 flavor
        //disruptor = new Disruptor<>(new CarEventFactory(), bufferSize, executorService);
    }

    @After
    public void cleanUp() {

        if (disruptor != null) {
            disruptor.shutdown();
        }

        if (executorService != null) {
            executorService.shutdown();
        }


    }

    @Test
    public void buildCarInOrder() throws Exception {

        disruptor.handleEventsWith(new SetColorCarEventHandler())
                .then(new SetPowerCarEventHandler())
                .then(new SetWheelCarEventHandler())
                .then((CarEvent event, long sequence, boolean endOfBatch) ->
                        System.out.println(String.format("id [%d] with %s", sequence, event.get())));

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        final RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();

        produceCar(ringBuffer);
    }


    @Test
    public void buildCarInParallel() throws Exception {
        // Connect the handlers

        disruptor.handleEventsWith(
                new SetColorCarEventHandler(),
                new SetPowerCarEventHandler(),
                new SetWheelCarEventHandler())
                .then(new PrintCarEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        final RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();

        produceCar(ringBuffer);
    }


    @Test
    public void dispatchEachCarByOnlyOneWarAtOnce() throws Exception {

        // Create a WorkerPool to dispatch onnect the handlers
        final WorkerPool<CarEvent> carDeliveryWorkerPool =
                new WorkerPool(CarEvent::new,
                        new IgnoreExceptionHandler(),
                        new DeliveryCarWorkHandler("Truck"),
                        new DeliveryCarWorkHandler("Ship"),
                        new DeliveryCarWorkHandler("Train"));

        final RingBuffer<CarEvent> ringBuffer = carDeliveryWorkerPool.start(executorService);

        produceCar(ringBuffer);

    }


    @Test
    public void diamondPattern() {
        disruptor.handleEventsWith(new SetColorCarEventHandler(), new SetPowerCarEventHandler())
                .then(new PrintCarEventHandler());

        final RingBuffer<CarEvent> ringBuffer = disruptor.start();

        produceCar(ringBuffer);
    }


    private void produceCar(final RingBuffer<CarEvent> ringBuffer) {
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
