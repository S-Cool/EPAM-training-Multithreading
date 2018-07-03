package com.kuliashou.multithreading.generator;

public class IdGenerator {
    private static int id;

    public static int generateId() {
        return id++;
    }

}
