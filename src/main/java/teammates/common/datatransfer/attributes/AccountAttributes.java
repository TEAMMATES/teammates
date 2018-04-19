package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
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
    public Instant createdAt;
    public StudentProfileAttributes studentProfile;

    AccountAttributes() {
        // Empty constructor for builder to construct object
    }

    public static AccountAttributes valueOf(Account a) {
        return builder()
                .withGoogleId(a.getGoogleId())
                .withName(a.getName())
                .withIsInstructor(a.isInstructor())
                .withInstitute(a.getInstitute())
                .withEmail(a.getEmail())
                .withCreatedAt(a.getCreatedAt())
                .withStudentProfile(a.getStudentProfile())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * A Builder class for {@link AccountAttributes}.
     */
    public static class Builder {
        private AccountAttributes accountAttributes;

        public Builder() {
            accountAttributes = new AccountAttributes();
        }

        public Builder withCreatedAt(Instant createdAt) {
            accountAttributes.createdAt = createdAt;
            return this;
        }

        public Builder withStudentProfile(StudentProfile studentProfile) {
            accountAttributes.studentProfile =
                    studentProfile == null ? null : StudentProfileAttributes.valueOf(studentProfile);
            return this;
        }

        public Builder withStudentProfileAttributes(StudentProfileAttributes studentProfileAttributes) {
            accountAttributes.studentProfile = studentProfileAttributes;

            return this;
        }

        public Builder withDefaultStudentProfileAttributes(String googleId) {
            accountAttributes.studentProfile = StudentProfileAttributes.builder(googleId)
                    .build();

            return this;
        }

        public Builder withGoogleId(String googleId) {
            accountAttributes.googleId = googleId;
            return this;
        }

        public Builder withName(String name) {
            accountAttributes.name = name;
            return this;
        }

        public Builder withIsInstructor(boolean isInstructor) {
            accountAttributes.isInstructor = isInstructor;
            return this;
        }

        public Builder withEmail(String email) {
            accountAttributes.email = email;
            return this;
        }

        public Builder withInstitute(String institute) {
            accountAttributes.institute = institute;
            return this;
        }

        public AccountAttributes build() {
            accountAttributes.googleId = SanitizationHelper.sanitizeGoogleId(accountAttributes.googleId);
            accountAttributes.name = SanitizationHelper.sanitizeName(accountAttributes.name);
            accountAttributes.email = SanitizationHelper.sanitizeEmail(accountAttributes.email);
            accountAttributes.institute = SanitizationHelper.sanitizeTitle(accountAttributes.institute);
            if (accountAttributes.studentProfile != null) {
                accountAttributes.studentProfile.sanitizeForSaving();
            }

            return accountAttributes;
        }

    }

    /**
     * Gets a deep copy of this object.
     */
    public AccountAttributes getCopy() {
        return AccountAttributes.builder()
                .withGoogleId(googleId)
                .withName(name)
                .withEmail(email)
                .withInstitute(institute)
                .withIsInstructor(isInstructor)
                .withStudentProfileAttributes(this.studentProfile == null ? null : this.studentProfile.getCopy())
                .build();

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
