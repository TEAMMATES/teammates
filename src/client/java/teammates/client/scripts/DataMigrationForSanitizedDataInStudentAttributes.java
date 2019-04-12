package teammates.client.scripts;

import java.io.IOException;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.CourseStudent;

/**
 * Script to desanitize content of {@link CourseStudent} if it is sanitized.
 */
public class DataMigrationForSanitizedDataInStudentAttributes
        extends DataMigrationEntitiesBaseScript<CourseStudent> {

    public DataMigrationForSanitizedDataInStudentAttributes() {
        numberOfScannedKey.set(0L);
        numberOfAffectedEntities.set(0L);
        numberOfUpdatedEntities.set(0L);
    }

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedDataInStudentAttributes migrator =
                new DataMigrationForSanitizedDataInStudentAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected Query<CourseStudent> getFilterQuery() {
        return ofy().load().type(CourseStudent.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(CourseStudent student) throws Exception {
        if (SanitizationHelper.isSanitizedHtml(student.getCourseId())) {
            logError(String.format("Student %s has unsanitized courseId %s, this should not happen",
                    student.getUniqueId(), student.getCourseId()));
        }
        if (SanitizationHelper.isSanitizedHtml(student.getEmail())) {
            logError(String.format("Student %s has unsanitized email %s, this should not happen",
                    student.getUniqueId(), student.getEmail()));
        }
        if (SanitizationHelper.isSanitizedHtml(student.getGoogleId())) {
            logError(String.format("Student %s has unsanitized googleId %s, this should not happen",
                    student.getUniqueId(), student.getGoogleId()));
        }
        if (SanitizationHelper.isSanitizedHtml(student.getSectionName())) {
            logError(String.format("Student %s has unsanitized sectionName %s, this should not happen",
                    student.getUniqueId(), student.getSectionName()));
        }
        if (SanitizationHelper.isSanitizedHtml(student.getTeamName())) {
            logError(String.format("Student %s has unsanitized teamName %s, this should not happen",
                    student.getUniqueId(), student.getTeamName()));
        }

        return SanitizationHelper.isSanitizedHtml(student.getComments())
                || SanitizationHelper.isSanitizedHtml(student.getLastName())
                || SanitizationHelper.isSanitizedHtml(student.getName());
    }

    @Override
    protected void migrateEntity(CourseStudent student) throws Exception {
        student.setComments(SanitizationHelper.desanitizeIfHtmlSanitized(student.getComments()));
        student.setName(SanitizationHelper.desanitizeIfHtmlSanitized(student.getName()));
        student.setLastName(SanitizationHelper.desanitizeIfHtmlSanitized(student.getLastName()));

        saveEntityDeferred(student);
    }
}
