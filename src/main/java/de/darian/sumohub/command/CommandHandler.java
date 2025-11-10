package de.darian.sumohub.command;

import java.util.List;

public class CommandHandler {

    public static final String NULL_COMMAND_NAME = "SWNoIExpZWJlIEZpbmph (Null command)";
    private final CommandHashMap commandHashMap;

    public CommandHandler() {
        List<Command> commands = CommandFactory.createCommands();
        commandHashMap = new CommandHashMap(commands);
    }

    public CommandHashMap getCommandHashMap() {
        return commandHashMap;
    }
}
