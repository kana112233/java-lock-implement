package com.github.kana112233.readwritelock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadWriteLockTest {
    static int counter = 0;

    final int THREAD_COUNT = 10;
    final int ONE_MILLION = 1_000_000;
    List<Thread> threadList;
    CountDownLatch countDownLatch;
    private ReadWriteLock readWriteLock;

    @BeforeEach
    public void before() {
        counter = 0;
        threadList = new ArrayList<>();
        readWriteLock = new ReadWriteLock();
        countDownLatch = new CountDownLatch(THREAD_COUNT);
    }

    @Test
    public void testReadLock() throws InterruptedException {
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Thread thread = new Thread(() -> {
                try {
                    readWriteLock.lockRead();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    readWriteLock.unlockRead();
                    countDownLatch.countDown();
                }
            });
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            thread.start();
        }
        countDownLatch.await();
        assertEquals(0, readWriteLock.getReadCount());
    }

    @Test
    public void testWriteLock() throws InterruptedException {
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Thread thread = new Thread(() -> {
                try {
                    readWriteLock.lockWrite();
                    for (int j = 0; j < ONE_MILLION; j++) {
                        counter++;
                    }
                    assertEquals(1, readWriteLock.getWriteAccesses());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    readWriteLock.unlockWrite();
                    assertEquals(0, readWriteLock.getWriteAccesses());
                    countDownLatch.countDown();
                }
            });
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            thread.start();
        }
        countDownLatch.await();
        assertEquals(THREAD_COUNT * ONE_MILLION, counter);
    }

    @RepeatedTest(10)
    public void testReadWriteLock() {
        final Thread thread1 = new Thread(() -> {
            try {
                readWriteLock.lockWrite();
                counter++;
                assertEquals(1, readWriteLock.getWriteAccesses());
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readWriteLock.unlockWrite();
            }
        });
        final Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(10);
                readWriteLock.lockRead();
                assertEquals(1, counter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readWriteLock.unlockRead();
            }
        });

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @RepeatedTest(10)
    public void testReadWriteLock2() {
        final Thread thread1 = new Thread(() -> {
            try {
                Thread.sleep(10);
                readWriteLock.lockWrite();
                counter++;
                assertEquals(1, readWriteLock.getWriteAccesses());
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readWriteLock.unlockWrite();
            }
        });
        final Thread thread2 = new Thread(() -> {
            try {
                readWriteLock.lockRead();
                assertEquals(0, counter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readWriteLock.unlockRead();
            }
        });

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}