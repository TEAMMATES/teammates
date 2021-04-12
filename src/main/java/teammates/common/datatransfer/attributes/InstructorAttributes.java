package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    public String courseId;
    public String email;

    public String name;
    public String googleId;
    public String key;
    public String role;
    public String displayedName;
    public boolean isArchived;
    public boolean isDisplayedToStudents;
    public InstructorPrivileges privileges;

    private InstructorAttributes(String courseId, String email) {
        this.courseId = courseId;
        this.email = email;

        this.role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        this.displayedName = DEFAULT_DISPLAY_NAME;
        this.isArchived = false;
        this.isDisplayedToStudents = true;
        this.privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
    }

    /**
     * Return a builder for {@link InstructorAttributes}.
     */
    public static Builder builder(String courseId, String email) {
        return new Builder(courseId, email);
    }

    public static InstructorAttributes valueOf(Instructor instructor) {
        InstructorAttributes instructorAttributes =
                new InstructorAttributes(instructor.getCourseId(), instructor.getEmail());

        instructorAttributes.name = instructor.getName();
        instructorAttributes.googleId = instructor.getGoogleId();
        instructorAttributes.key = instructor.getRegistrationKey();
        if (instructor.getRole() != null) {
            instructorAttributes.role = instructor.getRole();
        }
        if (instructor.getDisplayedName() != null) {
            instructorAttributes.displayedName = instructor.getDisplayedName();
        }
        instructorAttributes.isArchived = instructor.getIsArchived();
        instructorAttributes.isDisplayedToStudents = instructor.isDisplayedToStudents();

        if (instructor.getInstructorPrivilegesAsText() == null) {
            instructorAttributes.privileges =
                    new InstructorPrivileges(instructorAttributes.role);
        } else {
            instructorAttributes.privileges =
                    JsonUtils.fromJson(instructor.getInstructorPrivilegesAsText(), InstructorPrivileges.class);
        }

        return instructorAttributes;
    }

    public InstructorAttributes getCopy() {
        InstructorAttributes instructorAttributes = new InstructorAttributes(courseId, email);
        instructorAttributes.name = name;
        instructorAttributes.googleId = googleId;
        instructorAttributes.key = key;
        instructorAttributes.role = role;
        instructorAttributes.displayedName = displayedName;
        instructorAttributes.isArchived = isArchived;
        instructorAttributes.isDisplayedToStudents = isDisplayedToStudents;
        instructorAttributes.privileges = privileges;

        return instructorAttributes;
    }

    public String getTextFromInstructorPrivileges() {
        return JsonUtils.toJson(privileges, InstructorPrivileges.class);
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public InstructorPrivileges getPrivileges() {
        return privileges;
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
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.email).append(this.name).append(this.courseId)
                .append(this.googleId).append(this.displayedName).append(this.role);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            InstructorAttributes otherInstructor = (InstructorAttributes) other;
            return Objects.equals(this.email, otherInstructor.email)
                    && Objects.equals(this.name, otherInstructor.name)
                    && Objects.equals(this.courseId, otherInstructor.courseId)
                    && Objects.equals(this.googleId, otherInstructor.googleId)
                    && Objects.equals(this.displayedName, otherInstructor.displayedName)
                    && Objects.equals(this.role, otherInstructor.role);
        } else {
            return false;
        }
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
     * Returns a {@link UpdateOptionsWithEmail.Builder} to build {@link UpdateOptions}
     * for an instructor with {@code courseId} and {@code email}.
     */
    public static UpdateOptionsWithEmail.Builder updateOptionsWithEmailBuilder(String courseId, String email) {
        return new UpdateOptionsWithEmail.Builder(courseId, email);
    }

    /**
     * Returns a {@link UpdateOptionsWithGoogleId.Builder} to build {@link UpdateOptions}
     * for an instructor with {@code courseId} and {@code googleId}.
     */
    public static UpdateOptionsWithGoogleId.Builder updateOptionsWithGoogleIdBuilder(String courseId, String googleId) {
        return new UpdateOptionsWithGoogleId.Builder(courseId, googleId);
    }

    /**
     * A builder class for {@link InstructorAttributes}.
     */
    public static class Builder extends BasicBuilder<InstructorAttributes, Builder> {
        private final InstructorAttributes instructorAttributes;

        private Builder(String courseId, String email) {
            super(new UpdateOptions());
            thisBuilder = this;

            Assumption.assertNotNull(courseId);
            Assumption.assertNotNull(email);

            instructorAttributes = new InstructorAttributes(courseId, email);
        }

        public Builder withGoogleId(String googleId) {
            Assumption.assertNotNull(googleId);
            instructorAttributes.googleId = googleId;

            return this;
        }

        @Override
        public InstructorAttributes build() {
            instructorAttributes.updateBasic(updateOptions);

            return instructorAttributes;
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
            Assumption.assertNotNull(courseId);
            Assumption.assertNotNull(email);

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
        public static class Builder extends BasicBuilder<UpdateOptionsWithEmail, UpdateOptionsWithEmail.Builder> {

            private UpdateOptionsWithEmail updateOptionsWithEmail;

            private Builder(String courseId, String email) {
                super(new UpdateOptionsWithEmail(courseId, email));
                thisBuilder = this;

                updateOptionsWithEmail = (UpdateOptionsWithEmail) updateOptions;
            }

            public Builder withGoogleId(String googleId) {
                updateOptionsWithEmail.googleIdOption = UpdateOption.of(googleId);

                return this;
            }

            @Override
            public UpdateOptionsWithEmail build() {
                return updateOptionsWithEmail;
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
            Assumption.assertNotNull(courseId);
            Assumption.assertNotNull(googleId);

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
                extends BasicBuilder<UpdateOptionsWithGoogleId, UpdateOptionsWithGoogleId.Builder> {

            private UpdateOptionsWithGoogleId updateOptionsWithGoogleId;

            private Builder(String courseId, String email) {
                super(new UpdateOptionsWithGoogleId(courseId, email));
                thisBuilder = this;

                updateOptionsWithGoogleId = (UpdateOptionsWithGoogleId) updateOptions;
            }

            public Builder withEmail(String email) {
                Assumption.assertNotNull(email);

                updateOptionsWithGoogleId.emailOption = UpdateOption.of(email);
                return this;
            }

            @Override
            public UpdateOptionsWithGoogleId build() {
                return updateOptionsWithGoogleId;
            }
        }
    }

    /**
     * Helper class to specific the fields to update in {@link InstructorAttributes}.
     */
    private static class UpdateOptions {

        UpdateOption<String> nameOption = UpdateOption.empty();
        UpdateOption<Boolean> isArchivedOption = UpdateOption.empty();
        UpdateOption<String> roleOption = UpdateOption.empty();
        UpdateOption<Boolean> isDisplayedToStudentsOption = UpdateOption.empty();
        UpdateOption<String> displayedNameOption = UpdateOption.empty();
        UpdateOption<InstructorPrivileges> instructorPrivilegesOption = UpdateOption.empty();

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

    }

    /**
     * Basic builder to build {@link InstructorAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withName(String name) {
            Assumption.assertNotNull(name);

            updateOptions.nameOption = UpdateOption.of(name);
            return thisBuilder;
        }

        public B withRole(String role) {
            Assumption.assertNotNull(role);

            updateOptions.roleOption = UpdateOption.of(role);
            return thisBuilder;
        }

        public B withDisplayedName(String displayedName) {
            Assumption.assertNotNull(displayedName);

            updateOptions.displayedNameOption = UpdateOption.of(displayedName);
            return thisBuilder;
        }

        public B withPrivileges(InstructorPrivileges instructorPrivileges) {
            Assumption.assertNotNull(instructorPrivileges);

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

        public abstract T build();

    }
}
