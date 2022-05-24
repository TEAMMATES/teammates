package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to set recipientType as STUDENTS_EXCLUDING_SELF in all feedback questions whose recipientType is STUDENTS
 * and recipientType as TEAMS_EXCLUDING_SELF in all feedback questions whose recipientType is TEAMS.
 *
 * <p>See issue #2488</p>
 */
public class DataMigrationForStudentsAndTeamsRecipientType extends
        DataMigrationEntitiesBaseScript<FeedbackQuestion> {

    public static void main(String[] args) {
        new DataMigrationForStudentsAndTeamsRecipientType().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackQuestion> getFilterQuery() {
        return ofy().load().type(FeedbackQuestion.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackQuestion question) {
        return question.getRecipientType() == FeedbackParticipantType.STUDENTS
                || question.getRecipientType() == FeedbackParticipantType.TEAMS;
    }

    @Override
    protected void migrateEntity(FeedbackQuestion question) {
        if (question.getRecipientType() == FeedbackParticipantType.STUDENTS) {
            question.setRecipientType(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);
        }
        if (question.getRecipientType() == FeedbackParticipantType.TEAMS) {
            question.setRecipientType(FeedbackParticipantType.TEAMS_EXCLUDING_SELF);
        }

        saveEntityDeferred(question);
    }

}
