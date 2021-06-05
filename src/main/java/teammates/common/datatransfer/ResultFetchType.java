package teammates.common.datatransfer;

/**
 * The FeedbackResponse result fetching type to indicate whether the fetch is for giver only, receiver only or both.
 */
public enum ResultFetchType {
    /**
     * Fetch by giver only.
     */
    GIVER_ONLY,
    /**
     * Fetch by receiver only.
     */
    RECEIVER_ONLY,
    /**
     * Fetch by both giver and receiver.
     */
    BOTH;

    /**
     * Parse the input string into a {@link ResultFetchType} and default to {@link ResultFetchType}.BOTH.
     */
    public static ResultFetchType parseFetchType(String typeString) {
        switch (typeString.toLowerCase()) {
        case "giver":
            return GIVER_ONLY;
        case "receiver":
            return RECEIVER_ONLY;
        default:
        }

        return BOTH;
    }
}
