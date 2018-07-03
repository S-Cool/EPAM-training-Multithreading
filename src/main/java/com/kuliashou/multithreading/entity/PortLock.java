package com.kuliashou.multithreading.entity;

import com.kuliashou.multithreading.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PortLock implements IPort {

    private static Logger logger = LogManager.getLogger();

    private int piersCount;
    private int maxWeight;
    private int currentWeight;
    private int dockedShips;

    private static PortLock instance = null;
    private static AtomicBoolean instanceCreated = new AtomicBoolean(false);
    private static Lock lock = new ReentrantLock();

    public static PortLock getInstance(int piersCount, int maxWeight, int currentWeight) throws PortException {
        if (instanceCreated.compareAndSet(false, false)) {
            lock.lock();
            try {
                if (instanceCreated.compareAndSet(false, true)) {
                    instance = new PortLock(piersCount, maxWeight, currentWeight);
                }
            } catch (Exception e) {
                throw new PortException("Exception initialize PortLock.", e);
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    private PortLock(int piersCount, int maxWeight, int currentWeight) {
        this.piersCount = piersCount;
        this.maxWeight = maxWeight;
        this.currentWeight = currentWeight;
    }

    @Override
    public boolean dock() {
        while (!this.tryDock()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("Interrupt in dock method.");
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean unload(int weight) {
        return (tryUnload(weight));
    }

    @Override
    public int load(int max) {
        int cargo = tryLoad(max);
        if (cargo > 0) {
            return cargo;
        }
        return 0;
    }

    @Override
    public void sail() {
        lock.lock();
        try {
            dockedShips--;
        } finally {
            lock.unlock();
        }
    }

    private boolean tryDock() {
        lock.lock();
        try {
            if (dockedShips < piersCount) {
                dockedShips++;
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    private boolean tryUnload(int weight) {
        lock.lock();
        try {
            if (currentWeight + weight > maxWeight) {
                return false;
            }

            currentWeight += weight;
            return true;
        } finally {
            lock.unlock();
        }
    }

    private int tryLoad(int max) {
        lock.lock();
        try {
            if (currentWeight >= max) {
                currentWeight -= max;
                return max;
            }

            if (max > currentWeight) {
                int weight = currentWeight;
                currentWeight = 0;
                return weight;
            }

            return 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "PortLock{" +
                "maxWeight=" + maxWeight +
                ", currentWeight=" + currentWeight +
                '}';
    }
}
