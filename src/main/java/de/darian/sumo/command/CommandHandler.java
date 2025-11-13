package de.darian.sumo.command;

import de.darian.sumo.cli.CLI;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    public static final String NULL_COMMAND_NAME = "SWNoIExpZWJlIEZpbmph (Null command)";
    public static final int MAX_COMMAND_NAME_LENGTH = 12;
    public static final int MAX_ALIAS_LENGTH = 8;
    public static final int MAX_ALIAS_AMOUNT = 3;
    public static final int MAX_SHORT_DESCRIPTION_LENGTH = 50;
    public static final int MAX_LONG_DESCRIPTION_LENGTH = 200;
    private final CommandHashMap commandHashMap;

    public CommandHandler() {
        List<Command> commands = CommandFactory.createCommands();
        List<Command> validCommands = new ArrayList<>(validateCommands(commands));

        Command nullCommand = CommandFactory.createNullCommand();
        if(nullCommand != null) {
            validCommands.add(nullCommand);
        }

        if (CLI.DEBUG) {
            System.out.println("Created and validated " + validCommands.size() + " command(s).");
        }

        commandHashMap = new CommandHashMap(validCommands);
    }

    private List<Command> validateCommands(List<Command> commands) {
        List<Command> validCommands = new ArrayList<>();

        for (Command command : commands) {
            if (command.getCommandName().length() > 12) {
                if (CLI.DEBUG) {
                    System.out.println("Command \"" + command.getCommandName() + "\" is too long (" + command.getCommandName().length() + "/" + MAX_COMMAND_NAME_LENGTH + "). The command will not be added.");
                }
                continue;
            }

            if (command.getAliases().length > MAX_ALIAS_AMOUNT) {
                if (CLI.DEBUG) {
                    System.out.println("Too many aliases (" + command.getAliases().length + "/" + MAX_ALIAS_AMOUNT + "). The associated command \"" + command.getCommandName() + "\" will not be added.");
                }
                continue;
            }

            boolean validAliases = true;
            for (String alias : command.getAliases()) {
                if (alias.length() > 12) {
                    if (CLI.DEBUG) {
                        System.out.println("Alias \"" + alias + "\" is too long (" + alias.length() + "/" + MAX_ALIAS_LENGTH + "). The associated command \"" + command.getCommandName() + "\" will not be added.");
                    }
                    validAliases = false;
                    break;
                }
            }
            if (!validAliases) {
                continue;
            }

            if (command.getShortDescription().length() > MAX_SHORT_DESCRIPTION_LENGTH) {
                if (CLI.DEBUG) {
                    System.out.println("The short description is too long (" + command.getShortDescription().length() + "/" + MAX_SHORT_DESCRIPTION_LENGTH + "). The associated command \"" + command.getCommandName() + "\" will not be added.");
                }
                continue;
            }

            if (command.getLongDescription().length() > MAX_LONG_DESCRIPTION_LENGTH) {
                if (CLI.DEBUG) {
                    System.out.println("The long description is too long (" + command.getLongDescription().length() + "/" + MAX_LONG_DESCRIPTION_LENGTH + "). The associated command \"" + command.getCommandName() + "\" will not be added.");
                }
                continue;
            }

            validCommands.add(command);
        }
        return validCommands;
    }

    public CommandHashMap getCommandHashMap() {
        return commandHashMap;
    }
}
