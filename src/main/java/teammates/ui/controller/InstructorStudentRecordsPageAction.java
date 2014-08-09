package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentRecordsPageAction extends Action {
    
    private InstructorStudentRecordsPageData data;
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL); 
        Assumption.assertNotNull(studentEmail);
        
        String showCommentBox = getRequestParamValue(Const.ParamsNames.SHOW_COMMENT_BOX);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        
        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId));
        
        data = new InstructorStudentRecordsPageData(account);
        
        try {
            data.currentInstructor = instructor;
            data.courseId = courseId;
            data.student = logic.getStudentForEmail(courseId, studentEmail);
            
            if (data.student == null) {
                statusToUser.add(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_RECORDS);
                isError = true;
                return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
            }
            
            data.showCommentBox = showCommentBox;
            data.comments = logic.getCommentsForReceiver(courseId, instructor.email, CommentRecipientType.PERSON, studentEmail);
            Iterator<CommentAttributes> iterator = data.comments.iterator();
            while(iterator.hasNext()){
                CommentAttributes c = iterator.next();
                if(!c.giverEmail.equals(instructor.email)){
                    iterator.remove();
                }
            }
            List<EvaluationAttributes> evals = logic.getEvaluationsListForInstructor(account.googleId);
            List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsListForInstructor(account.googleId);
            
            //Remove evaluations and feedbacks not from the courseId parameters
            //Can be removed later when we want to have unified view
            for(int i = evals.size() - 1; i >= 0; i--){
                if(!evals.get(i).courseId.equals(courseId)){
                    evals.remove(i);
                } else if (!data.currentInstructor.isAllowedForPrivilege(data.student.section, 
                        evals.get(i).getSessionName(), Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {
                    evals.remove(i);
                }
            }
            
            for(int i = feedbacks.size() - 1; i >= 0; i--){
                if(!feedbacks.get(i).courseId.equals(courseId)){
                    feedbacks.remove(i);
                } else if (!data.currentInstructor.isAllowedForPrivilege(data.student.section, 
                        feedbacks.get(i).feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {
                    feedbacks.remove(i);
                }
            }
            
            data.sessions = new ArrayList<SessionAttributes>();
            data.sessions.addAll(evals);
            data.sessions.addAll(feedbacks);
            Collections.sort(data.sessions, SessionAttributes.DESCENDING_ORDER);
            CommentAttributes.sortCommentsByCreationTimeDescending(data.comments);

            data.results = new ArrayList<SessionResultsBundle>();
            for(SessionAttributes session : data.sessions){
                if(session instanceof EvaluationAttributes){
                    data.results.add(logic.getEvaluationResultForStudent(
                            courseId, session.getSessionName(), studentEmail));
                } else if(session instanceof FeedbackSessionAttributes){
                    data.results.add(logic.getFeedbackSessionResultsForInstructor(
                                    session.getSessionName(), courseId,instructor.email));
                } else {
                    Assumption.fail("Unknown session type");
                }
            }
            
            if (data.student.googleId == "") {
                statusToUser.add(Const.StatusMessages.STUDENT_NOT_JOINED_YET_FOR_RECORDS);
            } else if (!data.currentInstructor.isAllowedForPrivilege(data.student.section, 
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_UNACCESSIBLE_TO_INSTRUCTOR);
            } else {
                data.studentProfile = logic.getStudentProfile(data.student.googleId);
                Assumption.assertNotNull(data.studentProfile);
            }
            
            // call this function right before the 'no-records' check
            loadStudentProfile();
            
            if(data.sessions.size() == 0 
                    && data.comments.size() == 0){
                statusToUser.add(Const.StatusMessages.INSTRUCTOR_NO_STUDENT_RECORDS);
            }
            
            statusToAdmin = "instructorStudentRecords Page Load<br>" + 
                    "Viewing <span class=\"bold\">" + studentEmail + "'s</span> records " +
                    "for Course <span class=\"bold\">[" + courseId + "]</span><br>" +
                    "Number of sessions: " + data.sessions.size() + "<br>" +
                    "Student Profile: " + 
                    (data.studentProfile == null ? 
                            "No Profile" : 
                                data.studentProfile.toString());
            
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS, data);
            
        } catch (InvalidParametersException e) {
            // TODO: write test to trigger this path
            setStatusForException(e); 
            return createShowPageResult(Const.ViewURIs.STATUS_MESSAGE, data);
        }
    }
    
    private void loadStudentProfile() {
        boolean hasExistingStatus = !statusToUser.isEmpty() || 
                session.getAttribute(Const.ParamsNames.STATUS_MESSAGE) != null;
        
        if (data.student.googleId.isEmpty()) {
            if (!hasExistingStatus) {
                statusToUser.add(Const.StatusMessages.STUDENT_NOT_JOINED_YET_FOR_RECORDS);
            }
        } else if(!data.currentInstructor.isAllowedForPrivilege(data.student.section, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {
            if (!hasExistingStatus) {
                statusToUser.add(Const.StatusMessages.STUDENT_PROFILE_UNACCESSIBLE_TO_INSTRUCTOR);
            }
        } else {
            data.studentProfile = logic.getStudentProfile(data.student.googleId);
            Assumption.assertNotNull(data.studentProfile);
        }
    }
}
