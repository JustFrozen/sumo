package de.darian.sumo.command;

import de.darian.sumo.cli.CLI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandFactory {

    private final static ArrayList<Command> commands = new ArrayList<>();
    private static boolean created = false;
    private static boolean created_null_command = false;

    private CommandFactory() {}

    public static List<Command> createCommands() {
        if(!created) {
            createCommands0();
            created = true;
        } else {
            if (CLI.DEBUG) {
                System.out.println("Can't create commands. Commands were already created.");
            }
        }
        return commands;
    }

    private static void createCommands0() {
        commands.add(createGreetCommand());
        commands.add(createVersionCommand());
        commands.add(createHelpCommand());
    }

    private static Command createVersionCommand() {
        final String commandName = "version";
        final String shortDescription = "Display the current app version";
        final String longDescription = "Display the current app version";
        final String[] aliases = new String[] {"v", "ver"};
        final CommandArgument[] commandArgs = new CommandArgument[] {};
        final Consumer<String> action = (_) -> System.out.println("sumo version: " + CLI.VERSION);

        return new Command(commandName, shortDescription, longDescription, aliases, commandArgs, action);
    }

    private static Command createHelpCommand() {
        final String commandName = "help";
        final String shortDescription = "Get help about the app and the commands";
        final String longDescription = "Get help about the app and the commands";
        final String[] aliases = new String[] {"h"};
        final CommandArgument[] commandArgs = new CommandArgument[] {};

        String printFormat = "   " +
                "%-" + CommandHandler.MAX_COMMAND_NAME_LENGTH + "s " +
                "%-" + (CommandHandler.MAX_ALIAS_AMOUNT * CommandHandler.MAX_ALIAS_LENGTH + 4) + "s " +
                "%-" + CommandHandler.MAX_SHORT_DESCRIPTION_LENGTH + "s%n"; // plus 4 to add the ", " joins

        final Predicate<Command> commandFilter = (cmd) ->
                !cmd.getCommandName().equals(CommandHandler.NULL_COMMAND_NAME);
        final Consumer<Command> commandPrinter = (cmd) -> System.out.printf(printFormat, cmd.getCommandName(),
                String.join(", ", cmd.getAliases()), cmd.getShortDescription());

        final Consumer<String> action = (_) -> {
            System.out.println("-----sumo help page-----");
            System.out.println("usage: sumo <command> [<args>]");
            System.out.println();
            System.out.println("sumo commands list:");
            System.out.printf(printFormat, "Command", "Aliases", "Short Description");
            commands.stream().filter(commandFilter).forEach(commandPrinter);
            System.out.println("-----sumo help page-----");
        };

        return new Command(commandName, shortDescription, longDescription, aliases, commandArgs, action);
    }

    public static Command createNullCommand() {
        if (!created_null_command) {
            final String commandName = CommandHandler.NULL_COMMAND_NAME;
            final String shortDescription = "";
            final String longDescription = "";
            final String[] aliases = new String[] {};
            final CommandArgument[] commandArgs = new CommandArgument[] {};
            final Consumer<String> action = (param) -> System.out.println("No command named \"" + param
                    + "\". Use \"" + CLI.PREFIX + " help\" to see all commands.");

            created_null_command = true;
            return new Command(commandName, shortDescription, longDescription, aliases, commandArgs, action);
        } else {
            if (CLI.DEBUG) {
                System.out.println("Can't create null command. Command was already created.");
            }
            return null;
        }
    }

    private static Command createGreetCommand() {
        final String commandName = "greet";
        final String shortDescription = "Greet the provided name";
        final String longDescription = "Greet the provided name";
        final String[] aliases = new String[] {"gr", "greetings", "greets"};
        final CommandArgument[] commandArgs = new CommandArgument[] {};
        final Consumer<String> action = (param) -> System.out.println("Hello there, " + param + "!");
        return new Command(commandName, shortDescription, longDescription, aliases, commandArgs, action);
    }

}
