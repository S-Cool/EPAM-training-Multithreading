package com.kuliashou.multithreading.entity;

import com.kuliashou.multithreading.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PortSemaphore implements IPort {

    private static Logger logger = LogManager.getLogger();

    private int maxWeight;
    private int currentWeight;
    private Semaphore semaphore;

    private static PortSemaphore instance = null;
    private static AtomicBoolean instanceCreated = new AtomicBoolean(false);
    private static Lock lock = new ReentrantLock();

    public static PortSemaphore getInstance(int piersCount, int maxWeight, int currentWeight) throws PortException {
        if (instanceCreated.compareAndSet(false, false)) {
            lock.lock();
            try {
                if (instanceCreated.compareAndSet(false, true)) {
                    instance = new PortSemaphore(piersCount, maxWeight, currentWeight);
                }
            } catch (Exception e) {
                throw new PortException("Exception initialize PortSemaphore.", e);
            }
        }
        return instance;
    }

    private PortSemaphore(int piersCount, int maxWeight, int currentWeight) {
        this.maxWeight = maxWeight;
        this.currentWeight = currentWeight;
        this.semaphore = new Semaphore(piersCount, true);
    }

    @Override
    public boolean dock() throws PortException {
        try {
            semaphore.acquire();
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Interrupt in dock method.");
            throw new PortException("Interrupt in dock method.", e);
        }

        return true;
    }

    public boolean unload(int weight) {
        if (currentWeight + weight > maxWeight) {
            return false;
        }
        currentWeight += weight;
        return true;
    }

    public int load(int max) {
        int cargo = tryLoad(max);
        if (cargo > 0) {

            return cargo;
        }
        return 0;
    }

    public void sail() {
        semaphore.release();
    }

    private int tryLoad(int max) {
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
    }

    @Override
    public String toString() {
        return "PortSemaphore{" +
                "maxWeight=" + maxWeight +
                ", currentWeight=" + currentWeight +
                '}';
    }
}
