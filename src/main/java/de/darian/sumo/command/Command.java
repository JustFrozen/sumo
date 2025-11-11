package de.darian.sumo.command;

import java.util.function.Consumer;

public class Command {

    private final String commandName;
    private final String shortDescription;
    private final String longDescription;
    private final String[] aliases;
    private final Command[] subCommands;
    private final String[] arguments;
    private final Consumer<String> action;

    public Command(String command, String shortDescription, String longDescription, String[] aliases, Command[] subCommands, String[] arguments, Consumer<String> action) {
        this.commandName = command;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.aliases = aliases;
        this.subCommands = subCommands;
        this.arguments = arguments;
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

    public Command[] getSubCommands() {
        return subCommands;
    }

    public String[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return commandName;
    }
}
