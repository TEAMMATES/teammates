package teammates.ui.webapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.ActionClasses;

/**
 * SUT: {@link GetActionClassesAction}.
 */
public class GetActionClassesActionTest extends BaseActionTest<GetActionClassesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACTION_CLASS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        ______TS("Retrieve the list of action class names");
        List<String> expectedActionClasses = Arrays.asList(
                "DeleteFeedbackResponseCommentAction",
                "CreateFeedbackResponseCommentAction",
                "GetFeedbackResponseCommentAction",
                "UpdateFeedbackResponseCommentAction",
                "RestoreFeedbackSessionAction",
                "BinFeedbackSessionAction",
                "GetCoursesAction",
                "GetRegkeyValidityAction",
                "PutDataBundleAction",
                "DeleteDataBundleAction",
                "CreateFeedbackSessionLogAction",
                "GetFeedbackSessionLogsAction",
                "GetInstructorPrivilegeAction",
                "UpdateInstructorPrivilegeAction",
                "GetFeedbackSessionsAction",
                "GenerateEmailAction",
                "GetFeedbackQuestionsAction",
                "GetOngoingSessionsAction",
                "GetStudentProfileAction",
                "UpdateStudentProfileAction",
                "GetNationalitiesAction",
                "AdminExceptionTestAction",
                "RemindFeedbackSessionResultAction",
                "DeleteInstructorAction",
                "CreateInstructorAction",
                "GetInstructorAction",
                "UpdateInstructorAction",
                "ArchiveCourseAction",
                "InstructorCourseJoinEmailWorkerAction",
                "DeleteStudentAction",
                "GetStudentAction",
                "UpdateStudentAction",
                "SearchStudentsAction",
                "FeedbackSessionRemindEmailWorkerAction",
                "DeleteStudentProfilePictureAction",
                "PostStudentProfilePictureAction",
                "GetStudentProfilePictureAction",
                "DeleteFeedbackSessionAction",
                "CreateFeedbackSessionAction",
                "GetFeedbackSessionAction",
                "UpdateFeedbackSessionAction",
                "FeedbackSessionClosingRemindersAction",
                "GetLocalDateTimeInfoAction",
                "GetTimeZonesAction",
                "FeedbackSessionRemindParticularUsersEmailWorkerAction",
                "GetFeedbackResponsesAction",
                "SubmitFeedbackResponsesAction",
                "FeedbackSessionPublishedEmailWorkerAction",
                "FeedbackSessionClosedRemindersAction",
                "SendErrorReportAction",
                "GetActionClassesAction",
                "UnpublishFeedbackSessionAction",
                "PublishFeedbackSessionAction",
                "GetSessionResultsAction",
                "GetHasResponsesAction",
                "DatastoreBackupAction",
                "RestoreCourseAction",
                "BinCourseAction",
                "DeleteAccountAction",
                "CreateAccountAction",
                "GetAccountAction",
                "FeedbackSessionPublishedRemindersAction",
                "QueryLogsAction",
                "SessionLinksRecoveryAction",
                "SendJoinReminderEmailAction",
                "RegenerateStudentCourseLinksAction",
                "CompileLogsAction",
                "GetAuthInfoAction",
                "GetFeedbackSessionSubmittedGiverSetAction",
                "GetCourseJoinStatusAction",
                "JoinCourseAction",
                "GetSessionResponseStatsAction",
                "DeleteCourseAction",
                "CreateCourseAction",
                "GetCourseAction",
                "UpdateCourseAction",
                "GetFeedbackQuestionRecipientsAction",
                "DowngradeAccountAction",
                "RemindFeedbackSessionSubmissionAction",
                "FeedbackSessionUnpublishedEmailWorkerAction",
                "SendEmailWorkerAction",
                "GetInstructorsAction",
                "PutDataBundleDocumentsAction",
                "FeedbackSessionResendPublishedEmailWorkerAction",
                "StudentCourseJoinEmailWorkerAction",
                "SearchInstructorsAction",
                "GetCourseSectionNamesAction",
                "ResetAccountAction",
                "FeedbackSessionOpeningRemindersAction",
                "DeleteStudentsAction",
                "GetStudentsAction",
                "EnrollStudentsAction",
                "DeleteFeedbackQuestionAction",
                "CreateFeedbackQuestionAction",
                "UpdateFeedbackQuestionAction"
        );
        Collections.sort(expectedActionClasses);

        GetActionClassesAction action = getAction();
        action.execute();
        JsonResult result = getJsonResult(action);
        ActionClasses data = (ActionClasses) result.getOutput();
        List<String> actualActionClasses = data.getActionClasses();
        Collections.sort(actualActionClasses);

        assertEquals(expectedActionClasses, actualActionClasses);
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
