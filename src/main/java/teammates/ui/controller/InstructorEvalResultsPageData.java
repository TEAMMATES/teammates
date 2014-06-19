package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;

public class InstructorEvalResultsPageData extends PageData {
    public EvaluationResultsBundle evaluationResults;
    public InstructorAttributes instructor;

    public InstructorEvalResultsPageData(AccountAttributes account) {
        super(account);
    }
    
    public static String getPointsAsColorizedHtml(int points){
        return PageData.getPointsAsColorizedHtml(points);
    }
    
    public static String getPointsInEqualShareFormatAsHtml(int points, boolean inline){
        return PageData.getPointsInEqualShareFormatAsHtml(points, inline);
    }
    
    public static String getPointsDiffAsHtml(StudentResultBundle sub){
        return PageData.getPointsDiffAsHtml(sub);
    }
    
    public static String getJustificationAsSanitizedHtml(SubmissionAttributes sub){
        return PageData.getJustificationAsSanitizedHtml(sub);
    }
    
    
    public static String getNormalizedPointsListColorizedDescending(List<SubmissionAttributes> subs){
        String result = "";
        SubmissionAttributes.sortByNormalizedPointsDescending(subs);
        for(SubmissionAttributes sub: subs){
            if(sub.reviewee.equals(sub.reviewer)) continue;
            if(!result.isEmpty()) result+=", ";
            result+=getPointsAsColorizedHtml(sub.details.normalizedToInstructor);
        }
        return result;
    }
    
    
    public static String getP2pFeedbackAsHtml(String str, boolean enabled){
        return PageData.getP2pFeedbackAsHtml(str, enabled);
    }

}
