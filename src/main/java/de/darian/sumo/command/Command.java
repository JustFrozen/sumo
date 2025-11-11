package de.darian.sumo.command;

import java.util.function.Consumer;

public class Command {

    private final String commandName;
    private final String shortDescription;
    private final String longDescription;
    private final String[] aliases;
    private final CommandArgument[] commandArguments;
    private final Consumer<String> action;

    public Command(String command, String shortDescription, String longDescription, String[] aliases, CommandArgument[] commandArguments, Consumer<String> action) {
        this.commandName = command;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.aliases = aliases;
        this.commandArguments = commandArguments;
        this.action = action;
    }

    public boolean execute(String parameter) {
        action.accept(parameter);
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String[] getAliases() {
        return aliases;
    }

    public CommandArgument[] getCommandArguments() {
        return commandArguments;
    }

    @Override
    public String toString() {
        return commandName + "\t[" + String.join(", ", aliases) + "]";
    }
}
