package teammates.ui.webapi.request;

import javax.annotation.Nullable;

/**
 * The update request for instructor privilege.
 */
public class InstructorPrivilegeUpdateRequest extends BasicRequest {

    @Nullable
    private String sectionName;

    @Nullable
    private String feedbackSessionName;

    private boolean canModifyCourse;
    private boolean canModifySession;
    private boolean canModifyStudent;
    private boolean canModifyInstructor;

    private boolean canViewStudentInSections;

    private boolean canModifySessionCommentsInSections;
    private boolean canViewSessionInSections;
    private boolean canSubmitSessionInSections;

    @Override
    public void validate() {
        if (feedbackSessionName != null) {
            assertTrue(sectionName != null, "session must comes with a section");
        }
    }

    @Nullable
    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    @Nullable
    public String getSectionName() {
        return sectionName;
    }

    public boolean isCanViewStudentInSections() {
        return canViewStudentInSections;
    }

    public boolean isCanViewSessionInSections() {
        return canViewSessionInSections;
    }

    public boolean isCanSubmitSessionInSections() {
        return canSubmitSessionInSections;
    }

    public boolean isCanModifyStudent() {
        return canModifyStudent;
    }

    public boolean isCanModifySessionCommentsInSections() {
        return canModifySessionCommentsInSections;
    }

    public boolean isCanModifySession() {
        return canModifySession;
    }

    public boolean isCanModifyInstructor() {
        return canModifyInstructor;
    }

    public boolean isCanModifyCourse() {
        return canModifyCourse;
    }
}
