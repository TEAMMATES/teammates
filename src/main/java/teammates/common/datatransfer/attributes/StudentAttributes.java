package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Strings;

import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.CourseStudent;

public class StudentAttributes extends EntityAttributes<CourseStudent> {

    private static final String STUDENT_BACKUP_LOG_MSG = "Recently modified student::";
    private static final String ATTRIBUTE_NAME = "Student";

    // Required fields
    public String email;
    public String course;
    public String name;

    // Optional values
    public String googleId;
    public String lastName;
    public String comments;
    public String team;
    public String section;
    public String key;

    public transient StudentUpdateStatus updateStatus;

    /*
     * Creation and update time stamps.
     * Updated automatically in Student.java, jdoPreStore()
     */
    private transient Instant createdAt;
    private transient Instant updatedAt;

    StudentAttributes() {
        googleId = "";
        section = Const.DEFAULT_SECTION;
        updateStatus = StudentUpdateStatus.UNKNOWN;
        createdAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
        updatedAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;
    }

    public static StudentAttributes valueOf(CourseStudent student) {
        return builder(student.getCourseId(), student.getName(), student.getEmail())
                .withLastName(student.getLastName())
                .withComments(student.getComments())
                .withTeam(student.getTeamName())
                .withSection(student.getSectionName())
                .withGoogleId(student.getGoogleId())
                .withKey(student.getRegistrationKey())
                .withCreatedAt(student.getCreatedAt())
                .withUpdatedAt(student.getUpdatedAt())
                .build();
    }

    /**
     * Return new builder instance with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * <ul>
     * <li>{@code googleId = ""}</li>
     * <li>{@code section = Const.DEFAULT_SECTION}</li>
     * <li>{@code updateStatus = StudentUpdateStatus.UNKNOWN}</li>
     * <li>{@code createdAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP_DATE}</li>
     * <li>{@code updatedAt = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP_DATE}</li>
     * </ul>
     */
    public static Builder builder(String courseId, String name, String email) {
        return new Builder(courseId, name, email);
    }

    public StudentAttributes getCopy() {
        StudentAttributes studentAttributes = valueOf(toEntity());

        studentAttributes.updateStatus = updateStatus;
        studentAttributes.key = key;
        studentAttributes.createdAt = createdAt;
        studentAttributes.updatedAt = updatedAt;

        return studentAttributes;
    }

    public String toEnrollmentString() {
        String enrollmentStringSeparator = "|";

        return this.section + enrollmentStringSeparator
             + this.team + enrollmentStringSeparator
             + this.name + enrollmentStringSeparator
             + this.email + enrollmentStringSeparator
             + this.comments;
    }

    public boolean isRegistered() {
        return googleId != null && !googleId.trim().isEmpty();
    }

