package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;

public class InstructorFeedbackEditCopyPageData extends PageData {
    private List<CourseAttributes> courses;
    private String courseId;
    private String fsName;

    public InstructorFeedbackEditCopyPageData(AccountAttributes account, String sessionToken,
            List<CourseAttributes> courses, String courseId, String fsName) {
        super(account, sessionToken);
        this.courses = courses;
        this.courseId = courseId;
        this.fsName = fsName;
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
}
