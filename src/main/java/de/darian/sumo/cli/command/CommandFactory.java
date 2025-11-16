package de.darian.sumo.cli.command;

import de.darian.sumo.cli.CLI;
import de.darian.sumo.cli.command.commandhelper.ArgumentsConsumer;

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
        final Command greetCommand = createGreetCommand();
        final Command versionCommand = createVersionCommand();
        final Command helpCommand = createHelpCommand();
        final Command initCommand = createInitCommand();

        commands.add(greetCommand);
        commands.add(versionCommand);
        commands.add(helpCommand);
        commands.add(initCommand);

        // Note: NullCommand is created later by CommandHandler to avoid validation
    }

    private static Command createVersionCommand() {
        final String commandName = "version";
        final String shortDescription = "Display the current app version";
        final String longDescription = "Display the current app version";
        final String[] aliases = new String[] {"v", "ver"};
        final Command[] subCommands = new Command[] {};
        final String[] arguments = new String[] {};
        final ArgumentsConsumer action = (_) -> {
            System.out.println("sumo version: " + CLI.VERSION);
            return true;
        };

        return new Command(commandName, shortDescription, longDescription, aliases, subCommands, arguments, action);
    }

    private static Command createInitCommand() {
        final String commandName = "init";
        final String shortDescription = "Initializes the version control";
        final String longDescription = "Initializes the version control";
        final String[] aliases = new String[] {"i"};
        final Command[] subCommands = new Command[] {};
        final String[] arguments = new String[] {};
        final ArgumentsConsumer action = (_) -> CLI.sumo().init();

        return new Command(commandName, shortDescription, longDescription, aliases, subCommands, arguments, action);
    }

    private static Command createHelpCommand() {
        final String commandName = "help";
        final String shortDescription = "Get help about the app and the commands";
        final String longDescription = "Get help about the app and the commands";
        final String[] aliases = new String[] {"h"};
        final Command[] subCommands = new Command[] {};
        final String[] arguments = new String[] {};

        String printFormat = "   " +
                "%-" + CommandHandler.MAX_COMMAND_NAME_LENGTH + "s " +
                "%-" + (CommandHandler.MAX_ALIAS_AMOUNT * CommandHandler.MAX_ALIAS_LENGTH + 4) + "s " +
                "%-" + CommandHandler.MAX_SHORT_DESCRIPTION_LENGTH + "s%n"; // plus 4 to add the ", " joins

        final Predicate<Command> isNullCommand = (cmd) ->
                (cmd.getCommandName().equals(CommandHandler.NULL_COMMAND_NAME));
        final Consumer<Command> commandPrinter = (cmd) -> System.out.printf(printFormat, cmd.getCommandName(),
                String.join(", ", cmd.getAliases()), cmd.getShortDescription());

        final ArgumentsConsumer action = (_) -> {
            System.out.println("-----sumo help page-----");
            System.out.println("usage: sumo <command> [<args>] [<flags>]");
            System.out.println();
            System.out.println("sumo commands list:");
            System.out.printf(printFormat, "Command", "Aliases", "Short Description");
            commands.stream().filter(isNullCommand.negate()).forEach(commandPrinter);
            System.out.println("-----sumo help page-----");
            return true;
        };

        return new Command(commandName, shortDescription, longDescription, aliases, subCommands, arguments, action);
    }

    public static Command createNullCommand() {
        if (!created_null_command) {
            created_null_command = true;
            final ArgumentsConsumer action = (arguments) -> {
                if (arguments.length == 0 || (arguments.length == 1 && arguments[0].isEmpty())) {
                    System.out.print("No command provided. ");
                } else {
                    System.out.print("No command \"" + arguments[0] + "\" found. ");
                }
                System.out.println(CLI.HELP_SENTENCE);
                return true;
            };
            return new Command(CommandHandler.NULL_COMMAND_NAME, "", "", new String[] {}, null, null, action);
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
        final Command[] subCommands = new Command[] {};
        final String[] arguments = new String[] {};
        final ArgumentsConsumer action = (args) -> {
            if (args.length == 0 || (args.length == 1 && args[0].isEmpty())) {
                System.out.println("No one to greet provided. " + CLI.HELP_SENTENCE);
            } else {
                System.out.println("Hello there, " + String.join(" ", args) + "!");
            }
            return true;
        };
        return new Command(commandName, shortDescription, longDescription, aliases, subCommands, arguments, action);
    }

}
