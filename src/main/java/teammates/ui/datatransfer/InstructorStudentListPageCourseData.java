package teammates.ui.datatransfer;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * Serves as a datatransfer class between {@link teammates.ui.controller.InstructorStudentListPageAction}
 * and {@link teammates.ui.pagedata.InstructorStudentListPageData}.
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
