package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEditStudentFeedbackPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID); 
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String moderatedEntityIdentifier = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT);
        String moderatedQuestionNumber = getRequestParamValue("moderatedquestion");


        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.COURSE_ID), 
                                 courseId);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_NAME),
                                 feedbackSessionName);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT),
                                 moderatedEntityIdentifier);

        
        StudentAttributes studentUnderModeration = logic.getStudentForEmail(courseId, moderatedEntityIdentifier); 
        
        if (studentUnderModeration == null) {
            List<TeamDetailsBundle> teams = logic.getTeamsForCourse(courseId);
            boolean isTeam = false;
            
            for (TeamDetailsBundle team : teams) {
                if (team.name.equals(moderatedEntityIdentifier)) {
                    isTeam = true;
                    studentUnderModeration = team.students.get(0);
                    break;
                }
            }
            
            if (!isTeam) {
                throw new EntityDoesNotExistException("An entity with the identifier "
                        + moderatedEntityIdentifier + " does not exist in " + courseId
                        + ".");
            }
        }
        
        new GateKeeper().verifyAccessible(logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, studentUnderModeration.section, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        
        FeedbackSubmissionEditPageData data = new FeedbackSubmissionEditPageData(account, student);
        
        data.bundle = logic.getFeedbackSessionQuestionsBundleForStudent(
                feedbackSessionName, courseId, studentUnderModeration.email);
        
        Assumption.assertNotNull(data.bundle);
        
        data.setSessionOpenForSubmission(true);
        data.setModeration(true);
        data.setHeaderHidden(true);
        data.setStudentToViewPageAs(studentUnderModeration);
        
        if (moderatedQuestionNumber != null) {
          data.setModeratedQuestion(moderatedQuestionNumber);
        }
        
        hideQuestionsWithAnonymousResponses(data.bundle);

        statusToAdmin = "Moderating feedback session for student (" + studentUnderModeration.email + ")<br>" +
                "Session Name: " + feedbackSessionName + "<br>" +
                "Course ID: " + courseId;
        
        data.init("", "", courseId);
        
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
    }
    
    /**
     * Removes question from the bundle if the question has givers, recipients or responses that are anonymous to instructors.
     * @param bundle
     */
    private boolean hideQuestionsWithAnonymousResponses(FeedbackSessionQuestionsBundle bundle) {
        List<FeedbackQuestionAttributes> questionsToHide = new ArrayList<FeedbackQuestionAttributes>();
        
        for (FeedbackQuestionAttributes question : bundle.questionResponseBundle.keySet()) {
            boolean isGiverVisibleToInstructor = question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            boolean isRecipientVisibleToInstructor = question.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            boolean isResponseVisibleToInstructor = question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS);

            if (!isResponseVisibleToInstructor || !isGiverVisibleToInstructor || !isRecipientVisibleToInstructor) {
                questionsToHide.add(question);
                bundle.questionResponseBundle.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
        }
        
        bundle.questionResponseBundle.keySet().removeAll(questionsToHide);
        
        return !questionsToHide.isEmpty();
    }
}
