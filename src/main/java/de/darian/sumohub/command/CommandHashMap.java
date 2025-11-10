package de.darian.sumohub.command;

import java.util.HashMap;
import java.util.List;

public class CommandHashMap extends HashMap<String, Command> {

    public CommandHashMap(List<Command> commands) {
        super(commands.size());
        commands.forEach(cmd -> {
            super.put(cmd.getCommandName(), cmd);
            System.out.println("Added command: " + cmd.getCommandName() + ".");
        });
    }

    /**
     * Overwrites the hashmap get method.
     * Returns the command mapped to the (string) name, or returns the null command if no command was found
     */
    @Override
    public Command get(Object key) {
        if (!super.containsKey(key)) {
            return super.get(CommandHandler.NULL_COMMAND_NAME);
        }
        return super.get(key);
    }
}
