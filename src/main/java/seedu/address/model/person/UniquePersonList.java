package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * A list of persons that enforces uniqueness between its elements and does not allow nulls.
 * A person is considered unique by comparing using {@code Person#isSamePerson(Person)}. As such, adding and updating of
 * persons uses Person#isSamePerson(Person) for equality so as to ensure that the person being added or updated is
 * unique in terms of identity in the UniquePersonList. However, the removal of a person uses Person#equals(Object) so
 * as to ensure that the person with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Person#isSamePerson(Person)
 */
public class UniquePersonList implements Iterable<Person> {

    private final ObservableList<Person> internalList = FXCollections.observableArrayList();
    private final ObservableList<Person> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent person as the given argument.
     */
    public boolean contains(Person toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSamePerson);
    }

    /**
     * Adds a person to the list.
     * The person must not already exist in the list.
     */
    public void add(Person toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicatePersonException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the list.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the list.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        if (!target.isSamePerson(editedPerson) && contains(editedPerson)) {
            throw new DuplicatePersonException();
        }

        internalList.set(index, editedPerson);
    }

    /**
     * Removes the equivalent person from the list.
     * The person must exist in the list.
     */
    public void remove(Person toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new PersonNotFoundException();
        }
    }

    public void setPersons(UniquePersonList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        requireAllNonNull(persons);
        if (!personsAreUnique(persons)) {
            throw new DuplicatePersonException();
        }

        internalList.setAll(persons);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Person> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Pins the person in the list, ensuring they always appear as the first element.
     * The person must exist in the list.
     */
    public void pinPerson(Person toPin) {
        requireNonNull(toPin);

        int index = internalList.indexOf(toPin);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        // Remove and reinsert at the first position
        internalList.remove(index);
        internalList.add(0, toPin);
    }

    /**
     * Unpins the person in the list if they were previously pinned.
     * The person must exist in the list.
     */
    public void unpinPerson(Person toUnpin) {
        requireNonNull(toUnpin);

        int index = internalList.indexOf(toUnpin);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        // TODO: Remove and reinsert below all other pinned persons
        //internalList.remove(index);
        //internalList.add(0, toPin);
    }

    /**
     * Moves all the pinned people to the top of the list, retaining the sort order among the pinned people.
     */
    public void prioritisePins() {
        int pinnedIndex = 0;
        for (Person person : internalList) {
            if (person.getPin().isPinned()) {
                int index = internalList.indexOf(person);
                internalList.remove(index);
                internalList.add(pinnedIndex, person);
                pinnedIndex++;
            }
        }
    }


    @Override
    public Iterator<Person> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniquePersonList)) {
            return false;
        }

        UniquePersonList otherUniquePersonList = (UniquePersonList) other;
        return internalList.equals(otherUniquePersonList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /**
     * Returns true if {@code persons} contains only unique persons.
     */
    private boolean personsAreUnique(List<Person> persons) {
        for (int i = 0; i < persons.size() - 1; i++) {
            for (int j = i + 1; j < persons.size(); j++) {
                if (persons.get(i).isSamePerson(persons.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sorts the list of persons based on the specified prefix such as name, phone number, email address, address, tags.
     *
     * @param prefix The prefix indicates the sorting criteria.
     */
    public void sortBy(String prefix) {
        switch (prefix) {
        case "n/":
            sortByName();
            break;
        case "p/":
            sortByPhoneNumber();
            break;
        case "e/":
            sortByEmailAddress();
            break;
        case "a/":
            sortByAddress();
            break;
        case "t/":
            sortByTags();
            break;
        default:
            break;
        }
    }

    /**
     * Sort the list by name.
     */
    private void sortByName() {
        internalList.sort(Comparator.comparing(p -> p.getName().toString()));
        prioritisePins();
    }

    /**
     * Sort the list by phone number.
     */
    private void sortByPhoneNumber() {
        internalList.sort(Comparator.comparing(p -> new BigInteger(p.getPhone().toString())));
        prioritisePins();
    }

    /**
     * Sort the list by email address.
     */
    private void sortByEmailAddress() {
        internalList.sort(Comparator.comparing(p -> p.getEmail().toString()));
        prioritisePins();
    }

    /**
     * Sort the list by address.
     */
    private void sortByAddress() {
        internalList.sort(Comparator.comparing(p -> p.getAddress().toString()));
        prioritisePins();
    }

    /**
     * Sort the list by tags.
     */
    private void sortByTags() {
        internalList.sort(Comparator.comparing(p -> p.getTags().toString()));
        prioritisePins();
    }
}
