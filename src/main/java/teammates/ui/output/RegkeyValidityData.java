package teammates.ui.output;

/**
 * The API output format to represent if the registration key is valid for the logged in user (or lack thereof).
 */
public class RegkeyValidityData extends ApiOutput {
    private final boolean isValid;
    private final boolean isUsed;
    private final boolean isAllowedAccess;

    public RegkeyValidityData(boolean isValid, boolean isUsed, boolean isAllowedAccess) {
        this.isValid = isValid;
        this.isUsed = isUsed;
        this.isAllowedAccess = isAllowedAccess;
    }

    /**
     * Returns true if the registration key is valid, false otherwise.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Returns true if the registration key has been used, false otherwise.
     */
    public boolean isUsed() {
        return isUsed;
    }

    /**
     * Returns true if access is allowed for the requester by using the registration key, false otherwise.
     */
    public boolean isAllowedAccess() {
        return isAllowedAccess;
    }

}
