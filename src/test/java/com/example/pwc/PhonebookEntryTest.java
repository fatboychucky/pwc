package com.example.pwc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhonebookEntryTest {

    @Test
    void entryWithoutName() {
        assertThrows(NullPointerException.class, () -> PhonebookEntry.builder().build());
        assertThrows(NullPointerException.class, () -> PhonebookEntry.builder().name(null).build());
    }

    @Test
    void compareCaseInsensitive() {
        PhonebookEntry entry = PhonebookEntry.builder().name("Joe Biden").build();
        PhonebookEntry anotherEntry = PhonebookEntry.builder().name("joe biden").build();
        assertEquals(0, entry.compareTo(anotherEntry));
    }
    @Test
    void compareCaseInsensitiveWithPhoneNumber() {
        PhonebookEntry entry = PhonebookEntry.builder().name("Joe Biden").number("12398392").build();
        PhonebookEntry anotherEntry = PhonebookEntry.builder().name("joe biden").number("+1(02)23021392139").build();
        assertEquals(0, entry.compareTo(anotherEntry));
    }
}