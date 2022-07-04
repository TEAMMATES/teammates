package teammates.storage.api;

import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import java.util.ArrayList;
import java.util.Collection;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;
import teammates.test.ThreadHelper;

/**
 * SUT: {@link StudentsDb}.
 */
public class StudentsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final StudentsDb studentsDb = StudentsDb.inst();

    @Test
    public void testTimestamp() throws Exception {
        ______TS("success : created");

        StudentAttributes s = createNewStudent();

        StudentAttributes student = studentsDb.getStudentForEmail(s.getCourse(), s.getEmail());
        assertNotNull(student);

        // Assert dates are now.
        AssertHelper.assertInstantIsNow(student.getCreatedAt());
        AssertHelper.assertInstantIsNow(student.getUpdatedAt());

        ______TS("success : update lastUpdated");

        // wait for very briefly so that the update timestamp is guaranteed to change
        ThreadHelper.waitFor(5);

        s.setName("new-name");
        studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.getCourse(), s.getEmail())
                        .withName(s.getName())
                        .build());
        StudentAttributes updatedStudent = studentsDb.getStudentForGoogleId(s.getCourse(), s.getGoogleId());

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
                .build();

        ______TS("fail : invalid params");
        s.setCourse("invalid id space");
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class, () -> studentsDb.createEntity(s));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        COURSE_ID_ERROR_MESSAGE, s.getCourse(),
                        FieldValidator.COURSE_ID_FIELD_NAME, REASON_INCORRECT_FORMAT,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                ipe.getMessage());
        verifyAbsentInDatabase(s);

        ______TS("success : valid params");
        s.setCourse("valid-course");

        // remove possibly conflicting entity from the database
        studentsDb.deleteStudent(s.getCourse(), s.getEmail());

        studentsDb.createEntity(s);
        verifyPresentInDatabase(s);
        StudentAttributes retrievedStudent = studentsDb.getStudentForGoogleId(s.getCourse(), s.getGoogleId());
        assertTrue(isEnrollInfoSameAs(retrievedStudent, s));
        assertNull(studentsDb.getStudentForGoogleId(s.getCourse() + "not existing", s.getGoogleId()));
        assertNull(studentsDb.getStudentForGoogleId(s.getCourse(), s.getGoogleId() + "not existing"));
        assertNull(studentsDb.getStudentForGoogleId(s.getCourse() + "not existing", s.getGoogleId() + "not existing"));

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
    public void testHasExistingStudentsInCourse() throws Exception {

        StudentAttributes student1 = createNewStudent("student1@uni.edu");
        StudentAttributes student2 = createNewStudent("student2@uni.edu");
        String courseId = student1.getCourse();
        assertEquals(courseId, student2.getCourse());
        String nonExistentCourseId = "non-existent-course";

        Collection<String> studentEmailAddresses = new ArrayList<>();
        studentEmailAddresses.add(student1.getEmail());

        ______TS("all existing student email addresses");

        assertTrue(studentsDb.hasExistingStudentsInCourse(courseId, studentEmailAddresses));

        studentEmailAddresses.add(student2.getEmail());
        assertTrue(studentsDb.hasExistingStudentsInCourse(courseId, studentEmailAddresses));

        ______TS("all existing student email addresses in non-existent course");

        assertFalse(studentsDb.hasExistingStudentsInCourse(nonExistentCourseId, studentEmailAddresses));

        ______TS("some non-existent student email address in existing course");

        studentEmailAddresses.add("non-existent.student@email.com");

        assertFalse(studentsDb.hasExistingStudentsInCourse(courseId, studentEmailAddresses));

        ______TS("some non-existent student email address in non-existent course");

        assertFalse(studentsDb.hasExistingStudentsInCourse(nonExistentCourseId, studentEmailAddresses));
    }

    @Test
    public void testGetStudent() throws Exception {

        StudentAttributes s = createNewStudent();
        s.setGoogleId("validGoogleId");
        s.setTeam("validTeam");
        studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.getCourse(), s.getEmail())
                        .withGoogleId(s.getGoogleId())
                        .withTeamName(s.getTeam())
                        .build());

        ______TS("typical success case for getStudentForRegistrationKey: existing student");
        StudentAttributes retrieved = studentsDb.getStudentForEmail(s.getCourse(), s.getEmail());
        assertNotNull(retrieved);
        assertNotNull(studentsDb.getStudentForRegistrationKey(retrieved.getKey()));

        assertNull(studentsDb.getStudentForRegistrationKey(StringHelper.encrypt("notExistingKey")));

        ______TS("non existant student case");

        retrieved = studentsDb.getStudentForEmail("any-course-id", "non-existent@email.com");
        assertNull(retrieved);

        StudentAttributes s2 = createNewStudent("one.new@gmail.com");
        s2.setGoogleId("validGoogleId2");

        studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s2.getCourse(), s2.getEmail())
                        .withGoogleId(s2.getGoogleId())
                        .build());
        studentsDb.deleteStudent(s2.getCourse(), s2.getEmail());

        assertNull(studentsDb.getStudentForGoogleId(s2.getCourse(), s2.getGoogleId()));

        s2 = createNewStudent("one.new@gmail.com");
        assertTrue(isEnrollInfoSameAs(studentsDb.getUnregisteredStudentsForCourse(s2.getCourse()).get(0), s2));

        assertTrue(isEnrollInfoSameAs(s, studentsDb.getStudentsForGoogleId(s.getGoogleId()).get(0)));
        assertTrue(isEnrollInfoSameAs(studentsDb.getStudentsForCourse(s.getCourse()).get(0), s)
                || isEnrollInfoSameAs(studentsDb.getStudentsForCourse(s.getCourse()).get(0), s2));
        assertTrue(isEnrollInfoSameAs(studentsDb.getStudentsForTeam(s.getTeam(), s.getCourse()).get(0), s));

        ______TS("null params case");
        assertThrows(AssertionError.class, () -> studentsDb.getStudentForEmail(null, "valid@email.com"));

        assertThrows(AssertionError.class, () -> studentsDb.getStudentForEmail("any-course-id", null));

        studentsDb.deleteStudent(s.getCourse(), s.getEmail());
        studentsDb.deleteStudent(s2.getCourse(), s2.getEmail());
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
                StudentAttributes.updateOptionsBuilder(s.getCourse(), s.getEmail())
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
                        StudentAttributes.updateOptionsBuilder(null, s.getEmail())
                                .withName("new-name")
                                .build()));

        ______TS("null email case");
        assertThrows(AssertionError.class,
                () -> studentsDb.updateStudent(
                        StudentAttributes.updateOptionsBuilder(s.getCourse(), null)
                                .withName("new-name")
                                .build()));

        ______TS("duplicate email case");
        StudentAttributes duplicate = createNewStudent();
        // Create a second student with different email address
        StudentAttributes s2 = createNewStudent("valid2@email.com");
        StudentAttributes.UpdateOptions updateOptionsForS2 =
                StudentAttributes.updateOptionsBuilder(duplicate.getCourse(), duplicate.getEmail())
                        .withNewEmail(s2.getEmail())
                        .build();
        assertThrows(EntityAlreadyExistsException.class, () -> studentsDb.updateStudent(updateOptionsForS2));

        // clean up
        studentsDb.deleteStudent(s2.getCourse(), s2.getEmail());

        ______TS("typical success case");
        String originalEmail = s.getEmail();
        s.setName("new-name-2");
        s.setTeam("new-team-2");
        s.setEmail("new-email-2@email.com");
        s.setGoogleId("new-id-2");
        s.setComments("this are new comments");

        StudentAttributes updatedStudent = studentsDb.updateStudent(
                StudentAttributes.updateOptionsBuilder(s.getCourse(), originalEmail)
                        .withNewEmail(s.getEmail())
                        .withName(s.getName())
                        .withTeamName(s.getTeam())
                        .withSectionName(s.getSection())
                        .withGoogleId(s.getGoogleId())
                        .withComment(s.getComments())
                        .build());

        StudentAttributes actualStudent = studentsDb.getStudentForEmail(s.getCourse(), s.getEmail());
        assertTrue(isEnrollInfoSameAs(actualStudent, s));
        // the original student is deleted
        assertNull(studentsDb.getStudentForEmail(s.getCourse(), originalEmail));
        assertEquals("new-email-2@email.com", updatedStudent.getEmail());
        assertEquals("new-name-2", updatedStudent.getName());
        assertEquals("new-team-2", updatedStudent.getTeam());
        assertEquals("new-id-2", updatedStudent.getGoogleId());
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
        studentsDb.deleteStudent(s.getCourse(), s.getEmail());
        StudentAttributes deleted = studentsDb.getStudentForEmail(s.getCourse(), s.getEmail());
        assertNull(deleted);

        // delete again - should fail silently
        studentsDb.deleteStudent(s.getCourse(), s.getEmail());
        assertNull(studentsDb.getStudentForEmail(s.getCourse(), s.getEmail()));

        s = createNewStudent();

        // delete all students in non-existent course
        studentsDb.deleteStudents(
                AttributesDeletionQuery.builder()
                        .withCourseId("not_exist")
                        .build());

        // should pass, others students remain
        assertEquals(1, studentsDb.getNumberOfStudentsForCourse(s.getCourse()));

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
        assertNotEquals(0, studentsDb.getNumberOfStudentsForCourse(s.getCourse()));

        studentsDb.deleteStudents(
                AttributesDeletionQuery.builder()
                        .withCourseId(s.getCourse())
                        .build());

        assertEquals(0, studentsDb.getNumberOfStudentsForCourse(s.getCourse()));
        // other course should remain
        assertEquals(1, studentsDb.getNumberOfStudentsForCourse(anotherStudent.getCourse()));

        // clean up
        studentsDb.deleteStudent(anotherStudent.getCourse(), anotherStudent.getEmail());

        // null params check:
        StudentAttributes[] finalStudent = new StudentAttributes[] { s };
        assertThrows(AssertionError.class,
                () -> studentsDb.deleteStudent(null, finalStudent[0].getEmail()));

        assertThrows(AssertionError.class,
                () -> studentsDb.deleteStudent(finalStudent[0].getCourse(), null));
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
        return otherStudent != null && otherStudent.getEmail().equals(student.getEmail())
                && otherStudent.getCourse().equals(student.getCourse())
                && otherStudent.getName().equals(student.getName())
                && otherStudent.getComments().equals(student.getComments())
                && otherStudent.getTeam().equals(student.getTeam())
                && otherStudent.getSection().equals(student.getSection());
    }

}
