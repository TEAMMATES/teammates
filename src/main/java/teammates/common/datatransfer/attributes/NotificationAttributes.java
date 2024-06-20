package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Notification;

/**
 * The data transfer object for the notification.
 */
public final class NotificationAttributes extends EntityAttributes<Notification> {

    private String notificationId;
    private Instant startTime;
    private Instant endTime;
    private NotificationStyle style;
    private NotificationTargetUser targetUser;
    private String title;
    private String message;
    private boolean shown;
    // createdAt is not transient as it is used for the frontend to sort by creation datetime.
    private Instant createdAt;
    private transient Instant updatedAt;

    private NotificationAttributes(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Gets the {@link NotificationAttributes} instance of the given {@link Notification}.
     */
    public static NotificationAttributes valueOf(Notification n) {
        NotificationAttributes notificationAttributes = new NotificationAttributes(n.getNotificationId());

        notificationAttributes.startTime = n.getStartTime();
        notificationAttributes.endTime = n.getEndTime();
        notificationAttributes.style = n.getStyle();
        notificationAttributes.targetUser = n.getTargetUser();
        notificationAttributes.title = n.getTitle();
        notificationAttributes.message = n.getMessage();
        notificationAttributes.createdAt = n.getCreatedAt();
        notificationAttributes.updatedAt = n.getUpdatedAt();
        notificationAttributes.shown = n.isShown();

        return notificationAttributes;
    }

    /**
     * Returns a builder for {@link NotificationAttributes}.
     */
    public static Builder builder(String notificationId) {
        return new Builder(notificationId);
    }

    /**
     * Gets a deep copy of this object.
     */
    public NotificationAttributes getCopy() {
        NotificationAttributes notificationAttributes = new NotificationAttributes(this.notificationId);

        notificationAttributes.startTime = this.startTime;
        notificationAttributes.endTime = this.endTime;
        notificationAttributes.style = this.style;
        notificationAttributes.targetUser = this.targetUser;
        notificationAttributes.title = this.title;
        notificationAttributes.message = this.message;
        notificationAttributes.createdAt = this.createdAt;
        notificationAttributes.updatedAt = this.updatedAt;
        notificationAttributes.shown = this.shown;

        return notificationAttributes;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public NotificationStyle getStyle() {
        return style;
    }

    public void setStyle(NotificationStyle style) {
        this.style = style;
    }

    public NotificationTargetUser getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(NotificationTargetUser targetUser) {
        this.targetUser = targetUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShown() {
        return shown;
    }

    /**
     * Sets the notification as shown to the user.
     * Only allowed to change value from false to true.
     */
    public void setShown() {
        this.shown = true;
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

    /**
     * Sorts the list of notifications by the start time, with the latest as the first element.
     */
    public static void sortByStartTime(List<NotificationAttributes> notifications) {
        notifications.sort(Comparator.comparing(NotificationAttributes::getStartTime));
        Collections.reverse(notifications);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("notification visible time", startTime), errors);

        addNonEmptyError(FieldValidator.getValidityInfoForNonNullField("notification expiry time", endTime), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForTimeForNotificationStartAndEnd(startTime, endTime), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationStyle(style.name()), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUser.name()), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationTitle(title), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForNotificationBody(message), errors);

        return errors;
    }

    @Override
    public Notification toEntity() {
        return new Notification(notificationId, startTime, endTime, style,
                targetUser, title, message, shown, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this, NotificationAttributes.class);
    }

    @Override
    public int hashCode() {
        // Notification ID uniquely identifies a notification.
        return this.getNotificationId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            NotificationAttributes otherNotification = (NotificationAttributes) other;
            return Objects.equals(this.notificationId, otherNotification.notificationId);
        } else {
            return false;
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.title = SanitizationHelper.sanitizeTitle(title);
        this.message = SanitizationHelper.sanitizeForRichText(message);
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.startTimeOption.ifPresent(s -> startTime = s);
        updateOptions.endTimeOption.ifPresent(e -> endTime = e);
        updateOptions.styleOption.ifPresent(t -> style = t);
        updateOptions.targetUserOption.ifPresent(u -> targetUser = u);
        updateOptions.titleOption.ifPresent(t -> title = t);
        updateOptions.messageOption.ifPresent(m -> message = m);
        updateOptions.shownOption.ifPresent(s -> shown = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a notification.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String notificationId) {
        return new UpdateOptions.Builder(notificationId);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build on top of {@code updateOptions}.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(UpdateOptions updateOptions) {
        return new UpdateOptions.Builder(updateOptions);
    }

    /**
     * A builder for {@link NotificationAttributes}.
     */
    public static final class Builder extends BasicBuilder<NotificationAttributes, Builder> {
        private final NotificationAttributes notificationAttributes;

        private Builder(String notificationId) {
            super(new UpdateOptions(notificationId));
            thisBuilder = this;

            notificationAttributes = new NotificationAttributes(notificationId);
        }

        @Override
        public NotificationAttributes build() {
            notificationAttributes.update(updateOptions);

            return notificationAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link NotificationAttributes}.
     */
    public static final class UpdateOptions {
        private String notificationId;

        private UpdateOption<Instant> startTimeOption = UpdateOption.empty();
        private UpdateOption<Instant> endTimeOption = UpdateOption.empty();
        private UpdateOption<NotificationStyle> styleOption = UpdateOption.empty();
        private UpdateOption<NotificationTargetUser> targetUserOption = UpdateOption.empty();
        private UpdateOption<String> titleOption = UpdateOption.empty();
        private UpdateOption<String> messageOption = UpdateOption.empty();
        private UpdateOption<Boolean> shownOption = UpdateOption.empty();

        private UpdateOptions(String notificationId) {
            assert notificationId != null;

            this.notificationId = notificationId;
        }

        public String getNotificationId() {
            return notificationId;
        }

        @Override
        public String toString() {
            return "NotificationAttributes.UpdateOptions ["
                    + "startTime = " + startTimeOption
                    + ", endTime = " + endTimeOption
                    + ", style = " + styleOption
                    + ", targetUser = " + targetUserOption
                    + ", title = " + titleOption
                    + ", message = " + messageOption
                    + ", shown = " + shownOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static final class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(UpdateOptions updateOptions) {
                super(updateOptions);
                assert updateOptions != null;
                thisBuilder = this;
            }

            private Builder(String notificationId) {
                super(new UpdateOptions(notificationId));
                thisBuilder = this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link NotificationAttributes} related classes.
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

        public B withStartTime(Instant startTime) {
            assert startTime != null;

            updateOptions.startTimeOption = UpdateOption.of(startTime);
            return thisBuilder;
        }

        public B withEndTime(Instant endTime) {
            assert endTime != null;

            updateOptions.endTimeOption = UpdateOption.of(endTime);
            return thisBuilder;
        }

        public B withStyle(NotificationStyle style) {
            assert style != null;

            updateOptions.styleOption = UpdateOption.of(style);
            return thisBuilder;
        }

        public B withTargetUser(NotificationTargetUser targetUser) {
            assert targetUser != null;

            updateOptions.targetUserOption = UpdateOption.of(targetUser);
            return thisBuilder;
        }

        public B withTitle(String title) {
            assert title != null;

            updateOptions.titleOption = UpdateOption.of(title);
            return thisBuilder;
        }

        public B withMessage(String message) {
            assert message != null;

            updateOptions.messageOption = UpdateOption.of(message);
            return thisBuilder;
        }

        public B withShown() {
            updateOptions.shownOption = UpdateOption.of(true);
            return thisBuilder;
        }

        public abstract T build();

    }
}
