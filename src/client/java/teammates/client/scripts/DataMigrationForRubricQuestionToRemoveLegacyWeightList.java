package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import com.googlecode.objectify.Key;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to remove legacy 1D weight list from Rubric question metadata.
 */
public class DataMigrationForRubricQuestionToRemoveLegacyWeightList extends DataMigrationBaseScript<Key<FeedbackQuestion>> {

    public static void main(String[] args) throws IOException {
        new DataMigrationForRubricQuestionToRemoveLegacyWeightList().doOperationRemotely();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPreview() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Key<FeedbackQuestion>> getEntities() {
        return ofy().load().type(FeedbackQuestion.class).filter("questionType", FeedbackQuestionType.RUBRIC).keys().list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMigrationNeeded(Key<FeedbackQuestion> questionKey) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void printPreviewInformation(Key<FeedbackQuestion> questionKey) {
        // nothing to do.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void migrate(Key<FeedbackQuestion> questionKey) {
        ofy().transact(() -> {
            FeedbackQuestion question = ofy().load().key(questionKey).now();
            removeWeightList(question);
            ofy().save().entity(question).now();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postAction() {
        // nothing to do
    }

    protected void removeWeightList(FeedbackQuestion question) {
        FeedbackQuestionAttributes attr = FeedbackQuestionAttributes.valueOf(question);
        // De-serialize the questionMetaData
        FeedbackRubricQuestionDetails fqd = (FeedbackRubricQuestionDetails) attr.getQuestionDetails();
        // Serialize the questionMetaData
        attr.setQuestionDetails(fqd);
        question.setQuestionText(attr.questionMetaData);
        question.keepUpdateTimestamp = true;
    }
}
