package com.kuliashou.multithreading.thread;

import com.kuliashou.multithreading.entity.Ship;
import com.kuliashou.multithreading.entity.IPort;
import com.kuliashou.multithreading.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class ShipThread implements Callable<Integer> {

    private static Logger logger = LogManager.getLogger();

    private Ship ship;
    private IPort port;

    public ShipThread(IPort port, Ship ship) {
        this.ship = ship;
        this.port = port;
    }

    @Override
    public Integer call() {
        int carriedOutCargo = 0;

        //dock
        try {
            if (port.dock()) {
                System.out.println("The ship with ID " + ship.getId() + " was dock.");
            }
        } catch (PortException e) {
            logger.error("Mooring exception.", e);
        }

        // try unload everything to port
        if (ship.hasCargo()) {
            System.out.println("Trying unload cargo with size " + ship.getCurrentWeight()
                    + " from " + ship.toString() + " to PortLock.");

                if (port.unload(ship.getCurrentWeight())) {
                    carriedOutCargo += ship.getCurrentWeight();
                    ship.setCurrentWeight(0);
                }
        }

        // try load
        if (ship.hasSpace()) {
            System.out.println("Trying load cargo with size " + ship.getAvailableSpace()
                    + " from PortLock to " + ship.toString() + ".");
            carriedOutCargo += ship.getAvailableSpace();
                ship.addCargo(port.load(ship.getAvailableSpace()));
        }

        //try sail
            port.sail();
        System.out.println(ship.toString() + " sail away." + "\n");

        return carriedOutCargo;

    }
}
