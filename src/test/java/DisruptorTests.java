import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.Disruptor;
import com.mcba.disruptor.Car;
import com.mcba.disruptor.CarEvent;
import com.mcba.disruptor.eventhandler.CustomerNotificationCarEventHandler;
import com.mcba.disruptor.eventhandler.PrintCarEventHandler;
import com.mcba.disruptor.eventhandler.SetColorCarEventHandler;
import com.mcba.disruptor.eventhandler.SetPowerCarEventHandler;
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
        // Executor that will be used to construct new threads for consumers
        executorService = Executors.newFixedThreadPool(3);

        // Construct the Disruptor

        // 1-  Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        //2 - initialize the Disruptor object
        disruptor = new Disruptor<>(CarEvent::new, bufferSize, executorService);//java 8 flavor
        //disruptor = new Disruptor<>(new CarEventFactory(), bufferSize, executorService);
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
    public void buildCarStepByStep() throws Exception
    {
        disruptor.handleEventsWith(new SetColorCarEventHandler())
                .then(new SetPowerCarEventHandler())
                .then(new PrintCarEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();

        sendCarsToFactory(ringBuffer);
    }


    @Test
    public void sendCarByAAllDeliveryType() throws Exception {
        // Connect the handlers
        disruptor.handleEventsWith(new CustomerNotificationCarEventHandler("WebSite"),
                new CustomerNotificationCarEventHandler("Twitter"));

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();

        sendCarsToFactory(ringBuffer);
    }


    @Test
    public void sendCarByAOneDeliveryTypeAtOnce() throws Exception {
        // Connect the handlers

        WorkerPool<CarEvent> physicalDeliveryWorkerPool =
                new WorkerPool(CarEvent::new,
                        new IgnoreExceptionHandler(),
                        new DeliveryCarWorkHandler("Trunk"),
                        new DeliveryCarWorkHandler("boat"));
        RingBuffer<CarEvent> ringBuffer = physicalDeliveryWorkerPool.start(executorService);

        sendCarsToFactory(ringBuffer);
    }


    @Test
    public void carFactory() throws Exception {


        disruptor.handleEventsWith(new SetColorCarEventHandler())
                .then(new SetPowerCarEventHandler())
                /*.thenHandleEventsWithWorkerPool(new DeliveryCarWorkHandler("Trunk"),
                        new DeliveryCarWorkHandler("Boat"))*/
                .then(new CustomerNotificationCarEventHandler("WebSite"),
                        new CustomerNotificationCarEventHandler("Twitter"))
                .then(new PrintCarEventHandler());

        // Start the Disruptor, starts all threads running
        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<CarEvent> ringBuffer = disruptor.start();
        sendCarsToFactory(ringBuffer);
        //Thread.sleep(Integer.MAX_VALUE);
        System.out.println("yo");
    }



    @Test
    public void diamondPattern() {

    }


    private void sendCarsToFactory(RingBuffer<CarEvent> ringBuffer) {
        for (long l = 0; l < 10000; l++) {
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
