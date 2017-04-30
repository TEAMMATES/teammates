package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Instructor;

/**
 * The data transfer class for Instructor entities.
 */
public class InstructorAttributes extends EntityAttributes {

    public static final String DEFAULT_DISPLAY_NAME = "Instructor";

    // Note: be careful when changing these variables as their names are used in *.json files.
    public String googleId;
    public String courseId;
    public String name;
    public String email;
    public Boolean isArchived;
    public String key;
    public String role;
    public boolean isDisplayedToStudents;
    public String displayedName;

    public InstructorPrivileges privileges;

    /**
     * Creates a new instructor with default access level and default displayedName.
     *
     * @deprecated only to be used for testing
     */
    @Deprecated
    public InstructorAttributes(String googleId, String courseId, String name, String email) {
        this(googleId, courseId, name, email,
             Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER, DEFAULT_DISPLAY_NAME,
             new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER));
    }

    /**
     * Creates a new instructor with params specified (isDisplayedToStudent is set to true by default).
     */
    public InstructorAttributes(String googleId, String courseId, String name, String email, String role,
                                String displayedName, String instructorPrivilegesAsText) {
        this.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
        this.courseId = SanitizationHelper.sanitizeTitle(courseId);
        this.isArchived = false;
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = email;
        this.role = SanitizationHelper.sanitizeName(role);
        this.isDisplayedToStudents = true;
        this.displayedName = SanitizationHelper.sanitizeName(displayedName);
        this.privileges = getInstructorPrivilegesFromText(instructorPrivilegesAsText);
    }

    /**
     * Creates an instructor (isDisplayedToStudent is set to true by default).
     */
    public InstructorAttributes(String googleId, String courseId, String name, String email, String role,
                                String displayedName, InstructorPrivileges privileges) {
        this.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
        this.courseId = courseId;
        this.isArchived = false;
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = email;
        this.role = SanitizationHelper.sanitizeName(role);
        this.isDisplayedToStudents = true;
        this.displayedName = SanitizationHelper.sanitizeName(displayedName);
        this.privileges = privileges;
    }

    /**
     * Creates an instructor.
     */
    public InstructorAttributes(String googleId, String courseId, String name, String email, String role,
                                boolean isDisplayedToStudents, String displayName, InstructorPrivileges privileges) {
        this(googleId, courseId, name, email, role, displayName, privileges);
        this.isDisplayedToStudents = isDisplayedToStudents;
        this.isArchived = false;
    }

    public InstructorAttributes(Instructor instructor) {
        this.googleId = instructor.getGoogleId();
        this.courseId = instructor.getCourseId();
        this.isArchived = instructor.getIsArchived() != null && instructor.getIsArchived();
        this.name = instructor.getName();
        this.email = instructor.getEmail();

        if (instructor.getRegistrationKey() == null) {
            instructor.setGeneratedKeyIfNull();
        }

        this.key = instructor.getRegistrationKey();

        if (instructor.getRole() == null) {
            this.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            this.role = instructor.getRole();
        }

        this.isDisplayedToStudents = instructor.isDisplayedToStudents();

        if (instructor.getDisplayedName() == null) {
            this.displayedName = DEFAULT_DISPLAY_NAME;
        } else {
            this.displayedName = instructor.getDisplayedName();
        }

        if (instructor.getInstructorPrivilegesAsText() == null) {
            this.privileges =
                    new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        } else {
            this.privileges = getInstructorPrivilegesFromText(instructor.getInstructorPrivilegesAsText());
        }
    }

    @Deprecated
    public InstructorAttributes() {
        // deprecated
    }

    private InstructorAttributes(InstructorAttributes other) {
        this(other.googleId, other.courseId, other.name, other.email,
             other.role, other.isDisplayedToStudents, other.displayedName, other.privileges);
        this.key = other.key;
        this.isArchived = other.isArchived;
    }

    public InstructorAttributes getCopy() {
        return new InstructorAttributes(this);
    }

    public String getTextFromInstructorPrivileges() {
        return JsonUtils.toJson(privileges, InstructorPrivileges.class);
    }

    private static InstructorPrivileges getInstructorPrivilegesFromText(String instructorPrivilegesAsText) {
        return JsonUtils.fromJson(instructorPrivilegesAsText, InstructorPrivileges.class);
    }

    public String getName() {
        return name;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isDisplayedToStudents() {
        return isDisplayedToStudents;
    }

    public boolean isRegistered() {
        return googleId != null;
    }

    @Override
    public Instructor toEntity() {
        if (key != null) {
            return new Instructor(googleId, courseId, isArchived, name, email, key, role,
                                  isDisplayedToStudents, displayedName, getTextFromInstructorPrivileges());
        }
        return new Instructor(googleId, courseId, isArchived, name, email, role,
                              isDisplayedToStudents, displayedName, getTextFromInstructorPrivileges());
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();

        if (googleId != null) {
            addNonEmptyError(validator.getInvalidityInfoForGoogleId(googleId), errors);
        }

        addNonEmptyError(validator.getInvalidityInfoForCourseId(courseId), errors);

        addNonEmptyError(validator.getInvalidityInfoForPersonName(name), errors);

        addNonEmptyError(validator.getInvalidityInfoForEmail(email), errors);

        addNonEmptyError(validator.getInvalidityInfoForPersonName(displayedName), errors);

        return errors;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, InstructorAttributes.class);
    }

    @Override
    public String getIdentificationString() {
        return courseId + "/" + email;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Instructor";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, InstructorAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        googleId = SanitizationHelper.sanitizeGoogleId(googleId);
        name = SanitizationHelper.sanitizeForHtml(SanitizationHelper.sanitizeName(name));
        email = SanitizationHelper.sanitizeEmail(email);
        courseId = SanitizationHelper.sanitizeTitle(courseId);

        if (role == null) {
            role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            role = SanitizationHelper.sanitizeForHtml(SanitizationHelper.sanitizeName(role));
        }

        if (displayedName == null) {
            displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            displayedName = SanitizationHelper.sanitizeForHtml(SanitizationHelper.sanitizeName(displayedName));
        }

        if (privileges == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        }
    }

    public boolean isAllowedForPrivilege(String privilegeName) {
        if (privileges == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        }
        return privileges.isAllowedForPrivilege(privilegeName);
    }

    public boolean isAllowedForPrivilege(String sectionName, String privilegeName) {
        if (privileges == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        }
        return privileges.isAllowedForPrivilege(sectionName, privilegeName);
    }

    public boolean isAllowedForPrivilege(String sectionName, String sessionName, String privilegeName) {
        if (privileges == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        }
        return privileges.isAllowedForPrivilege(sectionName, sessionName, privilegeName);
    }

    /**
     * Returns true if privilege for session is present for any section.
     */
    public boolean isAllowedForPrivilegeAnySection(String sessionName, String privilegeName) {
        if (privileges == null) {
            privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        }
        return privileges.isAllowedForPrivilegeAnySection(sessionName, privilegeName);
    }

    public boolean hasCoownerPrivileges() {
        return privileges.hasCoownerPrivileges();
    }

    public boolean hasManagerPrivileges() {
        return privileges.hasManagerPrivileges();
    }

    public boolean hasObserverPrivileges() {
        return privileges.hasObserverPrivileges();
    }

    public boolean hasTutorPrivileges() {
        return privileges.hasTutorPrivileges();
    }

    /**
     * Returns true if this instructor object is equal with the given {@code instructor} object.
     *
     * @param instructor
     *            the {@link InstructorAttributes} of an instructor, cannot be
     *            {@code null}
     * @return true if this {@link InstructorAttributes} is equal to
     *         {@code instructor}, otherwise false
     */
    public boolean isEqualToAnotherInstructor(InstructorAttributes instructor) {
        // JsonParser is used instead of
        // this.getJsonString().equals(instructor.getJsonString) so that the
        // comparison ignores the order of key-value pairs in the json strings.
        return JsonUtils.parse(getJsonString()).equals(JsonUtils.parse(instructor.getJsonString()));
    }

    public boolean isCustomRole() {
        return Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM.equals(role);
    }

    public String getCourseId() {
        return courseId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getRole() {
        return role;
    }
}
