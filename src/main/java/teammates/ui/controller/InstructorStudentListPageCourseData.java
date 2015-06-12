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
    String instructorCourseEnrollLink;
    
    InstructorStudentListPageCourseData(CourseAttributes course, boolean isCourseArchived,
                                        boolean isInstructorAllowedToModify,
                                        String instructorCourseEnrollLink) {
        this.course = course;
        this.isCourseArchived = isCourseArchived;
        this.isInstructorAllowedToModify = isInstructorAllowedToModify;
        this.instructorCourseEnrollLink = instructorCourseEnrollLink;
    }

}
