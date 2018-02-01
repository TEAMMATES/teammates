package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * A data transfer object for Account entities.
 */
public class AccountAttributes extends EntityAttributes<Account> {

    //Note: be careful when changing these variables as their names are used in *.json files.

    public String googleId;
    public String name;
    public boolean isInstructor;
    public String email;
    public String institute;
    public Date createdAt;
    public StudentProfileAttributes studentProfile;

    public AccountAttributes() {
        // attributes to be set after construction
    }

    public AccountAttributes(String googleId, String name, boolean isInstructor, String email, String institute) {
        this.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
        this.name = SanitizationHelper.sanitizeName(name);
        this.isInstructor = isInstructor;
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.institute = SanitizationHelper.sanitizeTitle(institute);
    }

    public static Builder builder(String googleId, String name, boolean isInstructor, String email, String institute) {
        return new Builder(googleId, name, isInstructor, email, institute);
    }

    public static class Builder {
        private static final String REQUIRED_FIELD_CANNOT_BE_NULL = "Non-null value expected";
        private AccountAttributes accountAttributes;

        public Builder(String googleId, String name, boolean isInstructor, String email, String institute) {
            validateRequiredFields(googleId, name, isInstructor, email, institute);
            accountAttributes = new AccountAttributes(googleId, name, isInstructor, email, institute);
        }

        public AccountAttributes build() {
            return accountAttributes;
        }

        public Builder withCreatedAt(Date createdAt) {
            if (createdAt != null) {
                accountAttributes.createdAt = createdAt;
            }
            return this;
        }

        public Builder withStudentProfile(StudentProfile studentProfile) {
            accountAttributes.studentProfile =
                    studentProfile == null ? null : StudentProfileAttributes.valueOf(studentProfile);
            return this;
        }


        public Builder withStudentProfileAttributes(StudentProfileAttributes studentProfileAttributes) {
            accountAttributes.studentProfile = studentProfileAttributes;
            accountAttributes.studentProfile.sanitizeForSaving();

            return this;
        }

        public Builder withStudentProfileAttributes(String googleId) {
            accountAttributes.studentProfile = StudentProfileAttributes.builder().build();
            accountAttributes.studentProfile.googleId = SanitizationHelper.sanitizeGoogleId(googleId);;

            return this;
        }

        private void validateRequiredFields(Object... objects) {
            for (Object object : objects) {
                Assumption.assertNotNull(REQUIRED_FIELD_CANNOT_BE_NULL, object);
            }
        }
    }

    /**
     * Gets a deep copy of this object.
     */
    public AccountAttributes getCopy() {
        AccountAttributes copy = AccountAttributes.builder(googleId, name, isInstructor, email, institute)
                .withStudentProfileAttributes(googleId)
                .build();;
        copy.studentProfile = this.studentProfile == null ? null : this.studentProfile.getCopy();
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
        List<String> errors = new ArrayList<>();

        addNonEmptyError(validator.getInvalidityInfoForPersonName(name), errors);

        addNonEmptyError(validator.getInvalidityInfoForGoogleId(googleId), errors);

        addNonEmptyError(validator.getInvalidityInfoForEmail(email), errors);

        addNonEmptyError(validator.getInvalidityInfoForInstituteName(institute), errors);

        Assumption.assertNotNull("Non-null value expected for studentProfile", this.studentProfile);
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
        return new Account(googleId, name, isInstructor, email, institute, studentProfile.toEntity());
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
        this.googleId = SanitizationHelper.sanitizeForHtml(googleId);
        this.name = SanitizationHelper.sanitizeForHtml(name);
        this.email = SanitizationHelper.sanitizeForHtml(email);
        this.institute = SanitizationHelper.sanitizeForHtml(institute);
        if (studentProfile == null) {
            return;
        }
        this.studentProfile.sanitizeForSaving();
    }

    public boolean isUserRegistered() {
        return googleId != null && !googleId.isEmpty();
    }

}
