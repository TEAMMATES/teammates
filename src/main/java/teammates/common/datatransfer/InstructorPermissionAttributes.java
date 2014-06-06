package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap<String, HashMap<String, HashMap<String, Boolean>>> privileges;
    
    public InstructorPermissionAttributes(String instrEmail, String courseId, String role, Text access) {
        this.instructorEmail = Sanitizer.sanitizeEmail(instrEmail);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.role = Sanitizer.sanitizeName(role);
        this.access = Sanitizer.sanitizeTextField(access);
        this.privileges = getPrivilegesFromAccess(access);
    }
    
    public InstructorPermissionAttributes(String instrEmail, String courseId, String role, 
            HashMap<String, HashMap<String, HashMap<String, Boolean>>> privileges) {
        this.instructorEmail = Sanitizer.sanitizeEmail(instrEmail);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.role = Sanitizer.sanitizeName(role);
        this.privileges = privileges;
        this.access = getAccessFromPrivileges(privileges);
    }
    
    public InstructorPermissionAttributes(InstructorPermission instructorPermission) {
        this.instructorEmail = instructorPermission.getInstructorEmail();
        this.courseId = instructorPermission.getCourseId();
        this.role = instructorPermission.getRole();
        this.access = instructorPermission.getAccess();
    }
    
    private Text getAccessFromPrivileges(HashMap<String, HashMap<String, HashMap<String, Boolean>>> privileges) {
        String accessString = gson.toJson(privileges);
        
        return new Text(accessString);
    }
    
    @SuppressWarnings("unchecked")
    private HashMap<String, HashMap<String, HashMap<String, Boolean>>> getPrivilegesFromAccess(Text access) {
        String accessString = access.getValue();
        HashMap<String, HashMap<String, HashMap<String, Boolean>>> privileges = gson.fromJson(accessString, HashMap.class);
        
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
        
        return null;
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
    
    @SuppressWarnings("rawtypes")
    public HashMap getPrivileges() {
        return this.privileges;
    }

}
