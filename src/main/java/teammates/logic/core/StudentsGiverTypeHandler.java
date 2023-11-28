package teammates.logic.core;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class StudentsGiverTypeHandler implements GiverTypeHandler {
    @Override
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, CourseRoster courseRoster, FeedbackSessionsLogic fsLogic) {
        return courseRoster.getStudents()
                .stream()
                .map(StudentAttributes::getEmail)
                .collect(Collectors.toList());
    }
}
