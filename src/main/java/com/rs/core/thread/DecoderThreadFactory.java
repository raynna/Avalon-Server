package com.rs.core.thread;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DecoderThreadFactory implements ThreadFactory {

	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	public DecoderThreadFactory() {
		group = Thread.currentThread().getThreadGroup();
		namePrefix = "Decoder Pool-" + poolNumber.getAndIncrement() + "-thread-";
	}

	@Override
	public Thread newThread(@NotNull Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.MAX_PRIORITY - 1)
			t.setPriority(Thread.MAX_PRIORITY - 1);
		return t;
	}

}
