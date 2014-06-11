package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.api.InstructorPermissionsDb;

public class InstructorPermissionsLogic {
    
    private static final InstructorPermissionsDb instructorPermissionsDb = new InstructorPermissionsDb();
    
    private static Logger log = Utils.getLogger();
    
    private static InstructorPermissionsLogic instance = null;
    
    private InstructorPermissionsLogic(){}
    
    public static InstructorPermissionsLogic inst() {
        if (instance == null) {
            instance = new InstructorPermissionsLogic();
        }
        return instance;
    }
    
    public void addInstructorPermission(String courseId, String instrEmail, String role, InstructorPrivileges privileges) 
            throws InvalidParametersException, EntityAlreadyExistsException {
        InstructorPermissionAttributes instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, privileges);
        addInstructorPermission(instrPermissionAttr);
    }
    
    public void addInstructorPermission(InstructorPermissionAttributes instrPermissionAttr) 
            throws InvalidParametersException, EntityAlreadyExistsException {
        log.info(" going to create instructorPermission: " + instrPermissionAttr.toString());
        
        instructorPermissionsDb.createEntity(instrPermissionAttr);
    }
    
    public InstructorPermissionAttributes getInstructorPermissionForEmail(String courseId, String instrEmail) {
        return instructorPermissionsDb.getInstructorPermissionForEmail(courseId, instrEmail);
    }
    
    public List<InstructorPermissionAttributes> getInstructorPermissionsForEmail(String instrEmail) {
        return instructorPermissionsDb.getInstructorPermissionsForEmail(instrEmail);
    }
    
    public List<InstructorPermissionAttributes> getInstructorPermissionsForCourse(String courseId) {
        return instructorPermissionsDb.getInstructorPermissionsForCourse(courseId);
    }
    
    public void updateInstructorPermissionByEmail(InstructorPermissionAttributes updatedInstrPermissionAttr, String oldInstrEmail)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        instructorPermissionsDb.updateInstructorPermissionByEmail(updatedInstrPermissionAttr, oldInstrEmail);
    }
    
    public void deleteInstructorPermission(String courseId, String instrEmail) {
        instructorPermissionsDb.deleteInstructorPermission(courseId, instrEmail);
    }
    
    public void deleteInstructorPermissionsForCourse(String courseId) {
        instructorPermissionsDb.deleteInstructorPermissionsForCourse(courseId);
    }
    
    public boolean isAllowedForPrivilege(String courseId, String instrEmail, String privilegeName) {
        InstructorPermissionAttributes instrPermissionAttr = verifyExistenceOfInstructorPermission(courseId, instrEmail);
        
        return instrPermissionAttr.privileges.isAllowedForPrivilege(privilegeName);
    }
    
    public boolean isAllowedForPrivilege(String courseId, String instrEmail, String sectionId, String privilegeName) {
        InstructorPermissionAttributes instrPermissionAttr = verifyExistenceOfInstructorPermission(courseId, instrEmail);
        
        return instrPermissionAttr.privileges.isAllowedForPrivilege(sectionId, privilegeName);
    }
    
    public boolean isAllowedForPrivilege(String courseId, String instrEmail, String sectionId, String sessionId, String privilegeName) {
        InstructorPermissionAttributes instrPermissionAttr = verifyExistenceOfInstructorPermission(courseId, instrEmail);
        
        return instrPermissionAttr.privileges.isAllowedForPrivilege(sectionId, sessionId, privilegeName);
    }

    private InstructorPermissionAttributes verifyExistenceOfInstructorPermission(String courseId, String instrEmail) {
        InstructorPermissionAttributes instrPermissionAttr = instructorPermissionsDb.getInstructorPermissionForEmail(courseId, instrEmail);
        
        if (instrPermissionAttr == null) {
            InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
            instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId,
                    Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER, privileges);
            try {
                addInstructorPermission(instrPermissionAttr);
            } catch (Exception e) {
                log.severe("Error when migrating InstructorPermission: " + courseId + "/" + instrEmail);
            }
        }
        
        return instrPermissionAttr;
    }

}
