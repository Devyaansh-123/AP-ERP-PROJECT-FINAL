package simulator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;

public class HighwayCounter {
    private int distance;
    private final ReentrantLock lock = new ReentrantLock();
    private SyncStrategy strategy = SyncStrategy.NONE;

    public void setStrategy(SyncStrategy strategy) {
        this.strategy = strategy;
    }

    public int get() {
        return distance;
    }

    public void reset() {
        distance = 0;
    }

    public void increment() {
        switch (strategy) {
            case NONE -> incrementRacy();
            case SYNCHRONIZED -> incrementSynchronized();
            case LOCK -> incrementWithLock();
        }
    }

    private void incrementRacy() {
        int snapshot = distance;
        if ((snapshot & 3) == 0) {
            try { Thread.sleep(ThreadLocalRandom.current().nextInt(0, 2)); } catch (InterruptedException ignored) {}
        } else {
            Thread.yield();
        }
        distance = snapshot + 1;
    }

    private synchronized void incrementSynchronized() {
        distance++;
    }

    private void incrementWithLock() {
        lock.lock();
        try {
            distance++;
        } finally {
            lock.unlock();
        }
    }
}