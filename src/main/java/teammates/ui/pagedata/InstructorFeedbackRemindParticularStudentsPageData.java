package teammates.ui.pagedata;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.attributes.AccountAttributes;

public class InstructorFeedbackRemindParticularStudentsPageData extends PageData {
    private FeedbackSessionResponseStatus responseStatus;
    private String courseId;
    private String fsName;

    public InstructorFeedbackRemindParticularStudentsPageData(
                AccountAttributes account, FeedbackSessionResponseStatus responseStatus,
                String courseId, String fsName) {
        super(account);
        this.responseStatus = responseStatus;
        this.courseId = courseId;
        this.fsName = fsName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFsName() {
        return fsName;
    }

    public FeedbackSessionResponseStatus getResponseStatus() {
        return responseStatus;
    }
}
