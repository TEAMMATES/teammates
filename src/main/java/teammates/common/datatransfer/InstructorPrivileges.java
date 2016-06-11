package teammates.common.datatransfer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * Representation of instructor privileges. Store the privileges of the instructor
 */
public final class InstructorPrivileges {

    private static final HashMap<String, Boolean> PRIVILEGES_COOWNER = new HashMap<>();
    private static final HashMap<String, Boolean> PRIVILEGES_MANAGER = new HashMap<>();
    private static final HashMap<String, Boolean> PRIVILEGES_OBSERVER = new HashMap<>();
    private static final HashMap<String, Boolean> PRIVILEGES_TUTOR = new HashMap<>();
    private static final HashMap<String, Boolean> PRIVILEGES_CUSTOM = new HashMap<>();
    
    static {
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, true);
        PRIVILEGES_COOWNER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, true);
        PRIVILEGES_MANAGER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, false);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, false);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, false);
        PRIVILEGES_OBSERVER.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);
        
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, true);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, false);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, true);
        PRIVILEGES_TUTOR.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);
        
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, false);
        PRIVILEGES_CUSTOM.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, false);
    }

    private static final String[] COURSE_LEVEL_ONLY_LIST = new String[] {
            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE,
            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR,
            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION,
            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT
    };
    
    private static final String[] SECTION_LEVEL_ONLY_LIST = new String[] {
            Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
            Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS,
            Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS,
            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS
    };
    
    private static final String[] SESSION_LEVEL_ONLY_LIST = new String[] {
            Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
            Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
    };
    
    private static final HashSet<String> COURSE_LEVEL_ONLY_PRIVILEGES =
            new HashSet<String>(Arrays.asList(COURSE_LEVEL_ONLY_LIST));
    private static final HashSet<String> SECTION_LEVEL_ONLY_PRIVILEGES =
            new HashSet<String>(Arrays.asList(SECTION_LEVEL_ONLY_LIST));
    private static final HashSet<String> SESSION_LEVEL_ONLY_PRIVILEGES =
            new HashSet<String>(Arrays.asList(SESSION_LEVEL_ONLY_LIST));
    
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
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
            setDefaultPrivilegesForCustom();
            break;
        default:
            setDefaultPrivilegesForCustom();
            break;
        }
    }
    
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
    
    public void setDefaultPrivilegesForCoowner() {
        setDefaultPrivileges(PRIVILEGES_COOWNER);
    }
    
    public void setDefaultPrivilegesForManager() {
        setDefaultPrivileges(PRIVILEGES_MANAGER);
    }
    
    public void setDefaultPrivilegesForObserver() {
        setDefaultPrivileges(PRIVILEGES_OBSERVER);
    }
    
    public void setDefaultPrivilegesForTutor() {
        setDefaultPrivileges(PRIVILEGES_TUTOR);
    }
    
    public void setDefaultPrivilegesForCustom() {
        setDefaultPrivileges(PRIVILEGES_CUSTOM);
    }
    
    private void setDefaultPrivileges(HashMap<String, Boolean> defaultPrivileges) {
        for (String privilege : defaultPrivileges.keySet()) {
            courseLevel.put(privilege, defaultPrivileges.get(privilege));
        }
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
    
    /**
     * set privilege for the privilege specified by privilegeName
     * 
     * @param privilegeName
     * @param isAllowed
     */
    public void updatePrivilege(String privilegeName, boolean isAllowed) {
        updatePrivilegeInCourseLevel(privilegeName, isAllowed);
    }
    
    /**
     * set privilege for the privilege specified by privilegeName for sectionName
     * 
     * @param sectionName
     * @param privilegeName
     * @param isAllowed
     */
    public void updatePrivilege(String sectionName, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSectionLevel(sectionName, privilegeName, isAllowed);
    }
    
    /**
     * set privilege for the privilege specified by privilegeName for sessionName in sectionName
     * 
     * @param sectionName
     * @param sessionName
     * @param privilegeName
     * @param isAllowed
     */
    public void updatePrivilege(String sectionName, String sessionName, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSessionLevel(sectionName, sessionName, privilegeName, isAllowed);
    }
    
    private void updatePrivilegeInCourseLevel(String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValid(privilegeName)) {
            return;
        }
        this.courseLevel.put(privilegeName, isAllowed);
    }
    
    private void updatePrivilegeInSectionLevel(String sectionName, String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValidForSectionLevel(privilegeName)) {
            return;
        }
        if (!this.sectionLevel.containsKey(sectionName)) {
            sectionLevel.put(sectionName, new HashMap<String, Boolean>());
        }
        sectionLevel.get(sectionName).put(privilegeName, isAllowed);
    }
    
    private void updatePrivilegeInSessionLevel(String sectionName, String sessionName, String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValidForSessionLevel(privilegeName)) {
            return;
        }
        verifyExistenceOfsectionName(sectionName);
        if (!this.sessionLevel.get(sectionName).containsKey(sessionName)) {
            this.sessionLevel.get(sectionName).put(sessionName, new HashMap<String, Boolean>());
        }
        this.sessionLevel.get(sectionName).get(sessionName).put(privilegeName, isAllowed);
    }
    
    /**
     * used for bulk update of privileges for sectionName
     * 
     * @param sectionName
     * @param privileges
     */
    public void updatePrivileges(String sectionName, HashMap<String, Boolean> privileges) {
        updatePrivilegesInSectionLevel(sectionName, privileges);
    }
    
    /**
     * used for bulk update of privileges for sessionName in sectionName
     * 
     * @param sectionName
     * @param sessionName
     * @param privileges
     */
    public void updatePrivileges(String sectionName, String sessionName, HashMap<String, Boolean> privileges) {
        updatePrivilegesInSessionLevel(sectionName, sessionName, privileges);
    }
    
    @SuppressWarnings("unchecked")
    private void updatePrivilegesInSectionLevel(String sectionName, HashMap<String, Boolean> privileges) {
        for (String privilegeName : privileges.keySet()) {
            if (!isPrivilegeNameValidForSectionLevel(privilegeName)) {
                return;
            }
        }
        sectionLevel.put(sectionName, (HashMap<String, Boolean>) privileges.clone());
    }
    
    @SuppressWarnings("unchecked")
    private void updatePrivilegesInSessionLevel(String sectionName, String sessionName, HashMap<String, Boolean> privileges) {
        for (String privilegeName : privileges.keySet()) {
            if (!isPrivilegeNameValidForSessionLevel(privilegeName)) {
                return;
            }
        }
        verifyExistenceOfsectionName(sectionName);
        this.sessionLevel.get(sectionName).put(sessionName, (HashMap<String, Boolean>) privileges.clone());
    }

    private void verifyExistenceOfsectionName(String sectionName) {
        if (!this.sessionLevel.containsKey(sectionName)) {
            addSectionWithDefaultPrivileges(sectionName);
            this.sessionLevel.put(sectionName, new HashMap<String, HashMap<String, Boolean>>());
        }
    }
    
    public void addSectionWithDefaultPrivileges(String sectionName) {
        if (this.sectionLevel.containsKey(sectionName)) {
            return;
        }
        this.sectionLevel.put(sectionName, getOverallPrivilegesForSections());
    }
    
    public void addSessionWithDefaultPrivileges(String sectionName, String sessionName) {
        verifyExistenceOfsectionName(sectionName);
        if (this.sessionLevel.get(sectionName).containsKey(sessionName)) {
            return;
        }
        this.sessionLevel.get(sectionName).put(sessionName, getOverallPrivilegesForSessionsInSection(sectionName));
    }
    
    /**
     * @param privilegeName
     * @return whether it is allowed for the privilege specified by privilegeName
     */
    public boolean isAllowedForPrivilege(String privilegeName) {
        return isAllowedInCourseLevel(privilegeName);
    }
    
    /**
     * 
     * @param sectionName
     * @param privilegeName
     * @return whether it is allowed for the privilege specified by privilegeName in sectionName
     */
    public boolean isAllowedForPrivilege(String sectionName, String privilegeName) {
        return isAllowedInSectionLevel(sectionName, privilegeName);
    }
    
    /**
     * 
     * @param sectionName
     * @param sessionName
     * @param privilegeName
     * @return whether it is allowed for the privilege specified by privilegeName for sessionName in sectionName
     */
    public boolean isAllowedForPrivilege(String sectionName, String sessionName, String privilegeName) {
        return isAllowedInSessionLevel(sectionName, sessionName, privilegeName);
    }
    
    /**
     * Returns true if privilege for session is present for any section.
     */
    public boolean isAllowedForPrivilegeAnySection(String sessionName, String privilegeName) {
        return isAllowedInSessionLevelAnySection(sessionName, privilegeName);
    }

    public boolean hasCoownerPrivileges() {
        return hasSamePrivileges(PRIVILEGES_COOWNER);
    }

    public boolean hasManagerPrivileges() {
        return hasSamePrivileges(PRIVILEGES_MANAGER);
    }

    public boolean hasObserverPrivileges() {
        return hasSamePrivileges(PRIVILEGES_OBSERVER);
    }

    public boolean hasTutorPrivileges() {
        return hasSamePrivileges(PRIVILEGES_TUTOR);
    }

    private boolean hasSamePrivileges(HashMap<String, Boolean> defaultPrivileges) {

        for (String privilege : defaultPrivileges.keySet()) {
            if (isAllowedForPrivilege(privilege) != defaultPrivileges.get(privilege)) {
                return false;
            }
        }

        return true;
    }

    public boolean isSectionSpecial(String sectionName) {
        return this.sectionLevel.containsKey(sectionName);
    }
    
    public int numberOfSectionsSpecial() {
        return this.sectionLevel.keySet().size();
    }
    
    /**
     * 
     * @param sectionName
     * @return whether there are special settings for sectionName
     */
    public boolean isSessionsInSectionSpecial(String sectionName) {
        return this.sessionLevel.containsKey(sectionName);
    }
    
    /**
     * 
     * @param sectionName
     * @param sessionName
     * @return whether there are special settings for sessionName in sectionName
     */
    public boolean isSessionInSectionSpecial(String sectionName, String sessionName) {
        return this.sessionLevel.containsKey(sectionName)
               && this.sessionLevel.get(sectionName).containsKey(sessionName);
    }
    
    /**
     * remove special settings for sectionName
     * @param sectionName
     */
    public void removeSectionLevelPrivileges(String sectionName) {
        if (this.sectionLevel.containsKey(sectionName)) {
            this.sectionLevel.remove(sectionName);
        }
        this.removeSessionsPrivilegesForSection(sectionName);
    }
    
    /**
     * remove special settings for all sessionNames in sectionName
     * @param sectionName
     */
    public void removeSessionsPrivilegesForSection(String sectionName) {
        if (this.sessionLevel.containsKey(sectionName)) {
            this.sessionLevel.remove(sectionName);
        }
    }
    
    /**
     * remove special settings for sessionName in sectionName
     * @param sectionName
     * @param sessionName
     */
    public void removeSessionPrivileges(String sectionName, String sessionName) {
        if (this.sessionLevel.containsKey(sectionName) && this.sessionLevel.get(sectionName).containsKey(sessionName)) {
            this.sessionLevel.get(sectionName).remove(sessionName);
        }
    }
    
    private boolean isAllowedInCourseLevel(String privilegeName) {

        Assumption.assertTrue(isPrivilegeNameValid(privilegeName));

        if (!this.courseLevel.containsKey(privilegeName)) {
            return false;
        }
        
        return this.courseLevel.get(privilegeName).booleanValue();
    }
    
    private boolean isAllowedInSectionLevel(String sectionName, String privilegeName) {

        Assumption.assertTrue(isPrivilegeNameValid(privilegeName));

        if (!this.sectionLevel.containsKey(sectionName)) {
            return isAllowedInCourseLevel(privilegeName);
        }
        if (!this.sectionLevel.get(sectionName).containsKey(privilegeName)) {
            return false;
        }
        return this.sectionLevel.get(sectionName).get(privilegeName).booleanValue();
    }
    
    private boolean isAllowedInSessionLevel(String sectionName, String sessionName, String privilegeName) {

        Assumption.assertTrue(isPrivilegeNameValid(privilegeName));

        if (!this.sessionLevel.containsKey(sectionName)
                || !this.sessionLevel.get(sectionName).containsKey(sessionName)) {
            return isAllowedInSectionLevel(sectionName, privilegeName);
        }
        if (!this.sessionLevel.get(sectionName).get(sessionName).containsKey(privilegeName)) {
            return false;
        }
        return this.sessionLevel.get(sectionName).get(sessionName).get(privilegeName).booleanValue();
    }
    
    private boolean isAllowedInSessionLevelAnySection(String sessionName, String privilegeName) {

        Assumption.assertTrue(isPrivilegeNameValid(privilegeName));

        HashSet<String> sections = new HashSet<String>(this.sessionLevel.keySet());
        sections.addAll(this.sectionLevel.keySet());
        for (String sectionName : sections) {
            if (isAllowedInSessionLevel(sectionName, sessionName, privilegeName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * validate the privileges in course level, section level and session level
     * make sure there is nothing wrong with privileges hierarchy by adding the
     * prerequisite privileges if they have not been granted yet
     */
    public void validatePrivileges() {
        if (this.courseLevel.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)
                && this.courseLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS).booleanValue()) {
            this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
        }
        if (this.courseLevel.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                && this.courseLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                   .booleanValue()) {
            this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        }
        if (this.courseLevel.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)
                && this.courseLevel.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS).booleanValue()) {
            this.courseLevel.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        }
        for (HashMap<String, Boolean> sectionMap : this.sectionLevel.values()) {
            if (sectionMap.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)
                    && sectionMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS).booleanValue()) {
                sectionMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, true);
            }
            if (sectionMap.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                    && sectionMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                 .booleanValue()) {
                sectionMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
            }
            if (sectionMap.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)
                    && sectionMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS).booleanValue()) {
                sectionMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
            }
        }
        for (HashMap<String, HashMap<String, Boolean>> section : this.sessionLevel.values()) {
            for (HashMap<String, Boolean> sessionMap : section.values()) {
                if (sessionMap.containsKey(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                        && sessionMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                     .booleanValue()) {
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
    
    @Override
    public boolean equals(Object another) {
        if (!(another instanceof InstructorPrivileges)) {
            return false;
        }
        if (another == this) {
            return true;
        }
        
        InstructorPrivileges rhs = (InstructorPrivileges) another;
        return this.getCourseLevelPrivileges().equals(rhs.getCourseLevelPrivileges())
               && this.getSectionLevelPrivileges().equals(rhs.getSectionLevelPrivileges())
               && this.getSessionLevelPrivileges().equals(rhs.getSessionLevelPrivileges());
    }
    
    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        
        result = prime * result + this.getCourseLevelPrivileges().hashCode();
        result = prime * result + this.getSectionLevelPrivileges().hashCode();
        result = prime * result + this.getSessionLevelPrivileges().hashCode();
        
        return result;
    }
    
}
