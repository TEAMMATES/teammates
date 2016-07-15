package teammates.storage.entity;

import teammates.common.datatransfer.FeedbackQuestionAttributes;

public class QuestionAttributes extends FeedbackQuestionAttributes {
    
    public QuestionAttributes(FeedbackQuestionAttributes old) {
        feedbackSessionName = old.feedbackSessionName;
        courseId = old.courseId;
        creatorEmail = old.creatorEmail;
        
        questionMetaData = old.questionMetaData;
        questionNumber = old.questionNumber;
        questionType = old.questionType;
        giverType = old.giverType;
        recipientType = old.recipientType;
        numberOfEntitiesToGiveFeedbackTo = old.numberOfEntitiesToGiveFeedbackTo;
        showResponsesTo = old.showResponsesTo;
        showGiverNameTo = old.showGiverNameTo;
        showRecipientNameTo = old.showRecipientNameTo;
        createdAt = old.getCreatedAt();
        updatedAt = old.getUpdatedAt();
    }
    
    @Override
    public Question toEntity() {
        String questionId = makeId();
        return new Question(questionId,
                            feedbackSessionName, courseId, creatorEmail,
                            questionMetaData, questionNumber, questionType, giverType,
                            recipientType, numberOfEntitiesToGiveFeedbackTo,
                            showResponsesTo, showGiverNameTo, showRecipientNameTo);
    }
}
