package teammates.ui.template;

import teammates.common.util.Sanitizer;

public class InstructorStudentListStudentsTableCourse {

    private boolean courseArchived;
    private String courseId;
    private String courseName;
    private String googleId;
    private String instructorCourseEnrollLink;
    private boolean instructorAllowedToModify;

    public InstructorStudentListStudentsTableCourse(final boolean isCourseArchived, final String courseId, final String courseName,
                                                    final String googleId,
                                                    final String instructorCourseEnrollLink,
                                                    final boolean isInstructorAllowedToModify) {
        this.courseArchived = isCourseArchived;
        this.courseId = courseId;
        this.courseName = Sanitizer.sanitizeForHtml(courseName);
        this.googleId = googleId;
        this.instructorCourseEnrollLink = instructorCourseEnrollLink;
        this.instructorAllowedToModify = isInstructorAllowedToModify;
    }

    public boolean isCourseArchived() {
        return courseArchived;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getInstructorCourseEnrollLink() {
        return instructorCourseEnrollLink;
    }

    public boolean isInstructorAllowedToModify() {
        return instructorAllowedToModify;
    }

}
