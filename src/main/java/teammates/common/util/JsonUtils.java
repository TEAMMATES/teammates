package teammates.common.util;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import teammates.common.datatransfer.logs.LogDetails;
import teammates.common.datatransfer.logs.LogEvent;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;
import teammates.storage.sqlentity.questions.FeedbackConstantSumQuestion;
import teammates.storage.sqlentity.questions.FeedbackContributionQuestion;
import teammates.storage.sqlentity.questions.FeedbackMcqQuestion;
import teammates.storage.sqlentity.questions.FeedbackMsqQuestion;
import teammates.storage.sqlentity.questions.FeedbackNumericalScaleQuestion;
import teammates.storage.sqlentity.questions.FeedbackRankOptionsQuestion;
import teammates.storage.sqlentity.questions.FeedbackRankRecipientsQuestion;
import teammates.storage.sqlentity.questions.FeedbackRubricQuestion;
import teammates.storage.sqlentity.questions.FeedbackTextQuestion;

import jakarta.persistence.OneToMany;

/**
 * Provides means to handle, manipulate, and convert JSON objects to/from strings.
 */
public final class JsonUtils {

    private JsonUtils() {
        // utility class
    }

    /**
     * This creates a Gson object that can handle the Date format we use in the
     * Json file and also reformat the Json string in pretty-print format.
     */
    private static Gson getGsonInstance(boolean prettyPrint) {
        GsonBuilder builder = new GsonBuilder()
                .setExclusionStrategies(new HibernateExclusionStrategy())
                .registerTypeAdapter(User.class, new UserAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(ZoneId.class, new ZoneIdAdapter())
                .registerTypeAdapter(Duration.class, new DurationMinutesAdapter())
                .registerTypeAdapter(FeedbackQuestion.class, new FeedbackQuestionAdapter())
                .registerTypeAdapter(FeedbackQuestionDetails.class, new FeedbackQuestionDetailsAdapter())
                .registerTypeAdapter(FeedbackResponseDetails.class, new FeedbackResponseDetailsAdapter())
                .registerTypeAdapter(LogDetails.class, new LogDetailsAdapter())
                .disableHtmlEscaping();
        if (prettyPrint) {
            builder.setPrettyPrinting();
        }
        return builder.create();
    }

    /**
     * Serializes and pretty-prints the specified object into its equivalent JSON string.
     *
     * @see Gson#toJson(Object, Type)
     */
    public static String toJson(Object src, Type typeOfSrc) {
        return getGsonInstance(true).toJson(src, typeOfSrc);
    }

    /**
     * Serializes and pretty-prints the specified object into its equivalent JSON string.
     *
     * @see Gson#toJson(Object)
     */
    public static String toJson(Object src) {
        return getGsonInstance(true).toJson(src);
    }

    /**
     * Serializes the specified object into its equivalent JSON string.
     *
     * @see Gson#toJson(Object)
     */
    public static String toCompactJson(Object src) {
        return getGsonInstance(false).toJson(src);
    }

    /**
     * Serializes the specified object into its equivalent JSON string and stream into a writer.
     * This is done to reduce the memory consumption when creating object across call stack.
     *
     * @see Gson#toJson(Object, Appendable)
     */
    public static void toCompactJson(Object src, Appendable writer) {
        getGsonInstance(false).toJson(src, writer);
    }

    /**
     * Deserializes the specified JSON string into an object of the specified type.
     *
     * @see Gson#fromJson(String, Type)
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        return getGsonInstance(false).fromJson(json, typeOfT);
    }

    /**
     * Deserializes the specified JSON string into an object of the specified class.
     *
     * @see Gson#fromJson(String, Class)
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getGsonInstance(false).fromJson(json, classOfT);
    }

    /**
     * Parses the specified JSON string into a {@link JsonElement} object.
     *
     * @see JsonParser#parseString(String)
     */
    public static JsonElement parse(String json) {
        return JsonParser.parseString(json);
    }

    private static class HibernateExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            // Exclude certain fields to avoid circular references when serializing hibernate entities
            return f.getAnnotation(OneToMany.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    private static class UserAdapter implements JsonSerializer<User>, JsonDeserializer<User> {

        @Override
        public JsonElement serialize(User user, Type type, JsonSerializationContext context) {
            if (user instanceof Instructor) {
                JsonObject element = (JsonObject) context.serialize(user, Instructor.class);
                element.addProperty("type", "instructor");
                return element;
            }

            // User is a Student
            JsonObject element = (JsonObject) context.serialize(user, Student.class);
            element.addProperty("type", "student");
            return element;
        }

        @Override
        public User deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
            JsonObject obj = (JsonObject) element;

            if ("instructor".equals(obj.get("type").getAsString())) {
                return context.deserialize(element, Instructor.class);
            }

            // User is student
            return context.deserialize(obj, Student.class);
        }
    }

    private static class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

        @Override
        public JsonElement serialize(Instant instant, Type type, JsonSerializationContext context) {
            synchronized (this) {
                return new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(instant));
            }
        }

