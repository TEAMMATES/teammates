package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Utils;
import teammates.storage.entity.InstructorPermission;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.Text;

public class InstructorPermissionAttributes extends EntityAttributes {
    
    private static Gson gson = Utils.getTeammatesGson();
    
    private String instructorEmail;
    private String courseId;
    private String role;
    private Text access;
    private InstructorPrivileges privileges;
    
    public InstructorPermissionAttributes(String instrEmail, String courseId, String role, Text access) {
        this.instructorEmail = Sanitizer.sanitizeEmail(instrEmail);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.role = Sanitizer.sanitizeName(role);
        this.access = Sanitizer.sanitizeTextField(access);
        this.privileges = getPrivilegesFromAccess(access);
    }
    
    public InstructorPermissionAttributes(String instrEmail, String courseId, String role, InstructorPrivileges privileges) {
        this.instructorEmail = Sanitizer.sanitizeEmail(instrEmail);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.role = Sanitizer.sanitizeName(role);
        this.privileges = privileges;
        this.access = getAccessFromPrivileges(privileges);
    }
    
    public InstructorPermissionAttributes(InstructorPermission instructorPermission) {
        this(instructorPermission.getInstructorEmail(), instructorPermission.getCourseId(),
                instructorPermission.getRole(), instructorPermission.getAccess());
    }
    
    private Text getAccessFromPrivileges(InstructorPrivileges privileges) {
        String accessString = gson.toJson(privileges);
        
        return new Text(accessString);
    }
    
    private InstructorPrivileges getPrivilegesFromAccess(Text access) {
        String accessString = access.getValue();
        InstructorPrivileges privileges = gson.fromJson(accessString, InstructorPrivileges.class);
        
        return privileges;
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error = null;
        
        error= validator.getInvalidityInfo(FieldType.EMAIL, instructorEmail);
        if (!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
        if (!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.INTRUCTOR_ROLE, role);
        if (!error.isEmpty()) { errors.add(error); }
        
        return errors;
    }

    @Override
    public InstructorPermission toEntity() {
        return new InstructorPermission(this.instructorEmail, this.courseId, this.role, this.access);
    }

    @Override
    public String getIdentificationString() {
        return "permission for " + this.instructorEmail + ", " + this.courseId;
    }

    @Override
    public String getEntityTypeAsString() {
        return "instructor permission";
    }

    @Override
    public void sanitizeForSaving() {
        this.instructorEmail = Sanitizer.sanitizeEmail(this.instructorEmail);
        this.courseId = Sanitizer.sanitizeTitle(this.courseId);
        this.role = Sanitizer.sanitizeName(this.role);
        this.access = Sanitizer.sanitizeTextField(this.access);
    }
    
    public String toString() {
        return gson.toJson(this, InstructorPermissionAttributes.class);
    }
    
    public String getAccessAsString() {
        return this.access.getValue();
    }
    
    public InstructorPrivileges getPrivileges() {
        return this.privileges;
    }

}
