package teammates.logic.core;

import java.util.*;
import java.util.stream.Collectors;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Logger;

public class GiversLogic {
    private Map<FeedbackParticipantType, GiverTypeHandler> giverTypeHandlerMap;
    private static final Logger log = Logger.getLogger();

    public GiversLogic() {
        giverTypeHandlerMap = new HashMap<>();
        giverTypeHandlerMap.put(FeedbackParticipantType.STUDENTS, new StudentsGiverTypeHandler());
        giverTypeHandlerMap.put(FeedbackParticipantType.INSTRUCTORS, new InstructorsGiverTypeHandler());
        giverTypeHandlerMap.put(FeedbackParticipantType.TEAMS, new TeamsGiverTypeHandler());
        giverTypeHandlerMap.put(FeedbackParticipantType.SELF, new SelfGiverTypeHandler());
    }

    public List<String> getPossibleGivers(
            FeedbackQuestionAttributes fqa,CourseRoster courseRoster, FeedbackSessionsLogic fsLogic) {
        FeedbackParticipantType giverType = fqa.getGiverType();
        GiverTypeHandler handler = giverTypeHandlerMap.get(giverType);

        if (handler != null) {
            return handler.getPossibleGivers(fqa, courseRoster, fsLogic);
        } else {
            log.severe("Invalid giver type specified");
            return Collections.emptyList();
        }
    }
}
