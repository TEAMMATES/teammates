package teammates.common.datatransfer.attributes;

import java.util.List;
import java.util.function.Consumer;

import teammates.storage.entity.BaseEntity;

/**
 * Base class for Attribute classes (Attribute classes represent attributes of
 * persistable entities).
 *
 * @param <E> type of persistable entity being wrapped
 */
public abstract class EntityAttributes<E extends BaseEntity> {

    /**
     * Returns true if the attributes represent a valid state for the entity.
     */
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    /**
     * Returns a {@code List} of strings, one string for each attribute whose
     *         value is invalid. The string explains why the value is invalid
     *         and what should values are acceptable. These explanations are
     *         good enough to show to the user. Returns an empty {@code List} if
     *         all attributes are valid.
     */
    public abstract List<String> getInvalidityInfo();

    /**
     * Returns a {@code Object} corresponding to the attributes defined by {@code this}
     *            {@link EntityAttributes} class.
     */
    public abstract E toEntity();

    /**
     * Perform any sanitization that needs to be done before saving.
     * e.g. trim strings
     */
    public abstract void sanitizeForSaving();

    /**
     * Adds {@code error} to {@code errors} if {@code error} is a non-empty string.
     *
     * @param error An error message, possibly empty.
     * @param errors A List of errors, to add {@code error} to.
     */
    void addNonEmptyError(String error, List<String> errors) {
        if (error.isEmpty()) {
            return;
        }

        errors.add(error);
    }

    /**
     * If {@code error} is a non-empty string, prefixes it with {@code prefix} and adds the result to {@code errors}.
     *
     * @param error An error message, possibly empty.
     * @param errors A List of errors, to add {@code error} to.
     * @param prefix A prefix to be added in front of the {@code error}.
     */
    void addNonEmptyErrorWithPrefix(String error, List<String> errors, String prefix) {
        if (error.isEmpty()) {
            return;
        }

        errors.add(prefix.concat(error));
    }

    /**
     * Helper class to determine whether a field should be updated or not.
     *
     * <p>The class behaves like {@link java.util.Optional} but allows null value.
     *
     * @param <T> type of object being updated
     */
    protected static class UpdateOption<T> {

        private static final UpdateOption<?> EMPTY = new UpdateOption<>();

        private boolean isValuePresent;

        private T value;

        private UpdateOption() {
            this.value = null;
            this.isValuePresent = false;
        }

        private UpdateOption(T value) {
            this.value = value;
            this.isValuePresent = true;
        }

        /**
         * Returns an {@code UpdateOption} with the specified present value.
         */
        public static <T> UpdateOption<T> of(T value) {
            return new UpdateOption<>(value);
        }

        /**
         * Returns an empty {@code UpdateOption} instance.
         *
         * <p>No value is present for this UpdateOption.
         */
        @SuppressWarnings("unchecked")
        public static <T> UpdateOption<T> empty() {
            return (UpdateOption<T>) EMPTY;
        }

        /**
         * If a value is present, invoke the specified consumer with the value,
         * otherwise do nothing.
         *
         * @param consumer block to be executed if a value is present
         * @throws NullPointerException if value is present and {@code consumer} is null
         */
        void ifPresent(Consumer<? super T> consumer) {
            if (isValuePresent) {
                consumer.accept(value);
            }
        }

        /**
         * Returns {@code true} if value is present, {@code false} otherwise.
         */
        boolean isPresent() {
            return isValuePresent;
        }

        @Override
        public String toString() {
            return isValuePresent
                    ? String.format("UpdateOption[%s]", value)
                    : "UpdateOption.empty";
        }

    }
}
