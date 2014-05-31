package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
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
    
    //TODO: remove these after implementing more user friendly way of adding instructors
    public static final String ERROR_INSTRUCTOR_LINE_NULL = "Instructor line was null";
    public static final String ERROR_INFORMATION_INCORRECT = "Please enter information in the format: GoogleID | Name | Email\n";

    public InstructorAttributes(String id, String courseId, String name, String email) {        
        this.googleId = Sanitizer.sanitizeGoogleId(id);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.name = Sanitizer.sanitizeName(name);
        this.email = Sanitizer.sanitizeEmail(email);
    }
    
    public InstructorAttributes(Instructor instructor) {
        this.googleId = instructor.getGoogleId();
        this.courseId = instructor.getCourseId();
        this.name = instructor.getName();
        this.email = instructor.getEmail();
        this.key = instructor.getRegistrationKey();
    }

    public InstructorAttributes() {
        
    }
    
    //TODO: remove these after implementing more user friendly way of adding instructors
    public InstructorAttributes(String courseId, String enrolLine) throws InvalidParametersException {
        Assumption.assertNotNull(ERROR_INSTRUCTOR_LINE_NULL, enrolLine);
        
        String[] parts = enrolLine.replace("|", "\t").split("\t");
        
        if (parts.length != 3) {
            throw new InvalidParametersException(ERROR_INFORMATION_INCORRECT);
        }
        
        this.googleId = Sanitizer.sanitizeGoogleId(parts[0]);
        Assumption.assertIsEmpty(new FieldValidator().getInvalidityInfo(FieldType.GOOGLE_ID, googleId));

        this.courseId = courseId;
        this.name = parts[1].trim();
        this.email = parts[2].trim();
    }
    
    public boolean isRegistered() {
        return googleId != null;
    }

    public Instructor toEntity() {
        if (key != null) {
            return new Instructor(googleId, courseId, name, email, key);
        } else {
            return new Instructor(googleId, courseId, name, email);
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
        //TODO: consider changing to the same format as the one in StudentAttributes
        return this.email + ", " + this.courseId;
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
        this.googleId = Sanitizer.sanitizeForHtml(this.googleId);
        this.name = Sanitizer.sanitizeForHtml(this.name);
        this.email = Sanitizer.sanitizeForHtml(this.email);
        this.courseId = Sanitizer.sanitizeForHtml(this.courseId);
    }
}
