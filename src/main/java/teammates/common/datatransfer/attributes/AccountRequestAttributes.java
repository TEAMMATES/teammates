package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.AccountRequest;

/**
 * The data transfer object for {@link AccountRequest} entities.
 */
public class AccountRequestAttributes extends EntityAttributes<AccountRequest> {

    private String email;
    private String name;
    private String institute;
    private transient String registrationKey;
    private transient Instant createdAt;
    private transient Instant deletedAt;

    private AccountRequestAttributes(String email) {
        this.email = email;
        this.registrationKey = null;
        this.institute = null;
        this.name = null;
        this.createdAt = Instant.now();
        this.deletedAt = null;
    }

    /**
     * Gets the {@link AccountRequestAttributes} instance of the given {@link AccountRequest}.
     */
    public static AccountRequestAttributes valueOf(AccountRequest accountRequest) {
        AccountRequestAttributes accountRequestAttributes = new AccountRequestAttributes(accountRequest.getEmail());

        accountRequestAttributes.registrationKey = accountRequest.getRegistrationKey();
        accountRequestAttributes.name = accountRequest.getName();
        accountRequestAttributes.institute = accountRequest.getInstitute();

        if (accountRequest.getCreatedAt() != null) {
            accountRequestAttributes.createdAt = accountRequest.getCreatedAt();
        }
        accountRequestAttributes.deletedAt = accountRequest.getDeletedAt();

        return accountRequestAttributes;
    }

    /**
     * Returns a builder for {@link AccountRequestAttributes}.
     */
    public static Builder builder(String email) {
        return new Builder(email);
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitute() {
        return institute;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isAccountRequestDeleted() {
        return this.deletedAt != null;
    }

    @Override
    public List<String> getInvalidityInfo() {

        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(getName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(getInstitute()), errors);

        return errors;
    }

    @Override
    public AccountRequest toEntity() {
        return new AccountRequest(getEmail(), getRegistrationKey(), getName(),
                getInstitute(), getCreatedAt(), getDeletedAt());
    }

    @Override
    public String toString() {
        return "[" + AccountRequestAttributes.class.getSimpleName() + "] email: " + getEmail()
                + "registrationKey: " + getRegistrationKey()
                + " name: " + getName() + " institute: " + getInstitute();
    }

    @Override
    public int hashCode() {
        return (this.email + this.registrationKey + this.name + this.institute).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            AccountRequestAttributes otherCourse = (AccountRequestAttributes) other;
            return Objects.equals(this.registrationKey, otherCourse.registrationKey)
                    && Objects.equals(this.email, otherCourse.email)
                    && Objects.equals(this.institute, otherCourse.institute)
                    && Objects.equals(this.name, otherCourse.name);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.institute = SanitizationHelper.sanitizeTitle(institute);
        this.name = SanitizationHelper.sanitizeName(name);
        this.email = SanitizationHelper.sanitizeEmail(email);
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.nameOption.ifPresent(s -> name = s);
        updateOptions.registrationKeyOption.ifPresent(s -> registrationKey = s);
        updateOptions.instituteOption.ifPresent(s -> institute = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a accountRequest.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String email) {
        return new UpdateOptions.Builder(email);
    }

    /**
     * A builder for {@link AccountRequestAttributes}.
     */
    public static class Builder extends BasicBuilder<AccountRequestAttributes, Builder> {

        private final AccountRequestAttributes accountRequestAttributes;

        private Builder(String email) {
            super(new UpdateOptions(email));
            thisBuilder = this;

            accountRequestAttributes = new AccountRequestAttributes(email);
        }

        @Override
        public AccountRequestAttributes build() {
            accountRequestAttributes.update(updateOptions);

            return accountRequestAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link AccountAttributes}.
     */
    public static class UpdateOptions {
        private String email;

        private UpdateOption<String> nameOption = UpdateOption.empty();
        private UpdateOption<String> registrationKeyOption = UpdateOption.empty();
        private UpdateOption<String> instituteOption = UpdateOption.empty();

        private UpdateOptions(String email) {
            assert email != null;

            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "AccountRequestAttributes.UpdateOptions ["
                    + "email = " + email
                    + ", name = " + nameOption
                    + ", registrationKey = " + registrationKeyOption
                    + ", institute = " + instituteOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String email) {
                super(new UpdateOptions(email));
                thisBuilder = this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link AccountRequestAttributes} related classes.
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

        public B withName(String name) {
            assert name != null;

            updateOptions.nameOption = UpdateOption.of(name);
            return thisBuilder;
        }

        public B withRegistrationKey(String registrationKey) {
            assert registrationKey != null;

            updateOptions.registrationKeyOption = UpdateOption.of(registrationKey);
            return thisBuilder;
        }

        public B withInstitute(String institute) {
            assert institute != null;

            updateOptions.instituteOption = UpdateOption.of(institute);
            return thisBuilder;
        }

        public abstract T build();

    }
}
