package com.mcba.disruptor;

import java.util.concurrent.atomic.AtomicBoolean;

import com.lmax.disruptor.EventHandler;

public final class MyHandler implements EventHandler<ValueEvent>
{
	private final long ordinal;
	private final long numberOfConsumers;
	private AtomicBoolean hasBeenConsumed = new AtomicBoolean(false);

	public MyHandler(final long ordinal, final long numberOfConsumers)
	{
		this.ordinal = ordinal;
		this.numberOfConsumers = numberOfConsumers;
	}


	public void onEvent(final ValueEvent entry, final long sequence, final boolean onEndOfBatch)
	{
		//if ((sequence % numberOfConsumers) == ordinal)
		if(hasBeenConsumed.get())
		{
			System.out.println("Sequence: [" + sequence + "], numberOfConsumers["+numberOfConsumers+"], ordinal["+ordinal+"]");

		}
	}
}