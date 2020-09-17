package teammates.ui.request;

/**
 * The archive request of a course.
 */
public class CourseArchiveRequest extends BasicRequest {
    private boolean archiveStatus;

    @Override
    public void validate() {
        //nothing to validate
    }

    public boolean getArchiveStatus() {
        return archiveStatus;
    }

    public void setArchiveStatus(boolean archiveStatus) {
        this.archiveStatus = archiveStatus;
    }
}
