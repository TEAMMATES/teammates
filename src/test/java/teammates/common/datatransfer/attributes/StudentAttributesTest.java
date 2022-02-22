package teammates.common.datatransfer.attributes;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.StringHelperExtension;
import teammates.storage.entity.CourseStudent;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StudentAttributes}.
 */
public class StudentAttributesTest extends BaseTestCase {

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        StudentAttributes student = StudentAttributes
                .builder("courseId", "e@e.com")
                .build();

        assertEquals("courseId", student.getCourse());
        assertEquals("e@e.com", student.getEmail());

        assertNull(student.getName());
        assertEquals("", student.getGoogleId());
        assertNull(student.getTeam());
        assertEquals(Const.DEFAULT_SECTION, student.getSection());
        assertNull(student.getComments());
        assertNull(student.getKey());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, student.getCreatedAt());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, student.getUpdatedAt());
    }

    @Test
    public void testBuilder_nullValues_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            StudentAttributes
                    .builder(null, "email@email.com")
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentAttributes
                    .builder("course", null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentAttributes
                    .builder("course", "email@email.com")
                    .withName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentAttributes
                    .builder("course", "email@email.com")
                    .withTeamName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentAttributes
                    .builder("course", "email@email.com")
                    .withSectionName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            StudentAttributes
                    .builder("course", "email@email.com")
                    .withComment(null)
                    .build();
        });
    }

    @Test
    public void testGetCopy() {
        CourseStudent student = new CourseStudent("email@email.com", "name 1", "googleId.1",
                "comment 1", "courseId1", "team 1", "sect 1");
        StudentAttributes originalStudent = StudentAttributes.valueOf(student);

        StudentAttributes copyStudent = originalStudent.getCopy();

        assertEquals(originalStudent.getCourse(), copyStudent.getCourse());
        assertEquals(originalStudent.getName(), copyStudent.getName());
        assertEquals(originalStudent.getEmail(), copyStudent.getEmail());
        assertEquals(originalStudent.getGoogleId(), copyStudent.getGoogleId());
        assertEquals(originalStudent.getComments(), copyStudent.getComments());
        assertEquals(originalStudent.getKey(), copyStudent.getKey());
        assertEquals(originalStudent.getSection(), copyStudent.getSection());
        assertEquals(originalStudent.getTeam(), copyStudent.getTeam());
        assertEquals(originalStudent.getCreatedAt(), copyStudent.getCreatedAt());
        assertEquals(originalStudent.getUpdatedAt(), copyStudent.getUpdatedAt());
    }

    @Test
    public void testValueOf_withAllFieldPopulatedCourseStudent_shouldGenerateAttributesCorrectly() {
        CourseStudent originalStudent = new CourseStudent("email@email.com", "name 1", "googleId.1",
                "comment 1", "courseId1", "team 1", "sect 1");
        StudentAttributes copyStudent = StudentAttributes.valueOf(originalStudent);

        assertEquals(originalStudent.getCourseId(), copyStudent.getCourse());
        assertEquals(originalStudent.getName(), copyStudent.getName());
        assertEquals(originalStudent.getEmail(), copyStudent.getEmail());
        assertEquals(originalStudent.getGoogleId(), copyStudent.getGoogleId());
        assertEquals(originalStudent.getComments(), copyStudent.getComments());
        assertEquals(originalStudent.getRegistrationKey(), copyStudent.getKey());
        assertEquals(originalStudent.getSectionName(), copyStudent.getSection());
        assertEquals(originalStudent.getTeamName(), copyStudent.getTeam());
        assertEquals(originalStudent.getCreatedAt(), copyStudent.getCreatedAt());
        assertEquals(originalStudent.getUpdatedAt(), copyStudent.getUpdatedAt());
    }

    @Test
    public void testValueOf_withSomeFieldsPopulatedAsNull_shouldUseDefaultValues() {
        CourseStudent originalStudent = new CourseStudent("email@email.com", "name 1", null,
                "comment 1", "courseId1", "team 1", null);
        originalStudent.setCreatedAt(null);
        originalStudent.setLastUpdate(null);
        StudentAttributes copyStudent = StudentAttributes.valueOf(originalStudent);

        assertEquals(originalStudent.getCourseId(), copyStudent.getCourse());
        assertEquals(originalStudent.getName(), copyStudent.getName());
        assertEquals(originalStudent.getEmail(), copyStudent.getEmail());
        assertEquals("", copyStudent.getGoogleId());
        assertEquals(originalStudent.getComments(), copyStudent.getComments());
        assertEquals(originalStudent.getRegistrationKey(), copyStudent.getKey());
        assertEquals(Const.DEFAULT_SECTION, copyStudent.getSection());
        assertEquals(originalStudent.getTeamName(), copyStudent.getTeam());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, copyStudent.getCreatedAt());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, copyStudent.getUpdatedAt());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildAttributeWithCorrectValue() {
        CourseStudent expected = generateTypicalStudentObject();

        StudentAttributes studentUnderTest = StudentAttributes
                .builder(expected.getCourseId(), expected.getEmail())
                .withName(expected.getName())
                .withComment(expected.getComments())
                .withTeamName(expected.getTeamName())
                .withSectionName(expected.getSectionName())
                .withGoogleId(expected.getGoogleId())
                .build();

        assertEquals(expected.getCourseId(), studentUnderTest.getCourse());
        assertEquals(expected.getName(), studentUnderTest.getName());
        assertEquals(expected.getComments(), studentUnderTest.getComments());
        assertEquals(expected.getSectionName(), studentUnderTest.getSection());
        assertEquals(expected.getTeamName(), studentUnderTest.getTeam());
        assertEquals(expected.getGoogleId(), studentUnderTest.getGoogleId());

        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, studentUnderTest.getCreatedAt());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, studentUnderTest.getUpdatedAt());
    }

    @Test
    public void testValidate() throws Exception {

        ______TS("Typical cases: multiple invalid fields");
        StudentAttributes s = generateValidStudentAttributesObject();

        assertTrue("valid value", s.isValid());

        s.setGoogleId("invalid@google@id");
        s.setName("");
        s.setEmail("invalid email");
        s.setCourse("");
        s.setComments(StringHelperExtension.generateStringOfLength(FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH + 1));
        s.setTeam(StringHelperExtension.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1));

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
                      FieldValidator.SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, s.getComments(),
                      FieldValidator.STUDENT_ROLE_COMMENTS_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                      FieldValidator.STUDENT_ROLE_COMMENTS_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH);
        assertEquals("invalid value", errorMessage, StringHelper.toString(s.getInvalidityInfo()));

        ______TS("Failure case: student name too long");
        String longStudentName = StringHelperExtension
                .generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        StudentAttributes invalidStudent = StudentAttributes
                .builder("courseId", "e@e.com")
                .withName(longStudentName)
                .withSectionName("sect")
                .withComment("c")
                .withTeamName("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longStudentName,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.PERSON_NAME_MAX_LENGTH),
                invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: section name too long");
        String longSectionName = StringHelperExtension
                .generateStringOfLength(FieldValidator.SECTION_NAME_MAX_LENGTH + 1);
        invalidStudent = StudentAttributes
                .builder("courseId", "e@e.com")
                .withName("")
                .withSectionName(longSectionName)
                .withComment("c")
                .withTeamName("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, longSectionName,
                FieldValidator.SECTION_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.SECTION_NAME_MAX_LENGTH),
                invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: empty email");
        invalidStudent = StudentAttributes
                .builder("course", "")
                .withName("n")
                .withSectionName("sect")
                .withComment("c")
                .withTeamName("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedEmptyStringErrorMessage(
                FieldValidator.EMAIL_ERROR_MESSAGE_EMPTY_STRING,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.EMAIL_MAX_LENGTH),
                invalidStudent.getInvalidityInfo().get(0));

        ______TS("Failure case: empty name");
        invalidStudent = StudentAttributes
                .builder("course", "e@e.com")
                .withName("")
                .withSectionName("sect")
                .withComment("c")
                .withTeamName("t1")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(invalidStudent.getInvalidityInfo().get(0),
                getPopulatedEmptyStringErrorMessage(
                        FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                        FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH));

        ______TS("Failure case: invalid course id");
        invalidStudent = StudentAttributes
                .builder("Course Id with space", "e@e.com")
                .withName("name")
                .withSectionName("section")
                .withComment("c")
                .withTeamName("team")
                .build();

        assertFalse(invalidStudent.isValid());
        assertEquals(getPopulatedErrorMessage(
                FieldValidator.COURSE_ID_ERROR_MESSAGE, invalidStudent.getCourse(),
                FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                FieldValidator.COURSE_ID_MAX_LENGTH),
                invalidStudent.getInvalidityInfo().get(0));

    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        StudentAttributes.UpdateOptions updateOptions =
                StudentAttributes.updateOptionsBuilder("courseId", "email@email.com")
                        .withNewEmail("new@email.com")
                        .withName("John Doe")
                        .withComment("Comment")
                        .withGoogleId("googleId")
                        .withTeamName("teamName")
                        .withSectionName("sectionName")
                        .build();

        assertEquals("courseId", updateOptions.getCourseId());
        assertEquals("email@email.com", updateOptions.getEmail());

        StudentAttributes studentAttributes =
                StudentAttributes.builder("course", "alice@gmail.tmt")
                        .withName("Alice")
                        .withComment("Comment B")
                        .withGoogleId("googleIdC")
                        .withTeamName("TEAM B")
                        .withSectionName("Section C")
                        .build();

        studentAttributes.update(updateOptions);

        assertEquals("new@email.com", studentAttributes.getEmail());
        assertEquals("John Doe", studentAttributes.getName());
        assertEquals("Comment", studentAttributes.getComments());
        assertEquals("googleId", studentAttributes.getGoogleId());
        assertEquals("teamName", studentAttributes.getTeam());
        assertEquals("sectionName", studentAttributes.getSection());

    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () ->
                StudentAttributes.updateOptionsBuilder(null, "email@email.com"));
        assertThrows(AssertionError.class, () ->
                StudentAttributes.updateOptionsBuilder("course", null));
        assertThrows(AssertionError.class, () ->
                StudentAttributes.updateOptionsBuilder("course", "email@email.com")
                        .withNewEmail(null));
        assertThrows(AssertionError.class, () ->
                StudentAttributes.updateOptionsBuilder("course", "email@email.com")
                        .withName(null));
        assertThrows(AssertionError.class, () ->
                StudentAttributes.updateOptionsBuilder("course", "email@email.com")
                        .withComment(null));
        assertThrows(AssertionError.class, () ->
                StudentAttributes.updateOptionsBuilder("course", "email@email.com")
                        .withTeamName(null));
        assertThrows(AssertionError.class, () ->
                StudentAttributes.updateOptionsBuilder("course", "email@email.com")
                        .withSectionName(null));
    }

    @Test
    public void testSortByTeam() {
        List<StudentAttributes> sortedList = generateTypicalStudentAttributesList();
        StudentAttributes.sortByTeamName(sortedList);
        List<StudentAttributes> unsortedList = generateTypicalStudentAttributesList();
        assertEquals(sortedList.get(0).toString(),
                     unsortedList.get(2).toString());
        assertEquals(sortedList.get(1).toString(),
                     unsortedList.get(0).toString());
        assertEquals(sortedList.get(2).toString(),
                     unsortedList.get(1).toString());
        assertEquals(sortedList.get(3).toString(),
                     unsortedList.get(3).toString());
    }

    @Test
    public void testSortBySection() {
        List<StudentAttributes> sortedList = generateTypicalStudentAttributesList();
        StudentAttributes.sortBySectionName(sortedList);
        List<StudentAttributes> unsortedList = generateTypicalStudentAttributesList();
        assertEquals(sortedList.get(0).toString(),
                     unsortedList.get(3).toString());
        assertEquals(sortedList.get(1).toString(),
                     unsortedList.get(0).toString());
        assertEquals(sortedList.get(2).toString(),
                     unsortedList.get(1).toString());
        assertEquals(sortedList.get(3).toString(),
                     unsortedList.get(2).toString());
    }

    @Test
    public void testIsRegistered() {
        StudentAttributes sd = StudentAttributes
                .builder("course1", "email@email.com")
                .withName("name 1")
                .withSectionName("sect 1")
                .withComment("comment 1")
                .withTeamName("team 1")
                .build();

        // Id is not given yet
        assertFalse(sd.isRegistered());

        // Id empty
        sd.setGoogleId("");
        assertFalse(sd.isRegistered());

        // Id given
        sd.setGoogleId("googleId.1");
        assertTrue(sd.isRegistered());
    }

    @Test
    public void testToString() {
        StudentAttributes sd = StudentAttributes
                .builder("course1", "email@email.com")
                .withName("name 1")
                .withSectionName("sect 1")
                .withComment("comment 1")
                .withTeamName("team 1")
                .build();

        assertEquals("Student:name 1[email@email.com]", sd.toString());
    }

    @Test
    public void testGetRegistrationLink() {
        StudentAttributes sd = StudentAttributes
                .builder("course1", "email@email.com")
                .withName("name 1")
                .withSectionName("sect 1")
                .withComment("comment 1")
                .withTeamName("team 1")
                .build();

        String key = StringHelper.encrypt("testkey");
        sd.setKey(key);
        String regUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(key)
                .withEntityType(Const.EntityType.STUDENT)
                .toString();
        assertEquals(regUrl, sd.getRegistrationUrl());
    }

    @Test
    public void testEquals() {

        StudentAttributes student = StudentAttributes.valueOf(generateTypicalStudentObject());

        // When the two student objects are the exact same copy
        StudentAttributes studentCopy = student.getCopy();

        assertTrue(student.equals(studentCopy));

        // When the two students have same values but created at different time
        StudentAttributes studentSimilar = StudentAttributes.valueOf(generateTypicalStudentObject());

        assertTrue(student.equals(studentSimilar));

        // When the two students are different
        StudentAttributes studentDifferent = generateValidStudentAttributesObject();

        assertFalse(student.equals(studentDifferent));

        // When the other object is of different class
        assertFalse(student.equals(3));
    }

    @Test
    public void testHashCode() {

        StudentAttributes student = StudentAttributes.valueOf(generateTypicalStudentObject());

        // When the two student objects are the exact same copy, they should have the same hash code
        StudentAttributes studentCopy = student.getCopy();

        assertTrue(student.hashCode() == studentCopy.hashCode());

        // When the two students have same values but created at different time, they should still have
        // the same hash code
        StudentAttributes studentSimilar = StudentAttributes.valueOf(generateTypicalStudentObject());

        assertTrue(student.hashCode() == studentSimilar.hashCode());

        // When the two students are different, they should have different hash code
        StudentAttributes studentDifferent = generateValidStudentAttributesObject();

        assertFalse(student.hashCode() == studentDifferent.hashCode());
    }

    private CourseStudent generateTypicalStudentObject() {
        return new CourseStudent("email@email.com", "name 1", "googleId.1", "comment 1", "courseId1", "team 1", "sect 1");
    }

    private List<StudentAttributes> generateTypicalStudentAttributesList() {
        StudentAttributes studentAttributes1 = StudentAttributes
                .builder("courseId", "email 1")
                .withName("name 1")
                .withSectionName("sect 2")
                .withComment("comment 1")
                .withTeamName("team 2")
                .build();
        StudentAttributes studentAttributes2 = StudentAttributes
                .builder("courseId", "email 2")
                .withName("name 2")
                .withSectionName("sect 1")
                .withComment("comment 2")
                .withTeamName("team 3")
                .build();
        StudentAttributes studentAttributes3 = StudentAttributes
                .builder("courseId", "email 3")
                .withName("name 2")
                .withSectionName("sect 3")
                .withComment("comment 3")
                .withTeamName("team 1")
                .build();
        StudentAttributes studentAttributes4 = StudentAttributes
                .builder("courseId", "email 4")
                .withName("name 4")
                .withSectionName("sect 2")
                .withComment("comment 4")
                .withTeamName("team 2")
                .build();

        return Arrays.asList(studentAttributes1, studentAttributes4, studentAttributes3, studentAttributes2);
    }

    private StudentAttributes generateValidStudentAttributesObject() {
        return StudentAttributes.builder("valid-course-id", "valid@email.com")
                .withName("valid name")
                .withGoogleId("valid.google.id")
                .withTeamName("valid team")
                .withSectionName("valid section")
                .withComment("")
                .build();
    }

}
