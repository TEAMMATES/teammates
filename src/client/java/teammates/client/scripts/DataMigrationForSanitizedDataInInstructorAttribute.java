package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.InstructorsDb;

/**
 * Script to desanitize content of {@link InstructorAttribute} if it is sanitized.
 */

public class DataMigrationForSanitizedDataInInstructorAttribute extends DataMigrationBaseScript<InstructorAttributes> {

    private InstructorsDb instructorDb = new InstructorsDb();

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedDataInInstructorAttribute migrator =
                     new DataMigrationForSanitizedDataInInstructorAttribute();
        migrator.doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected List<InstructorAttributes> getEntities() {
        return instructorDb.getAllInstructor();

    }

    @Override
    protected boolean isMigrationNeeded(InstructorAttributes instructor) {
        return isSanitizedString(instructor.displayedName) || isSanitizedString(instructor.role);
    }

    @Override
    protected void printPreviewInformation(InstructorAttributes instructor) {
        println("Checking student having email: " + instructor.email);

        if (isSanitizedString(instructor.displayedName)) {
            println("displayName: " + instructor.displayedName);
            println("new displayName: " + fixSanitization(instructor.displayedName));
        }
        if (isSanitizedString(instructor.role)) {
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

    private boolean isSanitizedString(String s) {
        if (s == null) {
            return false;
        }
        if (s.indexOf('<') >= 0 || s.indexOf('>') >= 0 || s.indexOf('\"') >= 0
                || s.indexOf('/') >= 0 || s.indexOf('\'') >= 0) {
            return false;
        } else if (s.indexOf("&lt;") >= 0 || s.indexOf("&gt;") >= 0 || s.indexOf("&quot;") >= 0
                   || s.indexOf("&#x2f;") >= 0 || s.indexOf("&#39;") >= 0 || s.indexOf("&amp;") >= 0) {
            return true;
        }
        return false;
    }

    private String fixSanitization(String s) {
        if (isSanitizedString(s)) {
            return SanitizationHelper.desanitizeFromHtml(s);
        }
        return s;
    }

    private void fixSanitizationForInstructor(InstructorAttributes instructor) {
        instructor.displayedName = fixSanitization(instructor.displayedName);
        instructor.role = fixSanitization(instructor.role);
    }

    private void updateInstructor(InstructorAttributes instructor)
                throws InvalidParametersException, EntityDoesNotExistException {

        if (!instructor.isValid()) {
            throw new InvalidParametersException(instructor.getInvalidityInfo());
        }

        instructorDb.updateInstructorByEmail(instructor);
    }
}
