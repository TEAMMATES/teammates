package teammates.common.datatransfer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import teammates.common.util.Const;

/**
 * Representation of instructor privileges. Store the privileges of the instructor
 */
public final class InstructorPrivileges {

    private static final InstructorPermissionSet PRIVILEGES_COOWNER = new InstructorPermissionSet();
    private static final InstructorPermissionSet PRIVILEGES_MANAGER = new InstructorPermissionSet();
    private static final InstructorPermissionSet PRIVILEGES_OBSERVER = new InstructorPermissionSet();
    private static final InstructorPermissionSet PRIVILEGES_TUTOR = new InstructorPermissionSet();
    private static final InstructorPermissionSet PRIVILEGES_CUSTOM = new InstructorPermissionSet();

    static {
        PRIVILEGES_COOWNER.setCanModifyCourse(true);
        PRIVILEGES_COOWNER.setCanModifyInstructor(true);
        PRIVILEGES_COOWNER.setCanModifySession(true);
        PRIVILEGES_COOWNER.setCanModifyStudent(true);
        PRIVILEGES_COOWNER.setCanViewStudentInSections(true);
        PRIVILEGES_COOWNER.setCanViewSessionInSections(true);
        PRIVILEGES_COOWNER.setCanSubmitSessionInSections(true);
        PRIVILEGES_COOWNER.setCanModifySessionCommentsInSections(true);

        PRIVILEGES_MANAGER.setCanModifyCourse(false);
        PRIVILEGES_MANAGER.setCanModifyInstructor(true);
        PRIVILEGES_MANAGER.setCanModifySession(true);
        PRIVILEGES_MANAGER.setCanModifyStudent(true);
        PRIVILEGES_MANAGER.setCanViewStudentInSections(true);
        PRIVILEGES_MANAGER.setCanViewSessionInSections(true);
        PRIVILEGES_MANAGER.setCanSubmitSessionInSections(true);
        PRIVILEGES_MANAGER.setCanModifySessionCommentsInSections(true);

        PRIVILEGES_OBSERVER.setCanModifyCourse(false);
        PRIVILEGES_OBSERVER.setCanModifyInstructor(false);
        PRIVILEGES_OBSERVER.setCanModifySession(false);
        PRIVILEGES_OBSERVER.setCanModifyStudent(false);
        PRIVILEGES_OBSERVER.setCanViewStudentInSections(true);
        PRIVILEGES_OBSERVER.setCanViewSessionInSections(true);
        PRIVILEGES_OBSERVER.setCanSubmitSessionInSections(false);
        PRIVILEGES_OBSERVER.setCanModifySessionCommentsInSections(false);

        PRIVILEGES_TUTOR.setCanModifyCourse(false);
        PRIVILEGES_TUTOR.setCanModifyInstructor(false);
        PRIVILEGES_TUTOR.setCanModifySession(false);
        PRIVILEGES_TUTOR.setCanModifyStudent(false);
        PRIVILEGES_TUTOR.setCanViewStudentInSections(true);
        PRIVILEGES_TUTOR.setCanViewSessionInSections(true);
        PRIVILEGES_TUTOR.setCanSubmitSessionInSections(true);
        PRIVILEGES_TUTOR.setCanModifySessionCommentsInSections(false);

        PRIVILEGES_CUSTOM.setCanModifyCourse(false);
        PRIVILEGES_CUSTOM.setCanModifyInstructor(false);
        PRIVILEGES_CUSTOM.setCanModifySession(false);
        PRIVILEGES_CUSTOM.setCanModifyStudent(false);
        PRIVILEGES_CUSTOM.setCanViewStudentInSections(false);
        PRIVILEGES_CUSTOM.setCanViewSessionInSections(false);
        PRIVILEGES_CUSTOM.setCanSubmitSessionInSections(false);
        PRIVILEGES_CUSTOM.setCanModifySessionCommentsInSections(false);
    }

    private static final String[] COURSE_LEVEL_ONLY_LIST = new String[] {
            Const.InstructorPermissions.CAN_MODIFY_COURSE,
            Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR,
            Const.InstructorPermissions.CAN_MODIFY_SESSION,
            Const.InstructorPermissions.CAN_MODIFY_STUDENT,
    };

    private static final String[] SECTION_LEVEL_ONLY_LIST = new String[] {
            Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS,
    };

