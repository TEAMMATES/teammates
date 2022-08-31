package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.AccountRequest;

/**
 * The data transfer object for {@link AccountRequest} entities.
 */
public final class AccountRequestAttributes extends EntityAttributes<AccountRequest> {

    private String name;
    private String institute;
    private String email;
    private String homePageUrl;
    private String comments;
    private AccountRequestStatus status;
    private Instant createdAt;
    private Instant lastProcessedAt;
    private Instant registeredAt;
    private transient String registrationKey;

    private AccountRequestAttributes(String name, String institute, String email, String homePageUrl, String comments) {
        this.name = name;
        this.institute = institute;
        this.email = email;
        this.homePageUrl = homePageUrl;
        this.comments = comments;
        this.registrationKey = null;
        this.status = AccountRequestStatus.SUBMITTED;
        this.createdAt = null;
        this.lastProcessedAt = null;
        this.registeredAt = null;
    }

    /**
     * Gets the {@link AccountRequestAttributes} instance of the given {@link AccountRequest}.
     */
    public static AccountRequestAttributes valueOf(AccountRequest accountRequest) {
        AccountRequestAttributes accountRequestAttributes = new AccountRequestAttributes(
                accountRequest.getName(), accountRequest.getInstitute(), accountRequest.getEmail(),
                accountRequest.getHomePageUrl(), accountRequest.getComments());

        accountRequestAttributes.registrationKey = accountRequest.getRegistrationKey();
        accountRequestAttributes.status = accountRequest.getStatus();
        accountRequestAttributes.createdAt = accountRequest.getCreatedAt();
        accountRequestAttributes.lastProcessedAt = accountRequest.getLastProcessedAt();
        accountRequestAttributes.registeredAt = accountRequest.getRegisteredAt();

        return accountRequestAttributes;
    }

