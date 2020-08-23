package com.example.pwc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class PhonebookServiceTest {
    private static final String DEFAULT_BOOK = "personalBook.txt";
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void addEntry() throws IOException{
        String inputFilename = "src/test/resources/service/input/default.txt";
        String resultFilename = "src/test/resources/service/result/add.txt";
        String expectedFilename = "src/test/resources/service/expected/add.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Files.copy(Paths.get(inputFilename), Paths.get(resultFilename));
        PhonebookService service = new PhonebookService();
        assertTrue(service.addEntry(resultFilename, "PhonebookEntry(name=Billy-Jean McBill, number=++6188779-0992)"));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void removeEntry() throws IOException {
        String inputFilename = "src/test/resources/service/input/default.txt";
        String resultFilename = "src/test/resources/service/result/remove.txt";
        String expectedFilename = "src/test/resources/service/expected/remove.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Files.copy(Paths.get(inputFilename), Paths.get(resultFilename));
        PhonebookService service = new PhonebookService();
        assertTrue(service.removeEntry(resultFilename, "Angela McDowell"));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void printPhonebook() throws IOException {
        PhonebookService service = new PhonebookService();
        String inputFilename = "src/test/resources/service/input/default.txt";
        assertTrue(service.printPhonebook(inputFilename));
        String expected = "PhonebookEntry(name=Angela McDowell, number=0418100200)" + System.lineSeparator() +
                "PhonebookEntry(name=Danny O'Connell, number=null)" + System.lineSeparator() +
                "PhonebookEntry(name=Dr. William Vo, number=+61 (02) 8834 5647))" + System.lineSeparator() +
                "PhonebookEntry(name=George W. Bush Junior, number=+61298002333)" + System.lineSeparator() +
                "PhonebookEntry(name=John Citizen, number=0414557988)" + System.lineSeparator() +
                "PhonebookEntry(name=Shanz Gate, number=38888#1))";
        assertEquals(expected, outputStreamCaptor.toString().trim());
    }

    @Test
    void printUniqueEntries() throws IOException {
        String inputFilename1 = "src/test/resources/service/input/default.txt";
        String inputFilename2 = "src/test/resources/service/input/anotherPhonebook.txt";
        Files.deleteIfExists(Paths.get(DEFAULT_BOOK));
        Files.copy(Paths.get(inputFilename1), Paths.get(DEFAULT_BOOK));
        PhonebookService service = new PhonebookService();
        assertTrue(service.printUniqueName(inputFilename2));
        String expectedNames = "Shanz Gate";
        assertEquals(expectedNames, outputStreamCaptor.toString().trim());
    }
}