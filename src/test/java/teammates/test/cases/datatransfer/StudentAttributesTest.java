package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.CourseStudent;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link StudentAttributes}.
 */
public class StudentAttributesTest extends BaseTestCase {

    private static class StudentAttributesWithModifiableTimestamp extends StudentAttributes {

        void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

    @Test
    public void testDefaultTimestamp() {

        StudentAttributesWithModifiableTimestamp s = new StudentAttributesWithModifiableTimestamp();

        s.setCreatedAt(null);
        s.setUpdatedAt(null);

        Date defaultStudentCreationTimeStamp = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;

        ______TS("success : defaultTimeStamp for createdAt date");

        assertEquals(defaultStudentCreationTimeStamp, s.getCreatedAt());

        ______TS("success : defaultTimeStamp for updatedAt date");

        assertEquals(defaultStudentCreationTimeStamp, s.getUpdatedAt());
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
    public void testStudentConstructor() throws Exception {
        String courseId = "anyCoursId";
        StudentAttributes invalidStudent;

        CourseStudent expected;
        StudentAttributes studentUnderTest;

        ______TS("Typical case: contains white space");
        expected = generateTypicalStudentObject();
        studentUnderTest = new StudentAttributes("  sect 1 ", "  team 1   ", "   name 1   ",
                                                 "   email@email.com  ", "  comment 1  ", "courseId1");
        verifyStudentContent(expected, (CourseStudent) studentUnderTest.toEntity());

        ______TS("Typical case: contains google id");
        expected = generateTypicalStudentObject();
        studentUnderTest = new StudentAttributes("googleId.1", "email@email.com", "name 1", "comment 1",
                                                 "courseId1", "team 1", "section 1");

        verifyStudentContentIncludingId(expected, (CourseStudent) studentUnderTest.toEntity());

        ______TS("Typical case: initialize from entity");
        expected = generateTypicalStudentObject();
        studentUnderTest = new StudentAttributes(expected);

        verifyStudentContentIncludingId(expected, (CourseStudent) studentUnderTest.toEntity());

        ______TS("Failure case: empty course id");
        invalidStudent = new StudentAttributes("section", "team", "name", "e@e.com", "c", "");
        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedEmptyStringErrorMessage(
                         FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                         FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: invalid course id");
        invalidStudent = new StudentAttributes("section", "team", "name", "e@e.com", "c", "Course Id with space");
        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.COURSE_ID_ERROR_MESSAGE, invalidStudent.course,
                         FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                         FieldValidator.COURSE_ID_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: empty name");
        invalidStudent = new StudentAttributes("sect", "t1", "", "e@e.com",
                                               "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(invalidStudent.getInvalidityInfo().get(0),
                     getPopulatedEmptyStringErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                         FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH));

        ______TS("Failure case: empty email");
        invalidStudent = new StudentAttributes("sect", "t1", "n", "", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedEmptyStringErrorMessage(
                         FieldValidator.EMAIL_ERROR_MESSAGE_EMPTY_STRING,
                         FieldValidator.EMAIL_FIELD_NAME, FieldValidator.EMAIL_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: section name too long");
        String longSectionName = StringHelperExtension
                .generateStringOfLength(FieldValidator.SECTION_NAME_MAX_LENGTH + 1);
        invalidStudent = new StudentAttributes(longSectionName, "t1", "n", "e@e.com", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longSectionName,
                         FieldValidator.SECTION_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                         FieldValidator.SECTION_NAME_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: team name too long");
        String longTeamName = StringHelperExtension.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
        invalidStudent = new StudentAttributes("sect", longTeamName, "name", "e@e.com", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longTeamName,
                         FieldValidator.TEAM_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                         FieldValidator.TEAM_NAME_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: student name too long");
        String longStudentName = StringHelperExtension
                .generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        invalidStudent = new StudentAttributes("sect", "t1", longStudentName, "e@e.com", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longStudentName,
                         FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                         FieldValidator.PERSON_NAME_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: invalid email");
        invalidStudent = new StudentAttributes("sect", "t1", "name", "ee.com", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                         FieldValidator.EMAIL_ERROR_MESSAGE, "ee.com",
                         FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                         FieldValidator.EMAIL_MAX_LENGTH),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: comment too long");
        String longComment = StringHelperExtension
                .generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH + 1);
        invalidStudent = new StudentAttributes("sect", "t1", "name", "e@e.com", longComment, courseId);
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
                    FieldValidator.GOOGLE_ID_MAX_LENGTH) + Const.EOL
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH) + Const.EOL
                + getPopulatedErrorMessage(
                      FieldValidator.EMAIL_ERROR_MESSAGE, "invalid email",
                      FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                      FieldValidator.EMAIL_MAX_LENGTH) + Const.EOL
                + getPopulatedErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                      "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                      FieldValidator.TEAM_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                      FieldValidator.TEAM_NAME_MAX_LENGTH) + Const.EOL
                + getPopulatedErrorMessage(
                      FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, s.comments,
                      FieldValidator.STUDENT_ROLE_COMMENTS_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                      FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH) + Const.EOL
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH);
        assertEquals("invalid value", errorMessage, StringHelper.toString(s.getInvalidityInfo()));
    }

    @Test
    public void testIsEnrollInfoSameAs() {
        StudentAttributes student = new StudentAttributes(generateTypicalStudentObject());
        StudentAttributes other = new StudentAttributes(generateTypicalStudentObject());

        ______TS("Typical case: Same enroll info");
        assertTrue(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Compare to null");
        assertFalse(student.isEnrollInfoSameAs(null));

        ______TS("Typical case: Different in email");
        other.email = "other@email.com";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in name");
        other = new StudentAttributes(generateTypicalStudentObject());
        other.name = "otherName";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in course id");
        other = new StudentAttributes(generateTypicalStudentObject());
        other.course = "otherCourse";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in comment");
        other = new StudentAttributes(generateTypicalStudentObject());
        other.comments = "otherComments";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in team");
        other = new StudentAttributes(generateTypicalStudentObject());
        other.team = "otherTeam";
        assertFalse(student.isEnrollInfoSameAs(other));

        ______TS("Typical case: Different in section");
        other = new StudentAttributes(generateStudentWithoutSectionObject());
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
        StudentAttributes sd = new StudentAttributes("sect 1", "team 1", "name 1", "email@email.com",
                                                     "comment 1", "course1");

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
        StudentAttributes sd = new StudentAttributes("sect 1", "team 1", "name 1", "email@email.com",
                                                     "comment 1", "course1");
        assertEquals("Student:name 1[email@email.com]" + Const.EOL, sd.toString());
        assertEquals("    Student:name 1[email@email.com]" + Const.EOL, sd.toString(4));
    }

    @Test
    public void testToEnrollmentString() {
        StudentAttributes sd = new StudentAttributes("sect 1", "team 1", "name 1", "email@email.com",
                                                     "comment 1", "course1");
        assertEquals("sect 1|team 1|name 1|email@email.com|comment 1", sd.toEnrollmentString());
    }

    @Test
    public void testGetRegistrationLink() {
        StudentAttributes sd = new StudentAttributes("sect 1", "team 1", "name 1", "email@email.com",
                                                     "comment 1", "course1");
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
        StudentAttributes sd = new StudentAttributes("sect 1", "team 1", "name 1", "email@email.com",
                                                     "comment 1", "course1");
        String profilePicUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                                       .withStudentEmail(StringHelper.encrypt("email@email.com"))
                                       .withCourseId(StringHelper.encrypt("course1"))
                                       .toString();
        assertEquals(profilePicUrl, sd.getPublicProfilePictureUrl());
    }

    @Test
    public void testGetJsonString() {
        StudentAttributes sd = new StudentAttributes("sect 1", "team 1", "name 1", "email@email.com",
                                        "comment 1", "course1");
        assertEquals("{\n  \"email\": \"email@email.com\",\n  \"course\": \"course1\",\n  \"name\": \"name 1\","
                     + "\n  \"lastName\": \"1\",\n  \"comments\": \"comment 1\",\n  \"team\": \"team 1\","
                     + "\n  \"section\": \"sect 1\"\n}",
                     sd.getJsonString());
    }

    private CourseStudent generateTypicalStudentObject() {
        return new CourseStudent("email@email.com", "name 1", "googleId.1", "comment 1", "courseId1", "team 1", "sect 1");
    }

    private CourseStudent generateStudentWithoutSectionObject() {
        return new CourseStudent("email@email.com", "name 1", "googleId.1", "comment 1", "courseId1", "team 1", null);
    }

    private List<StudentAttributes> generateTypicalStudentAttributesList() {
        List<StudentAttributes> list = new ArrayList<>();
        list.add(new StudentAttributes("sect 2", "team 2", "name 1", "email 1", "comment 1", "courseId"));
        list.add(new StudentAttributes("sect 2", "team 2", "name 4", "email 4", "comment 4", "courseId"));
        list.add(new StudentAttributes("sect 3", "team 1", "name 2", "email 3", "comment 3", "courseId"));
        list.add(new StudentAttributes("sect 1", "team 3", "name 2", "email 2", "comment 2", "courseId"));
        return list;
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
        StudentAttributes s = new StudentAttributes();
        s.googleId = "valid.google.id";
        s.name = "valid name";
        s.email = "valid@email.com";
        s.course = "valid-course-id";
        s.comments = "";
        s.team = "valid team";
        s.section = "valid section";
        return s;
    }

}
