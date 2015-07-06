package teammates.ui.template;

import teammates.common.util.Const;

public class InstructorStudentRecordsMoreInfoModal {

    private String studentName;
    private String moreInfo;

    public InstructorStudentRecordsMoreInfoModal(String studentName, String moreInfo) {
        this.studentName = studentName;
        this.moreInfo = moreInfo.isEmpty() ? "<i class=\"text-muted\">"
                                                   + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>"
                                           : moreInfo;
    }
    
    public String getStudentName() {
        return studentName;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

}
