package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;

public class InstructorFeedbackQuestionVisibilityMessagePageData extends PageData {
    List<String> visibilityMessage;

    public InstructorFeedbackQuestionVisibilityMessagePageData(AccountAttributes account) {
        super(account);
    }
}
