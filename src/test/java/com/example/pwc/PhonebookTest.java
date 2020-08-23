package com.example.pwc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class PhonebookTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void givenNoFilenameInTheConstructor_whenPhonebookIsCreated_thenFilenameIsDefault() {
        Phonebook phoneBook = new Phonebook();
        assertEquals("personalBook.txt", phoneBook.getFilename());
    }

    @Test
    void givenBadFilenameInTheConstructor_whenPhonebookIsCreated_thenRuntimeExceptionThrow() {
        assertThrows(RuntimeException.class, () -> new Phonebook(null));
        assertThrows(RuntimeException.class, () -> new Phonebook(""));
        assertThrows(RuntimeException.class, () -> new Phonebook("   \t"));
        assertThrows(RuntimeException.class, () -> new Phonebook("."));
        assertThrows(RuntimeException.class, () -> new Phonebook("src/test/resources/phoneBook"));
    }

    @Test
    void givenNewFilename_whenPhonebookIsCreated_thenFilenameIsNewLocation() {
        assertEquals("src/test/resources/phoneBook/loadTest.txt", (new Phonebook("src/test/resources/phoneBook/loadTest.txt")).getFilename());
        Phonebook phoneBook = new Phonebook("src/test/resources/phoneBook/createPhonebook.txt");
        assertEquals("src/test/resources/phoneBook/createPhonebook.txt", phoneBook.getFilename());
        assertEquals(5, phoneBook.getPhonebookEntryList().size());
    }

    @Test
    void givenExistingFilename_whenPhonebookIsCreated_thenPhoneEntryGotLoaded() {
        Phonebook phoneBook = new Phonebook("src/test/resources/phoneBook/createPhonebook.txt");
        assertEquals("src/test/resources/phoneBook/createPhonebook.txt", phoneBook.getFilename());
        assertEquals(5, phoneBook.getPhonebookEntryList().size());
    }

    @Test
    void givenBadFilenameToLoad_whenPhonebookIsLoaded_thenRuntimeExceptionThrow() {
        Phonebook phoneBook = new Phonebook();
        assertThrows(RuntimeException.class, () -> phoneBook.loadPhonebook(null));
        assertThrows(RuntimeException.class, () -> phoneBook.loadPhonebook(""));
        assertThrows(RuntimeException.class, () -> phoneBook.loadPhonebook("   \t"));
        assertThrows(RuntimeException.class, () -> phoneBook.loadPhonebook("."));
        assertThrows(RuntimeException.class, () -> phoneBook.loadPhonebook("src/test/resources/phoneBook"));
    }

    @Test
    void givenFileWithSomeInvalidEntriesToLoad_whenPhonebookIsLoaded_thenGoodEntriesAreLoaded() {
        Phonebook phoneBook = new Phonebook();
        phoneBook.loadPhonebook("src/test/resources/phoneBook/2Bad3Good.txt");
        assertEquals(3, phoneBook.getPhonebookEntryList().size());
        assertEquals("Angela McDowell", phoneBook.getPhonebookEntryList().get(0).getName());
        assertEquals("0418100200", phoneBook.getPhonebookEntryList().get(0).getNumber());
        assertEquals("Mr. John-Paul Citizen", phoneBook.getPhonebookEntryList().get(1).getName());
        assertEquals("+61298002333", phoneBook.getPhonebookEntryList().get(1).getNumber());
        assertEquals("Danny O'Connell", phoneBook.getPhonebookEntryList().get(2).getName());
        assertEquals("null", phoneBook.getPhonebookEntryList().get(2).getNumber());
        phoneBook.loadPhonebook("src/test/resources/phoneBook/5Bad1Good.txt");
        assertEquals(1, phoneBook.getPhonebookEntryList().size());
        assertEquals("George W. Bush Junior", phoneBook.getPhonebookEntryList().get(0).getName());
        assertEquals("+61 (02) 9800-2333", phoneBook.getPhonebookEntryList().get(0).getNumber());
    }

    @Test
    void givenInvalidString_whenCreateEntry_thenReturnNull() {
        Phonebook phoneBook = new Phonebook();
        assertNull(phoneBook.createEntry("PhonebookEntry(name=Angela\\ McDowell, number=0418100200)"));
        assertNull(phoneBook.createEntry("PhonebookEntry(name=John-Citizen Paul*, number=0414557988)"));
        assertNull(phoneBook.createEntry("PhonebookEntry(name=Danny O'Conn''.+ell, number=null)"));
        assertNull(phoneBook.createEntry("PhonebookEntry(name=Dr. William Vo, number=-+61 *(02) 883A4 5647))"));
        assertNull(phoneBook.createEntry("PhonebookEntry(name=Daniel McBean, number=0418100200?)"));
        assertNull(phoneBook.createEntry("PhonebookEntry(name=George W. Bush Junior, number=+61(02) 9800-2333=)"));
    }

    @Test
    void givenValidString_whenCreateEntry_thenReturnEntry() {
        Phonebook phoneBook = new Phonebook();
        PhonebookEntry entry = phoneBook.createEntry("PhonebookEntry(name=Prof. Joe Manuel, number=+61#(02)   9800-2333)");
        assertEquals("Prof. Joe Manuel", entry.getName());
        assertEquals("+61#(02)   9800-2333", entry.getNumber());
        entry = phoneBook.createEntry("PhonebookEntry(name=Cr. Dany-Slate O'Field, number=+61#(02)9800-2333*1)");
        assertEquals("Cr. Dany-Slate O'Field", entry.getName());
        assertEquals("+61#(02)9800-2333*1", entry.getNumber());
    }

    @Test
    void givenNewFile_whenAddEntry_thenEntryIsPersisted() throws IOException {
        String resultFilename = "src/test/resources/phoneBook/addEntry/result/newFile.txt";
        String expectedFilename = "src/test/resources/phoneBook/addEntry/expected/newFile.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Phonebook phoneBook = new Phonebook(resultFilename);
        assertTrue(phoneBook.addEntry("PhonebookEntry(name=Prof. Joe Manuel, number=+61#(02)   9800-2333)"));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void givenExistingEntriesInTheFile_whenAddEntry_thenEntryIsPersisted() throws IOException {
        String inputFilename = "src/test/resources/phoneBook/addEntry/input/withExistingEntries.txt";
        String resultFilename = "src/test/resources/phoneBook/addEntry/result/withExistingEntries.txt";
        String expectedFilename = "src/test/resources/phoneBook/addEntry/expected/withExistingEntries.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Files.copy(Paths.get(inputFilename), Paths.get(resultFilename));
        Phonebook phoneBook = new Phonebook(resultFilename);
        assertTrue(phoneBook.addEntry("PhonebookEntry(name=George W. Bush Junior, number=+61298002333)"));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void givenNewFile_whenRemoveEntry_thenNoFileIsCreated() {
        String resultFilename = "src/test/resources/phoneBook/addEntry/result/nonExistenceFile.txt";
        Phonebook phoneBook = new Phonebook(resultFilename);
        assertFalse(phoneBook.removeEntry("McFurlan"));
        assertFalse(Files.exists(Paths.get(resultFilename)));
    }

    @Test
    void givenExistingFileWithNoMatch_whenRemoveEntry_thenFileIsNotChanged() throws IOException {
        String inputFilename = "src/test/resources/phoneBook/removeEntry/input/withExistingEntriesNoMatch.txt";
        String resultFilename = "src/test/resources/phoneBook/removeEntry/result/withExistingEntriesNoMatch.txt";
        String expectedFilename = "src/test/resources/phoneBook/removeEntry/expected/withExistingEntriesNoMatch.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Files.copy(Paths.get(inputFilename), Paths.get(resultFilename));
        Phonebook phoneBook = new Phonebook(resultFilename);
        assertFalse(phoneBook.removeEntry("McFurlan"));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void givenExistingFileWithPartialMatch_whenRemoveEntry_thenFileIsNotChanged() throws IOException {
        String inputFilename = "src/test/resources/phoneBook/removeEntry/input/withExistingEntriesNoMatch.txt";
        String resultFilename = "src/test/resources/phoneBook/removeEntry/result/withExistingEntriesNoMatch.txt";
        String expectedFilename = "src/test/resources/phoneBook/removeEntry/expected/withExistingEntriesNoMatch.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Files.copy(Paths.get(inputFilename), Paths.get(resultFilename));
        Phonebook phoneBook = new Phonebook(resultFilename);
        assertFalse(phoneBook.removeEntry("Danny"));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void givenExistingFileWithMatch_whenRemoveEntry_thenEntryHasRemovedFromTheFile() throws IOException {
        String inputFilename = "src/test/resources/phoneBook/removeEntry/input/withExistingEntries.txt";
        String resultFilename = "src/test/resources/phoneBook/removeEntry/result/withExistingEntries.txt";
        String expectedFilename = "src/test/resources/phoneBook/removeEntry/expected/withExistingEntries.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Files.copy(Paths.get(inputFilename), Paths.get(resultFilename));
        Phonebook phoneBook = new Phonebook(resultFilename);
        assertTrue(phoneBook.removeEntry("Danny O'Connell"));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void givenExistingFileWithCaseInsensitiveMatch_whenRemoveEntry_thenEntryHasRemovedFromTheFile() throws IOException {
        String inputFilename = "src/test/resources/phoneBook/removeEntry/input/withExistingEntriesCaseInsensitive.txt";
        String resultFilename = "src/test/resources/phoneBook/removeEntry/result/withExistingEntriesCaseInsensitive.txt";
        String expectedFilename = "src/test/resources/phoneBook/removeEntry/expected/withExistingEntriesCaseInsensitive.txt";
        Files.deleteIfExists(Paths.get(resultFilename));
        Files.copy(Paths.get(inputFilename), Paths.get(resultFilename));
        Phonebook phoneBook = new Phonebook(resultFilename);
        assertTrue(phoneBook.removeEntry("angela mcdowell"));
        assertLinesMatch(Files.readAllLines(Paths.get(expectedFilename)), Files.readAllLines(Paths.get(resultFilename)));
    }

    @Test
    void givenNoEntries_whenListPhonebook_thenPrintNoEntries() {
        String inputFilename = "src/test/resources/phoneBook/listEntry/input/withExistingEntriesCaseInsensitive.txt";
        Phonebook phoneBook = new Phonebook(inputFilename);
        assertFalse(phoneBook.printPhonebook());
        assertEquals("Current phone book has no entries.", outputStreamCaptor.toString().trim());
    }

    @Test
    void givenEntriesInTheFilesHaveDiff_whenListPhonebook_thenPrintUniqueNameFromBothFilesWithAscendingOrder() {
        String inputFilenameA = "src/test/resources/phoneBook/unique/input/withExistingEntriesA.txt";
        String inputFilenameB = "src/test/resources/phoneBook/unique/input/withExistingEntriesB.txt";
        String expected = "Angela McDowell" + System.lineSeparator() + "Dr. William Vo" + System.lineSeparator()
                + "John Citizen" + System.lineSeparator() + "Shaun Gate" + System.lineSeparator() + "William Shakesphere";
        Phonebook phonebookA = new Phonebook(inputFilenameA);
        Phonebook phonebookB = new Phonebook(inputFilenameB);
        phonebookA.printUniqueEntries(phonebookB);
        assertEquals(expected, outputStreamCaptor.toString().trim());
    }

    @Test
    void givenEntriesInTheFileIsSubSet_whenListPhonebook_thenPrintUniqueNameFromOneFilesWithAscendingOrder() {
        String inputFilenameA = "src/test/resources/phoneBook/unique/input/withExistingEntriesA.txt";
        String inputFilenameC = "src/test/resources/phoneBook/unique/input/withExistingEntriesC.txt";
        String expected = "Dr. William Vo" + System.lineSeparator() + "John Citizen";
        Phonebook phonebookA = new Phonebook(inputFilenameA);
        Phonebook phonebookB = new Phonebook(inputFilenameC);
        phonebookA.printUniqueEntries(phonebookB);
        assertEquals(expected, outputStreamCaptor.toString().trim());
    }

    @Test
    void givenEntriesInTheFileIsSuperSet_whenListPhonebook_thenPrintUniqueNameFromOneFilesWithAscendingOrder() {
        String inputFilenameA = "src/test/resources/phoneBook/unique/input/withExistingEntriesA.txt";
        String inputFilenameC = "src/test/resources/phoneBook/unique/input/withExistingEntriesC.txt";
        String expected = "Dr. William Vo" + System.lineSeparator() + "John Citizen";
        Phonebook phonebookA = new Phonebook(inputFilenameA);
        Phonebook phonebookB = new Phonebook(inputFilenameC);
        phonebookB.printUniqueEntries(phonebookA);
        assertEquals(expected, outputStreamCaptor.toString().trim());
    }

    @Test
    void givenEntriesInTheFileIsSame_whenListPhonebook_thenPrintUniqueNameFromOneFilesWithAscendingOrder2() {
        String inputFilenameA = "src/test/resources/phoneBook/unique/input/withExistingEntriesA.txt";
        String inputFilenameA2 = "src/test/resources/phoneBook/unique/input/withExistingEntriesA.txt";
        String expected = "";
        Phonebook phonebookA = new Phonebook(inputFilenameA);
        Phonebook phonebookA2 = new Phonebook(inputFilenameA2);
        phonebookA.printUniqueEntries(phonebookA2);
        assertEquals(expected, outputStreamCaptor.toString().trim());
    }

    @Test
    void givenNoOverlap_WhenPrintUnique_thenBothList() {
    }
}