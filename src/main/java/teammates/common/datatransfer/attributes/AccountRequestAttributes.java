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
    private String institute;
    private String country;
    private String email;
    private String homePageUrl;
    private String otherComments;
    private AccountRequestStatus status;
    private Instant createdAt;
    private Instant approvedAt;
    private Instant rejectedAt;
    private Instant registeredAt;
    private transient String registrationKey;

    private AccountRequestAttributes(String name, String institute, String country, String email,
                                     String homePageUrl, String otherComments) {
        this.name = name;
        this.institute = institute;
        this.country = country;
        this.email = email;
        this.homePageUrl = homePageUrl;
        this.otherComments = otherComments;
        this.status = AccountRequestStatus.SUBMITTED;
        this.registrationKey = null;
        this.createdAt = null;
        this.approvedAt = null;
        this.rejectedAt = null;
        this.registeredAt = null;
    }

    /**
     * Gets the {@link AccountRequestAttributes} instance of the given {@link AccountRequest}.
     */
    public static AccountRequestAttributes valueOf(AccountRequest accountRequest) {
        AccountRequestAttributes accountRequestAttributes = new AccountRequestAttributes(accountRequest.getName(),
                accountRequest.getInstitute(), accountRequest.getCountry(), accountRequest.getEmail(),
                accountRequest.getHomePageUrl(), accountRequest.getOtherComments());

        accountRequestAttributes.registrationKey = accountRequest.getRegistrationKey();
        accountRequestAttributes.status = accountRequest.getStatus();
        accountRequestAttributes.createdAt = accountRequest.getCreatedAt();
        accountRequestAttributes.approvedAt = accountRequest.getApprovedAt();
        accountRequestAttributes.rejectedAt = accountRequest.getRejectedAt();
        accountRequestAttributes.registeredAt = accountRequest.getRegisteredAt();

        return accountRequestAttributes;
    }

    /**
     * Returns a builder for {@link AccountRequestAttributes}.
     */
    public static Builder builder(String name, String institute, String country, String email,
                                  String homePageUrl, String otherComments) {
        return new Builder(name, institute, country, email, homePageUrl, otherComments);
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

    public String getCountry() {
        return country;
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

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public Instant getRejectedAt() {
        return rejectedAt;
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
        addNonEmptyError(FieldValidator.getInvalidityInfoForCountryName(getCountry()), errors);
        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(getEmail()), errors);

        return errors;
    }

    @Override
    public AccountRequest toEntity() {
        AccountRequest accountRequest = new AccountRequest(getName(), getInstitute(), getCountry(), getEmail(),
                getHomePageUrl(), getOtherComments());

        if (this.getRegistrationKey() != null) {
            accountRequest.setRegistrationKey(this.getRegistrationKey());
        }

        if (this.getCreatedAt() != null) {
            accountRequest.setCreatedAt(this.getCreatedAt());
        }

        if (this.getStatus() != AccountRequestStatus.SUBMITTED) {
            accountRequest.setStatus(this.getStatus());
        }

        accountRequest.setApprovedAt(this.getApprovedAt());
        accountRequest.setRejectedAt(this.getRejectedAt());
        accountRequest.setRegisteredAt(this.getRegisteredAt());

        return accountRequest;
    }

    @Override
    public String toString() {
        return "[" + AccountRequestAttributes.class.getSimpleName() + " email: " + getEmail()
                + " institute: " + getInstitute() + " country: " + getCountry();
    }

    @Override
    public int hashCode() {
        return (this.email + this.institute + this.country).hashCode();
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
                    && Objects.equals(this.country, otherAccountRequestAttributes.country);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.name = SanitizationHelper.sanitizeName(name);
        this.institute = SanitizationHelper.sanitizeTitle(institute);
        this.country = SanitizationHelper.sanitizeTitle(country);
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.homePageUrl = SanitizationHelper.sanitizeTextField(homePageUrl);
        this.otherComments = SanitizationHelper.sanitizeTextField(otherComments);
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
    public static UpdateOptions.Builder updateOptionsBuilder(String email, String institute, String country) {
        return new UpdateOptions.Builder(email, institute, country);
    }

    /**
     * A builder for {@link AccountRequestAttributes}.
     */
    public static class Builder extends BasicBuilder<AccountRequestAttributes, Builder> {
        private final AccountRequestAttributes accountRequestAttributes;

        private Builder(String name, String institute, String country, String email,
                        String homePageUrl, String otherComments) {
            super(new UpdateOptions(email, institute, country));
            thisBuilder = this;

            accountRequestAttributes = new AccountRequestAttributes(name, institute, country, email,
                    homePageUrl, otherComments);
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
        private String country;

        private UpdateOption<Instant> registeredAtOption = UpdateOption.empty();

        private UpdateOptions(String email, String institute, String country) {
            assert email != null;
            assert institute != null;
            assert country != null;

            this.email = email;
            this.institute = institute;
            this.country = country;
        }

        public String getEmail() {
            return email;
        }

        public String getInstitute() {
            return institute;
        }

        public String getCountry() {
            return country;
        }

        @Override
        public String toString() {
            return "AccountRequestAttributes.UpdateOptions ["
                    + ", email = " + email
                    + ", institute = " + institute
                    + ", country = " + country
                    + ", registeredAt = " + registeredAtOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {
            private Builder(String email, String institute, String country) {
                super(new UpdateOptions(email, institute, country));
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
