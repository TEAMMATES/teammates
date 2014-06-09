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
import teammates.common.util.Config;
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
    
    public List<InstructorPermissionAttributes> getInstructorPermissionsForCourse(String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<InstructorPermission> instructorPermissionList = getInstructorPermissionEntitiesForCourse(courseId);
        List<InstructorPermissionAttributes> instrPermissionAttrList = new ArrayList<InstructorPermissionAttributes>();
        
        for (InstructorPermission i : instructorPermissionList) {
            if (!JDOHelper.isDeleted(i)) {
                instrPermissionAttrList.add(new InstructorPermissionAttributes(i));
            }
        }
        
        return instrPermissionAttrList;
    }
    
    public void updateInstructorPermissionByEmail(InstructorPermissionAttributes updatedInstrPermission,
            String oldInstrEmail) throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updatedInstrPermission);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldInstrEmail);
        
        if (!updatedInstrPermission.isValid()) {
            throw new InvalidParametersException(updatedInstrPermission.getInvalidityInfo());
        }
        updatedInstrPermission.sanitizeForSaving();
        
        InstructorPermission oldInstrPermission = getInstructorPermissionEntityForEmail(updatedInstrPermission.courseId,
                oldInstrEmail);
        
        if (oldInstrPermission == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + oldInstrEmail
                    + ThreadHelper.getCurrentThreadStack());
        }
        
        if (!oldInstrEmail.equals(updatedInstrPermission.instructorEmail)) {
            oldInstrPermission.setInstructorEmail(updatedInstrPermission.instructorEmail);
        }
        oldInstrPermission.setRole(updatedInstrPermission.role);
        oldInstrPermission.setInstructorPrvilegesAsText(updatedInstrPermission.instructorPrivilegesAsText);
        
        getPM().close();
    }
    
    public void deleteInstructorPermission(String courseId, String instrEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, instrEmail);
        
        InstructorPermission instrPermissionToDelete = getInstructorPermissionEntityForEmail(courseId, instrEmail);
        
        if (instrPermissionToDelete == null) {
            return;
        }
        
        getPM().deletePersistent(instrPermissionToDelete);
        getPM().flush();
        
        // check delete operation persisted
        int elapsedTime = 0;
        InstructorPermission instrPermissionCheck = getInstructorPermissionEntityForEmail(courseId, instrEmail);
        while ((instrPermissionCheck != null)
                && (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
            ThreadHelper.waitBriefly();
            instrPermissionCheck = getInstructorPermissionEntityForEmail(courseId, instrEmail);
            elapsedTime += ThreadHelper.WAIT_DURATION;
        }
        if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
            log.severe("Operation did not persist in time: deleteInstructorPermission->"
                    + instrEmail);
        }
        
    }
    
    public void deleteInstructorPermissionsForCourse(String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<InstructorPermission> instrPermissionList = getInstructorPermissionEntitiesForCourse(courseId);
        
        getPM().deletePersistentAll(instrPermissionList);
        getPM().flush();
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
    
    private List<InstructorPermission> getInstructorPermissionEntitiesForCourse(String courseId) {
        
        Query q = getPM().newQuery(InstructorPermission.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<InstructorPermission> instructorPermissionList = (List<InstructorPermission>)q.execute(courseId);
        
        return instructorPermissionList;
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        InstructorPermissionAttributes instrPermissionAttr = (InstructorPermissionAttributes)attributes;
        
        return getInstructorPermissionEntityForEmail(instrPermissionAttr.courseId, instrPermissionAttr.instructorEmail);
    }

}
