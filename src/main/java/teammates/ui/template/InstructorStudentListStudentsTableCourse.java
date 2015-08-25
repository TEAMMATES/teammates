package teammates.ui.template;

import teammates.common.util.Sanitizer;

public class InstructorStudentListStudentsTableCourse {

    private boolean courseArchived;
    private String courseId;
    private String courseName;
    private String googleId;
    private String numStudents;
    private String instructorCourseEnrollLink;
    private boolean instructorAllowedToModify;

    public InstructorStudentListStudentsTableCourse(boolean isCourseArchived, String courseId, String courseName,
                                                    String googleId, String numStudents,
                                                    String instructorCourseEnrollLink,
                                                    boolean isInstructorAllowedToModify) {
        this.courseArchived = isCourseArchived;
        this.courseId = courseId;
        this.courseName = Sanitizer.sanitizeForHtml(courseName);
        this.googleId = googleId;
        this.numStudents = numStudents;
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

    public String getNumStudents() {
        return numStudents;
    }

    public String getInstructorCourseEnrollLink() {
        return instructorCourseEnrollLink;
    }

    public boolean isInstructorAllowedToModify() {
        return instructorAllowedToModify;
    }

}
