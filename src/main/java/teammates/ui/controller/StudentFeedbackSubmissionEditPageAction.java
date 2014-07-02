package teammates.ui.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackSubmissionEditPageAction extends FeedbackSubmissionEditPageAction {
    @Override
    protected boolean isSpecificUserJoinedCourse() {
        return isJoinedCourse(courseId, account.googleId);
    }
    
    @Override
    protected void verifyAccesibleForSpecificUser() {
        new GateKeeper().verifyAccessible(
                logic.getStudentForGoogleId(courseId, account.googleId), 
                logic.getFeedbackSession(feedbackSessionName, courseId));
    }

    @Override
    protected String getUserEmailForCourse() {
        return logic.getStudentForGoogleId(courseId, account.googleId).email;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(
            String userEmailForCourse) throws EntityDoesNotExistException {
        FeedbackSessionQuestionsBundle questionsBundle = logic.getFeedbackSessionQuestionsBundleForStudent(
                feedbackSessionName, courseId, userEmailForCourse);
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        HashSet<String> notDisplayedInstructorEmails = new HashSet<String>();
        
        extractNotDisplayedInstrutorEmails(instructors, notDisplayedInstructorEmails);
        
        filterSessionQuestionBundle(questionsBundle, notDisplayedInstructorEmails);
        
        return questionsBundle;
    }

    private void extractNotDisplayedInstrutorEmails(
            List<InstructorAttributes> instructors, HashSet<String> notDisplayedInstructorEmails) {
        for (InstructorAttributes instructor : instructors) {
            if (!instructor.isDisplayedToStudents) {
                notDisplayedInstructorEmails.add(instructor.email);
            }
        }
    }

    private void filterSessionQuestionBundle(
            FeedbackSessionQuestionsBundle questionsBundle, HashSet<String> notDisplayedInstructorEmails) {
        // remove instructor who are not displayed to students
        for (FeedbackQuestionAttributes question : questionsBundle.questionResponseBundle.keySet()) {
            if (question.recipientType == FeedbackParticipantType.INSTRUCTORS) {
                Map<String, String> recipients = questionsBundle.recipientList.get(question.getId());
                List<FeedbackResponseAttributes> responses = questionsBundle.questionResponseBundle.get(question);
                for (String instrEmail : notDisplayedInstructorEmails) {
                    if (recipients.containsKey(instrEmail)) {
                        recipients.remove(instrEmail);
                    }
                    // remove the response if response stored already
                    Iterator<FeedbackResponseAttributes> iterResponse = responses.iterator();
                    while (iterResponse.hasNext()) {
                        FeedbackResponseAttributes response = iterResponse.next();
                        if (response.recipientEmail.equals(instrEmail)) {
                            iterResponse.remove();
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        return session.isOpened();
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Show student feedback submission edit page<br>" +
                "Session Name: " + feedbackSessionName + "<br>" + 
                "Course ID: " + courseId;
    }

    @Override
    protected ShowPageResult createSpecificShowPageResult() {
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
    }
    
    @Override
    protected RedirectResult createSpecificRedirectResult() {
        return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
    }
}
