package teammates.common.datatransfer;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * A data transfer object for Account entities.
 */
public class AccountAttributes extends EntityAttributes {
    
    //Note: be careful when changing these variables as their names are used in *.json files.
    
    public String googleId;
    public String name;
    public boolean isInstructor;
    public String email;
    public String institute;
    public Date createdAt;
    public StudentProfileAttributes studentProfile;
    
    
    public AccountAttributes(Account a) {
        googleId = a.getGoogleId();
        name = a.getName();
        isInstructor = a.isInstructor();
        email = a.getEmail();
        institute = a.getInstitute();
        createdAt = a.getCreatedAt();
        studentProfile = a.getStudentProfile() == null ? null : 
            new StudentProfileAttributes(a.getStudentProfile());
    }
    
    public AccountAttributes() {
    }
    
    public AccountAttributes(String googleId, String name, boolean isInstructor,
                String email, String institute, StudentProfileAttributes studentProfileAttributes) {
        this.googleId = Sanitizer.sanitizeGoogleId(googleId);
        this.name = Sanitizer.sanitizeName(name);
        this.isInstructor = isInstructor;
        this.email = Sanitizer.sanitizeEmail(email);
        this.institute = Sanitizer.sanitizeTitle(institute);
        this.studentProfile = studentProfileAttributes;
        this.studentProfile.sanitizeForSaving();
        
    }
    
    public AccountAttributes(String googleId, String name, boolean isInstructor,
                String email, String institute) {
        this.googleId = Sanitizer.sanitizeGoogleId(googleId);
        this.name = Sanitizer.sanitizeName(name);
        this.isInstructor = isInstructor;
        this.email = Sanitizer.sanitizeEmail(email);
        this.institute = Sanitizer.sanitizeTitle(institute);
        this.studentProfile = new StudentProfileAttributes();
        this.studentProfile.googleId = this.googleId;        
    }
    
    public boolean isInstructor() {
        return isInstructor;
    }
    
    public String getGoogleId() {
        return googleId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getTruncatedGoogleId() {
        return StringHelper.truncate(googleId, Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH);
    }

    public String getInstitute() {
        return institute;
    }

    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        error= validator.getInvalidityInfo(FieldValidator.FieldType.PERSON_NAME, name);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldValidator.FieldType.GOOGLE_ID, googleId);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, email);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldValidator.FieldType.INSTITUTE_NAME, institute);
        if(!error.isEmpty()) { errors.add(error); }
        
        Assumption.assertTrue("Non-null value expected for studentProfile", this.studentProfile != null);
        // only check profile if the account is proper
        if (errors.isEmpty()) {
            errors.addAll(this.studentProfile.getInvalidityInfo());
        }
        
        //No validation for isInstructor and createdAt fields.
        return errors;
    }

    public Account toEntity() {
        Assumption.assertNotNull(this.studentProfile);
        return new Account(googleId, name, isInstructor, email, institute, (StudentProfile) studentProfile.toEntity());
    }
    
    public String toString(){
        return Utils.getTeammatesGson().toJson(this, AccountAttributes.class);
    }

    @Override
    public String getIdentificationString() {
        return this.googleId;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Account";
    }

    @Override
    public String getBackupIdentifier() {
        return "Account";
    }
    
    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, AccountAttributes.class);
    }
    
    @Override
    public void sanitizeForSaving() {
        this.googleId = Sanitizer.sanitizeForHtml(googleId);
        this.name = Sanitizer.sanitizeForHtml(name);
        this.email = Sanitizer.sanitizeForHtml(email);
        this.institute = Sanitizer.sanitizeForHtml(institute);
        this.studentProfile.sanitizeForSaving();
    }
    
    public boolean isUserRegistered() {
        return (googleId != null && !googleId.isEmpty());
    }
    
}
