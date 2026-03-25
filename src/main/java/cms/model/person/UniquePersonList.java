package cms.model.person;

import static cms.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;

import cms.model.person.exceptions.DuplicatePersonException;
import cms.model.person.exceptions.DuplicatePersonFieldException;
import cms.model.person.exceptions.PersonNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of persons that enforces uniqueness between its elements and does not
 * allow nulls.
 * A person is considered unique by comparing using
 * {@code Person#isSamePerson(Person)}. As such, adding and updating of
 * persons uses Person#isSamePerson(Person) for equality so as to ensure that
 * the person being added or updated is
 * unique in terms of identity in the UniquePersonList. However, the removal of
 * a person uses Person#equals(Object) so
 * as to ensure that the person with exactly the same fields will be removed.
 * <p>
 * Supports a minimal set of list operations.
 *
 * @see Person#isSamePerson(Person)
 */
public class UniquePersonList implements Iterable<Person> {

    private final ObservableList<Person> internalList = FXCollections.observableArrayList();
    private final ObservableList<Person> internalUnmodifiableList = FXCollections
            .unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent person as the given argument.
     */
    public boolean contains(Person toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSamePerson);
    }

    /**
     * Returns true if the list contains a person with a conflicting field as the
     * given argument.
     */
    public boolean containsFieldConflict(Person toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(existingPerson -> toCheck.findConflictingField(existingPerson) != null);
    }

    /**
     * Adds a person to the list.
     * The person must not already exist in the list.
     */
    public void add(Person toAdd) {
        requireNonNull(toAdd);
        Person conflictingPerson = findPersonWithSameIdentity(toAdd);
        if (conflictingPerson != null) {
            throw new DuplicatePersonException(conflictingPerson);
        }
        ensureNoFieldConflict(toAdd, null);
        internalList.add(toAdd);
    }

    /**
     * Replaces the person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the list.
     * The person identity of {@code editedPerson} must not be the same as another
     * existing person in the list.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        if (!target.isSamePerson(editedPerson)) {
            Person duplicatePerson = findPersonWithSameIdentity(editedPerson);
            if (duplicatePerson != null) {
                throw new DuplicatePersonException(duplicatePerson);
            }
        }

        ensureNoFieldConflict(editedPerson, target);
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
        ensurePersonsAreUnique(persons);

        internalList.setAll(persons);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Person> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
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
     * Ensures that the specified list of persons contains only unique persons.
     *
     * @param persons the list of persons to check
     * @throws DuplicatePersonException      if a duplicate person is found
     * @throws DuplicatePersonFieldException if a duplicate field is found between
     *                                       any two persons
     */
    private void ensurePersonsAreUnique(List<Person> persons) {
        for (int i = 0; i < persons.size() - 1; i++) {
            for (int j = i + 1; j < persons.size(); j++) {
                if (persons.get(i).isSamePerson(persons.get(j))) {
                    throw new DuplicatePersonException(persons.get(j));
                }

                FieldConflict conflict = persons.get(i).findConflictingField(persons.get(j));
                if (conflict != null) {
                    throw new DuplicatePersonFieldException(conflict);
                }
            }
        }
    }

    /**
     * Ensures that the specified person does not conflict with any existing person
     * in the list.
     *
     * @param personToCheck  the person to check for conflicts
     * @param personToIgnore the person to ignore when checking for conflicts
     * @throws DuplicatePersonFieldException if a duplicate field is found between
     *                                       {@code personToCheck} and any existing
     *                                       person in the list (except for
     *                                       {@code personToIgnore})
     */
    private void ensureNoFieldConflict(Person personToCheck, Person personToIgnore) {
        for (Person existingPerson : internalList) {
            if (existingPerson == personToIgnore) {
                continue;
            }

            FieldConflict conflict = personToCheck.findConflictingField(existingPerson);
            if (conflict != null) {
                throw new DuplicatePersonFieldException(conflict);
            }
        }
    }

    /**
     * Returns the person in the list with the same identity as {@code personToCheck}, if any.
     */
    private Person findPersonWithSameIdentity(Person personToCheck) {
        for (Person existingPerson : internalList) {
            if (existingPerson.isSamePerson(personToCheck)) {
                return existingPerson;
            }
        }

        return null;
    }

}
