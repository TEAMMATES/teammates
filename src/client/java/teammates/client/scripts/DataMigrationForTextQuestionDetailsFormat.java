package teammates.client.scripts;

import java.io.IOException;
import java.lang.reflect.Field;

import com.google.gson.JsonParseException;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to migrate old format of TEXT question details (plain text) to JSON.
 *
 * <p>This only affects very old question entities created before there are multiple question types.
 */
public class DataMigrationForTextQuestionDetailsFormat extends
        DataMigrationEntitiesBaseScript<FeedbackQuestion> {

    public static void main(String[] args) throws IOException {
        new DataMigrationForTextQuestionDetailsFormat().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackQuestion> getFilterQuery() {
        // Version 1: question has createdAt field
        // Instant earliestDate = TimeHelper.parseInstant("2015-11-30T16:00:00.00Z");
        // return ofy().load().type(FeedbackQuestion.class)
        //         .filter("createdAt <=", earliestDate)
        //         .order("-createdAt");

        // Version 2: question does not have createdAt field
        return ofy().load().type(FeedbackQuestion.class)
                .filter("questionType =", FeedbackQuestionType.TEXT);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackQuestion question) {
        // Version 1: question has createdAt field
        // if (question.getQuestionType() != FeedbackQuestionType.TEXT) {
        //     return false;
        // }

        // Version 2: question does not have createdAt field
        try {
            Field createdAt = question.getClass().getDeclaredField("createdAt");
            createdAt.setAccessible(true);
            if (createdAt.get(question) != null) {
                return false;
            }
        } catch (ReflectiveOperationException e) {
            // continue
        }

        try {
            // Old non-JSON format will encounter exception when GSON tries to parse it.
            JsonUtils.fromJson(question.getQuestionText(), FeedbackQuestionType.TEXT.getQuestionDetailsClass());
            return false;
        } catch (JsonParseException e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(FeedbackQuestion question) {
        FeedbackTextQuestionDetails ftqd = new FeedbackTextQuestionDetails(question.getQuestionText());

        question.setQuestionText(ftqd.getJsonString());

        saveEntityDeferred(question);
    }

}
