package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.util.Config;
import teammates.common.util.Const;
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
    private Instant registeredAt;
    private transient String registrationKey;
    private transient Instant createdAt;

    private AccountRequestAttributes(String email, String institute) {
        this.email = email;
        this.institute = institute;

        this.name = null;
        this.registrationKey = null;
        this.registeredAt = null;
        this.createdAt = null;
    }

    /**
     * Gets the {@link AccountRequestAttributes} instance of the given {@link AccountRequest}.
     */
    public static AccountRequestAttributes valueOf(AccountRequest accountRequest) {
        AccountRequestAttributes accountRequestAttributes = new AccountRequestAttributes(accountRequest.getEmail(),
                accountRequest.getInstitute());

        accountRequestAttributes.registrationKey = accountRequest.getRegistrationKey();
        accountRequestAttributes.name = accountRequest.getName();
        accountRequestAttributes.registeredAt = accountRequest.getRegisteredAt();
        accountRequestAttributes.createdAt = accountRequestAttributes.getCreatedAt();
        
        return accountRequestAttributes;
    }

    /**
     * Returns a builder for {@link AccountRequestAttributes}.
     */
    public static Builder builder(String email, String institute) {
        return new Builder(email, institute);
    }

    public String getRegistrationKey() {
        return registrationKey;
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

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getRegistrationUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withIsCreatingAccount(String.valueOf(true))
                .withRegistrationKey(this.getRegistrationKey())
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();
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
        AccountRequest accountRequest = new AccountRequest(getEmail(), getName(), getInstitute());

        if (this.getRegistrationKey() != null) {
            accountRequest.setRegistrationKey(this.getRegistrationKey());
        }

        if (this.getCreatedAt() != null) {
            accountRequest.setCreatedAt(this.getCreatedAt());
        }

        if (this.getRegisteredAt() != null) {
            accountRequest.setRegisteredAt(this.getRegisteredAt());
        }

        return accountRequest;
    }

    @Override
    public String toString() {
        return "[" + AccountRequestAttributes.class.getSimpleName() + "] email: " 
                + getEmail() + " name: " + getName() + " institute: " + getInstitute();
    }

    @Override
    public int hashCode() {
        return (this.email + this.name + this.institute).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            AccountRequestAttributes otherAccountRequest = (AccountRequestAttributes) other;
            return Objects.equals(this.email, otherAccountRequest.email)
                    && Objects.equals(this.institute, otherAccountRequest.institute)
                    && Objects.equals(this.name, otherAccountRequest.name);
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
        updateOptions.registrationKeyOption.ifPresent(s -> registrationKey = s);
        updateOptions.nameOption.ifPresent(s -> name = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for an accountRequest.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String email, String institute) {
        return new UpdateOptions.Builder(email, institute);
    }

    /**
     * A builder for {@link AccountRequestAttributes}.
     */
    public static class Builder extends BasicBuilder<AccountRequestAttributes, Builder> {

        private final AccountRequestAttributes accountRequestAttributes;

        private Builder(String email, String institute) {
            super(new UpdateOptions(email, institute));
            thisBuilder = this;

            accountRequestAttributes = new AccountRequestAttributes(email, institute);
        }

        @Override
        public AccountRequestAttributes build() {
            accountRequestAttributes.update(updateOptions);

            return accountRequestAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link AccountRequestAttributes}.
     */
    public static class UpdateOptions {
        private String email;
        private String institute;

        private UpdateOption<String> nameOption = UpdateOption.empty();
        private UpdateOption<String> registrationKeyOption = UpdateOption.empty();

        private UpdateOptions(String email, String institute) {
            assert email != null;
            assert institute != null;

            this.email = email;
            this.institute = institute;
        }

        @Override
        public String toString() {
            return "AccountRequestAttributes.UpdateOptions ["
                    + ", email = " + email
                    + ", institute = " + institute
                    + ", name = " + nameOption
                    + ", registrationKey = " + registrationKeyOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String email, String institute) {
                super(new UpdateOptions(email, institute));
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

        public B withRegistrationKey(String registationKey) {
            assert registationKey != null;

            updateOptions.registrationKeyOption = UpdateOption.of(registationKey);
            return thisBuilder;
        }

        public abstract T build();

    }
}
