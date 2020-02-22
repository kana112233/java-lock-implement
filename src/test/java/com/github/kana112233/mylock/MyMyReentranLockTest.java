package com.github.kana112233.mylock;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

public class MyMyReentranLockTest {
    static int countWithLock;
    static int countWithoutLock;

    final int ONE_MILLION = 1_000_000;
    final int THREAD_COUNT = 10;

    @Test
    public void testWithoutLock() throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < ONE_MILLION; i++) {
                        countWithoutLock++;
                    }
                    countDownLatch.countDown();
                }
            });
            threadList.add(t1);
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        countDownLatch.await();
        Assert.assertTrue(countWithoutLock < ONE_MILLION * THREAD_COUNT);
    }

    @Test
    public void testWithLock() throws InterruptedException {

        MyLock myLock = new MyLock();
        List<Thread> threadList = new ArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < ONE_MILLION; i++) {
                        myLock.lock();
                        countWithLock++;
                        myLock.unLock();
                    }
                    countDownLatch.countDown();
                }
            });
            threadList.add(t1);
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        countDownLatch.await();
        Assert.assertEquals(ONE_MILLION * THREAD_COUNT, countWithLock);
    }

    @Test
    public void testLockSupport() {
        LockSupport.park();

        System.out.println("block!");
    }


}