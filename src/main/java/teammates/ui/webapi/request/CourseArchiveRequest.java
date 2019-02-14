package teammates.ui.webapi.request;

/**
 * The delete request to archive a course.
 */
public class CourseArchiveRequest extends BasicRequest {
    private String courseId;
    private String archiveStatus;

    @Override
    public void validate() {
        assertTrue(courseId != null, "Course ID should not be null");
        assertTrue("true".equals(archiveStatus) || "false".equals(archiveStatus),
                "Archive status should be either true or false.");
    }

    public String getCourseId() {
        return courseId;
    }

    public String getArchiveStatus() {
        return archiveStatus;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setArchiveStatus(String archiveStatus) {
        this.archiveStatus = archiveStatus;
    }
}
