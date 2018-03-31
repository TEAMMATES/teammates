package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackEditPageData;

public class InstructorFeedbackEditPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        String shouldLoadInEditModeParam = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENABLE_EDIT);
        boolean shouldLoadInEditMode = "true".equals(shouldLoadInEditModeParam);

        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                feedbackSession,
                false,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        List<FeedbackQuestionAttributes> questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);

        Map<String, Boolean> questionHasResponses = new HashMap<>();

        for (FeedbackQuestionAttributes question : questions) {
            boolean hasResponse = logic.areThereResponsesForQuestion(question.getId());
            questionHasResponses.put(question.getId(), hasResponse);
        }

        List<StudentAttributes> studentList = logic.getStudentsForCourse(courseId);
        studentList.sort(Comparator.comparing((StudentAttributes student) -> student.team.toLowerCase())
                .thenComparing(student -> student.name.toLowerCase()));

        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);
        List<InstructorAttributes> instructorsWhoCanSubmit = new ArrayList<>();
        for (InstructorAttributes instructor : instructorList) {
            if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
                instructorsWhoCanSubmit.add(instructor);
            }
        }
        instructorList.sort(Comparator.comparing(instructor -> instructor.name.toLowerCase()));

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        int numOfInstructors = instructorList.size();

        statusToAdmin = "instructorFeedbackEdit Page Load<br>"
                        + "Editing information for Feedback Session "
                        + "<span class=\"bold\">[" + feedbackSessionName + "]</span>"
                        + "in Course: <span class=\"bold\">[" + courseId + "]</span>";

        InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account, sessionToken);

        data.init(feedbackSession, questions, questionHasResponses, studentList, instructorsWhoCanSubmit, instructor,
                shouldLoadInEditMode, numOfInstructors, logic.getCourseDetails(courseId));

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_EDIT, data);
    }

}
