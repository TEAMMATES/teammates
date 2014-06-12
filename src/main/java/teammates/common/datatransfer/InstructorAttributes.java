package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Utils;
import teammates.storage.entity.Instructor;

/**
 * The data transfer class for Instructor entities.
 */
public class InstructorAttributes extends EntityAttributes {
    
    //Note: be careful when changing these variables as their names are used in *.json files.
    public String googleId;
    public String name;
    public String email;
    public String courseId;
    public String key;
    public String role;
    public String displayedName;
    public String instructorPrivilegesAsText;
    public transient InstructorPrivileges privileges;

    public InstructorAttributes(String googleId, String courseId, String name, String email, String role,
            String displayedName, String instructorPrivilegesAsText) {        
        this.googleId = Sanitizer.sanitizeGoogleId(googleId);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.name = Sanitizer.sanitizeName(name);
        this.email = Sanitizer.sanitizeEmail(email);
        this.role = Sanitizer.sanitizeName(role);
        this.displayedName = Sanitizer.sanitizeName(displayedName);
        this.instructorPrivilegesAsText = Sanitizer.sanitizeTextField(instructorPrivilegesAsText);
    }
    
    public InstructorAttributes(Instructor instructor) {
        this.googleId = instructor.getGoogleId();
        this.courseId = instructor.getCourseId();
        this.name = instructor.getName();
        this.email = instructor.getEmail();
        this.key = instructor.getRegistrationKey();
        this.role = instructor.getRole();
        this.displayedName = instructor.getDisplayedName();
        this.instructorPrivilegesAsText = instructor.getInstructorPrivilegesAsText();
    }

    public InstructorAttributes() {
        
    }
    
    public boolean isRegistered() {
        return googleId != null;
    }

    public Instructor toEntity() {
        if (key != null) {
            return new Instructor(googleId, courseId, name, email, key, role, displayedName, instructorPrivilegesAsText);
        } else {
            return new Instructor(googleId, courseId, name, email, role, displayedName, instructorPrivilegesAsText);
        }
    }

    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        if (googleId != null) {
            error = validator.getInvalidityInfo(FieldType.GOOGLE_ID, googleId);
            if(!error.isEmpty()) {
                errors.add(error);
            }
        }
        
        error= validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.PERSON_NAME, name);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.EMAIL, email);
        if(!error.isEmpty()) { errors.add(error); }
        
        return errors;
    }
    
    public String toString(){
        return Utils.getTeammatesGson().toJson(this,InstructorAttributes.class);
    }

    @Override
    public String getIdentificationString() {
        return this.courseId + "/" + this.email;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Instructor";
    }

    @Override
    public void sanitizeForSaving() {
        this.googleId = Sanitizer.sanitizeGoogleId(this.googleId);
        this.name = Sanitizer.sanitizeName(this.name);
        this.email = Sanitizer.sanitizeEmail(this.email);
        this.courseId = Sanitizer.sanitizeTitle(this.courseId);
        
    }
}