    /**
     * Returns a builder for {@link AccountRequestAttributes}.
     */
    public static Builder builder(String name, String institute, String email, String homePageUrl, String comments) {
        assert name != null;
        assert institute != null;
        assert email != null;
        assert homePageUrl != null;
        assert comments != null;

        return new Builder(name, institute, email, homePageUrl, comments);
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastProcessedAt(Instant lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public String getName() {
        return name;
    }

    public String getInstitute() {
        return institute;
    }

    public String getEmail() {
        return email;
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public String getComments() {
        return comments;
    }

    public AccountRequestStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastProcessedAt() {
        return lastProcessedAt;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
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

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(getName()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(getInstitute()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForAccountRequestHomePageUrl(getHomePageUrl()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForAccountRequestComments(getComments()), errors);

        return errors;
    }

    @Override
    public AccountRequest toEntity() {
        AccountRequest accountRequest = new AccountRequest(getName(), getInstitute(), getEmail(),
                getHomePageUrl(), getComments());

        if (this.getRegistrationKey() != null) {
            accountRequest.setRegistrationKey(this.getRegistrationKey());
        }

        if (this.getCreatedAt() != null) {
            accountRequest.setCreatedAt(this.getCreatedAt());
        }

        accountRequest.setStatus(this.getStatus());
        accountRequest.setLastProcessedAt(this.getLastProcessedAt());
        accountRequest.setRegisteredAt(this.getRegisteredAt());

        return accountRequest;
    }

    @Override
    public String toString() {
        return "[" + AccountRequestAttributes.class.getSimpleName()
                + "] name= " + getName()
                + ", institute= " + getInstitute()
                + ", email= " + getEmail()
                + ", homePageUrl= " + getHomePageUrl()
                + ", comments= " + getComments()
                + ", status= " + getStatus()
                + ", createdAt= " + getCreatedAt()
                + ", lastProcessedAt= " + getLastProcessedAt()
                + ", registeredAt= " + getRegisteredAt();
    }

    @Override
    public int hashCode() {
        return (this.name + this.institute + this.email + this.homePageUrl + this.comments).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            AccountRequestAttributes otherAccountRequestAttributes = (AccountRequestAttributes) other;
            return Objects.equals(this.name, otherAccountRequestAttributes.name)
                    && Objects.equals(this.institute, otherAccountRequestAttributes.institute)
                    && Objects.equals(this.email, otherAccountRequestAttributes.email)
                    && Objects.equals(this.homePageUrl, otherAccountRequestAttributes.homePageUrl)
                    && Objects.equals(this.comments, otherAccountRequestAttributes.comments);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.name = SanitizationHelper.sanitizeName(name);
        this.institute = SanitizationHelper.sanitizeTitle(institute);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.homePageUrl = SanitizationHelper.sanitizeTextField(homePageUrl);
        this.comments = SanitizationHelper.sanitizeTextField(comments);
    }

    /**
     * Checks if (the registration key of) this account request can be used to 'join' TEAMMATES (at this moment).
     */
    public boolean canRegistrationKeyBeUseToJoin() {
        // TODO: status should be non-null after data migration
        if (status == null) {
            return registeredAt == null;
        } else {
            return registeredAt == null && status.equals(AccountRequestStatus.APPROVED);
        }
    }

    /**
     * Checks if (the registration key of) this account request has been used to 'join' TEAMMATES.
     */
    public boolean hasRegistrationKeyBeenUsedToJoin() {
        // TODO: status should be non-null after data migration
        if (status == null) {
            return registeredAt != null;
        } else {
            return registeredAt != null || status.equals(AccountRequestStatus.REGISTERED);
        }
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.nameOption.ifPresent(n -> name = n);
        updateOptions.instituteOption.ifPresent(n -> institute = n);
        updateOptions.emailOption.ifPresent(n -> email = n);
        updateOptions.statusOption.ifPresent(s -> status = s);
        updateOptions.lastProcessedAtOption.ifPresent(a -> lastProcessedAt = a);
        updateOptions.registeredAtOption.ifPresent(r -> registeredAt = r);
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
    public static class Builder extends BasicBuilder<AccountRequestAttributes, Builder> {
        private final AccountRequestAttributes accountRequestAttributes;

        private Builder(String name, String institute, String email, String homePageUrl, String comments) {
            super(new UpdateOptions(email, institute));
            thisBuilder = this;

            accountRequestAttributes = new AccountRequestAttributes(name, institute, email, homePageUrl, comments);
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
    public static class UpdateOptions {
        private String email;
        private String institute;

        private UpdateOption<String> nameOption = UpdateOption.empty();
        private UpdateOption<String> instituteOption = UpdateOption.empty();
        private UpdateOption<String> emailOption = UpdateOption.empty();
        private UpdateOption<AccountRequestStatus> statusOption = UpdateOption.empty();
        private UpdateOption<Instant> lastProcessedAtOption = UpdateOption.empty();
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
                    + "email = " + email
                    + ", institute = " + institute
                    + ", name = " + nameOption
                    + ", new institute = " + instituteOption
                    + ", new email = " + emailOption
                    + ", status = " + statusOption
                    + ", lastProcessedAt = " + lastProcessedAtOption
                    + ", registeredAt = " + registeredAtOption
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

        public B withInstitute(String institute) {
            assert institute != null;

            updateOptions.instituteOption = UpdateOption.of(institute);
            return thisBuilder;
        }

        public B withEmail(String email) {
            assert email != null;

            updateOptions.emailOption = UpdateOption.of(email);
            return thisBuilder;
        }

        public B withStatus(AccountRequestStatus status) {
            assert status != null;

            updateOptions.statusOption = UpdateOption.of(status);
            return thisBuilder;
        }

        public B withLastProcessedAt(Instant lastProcessedAt) {
            assert lastProcessedAt != null;

            updateOptions.lastProcessedAtOption = UpdateOption.of(lastProcessedAt);
            return thisBuilder;
        }

        public B withRegisteredAt(Instant registeredAt) {
            // registeredAt is null when instructor is unregistered

            updateOptions.registeredAtOption = UpdateOption.of(registeredAt);
            return thisBuilder;
        }

        public abstract T build();

    }
}
