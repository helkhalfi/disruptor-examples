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

	final EventHandler<ValueEvent> INPUT1 = new EventHandler<ValueEvent>() {
		// event will eventually be recycled by the Disruptor after it wraps
		@Override
		public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
			System.out.println("IN: " + sequence);
			System.out.println("ValueEvent: " + event.getValue());
		}
	};

	final EventHandler<ValueEvent> INPUT2 = new EventHandler<ValueEvent>() {
		// event will eventually be recycled by the Disruptor after it wraps
		@Override
		public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
			System.out.println("OUT: " + sequence);
			System.out.println("ValueEvent: " + event.getValue());
		}
	};


	final EventHandler<ValueEvent> OUTPUT = new EventHandler<ValueEvent>() {
		// event will eventually be recycled by the Disruptor after it wraps
		@Override
		public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
			System.out.println("INOUT: " + sequence);
			System.out.println("ValueEvent: " + event.getValue());
		}
	};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void diamondTest() {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 2, threadPool);
		disruptor.handleEventsWith(INPUT1, INPUT2).then(OUTPUT);

		RingBuffer<ValueEvent> ringBuffer = disruptor.start();

		for (long i = 0; i < 10; i++) {
			String uuid = UUID.randomUUID().toString();
			// Two phase commit. Grab one of the 1024 slots
			long seq = ringBuffer.next();
			ValueEvent valueEvent = ringBuffer.get(seq);
			valueEvent.setValue(uuid);
			ringBuffer.publish(seq);
		}

		disruptor.shutdown();
		threadPool.shutdown();
	}

	@Test
	public void parallelTest() {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 2, threadPool);

		disruptor.handleEventsWith(INPUT1, INPUT2);

		RingBuffer<ValueEvent> ringBuffer = disruptor.start();

		for (long i = 0; i < 10; i++) {
			String uuid = UUID.randomUUID().toString();
			// Two phase commit. Grab one of the 1024 slots
			long seq = ringBuffer.next();
			ValueEvent valueEvent = ringBuffer.get(seq);
			valueEvent.setValue(uuid);
			ringBuffer.publish(seq);
		}

		disruptor.shutdown();
		threadPool.shutdown();
	}


	@Test
	public void oneProducerWithMultipleConsumerTest() {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 2, threadPool);

		WorkHandler<ValueEvent> workHandler = new WorkHandler<ValueEvent>() {
			@Override
			public void onEvent(ValueEvent event) throws Exception {
				System.out.println("ValueEvent: " + event.getValue());
			}
		};
		WorkerPool<ValueEvent> workerPool = new WorkerPool<ValueEvent>(ValueEvent.EVENT_FACTORY,
				new IgnoreExceptionHandler(),
				workHandler,
				workHandler,
				workHandler,
				workHandler);

		RingBuffer<ValueEvent> ringBuffer = workerPool.start(threadPool);


		for (long i = 0; i < 10; i++) {
			String uuid = UUID.randomUUID().toString();
			// Two phase commit. Grab one of the 1024 slots
			long seq = ringBuffer.next();
			ValueEvent valueEvent = ringBuffer.get(seq);
			valueEvent.setValue(uuid);
			ringBuffer.publish(seq);
		}

		disruptor.shutdown();
		threadPool.shutdown();
	}

}

