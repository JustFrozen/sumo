package de.darian.sumo.command;

import de.darian.sumo.cli.CLI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Command {

    private final String commandName;
    private final String shortDescription;
    private final String longDescription;
    private final String[] aliases;
    private final Command[] subCommands;
    private final String[] arguments;
    private final ArgumentsConsumer action;

    public Command(@NotNull String command, @Nullable String shortDescription, @Nullable String longDescription, @NotNull String[] aliases, @Nullable Command[] subCommands, @Nullable String[] arguments, @NotNull ArgumentsConsumer action) {
        this.commandName = command;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.aliases = aliases;
        this.subCommands = subCommands;
        this.arguments = arguments;
        this.action = action;
    }

    public boolean execute(String[] arguments) {
        boolean success;
        try {
            success = action.accept(arguments);
        } catch (Exception e) {
            success = false;
            System.out.println("An error occurred while executing command \"" + commandName + "\".");
            if (CLI.DEBUG) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return success;
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
