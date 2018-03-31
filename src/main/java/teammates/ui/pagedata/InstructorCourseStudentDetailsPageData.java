package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.ui.template.StudentInfoTable;
import teammates.ui.template.StudentProfile;

public class InstructorCourseStudentDetailsPageData extends PageData {

    private StudentProfile studentProfile;
    private StudentInfoTable studentInfoTable;

    public InstructorCourseStudentDetailsPageData(AccountAttributes account, String sessionToken, StudentAttributes student,
            StudentProfileAttributes studentProfile, boolean hasSection) {
        super(account, sessionToken);
        if (studentProfile != null) {
            String pictureUrl = getPictureUrl(studentProfile.pictureKey);
            this.studentProfile = new StudentProfile(student.name, studentProfile, pictureUrl);
        }
        this.studentInfoTable = new StudentInfoTable(student, hasSection);
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public StudentInfoTable getStudentInfoTable() {
        return studentInfoTable;
    }

}
