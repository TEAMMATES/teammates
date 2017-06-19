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
public class AccountAttributes extends EntityAttributes {

    public static final Date DEFAULT_DATE = new Date(0); // "Thu Jan 01 01:00:00 GMT 1970"
    public static final StudentProfileAttributes DEFAULT_STUDENT_PROFILE_ATTRIBUTES = new StudentProfileAttributes();

    //Note: be careful when changing these variables as their names are used in *.json files.

    // Required fields
    public String googleId;
    public String name;
    public String email;
    public String institute;

    // Optional fields
    public boolean isInstructor;
    public Date createdAt;
    public StudentProfileAttributes studentProfile;

    public AccountAttributes() {
        // attributes to be set after construction
    }

    /**
     * AccountAttributes constructor with AccountAttributesBuilder as a parameter.
     * Default values set for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * {@code true} for {@code isInstructor} <br>
     * {@code new Date(0)} for {@code createdAt}
     * {@code new StudentProfileAttributes()} for {@code studentProfile}
     */
    public AccountAttributes(AccountAttributesBuilder builder) {
        this.googleId = SanitizationHelper.sanitizeGoogleId(builder.googleId);
        this.name = SanitizationHelper.sanitizeName(builder.name);
        this.email = SanitizationHelper.sanitizeEmail(builder.email);
        this.institute = SanitizationHelper.sanitizeTitle(builder.institute);

        this.isInstructor = builder.isInstructor;
        this.createdAt = builder.createdAt;
        this.studentProfile = builder.studentProfile;
    }

    public static AccountAttributes valueOf(Account account) {
        return new AccountAttributesBuilder(
                account.getGoogleId(),
                account.getName(),
                account.getEmail(),
                account.getInstitute())
                .withCreatedAt(account.getCreatedAt())
                .withIsInstructor(account.isInstructor())
                .withStudentProfileAttributes(account.getStudentProfile()
                        == null ? null : new StudentProfileAttributes(account.getStudentProfile()))
                .build();
    }

    @Override
    public Account toEntity() {
        Assumption.assertNotNull(this.studentProfile);
        return new Account(googleId, name, isInstructor, email, institute, (StudentProfile) studentProfile.toEntity());
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

        AccountAttributes copy = new AccountAttributesBuilder(googleId, name, email, institute)
                .withStudentProfileAttributes(studentProfile)
                .withCreatedAt(createdAt)
                .withIsInstructor(isInstructor)
                .build();

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

        addNonEmptyError(validator.getInvalidityInfoForPersonName(name), errors);

        addNonEmptyError(validator.getInvalidityInfoForGoogleId(googleId), errors);

        addNonEmptyError(validator.getInvalidityInfoForEmail(email), errors);

        addNonEmptyError(validator.getInvalidityInfoForInstituteName(institute), errors);

        Assumption.assertTrue("Non-null value expected for studentProfile", this.studentProfile != null);
        // Only check profile if the account is proper
        if (errors.isEmpty()) {
            errors.addAll(this.studentProfile.getInvalidityInfo());
        }

        // No validation for isInstructor and createdAt fields.
        return errors;
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

    /**
     * AccountAttributesBuilder class for {@link AccountAttributes}.
     */
    public static class AccountAttributesBuilder {

        // Required fields
        public final String googleId;
        public final String name;
        public final String email;
        public String institute;

        // Optional fields
        public Boolean isInstructor;
        public Date createdAt;
        public StudentProfileAttributes studentProfile;

        public AccountAttributesBuilder(String googleId, String name, String email, String institute) {
            this.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
            this.name = SanitizationHelper.sanitizeName(name);
            this.email = SanitizationHelper.sanitizeEmail(email);
            this.institute = SanitizationHelper.sanitizeTitle(institute);

            this.studentProfile = DEFAULT_STUDENT_PROFILE_ATTRIBUTES;
            this.studentProfile.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
            this.isInstructor = true;
            this.createdAt = DEFAULT_DATE;
        }

        public AccountAttributesBuilder withStudentProfileAttributes(StudentProfileAttributes studentProfile) {
            this.studentProfile = (studentProfile == null)
                    ? DEFAULT_STUDENT_PROFILE_ATTRIBUTES
                    : studentProfile;
            return this;
        }

        public AccountAttributesBuilder withIsInstructor(Boolean isInstructor) {
            if (isInstructor != null) {
                this.isInstructor = isInstructor;
            }
            return this;
        }

        public AccountAttributesBuilder withCreatedAt(Date createdAt) {
            if (createdAt != null) {
                this.createdAt = createdAt;
            }
            return this;
        }

        public AccountAttributes build() {
            return new AccountAttributes(this);
        }
    }

}
