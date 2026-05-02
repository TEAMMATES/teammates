package teammates.common.util;

import java.io.Writer;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;

import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;

import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
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

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.ser.std.StdSerializer;

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
        JsonMapper.Builder builder = JsonMapper.builder()
                .changeDefaultVisibility(v -> v
                        .withVisibility(PropertyAccessor.ALL, Visibility.NONE)
                        .withVisibility(PropertyAccessor.FIELD, Visibility.ANY))
                .changeDefaultPropertyInclusion(v -> v
                        .withValueInclusion(JsonInclude.Include.NON_NULL))
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .annotationIntrospector(new HibernateAnnotationIntrospector())
                .addModule(new CustomSerializerAndDeserializer())
                .addModule(new CustomPolymorphicSubtypeModule());

        builder.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        if (prettyPrint) {
            builder.defaultPrettyPrinter(new CustomPrettyPrinter());
            builder.enable(SerializationFeature.INDENT_OUTPUT);
        }

        return builder.build();
    }

    /**
     * Creates an {@code ObjectNode} that can be reformatted to modify JSON output.
     */
    public static ObjectNode toObjectNode(Object src) {
        return PRETTY_MAPPER.valueToTree(src);
    }

    /**
     * Serializes and pretty-prints the specified object into its equivalent JSON string.
     *
     * @see ObjectMapper#writeValueAsString(Object)
     */
    public static String toJson(Object src, Type typeOfSrc) {
        return PRETTY_MAPPER.writerFor(PRETTY_MAPPER.getTypeFactory().constructType(typeOfSrc)).writeValueAsString(src);
    }

    /**
     * Serializes and pretty-prints the specified object into its equivalent JSON string.
     *
     * @see ObjectMapper#writeValueAsString(Object)
     */
    public static String toJson(Object src) {
        return PRETTY_MAPPER.writeValueAsString(src);
    }

    /**
     * Serializes the specified object into its equivalent JSON string.
     *
     * @see ObjectMapper#writeValueAsString(Object)
     */
    public static String toCompactJson(Object src) {
        return MAPPER.writeValueAsString(src);
    }

    /**
     * Serializes the specified object into its equivalent JSON string and stream into a writer.
     * This is done to reduce the memory consumption when creating object across call stack.
     *
     * @see ObjectMapper#writeValue(Writer, Object)
     */
    public static void toCompactJson(Object src, Writer writer) {
        MAPPER.writeValue(writer, src);
    }

    /**
     * Deserializes the specified JSON string into an object of the specified type.
     *
     * @see ObjectMapper#readValue(String, TypeReference)
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructType(typeOfT));
    }

    /**
     * Deserializes the specified JSON string into an object of the specified class.
     *
     * @see ObjectMapper#readValue(String, Class)
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return MAPPER.readValue(json, classOfT);
    }

    /**
     * Deserializes the specified JSON string into an object of the specified {@link TypeReference}.
     *
     * @see ObjectMapper#readValue(String, TypeReference)
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        return MAPPER.readValue(json, typeRef);
    }

    /**
     * Parses the specified JSON string into a {@link JsonNode} object.
     *
     * @see ObjectMapper#readTree(String)
     */
    public static JsonNode parse(String json) {
        return MAPPER.readTree(json);
    }

    /**
     * Aggregates all custom serializers and deserializers.
     */
    private static final class CustomSerializerAndDeserializer extends SimpleModule {
        CustomSerializerAndDeserializer() {
            addSerializer(Duration.class, new DurationMinutesSerializer());
            addDeserializer(Duration.class, new DurationMinutesDeserializer());
            addDeserializer(FeedbackQuestion.class, new FeedbackQuestionDeserializer());
            addDeserializer(FeedbackResponse.class, new FeedbackResponseDeserializer());
        }
    }

    /**
     * Registers {@code @JsonTypeInfo(use = Id.NONE)} as a mix-in for every concrete subtype of each
     * polymorphic root listed in {@code POLYMORPHIC_ROOTS}. This allows deserializing a conrete subtype
     * without requiring the type discriminator field e.g. {@code questionType} when deserializing a
     * {@code FeedbackTextQuestionDetails}.
     */
    private static final class CustomPolymorphicSubtypeModule extends SimpleModule {

        private static final List<Class<?>> POLYMORPHIC_ROOTS = List.of(
                User.class,
                FeedbackQuestionDetails.class,
                FeedbackResponseDetails.class
        );

        CustomPolymorphicSubtypeModule() {
            for (Class<?> root : POLYMORPHIC_ROOTS) {
                for (JsonSubTypes.Type subtype : root.getAnnotation(JsonSubTypes.class).value()) {
                    setMixInAnnotation(subtype.value(), DirectDeserializeMixIn.class);
                }
            }
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        private interface DirectDeserializeMixIn {}
    }

    /**
     * A pretty-printer that modifies Jackson's default.
     * - ": " separator instead of " : "
     * - array elements on separate lines
     * - no space inside empty objects or arrays ({} and [] instead of { } and [ ])
     */
    private static final class CustomPrettyPrinter extends DefaultPrettyPrinter {
        CustomPrettyPrinter() {
            _objectIndenter = new DefaultIndenter("  ", "\n");
            _arrayIndenter = new DefaultIndenter("  ", "\n");
        }

        @Override
        public void writeObjectNameValueSeparator(JsonGenerator g) {
            g.writeRaw(": ");
        }

        @Override
        public void writeEndObject(JsonGenerator g, int nrOfEntries) {
            if (!_objectIndenter.isInline()) {
                --_nesting;
            }
            if (nrOfEntries > 0) {
                _objectIndenter.writeIndentation(g, _nesting);
            }
            g.writeRaw('}');
        }

        @Override
        public void writeEndArray(JsonGenerator g, int nrOfValues) {
            if (!_arrayIndenter.isInline()) {
                --_nesting;
            }
            if (nrOfValues > 0) {
                _arrayIndenter.writeIndentation(g, _nesting);
            }
            g.writeRaw(']');
        }

        @Override
        public CustomPrettyPrinter createInstance() {
            return new CustomPrettyPrinter();
        }
    }

    /**
     * Treats @OneToMany as @JsonIgnore too.
     */
    private static final class HibernateAnnotationIntrospector extends JacksonAnnotationIntrospector {

        @Override
        public boolean hasIgnoreMarker(MapperConfig<?> config, AnnotatedMember m) {
            return m.hasAnnotation(OneToMany.class) || super.hasIgnoreMarker(config, m);
        }
    }

    private static final class DurationMinutesSerializer extends StdSerializer<Duration> {
        DurationMinutesSerializer() {
            super(Duration.class);
        }

        @Override
        public void serialize(Duration value, JsonGenerator gen, SerializationContext ctx) {
            gen.writeNumber(value.toMinutes());
        }
    }

    private static final class DurationMinutesDeserializer extends StdDeserializer<Duration> {
        DurationMinutesDeserializer() {
            super(Duration.class);
        }

        @Override
        public Duration deserialize(JsonParser p, DeserializationContext ctx) {
            return Duration.ofMinutes(p.getLongValue());
        }
    }

    private static final class FeedbackQuestionDeserializer extends StdDeserializer<FeedbackQuestion> {
        FeedbackQuestionDeserializer() {
            super(FeedbackQuestion.class);
        }

        @Override
        public FeedbackQuestion deserialize(JsonParser p, DeserializationContext ctx) {
            ObjectNode node = p.readValueAsTree();
            String qt = node.path("questionDetails").path("questionType").asString();
            try {
                return switch (FeedbackQuestionType.valueOf(qt)) {
                case MCQ -> MAPPER.treeToValue(node, FeedbackMcqQuestion.class);
                case MSQ -> MAPPER.treeToValue(node, FeedbackMsqQuestion.class);
                case TEXT -> MAPPER.treeToValue(node, FeedbackTextQuestion.class);
                case RUBRIC -> MAPPER.treeToValue(node, FeedbackRubricQuestion.class);
                case CONTRIB -> MAPPER.treeToValue(node, FeedbackContributionQuestion.class);
                case CONSTSUM, CONSTSUM_OPTIONS, CONSTSUM_RECIPIENTS ->
                        MAPPER.treeToValue(node, FeedbackConstantSumQuestion.class);
                case NUMSCALE -> MAPPER.treeToValue(node, FeedbackNumericalScaleQuestion.class);
                case RANK_OPTIONS -> MAPPER.treeToValue(node, FeedbackRankOptionsQuestion.class);
                case RANK_RECIPIENTS -> MAPPER.treeToValue(node, FeedbackRankRecipientsQuestion.class);
                };
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private static final class FeedbackResponseDeserializer extends StdDeserializer<FeedbackResponse> {
        FeedbackResponseDeserializer() {
            super(FeedbackResponse.class);
        }

        @Override
        public FeedbackResponse deserialize(JsonParser p, DeserializationContext ctx) {
            ObjectNode node = p.readValueAsTree();
            String qt = node.path("answer").path("questionType").asString();
            try {
                return switch (FeedbackQuestionType.valueOf(qt)) {
                case MCQ -> MAPPER.treeToValue(node, FeedbackMcqResponse.class);
                case MSQ -> MAPPER.treeToValue(node, FeedbackMsqResponse.class);
                case TEXT -> MAPPER.treeToValue(node, FeedbackTextResponse.class);
                case RUBRIC -> MAPPER.treeToValue(node, FeedbackRubricResponse.class);
                case CONTRIB -> MAPPER.treeToValue(node, FeedbackContributionResponse.class);
                case CONSTSUM, CONSTSUM_OPTIONS, CONSTSUM_RECIPIENTS ->
                        MAPPER.treeToValue(node, FeedbackConstantSumResponse.class);
                case NUMSCALE -> MAPPER.treeToValue(node, FeedbackNumericalScaleResponse.class);
                case RANK_OPTIONS -> MAPPER.treeToValue(node, FeedbackRankOptionsResponse.class);
                case RANK_RECIPIENTS -> MAPPER.treeToValue(node, FeedbackRankRecipientsResponse.class);
                };
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
