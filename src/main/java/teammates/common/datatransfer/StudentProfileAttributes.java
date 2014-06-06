package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.storage.entity.StudentProfile;

public class StudentProfileAttributes extends EntityAttributes {

    public String googleId;
    public String shortName;
    public String email;
    public String institute;
    public String country;
    /* only accepts "male", "female" or "other" */
    public String gender;
    public String moreInfo;
    public Date modifiedDate;

    public StudentProfileAttributes(String googleId, String shortName, String email,
            String institute, String country, String gender, String moreInfo, Date modifiedDate) {
        this.googleId = googleId;
        this.shortName = shortName;
        this.email = email;
        this.institute = institute;
        this.country = country;
        this.gender = gender;
        this.moreInfo = moreInfo;
        this.modifiedDate = modifiedDate;
    }
    
    public StudentProfileAttributes() {
        // just a container so all can be null
        this.googleId = null;
        this.shortName = null;
        this.email = null;
        this.institute = null;
        this.country = null;
        this.gender = null;
        this.moreInfo = null;
        this.modifiedDate = null;
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        error = validator.getInvalidityInfo(FieldValidator.FieldType.GOOGLE_ID, googleId);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldValidator.FieldType.PERSON_NAME, shortName);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, email);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldValidator.FieldType.INSTITUTE_NAME, institute);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldValidator.FieldType.COUNTRY, country);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldValidator.FieldType.GENDER, gender);
        if(!error.isEmpty()) { errors.add(error); }
        
        // No validation for modified date as it is determined by the system.
        // No validation for More Info. It will properly sanitized.
        
        return errors;
    }

    @Override
    public Object toEntity() {
        return new StudentProfile(googleId, shortName, email, institute, country, gender, moreInfo, modifiedDate);
    }

    @Override
    public String getIdentificationString() {
        return googleId;
    }

    @Override
    public String getEntityTypeAsString() {
        return "StudentProfile";
    }

    @Override
    public void sanitizeForSaving() {
        this.googleId = Sanitizer.sanitizeGoogleId(this.googleId);
        this.shortName = Sanitizer.sanitizeForHtml(this.shortName);
        this.email = Sanitizer.sanitizeForHtml(this.email);
        this.institute = Sanitizer.sanitizeForHtml(this.institute);
        this.country = Sanitizer.sanitizeForHtml(this.country);
        this.gender = Sanitizer.sanitizeForHtml(this.gender);
        this.moreInfo = Sanitizer.sanitizeForHtml(this.moreInfo);
    }

}
