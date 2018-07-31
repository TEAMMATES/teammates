package teammates.client.scripts;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import com.googlecode.objectify.Key;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to load Rubric questions, and if the question has weights attached, converts the one-dimensional
 * rubric weights list to a two-dimensional weight list.
 */
public class DataMigrationForRubricQuestionsWithWeightsAttached extends DataMigrationBaseScript<Key<FeedbackQuestion>> {

    public static void main(String[] args) throws IOException {
        new DataMigrationForRubricQuestionsWithWeightsAttached().doOperationRemotely();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPreview() {
        return true;
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
        FeedbackQuestion question = ofy().load().key(questionKey).now();
        FeedbackQuestionAttributes attr = FeedbackQuestionAttributes.valueOf(question);
        FeedbackRubricQuestionDetails fqd = (FeedbackRubricQuestionDetails) attr.getQuestionDetails();

        try {
            Field hasAssignedWeights = fqd.getClass().getDeclaredField("hasAssignedWeights");
            hasAssignedWeights.setAccessible(true);
            return hasAssignedWeights.getBoolean(fqd);
        } catch (ReflectiveOperationException e) {
            System.out.println(e.getMessage());
            return true;
        }
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
    protected void migrate(Key<FeedbackQuestion> sessionKey) {
        // nothing to do.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postAction() {
        // nothing to do
    }

}