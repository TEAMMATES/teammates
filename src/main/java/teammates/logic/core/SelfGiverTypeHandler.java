package teammates.logic.core;

import java.util.Collections;
import java.util.List;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

public class SelfGiverTypeHandler implements GiverTypeHandler {
    @Override
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, CourseRoster courseRoster, FeedbackSessionsLogic fsLogic) {
        FeedbackSessionAttributes feedbackSession =
                fsLogic.getFeedbackSession(fqa.getFeedbackSessionName(), fqa.getCourseId());
        return Collections.singletonList(feedbackSession.getCreatorEmail());
    }
}
