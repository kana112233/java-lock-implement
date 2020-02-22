package com.github.kana112233.mylock;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.util.concurrent.locks.LockSupport;

public class MyLock {
    private volatile int state = 0;
    private ThreadList threadList = new ThreadList();
    private static long stateOffset;
    private static Unsafe unsafe;

    static {
        try {
            Constructor<Unsafe> constructor = Unsafe.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            unsafe = constructor.newInstance();
            stateOffset = unsafe.objectFieldOffset(MyLock.class.getDeclaredField("state"));
        } catch (Exception ignored) {
        }

    }

    public void lock() {
        if (compareAndSetState(0, 1)) {
        } else {
            addNodeAndWait();
        }
    }

    public void unLock() {
        compareAndSetState(1, 0);
        Thread thread = threadList.pop();
        if (thread != null) {
            LockSupport.unpark(thread);
        }
    }

    private void addNodeAndWait() {
        //如果当前只有一个等待线程时，重新获取一下锁，防止永远不被唤醒。
        boolean isOnlyOne = threadList.push(Thread.currentThread());
        if (isOnlyOne && compareAndSetState(0, 1)) {
            return;
        }
        //线程被挂起
        LockSupport.park(this);
        //线程被唤醒后继续竞争锁
        if (compareAndSetState(0, 1)) {
            // Lock done
            return;
        } else {
            addNodeAndWait();
        }
    }

    private boolean compareAndSetState(int expect, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }
}