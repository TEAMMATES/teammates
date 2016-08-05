package teammates.storage.entity;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * Handles the conversion of FeedbackQuestionAttributes to Question in QuestionsDb
 *
 */
public class QuestionsDbPersistenceAttributes extends FeedbackQuestionAttributes {
    
    public QuestionsDbPersistenceAttributes(FeedbackQuestionAttributes old) {
        feedbackQuestionId = old.getId();
        
        // during the period when the code supports both old (FeedbackQuestion) and new Question types
        // allow setting of id by our code outside of (Feedback)?QuestionAttributes
        // to allow setting id to be the same as the old question type
        // TODO this should be removed once the old question type (FeedbackQuestion) is removed.
        if (feedbackQuestionId == null) {
            Assumption.fail("Question id should be set");
        }
        
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
        // to allow setting id to be the same as the old question type
        // TODO this should be removed once the old question type (FeedbackQuestion) is removed.
        if (getId() == null) {
            Assumption.fail("Question id should be set");
        }
        Question q = new Question(getId(),
                            feedbackSessionName, courseId, creatorEmail,
                            questionMetaData, questionDescription, questionNumber, questionType, giverType,
                            recipientType, numberOfEntitiesToGiveFeedbackTo,
                            showResponsesTo, showGiverNameTo, showRecipientNameTo);
        if (getCreatedAt() != Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP) {
            q.setCreatedAt(getCreatedAt());
        }
        return q;
    }
}
