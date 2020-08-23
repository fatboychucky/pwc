package com.example.pwc;

import org.apache.commons.cli.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class PhonebookCliTest {
    private static final String EXPECTED_HELP = "usage: java -jar  target\\pwc-1.0.0-shaded.jar -a <PhonebookEntry> | -h |" + System.lineSeparator() +
            "       -p | -r <Name> | -u [-b <FILE>]" + System.lineSeparator() +
            " -a <PhonebookEntry>   PhonebookEntry(name=<NAME>, number=<NUMBER>)" + System.lineSeparator() +
            " -b <FILE>             Relative path of a file which contains the phone" + System.lineSeparator() +
            "                       entries" + System.lineSeparator() +
            " -h                    Print this help" + System.lineSeparator() +
            " -p                    Print phonebook in ascending order" + System.lineSeparator() +
            " -r <Name>             Name" + System.lineSeparator() +
            " -u                    Print the unique name from default phone book and" + System.lineSeparator() +
            "                       given phonebook" + System.lineSeparator() + System.lineSeparator() +
            "This is help menu on how to run PWC coding challenge.";
    private static final String DEFAULT_FILE = "personalBook.txt";
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void givenNoArgs_whenParseTheOption_thenItPrintsHelp() {
        String[] args = {};
        PhonebookCli.main(args);
        assertEquals(EXPECTED_HELP, outputStreamCaptor.toString().trim());
    }

    @Test
    void givenAddAndRemoveArg_whenParseTheOption_thenItPrintsHelp() {
        String[] args = {"-a", "PhonebookEntry(name=Angela McDowell, number=0418100200)", "-r", "Angela McDowell"};
        PhonebookCli.main(args);
        assertEquals(EXPECTED_HELP, outputStreamCaptor.toString().trim());
    }

    @Test
    void givenHelpAddAndBookArgs_whenParseTheOption_thenItPrintsHelp() {
        String[] args = {"-h", "-a", "PhonebookEntry(name=Angela McDowell, number=0418100200)", "-b", "test.txt"};
        PhonebookCli.main(args);
        assertEquals(EXPECTED_HELP, outputStreamCaptor.toString().trim());
    }

    @Test
    void givenHelpArg_whenProcessPB_thenReturnFalse() throws ParseException {
        String[] args = {"-h"};
        CommandLineParser parser = new DefaultParser();
        Options options = PhonebookCli.getOptions();
        CommandLine cmd = parser.parse(options, args);
        assertFalse(PhonebookCli.processPhonebook(cmd));
    }

    @Test
    void givenAddWithArgument_whenProcessPB_thenThrowMissingArgument() throws ParseException {
        String[] args = {"-a", "PhonebookEntry(name=Angela McDowell, number=0418100200)"};
        CommandLineParser parser = new DefaultParser();
        Options options = PhonebookCli.getOptions();
        CommandLine cmd = parser.parse(options, args);
        assertTrue(PhonebookCli.processPhonebook(cmd));
    }

    @Test
    void givenAddWithIncorrectArgument_whenProcessPB_thenThrowMissingArgument() throws ParseException {
        String[] args = {"-a", "Entry(name=Angela McDowell, number=0418100200)"};
        CommandLineParser parser = new DefaultParser();
        Options options = PhonebookCli.getOptions();
        CommandLine cmd = parser.parse(options, args);
        assertFalse(PhonebookCli.processPhonebook(cmd));
    }

    @Test
    void givenRemoveWithName_whenProcessPB_thenThrowMissingArgument() throws ParseException, IOException {
        String inputFilename = "src/test/resources/cli/input/remove.txt";
        String resultFilename = "src/test/resources/cli/result/remove.txt";
        String expectedFilename = "src/test/resources/cli/expected/remove.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Files.copy(Paths.get(inputFilename), Paths.get(resultFilename));
        String[] args = {"-r", "Angela McDowell", "-b", resultFilename};
        CommandLineParser parser = new DefaultParser();
        Options options = PhonebookCli.getOptions();
        CommandLine cmd = parser.parse(options, args);
        assertTrue(PhonebookCli.processPhonebook(cmd));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void givenPrint_whenProcessPB_thenPrintAllTheEntries() throws ParseException, IOException {
        String inputFilename = "src/test/resources/cli/input/print.txt";
        String[] args = {"-p", "-b", inputFilename};
        CommandLineParser parser = new DefaultParser();
        Options options = PhonebookCli.getOptions();
        CommandLine cmd = parser.parse(options, args);
        assertTrue(PhonebookCli.processPhonebook(cmd));
        String expected = "PhonebookEntry(name=Danny O'Connell, number=null)" + System.lineSeparator() +
                "PhonebookEntry(name=John Citizen, number=0414557988)";
        assertEquals(expected, outputStreamCaptor.toString().trim());
    }

    @Test
    void givenTwoFiles_whenProcessPB_thenPrintUnique() throws ParseException, IOException {
        String defaultFile = "src/test/resources/cli/input/default.txt";
        String uniqueFilename = "src/test/resources/cli/input/unique.txt";
        Files.deleteIfExists(Paths.get(DEFAULT_FILE));
        Files.copy(Paths.get(defaultFile), Paths.get(DEFAULT_FILE));
        String[] args = {"-u", "-b", uniqueFilename};
        CommandLineParser parser = new DefaultParser();
        Options options = PhonebookCli.getOptions();
        CommandLine cmd = parser.parse(options, args);
        assertTrue(PhonebookCli.processPhonebook(cmd));
        String expected = "Danny O'Connell" + System.lineSeparator() + "Dr. William Vo" + System.lineSeparator() +
                "George W. Bush Junior" + System.lineSeparator() + "Shanz Gate";
        assertEquals(expected, outputStreamCaptor.toString().trim());
    }

    @Test
    void getOptions() {
        Options options = PhonebookCli.getOptions();
        assertEquals(1, options.getRequiredOptions().size());
        assertEquals(6, options.getOptions().size());
        assertEquals("PhonebookEntry", options.getOption("a").getArgName());
        assertEquals("Name", options.getOption("r").getArgName());
        assertEquals("FILE", options.getOption("b").getArgName());
        assertNull(options.getOption("p").getArgName());
        assertNull(options.getOption("u").getArgName());
        assertNull(options.getOption("h").getArgName());
    }

    @Test
    void printHelp() {
        PhonebookCli.printHelp();
        assertEquals(EXPECTED_HELP, outputStreamCaptor.toString().trim());
    }
}