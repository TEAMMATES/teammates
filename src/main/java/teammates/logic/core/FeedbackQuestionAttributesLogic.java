package teammates.logic.core;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

import java.util.ArrayList;
import java.util.List;

public class FeedbackQuestionAttributesLogic {
    List<FeedbackQuestionAttributes> getFeedbackQuestionAttributes(FeedbackSessionAttributes session, boolean isInstructor, FeedbackQuestionsLogic fqLogic, FeedbackResponsesLogic frLogic) {
        List<teammates.common.datatransfer.attributes.FeedbackQuestionAttributes> questionsWithVisibleResponses = new ArrayList<>();
        List<teammates.common.datatransfer.attributes.FeedbackQuestionAttributes> questionsForUser =
                fqLogic.getFeedbackQuestionsForSession(session.getFeedbackSessionName(), session.getCourseId());
        for (teammates.common.datatransfer.attributes.FeedbackQuestionAttributes question : questionsForUser) {
            if (!isInstructor && frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question)
                    || isInstructor && frLogic.isResponseOfFeedbackQuestionVisibleToInstructor(question)) {
                // We only need one question with visible responses for the entire session to be visible
                questionsWithVisibleResponses.add(question);
                break;
            }
        }
        return questionsWithVisibleResponses;
    }
}
