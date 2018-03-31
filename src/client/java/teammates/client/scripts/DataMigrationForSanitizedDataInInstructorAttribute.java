package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.InstructorsDb;
import teammates.storage.entity.Instructor;

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
        return getAllInstructor();

    }

    @Override
    protected boolean isMigrationNeeded(InstructorAttributes instructor) {
        return isSanitizedString(instructor.displayedName) || isSanitizedString(instructor.role);
    }

    @Override
    protected void printPreviewInformation(InstructorAttributes instructor) {
        println("Checking instructor having email: " + instructor.email);

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
        SanitizationHelper.desanitizeIfHtmlSanitized(s);
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

    /**
     * TODO remove after data Migration.
     */
    public List<InstructorAttributes> getAllInstructor() {
        Map<String, InstructorAttributes> result = new LinkedHashMap<>();

        for (InstructorAttributes instructor : getAllCourseInstructor()) {
            result.put(instructor.getIdentificationString(), instructor);
        }
        return new ArrayList<>(result.values());
    }

    @Deprecated
    public List<InstructorAttributes> getAllCourseInstructor() {
        return instructorDb.makeAttributes(getCourseInstructorEntities());
    }

    public List<Instructor> getCourseInstructorEntities() {
        return ofy().load().type(Instructor.class).list();
    }
}
