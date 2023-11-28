package teammates.logic.core;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

public class TeamsGiverTypeHandler implements GiverTypeHandler {
    @Override
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, CourseRoster courseRoster, FeedbackSessionsLogic fsLogic) {
        return new ArrayList<>(courseRoster.getTeamToMembersTable().keySet());
    }
}
