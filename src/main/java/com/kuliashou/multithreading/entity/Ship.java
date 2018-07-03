package com.kuliashou.multithreading.entity;

import com.kuliashou.multithreading.generator.IdGenerator;

public class Ship {
    private int id;
    private int currentWeight;
    private int maxWeight;

    public Ship(int maxWeight, int currentWeight) {
        this.id = IdGenerator.generateId();
        this.maxWeight = maxWeight;
        this.currentWeight = currentWeight;
    }

    public int getId() {
        return id;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public boolean hasCargo() {
        return currentWeight > 0;
    }

    public boolean hasSpace() {
        return currentWeight < maxWeight;
    }

    public int getAvailableSpace() {
        return maxWeight - currentWeight;
    }

    public void addCargo(int weight) {
        currentWeight += weight;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", currentWeight=" + currentWeight +
                ", maxWeight=" + maxWeight +
                '}';
    }
}
