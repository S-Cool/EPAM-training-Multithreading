package com.kuliashou.multithreading.main;

import com.kuliashou.multithreading.entity.PortLock;
import com.kuliashou.multithreading.entity.PortSemaphore;
import com.kuliashou.multithreading.entity.Ship;
import com.kuliashou.multithreading.entity.IPort;
import com.kuliashou.multithreading.exception.PortException;
import com.kuliashou.multithreading.thread.ShipThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    private static Logger logger = LogManager.getLogger();

    private static final int COUNT_THREAD = 10;
    private static final int COUNT_PIERS_PORT = 5;
    private static final int MAX_WEIGHT_PORT = 500;
    private static final int CURRENT_WEIGHT_PORT = 100;

    private static int curriedOutCargo;

    public static void main(String[] args) {

        IPort port = null;

        //Port for PortLock variant
//        try {
//            port = PortLock.getInstance(COUNT_PIERS_PORT, MAX_WEIGHT_PORT, CURRENT_WEIGHT_PORT);
//        } catch (PortException e) {
//        logger.error("Exception initialize PortLock.", e);
//        }

        //Port for PortSemaphore variant
        try {
            port = PortSemaphore.getInstance(COUNT_PIERS_PORT, MAX_WEIGHT_PORT, CURRENT_WEIGHT_PORT);
        } catch (PortException e) {
            logger.error("Exception initialize PortSemaphore.", e);
        }

        //Ship Generator
        LinkedList<Ship> ships = new LinkedList<>();
        Random random = new Random();
        for (int i = 0; i < COUNT_THREAD; i++) {
            int maxWeight = 10 + random.nextInt(50);
            int currentWeight = random.nextInt(maxWeight);
            ships.add(new Ship(maxWeight, currentWeight));
        }

        ArrayList<Future<Integer>> list = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(COUNT_THREAD);
        for (int j = 0; j < COUNT_THREAD; j++) {
            list.add(executor.submit(new ShipThread(port, ships.get(j))));
        }

        for (Future future : list) {
            try {
                curriedOutCargo += (Integer) future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Thread has interrupted or attempting " +
                        "to retrieve the result of a task that aborted by throwing an exception.", e);
            }
        }

        System.out.println("During the shift " + curriedOutCargo + " cargo were serviced.");
        executor.shutdown();

    }

}

