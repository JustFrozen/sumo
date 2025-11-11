package de.darian.sumo.cli;

import de.darian.sumo.command.Command;
import de.darian.sumo.command.CommandHandler;
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

    public static final String PREFIX = "sumo";
    public static final String EXIT_WORD = "exit";
    public static String VERSION = "unknown";
    public static boolean DEBUG = false;

    private static final String commandRegex = "(^\\s*" + PREFIX + "\\s*$)|(^\\s*" + PREFIX + "\\s*.+$)";
    private static final String substituteRegex = "(\\s*sumo\\s*)|(\\s+)";
    private static final String exitRegex = "^\\s*" + EXIT_WORD + "\\s*$";
    public static final Pattern COMMAND_PATTERN = Pattern.compile(commandRegex, Pattern.CASE_INSENSITIVE);
    public static final Pattern SUBSTITUTE_PATTERN = Pattern.compile(substituteRegex, Pattern.CASE_INSENSITIVE);
    public static final Pattern EXIT_PATTERN = Pattern.compile(exitRegex, Pattern.CASE_INSENSITIVE);

    private Terminal terminal;
    private LineReader reader;
    private CommandHandler commandHandler;

    public CLI(boolean debug) {
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
            System.out.print("Starting CLI... ");
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

        // Length 1 means either NO base command provided, OR just ONE base command
        // In both cases, try to get the command from the hashmap, if the string is empty, or it's an unknown command
        // the null command will be pulled
        Command command = commandHandler.getCommandHashMap().get(commandParts[0]);
        if (commandParts.length == 1) {
            command.execute("");
        } else { // Length > 1 means there is a base command and at least one argument
            // Iterate through the arguments, skip the first one (first one is the base command)
            Arrays.stream(commandParts).skip(1).forEach(commandString -> {

            });
        }
        // Else the first element is the base command
        String baseCommandString = commandParts[0];


        // base command is the first command part
        //Command baseCommand = commandHandler.getCommandHashMap().get(commandParts[0]);
        // execute command
//        boolean success = command.execute("temp");
//        if(!success && CLI.DEBUG) {
//            System.out.println("Command " + commandString + " failed.");
//        }
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
}
