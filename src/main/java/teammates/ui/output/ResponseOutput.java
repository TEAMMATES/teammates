package teammates.ui.output;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;

/**
 * API output format for question responses.
 */
public final class ResponseOutput {

    private boolean isMissingResponse;

    private String responseId;

    private String giver;
    @Nullable
    private String userIdForModeration;
    @Nullable
    private String giverUserId;
    @Nullable
    private UUID giverTeamId;
    private String giverTeam;
    @Nullable
    private String giverEmail;
    @Nullable
    private UUID giverSectionId;
    private String giverSection;
    private String recipient;
    @Nullable
    private String recipientUserId;
    @Nullable
    private UUID recipientSectionId;
    @Nullable
    private UUID recipientTeamId;
    private String recipientTeam;
    @Nullable
    private String recipientEmail;
    private String recipientSection;
    private FeedbackResponseDetails responseDetails;

    // comments
    @Nullable
    private String participantComment;
    private List<ResponseInstructorCommentData> instructorComments;

    private ResponseOutput() {
        // use builder instead
    }

    /**
     * Returns a builder for {@link ResponseOutput}.
     */
    public static Builder builder() {
        return new Builder();
    }

    public boolean isMissingResponse() {
        return isMissingResponse;
    }

    public String getResponseId() {
        return responseId;
    }

    public String getGiver() {
        return giver;
    }

    @Nullable
    public String getGiverUserId() {
        return giverUserId;
    }

    @Nullable
    public String getGiverEmail() {
        return giverEmail;
    }

    @Nullable
    public String getUserIdForModeration() {
        return userIdForModeration;
    }

    public String getGiverTeam() {
        return giverTeam;
    }

    @Nullable
    public UUID getGiverTeamId() {
        return giverTeamId;
    }

    public String getGiverSection() {
        return giverSection;
    }

    public UUID getGiverSectionId() {
        return giverSectionId;
    }

    public String getRecipient() {
        return recipient;
    }

    @Nullable
    public String getRecipientUserId() {
        return recipientUserId;
    }

    public String getRecipientTeam() {
        return recipientTeam;
    }

    @Nullable
    public UUID getRecipientTeamId() {
        return recipientTeamId;
    }

    @Nullable
    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getRecipientSection() {
        return recipientSection;
    }

    public UUID getRecipientSectionId() {
        return recipientSectionId;
    }

    public FeedbackResponseDetails getResponseDetails() {
        return responseDetails;
    }

    @Nullable
    public String getParticipantComment() {
        return participantComment;
    }

    public List<ResponseInstructorCommentData> getInstructorComments() {
        return instructorComments;
    }

    /**
     * Builder class for {@link ResponseOutput}.
     */
    public static final class Builder {
        private final ResponseOutput responseOutput;

        private Builder() {
            responseOutput = new ResponseOutput();
        }

        public Builder withIsMissingResponse(boolean isMissingResponse) {
            responseOutput.isMissingResponse = isMissingResponse;
            return this;
        }

        public Builder withResponseId(String responseId) {
            responseOutput.responseId = responseId;
            return this;
        }

        public Builder withGiver(String giverName) {
            responseOutput.giver = giverName;
            return this;
        }

        public Builder withUserIdForModeration(@Nullable String userIdForModeration) {
            responseOutput.userIdForModeration = userIdForModeration;
            return this;
        }

        public Builder withGiverUserId(@Nullable UUID giverUserId) {
            responseOutput.giverUserId = giverUserId == null ? null : giverUserId.toString();
            return this;
        }

        public Builder withGiverTeamId(@Nullable UUID giverTeamId) {
            responseOutput.giverTeamId = giverTeamId;
            return this;
        }

        public Builder withGiverTeam(String giverTeam) {
            responseOutput.giverTeam = giverTeam;
            return this;
        }

        public Builder withGiverEmail(@Nullable String giverEmail) {
            responseOutput.giverEmail = giverEmail;
            return this;
        }

        public Builder withGiverSectionName(String giverSection) {
            responseOutput.giverSection = giverSection;
            return this;
        }

        public Builder withGiverSectionId(@Nullable UUID giverSectionId) {
            responseOutput.giverSectionId = giverSectionId;
            return this;
        }

        public Builder withRecipient(String recipientName) {
            responseOutput.recipient = recipientName;
            return this;
        }

        public Builder withRecipientUserId(@Nullable UUID recipientUserId) {
            responseOutput.recipientUserId = recipientUserId == null ? null : recipientUserId.toString();
            return this;
        }

        public Builder withRecipientTeamId(@Nullable UUID recipientTeamId) {
            responseOutput.recipientTeamId = recipientTeamId;
            return this;
        }

        public Builder withRecipientTeam(String recipientTeam) {
            responseOutput.recipientTeam = recipientTeam;
            return this;
        }

        public Builder withRecipientEmail(@Nullable String recipientEmail) {
            responseOutput.recipientEmail = recipientEmail;
            return this;
        }

        public Builder withRecipientSectionName(String recipientSection) {
            responseOutput.recipientSection = recipientSection;
            return this;
        }

        public Builder withRecipientSectionId(@Nullable UUID recipientSectionId) {
            responseOutput.recipientSectionId = recipientSectionId;
            return this;
        }

        public Builder withResponseDetails(FeedbackResponseDetails responseDetails) {
            responseOutput.responseDetails = responseDetails;
            return this;
        }

        public Builder withParticipantComment(@Nullable String participantComment) {
            responseOutput.participantComment = participantComment;
            return this;
        }

        public Builder withInstructorComments(List<ResponseInstructorCommentData> instructorComments) {
            responseOutput.instructorComments = instructorComments;
            return this;
        }

        public ResponseOutput build() {
            return responseOutput;
        }
    }
}
