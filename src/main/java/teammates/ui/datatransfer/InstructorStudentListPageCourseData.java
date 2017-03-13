package teammates.ui.datatransfer;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.ui.controller.InstructorStudentListPageAction;
import teammates.ui.pagedata.InstructorStudentListPageData;

/**
 * Serves as a datatransfer class between {@link InstructorStudentListPageAction}
 * and {@link InstructorStudentListPageData}.
 */
public class InstructorStudentListPageCourseData {

    public CourseAttributes course;
    public boolean isCourseArchived;
    public boolean isInstructorAllowedToModify;

    public InstructorStudentListPageCourseData(CourseAttributes course, boolean isCourseArchived,
                                        boolean isInstructorAllowedToModify) {
        this.course = course;
        this.isCourseArchived = isCourseArchived;
        this.isInstructorAllowedToModify = isInstructorAllowedToModify;
    }

}
