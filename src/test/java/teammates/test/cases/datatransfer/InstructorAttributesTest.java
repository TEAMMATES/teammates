package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Instructor;

/**
 * SUT: {@link InstructorAttributes}.
 */
public class InstructorAttributesTest extends BaseAttributesTest {

    private static final String DEFAULT_ROLE_NAME = Const.InstructorPermissionRoleNames
            .INSTRUCTOR_PERMISSION_ROLE_COOWNER;
    private static final String DEFAULT_DISPLAYED_NAME = InstructorAttributes.DEFAULT_DISPLAY_NAME;
    private static final InstructorPrivileges DEFAULT_PRIVILEGES =
            new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

    @Test
    public void testBuilderWithDefaultOptionalValues() {
        InstructorAttributes instructor = InstructorAttributes
                .builder("valid.google.id", "valid-course-id", "valid name", "valid@email.com")
                .build();

        // Check default values for optional params
        assertEquals(DEFAULT_ROLE_NAME, instructor.role);
        assertEquals(DEFAULT_DISPLAYED_NAME, instructor.displayedName);
        assertEquals(DEFAULT_PRIVILEGES, instructor.privileges);
        assertFalse(instructor.isArchived);
        assertTrue(instructor.isDisplayedToStudents);
    }

    @Test
    public void testBuilderWithNullArguments() {
        InstructorAttributes instructorWithNullValues = InstructorAttributes
                .builder(null, null, null, null)
                .withRole(null)
                .withDisplayedName(null)
                .withIsArchived(null)
                .withPrivileges((InstructorPrivileges) null)
                .build();
        // No default values for required params
        assertNull(instructorWithNullValues.googleId);
        assertNull(instructorWithNullValues.courseId);
        assertNull(instructorWithNullValues.name);
        assertNull(instructorWithNullValues.email);

        // Check default values for optional params
        assertEquals(DEFAULT_ROLE_NAME, instructorWithNullValues.role);
        assertEquals(DEFAULT_DISPLAYED_NAME, instructorWithNullValues.displayedName);
        assertEquals(DEFAULT_PRIVILEGES, instructorWithNullValues.privileges);
        assertFalse(instructorWithNullValues.isArchived);
    }

    @Test
    public void testBuilderCopy() {
        InstructorAttributes instructor = InstructorAttributes
                .builder("valid.google.id", "valid-course-id", "valid name", "valid@email.com")
                .withDisplayedName("Original instructor")
                .build();

        InstructorAttributes instructorCopy = InstructorAttributes
                .builder(instructor.googleId, instructor.courseId, instructor.name, instructor.email)
                .withDisplayedName(instructor.displayedName)
                .build();

        assertEquals(instructor.googleId, instructorCopy.googleId);
        assertEquals(instructor.courseId, instructorCopy.courseId);
        assertEquals(instructor.name, instructorCopy.name);
        assertEquals(instructor.email, instructorCopy.email);
        assertEquals(instructor.displayedName, instructorCopy.displayedName);
        assertEquals(instructor.role, instructorCopy.role);
        assertEquals(instructor.privileges, instructorCopy.privileges);
        assertEquals(instructor.key, instructorCopy.key);
        assertEquals(instructor.isArchived, instructorCopy.isArchived);
        assertEquals(instructor.isDisplayedToStudents, instructorCopy.isDisplayedToStudents);

    }

    @Test
    public void testValueOf() {
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instructor = InstructorAttributes
                .builder("valid.google.id", "valid-course-id", "valid name", "valid@email.com")
                .withDisplayedName(InstructorAttributes.DEFAULT_DISPLAY_NAME)
                .withRole(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER)
                .withPrivileges(privileges)
                .build();
        Instructor entity = instructor.toEntity();
        InstructorAttributes instructor1 = InstructorAttributes.valueOf(entity);

        assertEquals(instructor.googleId, instructor1.googleId);
        assertEquals(instructor.courseId, instructor1.courseId);
        assertEquals(instructor.name, instructor1.name);
        assertEquals(instructor.email, instructor1.email);
        assertEquals(instructor.role, instructor1.role);
        assertEquals(instructor.displayedName, instructor1.displayedName);
        assertEquals(instructor.privileges, instructor1.privileges);

        entity.setRole(null);
        entity.setDisplayedName(null);
        entity.setInstructorPrivilegeAsText(null);

        InstructorAttributes instructor2 = InstructorAttributes.valueOf(entity);
        assertEquals(instructor.googleId, instructor2.googleId);
        assertEquals(instructor.courseId, instructor2.courseId);
        assertEquals(instructor.name, instructor2.name);
        assertEquals(instructor.email, instructor2.email);
        // default values for these
        assertEquals(instructor.role, instructor2.role);
        assertEquals(instructor.displayedName, instructor2.displayedName);
        assertEquals(instructor.privileges, instructor2.privileges);
    }

