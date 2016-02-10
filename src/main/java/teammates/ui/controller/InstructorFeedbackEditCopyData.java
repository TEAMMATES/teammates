package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

/**
 * PageData object that is sent back as a result of InstructorFeedbackEditCopy. 
 * Specifies a page for the user to be redirected to as a result of the form submission. 
 *
 */
public class InstructorFeedbackEditCopyData extends PageData {
    public final String redirectUrl;
    public final String errorMessage;
    public final boolean isError;
    
    
    public InstructorFeedbackEditCopyData(AccountAttributes account, 
                                          String redirectUrl, String errorMessage) {
        super(account);
        this.redirectUrl = redirectUrl != null ? redirectUrl : "";
        this.isError = !errorMessage.isEmpty();
        this.errorMessage = errorMessage;
    }
    
    /**
     * @return new {@code InstructorFeedbackEditCopyData} with an error message, and a redirect url of ""
     */
    public InstructorFeedbackEditCopyData(AccountAttributes account, String errorMessage) {
        this(account, "", errorMessage);
    }
}
