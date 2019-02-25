package teammates.ui.webapi.output;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorPrivilegesBundle;

/**
 * The output format of instructor privileges.
 */
public class InstructorPrivilegeData extends ApiOutput {
    private InstructorPrivilegesBundle privilegesBundle;

    private Map<String, InstructorPrivilegesBundle> instructorPrivilegesMap;

    public InstructorPrivilegeData() {
        privilegesBundle = new InstructorPrivilegesBundle();
    }

    public InstructorPrivilegesBundle getPrivileges() {
        return privilegesBundle;
    }

    public Map<String, InstructorPrivilegesBundle> getInstructorPrivilegesMap() {
        return instructorPrivilegesMap;
    }

    /**
     * Set course level instructor privilege.
     */
    public void setPrivilegesCourseLevel(String privilegeName, boolean isAllowed) {
        privilegesBundle.updatePrivilegeInCourseLevel(privilegeName, isAllowed);
    }

    /**
     * Set section level instructor privilege.
     */
    public void setPrivilegesSectionLevel(String sectionName, String privilegeName, boolean isAllowed) {
        privilegesBundle.updatePrivilegeInSectionLevel(sectionName, privilegeName, isAllowed);
    }

    /**
     * Set session level instructor privilege.
     */
    public void setPrivilegesSessionLevel(String sectionName, String sessionName, String privilegeName, boolean isAllowed) {
        privilegesBundle.updatePrivilegeInSessionLevel(sectionName, sessionName, privilegeName, isAllowed);
    }

    /**
     * Set instructor privilege map.
     */
    public void setInstructorPrivilegesMap(Map<String, InstructorPrivileges> map) {
        instructorPrivilegesMap = new LinkedHashMap<>();
        map.forEach((k, v) -> {
            instructorPrivilegesMap.putIfAbsent(k, InstructorPrivilegesBundle.toInstructorPrivilegeBundle(v));
        });
    }

    /**
     * build instructor privileges data bundle.
     */
    public void setPrivilegesBundle(InstructorPrivileges privileges) {
        this.privilegesBundle.setPrivileges(privileges);
    }
}
