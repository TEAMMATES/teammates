package teammates.logic.core;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;

public class InstructorsGiverTypeHandler implements GiverTypeHandler {
    @Override
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, CourseRoster courseRoster, FeedbackSessionsLogic fsLogic) {
        return courseRoster.getInstructors()
                .stream()
                .map(InstructorAttributes::getEmail)
                .collect(Collectors.toList());
    }
}