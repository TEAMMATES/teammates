package teammates.client.scripts;

import java.time.Instant;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to set isZeroSum as false in all old feedback contribution questions before the addition of the isZeroSum
 * field in feedback contribution questions.
 *
 * <p>See issue #11772</p>
 */
public class DataMigrationForContributionQuestionDetailsIsZeroSum extends
        DataMigrationEntitiesBaseScript<FeedbackQuestion> {

    public static void main(String[] args) {
        new DataMigrationForContributionQuestionDetailsIsZeroSum().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackQuestion> getFilterQuery() {
        return ofy().load().type(FeedbackQuestion.class)
                .filter("questionType =", FeedbackQuestionType.CONTRIB);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackQuestion question) {
        // This is the timestamp of V8.18.0 release, where the addition of the isZeroSum field in feedback contribution
        // question is deployed
        return question.getUpdatedAt().isBefore(Instant.parse("2022-07-11T07:47:00.00Z"));
    }

    @Override
    protected void migrateEntity(FeedbackQuestion question) {
        FeedbackQuestionAttributes fqa = FeedbackQuestionAttributes.valueOf(question);
        FeedbackContributionQuestionDetails fcqd = (FeedbackContributionQuestionDetails) fqa.getQuestionDetails();
        fcqd.setZeroSum(false);
        question.setQuestionText(fcqd.getJsonString());

        saveEntityDeferred(question);
    }

}
