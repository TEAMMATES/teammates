package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Url;

/**
 * PageData object that is sent back as a result of InstructorFeedbackEditCopy.
 * Specifies a page for the user to be redirected to as a result of the form submission.
 *
 */
public class InstructorFeedbackEditCopyData extends PageData {
    public final String redirectUrl;
    public final String errorMessage;

    public InstructorFeedbackEditCopyData(AccountAttributes account,
                                          Url redirectUrl, String errorMessage) {
        super(account);
        String redirectUrlAsString = redirectUrl == null ? ""
                                                         : redirectUrl.toString();
        this.redirectUrl = redirectUrlAsString;
        this.errorMessage = errorMessage == null ? ""
                                                 : errorMessage;
    }

    /**
     * Creates a new {@code InstructorFeedbackEditCopyData} with a redirect url, and without an errorMessage.
     */
    public InstructorFeedbackEditCopyData(AccountAttributes account, Url redirectUrl) {
        this(account, redirectUrl, null);
    }

    /**
     * Creates a new {@code InstructorFeedbackEditCopyData} with an error message, and a redirect url of "".
     */
    public InstructorFeedbackEditCopyData(AccountAttributes account, String errorMessage) {
        this(account, null, errorMessage);
    }
}
