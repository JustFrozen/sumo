package de.darian.sumohub.command;

import de.darian.sumohub.cli.CLI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandFactory {

    private final static ArrayList<Command> commands = new ArrayList<>();
    private static boolean created = false;

    private CommandFactory() {}

    public static List<Command> createCommands() {
        if(!created) {
            createCommands0();
            created = true;
        } else {
            System.out.println("Can't create commands. Commands were already created.");
        }
        return commands;
    }

    private static void createCommands0() {
        // This command is triggered if no matching command was found
        commands.add(createNullCommand());

        // Other commands
        commands.add(createGreetCommand());
    }

    private static Command createNullCommand() {
        final String commandName = CommandHandler.NULL_COMMAND_NAME;
        final String[] aliases = new String[] {};
        final CommandArgument[] commandArgs = new CommandArgument[] {};
        final Consumer<String> action = (param) -> {
            System.out.println("No command named \"" + param + "\". Use \"" + CLI.PREFIX + " help\" to see all commands.");
        };

        return new Command(commandName, aliases, commandArgs, action);
    }

    private static Command createGreetCommand() {
        final String commandName = "greet";
        final String[] aliases = new String[] {"gr", "greetings", "greets"};
        final CommandArgument[] commandArgs = new CommandArgument[] {};
        final Consumer<String> action = (param) -> {
            System.out.println("Hello there, " + param + "!");
        };
        return new Command(commandName, aliases, commandArgs, action);
    }

}
