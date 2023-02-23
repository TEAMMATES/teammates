package teammates.storage.sqlentity;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.google.common.reflect.TypeToken;

import teammates.common.util.JsonUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Converter;
import jakarta.persistence.MappedSuperclass;

/**
 * Base class for all entities.
 */
@MappedSuperclass
public abstract class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    BaseEntity() {
        // instantiate as child classes
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

    /**
     * Generic attribute converter for classes stored in JSON.
     * @param <T> The type of entity to be converted to and from JSON.
     */
    @Converter
    public abstract class JsonConverter<T> implements AttributeConverter<T, String> {
        @Override
        public String convertToDatabaseColumn(T questionDetails) {
            return JsonUtils.toJson(questionDetails);
        }

        @Override
        public T convertToEntityAttribute(String dbData) {
            return JsonUtils.fromJson(dbData, new TypeToken<T>(){}.getType());
        }
    }
}
