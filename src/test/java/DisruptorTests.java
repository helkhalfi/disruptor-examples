import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.mcba.disruptor.*;
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
        // Executor that will be used to construct new threads for consumers
        executorService = Executors.newFixedThreadPool(3);

        // Construct the Disruptor
        int bufferSize = 1024;// Specify the size of the ring buffer, must be power of 2.
        disruptor = new Disruptor<>(CarEvent::new, bufferSize, executorService);
    }

    @After
    public void cleanUp() {
        if (executorService != null) {
            executorService.shutdown();
        }

        if (disruptor != null) {
            disruptor.shutdown();
        }
    }

    @Test
    public void oneProducerMultipleConsumersInSerieSameEvent() throws Exception
    {


        disruptor.handleEventsWith(new CarEventSetColorHandler())
                .then(new CarEventSetPowerHandler())
                .then(new CarEventPrintCarHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();

        doWork(ringBuffer);
    }


    @Test
    public void oneProducerMultipleConsumersInParallelSameEvent() throws Exception {
        // Connect the handlers
        disruptor.handleEventsWith(new CarEventSetColorHandler(), new CarEventSetPowerHandler(), new CarEventPrintCarHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();


        doWork(ringBuffer);
    }
/*
    @Test
    public void oneProducerMultipleConsumersSameEvent() {
        Disruptor disruptor = new Disruptor(ValueEvent.EVENT_FACTORY, RING_BUFFER_SIZE, threadPool);
        disruptor.handleEventsWith(INPUT1, INPUT2);
        generateEvent(ringBuffer);
        disruptor.shutdown();
        threadPool.shutdown();
        String dodo = "dodo";
        dodo.


        private void generateEvent(RingBuffer ringBuffer) {
            for (long i = 0; i <= 10; i++) {
                String uuid = UUID.randomUUID().toString();
                // Two phase commit. Grab one of the 1024 slots
                long seq = ringBuffer.next();
                ValueEvent valueEvent = ringBuffer.get(seq);
                valueEvent.setValue(uuid);
                ringBuffer.publish(seq);
            }
        }
    }


    @Test
    public void oneProducerMultipleConsumersDifferentEvent() {

    }


    @Test
    public void diamondPattern() {

    }

    */

    private void doWork(RingBuffer<CarEvent> ringBuffer) {
        for (long l = 0; l < 10; l++) {
            long seq = ringBuffer.next();
            CarEvent carEvent = ringBuffer.get(seq);
            carEvent.set(new Car());
            ringBuffer.publish(seq);

        }
    }
}
