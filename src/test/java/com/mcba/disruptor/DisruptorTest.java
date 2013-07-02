package com.mcba.disruptor;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.Disruptor;


public class DisruptorTest {

	public static final int RING_BUFFER_SIZE = 1024;

	final EventHandler<ValueEvent> INPUT1 = new EventHandler<ValueEvent>() {
		@Override
		public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
			System.out.println("Input1: " + event.getValue());
		}
	};

	final EventHandler<ValueEvent> INPUT2 = new EventHandler<ValueEvent>() {
		@Override
		public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
			System.out.println("Input2: " + event.getValue());
		}
	};


	final EventHandler<ValueEvent> OUTPUT = new EventHandler<ValueEvent>() {
		@Override
		public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
			System.out.println("Output: " + event.getValue());
		}
	};

	WorkHandler<ValueEvent> workHandler = new WorkHandler<ValueEvent>() {
		@Override
		public void onEvent(ValueEvent event) throws Exception {
			System.out.println("WorkHandlerEvent: " + event.getValue());
		}
	};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@SuppressWarnings("unchecked")
	@Test
	public void diamondTest() {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, RING_BUFFER_SIZE, threadPool);
		disruptor.handleEventsWith(INPUT1, INPUT2).then(OUTPUT);

		RingBuffer<ValueEvent> ringBuffer = disruptor.start();

		generateEvent(ringBuffer);

		disruptor.shutdown();
		threadPool.shutdown();
	}


	@SuppressWarnings("unchecked")
	@Test
	public void parallelTest() {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, RING_BUFFER_SIZE, threadPool);
		disruptor.handleEventsWith(INPUT1, INPUT2);

		RingBuffer<ValueEvent> ringBuffer = disruptor.start();

		generateEvent(ringBuffer);

		disruptor.shutdown();
		threadPool.shutdown();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void oneProducerWithMultipleConsumerTest() {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, RING_BUFFER_SIZE, threadPool);

		@SuppressWarnings("unchecked")
		WorkerPool<ValueEvent> workerPool = new WorkerPool<ValueEvent>(ValueEvent.EVENT_FACTORY,
				new IgnoreExceptionHandler(),
				workHandler,
				workHandler,
				workHandler,
				workHandler);

		RingBuffer<ValueEvent> ringBuffer = workerPool.start(threadPool);


		generateEvent(ringBuffer);

		disruptor.shutdown();
		threadPool.shutdown();
	}


	private void generateEvent(RingBuffer<ValueEvent> ringBuffer) {
		for (long i = 0; i < 10; i++) {
			String uuid = UUID.randomUUID().toString();
			// Two phase commit. Grab one of the 1024 slots
			long seq = ringBuffer.next();
			ValueEvent valueEvent = ringBuffer.get(seq);
			valueEvent.setValue(uuid);
			ringBuffer.publish(seq);
		}
	}
}

