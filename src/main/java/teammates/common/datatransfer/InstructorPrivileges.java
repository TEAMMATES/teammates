package teammates.common.datatransfer;

import java.util.HashMap;
import teammates.common.util.Const;

public class InstructorPrivileges {
    private HashMap<String, Boolean> courseLevel;
    private HashMap<String, Boolean> sectionLevel;
    private HashMap<String, HashMap<String, Boolean>> sessionLevel;
    
    public InstructorPrivileges() {
        this.courseLevel = new HashMap<String, Boolean>();
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
        
    }
    
    public void setDefaultPrivilegesForManager() {
        
    }
    
    public void setDefaultPrivilegesForObserver() {
        
    }
    
    public void setDefaultPrivilegesForTutor() {
        
    }
    
    public void setDefaultPrivilegesForHelper() {
        
    }

    public HashMap<String, Boolean> getCourseLevelPrivileges() {
        return courseLevel;
    }

    public HashMap<String, Boolean> getSectionLevelPrivileges() {
        return sectionLevel;
    }

    public HashMap<String, HashMap<String, Boolean>> getSessionLevelPrivileges() {
        return sessionLevel;
    }
    
}
