package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.Sanitizer;

public class InstructorEvalSubmissionEditPageData extends EvalSubmissionEditPageData {
    
    
    public InstructorEvalSubmissionEditPageData(AccountAttributes account) {
        super(account);
    }
    
    @Override
    public String getEvaluationSectionTitle(SubmissionAttributes sub){
        if(sub.reviewee.equals(sub.reviewer)){
            return Sanitizer.sanitizeForHtml(student.name) + "'s evaluation submission";
        } else {
            return "Evaluation for " + sub.details.revieweeName;
        }
    }
    
}
