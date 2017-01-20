package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
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
        studentProfile =
                a.getStudentProfile() == null ? null : new StudentProfileAttributes(a.getStudentProfile());
    }
    
    public AccountAttributes() {
        // attributes to be set after construction
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
    
    /**
     * Gets a deep copy of this object.
     */
    public AccountAttributes getCopy() {
        // toEntity() requires a non-null student profile
        boolean isStudentProfileNull = this.studentProfile == null;
        if (isStudentProfileNull) {
            this.studentProfile = new StudentProfileAttributes();
        }
        AccountAttributes copy = new AccountAttributes(this.toEntity());
        if (isStudentProfileNull) {
            copy.studentProfile = null;
            this.studentProfile = null;
        }
        return copy;
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
        return StringHelper.truncateLongId(googleId);
    }

    public String getInstitute() {
        return institute;
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        error = validator.getInvalidityInfoForPersonName(name);
        if (!error.isEmpty()) {
            errors.add(error);
        }
        
        error = validator.getInvalidityInfoForGoogleId(googleId);
        if (!error.isEmpty()) {
            errors.add(error);
        }
        
        error = validator.getInvalidityInfoForEmail(email);
        if (!error.isEmpty()) {
            errors.add(error);
        }
        
        error = validator.getInvalidityInfoForInstituteName(institute);
        if (!error.isEmpty()) {
            errors.add(error);
        }
        
        Assumption.assertTrue("Non-null value expected for studentProfile", this.studentProfile != null);
        // only check profile if the account is proper
        if (errors.isEmpty()) {
            errors.addAll(this.studentProfile.getInvalidityInfo());
        }
        
        //No validation for isInstructor and createdAt fields.
        return errors;
    }

    @Override
    public Account toEntity() {
        Assumption.assertNotNull(this.studentProfile);
        return new Account(googleId, name, isInstructor, email, institute, (StudentProfile) studentProfile.toEntity());
    }
    
    @Override
    public String toString() {
        return JsonUtils.toJson(this, AccountAttributes.class);
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
        return JsonUtils.toJson(this, AccountAttributes.class);
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
        return googleId != null && !googleId.isEmpty();
    }
    
}
