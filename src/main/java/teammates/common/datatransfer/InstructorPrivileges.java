package teammates.common.datatransfer;

import java.util.HashMap;
import teammates.common.util.Const;

public final class InstructorPrivileges {
    private HashMap<String, Boolean> courseLevel;
    private HashMap<String, Boolean> sectionRecord;
    private HashMap<String, Boolean> sectionLevel;
    private HashMap<String, HashMap<String, Boolean>> sessionLevel;
    
    public InstructorPrivileges() {
        this.courseLevel = new HashMap<String, Boolean>();
        this.sectionRecord = new HashMap<String, Boolean>();
        this.sectionLevel = new HashMap<String, Boolean>();
        this.sessionLevel = new HashMap<String, HashMap<String, Boolean>>();
    }
    
    public InstructorPrivileges(String instrRole) {
        this();
        switch (instrRole) {
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
            setDefaultPrivilegesForCoowner();
            break;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
            setDefaultPrivilegesForManager();
            break;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
            setDefaultPrivilegesForObserver();
            break;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
            setDefaultPrivilegesForTutor();
            break;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_HELPER:
            setDefaultPrivilegesForHelper();
            break;
        default:
            setDefaultPrivilegesForHelper();
            break;
        }
    }
    
    public void setDefaultPrivilegesForCoowner() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, Boolean.valueOf(true));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, Boolean.valueOf(true));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, Boolean.valueOf(true));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, Boolean.valueOf(true));
    }
    
    public void setDefaultPrivilegesForManager() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, Boolean.valueOf(true));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, Boolean.valueOf(true));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, Boolean.valueOf(true));
    }
    
    public void setDefaultPrivilegesForObserver() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, Boolean.valueOf(false));
    }
    
    public void setDefaultPrivilegesForTutor() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, Boolean.valueOf(true));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, Boolean.valueOf(false));
    }
    
    public void setDefaultPrivilegesForHelper() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, Boolean.valueOf(false));
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, Boolean.valueOf(false));
        this.sectionLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, Boolean.valueOf(false));
    }
    
    public void updatePrivilegeInCourseLevel(String privilegeName, boolean isAllowed) {
        this.courseLevel.put(privilegeName, Boolean.valueOf(isAllowed));
    }
    
    /**
     * This method will update the sectionId in sections record if sectionId exists
     * or add sectionId to sections record
     * @param sectionId
     * @param isSelected
     */
    public void addSectionToSectionRecord(String sectionId, boolean isSelected) {
        updateSectionInSectionRecord(sectionId, isSelected);      
    }
    
    public void updateSectionInSectionRecord(String sectionId, boolean isSelected) {
        this.sectionRecord.put(sectionId, Boolean.valueOf(isSelected));
    }
    
    public void updatePrivilegeInSectionLevel(String privilegeName, boolean isAllowed) {
        this.sectionLevel.put(privilegeName, Boolean.valueOf(isAllowed));
    }
    
    /**
     * add sessionId to sessionLevel with privileges configured in sectionLevel
     * @param sessionId
     */
    public void addSessionToSessionLevel(String sessionId) {        
        this.sessionLevel.put(sessionId, getPrivilegesForSessionsInSections());
    }
    
    public void addSessionToSessionLevel(String sessionId, HashMap<String, Boolean> privileges) {
        this.sessionLevel.put(sessionId, privileges);
    }
    
    public void updateSessionPrivilegeInSessionLevel(String sessionId, String privilegeName, boolean isAllowed) {
        if (!this.sessionLevel.containsKey(sessionId)) {
            addSessionToSessionLevel(sessionId);
        }
        this.sessionLevel.get(sessionId).put(privilegeName, Boolean.valueOf(isAllowed));
    }
    
    public HashMap<String, Boolean> getPrivilegesForSessionsInSections() {
        HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, 
                this.sectionLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, 
                this.sectionLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, 
                this.sectionLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        return privileges;
    }

    public HashMap<String, Boolean> getCourseLevelPrivileges() {
        return this.courseLevel;
    }
    
    public HashMap<String, Boolean> getSectionRecord() {
        return this.sectionRecord;
    }

    public HashMap<String, Boolean> getSectionLevelPrivileges() {
        return this.sectionLevel;
    }

    public HashMap<String, HashMap<String, Boolean>> getSessionLevelPrivileges() {
        return this.sessionLevel;
    }
    
    public boolean equals(Object another) {
        if (!(another instanceof InstructorPrivileges)) {
            return false;
        }
        if (another == this) {
            return true;
        }
        
        InstructorPrivileges rhs = (InstructorPrivileges)another;
        return this.getCourseLevelPrivileges().equals(rhs.getCourseLevelPrivileges()) &&
                this.getSectionLevelPrivileges().equals(rhs.getSectionLevelPrivileges()) &&
                this.getSessionLevelPrivileges().equals(rhs.getSessionLevelPrivileges());
    }
    
}
