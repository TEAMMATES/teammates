package teammates.common.datatransfer.attributes;

import static teammates.common.util.Const.EOL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

    // Note: be careful when changing these variables as their names are used in *.json files.
    public String googleId;
    public String email;
    public String course;
    public String name;
    public String lastName;
    public String comments;
    public String team;
    public String section;
    public String key;

    public transient StudentUpdateStatus updateStatus = StudentUpdateStatus.UNKNOWN;

    /*
     * Creation and update time stamps.
     * Updated automatically in Student.java, jdoPreStore()
     */
    protected transient Date createdAt;
    protected transient Date updatedAt;

    public StudentAttributes(String id, String email, String name, String comments, String courseId,
                             String team, String section) {
        this(section, team, name, email, comments, courseId);
        this.googleId = SanitizationHelper.sanitizeGoogleId(id);
    }

    public StudentAttributes() {
        // attributes to be set after construction
    }

    public StudentAttributes(String section, String team, String name, String email, String comment,
                             String courseId) {
        this();
        this.section = section;
        this.team = team;
        this.lastName = SanitizationHelper.sanitizeName(StringHelper.splitName(name)[1]);
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = email;
        this.comments = SanitizationHelper.sanitizeTextField(comment);
        this.course = courseId;
    }

    public StudentAttributes(CourseStudent student) {
        this();
        this.email = student.getEmail();
        this.course = student.getCourseId();
        this.name = student.getName();
        this.lastName = student.getLastName();
        this.comments = SanitizationHelper.sanitizeTextField(student.getComments());
        this.team = student.getTeamName();
        this.section = student.getSectionName() == null ? Const.DEFAULT_SECTION : student.getSectionName();
        this.googleId = student.getGoogleId() == null ? "" : student.getGoogleId();
        this.key = student.getRegistrationKey();

        this.createdAt = student.getCreatedAt();
        this.updatedAt = student.getUpdatedAt();

    }

    private StudentAttributes(StudentAttributes other) {
        this(other.googleId, other.email, other.name, other.comments,
             other.course, other.team, other.section);
        this.key = other.key;
        this.updateStatus = other.updateStatus;
    }

    public StudentAttributes getCopy() {
        return new StudentAttributes(this);
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
        return googleId != null && !googleId.isEmpty();
    }

    public String getRegistrationUrl() {
        return Config.getAppUrl(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                                           .withRegistrationKey(StringHelper.encrypt(key))
                                           .withStudentEmail(email)
                                           .withCourseId(course)
                                           .toString();
    }

    public String getPublicProfilePictureUrl() {
        return Config.getAppUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                           .withStudentEmail(StringHelper.encrypt(email))
                           .withCourseId(StringHelper.encrypt(course))
                           .toString();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
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
        Assumption.assertTrue(team != null);
        Assumption.assertTrue(comments != null);

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();

        if (isRegistered()) {
            addNonEmptyError(validator.getInvalidityInfoForGoogleId(googleId), errors);
        }

        addNonEmptyError(validator.getInvalidityInfoForCourseId(course), errors);

        addNonEmptyError(validator.getInvalidityInfoForEmail(email), errors);

        addNonEmptyError(validator.getInvalidityInfoForTeamName(team), errors);

        addNonEmptyError(validator.getInvalidityInfoForSectionName(section), errors);

        addNonEmptyError(validator.getInvalidityInfoForStudentRoleComments(comments), errors);

        addNonEmptyError(validator.getInvalidityInfoForPersonName(name), errors);

        return errors;
    }

    public static void sortBySectionName(List<StudentAttributes> students) {
        Collections.sort(students, new Comparator<StudentAttributes>() {
            @Override
            public int compare(StudentAttributes student1, StudentAttributes student2) {
                String sect1 = student1.section;
                String sect2 = student2.section;

                // If the section name is the same, reorder by team name
                if (sect1.compareTo(sect2) == 0) {
                    if (student1.team.compareTo(student2.team) == 0) {
                        return student1.name.compareTo(student2.name);
                    }

                    return student1.team.compareTo(student2.team);
                }

                return sect1.compareTo(sect2);
            }
        });
    }

    public static void sortByTeamName(List<StudentAttributes> students) {
        Collections.sort(students, new Comparator<StudentAttributes>() {
            @Override
            public int compare(StudentAttributes student1, StudentAttributes student2) {
                String team1 = student1.team;
                String team2 = student2.team;

                // If the team name is the same, reorder by student name
                if (team1.compareTo(team2) == 0) {
                    return student1.name.compareTo(student2.name);
                }

                return team1.compareTo(team2);
            }
        });
    }

    public static void sortByNameAndThenByEmail(List<StudentAttributes> students) {
        Collections.sort(students, new Comparator<StudentAttributes>() {
            @Override
            public int compare(StudentAttributes student1, StudentAttributes student2) {
                int result = student1.name.compareTo(student2.name);

                if (result == 0) {
                    result = student1.email.compareTo(student2.email);
                }

                return result;
            }
        });
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
        sb.append(indentString + "Student:" + name + "[" + email + "]" + EOL);

        return sb.toString();
    }

    @Override
    public String getIdentificationString() {
        return this.course + "/" + this.email;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Student";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + course;
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

    public Date getCreatedAt() {
        return createdAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : updatedAt;
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
}
