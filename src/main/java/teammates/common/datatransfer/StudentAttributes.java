package teammates.common.datatransfer;

import static teammates.common.util.Const.EOL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Url;
import teammates.storage.entity.Student;

public class StudentAttributes extends EntityAttributes {
    public enum UpdateStatus {
        // @formatter:off
        ERROR(0),
        NEW(1),
        MODIFIED(2),
        UNMODIFIED(3),
        NOT_IN_ENROLL_LIST(4),
        UNKNOWN(5);
        // @formatter:on

        public static final int STATUS_COUNT = 6;
        public final int numericRepresentation;

        private UpdateStatus(int numericRepresentation) {
            this.numericRepresentation = numericRepresentation;
        }

        public static UpdateStatus enumRepresentation(int numericRepresentation) {
            switch (numericRepresentation) {
                case 0:
                    return ERROR;
                case 1:
                    return NEW;
                case 2:
                    return MODIFIED;
                case 3:
                    return UNMODIFIED;
                case 4:
                    return NOT_IN_ENROLL_LIST;
                default:
                    return UNKNOWN;
            }
        }
    }

    // @formatter:off
    // Note: be careful when changing these variables as their names are used in *.json files.
    // @formatter:on
    public String googleId;
    public String name;
    public String lastName;
    public String email;
    public String course = null;
    public String comments = null;
    public String team = null;
    public String section = null;
    public String key = null;

    public UpdateStatus updateStatus = UpdateStatus.UNKNOWN;

    public StudentAttributes(String id, String email, String name, String comments, String courseId,
                             String team, String section) {
        this(section, team, name, email, comments, courseId);
        this.googleId = Sanitizer.sanitizeGoogleId(id);
    }

    public StudentAttributes() {

    }

    public StudentAttributes(String section, String team, String name, String email, String comment,
                             String courseId) {
        this();
        this.section = Sanitizer.sanitizeTitle(section);
        this.team = Sanitizer.sanitizeTitle(team);
        this.lastName = Sanitizer.sanitizeName(StringHelper.splitName(name)[1]);
        this.name = Sanitizer.sanitizeName(name);
        this.email = Sanitizer.sanitizeEmail(email);
        this.comments = Sanitizer.sanitizeTextField(comment);
        this.course = Sanitizer.sanitizeTitle(courseId);
    }

    public StudentAttributes(Student student) {
        this();
        this.email = student.getEmail();
        this.course = student.getCourseId();
        this.name = student.getName();
        this.lastName = student.getLastName();
        this.comments = Sanitizer.sanitizeTextField(student.getComments());
        this.team = Sanitizer.sanitizeTitle(student.getTeamName());
        this.section = (student.getSectionName() == null) ? Const.DEFAULT_SECTION
                                                          : Sanitizer.sanitizeTitle(student.getSectionName());
        this.googleId = (student.getGoogleId() == null) ? ""
                                                        : student.getGoogleId();
        Long keyAsLong = student.getRegistrationKey();
        this.key = (keyAsLong == null) ? null : Student.getStringKeyForLongKey(keyAsLong);
        /*
         * TODO: this is for backward compatibility with old system.
         * Old system considers "" as unregistered.
         * It should be changed to consider null as unregistered.
         */
    }

    public String toEnrollmentString() {
        String enrollmentString = "";
        String enrollmentStringSeparator = "|";

        enrollmentString = this.section + enrollmentStringSeparator;
        enrollmentString += this.team + enrollmentStringSeparator;
        enrollmentString += this.name + enrollmentStringSeparator;
        enrollmentString += this.email + enrollmentStringSeparator;
        enrollmentString += this.comments;

        return enrollmentString;
    }

    public boolean isRegistered() {
        return googleId != null && !googleId.equals("");
    }

    public String getRegistrationUrl() {
        return new Url(Config.APP_URL + Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                                           .withRegistrationKey(StringHelper.encrypt(key))
                                           .withStudentEmail(email)
                                           .withCourseId(course)
                                           .toString();
    }

    public String getPublicProfilePictureUrl() {
        return new Url(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
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
        return (otherStudent != null) && otherStudent.email.equals(this.email)
                && otherStudent.course.equals(this.course)
                && otherStudent.name.equals(this.name)
                && otherStudent.comments.equals(this.comments)
                && otherStudent.team.equals(this.team)
                && otherStudent.section.equals(this.section);
    }

    public List<String> getInvalidityInfo() {
        // id is allowed to be null when the student is not registered
        Assumption.assertTrue(team != null);
        Assumption.assertTrue(comments != null);

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;

        if (isRegistered()) {
            error = validator.getInvalidityInfo(FieldType.GOOGLE_ID, googleId);

            if (!error.isEmpty()) {
                errors.add(error);
            }
        }

        error = validator.getInvalidityInfo(FieldType.COURSE_ID, course);

        if (!error.isEmpty()) { errors.add(error); }

        error = validator.getInvalidityInfo(FieldType.EMAIL, email);

        if (!error.isEmpty()) { errors.add(error); }

        error = validator.getInvalidityInfo(FieldType.TEAM_NAME, team);

        if (!error.isEmpty()) { errors.add(error); }

        error = validator.getInvalidityInfo(FieldType.SECTION_NAME, section);

        if (!error.isEmpty()) { errors.add(error); }

        error = validator.getInvalidityInfo(FieldType.STUDENT_ROLE_COMMENTS, comments);

        if (!error.isEmpty()) { errors.add(error); }

        error = validator.getInvalidityInfo(FieldType.PERSON_NAME, name);

        if (!error.isEmpty()) { errors.add(error); }

        return errors;
    }

    public static void sortBySectionName(List<StudentAttributes> students) {
        Collections.sort(students, new Comparator<StudentAttributes>() {
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

    public Student toEntity() {
        return new Student(email, name, googleId, comments, course, team, section);
    }

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
        return Utils.getTeammatesGson().toJson(this, StudentAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        googleId = Sanitizer.sanitizeGoogleId(googleId);
        email = Sanitizer.sanitizeEmail(email);
        course = Sanitizer.sanitizeTitle(course);
        name = Sanitizer.sanitizeName(name);
        team = Sanitizer.sanitizeTitle(team);
        section = Sanitizer.sanitizeTitle(section);
        comments = Sanitizer.sanitizeTextField(comments);
        googleId = Sanitizer.sanitizeForHtml(googleId);
        email = Sanitizer.sanitizeForHtml(email);
        course = Sanitizer.sanitizeForHtml(course);
        name = Sanitizer.sanitizeForHtml(name);
        team = Sanitizer.sanitizeForHtml(team);
        section = Sanitizer.sanitizeForHtml(section);
        comments = Sanitizer.sanitizeForHtml(comments);
    }
    
    public String getStudentStatus() {
        if (isRegistered()) {
            return Const.STUDENT_COURSE_STATUS_JOINED;
        } else {
            return Const.STUDENT_COURSE_STATUS_YET_TO_JOIN;
        }
    }
}
