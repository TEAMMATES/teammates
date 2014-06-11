package teammates.common.datatransfer;

import java.util.HashMap;

import teammates.common.util.Const;

public final class InstructorPrivileges {
    private HashMap<String, Boolean> courseLevel;
    private HashMap<String, HashMap<String, Boolean>> sectionLevel;
    private HashMap<String, HashMap<String, HashMap<String, Boolean>>> sessionLevel;
    
    public static boolean isPrivilegeNameValid(String privilegeName) {
        boolean isValid = false;
        switch(privilegeName) {
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION:
            isValid = true;
            break;
        default:
            isValid = false;
            break;
        }
        
        return isValid;
    }
    
    public static boolean isPrivilegeNameValidForSectionLevel(String privilegeName) {
        boolean isValid = false;
        switch(privilegeName) {
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION:
            isValid = true;
            break;
        default:
            isValid = false;
            break;
        }
        
        return isValid;
    }
    
    public static boolean isPrivilegeNameValidForSessionLevel(String privilegeName) {
        boolean isValid = false;
        switch(privilegeName) {
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION:
            isValid = true;
            break;
        case Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION:
            isValid = true;
            break;
        default:
            isValid = false;
            break;
        }
        
        return isValid;
    }
    
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
    
    public HashMap<String, Boolean> getOverallPrivilegesForSections() {
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
    
    public HashMap<String, Boolean> getOverallPrivilegesForSessionsInSection(String sectionId) {
        HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
        
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION, 
                isAllowedInSectionLevel(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION, 
                isAllowedInSectionLevel(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTION));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION, 
                isAllowedInSectionLevel(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTION));
        
        return privileges;
    }
    
    public void updatePrivilege(String privilegeName, boolean isAllowed) {
        updatePrivilegeInCourseLevel(privilegeName, isAllowed);
    }
    
    public void updatePrivilege(String sectionId, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSectionLevel(sectionId, privilegeName, isAllowed);
    }
    
    public void updatePrivilege(String sectionId, String sessionId, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSessionLevel(sectionId, sessionId, privilegeName, isAllowed);
    }
    
    private void updatePrivilegeInCourseLevel(String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValid(privilegeName)) {
            return ;
        }
        this.courseLevel.put(privilegeName, isAllowed);
    }
    
    private void updatePrivilegeInSectionLevel(String sectionId, String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValidForSectionLevel(privilegeName)) {
            return ;
        }
        if (!this.sectionLevel.containsKey(sectionId)) {
            sectionLevel.put(sectionId, new HashMap<String, Boolean>());
        }
        sectionLevel.get(sectionId).put(privilegeName, isAllowed);
    }
    
    private void updatePrivilegeInSessionLevel(String sectionId, String sessionId, String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValidForSessionLevel(privilegeName)) {
            return ;
        }
        verifyExistenceOfSectionId(sectionId);
        if (!this.sessionLevel.get(sectionId).containsKey(sessionId)) {
            this.sessionLevel.get(sectionId).put(sessionId, new HashMap<String, Boolean>());
        }
        this.sessionLevel.get(sectionId).get(sessionId).put(privilegeName, isAllowed);
    }
    
    public void updatePrivileges(String sectionId, HashMap<String, Boolean> privileges) {
        updatePrivilegesInSectionLevel(sectionId, privileges);
    }
    
    public void updatePrivileges(String sectionId, String sessionId, HashMap<String, Boolean> privileges) {
        updatePrivilegesInSessionLevel(sectionId, sessionId, privileges);
    }
    
    @SuppressWarnings("unchecked")
    private void updatePrivilegesInSectionLevel(String sectionId, HashMap<String, Boolean> privileges) {
        for (String privilegeName : privileges.keySet()) {
            if (!isPrivilegeNameValidForSectionLevel(privilegeName)) {
                return ;
            }
        }
        sectionLevel.put(sectionId, (HashMap<String, Boolean>) privileges.clone());
    }
    
    @SuppressWarnings("unchecked")
    private void updatePrivilegesInSessionLevel(String sectionId, String sessionId, HashMap<String, Boolean> privileges) {
        for (String privilegeName : privileges.keySet()) {
            if (!isPrivilegeNameValidForSessionLevel(privilegeName)) {
                return ;
            }
        }
        verifyExistenceOfSectionId(sectionId);
        this.sessionLevel.get(sectionId).put(sessionId, (HashMap<String, Boolean>) privileges.clone());
    }

    private void verifyExistenceOfSectionId(String sectionId) {
        if (!this.sessionLevel.containsKey(sectionId)) {
            addSectionWithDefaultPrivileges(sectionId);
            this.sessionLevel.put(sectionId, new HashMap<String, HashMap<String, Boolean>>());
        }
    }
    
    public void addSectionWithDefaultPrivileges(String sectionId) {
        if (this.sectionLevel.containsKey(sectionId)) {
            return ;
        } else {
            this.sectionLevel.put(sectionId, getOverallPrivilegesForSections());
        }
    }
    
    public void addSessionWithDefaultPrivileges(String sectionId, String sessionId) {
        verifyExistenceOfSectionId(sectionId);
        if (this.sessionLevel.get(sectionId).containsKey(sessionId)) {
            return ;
        } else {
            this.sessionLevel.get(sectionId).put(sessionId, getOverallPrivilegesForSessionsInSection(sectionId));
        }
    }
    
    public boolean isAllowedForPrivilege(String privilegeName) {
        return isAllowedInCourseLevel(privilegeName);
    }
    
    public boolean isAllowedForPrivilege(String sectionId, String privilegeName) {
        return isAllowedInSectionLevel(sectionId, privilegeName);
    }
    
    public boolean isAllowedForPrivilege(String sectionId, String sessionId, String privilegeName) {
        return isAllowedInSessionLevel(sectionId, sessionId, privilegeName);
    }
    
    private boolean isAllowedInCourseLevel(String privilegeName) {
        if (!this.courseLevel.containsKey(privilegeName)) {
            return false;
        } else {
            return this.courseLevel.get(privilegeName).booleanValue();
        }
    }
    
    private boolean isAllowedInSectionLevel(String sectionId, String privilegeName) {
        if (!this.sectionLevel.containsKey(sectionId)) {
            return isAllowedInCourseLevel(privilegeName);
        }
        if (!this.sectionLevel.get(sectionId).containsKey(privilegeName)) {
            return false;
        } else {
            return this.sectionLevel.get(sectionId).get(privilegeName).booleanValue();
        }
    }
    
    private boolean isAllowedInSessionLevel(String sectionId, String sessionId, String privilegeName) {
        if (!this.sessionLevel.containsKey(sectionId)
                || !this.sessionLevel.get(sectionId).containsKey(sessionId)) {
            return isAllowedInSectionLevel(sectionId, privilegeName);
        }
        if (!this.sessionLevel.get(sectionId).get(sessionId).containsKey(privilegeName)) {
            return false;
        } else {
            return this.sessionLevel.get(sectionId).get(sessionId).get(privilegeName).booleanValue();
        }
    }
    
    @SuppressWarnings("unchecked")
    public HashMap<String, Boolean> getCourseLevelPrivileges() {
        return (HashMap<String, Boolean>) courseLevel.clone();
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, HashMap<String, Boolean>> getSectionLevelPrivileges() {
        return (HashMap<String, HashMap<String, Boolean>>) sectionLevel.clone();
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, HashMap<String, HashMap<String, Boolean>>> getSessionLevelPrivileges() {
        return (HashMap<String, HashMap<String, HashMap<String, Boolean>>>) sessionLevel.clone();
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
