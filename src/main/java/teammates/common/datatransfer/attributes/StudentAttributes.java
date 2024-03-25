package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Student;

/**
 * The data transfer object for {@link CourseStudent} entities.
 */
public final class StudentAttributes extends EntityAttributes<CourseStudent> {

    private String email;
    private String course;
    private String name;
    private String googleId;
    private String comments;
    private String team;
    private String section;
    private transient String key;
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

    /**
     * Gets the {@link StudentAttributes} instance of the given {@link CourseStudent}.
     */
    public static StudentAttributes valueOf(CourseStudent student) {
        StudentAttributes studentAttributes = new StudentAttributes(student.getCourseId(), student.getEmail());
        studentAttributes.name = student.getName();
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
     * Gets the {@link StudentAttributes} instance of the given {@link Student}.
     */
    public static StudentAttributes valueOf(Student student) {
        StudentAttributes studentAttributes = new StudentAttributes(student.getCourseId(), student.getEmail());
        studentAttributes.name = student.getName();
        if (student.getGoogleId() != null) {
            studentAttributes.googleId = student.getGoogleId();
        }
        studentAttributes.team = student.getTeamName();
        if (student.getSectionName() != null) {
            studentAttributes.section = student.getSectionName();
        }
        studentAttributes.comments = student.getComments();
        // studentAttributes.key = student.getRegistrationKey();
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

    /**
     * Gets a deep copy of this object.
     */
    public StudentAttributes getCopy() {
        StudentAttributes studentAttributes = new StudentAttributes(course, email);

        studentAttributes.name = name;
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
                .withRegistrationKey(key)
                .withEntityType(Const.EntityType.STUDENT)
                .toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public void setSection(String section) {
        this.section = section;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
        assert team != null;
        assert comments != null;

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

    /**
     * Sorts the list of students by the section name, then team name, then name.
     */
    public static void sortBySectionName(List<StudentAttributes> students) {
        students.sort(Comparator.comparing((StudentAttributes student) -> student.section)
                .thenComparing(student -> student.team)
                .thenComparing(student -> student.name));
    }

    /**
     * Sorts the list of students by the team name, then name.
     */
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
        updateOptions.nameOption.ifPresent(s -> name = s);
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
    public static final class Builder extends BasicBuilder<StudentAttributes, Builder> {

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
    public static final class UpdateOptions {
        private String courseId;
        private String email;

        private UpdateOption<String> newEmailOption = UpdateOption.empty();
        private UpdateOption<String> nameOption = UpdateOption.empty();
        private UpdateOption<String> commentOption = UpdateOption.empty();
        private UpdateOption<String> googleIdOption = UpdateOption.empty();
        private UpdateOption<String> teamNameOption = UpdateOption.empty();
        private UpdateOption<String> sectionNameOption = UpdateOption.empty();

        private UpdateOptions(String courseId, String email) {
            assert courseId != null;
            assert email != null;

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
                    + ", comment = " + commentOption
                    + ", googleId = " + googleIdOption
                    + ", teamName = " + teamNameOption
                    + ", sectionName = " + sectionNameOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static final class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String courseId, String email) {
                super(new UpdateOptions(courseId, email));
                thisBuilder = this;
            }

            public Builder withNewEmail(String email) {
                assert email != null;

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
            assert name != null;

            updateOptions.nameOption = UpdateOption.of(name);
            return thisBuilder;
        }

        public B withComment(String comment) {
            assert comment != null;

            updateOptions.commentOption = UpdateOption.of(comment);
            return thisBuilder;
        }

        public B withGoogleId(String googleId) {
            // google id can be set to null
            updateOptions.googleIdOption = UpdateOption.of(googleId);
            return thisBuilder;
        }

        public B withTeamName(String teamName) {
            assert teamName != null;

            updateOptions.teamNameOption = UpdateOption.of(teamName);
            return thisBuilder;
        }

        public B withSectionName(String sectionName) {
            assert sectionName != null;

            updateOptions.sectionNameOption = UpdateOption.of(sectionName);
            return thisBuilder;
        }

        public abstract T build();

    }
}
