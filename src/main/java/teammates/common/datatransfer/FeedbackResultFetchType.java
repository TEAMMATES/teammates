package teammates.common.datatransfer;

/**
 * The FeedbackResponse result fetching type to indicate whether the fetch is for giver only, receiver only or both.
 */
public enum FeedbackResultFetchType {
    /**
     * Fetch by giver only.
     */
    GIVER(true, false),
    /**
     * Fetch by receiver only.
     */
    RECEIVER(false, true),
    /**
     * Fetch by both giver and receiver.
     */
    BOTH(true, true);

    private final boolean isByGiver;
    private final boolean isByReceiver;

    FeedbackResultFetchType(boolean isByGiver, boolean isByReceiver) {
        this.isByGiver = isByGiver;
        this.isByReceiver = isByReceiver;
    }

    /**
     * Parse the input string into a {@link FeedbackResultFetchType} and default to {@link FeedbackResultFetchType}.BOTH.
     */
    public static FeedbackResultFetchType parseFetchType(String typeString) {
        if (typeString == null) {
            return BOTH;
        }

        switch (typeString.toLowerCase()) {
        case "giver":
            return GIVER;
        case "receiver":
            return RECEIVER;
        default:
        }

        return BOTH;
    }

    /**
     * This result fetch should be by giver.
     */
    public boolean shouldFetchByGiver() {
        return isByGiver;
    }

    /**
     * This result fetch should be by receiver.
     */
    public boolean shouldFetchByReceiver() {
        return isByReceiver;
    }
}
