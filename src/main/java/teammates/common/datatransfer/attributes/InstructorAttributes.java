package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Instructor;

/**
 * The data transfer class for Instructor entities.
 */
public class InstructorAttributes extends EntityAttributes<Instructor> {

    public static final String DEFAULT_DISPLAY_NAME = "Instructor";

    /**
     * Sorts the Instructors list alphabetically by name.
     */
    public static final Comparator<InstructorAttributes> COMPARE_BY_NAME =
            Comparator.comparing(instructor -> instructor.name.toLowerCase());

    private static final String INSTRUCTOR_BACKUP_LOG_MSG = "Recently modified instructor::";
    private static final String ATTRIBUTE_NAME = "Instructor";
    // Note: be careful when changing these variables as their names are used in *.json files.

    /** Required fields. */
    public String googleId;
    public String courseId;
    public String name;
    public String email;

    /** Optional fields. */
    public String key;
    public String role;
    public String displayedName;
    public Boolean isArchived;
    public boolean isDisplayedToStudents;
    public InstructorPrivileges privileges;

    /**
     * Return new builder instance with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * {@code isArchived = false} <br>
     * {@code isDisplayedForStudents = true} <br>
     * {@code displayedName = DEFAULT_DISPLAYED_NAME} <br>
     * {@code role = INSTRUCTOR_PERMISSION_ROLE_COOWNER} <br>
     * {@code privileges = new InstructorPrivileges(role)} <br>
     */
    public static Builder builder(String googleId, String courseId, String name, String email) {
        return new Builder(googleId, courseId, name, email);
    }

    public static InstructorAttributes valueOf(Instructor instructor) {
        instructor.setGeneratedKeyIfNull();

        return builder(instructor.getGoogleId(), instructor.getCourseId(), instructor.getName(), instructor.getEmail())
                .withKey(instructor.getRegistrationKey())
                .withRole(instructor.getRole())
                .withDisplayedName(instructor.getDisplayedName())
                .withPrivileges(instructor.getInstructorPrivilegesAsText())
                .withIsDisplayedToStudents(instructor.isDisplayedToStudents())
                .withIsArchived(instructor.getIsArchived())
                .build();
    }

    public InstructorAttributes getCopy() {
        return builder(googleId, courseId, name, email)
                .withKey(key).withRole(role).withDisplayedName(displayedName)
                .withPrivileges(privileges).withIsDisplayedToStudents(isDisplayedToStudents).withIsArchived(isArchived)
                .build();
    }

