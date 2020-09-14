package teammates.ui.webapi.output;

/**
 * The API output format to represent if the registration key is valid for the logged in user (or lack thereof).
 */
public class RegkeyValidityData extends ApiOutput {
    private final boolean isValid;
    private final boolean isUsable;

    public RegkeyValidityData(boolean isValid, boolean isUsable) {
        this.isValid = isValid;
        this.isUsable = isUsable;
    }

    /**
     * Returns true if the registration key is valid, false otherwise.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Returns true if the registration key is usable, false otherwise.
     */
    public boolean isUsable() {
        return isUsable;
    }

}
