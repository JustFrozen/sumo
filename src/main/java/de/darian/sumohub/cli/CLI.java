package de.darian.sumohub.cli;

import de.darian.sumohub.command.Command;
import de.darian.sumohub.command.CommandHandler;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.regex.Pattern;

public class CLI {

    public static final String PREFIX = "sumo";
    public static final String EXIT_WORD = "exit";

    private static final String commandRegex = "(^\\s*" + PREFIX + "\\s*$)|(^\\s*" + PREFIX + "\\s*.+$)";
    private static final String substituteRegex = "(\\s*sumo\\s*)|(\\s+)";
    private static final String exitRegex = "^\\s*" + EXIT_WORD + "\\s*$";
    public static final Pattern COMMAND_PATTERN = Pattern.compile(commandRegex, Pattern.CASE_INSENSITIVE);
    public static final Pattern SUBSTITUTE_PATTERN = Pattern.compile(substituteRegex, Pattern.CASE_INSENSITIVE);
    public static final Pattern EXIT_PATTERN = Pattern.compile(exitRegex, Pattern.CASE_INSENSITIVE);

    private Terminal terminal;
    private LineReader reader;
    private CommandHandler commandHandler;

    public CLI() {
        buildTerminal();
        buildLineReader();

        initializeCommands();

        startCLI();
    }

    private void startCLI() {
        System.out.println("Starting CLI...");

        String line;
        while ((line = reader.readLine("> ")) != null) {
            if (CLI.EXIT_PATTERN.matcher(line).matches()) {
                System.out.println("Exiting...");
                System.exit(0);
            }

            // only trigger command when "sumo" is prefixed
            if (CLI.COMMAND_PATTERN.matcher(line).matches()) {
                // trim command to only be subcommands
                String commandString = CLI.SUBSTITUTE_PATTERN.matcher(line).replaceAll(" ").trim();
                // get command obj from string
                Command command = commandHandler.getCommandHashMap().get(commandString);
                // execute command
                boolean success = command.execute("temp");
                // display success
                System.out.println("Command executed: " + success);
            }
        }
    }

    private void initializeCommands() {
        commandHandler = new CommandHandler();
        System.out.println("Initialized CommandHandler.");
    }

    private void buildLineReader() {
        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .history(new DefaultHistory())
                .build();
        System.out.println("Built JLine Reader.");
    }

    private void buildTerminal() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
        } catch (IOException e) {
            System.out.println("Error while building JLine Terminal: " + e.getMessage());
        }
        finally {
            if (terminal == null) {
                System.out.println("Couldn't find terminal. Exiting.");
                System.exit(0);
            }
        }
        System.out.println("Built JLine Terminal.");
    }
}
