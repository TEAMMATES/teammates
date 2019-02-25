package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.common.util.Assumption;

/**
 * Represents detailed privilege of an instructor.
 * <br> Contains:
 * <br> * course, section, session level privileges of the instructor.
 *
 */
public class InstructorPrivilegesBundle {

    private Map<String, Boolean> courseLevel;
    private Map<String, Map<String, Boolean>> sectionLevel;
    private Map<String, Map<String, Map<String, Boolean>>> sessionLevel;

    public InstructorPrivilegesBundle() {
        this.courseLevel = new LinkedHashMap<>();
        this.sectionLevel = new LinkedHashMap<>();
        this.sessionLevel = new LinkedHashMap<>();
    }

    private void setCourseLevelPrivileges(Map<String, Boolean> courseLevel) {
        this.courseLevel = courseLevel;
    }

    private void setSectionLevelPrivileges(Map<String, Map<String, Boolean>> sectionLevel) {
        this.sectionLevel = sectionLevel;
    }

    private void setSessionLevelPrivileges(Map<String, Map<String, Map<String, Boolean>>> sessionLevel) {
        this.sessionLevel = sessionLevel;
    }

    public void setPrivileges(InstructorPrivileges privileges) {
        setCourseLevelPrivileges(privileges.getCourseLevelPrivileges());
        setSessionLevelPrivileges(privileges.getSessionLevelPrivileges());
        setSectionLevelPrivileges(privileges.getSectionLevelPrivileges());
    }

    public void updatePrivilegeInCourseLevel(String privilegeName, boolean isAllowed) {
        if (!InstructorPrivileges.isPrivilegeNameValid(privilegeName)) {
            return;
        }
        this.courseLevel.put(privilegeName, isAllowed);
    }

    public void updatePrivilegeInSectionLevel(String sectionName, String privilegeName, boolean isAllowed) {
        if (!InstructorPrivileges.isPrivilegeNameValidForSectionLevel(privilegeName)) {
            return;
        }
        this.sectionLevel.computeIfAbsent(sectionName, key -> new LinkedHashMap<>())
                .put(privilegeName, isAllowed);
    }

    public void updatePrivilegeInSessionLevel(String sectionName, String sessionName,
                                               String privilegeName, boolean isAllowed) {
        if (!InstructorPrivileges.isPrivilegeNameValidForSessionLevel(privilegeName)) {
            return;
        }
        this.sessionLevel.computeIfAbsent(sectionName, key -> new LinkedHashMap<>());
        this.sessionLevel.get(sectionName).computeIfAbsent(sessionName, key -> new LinkedHashMap<>())
                .put(privilegeName, isAllowed);
    }

    public static InstructorPrivilegesBundle toInstructorPrivilegeBundle(InstructorPrivileges privileges) {
        InstructorPrivilegesBundle privilegesBundle = new InstructorPrivilegesBundle();
        privilegesBundle.setCourseLevelPrivileges(privileges.getCourseLevelPrivileges());
        privilegesBundle.setSectionLevelPrivileges(privileges.getSectionLevelPrivileges());
        privilegesBundle.setSessionLevelPrivileges(privileges.getSessionLevelPrivileges());
        return privilegesBundle;
    }

    public boolean isAllowedInCourseLevel(String privilegeName) {

        Assumption.assertTrue(InstructorPrivileges.isPrivilegeNameValid(privilegeName));

        return this.courseLevel.getOrDefault(privilegeName, false);
    }

    public boolean isAllowedInSectionLevel(String sectionName, String privilegeName) {

        Assumption.assertTrue(InstructorPrivileges.isPrivilegeNameValid(privilegeName));

        if (!this.sectionLevel.containsKey(sectionName)) {
            return isAllowedInCourseLevel(privilegeName);
        }

        return this.sectionLevel.get(sectionName).getOrDefault(privilegeName, false);
    }

    public Map<String, Boolean> getCourseLevelPrivileges() {
        return courseLevel;
    }

    public Map<String, Map<String, Boolean>> getSectionLevelPrivileges() {
        return sectionLevel;
    }

    public Map<String, Map<String, Map<String, Boolean>>> getSessionLevelPrivileges() {
        return sessionLevel;
    }
}
