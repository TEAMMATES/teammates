package teammates.ui.controller;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackQuestionEditForm;

public class InstructorFeedbackEditPageData extends PageData {

    private List<FeedbackQuestionEditForm> qnForms;
    
    public InstructorFeedbackEditPageData(AccountAttributes account) {
        super(account);
        
    }
    

    public void init(FeedbackSessionAttributes feedbackSession, 
                     List<FeedbackQuestionAttributes> questions,
                     List<FeedbackQuestionAttributes> copiableQuestions,
                     Map<String, Boolean> questionHasResponses,
                     List<StudentAttributes> studentList,
                     List<InstructorAttributes> instructorList,
                     InstructorAttributes instructor) {
        
        qnForms = new ArrayList<FeedbackQuestionEditForm>();
        for (FeedbackQuestionAttributes question : questions) {
            // build question edit form
            FeedbackQuestionEditForm qnForm = new FeedbackQuestionEditForm();
            qnForm.setCourseId(instructor.courseId);
            qnForm.setFeedbackSessionName(feedbackSession.feedbackSessionName);
            qnForm.setQuestion(question);
            qnForm.setQuestionDetails(question.getQuestionDetails());
            qnForm.setGiverParticipantOptions(getParticipantOptions(question, true));
            qnForm.setRecipientParticipantOptions(getParticipantOptions(question, false));
            qnForm.setNumOfQuestionsOnPage(questions.size());
            qnForm.setQuestionNumberOptions(getQuestionNumberOptions(questions.size()));
            qnForm.setQuestionTest(question.getQuestionDetails().questionText);
            
            // maps 
            
            Map<String, Boolean> isGiverNameVisible = new HashMap<String, Boolean>();
            for (FeedbackParticipantType giverType : question.showGiverNameTo) {
                isGiverNameVisible.put(giverType.name(), true);
            }
            qnForm.setIsGiverNameVisible(isGiverNameVisible);
            
            Map<String, Boolean> isRecipientNameVisible = new HashMap<String, Boolean>();
            for (FeedbackParticipantType recipientType : question.showRecipientNameTo) {
                isRecipientNameVisible.put(recipientType.name(), true);
            }
            qnForm.setIsRecipientNameVisible(isRecipientNameVisible);
            
            Map<String, Boolean> isResponsesVisible = new HashMap<String, Boolean>();
            for (FeedbackParticipantType participantType : question.showResponsesTo) {
                isResponsesVisible.put(participantType.name(), true);
            }
            qnForm.setIsResponseVisible(isResponsesVisible);
            
            qnForm.setNumberOfEntitiesToGiveFeedbackToChecked(question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS);
            qnForm.setNumOfEntitiesToGiveFeedbackToValue(question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS ?
                                                         1 :
                                                         question.numberOfEntitiesToGiveFeedbackTo );
            qnForm.setQuestionHasResponses(questionHasResponses.get(question.getId()));
            
            List<String> visibilityMessages = question.getVisibilityMessage();
            qnForm.setVisibilityMessages(visibilityMessages);
            
            
            qnForm.setQuestionSpecificEditFormHtml(question.getQuestionDetails().getQuestionSpecificEditFormHtml(question.questionNumber));
            
            qnForms.add(qnForm);
        }
        
    }
    
    
    
    
    public List<FeedbackQuestionEditForm> getQnForms() {
        return qnForms;
    }


    /**
     * Returns a list of HTML options for selecting participant type.
     * Used in instructorFeedbackEdit.jsp for selecting the participant type for a new question.
     * isGiver refers to the feedback path (!isGiver == feedback's recipient)
     */
    public List<ElementTag> getParticipantOptions(FeedbackQuestionAttributes question, boolean isGiver) {
        List<ElementTag> result = new ArrayList<ElementTag>();
        for (FeedbackParticipantType option : FeedbackParticipantType.values()) {
            
            boolean isValidGiver = isGiver && option.isValidGiver();
            boolean isValidRecipient = !isGiver && option.isValidRecipient();
            
            if (isValidGiver || isValidRecipient) {
                String participantName = isValidGiver ? option.toDisplayGiverName()
                                                      : option.toDisplayRecipientName();
                
                boolean selected = false;
                // for existing questions
                if (question != null) {
                    boolean isGiverType     = isValidGiver     && question.giverType == option;
                    boolean isRecipientType = isValidRecipient && question.recipientType == option;
                    
                    selected = (isGiverType || isRecipientType); 
                }
                
                ElementTag optionTag = createOption(participantName, option.toString(), selected);
                result.add(optionTag);
            }
        }
        return result;
    }
    
    public List<ElementTag> getQuestionNumberOptions(int numQuestions) {
        List<ElementTag> options = new ArrayList<ElementTag>();
        
        for (int opt = 1; opt < numQuestions + 1; opt++) {
            ElementTag option = createOption(String.valueOf(opt), String.valueOf(opt), false);
            options.add(option);
        }
        
        return options;
    }
    
    /**
     * Returns a list of HTML options for selecting question type.
     * Used in instructorFeedbackEdit.jsp for selecting the question type for a new question.
     */
    public List<String> getQuestionTypeChoiceOptions() {
        List<String> options = new ArrayList<String>();
        for (FeedbackQuestionType type : FeedbackQuestionType.values()) {
            options.add(type.getFeedbackQuestionDetailsInstance().getQuestionTypeChoiceOption());
        }
        return options;
    }
    
    /**
     * Get all question specific edit forms
     * Used in instructorFeedbackEdit.jsp for new question
     * @return
     */
    public String getNewQuestionSpecificEditFormHtml() {
        String newQuestionSpecificEditForms = "";
        for (FeedbackQuestionType type : FeedbackQuestionType.values()) {
            newQuestionSpecificEditForms +=
                    type.getFeedbackQuestionDetailsInstance().getNewQuestionSpecificEditFormHtml();
        }
        return newQuestionSpecificEditForms;
    }



}