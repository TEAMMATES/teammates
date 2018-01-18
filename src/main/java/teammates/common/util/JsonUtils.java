package teammates.common.util;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

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
    private static Gson getTeammatesGson() {
        return new GsonBuilder().registerTypeAdapter(Date.class, new TeammatesDateAdapter())
                                .setPrettyPrinting()
                                .disableHtmlEscaping()
                                .create();
    }

    /**
     * Serializes the specified object into its equivalent JSON string.
     *
     * @see Gson#toJson(Object, Type)
     */
    public static String toJson(Object src, Type typeOfSrc) {
        return getTeammatesGson().toJson(src, typeOfSrc);
    }

    /**
     * Serializes the specified object into its equivalent JSON string.
     *
     * @see Gson#toJson(Object)
     */
    public static String toJson(Object src) {
        return getTeammatesGson().toJson(src);
    }

    /**
     * Deserializes the specified JSON string into an object of the specified type.
     *
     * @see Gson#fromJson(String, Type)
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return getTeammatesGson().fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            // some of the existing data does not use the prescribed date format
            return new Gson().fromJson(json, typeOfT);
        }
    }

    /**
     * Parses the specified JSON string into a {@link JsonElement} object.
     *
     * @see JsonParser#parse(String)
     */
    public static JsonElement parse(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json);
    }

    /**
     * Ensures that JSON date output is in the standard time zone.
     * This workaround is necessary as the default GSON date serializer always uses the local time zone,
     * leading to unpredictable JSON output that depends on the system time zone.
     */
    private static class TeammatesDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        private final DateFormat dateFormat;

        TeammatesDateAdapter() {
            dateFormat = new SimpleDateFormat(Const.SystemParams.DEFAULT_DATE_TIME_FORMAT);
            dateFormat.setTimeZone(Const.SystemParams.TIME_ZONE);
        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(dateFormat.format(date));
        }

        @Override
        public synchronized Date deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
            try {
                return dateFormat.parse(element.getAsString());
            } catch (ParseException e) {
                throw new JsonSyntaxException(element.getAsString(), e);
            }
        }
    }

}
