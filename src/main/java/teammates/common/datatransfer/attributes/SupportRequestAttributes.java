package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.SupportRequestType;
import teammates.common.datatransfer.SupportRequestStatus;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.SupportRequest;

public final class SupportRequestAttributes extends EntityAttributes<SupportRequest> {
    private String id;
    private String name;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
    private SupportRequestType type;
    private String message;
    private SupportRequestStatus status;

    private SupportRequestAttributes(String id) {
        this.id = id;
    }

    /**
     * Gets the {@link SupportRequestAttributes} instance of the given {@link SupportRequest}
     */
    public static SupportRequestAttributes valueOf(SupportRequest sr) {
        SupportRequestAttributes supportRequestAttributes = new SupportRequestAttributes(sr.getId());

        supportRequestAttributes.name = sr.getName();
        supportRequestAttributes.email = sr.getEmail();
        supportRequestAttributes.createdAt = sr.getCreatedAt();
        supportRequestAttributes.updatedAt = sr.getUpdatedAt();
        supportRequestAttributes.type = sr.getType();
        supportRequestAttributes.message = sr.getMessage();
        supportRequestAttributes.status = sr.getStatus();

        return supportRequestAttributes;
    }

    /**
     * Returns a builder for {@link SupportRequestAttributes}.
     */
    public static Builder builder(String supportRequestId) {
        return new Builder(supportRequestId);
    }

    /**
     * Gets a deep copy of this object.
     */
    public SupportRequestAttributes getCopy() {
        SupportRequestAttributes supportRequestAttributes = new SupportRequestAttributes(this.getId());

        supportRequestAttributes.name = this.getName();
        supportRequestAttributes.email = this.getEmail();
        supportRequestAttributes.createdAt = this.getCreatedAt();
        supportRequestAttributes.updatedAt = this.getUpdatedAt();
        supportRequestAttributes.type = this.getType();
        supportRequestAttributes.message = this.getMessage();
        supportRequestAttributes.status = this.getStatus();

        return supportRequestAttributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public SupportRequestType getType() {
        return type;
    }

    public void setType(SupportRequestType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SupportRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SupportRequestStatus status) {
        this.status = status;
    }

    // TODO: do we need other sorts?
    /**
     * Sorts the list of support requests by the updated time, with the latest as the first element.
     */
    public static void sortByUpdatedTime(List<SupportRequestAttributes> supportRequests) {
        supportRequests.sort(Comparator.comparing(SupportRequestAttributes::getUpdatedAt));
        Collections.reverse(supportRequests);
    }
    
    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("support request created time", createdAt), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("support request updated time", updatedAt), errors);
        
        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForSupportRequestCreatedAndUpdated(createdAt, updatedAt), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForPersonName(name), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForEmail(email), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForSupportRequestMessage(message), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForSupportRequestType(type.name()), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForSupportRequestStatus(status.name()), errors);

        return errors;
    }


    @Override
    public SupportRequest toEntity() {
        return new SupportRequest(id, name, email, createdAt, updatedAt, type, message, status);
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, SupportRequestAttributes.class);
    }

    @Override
    public int hashCode() {
        // Support request ID uniquely identifies a support request.
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            SupportRequestAttributes otherSupportRequest = (SupportRequestAttributes) other;
            return Objects.equals(this.id, otherSupportRequest.id);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.email = SanitizationHelper.sanitizeEmail(email);
        this.name = SanitizationHelper.sanitizeName(name);
        this.message = SanitizationHelper.sanitizeForRichText(message);
    }
    
    /**
     * Updates with {@link UpdatedOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.nameOption.ifPresent(n -> name = n);
        updateOptions.emailOption.ifPresent(e -> email = e);
        updateOptions.createdAtOption.ifPresent(ca -> createdAt = ca);
        updateOptions.updatedAtOption.ifPresent(ua -> updatedAt = ua);
        updateOptions.typeOption.ifPresent(t -> type = t);
        updateOptions.messageOption.ifPresent(m -> message = m);
        updateOptions.statusOption.ifPresent(s -> status = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a support request.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String id) {
        return new UpdateOptions.Builder(id);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build on top of {@code updateOptions}.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(UpdateOptions updateOptions) {
        return new UpdateOptions.Builder(updateOptions);
    }

    /**
     * A builder for {@link SupportRequestAttributes}.
     */
    public static class Builder extends BasicBuilder<SupportRequestAttributes, Builder> {
        private final SupportRequestAttributes supportRequestAttributes;

        private Builder(String id) {
            super(new UpdateOptions(id));
            thisBuilder = this;

            supportRequestAttributes = new SupportRequestAttributes(id);
        }

        @Override
        public SupportRequestAttributes build() {
            supportRequestAttributes.update(updateOptions);

            return supportRequestAttributes;
        }
    }

    /**
     * Helper class to specify the fields to update in {@link SupportRequestAttributes}.
     */
    public static class UpdateOptions {
        private String id;

        private UpdateOption<String> nameOption = UpdateOption.empty();
        private UpdateOption<String> emailOption = UpdateOption.empty();
        private UpdateOption<Instant> createdAtOption = UpdateOption.empty();
        private UpdateOption<Instant> updatedAtOption = UpdateOption.empty();
        private UpdateOption<SupportRequestType> typeOption = UpdateOption.empty();
        private UpdateOption<String> messageOption = UpdateOption.empty();
        private UpdateOption<SupportRequestStatus> statusOption = UpdateOption.empty();

        private UpdateOptions(String id) {
            assert id != null;

            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return "SupportRequestAttributes.UpdateOptions ["
                    + "name = " + nameOption
                    + ", email = " + emailOption
                    + ", createdAt = " + createdAtOption
                    + ", updatedAt = " + updatedAtOption
                    + ", type = " + typeOption
                    + ", message = " + messageOption
                    + ", status = " + statusOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {
            private Builder(UpdateOptions updateOptions) {
                super(updateOptions);
                assert updateOptions != null;
                thisBuilder = this;
            }

            private Builder(String id) {
                super(new UpdateOptions(id));
                thisBuilder = this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }
        }
    }

    /**
     * Basic builder to build {@link SupportRequestAttributes} related classes.
     * 
     * @param <T> type to be build
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

        public B withEmail(String email) {
            assert email != null;

            updateOptions.nameOption = UpdateOption.of(email);
            return thisBuilder;
        }

        public B withCreatedAt(Instant createdAt) {
            assert createdAt != null;

            updateOptions.createdAtOption = UpdateOption.of(createdAt);
            return thisBuilder;
        }

        public B withUpdatedAt(Instant updatedAt) {
            assert updatedAt != null;

            updateOptions.createdAtOption = UpdateOption.of(updatedAt);
            return thisBuilder;
        }

        public B withType(SupportRequestType type) {
            assert type != null;

            updateOptions.typeOption = UpdateOption.of(type);
            return thisBuilder;
        }

        public B withMessage(String message) {
            assert message != null;

            updateOptions.messageOption = UpdateOption.of(message);
            return thisBuilder;
        }

        public B withStatus(SupportRequestStatus status) {
            assert status != null;

            updateOptions.statusOption = UpdateOption.of(status);
            return thisBuilder;
        }

        public abstract T build();
    }
}
