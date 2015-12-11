package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;

public class FeedbackRankOptionsResponseDetails extends FeedbackRankResponseDetails {
    private List<Integer> answers;
    
    public FeedbackRankOptionsResponseDetails() {
        super(FeedbackQuestionType.RANK_OPTIONS);
    }
    
    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType, 
                                       FeedbackQuestionDetails questionDetails, 
                                       String[] answer) {
        List<Integer> rankAnswer = new ArrayList<Integer>();
        for (int i = 0; i < answer.length ; i++){
            try {
                rankAnswer.add(Integer.parseInt(answer[i]));
            } catch (NumberFormatException e) {
                rankAnswer.add(Const.POINTS_NOT_SUBMITTED);
            }
        }
        FeedbackRankOptionsQuestionDetails rankQuestion = (FeedbackRankOptionsQuestionDetails) questionDetails;
        this.setRankResponseDetails(rankAnswer, rankQuestion.options);
    }

    /**
     * @return List of answers, with uninitialised values filtered out
     */
    public List<Integer> getFilteredAnswerList() {
        List<Integer> filteredAnswers = new ArrayList<>();
        
        for (int answer : answers) {
            if (answer != Const.POINTS_NOT_SUBMITTED) {
                filteredAnswers.add(answer);
            }
        }
        
        return filteredAnswers;
    }
    
    public List<Integer> getAnswerList() {
        return new ArrayList<>(answers);
    }
    
    @Override
    public String getAnswerString() {
        String listString = getFilteredAnswerList().toString(); //[1, 2, 3] format
        return listString.substring(1, listString.length() - 1); //remove []
    }

    @Override
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        FeedbackRankOptionsQuestionDetails rankQuestion = (FeedbackRankOptionsQuestionDetails) questionDetails;
        
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<ul>");
        
        for (int i = 0 ; i < answers.size() ; i++) {
            if (answers.get(i) == Const.POINTS_NOT_SUBMITTED) {
                continue;
            }
            String answer = answers.get(i).toString();
            String option = rankQuestion.options.get(i);
            
            
            htmlBuilder.append("<li>");
            htmlBuilder.append(option);
            htmlBuilder.append(": ");
            htmlBuilder.append(Sanitizer.sanitizeForHtml(answer));
            htmlBuilder.append("</li>");
        }
        
        htmlBuilder.append("</ul>");
        return htmlBuilder.toString();
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        StringBuilder csvBuilder = new StringBuilder();
        
        for (int answer : answers) {
            if (answer == Const.POINTS_NOT_SUBMITTED) {
                continue;
            }
            
            csvBuilder.append(",");
            csvBuilder.append(answer);
        }

        return csvBuilder.toString();
    }

    private void setRankResponseDetails(List<Integer> answers, List<String> options) {
        this.answers = answers;
    
        Assumption.assertEquals("Rank question: number of responses does not match number of options. " 
                                        + answers.size() + "/" + options.size(), 
                                answers.size(), options.size());
        
    }

}
