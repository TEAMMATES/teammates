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

/**
 * A data transfer object for Account entities.
 */
public class AccountAttributes extends EntityAttributes<Account> {

    public static final Date DEFAULT_DATE = new Date();
    public static final StudentProfileAttributes DEFAULT_STUDENT_PROFILE_ATTRIBUTES =
            StudentProfileAttributes.builder().build();

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

    /**
     * Creates a new AccountAttributes with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * <ul>
     * <li>{@code true} for {@code isInstructor}</li>
     * <li>{@code new Date()} for {@code createdAt}</li>
     * <li>{@code new StudentProfileAttributes()} for {@code studentProfile}</li>
     * </ul>
     */
    AccountAttributes(AccountAttributesBuilder builder) {
        this.googleId = SanitizationHelper.sanitizeGoogleId(builder.googleId);
        this.name = SanitizationHelper.sanitizeName(builder.name);
        this.email = SanitizationHelper.sanitizeEmail(builder.email);
        this.institute = SanitizationHelper.sanitizeTitle(builder.institute);

        this.isInstructor = builder.isInstructor;
        this.createdAt = builder.createdAt;
        this.studentProfile = builder.studentProfile;
    }

    public static AccountAttributes valueOf(Account account) {
        return new AccountAttributesBuilder()
                .withGoogleId(account.getGoogleId())
                .withName(account.getName())
                .withEmail(account.getEmail())
                .withInstitute(account.getInstitute())
                .withCreatedAt(account.getCreatedAt())
                .withIsInstructor(account.isInstructor())
                .withStudentProfileAttributes(account.getStudentProfile() == null
                        ? null : StudentProfileAttributes.valueOf(account.getStudentProfile()))
                .build();
    }

    /**
     * Gets a deep copy of this object.
     */
    public AccountAttributes getCopy() {
        return new AccountAttributesBuilder()
                .withGoogleId(googleId)
                .withName(name)
                .withEmail(email)
                .withInstitute(institute)
                .withStudentProfileAttributes(this.studentProfile == null ? null : this.studentProfile.getCopy())
                .withCreatedAt(createdAt)
                .withIsInstructor(isInstructor)
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

        Assumption.assertTrue("Non-null value expected for studentProfile", this.studentProfile != null);
        // Only check profile if the account is proper
        if (errors.isEmpty()) {
            errors.addAll(this.studentProfile.getInvalidityInfo());
        }

        // No validation for isInstructor and createdAt fields.
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

    /**
     * AccountAttributesBuilder class for {@link AccountAttributes}.<br>
     */
    public static class AccountAttributesBuilder {

        // Required fields
        public String googleId;
        public String name;
        public String email;
        public String institute;

        // Optional fields
        public Boolean isInstructor = true;
        public Date createdAt = DEFAULT_DATE;
        public StudentProfileAttributes studentProfile = DEFAULT_STUDENT_PROFILE_ATTRIBUTES;

        public AccountAttributesBuilder withGoogleId(String googleId) {
            this.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
            this.studentProfile.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
            return this;
        }

        public AccountAttributesBuilder withName(String name) {
            if (name != null) {
                this.name = SanitizationHelper.sanitizeName(name);
            }
            return this;
        }

        public AccountAttributesBuilder withEmail(String email) {
            if (email != null) {
                this.email = SanitizationHelper.sanitizeEmail(email);
            }
            return this;
        }

        public AccountAttributesBuilder withInstitute(String institute) {
            if (institute != null) {
                this.institute = SanitizationHelper.sanitizeTitle(institute);
            }
            return this;
        }

        public AccountAttributesBuilder withStudentProfileAttributes(StudentProfileAttributes studentProfile) {
            if (studentProfile != null) {
                this.studentProfile = studentProfile;
            }
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
