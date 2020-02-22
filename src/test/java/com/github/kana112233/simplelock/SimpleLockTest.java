package com.github.kana112233.simplelock;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleLockTest {
    int THREAD_COUNT = 10;
    private SimpleLock simpleLock = new SimpleLock();
    private int count = 0;

    @Test
    public void test() throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < 10; i++) {
            final Thread thread = new Thread(() -> {
                try {
                    inc();
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            thread.start();
        }
        countDownLatch.await();
        assertEquals(10, count);

    }

    public void inc() throws InterruptedException {
        simpleLock.lock();
        ++count;
        simpleLock.unlock();
    }

}