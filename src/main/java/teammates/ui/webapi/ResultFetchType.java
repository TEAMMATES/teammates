package teammates.ui.webapi;

public enum ResultFetchType {
    GIVER_ONLY, RECEIVER_ONLY, BOTH;

    static public ResultFetchType parseFetchType(String typeString) {
        switch (typeString.toLowerCase()) {
        case "giver":
            return GIVER_ONLY;
        case "receiver":
            return RECEIVER_ONLY;
        default:
            return BOTH;
        }
    }
}
