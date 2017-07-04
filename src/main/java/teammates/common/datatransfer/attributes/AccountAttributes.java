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
     * Creates a new AccountAttributes with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * {@code true} for {@code isInstructor} <br>
     * {@code new Date(0)} for {@code createdAt} <br>
     * {@code new StudentProfileAttributes()} for {@code studentProfile}
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

    /**
     * valueOf(Account account) usages.
     * @see teammates.storage.api.AccountsDb#getAccount(String, boolean)
     * @see teammates.storage.api.AccountsDb#getInstructorAccounts()
     */
    public static AccountAttributes valueOf(Account account) {
        return new AccountAttributesBuilder(
                account.getGoogleId(),
                account.getName(),
                account.getEmail(),
                account.getInstitute())
                .withCreatedAt(account.getCreatedAt())
                .withIsInstructor(account.isInstructor())
                .withStudentProfileAttributes(
                        (account.getStudentProfile() == null)
                                ? null
                                : new StudentProfileAttributes(account.getStudentProfile()))
                .build();
    }

    /**
     * Gets a deep copy of this object.
     */
    public AccountAttributes getCopy() {
        return new AccountAttributesBuilder(
                googleId, name, email, institute)
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
     * Usages:
     * @see teammates.logic.core.AccountsLogic#joinCourseForInstructorWithInstitute(String, String, String)
     * @see teammates.logic.backdoor.BackDoorLogic #persistDataBundle(DataBundle)
     * @see AccountAttributes#AccountAttributes(AccountAttributesBuilder)
     * @see AccountAttributes#valueOf(Account)
     * @see AccountAttributes#getCopy()
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
