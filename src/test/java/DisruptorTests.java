import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by hichame on 29/05/15.
 */
public class DisruptorTests {

    public class CarEvent
    {

        private String color = "";
        private String name = "";
        private String

    }


    public static class LongEventFactory implements EventFactory<LongEvent>
    {
        public LongEvent newInstance()
        {
            return new LongEvent();
        }
    }


    public static class LongEventHandler implements EventHandler<LongEvent>
    {
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch)
        {
            System.out.println("Event: " + event);
        }
    }


    public static class LongEventProducer
    {
        private final RingBuffer<LongEvent> ringBuffer;

        public LongEventProducer(RingBuffer<LongEvent> ringBuffer)
        {
            this.ringBuffer = ringBuffer;
        }

        public void onData(ByteBuffer bb)
        {
            long sequence = ringBuffer.next();  // Grab the next sequence
            try
            {
                LongEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
                // for the sequence
                event.set(bb.getLong(0));  // Fill with data
            }
            finally
            {
                ringBuffer.publish(sequence);
            }
        }
    }



    public static class LongEventProducerWithTranslator
    {
        private final RingBuffer<LongEvent> ringBuffer;

        public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer)
        {
            this.ringBuffer = ringBuffer;
        }

        private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR =
                new EventTranslatorOneArg<LongEvent, ByteBuffer>()
                {
                    public void translateTo(LongEvent event, long sequence, ByteBuffer bb)
                    {
                        event.set(bb.getLong(0));
                    }
                };

        public void onData(ByteBuffer bb)
        {
            ringBuffer.publishEvent(TRANSLATOR, bb);
            //ringBuffer.pub
        }
    }



    @Test
    public  void main() throws Exception
    {
        // Executor that will be used to construct new threads for consumers
        Executor executor = Executors.newCachedThreadPool();

        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor(factory, bufferSize, executor);

        // Connect the handler
        disruptor.handleEventsWith(new LongEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        LongEventProducer producer = new LongEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++)
        {
            bb.putLong(0, l);
            producer.onData(bb);
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
