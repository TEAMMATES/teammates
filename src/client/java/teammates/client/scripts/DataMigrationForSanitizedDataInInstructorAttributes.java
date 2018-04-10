package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.InstructorsDb;
import teammates.storage.entity.Instructor;

/**
 * Script to desanitize content of {@link InstructorAttributes} if it is sanitized.
 */

public class DataMigrationForSanitizedDataInInstructorAttributes extends DataMigrationForEntities<InstructorAttributes> {

    private InstructorsDb instructorsDb = new InstructorsDb();

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedDataInInstructorAttributes migrator =
                     new DataMigrationForSanitizedDataInInstructorAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected List<InstructorAttributes> getEntities() {
        return getAllCourseInstructors();

    }

    @Override
    protected boolean isMigrationNeeded(InstructorAttributes instructor) {
        return SanitizationHelper.isSanitizedHtml(instructor.displayedName)
                || SanitizationHelper.isSanitizedHtml(instructor.role);
    }

    @Override
    protected void printPreviewInformation(InstructorAttributes instructor) {
        println("Checking instructor having email: " + instructor.email);

        if (SanitizationHelper.isSanitizedHtml(instructor.displayedName)) {
            println("displayName: " + instructor.displayedName);
            println("new displayName: " + fixSanitization(instructor.displayedName));
        }
        if (SanitizationHelper.isSanitizedHtml(instructor.role)) {
            println("role: " + instructor.role);
            println("new role: " + fixSanitization(instructor.role));
        }
        println("");
    }

    @Override
    protected void migrate(InstructorAttributes instructor) throws InvalidParametersException, EntityDoesNotExistException {
        fixSanitizationForInstructor(instructor);
        updateInstructor(instructor);
    }

    @Override
    protected void postAction() {
        // nothing to do
    }

    private String fixSanitization(String s) {
        return SanitizationHelper.desanitizeIfHtmlSanitized(s);
    }

    private void fixSanitizationForInstructor(InstructorAttributes instructor) {
        instructor.displayedName = fixSanitization(instructor.displayedName);
        instructor.role = fixSanitization(instructor.role);
    }

    private void updateInstructor(InstructorAttributes instructor)
                throws InvalidParametersException, EntityDoesNotExistException {

        instructorsDb.updateInstructorByEmail(instructor);
    }

    private List<InstructorAttributes> getAllCourseInstructors() {
        ArrayList<InstructorAttributes> result = new ArrayList<>();

        for (Instructor instructor : getCourseInstructorEntities()) {
            result.add(InstructorAttributes.valueOf(instructor));

        }
        return result;
    }

    private List<Instructor> getCourseInstructorEntities() {
        return ofy().load().type(Instructor.class).list();
    }
}
