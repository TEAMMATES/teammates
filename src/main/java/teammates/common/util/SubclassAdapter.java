package teammates.common.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;

public final class SubclassAdapter implements JsonDeserializer<FeedbackResponseDetails> {

    @Override
    public FeedbackResponseDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        FeedbackQuestionType questionType =
                FeedbackQuestionType.valueOf(json.getAsJsonObject().get("questionType").getAsString());
        return context.deserialize(json, questionType.getResponseDetailsClass());
    }
}

