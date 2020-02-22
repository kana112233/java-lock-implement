package com.github.kana112233.reentrancelock;


public class Reentrant2 {

    MyReentranLock myReentranLock = new MyReentranLock();

    public void outer() throws InterruptedException {
        myReentranLock.lock();
        inner();
        myReentranLock.unlock();
    }

    public synchronized void inner() throws InterruptedException {
        myReentranLock.lock();
        //do something
        myReentranLock.unlock();
    }
}