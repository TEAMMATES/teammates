package teammates.storage.sqlentity;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Converter;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;

import com.google.common.reflect.TypeToken;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.JsonUtils;

/**
 * Base class for all entities.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
@MappedSuperclass
public abstract class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    BaseEntity() {
        // instantiate as child classes
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
     * Converter for {@code FeedbackQuestionDetails} stored in JSON.
     *
     */
    @Converter
    public static class FeedbackQuestionDetailsConverter implements AttributeConverter<FeedbackQuestionDetails, String> {
        @Override
        public String convertToDatabaseColumn(FeedbackQuestionDetails entity) {
            return JsonUtils.toJson(entity);
        }

        @Override
        public FeedbackQuestionDetails convertToEntityAttribute(String dbData) {
            return JsonUtils.fromJson(dbData, new TypeToken<FeedbackQuestionDetails>() {
            }.getType());
        }
    }

    /**
     * Converter for {@code FeedbackResponseDetails} stored in JSON.
     *
     */
    @Converter
    public static class FeedbackResponseDetailsConverter implements AttributeConverter<FeedbackResponseDetails, String> {
        @Override
        public String convertToDatabaseColumn(FeedbackResponseDetails entity) {
            return JsonUtils.toJson(entity);
        }

        @Override
        public FeedbackResponseDetails convertToEntityAttribute(String dbData) {
            return JsonUtils.fromJson(dbData, new TypeToken<FeedbackResponseDetails>() {
            }.getType());
        }
    }

    /**
     * Converter for {@code FeedbackQuestionType} stored in JSON.
     *
     */
    @Converter
    public static class FeedbackQuestionTypeConverter implements AttributeConverter<FeedbackQuestionType, String> {
        @Override
        public String convertToDatabaseColumn(FeedbackQuestionType entity) {
            return JsonUtils.toJson(entity);
        }

        @Override
        public FeedbackQuestionType convertToEntityAttribute(String dbData) {
            return JsonUtils.fromJson(dbData, new TypeToken<FeedbackQuestionType>() {
            }.getType());
        }
    }

    /**
     * Attribute converter between FeedbackParticipantType and JSON.
     */
    @Converter
    public static class FeedbackParticipantTypeConverter implements AttributeConverter<FeedbackParticipantType, String> {

        @Override
        public String convertToDatabaseColumn(FeedbackParticipantType attribute) {
            return JsonUtils.toJson(attribute);
        }

        @Override
        public FeedbackParticipantType convertToEntityAttribute(String dbData) {
            return JsonUtils.fromJson(dbData, new TypeToken<FeedbackParticipantType>() {
            }.getType());
        }
    }

    /**
     * Attribute converter between a list of FeedbackParticipantTypes and JSON.
     */
    @Converter
    public static class FeedbackParticipantTypeListConverter
            implements AttributeConverter<List<FeedbackParticipantType>, String> {

        @Override
        public String convertToDatabaseColumn(List<FeedbackParticipantType> attribute) {
            return JsonUtils.toJson(attribute);
        }

        @Override
        public List<FeedbackParticipantType> convertToEntityAttribute(String dbData) {
            return JsonUtils.fromJson(dbData, new TypeToken<List<FeedbackParticipantType>>() {
            }.getType());
        }
    }

    /**
     * Converter for InstructorPrivileges.
     */
    @Converter
    public static class InstructorPrivilegesConverter implements AttributeConverter<InstructorPrivileges, String> {
        @Override
        public String convertToDatabaseColumn(InstructorPrivileges entity) {
            return JsonUtils.toJson(entity);
        }

        @Override
        public InstructorPrivileges convertToEntityAttribute(String dbData) {
            return JsonUtils.fromJson(dbData, InstructorPrivileges.class);
        }
    }
}