    public String getTextFromInstructorPrivileges() {
        return JsonUtils.toJson(privileges, InstructorPrivileges.class);
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
        return googleId != null && !googleId.trim().isEmpty();
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
        List<String> errors = new ArrayList<>();

        if (googleId != null) {
            addNonEmptyError(FieldValidator.getInvalidityInfoForGoogleId(googleId), errors);
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(courseId), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(displayedName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForRole(role), errors);

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
        return ATTRIBUTE_NAME;
    }

    @Override
    public String getBackupIdentifier() {
        return INSTRUCTOR_BACKUP_LOG_MSG + courseId + "::" + email;
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, InstructorAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        googleId = SanitizationHelper.sanitizeGoogleId(googleId);
        name = SanitizationHelper.sanitizeName(name);
        email = SanitizationHelper.sanitizeEmail(email);
        courseId = SanitizationHelper.sanitizeTitle(courseId);

        if (role == null) {
            role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            role = SanitizationHelper.sanitizeName(role);
        }

        if (displayedName == null) {
            displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        } else {
            displayedName = SanitizationHelper.sanitizeName(displayedName);
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

    /**
     * Updates with {@link UpdateOptionsWithEmail}.
     */
    public void update(UpdateOptionsWithEmail updateOptions) {
        updateOptions.googleIdOption.ifPresent(s -> googleId = s);
        updateBasic(updateOptions);
    }

    /**
     * Updates with {@link UpdateOptionsWithGoogleId}.
     */
    public void update(UpdateOptionsWithGoogleId updateOptions) {
        updateOptions.emailOption.ifPresent(s -> email = s);
        updateBasic(updateOptions);
    }

    private void updateBasic(UpdateOptions updateOptions) {
        updateOptions.nameOption.ifPresent(s -> name = s);
        updateOptions.isArchivedOption.ifPresent(s -> isArchived = s);
        updateOptions.roleOption.ifPresent(s -> role = s);
        updateOptions.isDisplayedToStudentsOption.ifPresent(s -> isDisplayedToStudents = s);
        updateOptions.instructorPrivilegesOption.ifPresent(s -> privileges = s);
        updateOptions.displayedNameOption.ifPresent(s -> displayedName = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions}
     * for an instructor with {@code courseId} and {@code email}.
     */
    public static UpdateOptionsWithEmail.Builder updateOptionsWithEmailBuilder(String courseId, String email) {
        return new UpdateOptionsWithEmail.Builder(courseId, email);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions}
     * for an instructor with {@code courseId} and {@code googleId}.
     */
    public static UpdateOptionsWithGoogleId.Builder updateOptionsWithGoogleIdBuilder(String courseId, String googleId) {
        return new UpdateOptionsWithGoogleId.Builder(courseId, googleId);
    }

    /**
     * A Builder class for {@link InstructorAttributes}.
     */
    public static class Builder {
        private final InstructorAttributes instructorAttributes;

        public Builder(String googleId, String courseId, String name, String email) {
            instructorAttributes = new InstructorAttributes();

            instructorAttributes.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
            instructorAttributes.courseId = SanitizationHelper.sanitizeTitle(courseId);
            instructorAttributes.name = SanitizationHelper.sanitizeName(name);
            instructorAttributes.email = email;

            instructorAttributes.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
            instructorAttributes.displayedName = DEFAULT_DISPLAY_NAME;
            instructorAttributes.isArchived = false;
            instructorAttributes.isDisplayedToStudents = true;
            instructorAttributes.privileges = new InstructorPrivileges(instructorAttributes.role);
        }

        public Builder withKey(String key) {
            instructorAttributes.key = key;
            return this;
        }

        public Builder withRole(String role) {
            if (role != null) {
                instructorAttributes.role = SanitizationHelper.sanitizeName(role);
            }

            return this;
        }

        public Builder withDisplayedName(String displayedName) {
            if (displayedName != null) {
                instructorAttributes.displayedName = SanitizationHelper.sanitizeName(displayedName);
            }

            return this;
        }

        public Builder withIsArchived(Boolean isArchived) {
            instructorAttributes.isArchived = isArchived != null && isArchived;
            return this;
        }

        public Builder withIsDisplayedToStudents(boolean isDisplayedToStudents) {
            instructorAttributes.isDisplayedToStudents = isDisplayedToStudents;
            return this;
        }

        public Builder withPrivileges(InstructorPrivileges privileges) {
            instructorAttributes.privileges = (privileges == null)
                    ? new InstructorPrivileges(instructorAttributes.role)
                    : privileges;
            return this;
        }

        public Builder withPrivileges(String privilegesAsText) {
            instructorAttributes.privileges = (privilegesAsText == null)
                    ? new InstructorPrivileges(instructorAttributes.role)
                    : getInstructorPrivilegesFromText(privilegesAsText);
            return this;
        }

        public InstructorAttributes build() {
            return instructorAttributes;
        }

        private static InstructorPrivileges getInstructorPrivilegesFromText(String instructorPrivilegesAsText) {
            return JsonUtils.fromJson(instructorPrivilegesAsText, InstructorPrivileges.class);
        }
    }

    /**
     * Helper class to specific the fields to update in {@link StudentAttributes}.
     *
     * <p>{@code courseId} and {@code email} is used to identify the instructor.
     */
    public static class UpdateOptionsWithEmail extends UpdateOptions {
        private String courseId;
        private String email;

        private UpdateOption<String> googleIdOption = UpdateOption.empty();

        private UpdateOptionsWithEmail(String courseId, String email) {
            super();
            Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, courseId);
            Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, email);

            this.courseId = courseId;
            this.email = email;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return super.toString()
                    + "]" + String.format("(courseId = %s/googleId = %s)", courseId, email);
        }

        /**
         * Builder class for {@link UpdateOptionsWithEmail}.
         */
        public static class Builder extends UpdateOptions.Builder<UpdateOptionsWithEmail, UpdateOptionsWithEmail.Builder> {

            private Builder(String courseId, String email) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, courseId);
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, email);

                updateOptions = new UpdateOptionsWithEmail(courseId, email);
                thisBuilder = this;
            }

            public Builder withGoogleId(String googleId) {
                updateOptions.googleIdOption = UpdateOption.of(googleId);
                return this;
            }

        }
    }

    /**
     * Helper class to specific the fields to update in {@link StudentAttributes}
     *
     * <p>{@code courseId} and {@code googleId} is used to identify the instructor.
     */
    public static class UpdateOptionsWithGoogleId extends UpdateOptions {
        private String courseId;
        private String googleId;

        private UpdateOption<String> emailOption = UpdateOption.empty();

        private UpdateOptionsWithGoogleId(String courseId, String googleId) {
            super();
            Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, courseId);
            Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, googleId);

            this.courseId = courseId;
            this.googleId = googleId;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getGoogleId() {
            return googleId;
        }

        @Override
        public String toString() {
            return super.toString()
                    + ", email = " + emailOption
                    + "]" + String.format("(courseId = %s/googleId = %s)", courseId, googleId);
        }

        /**
         * Builder class for {@link UpdateOptionsWithGoogleId}.
         */
        public static class Builder
                extends UpdateOptions.Builder<UpdateOptionsWithGoogleId, UpdateOptionsWithGoogleId.Builder> {

            private Builder(String courseId, String email) {
                super();
                updateOptions = new UpdateOptionsWithGoogleId(courseId, email);
                thisBuilder = this;
            }

            public Builder withEmail(String email) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, email);

                updateOptions.emailOption = UpdateOption.of(email);
                return this;
            }
        }
    }

    /**
     * Helper class to specific the fields to update in {@link InstructorAttributes}.
     */
    private static class UpdateOptions {

        protected UpdateOption<String> nameOption = UpdateOption.empty();
        protected UpdateOption<Boolean> isArchivedOption = UpdateOption.empty();
        protected UpdateOption<String> roleOption = UpdateOption.empty();
        protected UpdateOption<Boolean> isDisplayedToStudentsOption = UpdateOption.empty();
        protected UpdateOption<String> displayedNameOption = UpdateOption.empty();
        protected UpdateOption<InstructorPrivileges> instructorPrivilegesOption = UpdateOption.empty();

        @Override
        public String toString() {
            return "InstructorAttributes.UpdateOptions ["
                    + "name = " + nameOption
                    + ", isAchieved = " + isArchivedOption
                    + ", roleOption = " + roleOption
                    + ", isDisplayedToStudents = " + isDisplayedToStudentsOption
                    + ", displayedName = " + displayedNameOption
                    + ", instructorPrivilegeAsText = " + instructorPrivilegesOption;
        }

        /**
         * A Builder class to build {@link UpdateOptions}.
         */
        private static class Builder<T extends UpdateOptions, B extends UpdateOptions.Builder<T, B>> {

            protected T updateOptions;
            protected B thisBuilder;

            public B withName(String name) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, name);

                updateOptions.nameOption = UpdateOption.of(name);
                return thisBuilder;
            }

            public B withRole(String role) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, role);

                updateOptions.roleOption = UpdateOption.of(role);
                return thisBuilder;
            }

            public B withDisplayedName(String displayedName) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, displayedName);

                updateOptions.displayedNameOption = UpdateOption.of(displayedName);
                return thisBuilder;
            }

            public B withPrivileges(InstructorPrivileges instructorPrivileges) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, instructorPrivileges);

                updateOptions.instructorPrivilegesOption = UpdateOption.of(instructorPrivileges);
                return thisBuilder;
            }

            public B withIsDisplayedToStudents(boolean isDisplayedToStudents) {
                updateOptions.isDisplayedToStudentsOption = UpdateOption.of(isDisplayedToStudents);
                return thisBuilder;
            }

            public B withIsArchived(boolean isAchieved) {
                updateOptions.isArchivedOption = UpdateOption.of(isAchieved);
                return thisBuilder;
            }

            public T build() {
                return updateOptions;
            }

        }

    }
}
