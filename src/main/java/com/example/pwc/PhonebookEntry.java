package com.example.pwc;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * This class contains a {@code name} and {@code number}. The valid {@code name} must start with an alphabet and
 * can contains apostrophe, hyphen, period, and whitespace.
 * <p>
 * The regular expression for the:
 * <li>
 *     <ul>valid name is ([a-zA-Z][a-zA-Z'\-.\s]+)</ul>
 *     <ul>valid phone number is(([\d+()\s\-*#]+)|(null))\)</ul>
 * </li>
 */
@Builder
@Data
public class PhonebookEntry implements Comparable<PhonebookEntry> {
    @NonNull
    private String name;
    private String number;

    public int compareTo(PhonebookEntry o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
