package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackEditPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(feedbackSessionName);       
        
        FeedbackSessionAttributes feedback = logic.getFeedbackSession(feedbackSessionName, courseId);
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId), 
                feedback, false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);       
        data.session = feedback;
        data.questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        data.copiableQuestions = logic.getCopiableFeedbackQuestionsForInstructor(account.googleId);
        for(FeedbackQuestionAttributes question : data.questions) {            
            data.questionHasResponses.put(question.getId(),
                    logic.isQuestionHasResponses(question.getId()));
        }

        data.studentList = logic.getStudentsForCourse(courseId);
        Collections.sort(data.studentList, new StudentComparator());
        
        data.instructorList = logic.getInstructorsForCourse(courseId);
        Collections.sort(data.instructorList, new InstructorComparator());
        
        data.instructor = logic.getInstructorForGoogleId(courseId, loggedInUser.googleId);
        
        statusToAdmin = "instructorFeedbackEdit Page Load<br>"
                + "Editing information for Feedback Session <span class=\"bold\">["
                + feedbackSessionName + "]</span>" + "in Course: <span class=\"bold\">[" + courseId + "]</span>";
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_EDIT, data);
    }

    private class StudentComparator implements Comparator<StudentAttributes> {
        @Override
        public int compare(StudentAttributes s1, StudentAttributes s2) {
            if (s1.team.equals(s2.team)) {
                return s1.name.compareToIgnoreCase(s2.name);
            }
            return s1.team.compareToIgnoreCase(s2.team);
        }    
    }
    
    private class InstructorComparator implements Comparator<InstructorAttributes> {
        @Override
        public int compare(InstructorAttributes i1, InstructorAttributes i2) {
            return i1.name.compareToIgnoreCase(i2.name);
        }
    }
}
