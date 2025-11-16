package de.darian.sumo.cli;

import de.darian.sumo.cli.command.Command;
import de.darian.sumo.cli.command.CommandHandler;
import de.darian.sumo.sumo.Sumo;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class CLI {

    public static final String PREFIX = Sumo.APP_NAME;
    public static final String EXIT_WORD = "exit";
    public static String VERSION = "unknown";
    private static final String commandRegex = "(^\\s*" + PREFIX + "\\s*$)|(^\\s*" + PREFIX + "\\s*.+$)";
    private static final String substituteRegex = "(\\s*sumo\\s*)|(\\s+)";
    private static final String exitRegex = "^\\s*" + EXIT_WORD + "\\s*$";
    public static final Pattern COMMAND_PATTERN = Pattern.compile(commandRegex, Pattern.CASE_INSENSITIVE);
    public static final Pattern SUBSTITUTE_PATTERN = Pattern.compile(substituteRegex, Pattern.CASE_INSENSITIVE);
    public static final Pattern EXIT_PATTERN = Pattern.compile(exitRegex, Pattern.CASE_INSENSITIVE);
    public static final String HELP_SENTENCE = "Use \"" + CLI.PREFIX + " help\" to see all commands.";

    private static Sumo sumo;
    public static boolean DEBUG;
    private Terminal terminal;
    private LineReader reader;
    private CommandHandler commandHandler;

    public CLI(Sumo sumo, boolean debug) {
        CLI.sumo = sumo;
        CLI.DEBUG = debug;

        getVersionFromPom();

        buildTerminal();
        buildLineReader();

        initializeCommands();


        startCLI();
    }

    private void getVersionFromPom() {
        Properties properties = new Properties();
        String version = "unknown";
        try (InputStream stream = CLI.class.getResourceAsStream(
                "/META-INF/maven/de.darian/sumohub/pom.properties")) {
            if (stream != null) {
                properties.load(stream);
                version = properties.getProperty("version", version);
            }
        } catch (IOException e) {
            if (DEBUG) {
                System.out.println("Error while retrieving the app version: " + e.getMessage());
            }
            System.out.println("Could not read the version. Exiting.");
            System.exit(0);
        }
        CLI.VERSION = version;
    }

    private void startCLI() {
        if (CLI.DEBUG) {
            System.out.print("Starting CLI (DEBUG)... ");
        }
        System.out.println("sumo v-" + CLI.VERSION);

        String line;
        while ((line = reader.readLine("> ")) != null) {
            if (CLI.EXIT_PATTERN.matcher(line).matches()) {
                System.out.println("Exiting...");
                System.exit(0);
            }

            // only trigger command when "sumo" is prefixed
            if (CLI.COMMAND_PATTERN.matcher(line).matches()) {
                handleCommandLineInput(line);
            }
        }
    }

    private void handleCommandLineInput(String line) {
        // trim prefix so there are only commands left
        String cleanedCommandString = CLI.SUBSTITUTE_PATTERN.matcher(line).replaceAll(" ").trim();
        String[] commandParts = cleanedCommandString.split(" ");

        // Length 1 commandParts Array means either NO base command provided, OR just ONE base command
        // In both cases, try to get the command from the hashmap, weather the string is empty or it's an unknown command
        // the null command will be pulled
        // Else, the actual command will be pulled and executed
        Command command = commandHandler.getCommand(commandParts[0]);
        String[] commandArguments = Arrays.stream(commandParts).skip(1).toArray(String[]::new);

        // if we have the null command, we want to provide it with every commandPart, not only the commandArguments
        boolean success;
        if (command.getCommandName().equals(CommandHandler.NULL_COMMAND_NAME)) {
            success = command.execute(commandParts);
        } else {
            success = command.execute(commandArguments);
        }

        if (!success) {
            System.out.println("Something went wrong while executing command: " + command.getCommandName());
        }
    }

    private void initializeCommands() {
        commandHandler = new CommandHandler();
        if(DEBUG) {
            System.out.println("Initialized CommandHandler.");
        }
    }

    private void buildLineReader() {
        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .history(new DefaultHistory())
                .build();
        if (CLI.DEBUG) {
            System.out.println("Built JLine Reader.");
        }
    }

    private void buildTerminal() {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
        } catch (IOException e) {
            if(CLI.DEBUG) {
                System.out.println("Error while building JLine Terminal: " + e.getMessage());
            }
        }
        finally {
            if (terminal == null) {
                System.out.println("Couldn't find terminal. Exiting.");
                System.exit(0);
            }
        }
        if (DEBUG) {
            System.out.println("Built JLine Terminal.");
        }
    }

    public static Sumo sumo() {
        return sumo;
    }
}
