package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to set recipientType as STUDENTS_EXCLUDING_SELF in all feedback questions whose recipientType is STUDENTS.
 *
 * <p>See issue #2488</p>
 */
public class DataMigrationForStudentsRecipientType extends
        DataMigrationEntitiesBaseScript<FeedbackQuestion> {

    public static void main(String[] args) {
        new DataMigrationForStudentsRecipientType().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackQuestion> getFilterQuery() {
        return ofy().load().type(FeedbackQuestion.class)
                .filter("recipientType =", null);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackQuestion question) {
        return question.getRecipientType() == FeedbackParticipantType.STUDENTS;
    }

    @Override
    protected void migrateEntity(FeedbackQuestion question) {
        question.setRecipientType(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);

        saveEntityDeferred(question);
    }

}
