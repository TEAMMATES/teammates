package teammates.common.util;


import com.google.gson.*;

import java.lang.reflect.Type;

public final class SubclassAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("type", src.getClass().getName());
        wrapper.add("data", context.serialize(src));
        return wrapper;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject wrapper = (JsonObject) json;
        final JsonElement typeName = get(wrapper, "type");
        final JsonElement data = get(wrapper, "data");
        final Type actualType = typeForName(typeName);
        return context.deserialize(data, actualType);
    }

    private Type typeForName(final JsonElement typeElem) {
        try {
            return Class.forName(typeElem.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, String memberName) {
        final JsonElement jsonElement = wrapper.get(memberName);
        if (jsonElement == null) {
            throw new JsonParseException("no '" + memberName
                    + "' member found in what was expected to be an interface wrapper");
        }
        return jsonElement;
    }
}
