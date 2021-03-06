package seedu.task.model.task;

import seedu.task.commons.exceptions.IllegalValueException;

/**
 * Represents a Task name in the task manager.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_NAME_CONSTRAINTS = "Task names should be spaces or alphanumeric characters";
    private static final String NAME_VALIDATION_REGEX = "[\\p{Alnum} ]+";

    public final String fullName;

    /**
     * Validates given name.
     *
     * @throws IllegalValueException
     *             if given name string is invalid.
     */
    public Name(String name) throws IllegalValueException {
        assert name != null;
        String newName = name.trim();
        if (!isValidName(newName)) {
            throw new IllegalValueException(MESSAGE_NAME_CONSTRAINTS);
        }
        this.fullName = newName;
    }

    /**
     * Returns true if a given string is a valid task name.
     */
    public static boolean isValidName(String test) {
        return test.matches(NAME_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Name // instanceof handles nulls
                && this.fullName.equals(((Name) other).fullName)); // state check
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

    // @@author A0147944U
    /**
     * Compares the two Names lexicographically.
     * 
     * @param anotherName
     *            Name of another Task to compare to
     */
    public int compareTo(Name anotherName) {
        return this.toString().compareTo(anotherName.toString());
    }
    // @@author

}
