package com.github.kana112233.blockqueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyBlockingQueueTest {
    MyBlockingQueue myBlockingQueue;

    @BeforeEach
    void setUp() {
        myBlockingQueue = new MyBlockingQueue(2);
    }

    @Test
    void testMyBLockingQueue() {
        for (int i = 0; i < 5; i++) {
            final Thread thread = new Thread(() -> {
                try {
                    myBlockingQueue.enqueue("");
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        myBlockingQueue.dequeue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            });
            thread.start();
        }
    }
}