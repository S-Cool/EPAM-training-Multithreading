package com.kuliashou.multithreading.entity;

import com.kuliashou.multithreading.exception.PortException;

public interface IPort {
    /**
     * The ship docks at the port
     */
    boolean dock() throws PortException;

    /**
     * Unloads cargo into the port
     */
    boolean unload(int weight);

    /**
     * Loads cargo on the ship
     */
    int load(int max);

    /**
     * The ship sail away
     */
    void sail();
}
