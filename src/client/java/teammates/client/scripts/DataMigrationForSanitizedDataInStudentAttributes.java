package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.StudentsDb;

/**
 * Script to desanitize content of {@link StudentAttributes} if it is sanitized.
 */
public class DataMigrationForSanitizedDataInStudentAttributes extends DataMigrationForEntities<StudentAttributes> {

    private StudentsDb studentsDb = new StudentsDb();

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedDataInStudentAttributes migrator = new DataMigrationForSanitizedDataInStudentAttributes();
        migrator.doOperationRemotely();
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
    @SuppressWarnings("deprecation")
    protected List<StudentAttributes> getEntities() {
        return studentsDb.getAllStudents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMigrationNeeded(StudentAttributes student) {
        return isSanitizedString(student.comments) || isSanitizedString(student.course)
                || isSanitizedString(student.email) || isSanitizedString(student.googleId)
                || isSanitizedString(student.lastName) || isSanitizedString(student.name)
                || isSanitizedString(student.section) || isSanitizedString(student.team);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void printPreviewInformation(StudentAttributes student) {
        println("Checking student having email: " + student.email);

        if (isSanitizedString(student.comments)) {
            println("comments: " + student.comments);
            println("new comments: " + fixSanitization(student.comments));
        }
        if (isSanitizedString(student.course)) {
            println("course: " + student.course);
            println("new course: " + fixSanitization(student.course));
        }
        if (isSanitizedString(student.email)) {
            println("email: " + student.email);
            println("new email: " + fixSanitization(student.email));
        }
        if (isSanitizedString(student.googleId)) {
            println("googleId: " + student.googleId);
            println("new googleId: " + fixSanitization(student.googleId));
        }
        if (isSanitizedString(student.lastName)) {
            println("lastName: " + student.lastName);
            println("new lastName: " + fixSanitization(student.lastName));
        }
        if (isSanitizedString(student.name)) {
            println("name: " + student.name);
            println("new name: " + fixSanitization(student.name));
        }
        if (isSanitizedString(student.section)) {
            println("section: " + student.section);
            println("new section: " + fixSanitization(student.section));
        }
        if (isSanitizedString(student.team)) {
            println("team: " + student.team);
            println("new team: " + fixSanitization(student.team));
        }
        println("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void migrate(StudentAttributes student) throws InvalidParametersException, EntityDoesNotExistException {
        fixSanitizationForStudent(student);
        updateStudent(student.email, student);
    }

    /**
     * {@inheritDoc}
     */
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

    private void fixSanitizationForStudent(StudentAttributes student) {
        student.comments = fixSanitization(student.comments);
        student.course = fixSanitization(student.course);
        student.email = fixSanitization(student.email);
        student.googleId = fixSanitization(student.googleId);
        student.lastName = fixSanitization(student.lastName);
        student.name = fixSanitization(student.name);
        student.section = fixSanitization(student.section);
        student.team = fixSanitization(student.team);
    }

    private void updateStudent(String originalEmail, StudentAttributes student)
            throws InvalidParametersException, EntityDoesNotExistException {
        studentsDb.verifyStudentExists(student.course, originalEmail);
        StudentAttributes originalStudent = studentsDb.getStudentForEmail(student.course, originalEmail);

        // prepare new student
        student.updateWithExistingRecord(originalStudent);

        if (!student.isValid()) {
            throw new InvalidParametersException(student.getInvalidityInfo());
        }

        studentsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.section,
                                 student.email, student.googleId, student.comments, true);
    }

}
