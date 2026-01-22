package teammates.e2e.cases.sql;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPageSql;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Base class for all feedback question related browser tests.
 *
 * <p>SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}.
 *
 * <p>Only UI-intensive operations, e.g. question creation and response submission, are tested separately.
 * This is so that if any part of the testing fails (due to regression or inherent instability), only the
 * specific test for the specific feedback question needs to be re-run.
 *
 * <p>For the above reason, viewing feedback responses/results is not considered to be under this test case.
 * This is because viewing results is a fast action and combining all question types together under one test case
 * will save some testing time.
 */
public abstract class BaseFeedbackQuestionE2ETest extends BaseE2ETestCase {
    Instructor instructor;
    Course course;
    FeedbackSession feedbackSession;
    Student student;

    abstract void testEditPage();

    abstract void testSubmitPage();

    InstructorFeedbackEditPageSql loginToFeedbackEditPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getName());

        return loginToPage(url, InstructorFeedbackEditPageSql.class, instructor.getGoogleId());
    }

    FeedbackSubmitPageSql loginToFeedbackSubmitPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(student.getCourse().getId())
                .withSessionName(feedbackSession.getName());

        return loginToPage(url, FeedbackSubmitPageSql.class, student.getGoogleId());
    }

    FeedbackSubmitPageSql getFeedbackSubmitPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(student.getCourse().getId())
                .withSessionName(feedbackSession.getName());

        return getNewPageInstance(url, FeedbackSubmitPageSql.class);
    }
}
