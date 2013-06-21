package com.mcba.disruptor;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorExample {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 2, threadPool);

		//this is a comment
		final EventHandler<ValueEvent> IN = new EventHandler<ValueEvent>() {
			// event will eventually be recycled by the Disruptor after it wraps
			@Override
			public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
				System.out.println("IN: " + sequence);
				System.out.println("ValueEvent: " + event.getValue());
			}
		};

		final EventHandler<ValueEvent> OUT = new EventHandler<ValueEvent>() {
			// event will eventually be recycled by the Disruptor after it wraps
			@Override
			public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
				System.out.println("OUT: " + sequence);
				System.out.println("ValueEvent: " + event.getValue());
			}
		};
 

		final EventHandler<ValueEvent> INOUT = new EventHandler<ValueEvent>() {
			// event will eventually be recycled by the Disruptor after it wraps
			@Override
			public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception {
				System.out.println("INOUT: " + sequence);
				System.out.println("ValueEvent: " + event.getValue());
			}
		};

		/*
		 * handles event in parallel between with handler1 and handler2
		 */
		//disruptor.handleEventsWith(IN, OUT);

		
		/*
		 * handles event with handler1 then handler2
		 */
		//disruptor.handleEventsWith(IN,JOURNAL).then(OUT);

		
		/*
		 * Diamond example
		 */
		//disruptor.handleEventsWith(IN, OUT).then(INOUT);

		


		/*
		 * the ringBuffer has many consumer than consume different event
		 * Like same queue with multiple consumer 
		 */
		
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

		//RingBuffer<ValueEvent> ringBuffer = workerPool.start(threadPool)
		

		RingBuffer<ValueEvent> ringBuffer = disruptor.start();

		for (long i = 0; i < 10; i++) {
			String uuid = UUID.randomUUID().toString();
			// Two phase commit. Grab one of the 1024 slots
			long seq = ringBuffer.next();
			ValueEvent valueEvent = ringBuffer.get(seq);
			valueEvent.setValue(uuid);
			ringBuffer.publish(seq);
		}


		
		//workerPool.drainAndHalt();
		disruptor.shutdown();
		threadPool.shutdown();
	}
}