    @Test
    public void testIsRegistered() {
        @SuppressWarnings("deprecation")
        InstructorAttributes instructor = InstructorAttributes
                .builder("valid.google.id", "valid-course-id", "valid name", "valid@email.com")
                .build();

        assertTrue(instructor.isRegistered());

        instructor.googleId = null;
        assertFalse(instructor.isRegistered());
    }

    @Override
    @Test
    public void testToEntity() {
        String googleId = "valid.googleId";
        String courseId = "courseId";
        String name = "name";
        String email = "email@google.com";
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instructor = InstructorAttributes.builder(googleId, courseId, name, email)
                .withRole(roleName)
                .withDisplayedName(displayedName).withPrivileges(privileges)
                .build();
        String key = "randomKey";
        instructor.key = key;

        Instructor entity = instructor.toEntity();
        assertEquals(key, entity.getRegistrationKey());
    }

    @Test
    public void testGetInvalidityInfo() throws Exception {

        @SuppressWarnings("deprecation")
        InstructorAttributes i = InstructorAttributes
                .builder("valid.google.id", "valid-course-id", "valid name", "valid@email.com")
                .build();

        assertTrue(i.isValid());

        i.googleId = "invalid@google@id";
        i.name = "";
        i.email = "invalid email";
        i.courseId = "";
        i.role = "invalidRole";

        assertFalse("invalid value", i.isValid());
        String errorMessage =
                getPopulatedErrorMessage(
                    FieldValidator.GOOGLE_ID_ERROR_MESSAGE, i.googleId,
                    FieldValidator.GOOGLE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                    FieldValidator.GOOGLE_ID_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedErrorMessage(
                      FieldValidator.EMAIL_ERROR_MESSAGE, i.email,
                      FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                      FieldValidator.EMAIL_MAX_LENGTH) + System.lineSeparator()
                + String.format(FieldValidator.ROLE_ERROR_MESSAGE, i.role);
        assertEquals("invalid value", errorMessage, StringHelper.toString(i.getInvalidityInfo()));

        i.googleId = null;

        assertFalse("invalid value", i.isValid());
        errorMessage =
                getPopulatedEmptyStringErrorMessage(
                    FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedErrorMessage(
                      FieldValidator.EMAIL_ERROR_MESSAGE, i.email,
                      FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                      FieldValidator.EMAIL_MAX_LENGTH) + System.lineSeparator()
                + String.format(FieldValidator.ROLE_ERROR_MESSAGE, i.role);
        assertEquals("invalid value", errorMessage, StringHelper.toString(i.getInvalidityInfo()));
    }

    @Test
    public void testSanitizeForSaving() {
        String googleId = "valid.googleId";
        String courseId = "courseId";
        String name = "name";
        String email = "email@google.com";
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instructor = InstructorAttributes.builder(googleId, courseId, name, email)
                .withRole(roleName)
                .withDisplayedName(displayedName).withPrivileges(privileges)
                .build();

        instructor.sanitizeForSaving();
        assertEquals(privileges, instructor.privileges);

        instructor.role = null;
        instructor.displayedName = null;
        instructor.privileges = null;
        instructor.sanitizeForSaving();
        assertEquals(privileges, instructor.privileges);
    }

    @Test
    public void testIsAllowedForPrivilege() {
        String googleId = "valid.googleId";
        String courseId = "courseId";
        String name = "name";
        String email = "email@google.com";
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        InstructorAttributes instructor = InstructorAttributes.builder(googleId, courseId, name, email)
                .withRole(roleName)
                .withDisplayedName(displayedName).withPrivileges(privileges)
                .build();

        assertFalse(instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        instructor.privileges = null;
        assertTrue(instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));

        String sectionId = "sectionId";
        String sessionId = "sessionId";
        assertTrue(instructor.isAllowedForPrivilege(sectionId, sessionId,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        instructor.privileges = null;
        assertTrue(instructor.isAllowedForPrivilege(sectionId, sessionId,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    public void testIsEqualToAnotherInstructor() {
        String googleId = "valid.googleId";
        String courseId = "courseId";
        String name = "name";
        String email = "email@google.com";
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        InstructorAttributes instructor = InstructorAttributes.builder(googleId, courseId, name, email)
                .withRole(roleName)
                .withDisplayedName(displayedName).withPrivileges(privileges)
                .build();
        InstructorPrivileges privileges2 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        InstructorAttributes instructor2 = InstructorAttributes.builder(googleId, courseId, name, email)
                .withRole(roleName)
                .withDisplayedName(displayedName)
                .withPrivileges(privileges2)
                .build();

        assertTrue(instructor.isEqualToAnotherInstructor(instructor2));
        instructor2.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, true);
        assertFalse(instructor.isEqualToAnotherInstructor(instructor2));
        instructor2.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        assertTrue(instructor.isEqualToAnotherInstructor(instructor2));
        // TODO: find ways to test this method more thoroughly
    }

}
