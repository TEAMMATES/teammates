package teammates.common.datatransfer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import teammates.common.util.Const;

public final class InstructorPrivileges {
    private HashMap<String, Boolean> courseLevel;
    private HashMap<String, HashMap<String, Boolean>> sectionLevel;
    private HashMap<String, HashMap<String, HashMap<String, Boolean>>> sessionLevel;
    
    private static final String[] courseLevelOnlyList = new String[] {
        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE,
        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR,
        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION,
        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT
        };
    
    private static final String[] sectionLevelOnlyList = new String[] {
        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS,
        Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS,
        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS
        };
    
    private static final String[] sessionLevelOnlyList = new String[] {
        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
        };
    
    private static final HashSet<String> COURSE_LEVEL_ONLY_PRIVILEGES = new HashSet<String>(Arrays.asList(courseLevelOnlyList));
    private static final HashSet<String> SECTION_LEVEL_ONLY_PRIVILEGES = new HashSet<String>(Arrays.asList(sectionLevelOnlyList));
    private static final HashSet<String> SESSION_LEVEL_ONLY_PRIVILEGES = new HashSet<String>(Arrays.asList(sessionLevelOnlyList));
    
    public static boolean isPrivilegeNameValid(String privilegeName) {
        return COURSE_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SECTION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
    }
    
    public static boolean isPrivilegeNameValidForSectionLevel(String privilegeName) {
        return SECTION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
    }
    
    public static boolean isPrivilegeNameValidForSessionLevel(String privilegeName) {
        return SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
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
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
    }
    
    public void setDefaultPrivilegesForManager() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
    }
    
    public void setDefaultPrivilegesForObserver() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);
    }
    
    public void setDefaultPrivilegesForTutor() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);
    }
    
    public void setDefaultPrivilegesForHelper() {
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, false);
        this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);
    }
    
    public HashMap<String, Boolean> getOverallPrivilegesForSections() {
        HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
        
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, 
                isAllowedInCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        return privileges;
    }
    
    public HashMap<String, Boolean> getOverallPrivilegesForSessionsInSection(String sectionName) {
        HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
        
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, 
                isAllowedInSectionLevel(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, 
                isAllowedInSectionLevel(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        privileges.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, 
                isAllowedInSectionLevel(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        return privileges;
    }
    
    public void updatePrivilege(String privilegeName, boolean isAllowed) {
        updatePrivilegeInCourseLevel(privilegeName, isAllowed);
    }
    
    public void updatePrivilege(String sectionName, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSectionLevel(sectionName, privilegeName, isAllowed);
    }
    
    public void updatePrivilege(String sectionName, String sessionName, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSessionLevel(sectionName, sessionName, privilegeName, isAllowed);
    }
    
    private void updatePrivilegeInCourseLevel(String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValid(privilegeName)) {
            return ;
        }
        this.courseLevel.put(privilegeName, isAllowed);
    }
    
    private void updatePrivilegeInSectionLevel(String sectionName, String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValidForSectionLevel(privilegeName)) {
            return ;
        }
        if (!this.sectionLevel.containsKey(sectionName)) {
            sectionLevel.put(sectionName, new HashMap<String, Boolean>());
        }
        sectionLevel.get(sectionName).put(privilegeName, isAllowed);
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
    
    public void updatePrivileges(String sectionName, HashMap<String, Boolean> privileges) {
        updatePrivilegesInSectionLevel(sectionName, privileges);
    }
    
    public void updatePrivileges(String sectionName, String sessionName, HashMap<String, Boolean> privileges) {
        updatePrivilegesInSessionLevel(sectionName, sessionName, privileges);
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
    
    public boolean isAllowedForPrivilege(String sectionName, String privilegeName) {
        return isAllowedInSectionLevel(sectionName, privilegeName);
    }
    
    public boolean isAllowedForPrivilege(String sectionName, String sessionName, String privilegeName) {
        return isAllowedInSessionLevel(sectionName, sessionName, privilegeName);
    }
    
    public boolean isSectionSpecial(String sectionName) {
        return this.sectionLevel.containsKey(sectionName);
    }
    
    public int numberOfSectionsSpecial() {
        return this.sectionLevel.keySet().size();
    }
    
    public boolean isSessionsInSectionSpecial(String sectionName) {
        return this.sessionLevel.containsKey(sectionName);
    }
    
    public boolean isSessionInSectionSpecial(String sectionName, String sessionName) {
        return (this.sessionLevel.containsKey(sectionName)) && this.sessionLevel.get(sectionName).containsKey(sessionName);
    }
    
    public void removeSectionLevelPrivileges(String sectionName) {
        if (this.sectionLevel.containsKey(sectionName)) {
            this.sectionLevel.remove(sectionName);
        }
        this.removeSessionsPrivilegesForSection(sectionName);
    }
    
    public void removeSessionsPrivilegesForSection(String sectionName) {
        if (this.sessionLevel.containsKey(sectionName)) {
            this.sessionLevel.remove(sectionName);
        }
    }
    
    public void removeSessionPrivileges(String sectionName, String sessionName) {
        if (this.sessionLevel.containsKey(sectionName) && this.sessionLevel.get(sectionName).containsKey(sessionName)) {
            this.sessionLevel.get(sectionName).remove(sessionName);
        }
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
    
    public void validatePrivileges() {
        if (this.courseLevel.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)
                && this.courseLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS).booleanValue()) {
            this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
        }
        if (this.courseLevel.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                && this.courseLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS).booleanValue()) {
            this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        }
        for (HashMap<String, Boolean> sectionMap : this.sectionLevel.values()) {
            if (sectionMap.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)
                    && sectionMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS).booleanValue()) {
                sectionMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
            }
            if (sectionMap.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                    && sectionMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS).booleanValue()) {
                sectionMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
            }
        }
        for (HashMap<String, HashMap<String, Boolean>> section : this.sessionLevel.values()) {
            for (HashMap<String, Boolean> sessionMap : section.values()) {
                if (sessionMap.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)
                        && sessionMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS).booleanValue()) {
                    sessionMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
                }
                if (sessionMap.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                        && sessionMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS).booleanValue()) {
                    sessionMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
                }
            }
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
