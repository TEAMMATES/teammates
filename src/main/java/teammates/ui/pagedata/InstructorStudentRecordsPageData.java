package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.ui.template.StudentProfile;

public class InstructorStudentRecordsPageData extends PageData {
    public static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";

    public StudentProfileAttributes spa; // used for testing admin message
    private String courseId;
    private String studentName;
    private String studentEmail;
    private StudentProfile studentProfile;
    private List<String> sessionNames;

    public InstructorStudentRecordsPageData(AccountAttributes account, StudentAttributes student, String sessionToken,
                                            String courseId, StudentProfileAttributes spa,
                                            List<String> sessionNames) {
        super(account, student, sessionToken);
        this.courseId = courseId;
        this.studentName = student.name;
        this.studentEmail = student.email;
        if (spa != null) {
            this.spa = spa;
            String pictureUrl = getPictureUrl(spa.pictureKey);
            this.studentProfile = new StudentProfile(student.name, spa, pictureUrl);
        }
        this.sessionNames = sessionNames;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getGoogleId() {
        return account.googleId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public List<String> getSessionNames() {
        return sessionNames;
    }

}
