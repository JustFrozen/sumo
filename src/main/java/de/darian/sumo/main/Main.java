package de.darian.sumo.main;

import de.darian.sumo.cli.CLI;

public class Main {
    public static void main(String[] args) {
        boolean debug = Boolean.parseBoolean(args[0]);
        CLI cli = new CLI(debug);
    }
}