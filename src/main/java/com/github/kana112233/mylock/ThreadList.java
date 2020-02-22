package com.github.kana112233.mylock;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;

public class ThreadList {
    private volatile Node head = null;
    private static long headOffset;
    private static Unsafe unsafe;

    static {
        try {
            Constructor<Unsafe> constructor = Unsafe.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            unsafe = constructor.newInstance();
            headOffset = unsafe.objectFieldOffset(ThreadList.class.getDeclaredField("head"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param thread
     * @return 是否只有当前一个线程在等待
     */
    public boolean push(Thread thread) {
        Node node = new Node(thread);
        for (; ; ) {
            Node first = getHead();
            node.setNext(first);
            if (unsafe.compareAndSwapObject(this, headOffset, first, node)) {
                return first == null;
            } else {
                System.out.println("missing");
            }
        }
    }

    public Thread pop() {
        Node first;
        for (; ; ) {
            first = getHead();
            Node next = null;
            if (first != null) {
                next = first.getNext();
            }
            if (unsafe.compareAndSwapObject(this, headOffset, first, next)) {
                break;
            }
        }
        return first == null ? null : first.getThread();
    }

    private Node getHead() {
        return this.head;
    }

    private static class Node {
        volatile Node next;
        volatile Thread thread;

        public Node(Thread thread) {
            this.thread = thread;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getNext() {
            return next;
        }

        public Thread getThread() {
            return this.thread;
        }
    }
}