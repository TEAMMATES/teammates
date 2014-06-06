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
            
        }
    }
    
    public void setDefaultPrivilegesForCoowner() {
        
    }
}
