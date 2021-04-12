package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.CourseStudent;

public class StudentAttributes extends EntityAttributes<CourseStudent> {

    public String email;
    public String course;

    public String name;
    public String googleId;
    public String lastName;
    public String comments;
    public String team;
    public String section;
    public String key;

    private transient Instant createdAt;
    private transient Instant updatedAt;

    private StudentAttributes(String courseId, String email) {
        this.course = courseId;
        this.email = email;

        this.googleId = "";
        this.section = Const.DEFAULT_SECTION;
        this.createdAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        this.updatedAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
    }

    public static StudentAttributes valueOf(CourseStudent student) {
        StudentAttributes studentAttributes = new StudentAttributes(student.getCourseId(), student.getEmail());
        studentAttributes.name = student.getName();
        studentAttributes.lastName = student.getLastName();
        if (student.getGoogleId() != null) {
            studentAttributes.googleId = student.getGoogleId();
        }
        studentAttributes.team = student.getTeamName();
        if (student.getSectionName() != null) {
            studentAttributes.section = student.getSectionName();
        }
        studentAttributes.comments = student.getComments();
        studentAttributes.key = student.getRegistrationKey();
        if (student.getCreatedAt() != null) {
            studentAttributes.createdAt = student.getCreatedAt();
        }
        if (student.getUpdatedAt() != null) {
            studentAttributes.updatedAt = student.getUpdatedAt();
        }

        return studentAttributes;
    }

    /**
     * Return a builder for {@link StudentAttributes}.
     */
    public static Builder builder(String courseId, String email) {
        return new Builder(courseId, email);
    }

    public StudentAttributes getCopy() {
        StudentAttributes studentAttributes = new StudentAttributes(course, email);

        studentAttributes.name = name;
        studentAttributes.lastName = lastName;
        studentAttributes.googleId = googleId;
        studentAttributes.team = team;
        studentAttributes.section = section;
        studentAttributes.comments = comments;
        studentAttributes.key = key;
        studentAttributes.createdAt = createdAt;
        studentAttributes.updatedAt = updatedAt;

        return studentAttributes;
    }

    public boolean isRegistered() {
        return googleId != null && !googleId.trim().isEmpty();
    }

    public String getRegistrationUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(StringHelper.encrypt(key))
                .withStudentEmail(email)
                .withCourseId(course)
                .withEntityType(Const.EntityType.STUDENT)
                .toString();
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getKey() {
        return key;
    }

    /**
     * Format: email%courseId e.g., adam@gmail.com%cs1101.
     */
    public String getId() {
        return email + "%" + course;
    }

    public String getSection() {
        return section;
    }

