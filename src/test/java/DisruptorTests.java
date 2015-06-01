import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.mcba.disruptor.CarEvent;
import com.mcba.disruptor.CarEventPrintColorHandler;
import com.mcba.disruptor.CarEventProducer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hichame on 29/05/15.
 */
public class DisruptorTests {

    private ExecutorService executorService;

    @Before
    public void init() {
        // Executor that will be used to construct new threads for consumers
        executorService = Executors.newFixedThreadPool(3);
    }

    @After
    public void cleanUp() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Test
    public void toto1() throws Exception
    {
        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<CarEvent> disruptor = new Disruptor<>(CarEvent::new, bufferSize, executorService);
        //Disruptor<CarEvent> disruptor = new Disruptor(new CarEventFactory(), bufferSize, executorService);

        // Connect the handlers

        disruptor.handleEventsWith(new CarEventPrintColorHandler(),
                (carEvent, sequence1, endOfBatch) ->
                        System.out.println("Power:[" + carEvent.get().getPower() + "]")

        );

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();

        CarEventProducer producer = new CarEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++)
        {
            bb.putLong(0, l);
            //producer.onData();
            //ringBuffer.publishEvent((event, sequence, buffer) -> event.set(null), bb);
            Thread.sleep(1000);
        }
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
}
