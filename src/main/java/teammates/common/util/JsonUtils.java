package teammates.common.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.Duration;

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

import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
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
     * This creates an {@code ObjectNode} that can be reformatted to modify JSON output.
     */
    public static ObjectNode toObjectNode(Object src) {
        return PRETTY_MAPPER.valueToTree(src);
    }

    /**
     * Serializes and pretty-prints the specified object into its equivalent JSON string.
     */
    public static String toJson(Object src, Type typeOfSrc) {
        try {
            return PRETTY_MAPPER.writerFor(PRETTY_MAPPER.getTypeFactory().constructType(typeOfSrc))
                    .writeValueAsString(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Serializes and pretty-prints the specified object into its equivalent JSON string.
     */
    public static String toJson(Object src) {
        try {
            return PRETTY_MAPPER.writeValueAsString(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Serializes the specified object into its equivalent JSON string.
     */
    public static String toCompactJson(Object src) {
        try {
            return MAPPER.writeValueAsString(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Serializes the specified object into its equivalent JSON string and stream into a writer.
     * This is done to reduce the memory consumption when creating object across call stack.
     * Note: writer must be a {@link java.io.Writer} at runtime (e.g. PrintWriter).
     */
    public static void toCompactJson(Object src, Appendable writer) {
        try {
            MAPPER.writeValue((Writer) writer, src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Deserializes the specified JSON string into an object of the specified type.
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructType(typeOfT));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Deserializes the specified JSON string into an object of the specified class.
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return MAPPER.readValue(json, classOfT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Jackson overload for callers using {@link TypeReference} instead of Gson TypeToken.
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Parses the specified JSON string into a {@link JsonNode} object.
     */
    public static JsonNode parseJackson(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
