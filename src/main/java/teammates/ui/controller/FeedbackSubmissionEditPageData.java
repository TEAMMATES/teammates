package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.ui.template.FeedbackSubmissionEditQuestion;
import teammates.ui.template.FeedbackSubmissionEditResponse;
import teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses;

public class FeedbackSubmissionEditPageData extends PageData {
    public FeedbackSessionQuestionsBundle bundle = null;
    private String moderatedQuestion = null;
    private boolean isSessionOpenForSubmission;
    private boolean isPreview;
    private boolean isModeration;
    private boolean isShowRealQuestionNumber;
    private boolean isHeaderHidden;
    private StudentAttributes studentToViewPageAs;
    private InstructorAttributes previewInstructor;    
    private String registerMessage; 
    private List<StudentFeedbackSubmissionEditQuestionsWithResponses> questionsWithResponses;
    
    public FeedbackSubmissionEditPageData(AccountAttributes account, StudentAttributes student) {
        super(account, student);
        isPreview = false;
        isModeration = false;
        isShowRealQuestionNumber = false;
        isHeaderHidden = false;        
    }

    public void init(String regKey, String email, String courseId) {
        String joinUrl = new Url(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                                        .withRegistrationKey(regKey)
                                        .withStudentEmail(email)
                                        .withCourseId(courseId)
                                        .toString();
        
        registerMessage = (student == null || joinUrl == null) ? "" : String.format(Const.StatusMessages.UNREGISTERED_STUDENT, 
                                                                                       student.name, joinUrl);
        createQuestionsWithResponses();        
    }
    
    public FeedbackSessionQuestionsBundle getBundle() {
        return bundle;
    }
    
    public String getModeratedQuestion() {
        return moderatedQuestion;
    }
   
    public boolean isSessionOpenForSubmission() {
        return isSessionOpenForSubmission;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public boolean isModeration() {
        return isModeration;
    }
    
    public boolean isShowRealQuestionNumber() {
        return isShowRealQuestionNumber;
    }

    public boolean isHeaderHidden() {
        return isHeaderHidden;
    }

    public StudentAttributes getStudentToViewPageAs() {
        return studentToViewPageAs;
    }
    
    public AccountAttributes getAccount() {
        return account;
    }
    
    public StudentAttributes getStudent() {
        return student;
    }
    
    public InstructorAttributes getPreviewInstructor() {
        return previewInstructor;
    }
    
    public String getRegisterMessage() {
        return registerMessage;
    }
    
    public String getSubmitAction() {
        return isModeration ? Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_SAVE
                              : Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE;
    }
    
    public String getSubmitActionQuestion() {
        return isModeration ? Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_SAVE
                              : Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE;
    }
    
    public boolean isSubmittable() {
        return isSessionOpenForSubmission || isModeration;
    }
    
    public List<StudentFeedbackSubmissionEditQuestionsWithResponses> getQuestionsWithResponses() {
        return questionsWithResponses;
    }

    public void setModeratedQuestion(String moderatedQuestion) {
        this.moderatedQuestion = moderatedQuestion;
    }

    public void setSessionOpenForSubmission(boolean isSessionOpenForSubmission) {
        this.isSessionOpenForSubmission = isSessionOpenForSubmission;
    }

    public void setPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }

    public void setModeration(boolean isModeration) {
        this.isModeration = isModeration;
    }

    public void setShowRealQuestionNumber(boolean isShowRealQuestionNumber) {
        this.isShowRealQuestionNumber = isShowRealQuestionNumber;
    }

    public void setHeaderHidden(boolean isHeaderHidden) {
        this.isHeaderHidden = isHeaderHidden;
    }

    public void setStudentToViewPageAs(StudentAttributes studentToViewPageAs) {
        this.studentToViewPageAs = studentToViewPageAs;
    }

    public void setPreviewInstructor(InstructorAttributes previewInstructor) {
        this.previewInstructor = previewInstructor;
    }

    public void setRegisterMessage(String registerMessage) {
        this.registerMessage = registerMessage;
    }

    public List<String> getRecipientOptionsForQuestion(String feedbackQuestionId, String currentlySelectedOption) {
        ArrayList<String> result = new ArrayList<String>();
        
        if (this.bundle == null) {
            return null;
        }
        
        Map<String, String> emailNamePair = this.bundle.getSortedRecipientList(feedbackQuestionId);
        
        // Add an empty option first.
        result.add(
            "<option value=\"\" " +
            (currentlySelectedOption == null ? "selected=\"selected\">" : ">") +
            "</option>"
        );
        
        for (Map.Entry<String, String> pair : emailNamePair.entrySet()) {
            result.add(
                "<option value=\"" + pair.getKey() + "\"" 
                + (StringHelper.recoverFromSanitizedText(pair.getKey()).equals(currentlySelectedOption)  
                                                ? " selected=\"selected\"" : "")
                + ">" + sanitizeForHtml(pair.getValue()) 
                + "</option>"
            );
        }

        return result;
    }
    
    private void createQuestionsWithResponses() {
        questionsWithResponses = new ArrayList<StudentFeedbackSubmissionEditQuestionsWithResponses>();
        int qnIndx = 1;
        
        for (FeedbackQuestionAttributes questionAttributes : bundle.getSortedQuestions()) {
            int numOfResponseBoxes = questionAttributes.numberOfEntitiesToGiveFeedbackTo;
            int maxResponsesPossible = bundle.recipientList.get(questionAttributes.getId()).size();
            
            if (numOfResponseBoxes == Const.MAX_POSSIBLE_RECIPIENTS || numOfResponseBoxes > maxResponsesPossible) {
                numOfResponseBoxes = maxResponsesPossible;
            }
            
            if (numOfResponseBoxes > 0) {
                FeedbackSubmissionEditQuestion question = createQuestion(questionAttributes, qnIndx);
                List<FeedbackSubmissionEditResponse> responses = createResponses(questionAttributes, qnIndx, numOfResponseBoxes);
            
                questionsWithResponses.add(new StudentFeedbackSubmissionEditQuestionsWithResponses(
                                                question, responses, numOfResponseBoxes, maxResponsesPossible));
                qnIndx++;
            }
        }
    }

    private FeedbackSubmissionEditQuestion createQuestion(FeedbackQuestionAttributes questionAttributes, int qnIndx) {
        boolean isModeratedQuestion = String.valueOf(questionAttributes.questionNumber).equals(getModeratedQuestion());
        
        return new FeedbackSubmissionEditQuestion(questionAttributes, qnIndx, isModeratedQuestion);
    }

    private List<FeedbackSubmissionEditResponse> createResponses(
                                    FeedbackQuestionAttributes questionAttributes, int qnIndx, int numOfResponseBoxes) {
        List<FeedbackSubmissionEditResponse> responses = new ArrayList<FeedbackSubmissionEditResponse>();
        
        List<FeedbackResponseAttributes> existingResponses = bundle.questionResponseBundle.get(questionAttributes);
        int responseIndx = 0;
        
        for(FeedbackResponseAttributes existingResponse : existingResponses) {
            List<String> recipientOptionsForQuestion = getRecipientOptionsForQuestion(
                                                           questionAttributes.getId(), existingResponse.recipientEmail);
            
            String submissionFormHtml = questionAttributes.getQuestionDetails()
                                            .getQuestionWithExistingResponseSubmissionFormHtml(isSessionOpenForSubmission,
                                                                                               qnIndx, responseIndx, questionAttributes.courseId,
                                                                                               existingResponse.getResponseDetails());
            
            responses.add(new FeedbackSubmissionEditResponse(responseIndx, true, recipientOptionsForQuestion, 
                                                                 submissionFormHtml, existingResponse.getId()));
            responseIndx++;
        }
        
        while (responseIndx < numOfResponseBoxes) {
            List<String> recipientOptionsForQuestion = getRecipientOptionsForQuestion(questionAttributes.getId(), null);
            String submissionFormHtml = questionAttributes.getQuestionDetails()
                                            .getQuestionWithoutExistingResponseSubmissionFormHtml(isSessionOpenForSubmission,
                                                                                                  qnIndx, responseIndx, questionAttributes.courseId);
            
            responses.add(new FeedbackSubmissionEditResponse(responseIndx, false, recipientOptionsForQuestion, submissionFormHtml, ""));
            responseIndx++;
        }
        
        return responses;
    }
}
