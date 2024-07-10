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
public final class AccountRequestAttributes extends EntityAttributes<AccountRequest> {
    private String id;
    private String email;
    private String name;
    private String institute;
    private Instant registeredAt;
    private Instant createdAt;
    private transient String registrationKey;

    private AccountRequestAttributes(String email, String institute, String name) {
        this.email = email;
        this.institute = institute;
        this.name = name;
        this.registrationKey = null;
        this.registeredAt = null;
        this.createdAt = null;
    }

    /**
     * Gets the {@link AccountRequestAttributes} instance of the given {@link AccountRequest}.
     */
    public static AccountRequestAttributes valueOf(AccountRequest accountRequest) {
        AccountRequestAttributes accountRequestAttributes = new AccountRequestAttributes(accountRequest.getEmail(),
                accountRequest.getInstitute(), accountRequest.getName());
        accountRequestAttributes.id = accountRequest.getId();
        accountRequestAttributes.registrationKey = accountRequest.getRegistrationKey();
        accountRequestAttributes.registeredAt = accountRequest.getRegisteredAt();
        accountRequestAttributes.createdAt = accountRequest.getCreatedAt();

        return accountRequestAttributes;
    }

    /**
     * Returns a builder for {@link AccountRequestAttributes}.
     */
    public static Builder builder(String email, String institute, String name) {
        return new Builder(email, institute, name);
    }

    public String getId() {
        return id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getRegistrationUrl() {
        return Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withIsCreatingAccount("true")
                .withRegistrationKey(this.getRegistrationKey())
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

        accountRequest.setRegisteredAt(this.getRegisteredAt());

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
        updateOptions.registeredAtOption.ifPresent(s -> registeredAt = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for an account request.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String email, String institute) {
        return new UpdateOptions.Builder(email, institute);
    }

    /**
     * A builder for {@link AccountRequestAttributes}.
     */
    public static final class Builder extends BasicBuilder<AccountRequestAttributes, Builder> {
        private final AccountRequestAttributes accountRequestAttributes;

        private Builder(String email, String institute, String name) {
            super(new UpdateOptions(email, institute));
            thisBuilder = this;

            accountRequestAttributes = new AccountRequestAttributes(email, institute, name);
        }

        @Override
        public AccountRequestAttributes build() {
            accountRequestAttributes.update(updateOptions);

            return accountRequestAttributes;
        }
    }

    /**
     * Helper class to specify the fields to update in {@link AccountRequestAttributes}.
     */
    public static final class UpdateOptions {
        private String email;
        private String institute;

        private UpdateOption<Instant> registeredAtOption = UpdateOption.empty();

        private UpdateOptions(String email, String institute) {
            assert email != null;
            assert institute != null;

            this.email = email;
            this.institute = institute;
        }

        public String getEmail() {
            return email;
        }

        public String getInstitute() {
            return institute;
        }

        @Override
        public String toString() {
            return "AccountRequestAttributes.UpdateOptions ["
                    + ", email = " + email
                    + ", institute = " + institute
                    + ", registeredAt = " + registeredAtOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static final class Builder extends BasicBuilder<UpdateOptions, Builder> {
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

        public B withRegisteredAt(Instant registeredAt) {
            updateOptions.registeredAtOption = UpdateOption.of(registeredAt);
            return thisBuilder;
        }

        public abstract T build();

    }
}
