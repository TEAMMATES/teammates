package teammates.common.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import teammates.storage.sqlentity.FeedbackResponse;
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
import teammates.storage.sqlentity.responses.FeedbackConstantSumResponse;
import teammates.storage.sqlentity.responses.FeedbackContributionResponse;
import teammates.storage.sqlentity.responses.FeedbackMcqResponse;
import teammates.storage.sqlentity.responses.FeedbackMsqResponse;
import teammates.storage.sqlentity.responses.FeedbackNumericalScaleResponse;
import teammates.storage.sqlentity.responses.FeedbackRankOptionsResponse;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse;
import teammates.storage.sqlentity.responses.FeedbackRubricResponse;
import teammates.storage.sqlentity.responses.FeedbackTextResponse;

/**
 * Provides means to handle, manipulate, and convert JSON objects to/from strings.
 */
public final class JsonUtils {

    private static final ObjectMapper MAPPER = buildMapper(false);
    private static final ObjectMapper PRETTY_MAPPER = buildMapper(true);

    private JsonUtils() {
        // utility class
    }

    private static ObjectMapper buildMapper(boolean prettyPrint) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Treat @OneToMany fields as @JsonIgnore
        mapper.setAnnotationIntrospector(new HibernateAnnotationIntrospector());
        mapper.registerModule(new JavaTimeModule()); // Format Instant as ISO 8601 string and ZoneId
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationMinutesJacksonSerializer());
        module.addDeserializer(Duration.class, new DurationMinutesJacksonDeserializer());
        module.addDeserializer(FeedbackQuestion.class, new FeedbackQuestionJacksonDeserializer());
        module.addDeserializer(FeedbackResponse.class, new FeedbackResponseJacksonDeserializer());
        module.addDeserializer(FeedbackQuestionDetails.class, new FeedbackQuestionDetailsJacksonDeserializer());
        module.addDeserializer(FeedbackResponseDetails.class, new FeedbackResponseDetailsJacksonDeserializer());
        mapper.registerModule(module);
        if (prettyPrint) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return mapper;
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
                .registerTypeAdapter(FeedbackResponse.class, new FeedbackResponseAdapter())
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
     * This creates a Gson object that can be reformatted to modify JSON output.
     */
    public static JsonObject toJsonObject(Object src) {
        return (JsonObject) getGsonInstance(true).toJsonTree(src);
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

    /**
     * Jackson equivalent of {@link #toJsonObject}. Returns an {@link ObjectNode} instead of Gson {@link JsonObject}.
     */
    public static ObjectNode toObjectNodeJackson(Object src) {
        return PRETTY_MAPPER.valueToTree(src);
    }

    /**
     * Jackson equivalent of {@link #toJson(Object, Type)}.
     */
    public static String toJsonJackson(Object src, Type typeOfSrc) {
        try {
            return PRETTY_MAPPER.writerFor(PRETTY_MAPPER.getTypeFactory().constructType(typeOfSrc))
                    .writeValueAsString(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Jackson equivalent of {@link #toJson(Object)}.
     */
    public static String toJsonJackson(Object src) {
        try {
            return PRETTY_MAPPER.writeValueAsString(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Jackson equivalent of {@link #toCompactJson(Object)}.
     */
    public static String toCompactJsonJackson(Object src) {
        try {
            return MAPPER.writeValueAsString(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Jackson equivalent of {@link #toCompactJson(Object, Appendable)}.
     * Note: writer must be a {@link java.io.Writer} at runtime (e.g. PrintWriter).
     */
    public static void toCompactJsonJackson(Object src, Appendable writer) {
        try {
            MAPPER.writeValue((Writer) writer, src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Jackson equivalent of {@link #fromJson(String, Type)}.
     */
    public static <T> T fromJsonJackson(String json, Type typeOfT) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructType(typeOfT));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Jackson equivalent of {@link #fromJson(String, Class)}.
     */
    public static <T> T fromJsonJackson(String json, Class<T> classOfT) {
        try {
            return MAPPER.readValue(json, classOfT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Jackson overload for callers using {@link TypeReference} instead of Gson TypeToken.
     */
    public static <T> T fromJsonJackson(String json, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Jackson equivalent of {@link #parse}. Returns {@link JsonNode} instead of Gson {@link JsonElement}.
     */
    public static JsonNode parseJackson(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final class HibernateExclusionStrategy implements ExclusionStrategy {

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

    private static final class UserAdapter implements JsonSerializer<User>, JsonDeserializer<User> {

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

    private static final class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

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

    private static final class ZoneIdAdapter implements JsonSerializer<ZoneId>, JsonDeserializer<ZoneId> {

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

    private static final class DurationMinutesAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

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

    private static final class FeedbackResponseAdapter implements JsonSerializer<FeedbackResponse>,
            JsonDeserializer<FeedbackResponse> {

        @Override
        public JsonElement serialize(FeedbackResponse src, Type typeOfSrc, JsonSerializationContext context) {
            if (src instanceof FeedbackConstantSumResponse) {
                return context.serialize(src, FeedbackConstantSumResponse.class);
            } else if (src instanceof FeedbackContributionResponse) {
                return context.serialize(src, FeedbackContributionResponse.class);
            } else if (src instanceof FeedbackMcqResponse) {
                return context.serialize(src, FeedbackMcqResponse.class);
            } else if (src instanceof FeedbackMsqResponse) {
                return context.serialize(src, FeedbackMsqResponse.class);
            } else if (src instanceof FeedbackNumericalScaleResponse) {
                return context.serialize(src, FeedbackNumericalScaleResponse.class);
            } else if (src instanceof FeedbackRankOptionsResponse) {
                return context.serialize(src, FeedbackRankOptionsResponse.class);
            } else if (src instanceof FeedbackRankRecipientsResponse) {
                return context.serialize(src, FeedbackRankRecipientsResponse.class);
            } else if (src instanceof FeedbackRubricResponse) {
                return context.serialize(src, FeedbackRubricResponse.class);
            } else if (src instanceof FeedbackTextResponse) {
                return context.serialize(src, FeedbackTextResponse.class);
            }
            return null;
        }

        @Override
        public FeedbackResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            FeedbackQuestionType questionType =
                    FeedbackQuestionType.valueOf(json.getAsJsonObject().get("answer")
                        .getAsJsonObject().get("questionType").getAsString());
            switch (questionType) {
            case MCQ:
                return context.deserialize(json, FeedbackMcqResponse.class);
            case MSQ:
                return context.deserialize(json, FeedbackMsqResponse.class);
            case TEXT:
                return context.deserialize(json, FeedbackTextResponse.class);
            case RUBRIC:
                return context.deserialize(json, FeedbackRubricResponse.class);
            case CONTRIB:
                return context.deserialize(json, FeedbackContributionResponse.class);
            case CONSTSUM:
            case CONSTSUM_RECIPIENTS:
            case CONSTSUM_OPTIONS:
                return context.deserialize(json, FeedbackConstantSumResponse.class);
            case NUMSCALE:
                return context.deserialize(json, FeedbackNumericalScaleResponse.class);
            case RANK_OPTIONS:
                return context.deserialize(json, FeedbackRankOptionsResponse.class);
            case RANK_RECIPIENTS:
                return context.deserialize(json, FeedbackRankRecipientsResponse.class);
            default:
                return null;
            }
        }
    }

    private static final class FeedbackResponseDetailsAdapter implements JsonSerializer<FeedbackResponseDetails>,
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

    private static final class FeedbackQuestionAdapter implements JsonSerializer<FeedbackQuestion>,
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
                    FeedbackQuestionType.valueOf(json.getAsJsonObject().get("questionDetails")
                            .getAsJsonObject().get("questionType").getAsString());
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

    private static final class FeedbackQuestionDetailsAdapter implements JsonSerializer<FeedbackQuestionDetails>,
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

    private static final class LogDetailsAdapter implements JsonSerializer<LogDetails>, JsonDeserializer<LogDetails> {

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

    private static final class HibernateAnnotationIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public boolean hasIgnoreMarker(AnnotatedMember m) {
            return m.hasAnnotation(OneToMany.class) || super.hasIgnoreMarker(m);
        }
    }

    private static final class DurationMinutesJacksonSerializer extends StdSerializer<Duration> {
        DurationMinutesJacksonSerializer() {
            super(Duration.class);
        }

        @Override
        public void serialize(Duration value, JsonGenerator gen, SerializerProvider p) throws IOException {
            gen.writeNumber(value.toMinutes());
        }
    }

    private static final class DurationMinutesJacksonDeserializer extends StdDeserializer<Duration> {
        DurationMinutesJacksonDeserializer() {
            super(Duration.class);
        }

        @Override
        public Duration deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctx)
                throws IOException {
            return Duration.ofMinutes(p.getLongValue());
        }
    }

    private static final class FeedbackQuestionJacksonDeserializer extends StdDeserializer<FeedbackQuestion> {
        FeedbackQuestionJacksonDeserializer() {
            super(FeedbackQuestion.class);
        }

        @Override
        public FeedbackQuestion deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctx)
                throws IOException {
            ObjectNode node = p.readValueAsTree();
            String qt = node.path("questionDetails").path("questionType").asText();
            try {
                switch (FeedbackQuestionType.valueOf(qt)) {
                case MCQ:
                    return MAPPER.treeToValue(node, FeedbackMcqQuestion.class);
                case MSQ:
                    return MAPPER.treeToValue(node, FeedbackMsqQuestion.class);
                case TEXT:
                    return MAPPER.treeToValue(node, FeedbackTextQuestion.class);
                case RUBRIC:
                    return MAPPER.treeToValue(node, FeedbackRubricQuestion.class);
                case CONTRIB:
                    return MAPPER.treeToValue(node, FeedbackContributionQuestion.class);
                case CONSTSUM:
                case CONSTSUM_OPTIONS:
                case CONSTSUM_RECIPIENTS:
                    return MAPPER.treeToValue(node, FeedbackConstantSumQuestion.class);
                case NUMSCALE:
                    return MAPPER.treeToValue(node, FeedbackNumericalScaleQuestion.class);
                case RANK_OPTIONS:
                    return MAPPER.treeToValue(node, FeedbackRankOptionsQuestion.class);
                case RANK_RECIPIENTS:
                    return MAPPER.treeToValue(node, FeedbackRankRecipientsQuestion.class);
                default:
                    return null;
                }
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private static final class FeedbackQuestionDetailsJacksonDeserializer
            extends StdDeserializer<FeedbackQuestionDetails> {
        FeedbackQuestionDetailsJacksonDeserializer() {
            super(FeedbackQuestionDetails.class);
        }

        @Override
        public FeedbackQuestionDetails deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctx)
                throws IOException {
            ObjectNode node = p.readValueAsTree();
            String qt = node.path("questionType").asText();
            try {
                FeedbackQuestionType type = FeedbackQuestionType.valueOf(qt);
                return MAPPER.treeToValue(node, type.getQuestionDetailsClass());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private static final class FeedbackResponseDetailsJacksonDeserializer
            extends StdDeserializer<FeedbackResponseDetails> {
        FeedbackResponseDetailsJacksonDeserializer() {
            super(FeedbackResponseDetails.class);
        }

        @Override
        public FeedbackResponseDetails deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctx)
                throws IOException {
            ObjectNode node = p.readValueAsTree();
            String qt = node.path("questionType").asText();
            try {
                FeedbackQuestionType type = FeedbackQuestionType.valueOf(qt);
                return MAPPER.treeToValue(node, type.getResponseDetailsClass());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private static final class FeedbackResponseJacksonDeserializer extends StdDeserializer<FeedbackResponse> {
        FeedbackResponseJacksonDeserializer() {
            super(FeedbackResponse.class);
        }

        @Override
        public FeedbackResponse deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctx)
                throws IOException {
            ObjectNode node = p.readValueAsTree();
            String qt = node.path("answer").path("questionType").asText();
            try {
                switch (FeedbackQuestionType.valueOf(qt)) {
                case MCQ:
                    return MAPPER.treeToValue(node, FeedbackMcqResponse.class);
                case MSQ:
                    return MAPPER.treeToValue(node, FeedbackMsqResponse.class);
                case TEXT:
                    return MAPPER.treeToValue(node, FeedbackTextResponse.class);
                case RUBRIC:
                    return MAPPER.treeToValue(node, FeedbackRubricResponse.class);
                case CONTRIB:
                    return MAPPER.treeToValue(node, FeedbackContributionResponse.class);
                case CONSTSUM:
                case CONSTSUM_OPTIONS:
                case CONSTSUM_RECIPIENTS:
                    return MAPPER.treeToValue(node, FeedbackConstantSumResponse.class);
                case NUMSCALE:
                    return MAPPER.treeToValue(node, FeedbackNumericalScaleResponse.class);
                case RANK_OPTIONS:
                    return MAPPER.treeToValue(node, FeedbackRankOptionsResponse.class);
                case RANK_RECIPIENTS:
                    return MAPPER.treeToValue(node, FeedbackRankRecipientsResponse.class);
                default:
                    return null;
                }
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
