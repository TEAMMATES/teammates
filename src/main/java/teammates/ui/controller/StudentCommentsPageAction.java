package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.SessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.pagedata.StudentCommentsPageData;

/**
 * Action: Showing the StudentCommentsPage for a student.
 */
public class StudentCommentsPageAction extends Action {

    private String courseId;
    private String studentEmail;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        //check accessibility without courseId
        verifyBasicAccessibility();

        //COURSE_ID can be null, if viewed by default
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        List<CourseAttributes> courses = logic.getCoursesForStudentAccount(account.googleId);

        Collections.sort(courses);

        if (courseId == null && !courses.isEmpty()) {
            // if courseId not provided, select the newest course
            courseId = courses.get(0).getId();
        }

        String courseName = getSelectedCourseName(courses);
        if (courseName.isEmpty()) {
            throw new EntityDoesNotExistException("Trying to access a course that does not exist.");
        }

        //check accessibility with courseId
        if (!isJoinedCourse(courseId)) {
            return createPleaseJoinCourseResponse(courseId);
        }
        verifyAccessible();

        List<String> coursePaginationList = getCoursePaginationList(courses);

        studentEmail = logic.getStudentForGoogleId(courseId, account.googleId).email;
        CourseRoster roster = null;
        Map<String, FeedbackSessionResultsBundle> feedbackResultBundles =
                new HashMap<String, FeedbackSessionResultsBundle>();
        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        if (!coursePaginationList.isEmpty()) {
            roster = new CourseRoster(
                    logic.getStudentsForCourse(courseId),
                    logic.getInstructorsForCourse(courseId));

            //Prepare comments data
            StudentAttributes student = roster.getStudentForEmail(studentEmail);
            comments = logic.getCommentsForStudent(student);
            feedbackResultBundles = getFeedbackResultBundles(roster);
        }

        StudentCommentsPageData data = new StudentCommentsPageData(account);
        data.init(courseId, courseName, coursePaginationList, comments, roster,
                  studentEmail, feedbackResultBundles);

        statusToAdmin = "studentComments Page Load<br>"
                + "Viewing <span class=\"bold\">" + account.googleId + "'s</span> comment records "
                + "for Course <span class=\"bold\">[" + courseId + "]</span>";

        return createShowPageResult(Const.ViewURIs.STUDENT_COMMENTS, data);
    }

    private void verifyBasicAccessibility() {
        gateKeeper.verifyLoggedInUserPrivileges();
        if (regkey != null) {
            // unregistered users cannot view the page
            throw new UnauthorizedAccessException("User is not registered");
        }
    }

    private void verifyAccessible() {
        gateKeeper.verifyAccessible(
                logic.getStudentForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
    }

    private List<String> getCoursePaginationList(List<CourseAttributes> sortedCourses) {
        List<String> coursePaginationList = new ArrayList<>();

        for (CourseAttributes course : sortedCourses) {
            coursePaginationList.add(course.getId());
        }

        return coursePaginationList;
    }

    private String getSelectedCourseName(List<CourseAttributes> sortedCourses) {
        for (CourseAttributes course : sortedCourses) {
            if (course.getId().equals(courseId)) {
                return course.getId() + " : " + course.getName();
            }
        }
        return "";
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
        for (FeedbackSessionAttributes fs : fsList) {
            if (!fs.isPublished()) {
                continue;
            }

            FeedbackSessionResultsBundle bundle =
                    logic.getFeedbackSessionResultsForStudent(
                                  fs.getFeedbackSessionName(), courseId, studentEmail, roster);
            removeQuestionsAndResponsesWithoutFeedbackResponseComment(bundle);
            if (bundle.questions.size() != 0) {
                feedbackResultBundles.put(fs.getFeedbackSessionName(), bundle);
            }
        }
        return feedbackResultBundles;
    }

    private void removeQuestionsAndResponsesWithoutFeedbackResponseComment(FeedbackSessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responsesWithFeedbackResponseComment =
                new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes fr : bundle.responses) {
            List<FeedbackResponseCommentAttributes> frComment = bundle.responseComments.get(fr.getId());
            if (frComment != null && !frComment.isEmpty()) {
                responsesWithFeedbackResponseComment.add(fr);
            }
        }
        Map<String, FeedbackQuestionAttributes> questionsWithFeedbackResponseComment =
                new HashMap<String, FeedbackQuestionAttributes>();
        for (FeedbackResponseAttributes fr : responsesWithFeedbackResponseComment) {
            FeedbackQuestionAttributes qn = bundle.questions.get(fr.feedbackQuestionId);
            if (questionsWithFeedbackResponseComment.get(qn.getId()) == null) {
                questionsWithFeedbackResponseComment.put(qn.getId(), qn);
            }
        }
        bundle.questions = questionsWithFeedbackResponseComment;
        bundle.responses = responsesWithFeedbackResponseComment;
    }
}
