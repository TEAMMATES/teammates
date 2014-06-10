package teammates.common.datatransfer;

import java.util.HashMap;
import teammates.common.util.Const;

public final class InstructorPrivileges {
    private HashMap<String, Boolean> courseLevel;
    private HashMap<String, HashMap<String, Boolean>> sectionLevel;
    private HashMap<String, HashMap<String, HashMap<String, Boolean>>> sessionLevel;
    
    public InstructorPrivileges() {
        this.courseLevel = new HashMap<String, Boolean>();
        this.sectionLevel = new HashMap<String, HashMap<String, Boolean>>();
        this.sessionLevel = new HashMap<String, HashMap<String, HashMap<String, Boolean>>>();
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
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, true);
    }
    
    public void setDefaultPrivilegesForManager() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, true);
    }
    
    public void setDefaultPrivilegesForObserver() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, false);
    }
    
    public void setDefaultPrivilegesForTutor() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, false);
    }
    
    public void setDefaultPrivilegesForHelper() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, false);
    }
    
    public void updatePrivilegeInCourseLevel(String privilegeName, boolean isAllowed) {
        this.courseLevel.put(privilegeName, isAllowed);
    }
    
    /**
     * add privilege with privilegeName to sectionId if sectionId exists in record,
     * or update the privilege with privilegeName under sectionId
     * @param sectionId
     * @param privilegeName
     * @param isAllowed
     */
    public void addPrivilegeInSectionLevel(String sectionId, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSectionLevel(sectionId, privilegeName, isAllowed);
    }
    
    /**
     * add privilege with privilegeName to sectionId if sectionId exists in record,
     * or update the privilege with privilegeName under sectionId
     * @param sectionId
     * @param privilegeName
     * @param isAllowed
     */
    public void updatePrivilegeInSectionLevel(String sectionId, String privilegeName, boolean isAllowed) {
        if (!this.sectionLevel.containsKey(sectionId)) {
            sectionLevel.put(sectionId, new HashMap<String, Boolean>());
        }
        sectionLevel.get(sectionId).put(privilegeName, isAllowed);
    }
    
    public void addPrivilegesInSectionLevel(String sectionId, HashMap<String, Boolean> privileges) {
        updatePrivilegesInSectionLevel(sectionId, privileges);
    }
    
    public void updatePrivilegesInSectionLevel(String sectionId, HashMap<String, Boolean> privileges) {
        sectionLevel.put(sectionId, privileges);
    }
    
    public void addPrivilegeInSessionLevel(String sectionId, String sessionId, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSessionLevel(sectionId, sessionId, privilegeName, isAllowed);
    }
    
    public void updatePrivilegeInSessionLevel(String sectionId, String sessionId, String privilegeName, boolean isAllowed) {
        if (!this.sessionLevel.containsKey(sectionId)) {
            this.sessionLevel.put(sectionId, new HashMap<String, HashMap<String, Boolean>>());
        }
        if (!this.sessionLevel.get(sectionId).containsKey(sessionId)) {
            this.sessionLevel.get(sectionId).put(sessionId, new HashMap<String, Boolean>());
        }
        this.sessionLevel.get(sectionId).get(sessionId).put(privilegeName, isAllowed);
    }
    
    public void addPrivilegesInSessionLevel(String sectionId, String sessionId, HashMap<String, Boolean> privileges) {
        updatePrivilegesInSessionLevel(sectionId, sessionId, privileges);
    }
    
    public void updatePrivilegesInSessionLevel(String sectionId, String sessionId, HashMap<String, Boolean> privileges) {
        if (!this.sessionLevel.containsKey(sectionId)) {
            this.sessionLevel.put(sectionId, new HashMap<String, HashMap<String, Boolean>>());
        }
        this.sessionLevel.get(sectionId).put(sessionId, privileges);
    }
    
    /**
     * @param privilegeName
     * @return true if the value for privilegeName is true
     */
    public boolean isAllowedInCourseLevel(String privilegeName) {
        if (!this.courseLevel.containsKey(privilegeName)) {
            return false;
        } else {
            return this.courseLevel.get(privilegeName).booleanValue();
        }
    }
    
    public boolean isAllowedInSectionLevel(String sectionId, String privilegeName) {
        if (!this.sectionLevel.containsKey(sectionId)) {
            return isAllowedInCourseLevel(privilegeName);
        }
        if (!this.sectionLevel.get(sectionId).containsKey(privilegeName)) {
            return false;
        } else {
            return this.sectionLevel.get(sectionId).get(privilegeName).booleanValue();
        }
    }
    
    public boolean isAllowedInSessionLevel(String sectionId, String sessionId, String privilegeName) {
        if (!this.sessionLevel.containsKey(sectionId)) {
            return isAllowedInCourseLevel(privilegeName);
        }
        if (!this.sessionLevel.get(sectionId).containsKey(sessionId)) {
            isAllowedInSectionLevel(sectionId, privilegeName);
        }
        if (!this.sessionLevel.get(sectionId).get(sessionId).containsKey(privilegeName)) {
            return false;
        } else {
            return this.sessionLevel.get(sectionId).get(sessionId).get(privilegeName).booleanValue();
        }
    }
    
    public HashMap<String, Boolean> getCourseLevelPrivileges() {
        return courseLevel;
    }

    public HashMap<String, HashMap<String, Boolean>> getSectionLevelPrivileges() {
        return sectionLevel;
    }

    public HashMap<String, HashMap<String, HashMap<String, Boolean>>> getSessionLevelPrivileges() {
        return sessionLevel;
    }
    
    public HashMap<String, Boolean> getSectionLevelOverallPrivileges() {
        HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
        
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION));
        
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        return privileges;
    }
    
    public HashMap<String, Boolean> getSessionLevelOverallPrivilegesForSection(String sectionId) {
        HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
        
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, 
                isAllowedInSectionLevel(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, 
                isAllowedInSectionLevel(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, 
                isAllowedInSectionLevel(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        return privileges;
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
