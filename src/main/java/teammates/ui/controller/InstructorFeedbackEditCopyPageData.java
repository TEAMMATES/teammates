package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;

public class InstructorFeedbackEditCopyPageData extends PageData {
    private List<CourseAttributes> courses;
    private String courseId;
    private String fsName;
    private String currentPage;
    
    public InstructorFeedbackEditCopyPageData(AccountAttributes account, List<CourseAttributes> courses,
                                              String courseId, String fsName, String currentPage) {
        super(account);
        this.courses = courses;
        this.courseId = courseId;
        this.fsName = fsName;
        this.currentPage = currentPage;
    }

    public List<CourseAttributes> getCourses() {
        return courses;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFsName() {
        return fsName;
    }
    
    public String getCurrentPage() {
        return currentPage;
    }
}
