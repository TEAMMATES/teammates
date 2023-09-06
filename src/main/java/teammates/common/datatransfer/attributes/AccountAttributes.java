package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Account;

/**
 * The data transfer object for {@link Account} entities.
 */
public final class AccountAttributes extends EntityAttributes<Account> {

    private String googleId;
    private String name;
    private String email;
    private String description;
    private Map<String, Instant> readNotifications;
    private Instant createdAt;

    private AccountAttributes(String googleId) {
        this.googleId = googleId;
        this.readNotifications = new HashMap<>();
    }

    /**
     * Gets the {@link AccountAttributes} instance of the given {@link Account}.
     */
    public static AccountAttributes valueOf(Account a) {
        AccountAttributes accountAttributes = new AccountAttributes(a.getGoogleId());

        accountAttributes.name = a.getName();
        accountAttributes.email = a.getEmail();
        accountAttributes.description = a.getDescription();
        accountAttributes.readNotifications = a.getReadNotifications();
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
        accountAttributes.email = this.email;
        accountAttributes.description = this.description;
        accountAttributes.readNotifications = this.readNotifications;
        accountAttributes.createdAt = this.createdAt;

        return accountAttributes;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

    }

    public Map<String, Instant> getReadNotifications() {
        return readNotifications;
    }

    public void setReadNotifications(Map<String, Instant> readNotifications) {
        this.readNotifications = readNotifications;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForGoogleId(googleId), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        // No validation necessary for createdAt and readNotifications fields.

        return errors;
    }

    @Override
    public Account toEntity() {
        return new Account(googleId, name, email, description, readNotifications);
    }

    @Override
    public String toString() {
        return "AccountAttributes [googleId=" + googleId + ", name=" + name
               + ", email=" + email + ", description= " + description + "]";
    }

    @Override
    public int hashCode() {
        return (this.email + this.name + this.googleId).hashCode();
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
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.readNotificationsOption.ifPresent(s -> readNotifications = s);
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
            assert name != null;

            accountAttributes.name = name;
            return this;
        }

        public Builder withEmail(String email) {
            assert email != null;

            accountAttributes.email = email;
            return this;
        }

        @Override
        public AccountAttributes build() {
            accountAttributes.update(updateOptions);

            return accountAttributes;
        }
    }

    /**
     * Helper class to specify the fields to update in {@link AccountAttributes}.
     */
    public static class UpdateOptions {
        private String googleId;

        private UpdateOption<Map<String, Instant>> readNotificationsOption = UpdateOption.empty();

        private UpdateOptions(String googleId) {
            assert googleId != null;

            this.googleId = googleId;
        }

        public String getGoogleId() {
            return googleId;
        }

        @Override
        public String toString() {
            return "AccountAttributes.UpdateOptions ["
                    + "googleId = " + googleId
                    + ", readNotifications = " + JsonUtils.toJson(readNotificationsOption)
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

        public B withReadNotifications(Map<String, Instant> readNotifications) {
            updateOptions.readNotificationsOption = UpdateOption.of(readNotifications);
            return thisBuilder;
        }

        public abstract T build();
    }

}
