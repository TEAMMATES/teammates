package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Account;

/**
 * A data transfer object for Account entities.
 */
public class AccountAttributes extends EntityAttributes<Account> {

    public String googleId;

    public String name;
    public boolean isInstructor;
    public String email;
    public String institute;
    public Instant createdAt;

    private AccountAttributes(String googleId) {
        this.googleId = googleId;
    }

    public static AccountAttributes valueOf(Account a) {
        AccountAttributes accountAttributes = new AccountAttributes(a.getGoogleId());

        accountAttributes.name = a.getName();
        accountAttributes.isInstructor = a.isInstructor();
        accountAttributes.email = a.getEmail();
        accountAttributes.institute = a.getInstitute();
        accountAttributes.createdAt = a.getCreatedAt();

        return accountAttributes;
    }

    /**
     * Returns a builder for {@link AccountAttributes}.
     */
    public static Builder builder(String googleId) {
        return new Builder(googleId);
    }

    /**
     * Gets a deep copy of this object.
     */
    public AccountAttributes getCopy() {
        AccountAttributes accountAttributes = new AccountAttributes(this.googleId);

        accountAttributes.name = this.name;
        accountAttributes.isInstructor = this.isInstructor;
        accountAttributes.email = this.email;
        accountAttributes.institute = this.institute;
        accountAttributes.createdAt = this.createdAt;

        return accountAttributes;
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

    public String getInstitute() {
        return institute;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForGoogleId(googleId), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(institute), errors);

        // No validation for isInstructor and createdAt fields.

        return errors;
    }

    @Override
    public Account toEntity() {
        return new Account(googleId, name, isInstructor, email, institute);
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, AccountAttributes.class);
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.email).append(this.name)
                .append(this.institute).append(this.googleId);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            AccountAttributes otherAccount = (AccountAttributes) other;
            return Objects.equals(this.email, otherAccount.email)
                    && Objects.equals(this.name, otherAccount.name)
                    && Objects.equals(this.institute, otherAccount.institute)
                    && Objects.equals(this.googleId, otherAccount.googleId);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.googleId = SanitizationHelper.sanitizeGoogleId(googleId);
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.institute = SanitizationHelper.sanitizeTitle(institute);
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.isInstructorOption.ifPresent(s -> isInstructor = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for an account.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String googleId) {
        return new UpdateOptions.Builder(googleId);
    }

    /**
     * A builder class for {@link AccountAttributes}.
     */
    public static class Builder extends BasicBuilder<AccountAttributes, Builder> {

        private AccountAttributes accountAttributes;

        private Builder(String googleId) {
            super(new UpdateOptions(googleId));
            thisBuilder = this;

            accountAttributes = new AccountAttributes(googleId);
        }

        public Builder withName(String name) {
            Assumption.assertNotNull(name);

            accountAttributes.name = name;
            return this;
        }

        public Builder withEmail(String email) {
            Assumption.assertNotNull(email);

            accountAttributes.email = email;
            return this;
        }

        public Builder withInstitute(String institute) {
            Assumption.assertNotNull(institute);

            accountAttributes.institute = institute;
            return this;
        }

        @Override
        public AccountAttributes build() {
            accountAttributes.update(updateOptions);

            return accountAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link AccountAttributes}.
     */
    public static class UpdateOptions {
        private String googleId;

        private UpdateOption<Boolean> isInstructorOption = UpdateOption.empty();

        private UpdateOptions(String googleId) {
            Assumption.assertNotNull(googleId);

            this.googleId = googleId;
        }

        public String getGoogleId() {
            return googleId;
        }

        @Override
        public String toString() {
            return "AccountAttributes.UpdateOptions ["
                    + "googleId = " + googleId
                    + ", isInstructor = " + isInstructorOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String googleId) {
                super(new UpdateOptions(googleId));
                thisBuilder = this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link AccountAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withIsInstructor(boolean isInstructor) {
            updateOptions.isInstructorOption = UpdateOption.of(isInstructor);
            return thisBuilder;
        }

        public abstract T build();
    }

}
