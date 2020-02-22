package com.github.kana112233.reentrancelock;

public class Reentrant {

    public synchronized void outer() {
        inner();
    }

    public synchronized void inner() {
        //do something
    }
}