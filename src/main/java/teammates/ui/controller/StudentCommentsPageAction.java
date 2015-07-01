package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: Showing the StudentCommentsPage for a student
 */
public class StudentCommentsPageAction extends Action {
    
    private StudentCommentsPageData data;
    private String courseId;
    private String studentEmail;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        //check accessibility without courseId
        verifyBasicAccessibility();
        
        //COURSE_ID can be null, if viewed by default
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (courseId == null) {
            courseId = "";
        }
        
        List<String> coursePaginationList = new ArrayList<String>(); 
        String courseName = getCoursePaginationList(coursePaginationList);
        
        //check accessibility with courseId
        if (!isJoinedCourse(courseId, account.googleId)) {
            return createPleaseJoinCourseResponse(courseId);
        }
        verifyAccessible();
        
        studentEmail = logic.getStudentForGoogleId(courseId, account.googleId).email;
        CourseRoster roster = null;
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = 
                new HashMap<String, FeedbackSessionResultsBundle>();
        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        if (coursePaginationList.size() > 0) {
            roster = new CourseRoster(
                    logic.getStudentsForCourse(courseId),
                    logic.getInstructorsForCourse(courseId));

            //Prepare comments data
            StudentAttributes student = roster.getStudentForEmail(studentEmail);
            comments = logic.getCommentsForStudent(student);
            feedbackResultBundles = getFeedbackResultBundles(roster);
        }
        
        data = new StudentCommentsPageData(account);
        data.init(courseId, courseName, coursePaginationList, comments, roster,
                  studentEmail, feedbackResultBundles);
        
        statusToAdmin = "studentComments Page Load<br>" + 
                "Viewing <span class=\"bold\">" + account.googleId + "'s</span> comment records " +
                "for Course <span class=\"bold\">[" + courseId + "]</span>";

        return createShowPageResult(Const.ViewURIs.STUDENT_COMMENTS, data);
    }
    
    private void verifyBasicAccessibility() {
        new GateKeeper().verifyLoggedInUserPrivileges();
        if (regkey != null) { 
            // unregistered users cannot view the page
            throw new UnauthorizedAccessException("User is not registered");
        }
    }
    
    private void verifyAccessible() {
        new GateKeeper().verifyAccessible(
                logic.getStudentForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
    }

    private String getCoursePaginationList(List<String> coursePaginationList) 
            throws EntityDoesNotExistException {
        String courseName = "";
        List<CourseAttributes> courses = logic.getCoursesForStudentAccount(account.googleId);
        java.util.Collections.sort(courses);
        for (int i = 0; i < courses.size(); i++) {
            CourseAttributes course = courses.get(i);
            coursePaginationList.add(course.id);
            if (courseId.isEmpty()) {
                //if courseId not provided, select the newest course
                courseId = course.id;
            }
            if (course.id.equals(courseId)) {
                courseName = course.id + " : " + course.name;
            }
        }
        if (courseName.equals("")) {
            throw new EntityDoesNotExistException("Trying to access a course that does not exist.");
        }
        return courseName;
    }
    
    /*
     * Returns a sorted map(LinkedHashMap) of FeedbackSessionResultsBundle,
     * where the sessions are sorted in descending order of their endTime
     */
    private Map<String, FeedbackSessionResultsBundle> getFeedbackResultBundles(CourseRoster roster)
            throws EntityDoesNotExistException {
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles = 
                new LinkedHashMap<String, FeedbackSessionResultsBundle>();
        List<FeedbackSessionAttributes> fsList = logic.getFeedbackSessionsForCourse(courseId);
        Collections.sort(fsList, SessionAttributes.DESCENDING_ORDER);
        for(FeedbackSessionAttributes fs : fsList) {
            if (!fs.isPublished()) {
                continue;
            }
            
            FeedbackSessionResultsBundle bundle = 
                    logic.getFeedbackSessionResultsForStudent(
                                  fs.feedbackSessionName, courseId, studentEmail, roster);
            if (bundle != null) {
                removeQuestionsAndResponsesWithoutFeedbackResponseComment(bundle);
                if (bundle.questions.size() != 0) {
                    feedbackResultBundles.put(fs.feedbackSessionName, bundle);
                }
            }
        }
        return feedbackResultBundles;
    }

    private void removeQuestionsAndResponsesWithoutFeedbackResponseComment(FeedbackSessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responsesWithFeedbackResponseComment = 
                new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes fr: bundle.responses) {
            List<FeedbackResponseCommentAttributes> frComment = bundle.responseComments.get(fr.getId());
            if (frComment != null && frComment.size() != 0) {
                responsesWithFeedbackResponseComment.add(fr);
            }
        }
        Map<String, FeedbackQuestionAttributes> questionsWithFeedbackResponseComment = 
                new HashMap<String, FeedbackQuestionAttributes>();
        for (FeedbackResponseAttributes fr: responsesWithFeedbackResponseComment) {
            FeedbackQuestionAttributes qn = bundle.questions.get(fr.feedbackQuestionId);
            if (questionsWithFeedbackResponseComment.get(qn.getId()) == null) {
                questionsWithFeedbackResponseComment.put(qn.getId(), qn);
            }
        }
        bundle.questions = questionsWithFeedbackResponseComment;
        bundle.responses = responsesWithFeedbackResponseComment;
    }
}
