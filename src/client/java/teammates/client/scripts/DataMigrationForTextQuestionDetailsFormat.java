package teammates.client.scripts;

import java.io.IOException;

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
        return ofy().load().type(FeedbackQuestion.class)
                .filter("questionType =", FeedbackQuestionType.TEXT);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackQuestion question) {
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
