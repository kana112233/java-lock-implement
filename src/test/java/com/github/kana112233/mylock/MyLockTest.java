package com.github.kana112233.mylock;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyLockTest {
    static int countWithLock;

    final int ONE_MILLION = 1_000_000;
    final int THREAD_COUNT = 10;

    @Test
    public void testWithLock() throws InterruptedException {

        MyLock myLock = new MyLock();
        List<Thread> threadList = new ArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread t1 = new Thread(() -> {
                for (int i1 = 0; i1 < ONE_MILLION; i1++) {
                    myLock.lock();
                    countWithLock++;
                    myLock.unLock();
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

    @Ignore
    public void testLockSupport() throws InterruptedException {
        AtomicReference<String> expect = new AtomicReference<>("");
        final Thread thread = new Thread(() -> {
            LockSupport.park(this);
            expect.set("end");
        }, "thread-one");
        final Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            expect.set("start");
            LockSupport.unpark(thread);
        }, "thread-two");
        thread.start();
        thread2.start();
        thread.join();
        thread2.join();
        assertEquals("end", expect.get());
    }


}