package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationDetailsBundle;

public class EvaluationStatsPageData extends PageData {
    public EvaluationDetailsBundle evaluationDetails;
    
    public EvaluationStatsPageData(AccountAttributes account) {
        super(account);
    }
}
