package com.github.kana112233.reentrancelock;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyReentrantLockTest {

    static int countWithLock;

    final int ONE_MILLION = 1_000_000;
    final int THREAD_COUNT = 10;

    @Test
    public void testWithLock() throws InterruptedException {

        MyReentrantLock myLock = new MyReentrantLock();
        List<Thread> threadList = new ArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread t1 = new Thread(() -> {
                for (int i1 = 0; i1 < ONE_MILLION; i1++) {
                    try {
                        myLock.lock();
                        countWithLock++;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        myLock.unLock();
                    }
                }
                countDownLatch.countDown();
            });
            threadList.add(t1);
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        countDownLatch.await();
        assertEquals(ONE_MILLION * THREAD_COUNT, countWithLock);
    }

    @Test
    public void testReentrant() throws InterruptedException {
        Reentrant reentrant = new Reentrant();
        reentrant.outer();
        assertEquals(1, reentrant.counter);
    }
}
