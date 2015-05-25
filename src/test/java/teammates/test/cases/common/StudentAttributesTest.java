package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.util.Const.EOL;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_EMPTY;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;
import static teammates.common.util.FieldValidator.REASON_TOO_LONG;
import static teammates.common.util.FieldValidator.STUDENT_ROLE_COMMENTS_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.SECTION_NAME_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.TEAM_NAME_ERROR_MESSAGE;

import java.util.List;
import java.util.Vector;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.storage.entity.Student;
import teammates.test.cases.BaseTestCase;

public class StudentAttributesTest extends BaseTestCase {

    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testUpdateStatusEnum() {
        assertEquals(UpdateStatus.ERROR, UpdateStatus.enumRepresentation(0));
        assertEquals(UpdateStatus.NEW, UpdateStatus.enumRepresentation(1));
        assertEquals(UpdateStatus.MODIFIED, UpdateStatus.enumRepresentation(2));
        assertEquals(UpdateStatus.UNMODIFIED, UpdateStatus.enumRepresentation(3));
        assertEquals(UpdateStatus.NOT_IN_ENROLL_LIST, UpdateStatus.enumRepresentation(4));
        assertEquals(UpdateStatus.UNKNOWN, UpdateStatus.enumRepresentation(5));
        assertEquals(UpdateStatus.UNKNOWN, UpdateStatus.enumRepresentation(-1));
    }

