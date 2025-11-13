package de.darian.sumo.main;

import de.darian.sumo.cli.CLI;
import de.darian.sumo.sumo.Sumo;

public class Main {
    public static void main(String[] args) {
        boolean debug = false;
        if (args.length != 0) {
            debug = Boolean.parseBoolean(args[0]);
        }
        CLI cli = new CLI(debug);
        Sumo sumo = new Sumo();
    }
}