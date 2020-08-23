## PWC code challenge
This challenge was written as a maven java project. It consists of:
* PhonebookEntry 
* Phonebook
* PhonebookService, and
* PhonebookCli

### Features
Phonebooks are stored in files for the persistent. This application provides the following features:
1. Add entries to phonebook
1. Remove entries to phonebook
1. Print phonebook entries
1. Print Unique of two phonebooks

These features can be used for the default phonebook (**personalBook.txt**) or another phonebook using "**-b**" option.

### Assumption
The following assumptions were made in this application:
###### PhonebookEntry
- Names are case insensitive
- Names must start with an alphabet and can contain one of more apostrophe, hyphen, period, and whitespace

###### Phonebook
- The default phonebook is personalBook.txt and it will not be created until an entry is added
- When removing an entry, the name can be case insensitive, however, the name has to be matched entirely. 
eg. If entry in the phonebook is "Bob Jones", the name to be has to be "Bob Jones", and 
it can't be "Bob" or "Jones" only.

## Software Prerequisites 
- Maven 3.6+
- JDK 8 

## How to run this program
Run the following maven command to build the executable jar file
mvn clean install

###### To add an entry to default phonebook
java -jar target\pwc-1.0.0-shaded.jar -a "PhonebookEntry(name=Angela McDowell, number=0418100200)"

###### To add an entry to other phonebook
java -jar target\pwc-1.0.0-shaded.jar -a "PhonebookEntry(name=Angela McDowell, number=0418100200)" -b anotherPhonebook.txt

###### To remove an entry to default phonebook
java -jar target\pwc-1.0.0-shaded.jar -r "Angela McDowell"

###### To remove an entry to other phonebook
java -jar target\pwc-1.0.0-shaded.jar -r "Angela McDowell" -b anotherPhonebook.txt

###### To print entries from the default phonebook
java -jar target\pwc-1.0.0-shaded.jar -p "Angela McDowell"

###### To print entries from the other phonebook
java -jar target\pwc-1.0.0-shaded.jar -p "Angela McDowell" -b anotherPhonebook.txt

###### To print the name of union of all the relative complements of the default phonebook and another phonebook
java -jar target\pwc-1.0.0-shaded.jar -u "Angela McDowell" -b anotherPhonebook.txt



