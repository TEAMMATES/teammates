package teammates.common.util;

import java.lang.reflect.Type;
import java.text.DateFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
        return new GsonBuilder().setDateFormat(DateFormat.FULL)
                                .setDateFormat(Const.SystemParams.DEFAULT_DATE_TIME_FORMAT)
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

}
