package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;

public class InstructorFeedbackQuestionVisibilityMessagePageData extends PageData {
    public List<String> visibilityMessage;

    public InstructorFeedbackQuestionVisibilityMessagePageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }
}