    private static final String[] SESSION_LEVEL_ONLY_LIST = new String[] {
            Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS,
            Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS,
            Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
    };

    private static final Set<String> COURSE_LEVEL_ONLY_PRIVILEGES =
            new LinkedHashSet<>(Arrays.asList(COURSE_LEVEL_ONLY_LIST));
    private static final Set<String> SECTION_LEVEL_ONLY_PRIVILEGES =
            new LinkedHashSet<>(Arrays.asList(SECTION_LEVEL_ONLY_LIST));
    private static final Set<String> SESSION_LEVEL_ONLY_PRIVILEGES =
            new LinkedHashSet<>(Arrays.asList(SESSION_LEVEL_ONLY_LIST));

    private final InstructorPermissionSet courseLevel;
    private final Map<String, InstructorPermissionSet> sectionLevel;
    private final Map<String, Map<String, InstructorPermissionSet>> sessionLevel;

    public InstructorPrivileges() {
        this.courseLevel = new InstructorPermissionSet();
        this.sectionLevel = new LinkedHashMap<>();
        this.sessionLevel = new LinkedHashMap<>();
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
        default:
            setDefaultPrivilegesForCustom();
            break;
        }
    }

    public InstructorPrivileges(InstructorPrivilegesLegacy legacyFormat) {
        this.courseLevel = InstructorPermissionSet.fromLegacyMapFormat(legacyFormat.getCourseLevel());

        this.sectionLevel = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Boolean>> entry : legacyFormat.getSectionLevel().entrySet()) {
            this.sectionLevel.put(entry.getKey(), InstructorPermissionSet.fromLegacyMapFormat(entry.getValue()));
        }

        this.sessionLevel = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Map<String, Boolean>>> section : legacyFormat.getSessionLevel().entrySet()) {
            Map<String, InstructorPermissionSet> sessionMap = new HashMap<>();
            for (Map.Entry<String, Map<String, Boolean>> session : section.getValue().entrySet()) {
                sessionMap.put(session.getKey(), InstructorPermissionSet.fromLegacyMapFormat(session.getValue()));
            }
            this.sessionLevel.put(section.getKey(), sessionMap);
        }
    }

    /**
     * Converts the current privilege object to its legacy format.
     */
    public InstructorPrivilegesLegacy toLegacyFormat() {
        InstructorPrivilegesLegacy privilegesLegacy = new InstructorPrivilegesLegacy();
        privilegesLegacy.getCourseLevel().putAll(courseLevel.toLegacyMapFormat());
        for (Map.Entry<String, InstructorPermissionSet> entry : sectionLevel.entrySet()) {
            Map<String, Boolean> legacySectionMap = new HashMap<>();
            for (Map.Entry<String, Boolean> section : entry.getValue().toLegacyMapFormat().entrySet()) {
                if (isPrivilegeNameValidForSectionLevel(section.getKey())) {
                    legacySectionMap.put(section.getKey(), section.getValue());
                }
            }
            privilegesLegacy.getSectionLevel().put(entry.getKey(), legacySectionMap);
        }
        for (Map.Entry<String, Map<String, InstructorPermissionSet>> section : sessionLevel.entrySet()) {
            Map<String, Map<String, Boolean>> sessionMap = new HashMap<>();
            for (Map.Entry<String, InstructorPermissionSet> entry : section.getValue().entrySet()) {
                Map<String, Boolean> legacySessionMap = new HashMap<>();
                for (Map.Entry<String, Boolean> session : entry.getValue().toLegacyMapFormat().entrySet()) {
                    if (isPrivilegeNameValidForSessionLevel(session.getKey())) {
                        legacySessionMap.put(session.getKey(), session.getValue());
                    }
                }
                sessionMap.put(entry.getKey(), legacySessionMap);
            }
            privilegesLegacy.getSessionLevel().put(section.getKey(), sessionMap);
        }
        return privilegesLegacy;
    }

    /**
     * Returns true if the given string is a valid privilege name.
     */
    public static boolean isPrivilegeNameValid(String privilegeName) {
        return COURSE_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SECTION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
    }

    /**
     * Returns true if the given string is a valid section-level privilege name.
     */
    public static boolean isPrivilegeNameValidForSectionLevel(String privilegeName) {
        return SECTION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
    }

    /**
     * Returns true if the given string is a valid session-level privilege name.
     */
    public static boolean isPrivilegeNameValidForSessionLevel(String privilegeName) {
        return SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
    }

    void setDefaultPrivilegesForCoowner() {
        setDefaultPrivileges(PRIVILEGES_COOWNER);
    }

    void setDefaultPrivilegesForManager() {
        setDefaultPrivileges(PRIVILEGES_MANAGER);
    }

    void setDefaultPrivilegesForObserver() {
        setDefaultPrivileges(PRIVILEGES_OBSERVER);
    }

    void setDefaultPrivilegesForTutor() {
        setDefaultPrivileges(PRIVILEGES_TUTOR);
    }

    void setDefaultPrivilegesForCustom() {
        setDefaultPrivileges(PRIVILEGES_CUSTOM);
    }

    private void setDefaultPrivileges(InstructorPermissionSet defaultPrivileges) {
        courseLevel.setCanModifyCourse(defaultPrivileges.isCanModifyCourse());
        courseLevel.setCanModifyInstructor(defaultPrivileges.isCanModifyInstructor());
        courseLevel.setCanModifySession(defaultPrivileges.isCanModifySession());
        courseLevel.setCanModifyStudent(defaultPrivileges.isCanModifyStudent());
        courseLevel.setCanViewStudentInSections(defaultPrivileges.isCanViewStudentInSections());
        courseLevel.setCanViewSessionInSections(defaultPrivileges.isCanViewSessionInSections());
        courseLevel.setCanSubmitSessionInSections(defaultPrivileges.isCanSubmitSessionInSections());
        courseLevel.setCanModifySessionCommentsInSections(defaultPrivileges.isCanModifySessionCommentsInSections());
    }

    private InstructorPermissionSet getOverallPrivilegesForSections() {
        InstructorPermissionSet privileges = new InstructorPermissionSet();

        privileges.setCanViewStudentInSections(courseLevel.isCanViewStudentInSections());
        privileges.setCanViewSessionInSections(courseLevel.isCanViewSessionInSections());
        privileges.setCanSubmitSessionInSections(courseLevel.isCanSubmitSessionInSections());
        privileges.setCanModifySessionCommentsInSections(courseLevel.isCanModifySessionCommentsInSections());

        return privileges;
    }

    /**
     * Sets privilege for the privilege specified by privilegeName.
     */
    public void updatePrivilege(String privilegeName, boolean isAllowed) {
        updatePrivilegeInCourseLevel(privilegeName, isAllowed);
    }

    /**
     * Sets privilege for the privilege specified by privilegeName for sectionName.
     */
    public void updatePrivilege(String sectionName, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSectionLevel(sectionName, privilegeName, isAllowed);
    }

    /**
     * Sets privilege for the privilege specified by privilegeName for sessionName in sectionName.
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
        addSectionWithDefaultPrivileges(sectionName);
        this.sectionLevel.get(sectionName).put(privilegeName, isAllowed);
    }

    private void updatePrivilegeInSessionLevel(String sectionName, String sessionName,
                                               String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValidForSessionLevel(privilegeName)) {
            return;
        }
        verifyExistenceOfsectionName(sectionName);
        this.sessionLevel.get(sectionName).computeIfAbsent(sessionName, key -> new InstructorPermissionSet())
                                          .put(privilegeName, isAllowed);
    }

    private void verifyExistenceOfsectionName(String sectionName) {
        this.sessionLevel.computeIfAbsent(sectionName, key -> {
            addSectionWithDefaultPrivileges(sectionName);
            return new LinkedHashMap<>();
        });
    }

    void addSectionWithDefaultPrivileges(String sectionName) {
        this.sectionLevel.putIfAbsent(sectionName, getOverallPrivilegesForSections());
    }

    /**
     * Returns true if it is allowed for the privilege specified by privilegeName.
     */
    public boolean isAllowedForPrivilege(String privilegeName) {
        return isAllowedInCourseLevel(privilegeName);
    }

    /**
     * Returns true if it is allowed for the privilege specified by privilegeName in sectionName.
     */
    public boolean isAllowedForPrivilege(String sectionName, String privilegeName) {
        return isAllowedInSectionLevel(sectionName, privilegeName);
    }

    /**
     * Returns true if it is allowed for the privilege specified by privilegeName for sessionName in sectionName.
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

    /**
     * Returns true if co-owner privilege exists.
     */
    public boolean hasCoownerPrivileges() {
        return courseLevel.equals(PRIVILEGES_COOWNER);
    }

    /**
     * Returns true if manager privilege exists.
     */
    public boolean hasManagerPrivileges() {
        return courseLevel.equals(PRIVILEGES_MANAGER);
    }

    /**
     * Returns true if observer privilege exists.
     */
    public boolean hasObserverPrivileges() {
        return courseLevel.equals(PRIVILEGES_OBSERVER);
    }

    /**
     * Returns true if tutor privilege exists.
     */
    public boolean hasTutorPrivileges() {
        return courseLevel.equals(PRIVILEGES_TUTOR);
    }

    private boolean isAllowedInCourseLevel(String privilegeName) {

        assert isPrivilegeNameValid(privilegeName);

        return this.courseLevel.get(privilegeName);
    }

    private boolean isAllowedInSectionLevel(String sectionName, String privilegeName) {

        assert isPrivilegeNameValid(privilegeName);

        if (!this.sectionLevel.containsKey(sectionName)) {
            return isAllowedInCourseLevel(privilegeName);
        }

        return this.sectionLevel.get(sectionName).get(privilegeName);
    }

    private boolean isAllowedInSessionLevel(String sectionName, String sessionName, String privilegeName) {

        assert isPrivilegeNameValid(privilegeName);

        if (!this.sessionLevel.containsKey(sectionName)
                || !this.sessionLevel.get(sectionName).containsKey(sessionName)) {
            return isAllowedInSectionLevel(sectionName, privilegeName);
        }

        return this.sessionLevel.get(sectionName).get(sessionName).get(privilegeName);
    }

    private boolean isAllowedInSessionLevelAnySection(String sessionName, String privilegeName) {

        assert isPrivilegeNameValid(privilegeName);

        Set<String> sections = new LinkedHashSet<>(this.sessionLevel.keySet());
        sections.addAll(this.sectionLevel.keySet());
        for (String sectionName : sections) {
            if (isAllowedInSessionLevel(sectionName, sessionName, privilegeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the privileges in course level, section level and session level.
     *
     * <p>Makes sure there is nothing wrong with privileges hierarchy by adding the
     * prerequisite privileges if they have not been granted yet.
     */
    public void validatePrivileges() {
        if (this.courseLevel.isCanModifySessionCommentsInSections()) {
            this.courseLevel.setCanViewSessionInSections(true);
        }
        for (InstructorPermissionSet sectionMap : this.sectionLevel.values()) {
            if (sectionMap.isCanModifySessionCommentsInSections()) {
                sectionMap.setCanViewSessionInSections(true);
            }
        }
        for (Map<String, InstructorPermissionSet> section : this.sessionLevel.values()) {
            for (InstructorPermissionSet sessionMap : section.values()) {
                if (sessionMap.isCanModifySessionCommentsInSections()) {
                    sessionMap.setCanViewSessionInSections(true);
                }
            }
        }
    }

    public InstructorPermissionSet getCourseLevelPrivileges() {
        return courseLevel.getCopy();
    }

    /**
     * Returns the section level privileges of the instructor.
     */
    public Map<String, InstructorPermissionSet> getSectionLevelPrivileges() {
        Map<String, InstructorPermissionSet> copy = new LinkedHashMap<>();
        sectionLevel.forEach((key, value) -> copy.put(key, value.getCopy()));
        return copy;
    }

    /**
     * Returns the session level privileges of the instructor.
     */
    public Map<String, Map<String, InstructorPermissionSet>> getSessionLevelPrivileges() {
        Map<String, Map<String, InstructorPermissionSet>> copy = new LinkedHashMap<>();
        sessionLevel.forEach((sessionLevelKey, sessionLevelValue) -> {
            Map<String, InstructorPermissionSet> sectionCopy = new LinkedHashMap<>();
            sessionLevelValue.forEach((key, value) -> sectionCopy.put(key, value.getCopy()));

            copy.put(sessionLevelKey, sectionCopy);
        });
        return copy;
    }

    /**
     * Returns the list of sections the instructor has the specified privilege name.
     */
    public Map<String, InstructorPermissionSet> getSectionsWithPrivilege(String privilegeName) {
        Map<String, InstructorPermissionSet> copy = new LinkedHashMap<>();
        sectionLevel.forEach((key, value) -> {
            if (isAllowedInSectionLevel(key, privilegeName)) {
                copy.put(key, value.getCopy());
            }
        });
        return copy;
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
