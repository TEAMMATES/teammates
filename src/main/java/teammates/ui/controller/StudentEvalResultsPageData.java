package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;

public class StudentEvalResultsPageData extends PageData {
    
    public StudentEvalResultsPageData(AccountAttributes account) {
        super(account);
    }
    
    public EvaluationAttributes eval;
    public List<SubmissionAttributes> incoming;
    public List<SubmissionAttributes> outgoing;
    public List<SubmissionAttributes> selfEvaluations;

    public StudentResultBundle evalResult;
    
    public static String getPointsAsColorizedHtml(int points){
        return PageData.getPointsAsColorizedHtml(points);
    }
    
    public static String getPointsListOriginal(List<SubmissionAttributes> subs){
        return getPointsList(subs, false);
    }
    
    //TODO: This method doesn't seem to be used
    public static String getPointsListNormalized(List<SubmissionAttributes> subs){
        return getPointsList(subs, true);
    }
    
    /**
     * Prints the list of normalized points from the given list of submission data.
     * It will be colorized and printed descending.
     */
    public static String getPointsList(List<SubmissionAttributes> subs, final boolean normalized){
        //TODO: remove boolean variable and have two different variations of the method?
        String result = "";
        Collections.sort(subs, new Comparator<SubmissionAttributes>(){
            @Override
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2){
                if(normalized)
                    return Integer.valueOf(s2.details.normalizedToInstructor).compareTo(s1.details.normalizedToInstructor);
                else
                    return Integer.valueOf(s2.points).compareTo(s1.points);
            }
        });
        for(SubmissionAttributes sub: subs){
            if(sub.reviewee.equals(sub.reviewer)) continue;
            if(!result.isEmpty()) result+=", ";
            if(normalized){
                result+=getPointsAsColorizedHtml(sub.details.normalizedToInstructor);
            } else{
                result+=getPointsAsColorizedHtml(sub.points);
            }
        }
        return result;
    }
    
    /**
     * @return The "normalized-for-student-view" points of the given 
     *    submission list, sorted in descending order, formatted as a comma 
     *    separated and colorized string. Excludes self-rating.
     */
    public static String getNormalizedToStudentsPointsList(final List<SubmissionAttributes> subs){
        String result = "";
        List<SubmissionAttributes> tempSubs =  new ArrayList<SubmissionAttributes>(subs);

        Collections.sort(tempSubs, new Comparator<SubmissionAttributes>(){
            @Override
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2){
                return Integer.valueOf(s2.details.normalizedToStudent).compareTo(s1.details.normalizedToStudent);
            }
        });
        for(SubmissionAttributes sub: tempSubs){
            if(sub.reviewee.equals(sub.reviewer)) continue;
            if(!result.isEmpty()) result+=", ";
            result+=InstructorEvalResultsPageData.getPointsAsColorizedHtml(sub.details.normalizedToStudent);
        }
        return result;
    }
    
    /**
     * Make the headings bold, and covert newlines to html linebreaks.
     * @param isP2pEnabled Whether the P2P feedback is enabled.
     */
    public static String formatP2PFeedback(String feedbackGiven, boolean isP2pEnabled){
        if(!isP2pEnabled){
            return "<span style=\"font-style: italic;\">Disabled</span>";
        }
        if(feedbackGiven.equals("") || feedbackGiven == null){
            return "N/A";
        }
        return feedbackGiven.replace("&lt;&lt;What I appreciate about you as a team member&gt;&gt;:", "<span class=\"bold\">What I appreciate about you as a team member:</span>")
                .replace("&lt;&lt;Areas you can improve further&gt;&gt;:", "<span class=\"bold\">Areas you can improve further:</span>")
                .replace("&lt;&lt;Other comments&gt;&gt;:", "<span class=\"bold\">Other comments:</span>")
                .replace("&#010;", "<br>");
    }

}
