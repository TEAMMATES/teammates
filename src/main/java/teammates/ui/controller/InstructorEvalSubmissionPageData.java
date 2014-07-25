package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;

public class InstructorEvalSubmissionPageData extends PageData {
    public EvaluationAttributes evaluation;
    public StudentResultBundle studentResult;

    public InstructorEvalSubmissionPageData(AccountAttributes account) {
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
        
        
        public static String getP2pFeedbackAsHtml(String str, boolean enabled){
            return PageData.getP2pFeedbackAsHtml(str, enabled);
        }


}
