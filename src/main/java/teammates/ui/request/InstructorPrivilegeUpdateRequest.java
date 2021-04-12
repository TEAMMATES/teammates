package teammates.ui.request;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import teammates.common.util.Const;

/**
 * The update request for instructor privilege.
 */
public class InstructorPrivilegeUpdateRequest extends BasicRequest {
    @Nullable
    private String sectionName;

    @Nullable
    private String feedbackSessionName;

    @Nullable
    private Boolean canModifyCourse;
    @Nullable
    private Boolean canModifySession;
    @Nullable
    private Boolean canModifyStudent;
    @Nullable
    private Boolean canModifyInstructor;

    @Nullable
    private Boolean canViewStudentInSections;

    @Nullable
    private Boolean canModifySessionCommentsInSections;
    @Nullable
    private Boolean canViewSessionInSections;
    @Nullable
    private Boolean canSubmitSessionInSections;

    @Override
    public void validate() {
        if (feedbackSessionName != null) {
            assertTrue(sectionName != null, "session must comes with a section");
        }

        if (sectionName != null) {
            // course level privileges should not be present.
            boolean isNoneCourseLevelPrivilegesPresent = this.canModifyCourse == null
                    && this.canModifySession == null
                    && this.canModifyStudent == null
                    && this.canModifyInstructor == null;

            assertTrue(isNoneCourseLevelPrivilegesPresent, "only section and session level privileges.");

            if (feedbackSessionName != null) {
                // only session level privileges should be present.
                boolean isNoneSectionLevelPrivilegePresent = this.canViewStudentInSections == null;
                assertTrue(isNoneSectionLevelPrivilegePresent, "only session level privileges.");
            }
        }

        boolean isAnyPrivilegesToUpdate = this.canModifyCourse != null
                || this.canModifyStudent != null
                || this.canModifyInstructor != null
                || this.canModifySession != null
                || this.canViewStudentInSections != null
                || this.canSubmitSessionInSections != null
                || this.canViewSessionInSections != null
                || this.canModifySessionCommentsInSections != null;

        assertTrue(isAnyPrivilegesToUpdate, "should have at least one privileges present to update");
    }

    @Nullable
    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    @Nullable
    public String getSectionName() {
        return sectionName;
    }

    public Boolean isCanViewStudentInSections() {
        return canViewStudentInSections;
    }

    public Boolean isCanViewSessionInSections() {
        return canViewSessionInSections;
    }

    public Boolean isCanSubmitSessionInSections() {
        return canSubmitSessionInSections;
    }

    public Boolean isCanModifyStudent() {
        return canModifyStudent;
    }

    public Boolean isCanModifySessionCommentsInSections() {
        return canModifySessionCommentsInSections;
    }

    public Boolean isCanModifySession() {
        return canModifySession;
    }

    public Boolean isCanModifyInstructor() {
        return canModifyInstructor;
    }

    public Boolean isCanModifyCourse() {
        return canModifyCourse;
    }

    /**
     * Gets all present general privileges as a map.
     */
    public Map<String, Boolean> getAllPresentCourseLevelPrivileges() {
        Map<String, Boolean> privilegesMap = new HashMap<>();
        if (this.canModifyCourse != null) {
            privilegesMap.put(Const.InstructorPermissions.CAN_MODIFY_COURSE, this.canModifyCourse);
        }
        if (this.canModifySession != null) {
            privilegesMap.put(Const.InstructorPermissions.CAN_MODIFY_SESSION, this.canModifySession);
        }
        if (this.canModifyStudent != null) {
            privilegesMap.put(Const.InstructorPermissions.CAN_MODIFY_STUDENT, this.canModifyStudent);
        }
        if (this.canModifyInstructor != null) {
            privilegesMap.put(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, this.canModifyInstructor);
        }
        return privilegesMap;
    }

    /**
     * Gets all present section level privileges as a map.
     */
    public Map<String, Boolean> getAllPresentSectionLevelPrivileges() {
        Map<String, Boolean> privilegesMap = new HashMap<>();
        if (this.canViewStudentInSections != null) {
            privilegesMap.put(
                    Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, this.canViewStudentInSections);
        }
        return privilegesMap;
    }

    /**
     * Gets all present session level privileges as a map.
     */
    public Map<String, Boolean> getAllPresentSessionLevelPrivileges() {
        Map<String, Boolean> privilegesMap = new HashMap<>();
        if (this.canSubmitSessionInSections != null) {
            privilegesMap.put(
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, this.canSubmitSessionInSections);
        }
        if (this.canViewSessionInSections != null) {
            privilegesMap.put(
                    Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, this.canViewSessionInSections);
        }
        if (this.canModifySessionCommentsInSections != null) {
            privilegesMap.put(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                    this.canModifySessionCommentsInSections);
        }
        return privilegesMap;
    }

    public void setSectionName(@Nullable String sectionName) {
        this.sectionName = sectionName;
    }

    public void setFeedbackSessionName(@Nullable String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public void setCanViewStudentInSections(@Nullable Boolean canViewStudentInSections) {
        this.canViewStudentInSections = canViewStudentInSections;
    }

    public void setCanViewSessionInSections(@Nullable Boolean canViewSessionInSections) {
        this.canViewSessionInSections = canViewSessionInSections;
    }

    public void setCanModifySessionCommentsInSections(@Nullable Boolean canModifySessionCommentsInSections) {
        this.canModifySessionCommentsInSections = canModifySessionCommentsInSections;
    }

    public void setCanModifyInstructor(@Nullable Boolean canModifyInstructor) {
        this.canModifyInstructor = canModifyInstructor;
    }

    public void setCanModifyCourse(@Nullable Boolean canModifyCourse) {
        this.canModifyCourse = canModifyCourse;
    }

    public void setCanModifySession(@Nullable Boolean canModifySession) {
        this.canModifySession = canModifySession;
    }

    public void setCanModifyStudent(@Nullable Boolean canModifyStudent) {
        this.canModifyStudent = canModifyStudent;
    }

    public void setCanSubmitSessionInSections(@Nullable Boolean canSubmitSessionInSections) {
        this.canSubmitSessionInSections = canSubmitSessionInSections;
    }
}