    public String getTeam() {
        return team;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            StudentAttributes otherStudent = (StudentAttributes) other;
            return Objects.equals(this.course, otherStudent.course)
                    && Objects.equals(this.name, otherStudent.name)
                    && Objects.equals(this.email, otherStudent.email)
                    && Objects.equals(this.googleId, otherStudent.googleId)
                    && Objects.equals(this.comments, otherStudent.comments)
                    && Objects.equals(this.team, otherStudent.team)
                    && Objects.equals(this.section, otherStudent.section);
        } else {
            return false;
        }
    }

    @Override
    public List<String> getInvalidityInfo() {
        // id is allowed to be null when the student is not registered
        Assumption.assertNotNull(team);
        Assumption.assertNotNull(comments);

        List<String> errors = new ArrayList<>();

        if (isRegistered()) {
            addNonEmptyError(FieldValidator.getInvalidityInfoForGoogleId(googleId), errors);
        }

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(course), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTeamName(team), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForSectionName(section), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForStudentRoleComments(comments), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);

        return errors;
    }

    public static void sortBySectionName(List<StudentAttributes> students) {
        students.sort(Comparator.comparing((StudentAttributes student) -> student.section)
                .thenComparing(student -> student.team)
                .thenComparing(student -> student.name));
    }

    public static void sortByTeamName(List<StudentAttributes> students) {
        students.sort(Comparator.comparing((StudentAttributes student) -> student.team)
                .thenComparing(student -> student.name));
    }

    @Override
    public CourseStudent toEntity() {
        return new CourseStudent(email, name, googleId, comments, course, team, section);
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.email).append(this.name).append(this.course)
            .append(this.googleId).append(this.team).append(this.section).append(this.comments);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public String toString() {
        return "Student:" + name + "[" + email + "]";
    }

    @Override
    public void sanitizeForSaving() {
        googleId = SanitizationHelper.sanitizeGoogleId(googleId);
        name = SanitizationHelper.sanitizeName(name);
        comments = SanitizationHelper.sanitizeTextField(comments);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.newEmailOption.ifPresent(s -> email = s);
        updateOptions.nameOption.ifPresent(s -> {
            name = s;
            lastName = StringHelper.splitName(s)[1];
        });
        updateOptions.lastNameOption.ifPresent(s -> lastName = s);
        updateOptions.commentOption.ifPresent(s -> comments = s);
        updateOptions.googleIdOption.ifPresent(s -> googleId = s);
        updateOptions.teamNameOption.ifPresent(s -> team = s);
        updateOptions.sectionNameOption.ifPresent(s -> section = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a student.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String courseId, String email) {
        return new UpdateOptions.Builder(courseId, email);
    }

    /**
     * A builder class for {@link StudentAttributes}.
     */
    public static class Builder extends BasicBuilder<StudentAttributes, Builder> {

        private final StudentAttributes studentAttributes;

        private Builder(String courseId, String email) {
            super(new UpdateOptions(courseId, email));
            thisBuilder = this;

            studentAttributes = new StudentAttributes(courseId, email);
        }

        @Override
        public StudentAttributes build() {
            studentAttributes.update(updateOptions);

            return studentAttributes;
        }
    }

    /**
     * Helper class to specify the fields to update in {@link StudentAttributes}.
     */
    public static class UpdateOptions {
        private String courseId;
        private String email;

        private UpdateOption<String> newEmailOption = UpdateOption.empty();
        private UpdateOption<String> nameOption = UpdateOption.empty();
        private UpdateOption<String> lastNameOption = UpdateOption.empty();
        private UpdateOption<String> commentOption = UpdateOption.empty();
        private UpdateOption<String> googleIdOption = UpdateOption.empty();
        private UpdateOption<String> teamNameOption = UpdateOption.empty();
        private UpdateOption<String> sectionNameOption = UpdateOption.empty();

        private UpdateOptions(String courseId, String email) {
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
            return "StudentAttributes.UpdateOptions ["
                    + "courseId = " + courseId
                    + ", email = " + email
                    + ", newEmail = " + newEmailOption
                    + ", name = " + nameOption
                    + ", lastName = " + lastNameOption
                    + ", comment = " + commentOption
                    + ", googleId = " + googleIdOption
                    + ", teamName = " + teamNameOption
                    + ", sectionName = " + sectionNameOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String courseId, String email) {
                super(new UpdateOptions(courseId, email));
                thisBuilder = this;
            }

            public Builder withNewEmail(String email) {
                Assumption.assertNotNull(email);

                updateOptions.newEmailOption = UpdateOption.of(email);
                return thisBuilder;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link StudentAttributes} related classes.
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

        public B withLastName(String name) {
            Assumption.assertNotNull(name);

            updateOptions.lastNameOption = UpdateOption.of(name);
            return thisBuilder;
        }

        public B withComment(String comment) {
            Assumption.assertNotNull(comment);

            updateOptions.commentOption = UpdateOption.of(comment);
            return thisBuilder;
        }

        public B withGoogleId(String googleId) {
            // google id can be set to null
            updateOptions.googleIdOption = UpdateOption.of(googleId);
            return thisBuilder;
        }

        public B withTeamName(String teamName) {
            Assumption.assertNotNull(teamName);

            updateOptions.teamNameOption = UpdateOption.of(teamName);
            return thisBuilder;
        }

        public B withSectionName(String sectionName) {
            Assumption.assertNotNull(sectionName);

            updateOptions.sectionNameOption = UpdateOption.of(sectionName);
            return thisBuilder;
        }

        public abstract T build();

    }
}