    @Test
    public void testStudentConstructor() throws TeammatesException {
        String courseId = "anyCoursId";
        StudentAttributes invalidStudent;

        Student expected;
        StudentAttributes studentUnderTest;

        ______TS("Typical case: contains white space");
        expected = generateTypicalStudentObject();
        studentUnderTest = new StudentAttributes("  sect 1 ", "  team 1   ", "   name 1   ",
                                                 "   email@email.com  ", "  comment 1  ", "courseId1");
        verifyStudentContent(expected, studentUnderTest.toEntity());

        ______TS("Typical case: contains google id");
        expected = generateTypicalStudentObject();
        studentUnderTest = new StudentAttributes("googleId.1", "email@email.com", "name 1", "comment 1",
                                                 "courseId1", "team 1", "section 1");
        verifyStudentContentIncludingID(expected, studentUnderTest.toEntity());

        ______TS("Typical case: initialize from entity");
        expected = generateTypicalStudentObject();
        studentUnderTest = new StudentAttributes(expected);
        verifyStudentContentIncludingID(expected, studentUnderTest.toEntity());

        ______TS("Failure case: empty course id");
        invalidStudent = new StudentAttributes("section", "team", "name", "e@e.com", "c", "");
        assertFalse(invalidStudent.isValid());
        assertEquals(String.format(COURSE_ID_ERROR_MESSAGE, invalidStudent.course, REASON_EMPTY),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: invalid course id");
        invalidStudent = new StudentAttributes("section", "team", "name", "e@e.com", "c", "Course Id with space");
        assertFalse(invalidStudent.isValid());
        assertEquals(String.format(COURSE_ID_ERROR_MESSAGE, invalidStudent.course, REASON_INCORRECT_FORMAT),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: empty name");
        invalidStudent = new StudentAttributes("sect", "t1", "", "e@e.com",
                                               "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(invalidStudent.getInvalidityInfo().get(0),
                     String.format(FieldValidator.PERSON_NAME_ERROR_MESSAGE, "", FieldValidator.REASON_EMPTY));

        ______TS("Failure case: empty email");
        invalidStudent = new StudentAttributes("sect", "t1", "n", "", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(String.format(EMAIL_ERROR_MESSAGE, "", REASON_EMPTY), 
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: section name too long");
        String longSectionName = StringHelper
                .generateStringOfLength(FieldValidator.SECTION_NAME_MAX_LENGTH + 1);
        invalidStudent = new StudentAttributes(longSectionName, "t1", "n", "e@e.com", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(String.format(SECTION_NAME_ERROR_MESSAGE, longSectionName, REASON_TOO_LONG),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: team name too long");
        String longTeamName = StringHelper.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
        invalidStudent = new StudentAttributes("sect", longTeamName, "name", "e@e.com", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(String.format(TEAM_NAME_ERROR_MESSAGE, longTeamName, REASON_TOO_LONG),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: student name too long");
        String longStudentName = StringHelper
                .generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        invalidStudent = new StudentAttributes("sect", "t1", longStudentName, "e@e.com", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(String.format(FieldValidator.PERSON_NAME_ERROR_MESSAGE, longStudentName,
                                   FieldValidator.REASON_TOO_LONG),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: invalid email");
        invalidStudent = new StudentAttributes("sect", "t1", "name", "ee.com", "c", courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(String.format(EMAIL_ERROR_MESSAGE, "ee.com", REASON_INCORRECT_FORMAT),
                     invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: comment too long");
        String longComment = StringHelper
                .generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH + 1);
        invalidStudent = new StudentAttributes("sect", "t1", "name", "e@e.com", longComment, courseId);
        assertFalse(invalidStudent.isValid());
        assertEquals(String.format(STUDENT_ROLE_COMMENTS_ERROR_MESSAGE, longComment, REASON_TOO_LONG),
                     invalidStudent.getInvalidityInfo().get(0));

        // Other invalid parameters cases are omitted because they are already
        // unit-tested in validate*() methods in Common.java
    }

    @Test
    public void testValidate() {
        StudentAttributes s = generateValidStudentAttributesObject();

        assertTrue("valid value", s.isValid());

        s.googleId = "invalid@google@id";
        s.name = "";
        s.email = "invalid email";
        s.course = "";
        s.comments = StringHelper.generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH + 1);
        s.team = StringHelper.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);

        assertFalse("invalid value", s.isValid());
        String errorMessage = "\"invalid@google@id\" is not acceptable to TEAMMATES as a Google ID because it is not in the correct format. A Google ID must be a valid id already registered with Google. It cannot be longer than 45 characters. It cannot be empty." + EOL
                            + "\"\" is not acceptable to TEAMMATES as a Course ID because it is empty. A Course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. It cannot be longer than 40 characters. It cannot be empty or contain spaces." + EOL
                            + "\"invalid email\" is not acceptable to TEAMMATES as an email because it is not in the correct format. An email address contains some text followed by one '@' sign followed by some more text. It cannot be longer than 45 characters. It cannot be empty and it cannot have spaces." + EOL
                            + "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES as a team name because it is too long. The value of a team name should be no longer than 60 characters. It should not be empty." + EOL
                            + "\"" + s.comments
                            + "\" is not acceptable to TEAMMATES as comments about a student enrolled in a course because it is too long. The value of comments about a student enrolled in a course should be no longer than 500 characters." + EOL
                            + "\"\" is not acceptable to TEAMMATES as a person name because it is empty. The value of a person name should be no longer than 100 characters. It should not be empty.";
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
    public void testIsRegistered() throws Exception {
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
        String regUrl = new Url(Config.APP_URL + Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
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
        String profilePicUrl = new Url(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                                       .withStudentEmail(StringHelper.encrypt("email@email.com"))
                                       .withCourseId(StringHelper.encrypt("course1"))
                                       .toString();
        assertEquals(profilePicUrl, sd.getPublicProfilePictureUrl());
    }

    @Test
    public void testGetJsonString() {
        StudentAttributes sd = new StudentAttributes("sect 1", "team 1", "name 1", "email@email.com",
                                        "comment 1", "course1");
        assertEquals("{\n  \"name\": \"name 1\",\n  \"lastName\": \"1\",\n  \"email\": \"email@email.com\","
                     + "\n  \"course\": \"course1\",\n  \"comments\": \"comment 1\",\n  \"team\": \"team 1\","
                     + "\n  \"section\": \"sect 1\",\n  \"updateStatus\": \"UNKNOWN\"\n}",
                     sd.getJsonString());
    }

    private Student generateTypicalStudentObject() {
        return new Student("email@email.com", "name 1", "googleId.1", "comment 1", "courseId1", "team 1", "sect 1");
    }

    private Student generateStudentWithoutSectionObject() {
        return new Student("email@email.com", "name 1", "googleId.1", "comment 1", "courseId1", "team 1", null);
    }

    private List<StudentAttributes> generateTypicalStudentAttributesList() {
        List<StudentAttributes> list = new Vector<>();
        list.add(new StudentAttributes("sect 2", "team 2", "name 1", "email 1", "comment 1", "courseId"));
        list.add(new StudentAttributes("sect 2", "team 2", "name 4", "email 4", "comment 4", "courseId"));
        list.add(new StudentAttributes("sect 3", "team 1", "name 2", "email 3", "comment 3", "courseId"));
        list.add(new StudentAttributes("sect 1", "team 3", "name 2", "email 2", "comment 2", "courseId"));
        return list;
    }

    private void verifyStudentContent(Student expected, Student actual) {
        assertEquals(expected.getTeamName(), actual.getTeamName());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getComments(), actual.getComments());
    }

    private void verifyStudentContentIncludingID(Student expected, Student actual) {
        verifyStudentContent(expected, actual);
        assertEquals(expected.getGoogleId(), actual.getGoogleId());
    }

    private StudentAttributes generateValidStudentAttributesObject() {
        StudentAttributes s;
        s = new StudentAttributes();
        s.googleId = "valid.google.id";
        s.name = "valid name";
        s.email = "valid@email.com";
        s.course = "valid-course-id";
        s.comments = "";
        s.team = "valid team";
        s.section = "valid section";
        return s;
    }

    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }

}
