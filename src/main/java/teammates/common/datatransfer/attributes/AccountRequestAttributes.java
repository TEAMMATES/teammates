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
public class AccountRequestAttributes extends EntityAttributes<AccountRequest> {

    private String name;
    private String pureInstitute;
    private String pureCountry;
    private String institute;
    private String email;
    private String homePageUrl;
    private String otherComments;
    private AccountRequestStatus status;
    private Instant createdAt;
    private Instant lastProcessedAt;
    private Instant registeredAt;
    private transient String registrationKey;

    private AccountRequestAttributes(String name, String pureInstitute, String pureCountry, String institute,
                                     String email, String homePageUrl, String otherComments) {
        this.name = name;
        this.pureInstitute = pureInstitute;
        this.pureCountry = pureCountry;
        this.institute = institute;
        this.email = email;
        this.homePageUrl = homePageUrl;
        this.otherComments = otherComments;
        this.registrationKey = null;
        this.status = AccountRequestStatus.SUBMITTED;
        this.createdAt = null;
        this.lastProcessedAt = null;
        this.registeredAt = null;
    }

    /**
     * Generates the {@code institute} field by combining {@code pureInstitute} and {@code pureCountry}.
     */
    private static String generateInstitute(String pureInstitute, String pureCountry) {
        return pureInstitute + ", " + pureCountry;
    }

    /**
     * Gets the {@link AccountRequestAttributes} instance of the given {@link AccountRequest}.
     * As an AccountRequest only stores institute, pureInstitute and pureCountry are set to null.
     */
    public static AccountRequestAttributes valueOf(AccountRequest accountRequest) {
        AccountRequestAttributes accountRequestAttributes = new AccountRequestAttributes(accountRequest.getName(),
                null, null, accountRequest.getInstitute(),
                accountRequest.getEmail(), accountRequest.getHomePageUrl(), accountRequest.getOtherComments());

        accountRequestAttributes.registrationKey = accountRequest.getRegistrationKey();
        accountRequestAttributes.status = accountRequest.getStatus();
        accountRequestAttributes.createdAt = accountRequest.getCreatedAt();
        accountRequestAttributes.lastProcessedAt = accountRequest.getLastProcessedAt();
        accountRequestAttributes.registeredAt = accountRequest.getRegisteredAt();

        return accountRequestAttributes;
    }

    /**
     * Returns a builder for {@link AccountRequestAttributes}. {@code pureInstitute} and {@code pureCountry} are specified.
     */
    public static Builder builder(String name, String pureInstitute, String pureCountry, String email,
                                  String homePageUrl, String otherComments) {
        return new Builder(name, pureInstitute, pureCountry, email, homePageUrl, otherComments);
    }

    /**
     * Returns a builder for {@link AccountRequestAttributes}. {@code institute} is specified.
     */
    public static Builder builder(String name, String institute, String email, String homePageUrl, String otherComments) {
        return new Builder(name, institute, email, homePageUrl, otherComments);
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public String getName() {
        return name;
    }

    public String getPureInstitute() {
        return pureInstitute;
    }

    public String getPureCountry() {
        return pureCountry;
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

    public String getOtherComments() {
        return otherComments;
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
        if (getPureInstitute() != null || getPureCountry() != null) {
            // if either one is non-null, both should be non-null
            // if both are valid, institute should be valid as well
            addNonEmptyError(FieldValidator.getInvalidityInfoForPureInstituteName(getPureInstitute()), errors);
            addNonEmptyError(FieldValidator.getInvalidityInfoForPureCountryName(getPureCountry()), errors);
        } // TODO: reconsider validation logic here
        addNonEmptyError(FieldValidator.getInvalidityInfoForInstituteName(getInstitute()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(getEmail()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForAccountRequestHomePageUrl(getHomePageUrl()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForAccountRequestComments(getOtherComments()), errors);

        return errors;
    }

    @Override
    public AccountRequest toEntity() {
        AccountRequest accountRequest = new AccountRequest(getName(), getInstitute(), getEmail(),
                getHomePageUrl(), getOtherComments());

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
            AccountRequestAttributes otherAccountRequestAttributes = (AccountRequestAttributes) other;
            return Objects.equals(this.email, otherAccountRequestAttributes.email)
                    && Objects.equals(this.institute, otherAccountRequestAttributes.institute)
                    && Objects.equals(this.name, otherAccountRequestAttributes.name);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.name = SanitizationHelper.sanitizeName(name);
        // pureInstitute and pureCountry won't be saved for now, but they are still sanitized
        this.pureInstitute = SanitizationHelper.sanitizeTitle(pureInstitute);
        this.pureCountry = SanitizationHelper.sanitizeTitle(pureCountry);
        this.institute = SanitizationHelper.sanitizeTitle(institute);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.homePageUrl = SanitizationHelper.sanitizeTextField(homePageUrl);
        this.otherComments = SanitizationHelper.sanitizeTextField(otherComments);
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.nameOption.ifPresent(n -> name = n);
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

        private Builder(String name, String pureInstitute, String pureCountry, String email,
                        String homePageUrl, String otherComments) {
            super(new UpdateOptions(email, generateInstitute(pureInstitute, pureCountry)));
            thisBuilder = this;

            accountRequestAttributes = new AccountRequestAttributes(name, pureInstitute, pureCountry,
                    generateInstitute(pureInstitute, pureCountry), email, homePageUrl, otherComments);
        }

        private Builder(String name, String institute, String email, String homePageUrl, String otherComments) {
            super(new UpdateOptions(email, institute));
            thisBuilder = this;

            accountRequestAttributes = new AccountRequestAttributes(name, null, null,
                    institute, email, homePageUrl, otherComments);
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
                    + ", email = " + email
                    + ", institute = " + institute
                    + ", name = " + nameOption
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
            updateOptions.nameOption = UpdateOption.of(name);
            return thisBuilder;
        }

        public B withStatus(AccountRequestStatus status) {
            updateOptions.statusOption = UpdateOption.of(status);
            return thisBuilder;
        }

        public B withLastProcessedAt(Instant lastProcessedAt) {
            updateOptions.lastProcessedAtOption = UpdateOption.of(lastProcessedAt);
            return thisBuilder;
        }

        public B withRegisteredAt(Instant registeredAt) {
            updateOptions.registeredAtOption = UpdateOption.of(registeredAt);
            return thisBuilder;
        }

        public abstract T build();

    }
}
