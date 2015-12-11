package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;

public abstract class FeedbackRankQuestionDetails extends FeedbackQuestionDetails {
    
    public boolean areDuplicatesAllowed;

    public FeedbackRankQuestionDetails(FeedbackQuestionType questionType) {
        super(questionType);
    }

    public FeedbackRankQuestionDetails(FeedbackQuestionType questionType, String questionText) {
        super(questionType, questionText);
    }

    @Override
    public boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                          FeedbackQuestionType questionType) {
        
        String areDuplicatesAllowedString 
            = HttpRequestHelper.getValueFromParamMap(requestParameters, 
                                                     Const.ParamsNames.FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED);
        boolean areDuplicatesAllowed = areDuplicatesAllowedString != null 
                                    && areDuplicatesAllowedString.equals("on");
        
        this.areDuplicatesAllowed = areDuplicatesAllowed;
        return true;
    }


    @Override
    public abstract String getQuestionTypeDisplayName();

    @Override
    public abstract String getQuestionWithExistingResponseSubmissionFormHtml(
                        boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                        int totalNumRecipients,
                        FeedbackResponseDetails existingResponseDetails);

    @Override
    public abstract String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients);
    


    @Override
    public abstract String getQuestionSpecificEditFormHtml(int questionNumber);


    /**
     * Used to update the OptionPointsMapping for the option optionReceivingPoints
     * 
     * @param optionPoints
     * @param optionReceivingPoints
     * @param pointsReceived
     */
    protected void updateOptionRanksMapping(
            Map<String, List<Integer>> optionPoints,
            String optionReceivingPoints, int pointsReceived) {
        List<Integer> points = optionPoints.get(optionReceivingPoints);
        if (points == null) {
            points = new ArrayList<Integer>();
            optionPoints.put(optionReceivingPoints, points);
        }
        
        points.add(pointsReceived);
    }

    /**
     * Returns the list of points as as string to display
     * @param ranksReceived
     */
    protected String getListOfRanksReceivedAsString(List<Integer> ranksReceived) {
        Collections.sort(ranksReceived);
        String pointsReceived = "";
        
        if (ranksReceived.size() > 10) {
            for (int i = 0; i < 5; i++) {
                pointsReceived += ranksReceived.get(i) + " , ";
            }
            
            pointsReceived += "...";
            
            for (int i = ranksReceived.size() - 5; i < ranksReceived.size(); i++) {
                pointsReceived += " , " + ranksReceived.get(i);
            }
        } else {
            for (int i = 0; i < ranksReceived.size(); i++) {
                pointsReceived += ranksReceived.get(i);
                
                if (i != ranksReceived.size() - 1) {
                    pointsReceived += " , ";
                }
            }
        }
        
        return pointsReceived;
    }

    protected double computeAverage(List<Integer> values) {
        double average = 0;
        for (int value : values) {
            average += value;
        }
        average = average / values.size();
        return average;
    }
    
    

}
