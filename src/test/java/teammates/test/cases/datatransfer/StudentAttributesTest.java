package teammates.test.cases.datatransfer;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.CourseStudent;
import teammates.test.cases.BaseTestCaseWithMinimalGaeEnvironment;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link StudentAttributes}.
 */
public class StudentAttributesTest extends BaseTestCaseWithMinimalGaeEnvironment {

    @Test
    public void testBuilderWithDefaultValues() {
        StudentAttributes sd = StudentAttributes
                .builder("courseId", "Joe White", "e@e.com")
                .build();

        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, sd.getCreatedAt());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, sd.getUpdatedAt());
        assertEquals("", sd.googleId);
        assertEquals(Const.DEFAULT_SECTION, sd.section);
        assertEquals(StudentUpdateStatus.UNKNOWN, sd.updateStatus);
        assertEquals("White", sd.lastName);
    }

    @Test
    public void testBuilderWithNullValues() {
        StudentAttributes sd = StudentAttributes
                .builder("courseID", "name", "email")
                .withGoogleId(null).withUpdatedAt(null).withCreatedAt(null)
                .withKey(null).withSection(null).withTeam(null)
                .withComments(null).withComments(null)
                .withLastName(null).withUpdateStatus(null)
                .build();

        // Fields with default values
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, sd.getCreatedAt());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, sd.getUpdatedAt());
        assertEquals("", sd.googleId);
        assertEquals(Const.DEFAULT_SECTION, sd.section);
        assertEquals(StudentUpdateStatus.UNKNOWN, sd.updateStatus);

        // Nullable fields
        assertNull(sd.key);
        assertNull(sd.team);
        assertNull(sd.comments);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testBuilderWithNullValuesForRequiredFields() {
        StudentAttributes.builder(null, null, null)
                .build();
    }

    @Test
    public void testBuilderCopy() {
        StudentAttributes originalStudent = StudentAttributes
                .builder("courseId1", "name 1", "email@email.com")
                .withGoogleId("googleId.1").withSection("section 1")
                .withComments("comment 1").withTeam("team 1")
                .build();

        StudentAttributes copyStudent = originalStudent.getCopy();

        assertEquals(originalStudent.course, copyStudent.course);
        assertEquals(originalStudent.name, copyStudent.name);
        assertEquals(originalStudent.email, copyStudent.email);
        assertEquals(originalStudent.googleId, copyStudent.googleId);
        assertEquals(originalStudent.comments, copyStudent.comments);
        assertEquals(originalStudent.key, copyStudent.key);
        assertEquals(originalStudent.updateStatus, copyStudent.updateStatus);
        assertEquals(originalStudent.lastName, copyStudent.lastName);
        assertEquals(originalStudent.section, copyStudent.section);
        assertEquals(originalStudent.team, copyStudent.team);
        assertEquals(originalStudent.getCreatedAt(), copyStudent.getCreatedAt());
        assertEquals(originalStudent.getUpdatedAt(), copyStudent.getUpdatedAt());
    }

    @Test
    public void testValueOf() {
        CourseStudent originalStudent = new CourseStudent("email@email.com", "name 1", "googleId.1",
                "comment 1", "courseId1", "team 1", "sect 1");
        StudentAttributes copyStudent = StudentAttributes.valueOf(originalStudent);

        assertEquals(originalStudent.getCourseId(), copyStudent.course);
        assertEquals(originalStudent.getName(), copyStudent.name);
        assertEquals(originalStudent.getEmail(), copyStudent.email);
        assertEquals(originalStudent.getGoogleId(), copyStudent.googleId);
        assertEquals(originalStudent.getComments(), copyStudent.comments);
        assertEquals(originalStudent.getRegistrationKey(), copyStudent.key);
        assertEquals(originalStudent.getLastName(), copyStudent.lastName);
        assertEquals(originalStudent.getSectionName(), copyStudent.section);
        assertEquals(originalStudent.getTeamName(), copyStudent.team);
        assertEquals(originalStudent.getCreatedAt(), copyStudent.getCreatedAt());
        assertEquals(originalStudent.getUpdatedAt(), copyStudent.getUpdatedAt());
    }

    @Test
    public void testUpdateStatusEnum() {
        assertEquals(StudentUpdateStatus.ERROR, StudentUpdateStatus.enumRepresentation(0));
        assertEquals(StudentUpdateStatus.NEW, StudentUpdateStatus.enumRepresentation(1));
        assertEquals(StudentUpdateStatus.MODIFIED, StudentUpdateStatus.enumRepresentation(2));
        assertEquals(StudentUpdateStatus.UNMODIFIED, StudentUpdateStatus.enumRepresentation(3));
        assertEquals(StudentUpdateStatus.NOT_IN_ENROLL_LIST, StudentUpdateStatus.enumRepresentation(4));
        assertEquals(StudentUpdateStatus.UNKNOWN, StudentUpdateStatus.enumRepresentation(5));
        assertEquals(StudentUpdateStatus.UNKNOWN, StudentUpdateStatus.enumRepresentation(-1));
    }

    @Test
    public void testStudentBuilder() throws Exception {
        String courseId = "anyCourseId";
        StudentAttributes invalidStudent;

        CourseStudent expected;
        StudentAttributes studentUnderTest;

        ______TS("Typical case: contains white space");
        expected = generateTypicalStudentObject();
        studentUnderTest = StudentAttributes
                .builder("courseId1", "   name 1   ", "   email@email.com  ")
                .withSection("  sect 1 ").withComments("  comment 1  ").withTeam("  team 1   ")
                .build();
        verifyStudentContent(expected, (CourseStudent) studentUnderTest.toEntity());

        ______TS("Typical case: contains google id");
        expected = generateTypicalStudentObject();
        studentUnderTest = StudentAttributes
                .builder("courseId1", "name 1", "email@email.com")
                .withGoogleId("googleId.1").withSection("section 1")
                .withComments("comment 1").withTeam("team 1")
                .build();

        verifyStudentContentIncludingId(expected, (CourseStudent) studentUnderTest.toEntity());

        ______TS("Typical case: initialize from entity");
        expected = generateTypicalStudentObject();
        studentUnderTest = StudentAttributes.valueOf(expected);

        verifyStudentContentIncludingId(expected, (CourseStudent) studentUnderTest.toEntity());

        ______TS("Failure case: empty course id");
        invalidStudent = StudentAttributes
                .builder("", "name", "e@e.com")
                .withSection("section").withComments("c").withTeam("team")
                .build();
        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedEmptyStringErrorMessage(
                         FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                         FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: invalid course id");
        invalidStudent = StudentAttributes
                .builder("Course Id with space", "name", "e@e.com")
                .withSection("section").withComments("c").withTeam("team")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.COURSE_ID_ERROR_MESSAGE, invalidStudent.course,
                         FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                         FieldValidator.COURSE_ID_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: empty name");
        invalidStudent = StudentAttributes
                .builder(courseId, "", "e@e.com")
                .withSection("sect").withComments("c").withTeam("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(invalidStudent.getInvalidityInfo().get(0),
                     getPopulatedEmptyStringErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                         FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH));

        ______TS("Failure case: empty email");
        invalidStudent = StudentAttributes
                .builder(courseId, "n", "")
                .withSection("sect").withComments("c").withTeam("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedEmptyStringErrorMessage(
                         FieldValidator.EMAIL_ERROR_MESSAGE_EMPTY_STRING,
                         FieldValidator.EMAIL_FIELD_NAME, FieldValidator.EMAIL_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: section name too long");
        String longSectionName = StringHelperExtension
                .generateStringOfLength(FieldValidator.SECTION_NAME_MAX_LENGTH + 1);
        invalidStudent = StudentAttributes
                .builder(courseId, "", "e@e.com")
                .withSection(longSectionName).withComments("c").withTeam("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longSectionName,
                         FieldValidator.SECTION_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                         FieldValidator.SECTION_NAME_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: team name too long");
        String longTeamName = StringHelperExtension.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
        invalidStudent = StudentAttributes
                .builder(courseId, "", "e@e.com")
                .withSection("sect").withComments("c").withTeam(longTeamName)
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longTeamName,
                         FieldValidator.TEAM_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                         FieldValidator.TEAM_NAME_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: student name too long");
        String longStudentName = StringHelperExtension
                .generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        invalidStudent = StudentAttributes
                .builder(courseId, longStudentName, "e@e.com")
                .withSection("sect").withComments("c").withTeam("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longStudentName,
                         FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                         FieldValidator.PERSON_NAME_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: invalid email");
        invalidStudent = StudentAttributes
                .builder(courseId, "name", "ee.com")
                .withSection("sect").withComments("c").withTeam("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.EMAIL_ERROR_MESSAGE, "ee.com",
                         FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                         FieldValidator.EMAIL_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: comment too long");
        String longComment = StringHelperExtension
                .generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH + 1);
        invalidStudent = StudentAttributes
                .builder(courseId, "name", "e@e.com")
                .withSection("sect").withComments(longComment).withTeam("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(
                getPopulatedErrorMessage(
                    FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, longComment,
                    FieldValidator.STUDENT_ROLE_COMMENTS_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                    FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH),
                invalidStudent.getInvalidityInfo().get(0));

        // Other invalid parameters cases are omitted because they are already
        // unit-tested in validate*() methods in Common.java
    }

    @Test
    public void testValidate() throws Exception {
        StudentAttributes s = generateValidStudentAttributesObject();

        assertTrue("valid value", s.isValid());

        s.googleId = "invalid@google@id";
        s.name = "";
        s.email = "invalid email";
        s.course = "";
        s.comments = StringHelperExtension.generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH + 1);
        s.team = StringHelperExtension.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);

        assertFalse("invalid value", s.isValid());
        String errorMessage =
                getPopulatedErrorMessage(
                    FieldValidator.GOOGLE_ID_ERROR_MESSAGE, "invalid@google@id",
                    FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                    FieldValidator.GOOGLE_ID_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedErrorMessage(
                      FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                      FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                      FieldValidator.EMAIL_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                      "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                      FieldValidator.TEAM_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                      FieldValidator.TEAM_NAME_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedErrorMessage(
                      FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, s.comments,
                      FieldValidator.STUDENT_ROLE_COMMENTS_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                      FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH);
        assertEquals("invalid value", errorMessage, StringHelper.toString(s.getInvalidityInfo()));
    }

    @Test
    public void testIsEnrollInfoSameAs() {
        StudentAttributes student = StudentAttributes.valueOf(generateTypicalStudentObject());
        StudentAttributes other = StudentAttributes.valueOf(generateTypicalStudentObject());

        ______TS("Typical case: Same enroll info");
        assertTrue(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Compare to null");
        assertFalse(student.isEnrollInfoSameAs(null));

        ______TS("Typical case: Different in email");
        other.email = "other@email.com";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in name");
        other = StudentAttributes.valueOf(generateTypicalStudentObject());
        other.name = "otherName";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in course id");
        other = StudentAttributes.valueOf(generateTypicalStudentObject());
        other.course = "otherCourse";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in comment");
        other = StudentAttributes.valueOf(generateTypicalStudentObject());
        other.comments = "otherComments";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in team");
        other = StudentAttributes.valueOf(generateTypicalStudentObject());
        other.team = "otherTeam";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in section");
        other = StudentAttributes.valueOf(generateStudentWithoutSectionObject());
        assertFalse(student.isEnrollInfoSameAs(other));
    }

    @Test
    public void testSortByNameAndThenByEmail() {
        List<StudentAttributes> sortedList = generateTypicalStudentAttributesList();
        StudentAttributes.sortByNameAndThenByEmail(sortedList);
        List<StudentAttributes> unsortedList = generateTypicalStudentAttributesList();
        assertEquals(sortedList.get(0).toEnrollmentString(), unsortedList.get(0).toEnrollmentString());
        assertEquals(sortedList.get(1).toEnrollmentString(), unsortedList.get(3).toEnrollmentString());
        assertEquals(sortedList.get(2).toEnrollmentString(), unsortedList.get(2).toEnrollmentString());
        assertEquals(sortedList.get(3).toEnrollmentString(), unsortedList.get(1).toEnrollmentString());
    }

    @Test
    public void testSortByTeam() {
        List<StudentAttributes> sortedList = generateTypicalStudentAttributesList();
        StudentAttributes.sortByTeamName(sortedList);
        List<StudentAttributes> unsortedList = generateTypicalStudentAttributesList();
        assertEquals(sortedList.get(0).toEnrollmentString(),
                     unsortedList.get(2).toEnrollmentString());
        assertEquals(sortedList.get(1).toEnrollmentString(),
                     unsortedList.get(0).toEnrollmentString());
        assertEquals(sortedList.get(2).toEnrollmentString(),
                     unsortedList.get(1).toEnrollmentString());
        assertEquals(sortedList.get(3).toEnrollmentString(),
                     unsortedList.get(3).toEnrollmentString());
    }

    @Test
    public void testSortBySection() {
        List<StudentAttributes> sortedList = generateTypicalStudentAttributesList();
        StudentAttributes.sortBySectionName(sortedList);
        List<StudentAttributes> unsortedList = generateTypicalStudentAttributesList();
        assertEquals(sortedList.get(0).toEnrollmentString(),
                     unsortedList.get(3).toEnrollmentString());
        assertEquals(sortedList.get(1).toEnrollmentString(),
                     unsortedList.get(0).toEnrollmentString());
        assertEquals(sortedList.get(2).toEnrollmentString(),
                     unsortedList.get(1).toEnrollmentString());
        assertEquals(sortedList.get(3).toEnrollmentString(),
                     unsortedList.get(2).toEnrollmentString());
    }

    @Test
    public void testIsRegistered() {
        StudentAttributes sd = StudentAttributes
                .builder("course1", "name 1", "email@email.com")
                .withSection("sect 1").withComments("comment 1").withTeam("team 1")
                .build();

        // Id is not given yet
        assertFalse(sd.isRegistered());

        // Id empty
        sd.googleId = "";
        assertFalse(sd.isRegistered());

        // Id given
        sd.googleId = "googleId.1";
        assertTrue(sd.isRegistered());
    }

    @Test
    public void testToString() {
        StudentAttributes sd = StudentAttributes
                .builder("course1", "name 1", "email@email.com")
                .withSection("sect 1").withComments("comment 1").withTeam("team 1")
                .build();

        assertEquals("Student:name 1[email@email.com]" + System.lineSeparator(), sd.toString());
        assertEquals("    Student:name 1[email@email.com]" + System.lineSeparator(), sd.toString(4));
    }

    @Test
    public void testToEnrollmentString() {
        StudentAttributes sd = StudentAttributes
                .builder("course1", "name 1", "email@email.com")
                .withSection("sect 1").withComments("comment 1").withTeam("team 1")
                .build();

        assertEquals("sect 1|team 1|name 1|email@email.com|comment 1", sd.toEnrollmentString());
    }

    @Test
    public void testGetRegistrationLink() {
        StudentAttributes sd = StudentAttributes
                .builder("course1", "name 1", "email@email.com")
                .withSection("sect 1").withComments("comment 1").withTeam("team 1")
                .build();

        sd.key = "testkey";
        String regUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                                .withRegistrationKey(StringHelper.encrypt("testkey"))
                                .withStudentEmail("email@email.com")
                                .withCourseId("course1")
                                .toString();
        assertEquals(regUrl, sd.getRegistrationUrl());
    }

    @Test
    public void testGetPublicProfilePictureUrl() {
        StudentAttributes studentAttributes = StudentAttributes
                .builder("course1", "name 1", "email@email.com")
                .withSection("sect 1").withComments("comment 1").withTeam("team 1")
                .build();
        String profilePicUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                                       .withStudentEmail(StringHelper.encrypt("email@email.com"))
                                       .withCourseId(StringHelper.encrypt("course1"))
                                       .toString();
        assertEquals(profilePicUrl, studentAttributes.getPublicProfilePictureUrl());
    }

    @Test
    public void testGetJsonString() {
        StudentAttributes studentAttributes = StudentAttributes
                .builder("course1", "name 1", "email@email.com")
                .withSection("sect 1").withComments("comment 1").withTeam("team 1")
                .build();

        assertEquals("{\n  \"email\": \"email@email.com\",\n  \"course\": \"course1\",\n  \"name\": \"name 1\","
                     + "\n  \"googleId\": \"\",\n  \"lastName\": \"1\","
                     + "\n  \"comments\": \"comment 1\",\n  \"team\": \"team 1\","
                     + "\n  \"section\": \"sect 1\"\n}", studentAttributes.getJsonString());
    }

    private CourseStudent generateTypicalStudentObject() {
        return new CourseStudent("email@email.com", "name 1", "googleId.1", "comment 1", "courseId1", "team 1", "sect 1");
    }

    private CourseStudent generateStudentWithoutSectionObject() {
        return new CourseStudent("email@email.com", "name 1", "googleId.1", "comment 1", "courseId1", "team 1", null);
    }

    private List<StudentAttributes> generateTypicalStudentAttributesList() {
        StudentAttributes studentAttributes1 = StudentAttributes
                .builder("courseId", "name 1", "email 1")
                .withSection("sect 2").withComments("comment 1").withTeam("team 2")
                .build();
        StudentAttributes studentAttributes2 = StudentAttributes
                .builder("courseId", "name 2", "email 2")
                .withSection("sect 1").withComments("comment 2").withTeam("team 3")
                .build();
        StudentAttributes studentAttributes3 = StudentAttributes
                .builder("courseId", "name 2", "email 3")
                .withSection("sect 3").withComments("comment 3").withTeam("team 1")
                .build();
        StudentAttributes studentAttributes4 = StudentAttributes
                .builder("courseId", "name 4", "email 4")
                .withSection("sect 2").withComments("comment 4").withTeam("team 2")
                .build();

        return Arrays.asList(studentAttributes1, studentAttributes4, studentAttributes3, studentAttributes2);
    }

    private void verifyStudentContent(CourseStudent expected, CourseStudent actual) {
        assertEquals(expected.getTeamName(), actual.getTeamName());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getComments(), actual.getComments());
    }

    private void verifyStudentContentIncludingId(CourseStudent expected, CourseStudent actual) {
        verifyStudentContent(expected, actual);
        assertEquals(expected.getGoogleId(), actual.getGoogleId());
    }

    private StudentAttributes generateValidStudentAttributesObject() {
        return StudentAttributes.builder("valid-course-id", "valid name", "valid@email.com")
                .withGoogleId("valid.google.id").withTeam("valid team").withSection("valid section")
                .withComments("").build();
    }

}
