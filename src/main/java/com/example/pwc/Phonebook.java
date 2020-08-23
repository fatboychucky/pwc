package com.example.pwc;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains the phone number entries.
 */
@Getter
public class Phonebook {
    private static final Pattern pattern = Pattern.compile("PhonebookEntry\\(name=([a-zA-Z][a-zA-Z'\\-.\\s]+), number=(([\\d+()\\s\\-*#]+)|(null))\\)");
    private static final String DEFAULT_BOOK = "personalBook.txt";
    private final String filename;
    private List<PhonebookEntry> phonebookEntryList = new ArrayList<>();

    /**
     * Default constructor which loads the {@link PhonebookEntry} from the default file location {@link
     * Phonebook#DEFAULT_BOOK}.
     */
    public Phonebook() {
        this(DEFAULT_BOOK);
    }

    /**
     * Default constructor which loads the {@link PhonebookEntry} from the given {@code filename}.
     *
     * @param filename The filename where the phone book is stored or will be stored.
     */
    public Phonebook(String filename) {
        this.filename = filename;
        loadPhonebook(filename);
    }

    /**
     * Loads the {@link PhonebookEntry} from the given {@code filename}. If the file exist, it loads all the entries
     * from the files. If the file does not exist, it assumes that user would like to use a new file {@code filename}
     * and any new {@link PhonebookEntry} will be save to that {@code filename} location.
     * <p>
     * Loading of the phone book entries is lenient. If entries are not compliance with the regular expression in {@link
     * Phonebook#pattern}, it'll just skip those entries without raising exception.
     *
     * @param filename The filename where the phone book is stored or will be stored.
     */
    public void loadPhonebook(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("filename is either null or empty.");
            throw new RuntimeException("filename is either null or empty.");
        }
        Path path = Paths.get(filename);
        if (Files.isDirectory(path)) {
            System.err.println(filename + " is a directory. Please check the address book filename.");
            throw new RuntimeException(filename + " is a directory.");
        }
        try {
            if (Files.exists(path)) {
                try (Stream<String> lines = Files.lines(Paths.get(filename))) {
                    phonebookEntryList = lines.map(this::createEntry).filter(Objects::nonNull).collect(Collectors.toList());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error with reading files. " + e.getMessage());
        }
    }

    /**
     * @param line String representation of {@link PhonebookEntry}. eg Entry(name=John Citizen, number=0414557988)
     * @return {@link PhonebookEntry} if the {@code line} matches the regular expression {@link Phonebook#pattern}.
     */
    public PhonebookEntry createEntry(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            return PhonebookEntry.builder().name(matcher.group(1)).number(matcher.group(2)).build();
        }
        return null;
    }

    /**
     * Add an entry to the this {@link Phonebook}
     *
     * @param line String representation of {@link PhonebookEntry}. eg Entry(name=John Citizen, number=0414557988)
     * @return {@code true} if an entry is added successfully, {@code false} otherwise
     */
    public boolean addEntry(String line) {
        PhonebookEntry phoneBookEntry = createEntry(line);
        if (phoneBookEntry != null) {
            phonebookEntryList.add(phoneBookEntry);
            persistBook();
            return true;
        }
        return false;
    }

    /**
     * Add an entry to the this {@link Phonebook}
     *
     * @param name Name to be remove from the phonebook.
     * @return {@code true} if an entry is removed successfully, {@code false} otherwise
     */
    public boolean removeEntry(String name) {
        Optional<PhonebookEntry> entryOptional = phonebookEntryList.stream().filter(e -> e.getName().compareToIgnoreCase(name) == 0).findFirst();
        if (entryOptional.isPresent()) {
            if (phonebookEntryList.remove(entryOptional.get())) {
                persistBook();
                return true;
            }
        }
        return false;
    }

    /**
     * Add an entry to the this {@link Phonebook}
     *
     * @return {@code true} if entries are printed successfully, {@code false} if no entries in this {@link Phonebook}
     */
    public boolean printPhonebook() {
        if (phonebookEntryList.isEmpty()) {
            System.out.println("Current phone book has no entries.");
            return false;
        }
        phonebookEntryList.stream().sorted().forEach(System.out::println);
        return true;
    }

    /**
     * Print unique names from this {@link Phonebook} and another {@code phonebook}
     *
     * @param phonebook Another phonebook to be compare with this phonebook
     */
    public void printUniqueEntries(Phonebook phonebook) {
        Set<String> namesFromThisPhonebook = phonebookEntryList.stream().map(PhonebookEntry::getName).collect(Collectors.toSet());
        Set<String> namesFromAnotherPhonebook = phonebook.getPhonebookEntryList().stream().map(PhonebookEntry::getName).collect(Collectors.toSet());
        Set<String> complementSet1 = namesFromThisPhonebook.stream().filter(n -> !namesFromAnotherPhonebook.contains(n)).collect(Collectors.toSet());
        Set<String> complementSet2 = namesFromAnotherPhonebook.stream().filter(n -> !namesFromThisPhonebook.contains(n)).collect(Collectors.toSet());
        Set<String> complements = Stream.concat(complementSet1.stream(), complementSet2.stream()).collect(Collectors.toSet());
        complements.stream().sorted().forEach(System.out::println);
    }

    /**
     * Persist the entityList to the file.
     *
     * @throws RuntimeException if there is {@link IOException} being thrown
     */
    private void persistBook() {
        Path path = Paths.get(filename);
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, phonebookEntryList.stream().map(phonebookEntry -> (CharSequence) phonebookEntry.toString())::iterator);
        } catch (IOException e) {
            throw new RuntimeException("File persistent failed.");
        }
    }
}
