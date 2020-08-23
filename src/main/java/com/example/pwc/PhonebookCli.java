package com.example.pwc;

import org.apache.commons.cli.*;

/**
 * This class is responsible for interacting with command line inputs
 */
public class PhonebookCli {
    private static final String ADD = "a";
    private static final String BOOK = "b";
    private static final String UNIQUE = "u";
    private static final String HELP = "h";
    private static final String PRINT = "p";
    private static final String REMOVE = "r";
    private static final String DEFAULT_BOOK = "personalBook.txt";

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            printHelp();
            return;
        }
        if (!processPhonebook(cmd)) {
            printHelp();
        }
    }

    public static boolean processPhonebook(CommandLine cmd) {
        if (cmd.getOptions().length == 1 && cmd.hasOption(UNIQUE)) {
            System.err.println("Please supply another phonebook with option -b <FILE>.");
            return false;
        }
        String filename = DEFAULT_BOOK;
        if (cmd.getOptions().length == 2) {
            filename = cmd.getOptionValue(BOOK);
        }
        PhonebookService phonebookService = new PhonebookService();
        if (cmd.hasOption(ADD)) {
            return phonebookService.addEntry(filename, cmd.getOptionValue(ADD));
        } else if (cmd.hasOption(REMOVE)) {
            return phonebookService.removeEntry(filename, cmd.getOptionValue(REMOVE));
        } else if (cmd.hasOption(PRINT)) {
            return phonebookService.printPhonebook(filename);
        } else if (cmd.hasOption(UNIQUE)) {
            return phonebookService.printUniqueName(filename);
        }
        return false;
    }

    public static Options getOptions() {
        Option addOption = Option.builder(ADD).argName("PhonebookEntry").hasArg().desc("PhonebookEntry(name=<NAME>, number=<NUMBER>)").build();
        Option removeOption = Option.builder(REMOVE).argName("Name").hasArg().desc("Name").build();
        Option listOption = Option.builder(PRINT).desc("Print phonebook in ascending order").build();
        Option findUniqueOption = Option.builder(UNIQUE).desc("Print the unique name from default phone book and given phonebook").build();
        Option bookOption = Option.builder(BOOK).argName("FILE").hasArg().desc("Relative path of a file which contains the phone entries").build();
        Option helpOption = Option.builder(HELP).desc("Print this help").build();
        Options options = new Options();
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(addOption);
        optionGroup.addOption(removeOption);
        optionGroup.addOption(listOption);
        optionGroup.addOption(findUniqueOption);
        optionGroup.addOption(helpOption);
        optionGroup.setRequired(true);
        options.addOptionGroup(optionGroup);
        options.addOption(bookOption);
        return options;
    }

    public static void printHelp() {
        String footer = System.lineSeparator() + "This is help menu on how to run PWC coding challenge.";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar  target\\pwc-1.0.0-shaded.jar", null, getOptions(), footer, true);
    }
}
