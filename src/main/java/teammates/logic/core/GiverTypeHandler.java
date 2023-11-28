package teammates.logic.core;

import java.util.*;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

public interface GiverTypeHandler {
    List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, CourseRoster courseRoster, FeedbackSessionsLogic fsLogic);
}