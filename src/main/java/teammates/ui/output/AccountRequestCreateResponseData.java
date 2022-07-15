package teammates.ui.output;

import javax.annotation.Nullable;

/**
 * The API output format for account request create request.
 */
public class AccountRequestCreateResponseData extends ApiOutput {

    private String message;
    @Nullable
    private String joinLink;

    public AccountRequestCreateResponseData() {}

    public void setMessage(String message) {
        this.message = message;
    }

    public void setJoinLink(String joinLink) {
        this.joinLink = joinLink;
    }

    public String getMessage() {
        return message;
    }

    public String getJoinLink() {
        return joinLink;
    }

    /**
     * The API output format of the error results when account request create request fails.
     */
    public static class AccountRequestCreateErrorResults extends ApiOutput {
        private String invalidNameMessage = "";
        private String invalidInstituteMessage = "";
        private String invalidCountryMessage = "";
        private String invalidEmailMessage = "";
        private String invalidHomePageUrlMessage = "";
        private String invalidCommentsMessage = "";

        public AccountRequestCreateErrorResults() {}

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

}