    public String getRegistrationUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(StringHelper.encrypt(key))
                .withStudentEmail(email)
                .withCourseId(course)
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT)
                .toString();
    }

    public String getPublicProfilePictureUrl() {
        return Config.getBackEndAppUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                .withStudentEmail(StringHelper.encrypt(email))
                .withCourseId(StringHelper.encrypt(course))
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

    public boolean isEnrollInfoSameAs(StudentAttributes otherStudent) {
        return otherStudent != null && otherStudent.email.equals(this.email)
               && otherStudent.course.equals(this.course)
               && otherStudent.name.equals(this.name)
               && otherStudent.comments.equals(this.comments)
               && otherStudent.team.equals(this.team)
               && otherStudent.section.equals(this.section);
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

    public static void sortByNameAndThenByEmail(List<StudentAttributes> students) {
        students.sort(Comparator.comparing((StudentAttributes student) -> student.name)
                .thenComparing(student -> student.email));
    }

    public void updateWithExistingRecord(StudentAttributes originalStudent) {
        if (this.email == null) {
            this.email = originalStudent.email;
        }

        if (this.name == null) {
            this.name = originalStudent.name;
        }

        if (this.googleId == null) {
            this.googleId = originalStudent.googleId;
        }

        if (this.team == null) {
            this.team = originalStudent.team;
        }

        if (this.comments == null) {
            this.comments = originalStudent.comments;
        }

        if (this.section == null) {
            this.section = originalStudent.section;
        }
    }

    @Override
    public CourseStudent toEntity() {
        return new CourseStudent(email, name, googleId, comments, course, team, section);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        String indentString = StringHelper.getIndent(indent);
        StringBuilder sb = new StringBuilder();
        sb.append(indentString + "Student:" + name + "[" + email + "]" + System.lineSeparator());

        return sb.toString();
    }

    @Override
    public String getIdentificationString() {
        return this.course + "/" + this.email;
    }

    @Override
    public String getEntityTypeAsString() {
        return ATTRIBUTE_NAME;
    }

    @Override
    public String getBackupIdentifier() {
        return STUDENT_BACKUP_LOG_MSG + getId();
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, StudentAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        googleId = SanitizationHelper.sanitizeGoogleId(googleId);
        name = SanitizationHelper.sanitizeName(name);
        comments = SanitizationHelper.sanitizeTextField(comments);
    }

    public String getStudentStatus() {
        if (isRegistered()) {
            return Const.STUDENT_COURSE_STATUS_JOINED;
        }
        return Const.STUDENT_COURSE_STATUS_YET_TO_JOIN;
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
     * Returns true if section value has changed from its original value.
     */
    public boolean isSectionChanged(StudentAttributes originalStudentAttribute) {
        return this.section != null && !this.section.equals(originalStudentAttribute.section);
    }

    /**
     * Returns true if team value has changed from its original value.
     */
    public boolean isTeamChanged(StudentAttributes originalStudentAttribute) {
        return this.team != null && !this.team.equals(originalStudentAttribute.team);
    }

    /**
     * Returns true if email value has changed from its original value.
     */
    public boolean isEmailChanged(StudentAttributes originalStudentAttribute) {
        return this.email != null && !this.email.equals(originalStudentAttribute.email);
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
     * A Builder class for {@link StudentAttributes}.
     */
    public static class Builder {
        private static final String REQUIRED_FIELD_CANNOT_BE_NULL = "Required field cannot be null";

        private final StudentAttributes studentAttributes;

        public Builder(String courseId, String name, String email) {
            studentAttributes = new StudentAttributes();

            Assumption.assertNotNull(REQUIRED_FIELD_CANNOT_BE_NULL, courseId, name, email);

            studentAttributes.course = courseId;
            studentAttributes.name = SanitizationHelper.sanitizeName(name);
            studentAttributes.email = email;
            studentAttributes.lastName = processLastName(null);
        }

        public Builder withGoogleId(String googleId) {
            if (googleId != null) {
                studentAttributes.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
            }

            return this;
        }

        public Builder withLastName(String lastName) {
            studentAttributes.lastName = processLastName(lastName);
            return this;
        }

        private String processLastName(String lastName) {
            if (lastName != null) {
                return lastName;
            }

            if (Strings.isNullOrEmpty(studentAttributes.name)) {
                return "";
            }

            String[] nameParts = StringHelper.splitName(studentAttributes.name);
            return nameParts.length < 2 ? "" : SanitizationHelper.sanitizeName(nameParts[1]);
        }

        public Builder withComments(String comments) {
            studentAttributes.comments = SanitizationHelper.sanitizeTextField(comments);
            return this;
        }

        public Builder withTeam(String team) {
            if (team != null) {
                studentAttributes.team = team;
            }
            return this;
        }

        public Builder withSection(String section) {
            studentAttributes.section = section == null ? Const.DEFAULT_SECTION : section;
            return this;
        }

        public Builder withKey(String key) {
            if (key != null) {
                studentAttributes.key = key;
            }
            return this;
        }

        public Builder withUpdateStatus(StudentUpdateStatus updateStatus) {
            studentAttributes.updateStatus = updateStatus == null
                    ? StudentUpdateStatus.UNKNOWN
                    : updateStatus;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            Instant dateToAdd = createdAt == null
                    ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP
                    : createdAt;
            studentAttributes.setCreatedAt(dateToAdd);
            return this;
        }

        public Builder withUpdatedAt(Instant updatedAt) {
            Instant dateToAdd = updatedAt == null
                    ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP
                    : updatedAt;
            studentAttributes.setUpdatedAt(dateToAdd);
            return this;
        }

        public StudentAttributes build() {
            return studentAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link StudentAttributes}.
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
        public static class Builder {
            private UpdateOptions updateOptions;

            private Builder(String courseId, String email) {
                updateOptions = new UpdateOptions(courseId, email);
            }

            public Builder withNewEmail(String email) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, email);

                updateOptions.newEmailOption = UpdateOption.of(email);
                return this;
            }

            public Builder withName(String name) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, name);

                updateOptions.nameOption = UpdateOption.of(name);
                return this;
            }

            public Builder withLastName(String name) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, name);

                updateOptions.lastNameOption = UpdateOption.of(name);
                return this;
            }

            public Builder withComment(String comment) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, comment);

                updateOptions.commentOption = UpdateOption.of(comment);
                return this;
            }

            public Builder withGoogleId(String googleId) {
                // google id can be set to null
                updateOptions.googleIdOption = UpdateOption.of(googleId);
                return this;
            }

            public Builder withTeamName(String teamName) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, teamName);

                updateOptions.teamNameOption = UpdateOption.of(teamName);
                return this;
            }

            public Builder withSectionName(String sectionName) {
                Assumption.assertNotNull(Const.StatusCodes.UPDATE_OPTIONS_NULL_INPUT, sectionName);

                updateOptions.sectionNameOption = UpdateOption.of(sectionName);
                return this;
            }

            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }
}
