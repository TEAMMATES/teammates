package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumDistributePointsType;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to set distributePointsFor to DISTRIBUTE_ALL_UNEVENLY if forceUnevenDistribution is true.
 *
 * <p>See issue #8577.
 */
public class DataMigrationForConstSumForceUnevenDistribution extends
        DataMigrationEntitiesBaseScript<FeedbackQuestion> {

    public static void main(String[] args) {
        new DataMigrationForConstSumForceUnevenDistribution().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackQuestion> getFilterQuery() {
        return ofy().load().type(FeedbackQuestion.class)
                .filter("questionType =", FeedbackQuestionType.CONSTSUM.name());
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackQuestion question) {
        FeedbackQuestionAttributes fqa = FeedbackQuestionAttributes.valueOf(question);
        FeedbackConstantSumQuestionDetails fcsqd = (FeedbackConstantSumQuestionDetails) fqa.getQuestionDetails();

        return fcsqd.isForceUnevenDistribution()
                && FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption().equals(fcsqd.getDistributePointsFor());
    }

    @Override
    protected void migrateEntity(FeedbackQuestion question) {
        FeedbackQuestionAttributes fqa = FeedbackQuestionAttributes.valueOf(question);
        FeedbackConstantSumQuestionDetails fcsqd = (FeedbackConstantSumQuestionDetails) fqa.getQuestionDetails();

        fcsqd.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        question.setQuestionText(fcsqd.getJsonString());

        saveEntityDeferred(question);
    }

}
