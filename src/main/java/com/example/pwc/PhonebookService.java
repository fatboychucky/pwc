package com.example.pwc;

/**
 * This service class is responsible performing action on the {@link Phonebook}
 */
public class PhonebookService {
    public boolean addEntry(String filename, String line) {
        Phonebook phoneBook = new Phonebook(filename);
        return phoneBook.addEntry(line);
    }

    public boolean removeEntry(String filename, String line) {
        Phonebook phoneBook = new Phonebook(filename);
        return phoneBook.removeEntry(line);
    }

    public boolean printPhonebook(String filename) {
        Phonebook phonebook = new Phonebook(filename);
        return phonebook.printPhonebook();
    }

    public boolean printUniqueName(String filename) {
        Phonebook defaultPhonebook = new Phonebook();
        Phonebook anotherPhonebook = new Phonebook(filename);
        defaultPhonebook.printUniqueEntries(anotherPhonebook);
        return true;
    }
}
