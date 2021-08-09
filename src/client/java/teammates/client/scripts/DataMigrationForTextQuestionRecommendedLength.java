package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to set recommendedLength as null in all text questions whose recommendedLength is 0.
 *
 * <p>See issue #10677</p>
 */
public class DataMigrationForTextQuestionRecommendedLength extends
        DataMigrationEntitiesBaseScript<FeedbackQuestion> {

    public static void main(String[] args) {
        new DataMigrationForTextQuestionRecommendedLength().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackQuestion> getFilterQuery() {
        return ofy().load().type(FeedbackQuestion.class)
                .filter("questionType =", FeedbackQuestionType.TEXT.name());
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackQuestion question) {
        FeedbackQuestionAttributes fqa = FeedbackQuestionAttributes.valueOf(question);
        FeedbackTextQuestionDetails ftqd = (FeedbackTextQuestionDetails) fqa.getQuestionDetails();
        return ftqd.getRecommendedLength() != null && ftqd.getRecommendedLength() == 0;
    }

    @Override
    protected void migrateEntity(FeedbackQuestion question) {
        FeedbackQuestionAttributes fqa = FeedbackQuestionAttributes.valueOf(question);
        FeedbackTextQuestionDetails ftqd = (FeedbackTextQuestionDetails) fqa.getQuestionDetails();
        ftqd.setRecommendedLength(null);

        question.setQuestionText(ftqd.getJsonString());

        saveEntityDeferred(question);
    }

}
