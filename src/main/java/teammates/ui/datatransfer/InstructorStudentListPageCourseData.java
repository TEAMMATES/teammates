package teammates.ui.datatransfer;

import teammates.common.datatransfer.CourseAttributes;

/**
 * Serves as a datatransfer class between {@link InstructorStudentListPageAction}
 * and {@link InstructorStudentListPageData}
 */
public class InstructorStudentListPageCourseData {

    public CourseAttributes course;
    public boolean isCourseArchived;
    public boolean isInstructorAllowedToModify;
    
    public InstructorStudentListPageCourseData(final CourseAttributes course, final boolean isCourseArchived,
                                        final boolean isInstructorAllowedToModify) {
        this.course = course;
        this.isCourseArchived = isCourseArchived;
        this.isInstructorAllowedToModify = isInstructorAllowedToModify;
    }

}
