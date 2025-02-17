package teammates.sqlui.webapi;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
import teammates.ui.webapi.CreateFeedbackSessionLogAction;

public class CreateFeedbackSessionLogActionTest extends BaseActionTest<CreateFeedbackSessionLogAction> {
    Course course1;
    Course course2;
    Course course3;
    String courseId1;
    FeedbackSession fsa1;
    FeedbackSession fsa2;
    Student student1;
    Student student2;
    Student student3;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeSuite
    protected void prepareData() {
        course1 = getTypicalCourse();
        course2 = getTypicalCourse();
        course3 = getTypicalCourse();
        course2.setId("course2");
        course3.setId("course3");
        courseId1 = course1.getId();
        fsa1 = getTypicalFeedbackSessionForCourse(course1);
        fsa2 = getTypicalFeedbackSessionForCourse(course2);
        student1 = getTypicalStudent();
        student2 = getTypicalStudent();
        student3 = getTypicalStudent();
        student1.setCourse(course1);
        student2.setCourse(course1);
        student3.setCourse(course3);
    }

    @Test
    void testExecute_notEnoughParameters_shouldFail() throws Exception {
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, courseId1);
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName()
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, FeedbackSessionLogType.SUBMISSION.getLabel(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail()
        );
    }

    @Test
    void testExecute_invalidLogType_shouldFail() throws Exception {
        String[] paramsInvalid = {
                Const.ParamsNames.COURSE_ID, courseId1,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa1.getName(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_TYPE, "invalid log type",
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        verifyHttpParameterFailure(paramsInvalid);
    }


}
