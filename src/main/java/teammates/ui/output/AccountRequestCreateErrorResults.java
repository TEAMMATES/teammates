package teammates.ui.output;

/**
 * The API output format of the error results when account request create request fails.
 */
public class AccountRequestCreateErrorResults extends ApiOutput {

    private String invalidNameMessage = "";
    private String invalidInstituteMessage = "";
    private String invalidCountryMessage = "";
    private String invalidEmailMessage = "";
    private String invalidHomePageUrlMessage = "";
    private String invalidCommentsMessage = "";

    public String getInvalidNameMessage() {
        return invalidNameMessage;
    }

    public void setInvalidNameMessage(String invalidNameMessage) {
        this.invalidNameMessage = invalidNameMessage;
    }

    public String getInvalidInstituteMessage() {
        return invalidInstituteMessage;
    }

    public void setInvalidInstituteMessage(String invalidInstituteMessage) {
        this.invalidInstituteMessage = invalidInstituteMessage;
    }

    public String getInvalidCountryMessage() {
        return invalidCountryMessage;
    }

    public void setInvalidCountryMessage(String invalidCountryMessage) {
        this.invalidCountryMessage = invalidCountryMessage;
    }

    public String getInvalidEmailMessage() {
        return invalidEmailMessage;
    }

    public void setInvalidEmailMessage(String invalidEmailMessage) {
        this.invalidEmailMessage = invalidEmailMessage;
    }

    public String getInvalidHomePageUrlMessage() {
        return invalidHomePageUrlMessage;
    }

    public void setInvalidHomePageUrlMessage(String invalidHomePageUrlMessage) {
        this.invalidHomePageUrlMessage = invalidHomePageUrlMessage;
    }

    public String getInvalidCommentsMessage() {
        return invalidCommentsMessage;
    }

    public void setInvalidCommentsMessage(String invalidCommentsMessage) {
        this.invalidCommentsMessage = invalidCommentsMessage;
    }

}
