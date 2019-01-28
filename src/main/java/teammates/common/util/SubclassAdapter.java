package teammates.common.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;

public final class SubclassAdapter implements JsonSerializer<FeedbackResponseAttributes>,
        JsonDeserializer<FeedbackResponseAttributes> {

    @Override
    public JsonElement serialize(FeedbackResponseAttributes src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }

    @Override
    public FeedbackResponseAttributes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        FeedbackResponseAttributes feedbackResponseAttributes = context.deserialize(json, typeOfT);
        String detailsInJson = JsonUtils.toJson(feedbackResponseAttributes.responseDetails);
        FeedbackResponseDetails actualDetail = JsonUtils.fromJson(detailsInJson,
                feedbackResponseAttributes.feedbackQuestionType.getResponseDetailsClass());
        feedbackResponseAttributes.responseDetails = actualDetail;
        return feedbackResponseAttributes;
    }
}
