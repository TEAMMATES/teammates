package teammates.storage.sqlentity;

import java.time.Duration;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Base class for all entities.
 */
public abstract class BaseEntity {

    BaseEntity() {
        // instantiate as child classes
    }

    /**
     * Perform any sanitization that needs to be done before saving.
     */
    public abstract void sanitizeForSaving();

    /**
     * Returns a {@code List} of strings, one string for each attribute whose
     * value is invalid, or an empty {@code List} if all attributes are valid.
     *
     * <p>The string explains why the value is invalid
     * and what should values are acceptable. These explanations are
     * good enough to show to the user.
     */
    public abstract List<String> getInvalidityInfo();

    /**
     * Returns true if the attributes represent a valid state for the entity.
     */
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    /**
     * Adds {@code error} to {@code errors} if {@code error} is a non-empty string.
     *
     * @param error  An error message, possibly empty.
     * @param errors A List of errors, to add {@code error} to.
     */
    void addNonEmptyError(String error, List<String> errors) {
        if (error.isEmpty()) {
            return;
        }

        errors.add(error);
    }

    /**
     * Attribute converter between Duration and Long types.
     */
    @Converter
    public static class DurationLongConverter implements AttributeConverter<Duration, Long> {
        @Override
        public Long convertToDatabaseColumn(Duration duration) {
            return duration.toMinutes();
        }

        @Override
        public Duration convertToEntityAttribute(Long minutes) {
            return Duration.ofMinutes(minutes);
        }
    }
}
