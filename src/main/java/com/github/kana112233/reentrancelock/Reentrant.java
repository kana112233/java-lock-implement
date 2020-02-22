package com.github.kana112233.reentrancelock;


public class Reentrant {

    public int counter = 0;

    MyReentrantLock myReentrantLock = new MyReentrantLock();

    public void outer() throws InterruptedException {
        myReentrantLock.lock();
        inner();
        myReentrantLock.unLock();
    }

    public synchronized void inner() throws InterruptedException {
        myReentrantLock.lock();
        counter++;
        myReentrantLock.unLock();
    }
}