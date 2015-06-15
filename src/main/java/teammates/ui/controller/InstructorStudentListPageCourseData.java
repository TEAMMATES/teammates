package teammates.ui.controller;

import teammates.common.datatransfer.CourseAttributes;

/**
 * Serves as a datatransfer class between {@link InstructorStudentListPageAction}
 * and {@link InstructorStudentListPageData}
 */
public class InstructorStudentListPageCourseData {

    CourseAttributes course;
    boolean isCourseArchived;
    boolean isInstructorAllowedToModify;
    
    InstructorStudentListPageCourseData(CourseAttributes course, boolean isCourseArchived,
                                        boolean isInstructorAllowedToModify) {
        this.course = course;
        this.isCourseArchived = isCourseArchived;
        this.isInstructorAllowedToModify = isInstructorAllowedToModify;
    }

}
