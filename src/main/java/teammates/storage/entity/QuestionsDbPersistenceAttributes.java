package teammates.storage.entity;

import teammates.common.datatransfer.FeedbackQuestionAttributes;

/**
 * Handles the conversion of FeedbackQuestionAttributes to Question in QuestionsDb
 *
 */
public class QuestionsDbPersistenceAttributes extends FeedbackQuestionAttributes {
    
    public QuestionsDbPersistenceAttributes(FeedbackQuestionAttributes old) {
        feedbackQuestionId = old.getId();
        feedbackSessionName = old.feedbackSessionName;
        courseId = old.courseId;
        creatorEmail = old.creatorEmail;
        
        questionMetaData = old.questionMetaData;
        questionDescription = old.questionDescription;
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
        // during the period when the code supports both old (FeedbackQuestion) and new Question types
        // allow setting of id by our code outside of (Feedback)?QuestionAttributes
        // TODO this should be removed once the old question type (FeedbackQuestion) is removed.
        String questionId = getId() == null ? makeId() : getId();
        return new Question(questionId,
                            feedbackSessionName, courseId, creatorEmail,
                            questionMetaData, questionDescription, questionNumber, questionType, giverType,
                            recipientType, numberOfEntitiesToGiveFeedbackTo,
                            showResponsesTo, showGiverNameTo, showRecipientNameTo);
    }
}