        @Override
        public Instant deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
            synchronized (this) {
                return Instant.parse(element.getAsString());
            }
        }
    }

    private static class ZoneIdAdapter implements JsonSerializer<ZoneId>, JsonDeserializer<ZoneId> {

        @Override
        public JsonElement serialize(ZoneId zoneId, Type type, JsonSerializationContext context) {
            synchronized (this) {
                return new JsonPrimitive(zoneId.getId());
            }
        }

        @Override
        public ZoneId deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
            synchronized (this) {
                return ZoneId.of(element.getAsString());
            }
        }
    }

    private static class DurationMinutesAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

        @Override
        public JsonElement serialize(Duration duration, Type type, JsonSerializationContext context) {
            synchronized (this) {
                return new JsonPrimitive(duration.toMinutes());
            }
        }

        @Override
        public Duration deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
            synchronized (this) {
                return Duration.ofMinutes(element.getAsLong());
            }
        }
    }

    private static class FeedbackResponseDetailsAdapter implements JsonSerializer<FeedbackResponseDetails>,
            JsonDeserializer<FeedbackResponseDetails> {

        @Override
        public JsonElement serialize(FeedbackResponseDetails src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, src.getQuestionType().getResponseDetailsClass());
        }

        @Override
        public FeedbackResponseDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            FeedbackQuestionType questionType =
                    FeedbackQuestionType.valueOf(json.getAsJsonObject().get("questionType").getAsString());
            return context.deserialize(json, questionType.getResponseDetailsClass());
        }

    }

    private static class FeedbackQuestionAdapter implements JsonSerializer<FeedbackQuestion>,
            JsonDeserializer<FeedbackQuestion> {

        @Override
        public JsonElement serialize(FeedbackQuestion src, Type typeOfSrc, JsonSerializationContext context) {
            if (src instanceof FeedbackMcqQuestion) {
                return context.serialize(src, FeedbackMcqQuestion.class);
            } else if (src instanceof FeedbackMsqQuestion) {
                return context.serialize(src, FeedbackMsqQuestion.class);
            } else if (src instanceof FeedbackTextQuestion) {
                return context.serialize(src, FeedbackTextQuestion.class);
            } else if (src instanceof FeedbackNumericalScaleQuestion) {
                return context.serialize(src, FeedbackNumericalScaleQuestion.class);
            } else if (src instanceof FeedbackConstantSumQuestion) {
                return context.serialize(src, FeedbackConstantSumQuestion.class);
            } else if (src instanceof FeedbackContributionQuestion) {
                return context.serialize(src, FeedbackContributionQuestion.class);
            } else if (src instanceof FeedbackRubricQuestion) {
                return context.serialize(src, FeedbackRubricQuestion.class);
            } else if (src instanceof FeedbackRankOptionsQuestion) {
                return context.serialize(src, FeedbackRankOptionsQuestion.class);
            } else if (src instanceof FeedbackRankRecipientsQuestion) {
                return context.serialize(src, FeedbackRankRecipientsQuestion.class);
            }
            return null;
        }

        @Override
        public FeedbackQuestion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            FeedbackQuestionType questionType =
                    FeedbackQuestionType.valueOf(json.getAsJsonObject().get("questionType").getAsString());
            switch (questionType) {
            case MCQ:
                return context.deserialize(json, FeedbackMcqQuestion.class);
            case MSQ:
                return context.deserialize(json, FeedbackMsqQuestion.class);
            case TEXT:
                return context.deserialize(json, FeedbackTextQuestion.class);
            case RUBRIC:
                return context.deserialize(json, FeedbackRubricQuestion.class);
            case CONTRIB:
                return context.deserialize(json, FeedbackContributionQuestion.class);
            case CONSTSUM:
            case CONSTSUM_RECIPIENTS:
            case CONSTSUM_OPTIONS:
                return context.deserialize(json, FeedbackConstantSumQuestion.class);
            case NUMSCALE:
                return context.deserialize(json, FeedbackNumericalScaleQuestion.class);
            case RANK_OPTIONS:
                return context.deserialize(json, FeedbackRankOptionsQuestion.class);
            case RANK_RECIPIENTS:
                return context.deserialize(json, FeedbackRankRecipientsQuestion.class);
            default:
                return null;
            }
        }
    }

    private static class FeedbackQuestionDetailsAdapter implements JsonSerializer<FeedbackQuestionDetails>,
            JsonDeserializer<FeedbackQuestionDetails> {

        @Override
        public JsonElement serialize(FeedbackQuestionDetails src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, src.getQuestionType().getQuestionDetailsClass());
        }

        @Override
        public FeedbackQuestionDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            FeedbackQuestionType questionType =
                    FeedbackQuestionType.valueOf(json.getAsJsonObject().get("questionType").getAsString());
            return context.deserialize(json, questionType.getQuestionDetailsClass());
        }
    }

    private static class LogDetailsAdapter implements JsonSerializer<LogDetails>, JsonDeserializer<LogDetails> {

        @Override
        public JsonElement serialize(LogDetails src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, src.getEvent().getDetailsClass());
        }

        @Override
        public LogDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            LogEvent event;
            if (json.getAsJsonObject().has("event")) {
                try {
                    event = LogEvent.valueOf(json.getAsJsonObject().get("event").getAsString());
                } catch (IllegalArgumentException e) {
                    event = LogEvent.DEFAULT_LOG;
                }
            } else {
                event = LogEvent.DEFAULT_LOG;
            }
            return context.deserialize(json, event.getDetailsClass());
        }
    }
}
