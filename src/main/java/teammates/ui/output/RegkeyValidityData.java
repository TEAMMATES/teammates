package teammates.ui.output;

/**
 * The API output format to represent if the registration key is valid for the logged in user (or lack thereof).
 */
public class RegkeyValidityData extends ApiOutput {
    private final boolean isValid;

    public RegkeyValidityData(boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Returns true if the registration key is valid, false otherwise.
     */
    public boolean isValid() {
        return isValid;
    }

}
