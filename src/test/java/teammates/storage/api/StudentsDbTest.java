package teammates.storage.api;

import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.test.AssertHelper;
import teammates.test.BaseComponentTestCase;

/**
 * SUT: {@link StudentsDb}.
 */
public class StudentsDbTest extends BaseComponentTestCase {

    private StudentsDb studentsDb = new StudentsDb();

    @Test
    public void testTimestamp() throws Exception {
        ______TS("success : created");

        StudentAttributes s = createNewStudent();

        StudentAttributes student = studentsDb.getStudentForEmail(s.course, s.email);
        assertNotNull(student);

        // Assert dates are now.
        AssertHelper.assertInstantIsNow(student.getCreatedAt());
        AssertHelper.assertInstantIsNow(student.getUpdatedAt());

        ______TS("success : update lastUpdated");

        // wait for very briefly so that the update timestamp is guaranteed to change
        ThreadHelper.waitFor(5);

        s.name = "new-name";
        studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.course, s.email)
                        .withName(s.name)
                        .build());
        StudentAttributes updatedStudent = studentsDb.getStudentForGoogleId(s.course, s.googleId);

        // Assert lastUpdate has changed, and is now.
        assertFalse(student.getUpdatedAt().equals(updatedStudent.getUpdatedAt()));
        AssertHelper.assertInstantIsNow(updatedStudent.getUpdatedAt());
    }

    @Test
    public void testCreateStudent() throws Exception {

        StudentAttributes s = StudentAttributes
                .builder("course id", "valid-fresh@email.com")
                .withName("valid student")
                .withComment("")
                .withTeamName("validTeamName")
                .withSectionName("validSectionName")
                .withGoogleId("validGoogleId")
                .withLastName("student")
                .build();

        ______TS("fail : invalid params");
        s.course = "invalid id space";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class, () -> studentsDb.createEntity(s));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        COURSE_ID_ERROR_MESSAGE, s.course,
                        FieldValidator.COURSE_ID_FIELD_NAME, REASON_INCORRECT_FORMAT,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                ipe.getMessage());
        verifyAbsentInDatastore(s);

        ______TS("success : valid params");
        s.course = "valid-course";

        // remove possibly conflicting entity from the database
        studentsDb.deleteStudent(s.course, s.email);

        studentsDb.createEntity(s);
        verifyPresentInDatastore(s);
        StudentAttributes retrievedStudent = studentsDb.getStudentForGoogleId(s.course, s.googleId);
        assertTrue(isEnrollInfoSameAs(retrievedStudent, s));
        assertNull(studentsDb.getStudentForGoogleId(s.course + "not existing", s.googleId));
        assertNull(studentsDb.getStudentForGoogleId(s.course, s.googleId + "not existing"));
        assertNull(studentsDb.getStudentForGoogleId(s.course + "not existing", s.googleId + "not existing"));

        ______TS("fail : duplicate");
        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> studentsDb.createEntity(s));
        assertEquals(
                String.format(StudentsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, s.toString()), eaee.getMessage());

        ______TS("null params check");
        assertThrows(AssertionError.class, () -> studentsDb.createEntity(null));

        studentsDb.deleteStudent(s.getCourse(), s.getEmail());
    }

    @Test
    public void testGetStudent() throws Exception {

        StudentAttributes s = createNewStudent();
        s.googleId = "validGoogleId";
        s.team = "validTeam";
        studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.course, s.email)
                        .withGoogleId(s.googleId)
                        .withTeamName(s.team)
                        .build());

        ______TS("typical success case for getStudentForRegistrationKey: existing student");
        StudentAttributes retrieved = studentsDb.getStudentForEmail(s.course, s.email);
        assertNotNull(retrieved);
        assertNotNull(studentsDb.getStudentForRegistrationKey(StringHelper.encrypt(retrieved.key)));

        assertNull(studentsDb.getStudentForRegistrationKey(StringHelper.encrypt("notExistingKey")));

        ______TS("non existant student case");

        retrieved = studentsDb.getStudentForEmail("any-course-id", "non-existent@email.com");
        assertNull(retrieved);

        StudentAttributes s2 = createNewStudent("one.new@gmail.com");
        s2.googleId = "validGoogleId2";

        studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s2.course, s2.email)
                        .withGoogleId(s2.googleId)
                        .build());
        studentsDb.deleteStudent(s2.getCourse(), s2.getEmail());

        assertNull(studentsDb.getStudentForGoogleId(s2.course, s2.googleId));

        s2 = createNewStudent("one.new@gmail.com");
        assertTrue(isEnrollInfoSameAs(studentsDb.getUnregisteredStudentsForCourse(s2.course).get(0), s2));

        assertTrue(isEnrollInfoSameAs(s, studentsDb.getStudentsForGoogleId(s.googleId).get(0)));
        assertTrue(isEnrollInfoSameAs(studentsDb.getStudentsForCourse(s.course).get(0), s)
                || isEnrollInfoSameAs(studentsDb.getStudentsForCourse(s.course).get(0), s2));
        assertTrue(isEnrollInfoSameAs(studentsDb.getStudentsForTeam(s.team, s.course).get(0), s));

        ______TS("null params case");
        assertThrows(AssertionError.class, () -> studentsDb.getStudentForEmail(null, "valid@email.com"));

        assertThrows(AssertionError.class, () -> studentsDb.getStudentForEmail("any-course-id", null));

        studentsDb.deleteStudent(s.course, s.email);
        studentsDb.deleteStudent(s2.course, s2.email);
    }

    @Test
    public void testUpdateStudent_noChangeToStudent_shouldNotIssueSaveRequest() throws Exception {
        StudentAttributes s = createNewStudent();

        StudentAttributes updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.getCourse(), s.getEmail())
                        .build());

        assertEquals(JsonUtils.toJson(s), JsonUtils.toJson(updatedStudent));
        assertEquals(s.getUpdatedAt(), studentsDb.getStudentForEmail(s.getCourse(), s.getEmail()).getUpdatedAt());

        updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.getCourse(), s.getEmail())
                        .withName(s.getName())
                        .withLastName(s.getLastName())
                        .withComment(s.getComments())
                        .withGoogleId(s.getGoogleId())
                        .withTeamName(s.getTeam())
                        .withSectionName(s.getSection())
                        .build());

        assertEquals(JsonUtils.toJson(s), JsonUtils.toJson(updatedStudent));
        assertEquals(s.getUpdatedAt(), studentsDb.getStudentForEmail(s.getCourse(), s.getEmail()).getUpdatedAt());
    }

    @Test
    public void testUpdateStudent() throws Exception {

        // Create a new student with valid attributes
        StudentAttributes s = createNewStudent();

        studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.course, s.email)
                        .withGoogleId("new.google.id")
                        .withComment("lorem ipsum dolor si amet")
                        .withNewEmail("new@email.com")
                        .withSectionName("new-section")
                        .withTeamName("new-team")
                        .withName("new-name")
                        .build());

        ______TS("non-existent case");
        StudentAttributes.UpdateOptions updateOptions =
                StudentAttributes.updateOptionsBuilder("non-existent-course", "non@existent.email")
                        .withName("no-name")
                        .build();
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> studentsDb.updateStudent(updateOptions));
        assertEquals(StudentsDb.ERROR_UPDATE_NON_EXISTENT + updateOptions, ednee.getMessage());

        ______TS("null course case");
        assertThrows(AssertionError.class,
                () -> studentsDb.updateStudent(
                        StudentAttributes.updateOptionsBuilder(null, s.email)
                                .withName("new-name")
                                .build()));

        ______TS("null email case");
        assertThrows(AssertionError.class,
                () -> studentsDb.updateStudent(
                        StudentAttributes.updateOptionsBuilder(s.course, null)
                                .withName("new-name")
                                .build()));

        ______TS("duplicate email case");
        StudentAttributes duplicate = createNewStudent();
        // Create a second student with different email address
        StudentAttributes s2 = createNewStudent("valid2@email.com");
        StudentAttributes.UpdateOptions updateOptionsForS2 =
                StudentAttributes.updateOptionsBuilder(duplicate.course, duplicate.email)
                        .withNewEmail(s2.email)
                        .build();
        assertThrows(EntityAlreadyExistsException.class, () -> studentsDb.updateStudent(updateOptionsForS2));

        // clean up
        studentsDb.deleteStudent(s2.getCourse(), s2.getEmail());

        ______TS("typical success case");
        String originalEmail = s.email;
        s.name = "new-name-2";
        s.team = "new-team-2";
        s.email = "new-email-2@email.com";
        s.googleId = "new-id-2";
        s.comments = "this are new comments";

        StudentAttributes updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.course, originalEmail)
                        .withNewEmail(s.email)
                        .withName(s.name)
                        .withTeamName(s.team)
                        .withSectionName(s.section)
                        .withGoogleId(s.googleId)
                        .withComment(s.comments)
                        .build());

        StudentAttributes actualStudent = studentsDb.getStudentForEmail(s.course, s.email);
        assertTrue(isEnrollInfoSameAs(actualStudent, s));
        // the original student is deleted
        assertNull(studentsDb.getStudentForEmail(s.course, originalEmail));
        assertEquals("new-email-2@email.com", updatedStudent.getEmail());
        assertEquals("new-name-2", updatedStudent.getName());
        assertEquals("new-team-2", updatedStudent.getTeam());
        assertEquals("new-id-2", updatedStudent.googleId);
        assertEquals("this are new comments", updatedStudent.getComments());
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateStudent_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        StudentAttributes typicalStudent = createNewStudent();

        assertNotEquals("John Doe", typicalStudent.getName());
        StudentAttributes updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(typicalStudent.getCourse(), typicalStudent.getEmail())
                        .withName("John Doe")
                        .build());
        StudentAttributes actualStudent =
                studentsDb.getStudentForEmail(typicalStudent.getCourse(), typicalStudent.getEmail());
        assertEquals("John Doe", updatedStudent.getName());
        assertEquals("John Doe", actualStudent.getName());

        assertNotEquals("Wu", actualStudent.getLastName());
        updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(typicalStudent.getCourse(), typicalStudent.getEmail())
                        .withLastName("Wu")
                        .build());
        actualStudent = studentsDb.getStudentForEmail(typicalStudent.getCourse(), typicalStudent.getEmail());
        assertEquals("Wu", updatedStudent.getLastName());
        assertEquals("Wu", actualStudent.getLastName());

        assertNotEquals("Comment", actualStudent.getComments());
        updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(typicalStudent.getCourse(), typicalStudent.getEmail())
                        .withComment("Comment")
                        .build());
        actualStudent = studentsDb.getStudentForEmail(typicalStudent.getCourse(), typicalStudent.getEmail());
        assertEquals("Comment", updatedStudent.getComments());
        assertEquals("Comment", actualStudent.getComments());

        assertNotEquals("googleId", actualStudent.getGoogleId());
        updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(typicalStudent.getCourse(), typicalStudent.getEmail())
                        .withGoogleId("googleId")
                        .build());
        actualStudent = studentsDb.getStudentForEmail(typicalStudent.getCourse(), typicalStudent.getEmail());
        assertEquals("googleId", updatedStudent.getGoogleId());
        assertEquals("googleId", actualStudent.getGoogleId());

        assertNotEquals("teamName", actualStudent.getTeam());
        updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(typicalStudent.getCourse(), typicalStudent.getEmail())
                        .withTeamName("teamName")
                        .build());
        actualStudent = studentsDb.getStudentForEmail(typicalStudent.getCourse(), typicalStudent.getEmail());
        assertEquals("teamName", updatedStudent.getTeam());
        assertEquals("teamName", actualStudent.getTeam());

        assertNotEquals("sectionName", actualStudent.getSection());
        updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(typicalStudent.getCourse(), typicalStudent.getEmail())
                        .withSectionName("sectionName")
                        .build());
        actualStudent = studentsDb.getStudentForEmail(typicalStudent.getCourse(), typicalStudent.getEmail());
        assertEquals("sectionName", updatedStudent.getSection());
        assertEquals("sectionName", actualStudent.getSection());
    }

    @Test
    public void testDeleteStudent() throws Exception {
        StudentAttributes s = createNewStudent();

        // delete student not exist
        studentsDb.deleteStudent("not-exist", s.getEmail());
        assertNotNull(studentsDb.getStudentForEmail(s.getCourse(), s.getEmail()));

        studentsDb.deleteStudent(s.getCourse(), "not_exist@email.com");
        assertNotNull(studentsDb.getStudentForEmail(s.getCourse(), s.getEmail()));

        studentsDb.deleteStudent("not-exist", "not_exist@email.com");
        assertNotNull(studentsDb.getStudentForEmail(s.getCourse(), s.getEmail()));

        // delete by course and email
        studentsDb.deleteStudent(s.course, s.email);
        StudentAttributes deleted = studentsDb.getStudentForEmail(s.course, s.email);
        assertNull(deleted);

        // delete again - should fail silently
        studentsDb.deleteStudent(s.course, s.email);
        assertNull(studentsDb.getStudentForEmail(s.getCourse(), s.getEmail()));

        s = createNewStudent();

        // delete all students in non-existent course
        studentsDb.deleteStudents(
                AttributesDeletionQuery.builder()
                        .withCourseId("not_exist")
                        .build());

        // should pass, others students remain
        assertEquals(1, studentsDb.getStudentsForCourse(s.course).size());

        // delete all students in a course

        // create another student in different course
        StudentAttributes anotherStudent = StudentAttributes
                .builder("valid-course2", "email@email.com")
                .withName("valid student 2")
                .withComment("")
                .withTeamName("valid team name")
                .withSectionName("valid section name")
                .withGoogleId("")
                .build();
        studentsDb.createEntity(anotherStudent);
        assertNotNull(studentsDb.getStudentForEmail(anotherStudent.getCourse(), anotherStudent.getEmail()));

        // there are students in the course
        assertFalse(studentsDb.getStudentsForCourse(s.course).isEmpty());

        studentsDb.deleteStudents(
                AttributesDeletionQuery.builder()
                        .withCourseId(s.course)
                        .build());

        assertEquals(0, studentsDb.getStudentsForCourse(s.course).size());
        // other course should remain
        assertEquals(1, studentsDb.getStudentsForCourse(anotherStudent.getCourse()).size());

        // clean up
        studentsDb.deleteStudent(anotherStudent.getCourse(), anotherStudent.getEmail());

        // null params check:
        StudentAttributes[] finalStudent = new StudentAttributes[] { s };
        assertThrows(AssertionError.class,
                () -> studentsDb.deleteStudent(null, finalStudent[0].email));

        assertThrows(AssertionError.class,
                () -> studentsDb.deleteStudent(finalStudent[0].course, null));
    }

    private StudentAttributes createNewStudent() throws Exception {
        StudentAttributes s = StudentAttributes
                .builder("valid-course", "valid@email.com")
                .withName("valid student")
                .withComment("")
                .withTeamName("validTeamName")
                .withSectionName("validSectionName")
                .withGoogleId("")
                .build();

        studentsDb.deleteStudent(s.getCourse(), s.getEmail());
        return studentsDb.createEntity(s);
    }

    private StudentAttributes createNewStudent(String email) throws Exception {
        StudentAttributes s = StudentAttributes
                .builder("valid-course", email)
                .withName("valid student 2")
                .withComment("")
                .withTeamName("valid team name")
                .withSectionName("valid section name")
                .withGoogleId("")
                .build();

        studentsDb.deleteStudent(s.getCourse(), s.getEmail());
        return studentsDb.createEntity(s);
    }

    private boolean isEnrollInfoSameAs(StudentAttributes student, StudentAttributes otherStudent) {
        return otherStudent != null && otherStudent.email.equals(student.email)
                && otherStudent.course.equals(student.course)
                && otherStudent.name.equals(student.name)
                && otherStudent.comments.equals(student.comments)
                && otherStudent.team.equals(student.team)
                && otherStudent.section.equals(student.section);
    }

}
