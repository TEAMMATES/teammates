package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.InstructorPermission;

public class InstructorPermissionsDb extends EntitiesDb {

    private static final Logger log = Utils.getLogger();
    
    public InstructorPermissionAttributes getInstructorPermissionForEmail(String courseId, String instrEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, instrEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        InstructorPermission i = getInstructorPermissionEntityForEmail(courseId, instrEmail);
        
        if (i == null) {
            log.info("Trying to get non-existent InstructorPermission: " + courseId +"/"+ instrEmail );
            // should we create entity here for data migration?
            return null;
        }
        
        return new InstructorPermissionAttributes(i);
    }
    
    public List<InstructorPermissionAttributes> getInstructorPermissionsForEmail(String instrEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, instrEmail);
        
        List<InstructorPermission> instructorPermissionList = getInstructorPermissionEntitiesForEmail(instrEmail);
        List<InstructorPermissionAttributes> instrPermissionAttrList = new ArrayList<InstructorPermissionAttributes>();
        
        for (InstructorPermission i : instructorPermissionList) {
            if (!JDOHelper.isDeleted(i)) {
                instrPermissionAttrList.add(new InstructorPermissionAttributes(i));
            }
        }
        
        return instrPermissionAttrList;
    }
    
    public void updateInstructorPermissionByEmail(InstructorPermissionAttributes updatedInstrPermission,
            String oldInstrEmail) throws InvalidParametersException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldInstrEmail);
        
        if (!updatedInstrPermission.isValid()) {
            throw new InvalidParametersException(updatedInstrPermission.getInvalidityInfo());
        }
        updatedInstrPermission.sanitizeForSaving();
        
        InstructorPermission oldInstrPermission = getInstructorPermissionEntityForEmail(updatedInstrPermission.getCourseId(),
                oldInstrEmail);
        
        if (oldInstrPermission == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + oldInstrEmail
                    + ThreadHelper.getCurrentThreadStack());
        }
        
        if (!oldInstrEmail.equals(updatedInstrPermission.getInstructorEmail())) {
            oldInstrPermission.setInstructorEmail(updatedInstrPermission.getInstructorEmail());
        }
        if (!oldInstrPermission.getRole().equals(updatedInstrPermission.getRole())) {
            oldInstrPermission.setRole(updatedInstrPermission.getRole());
        }
        if (!oldInstrPermission.getAccess().equals(updatedInstrPermission.getAccess())) {
            oldInstrPermission.setAccess(updatedInstrPermission.getAccess());
        }
        
        getPM().close();
    }
    
    private InstructorPermission getInstructorPermissionEntityForEmail(String courseId, String instrEmail) {
        
        Query q = getPM().newQuery(InstructorPermission.class);
        q.declareParameters("String courseIdParam, String emailParam");
        q.setFilter("courseId == courseIdParam && instructorEmail == emailParam");
        
        @SuppressWarnings("unchecked")
        List<InstructorPermission> instructorPermissionList = (List<InstructorPermission>) q.execute(courseId, instrEmail);
        
        if (instructorPermissionList.isEmpty()
                || JDOHelper.isDeleted(instructorPermissionList.get(0))) {
            return null;
        }

        return instructorPermissionList.get(0);
    }
    
    private List<InstructorPermission> getInstructorPermissionEntitiesForEmail(String instrEmail) {
        
        Query q = getPM().newQuery(InstructorPermission.class);
        q.declareParameters("String instrEmailParam");
        q.setFilter("instructorEmail == instrEmailParam");
        
        @SuppressWarnings("unchecked")
        List<InstructorPermission> instructorPermissionList = (List<InstructorPermission>)q.execute(instrEmail);
        
        return instructorPermissionList;
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        // TODO Auto-generated method stub
        return null;
    }

}
