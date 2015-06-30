package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;

public class InstructorFeedbackEditCopyPageData extends PageData {
    public List<CourseAttributes> courses;
    private String courseId;
    private String fsName;
    
    public InstructorFeedbackEditCopyPageData(AccountAttributes account) {
        super(account);
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFsName() {
        return fsName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setFsName(String fsName) {
        this.fsName = fsName;
    }
}
