package teammates.ui.webapi.request;

/**
 * The archive request of a course.
 */
public class CourseArchiveRequest extends BasicRequest {
    private String archiveStatus;

    @Override
    public void validate() {
        assertTrue("true".equals(archiveStatus) || "false".equals(archiveStatus),
                "Archive status should be either true or false.");
    }

    public String getArchiveStatus() {
        return archiveStatus;
    }

    public void setArchiveStatus(String archiveStatus) {
        this.archiveStatus = archiveStatus;
    }
}
