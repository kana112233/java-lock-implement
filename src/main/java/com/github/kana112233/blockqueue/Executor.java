package com.github.kana112233.blockqueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Executor {

    public static <T> void runInParallelButConsumeInSerial(List<Callable<T>> tasks,
                                                           Consumer<T> consumer,
                                                           int numberOfThreads) throws Exception {
        // TODO: let  MyBlockingQueue replace BlockingQueue
        BlockingQueue<Future<T>> queue = new LinkedBlockingQueue<>(numberOfThreads);
        List<Exception> captureException = new LinkedList<>();

        Thread consumerThread = new Thread(() -> {
            while (true) {
                try {
                    Future<T> future = queue.poll(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                    if (future == PoisonPill.INSTANCE) {
                        break;
                    }

                    try {
                        if (future != null) {
                            consumer.accept(future.get());
                        }
                    } catch (Exception e) {
                        captureException.add(e);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        consumerThread.start();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (Callable<T> task : tasks) {
            queue.put(threadPool.submit(task));
        }

        queue.put((Future) PoisonPill.INSTANCE);

        consumerThread.join();

        threadPool.shutdown();
        for (Exception exception : captureException) {
            throw exception;
        }
    }

    /**
     * put PoisonPill
     */
    private enum PoisonPill implements Future<Object> {
        INSTANCE;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDone() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }
    }
}
