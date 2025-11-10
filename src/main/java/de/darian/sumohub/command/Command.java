package de.darian.sumohub.command;

import java.util.function.Consumer;

public class Command {

    private final String commandName;
    private final String[] aliases;
    private final CommandArgument[] commandArguments;
    private final Consumer<String> action;


    public Command(String command, String[] aliases, CommandArgument[] commandArguments, Consumer<String> action) {
        this.commandName = command;
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

    public String[] getAliases() {
        return aliases;
    }

    public CommandArgument[] getCommandArguments() {
        return commandArguments;
    }
}
