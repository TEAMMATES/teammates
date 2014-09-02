package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;

import com.google.appengine.api.datastore.Text;

public class AllActionsAccessControlTest extends BaseActionTest {
    
    private String[] submissionParams = new String[]{};
    private static final DataBundle dataBundle = getTypicalDataBundle();
    String invalidEncryptedKey = StringHelper.encrypt("invalidKey");
    
    private final CommentsDb commentsDb = new CommentsDb();
    private final EvaluationsDb evaluationsDb = new EvaluationsDb();
    private final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    private final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
		addUnregStudentToCourse1();
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        StudentsLogic.inst().deleteStudentCascade("idOfTypicalCourse1", "student6InCourse1@gmail.tmt");
    }
    
    private static void addUnregStudentToCourse1() throws Exception{
        StudentsLogic.inst().deleteStudentCascade("idOfTypicalCourse1", "student6InCourse1@gmail.tmt");
        StudentAttributes student = new StudentAttributes();
        student.email = "student6InCourse1@gmail.tmt";
        student.name = "unregistered student6 In Course1";
        student.team = "Team Unregistered";
        student.section = "Section 3";
        student.course = "idOfTypicalCourse1";
        student.comments = "";
        StudentsLogic.inst().createStudentCascade(student);
    }

    @Test
    public void InstructorCoursesPage() throws Exception{
        /*Explanation: We change the uri variable to specify which page we want 
         * test access control for. The URIs can be found in Const.ActionURIs
         */
        uri = Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
        /* Explanation: In this case, we use an empty array because this action does not 
         * require any parameters. When the action does need parameters, we
         * can put them in this array as pairs of strings (parameter name, value).
         * e.g., new String[]{Const.ParamsNames.COURSE_ID, "course101"}
         */
        String[] submissionParams = new String[]{};
        
        /* Explanation: Here, we use one of the access control test methods available in the
         * parent class. 
         */
        verifyOnlyInstructorsCanAccess(submissionParams);
        
    }
    
    @Test
    public void AdminAccountDelete() throws Exception {
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminAccountDetailsPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminAccountManagementPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminActivityLogPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminExceptionTest() throws Exception{
        uri = Const.ActionURIs.ADMIN_EXCEPTION_TEST;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminHomePage() throws Exception{
        uri = Const.ActionURIs.ADMIN_HOME_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminInstructorAccountAdd() throws Exception{
        uri = Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminSearchPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_SEARCH_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorEvaluationStatsPage() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_STATS_PAGE;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        EvaluationAttributes accessableEvaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        String[] submissionParams = new String[] { Const.ParamsNames.EVALUATION_NAME, accessableEvaluation.name,
                                          Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId};
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackStatsPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes accessableFeedbackSession = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] { Const.ParamsNames.FEEDBACK_SESSION_NAME, accessableFeedbackSession.feedbackSessionName,
                                            Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId};
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
    public void InstructorCommentsPage() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE;
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseAdd() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ADD;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, "ticac.tac.id",
                Const.ParamsNames.COURSE_NAME, "ticac tac name"};
        
        verifyOnlyInstructorsCanAccess(submissionParams);
        
        // remove course that was created
        CoursesLogic.inst().deleteCourseCascade("ticac.tac.id");
    }
    
    @Test
    public void InstructorCourseArchive() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true"
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseDelete() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_DELETE;
        CoursesLogic.inst().createCourseAndInstructor(
                dataBundle.instructors.get("instructor1OfCourse1").googleId, 
                "icdat.owncourse", "New course");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, "icdat.owncourse"
        };

        /*  Test access for users 
         *  This should be separated from testing for admin as we need to recreate the course after being removed         
         */
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifyCoursePrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);

        /* Test access for admin in masquerade mode */
        CoursesLogic.inst().createCourseAndInstructor(
                dataBundle.instructors.get("instructor1OfCourse1").googleId, 
                "icdat.owncourse", "New course");
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    @Test
    public void InstructorCourseDetailsPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseEdit() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseEnrollPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorCourseEnrollSave() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_SAVE;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, ""
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorCourseInstructorAdd() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_ADD;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
                Const.ParamsNames.INSTRUCTOR_NAME, "Instructor Name",
                Const.ParamsNames.INSTRUCTOR_EMAIL, "instructor@email.tmt",
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, "true"
                };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyInstructorPrivilege(submissionParams);
        
        // remove the newly added instructor
        InstructorsLogic.inst().deleteInstructorCascade("idOfTypicalCourse1", "instructor@email.tmt");
    }
    
    @Test
    public void InstructorCourseInstructorDelete() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
        InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.email
        };
        
        verifyUnaccessibleWithoutModifyInstructorPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseInstructorEditSave() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE;
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructor.googleId,
                Const.ParamsNames.INSTRUCTOR_NAME, instructor.name,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.email,
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, "true",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, "true"
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyInstructorPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorCourseJoin() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };
        
        verifyOnlyLoggedInUsersCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseJoinAuthenticated() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED;
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };
        
        verifyOnlyLoggedInUsersCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseRemind() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseStudentDelete() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student5InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email 
        };
        
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorCourseStudentDetailsEdit() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorCourseStudentDetailsEditSave() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student3InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorCourseStudentDetailsPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutViewStudentInSectionsPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorCourseStudentListDownload() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD;
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, course.id
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorEvaluationAdd() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_ADD;
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        
        String[] submissionParams = 
                createParamsForTypicalEval(instructor1ofCourse1.courseId, "ieaat tca eval");
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        
        // delete the evaluation
        evaluationsDb.deleteEvaluation(instructor1ofCourse1.courseId, "ieaat tca eval");
    }
    
    @Test
    public void InstructorEvaluationDelete() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_DELETE;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation2InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
        };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        
        //recreate the evaluation
        evaluationsDb.createEntity(evaluationInCourse1);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
        
    }
    
    @Test
    public void InstructorEvaluationEditPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_PAGE;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorEvaluationEditSave() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_SAVE;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        
        String[] submissionParams = createParamsForTypicalEval(
                evaluationInCourse1.courseId, evaluationInCourse1.name);
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorEvaluationPreview() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_PREVIEW;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
    }
    
    @Test
    public void InstructorEvaluationPublish() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_PUBLISH;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        
        makeEvaluationClosed(evaluationInCourse1);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
        };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        
        makeEvaluationClosed(evaluationInCourse1); //we have to revert to the closed state
        
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
        
    }
    
    @Test
    public void InstructorEvaluationRemind() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_REMIND;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
    }
    
    @Test
    public void InstructorEvaluationResultsDownload() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_DOWNLOAD;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
    }
    
    @Test
    public void InstructorEvaluationResultsPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
    }

    @Test
    public void InstructorEvaluationPage() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_EVALS_PAGE;
        verifyOnlyInstructorsCanAccess(submissionParams);
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse2").googleId);
        verifyRedirectTo(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE, submissionParams);
        gaeSimulation.logoutUser();
    }
    
    @Test
    public void InstructorEvaluationSubmissionEdit() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT;
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
                
        String[] submissionParams = {
            Const.ParamsNames.COURSE_ID, eval.courseId,
            Const.ParamsNames.EVALUATION_NAME, eval.name,
            Const.ParamsNames.STUDENT_EMAIL, student.email
            };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionInSectionsPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorEvaluationSubmissionEditSave() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SAVE;
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
                
        String[] submissionParams = {
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name,
                Const.ParamsNames.FROM_EMAIL, student.email
            };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifySessionInSectionsPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorEvaluationSubmissionPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_PAGE;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutViewSessionInSectionsPrivilege(submissionParams);
    }
    
    @Test
    public void InstructorEvaluationUnpublish() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_UNPUBLISH;
        EvaluationAttributes evaluationInCourse1 = dataBundle.evaluations.get("evaluation1InCourse1");
        makeEvaluationPublished(evaluationInCourse1);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, evaluationInCourse1.courseId,
                Const.ParamsNames.EVALUATION_NAME, evaluationInCourse1.name 
        };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        
        makeEvaluationPublished(evaluationInCourse1); //we have revert to the PUBLISHED state
        
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
        
        makeEvaluationClosed(evaluationInCourse1);
        
    }
    
    @Test
    public void InstructorFeedbackAdd() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD;
        InstructorAttributes instructor1ofCourse1 = 
                dataBundle.instructors.get("instructor1OfCourse1");
        
        String[] params = 
                createParamsForTypicalFeedbackSession(
                        instructor1ofCourse1.courseId, "ifaat tca fs");
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyCoursePrivilege(params);
        
        // delete the sessions
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade("ifaat tca fs", instructor1ofCourse1.courseId);
    }
    
    @Test
    
    public void InstructorFeedbackDelete() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_DELETE;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session2InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
        };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        
        //recreate the entity
        FeedbackSessionsLogic.inst().createFeedbackSession(fs);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);        
    }
    
    @Test
    public void InstructorFeedbackEditPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackEditSave() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams =
                createParamsForTypicalFeedbackSession(fs.courseId, fs.feedbackSessionName);
        
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackPreviewAsInstructor() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR;
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, instructor.email
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackPreviewAsStudent() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT;
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.PREVIEWAS, student.email
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackPublish() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PUBLISH;
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        
        makeFeedbackSessionUnpublished(session); //we have to revert to the closed state
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName 
        };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        
        makeFeedbackSessionUnpublished(session); //we have to revert to the closed state
        
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackQuestionAdd() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD;
        FeedbackSessionAttributes fs = 
                dataBundle.feedbackSessions.get("empty.session");
        
        String[] submissionParams = 
                createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        // set question number to be the last
        submissionParams[9] = "5";
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
        // remove the session as removing questions is difficult
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
   }
    
    @Test
    public void InstructorFeedbackQuestionEdit() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes fq = 
                FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 4);
        
        String[] submissionParams = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        submissionParams[9] = "4";
        
        submissionParams = addQuestionIdToParams(fq.getId(), submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams); 
    }

    @Test
    public void InstructorFeedbackQuestionSubmissionEdit() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        FeedbackQuestionAttributes q = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 3);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, q.getId()
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);        
    }

    @Test
    public void InstructorFeedbackQuestionSubmissionEditSave() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        FeedbackQuestionAttributes q = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 3);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, q.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "0"
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);        
    }
    
    @Test
    public void InstructorFeedbackRemind() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND;
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
    public void InstructorFeedbackResponseCommentAdd() throws Exception {
         uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD;
        
        int questionNumber = 1;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes question = fqDb.getFeedbackQuestion(
                fs.feedbackSessionName, fs.courseId, questionNumber);
        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes response = frDb.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);
        FeedbackResponseCommentAttributes comment = new FeedbackResponseCommentAttributes();
        comment.courseId = fs.courseId;
        comment.feedbackSessionName = fs.feedbackSessionName;
        comment.feedbackQuestionId = question.getId();
        comment.feedbackResponseId = response.getId();
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, comment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, comment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, comment.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment.feedbackResponseId
        };
        
        verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
        
        // remove the comment
        frcDb.deleteEntity(comment);
    }

    @Test
    public void InstructorFeedbackResponseCommentDelete() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE;
        
        int questionNumber = 2;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get("response1ForQ2S1C1");
        
        FeedbackQuestionAttributes question = fqDb.getFeedbackQuestion(
                fs.feedbackSessionName, fs.courseId, questionNumber);
        response = frDb.getFeedbackResponse(question.getId(), response.giverEmail, response.recipientEmail);
        comment = frcDb.getFeedbackResponseComment(response.getId(), comment.giverEmail, comment.createdAt);
        comment.feedbackResponseId = response.getId();

        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId())
        };
        verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackResponseCommentEdit() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = fqDb.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);
        
        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes feedbackResponse = frDb.getFeedbackResponse(feedbackQuestion.getId(),
                giverEmail, receiverEmail);
        
        FeedbackResponseCommentAttributes feedbackResponseComment = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");
        
        feedbackResponseComment = frcDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponse.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "comment",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };
        // this person is not the giver. so not accessible
        verifyUnaccessibleWithoutModifySessionCommentInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackResultsDownload() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD;
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackResultsPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbacksPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, 
                dataBundle.instructors.get("instructor1OfCourse1").courseId
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackSubmissionEditPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackSubmissionEditSave() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE;
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        testGracePeriodAccessControlForInstructors();
    }
    
    private void testGracePeriodAccessControlForInstructors() throws Exception{
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("gracePeriodSession");
        
        closeSession(fs);
        
        assertFalse(fs.isOpened());
        assertTrue(fs.isInGracePeriod());
        assertFalse(fs.isClosed());
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void InstructorFeedbackUnpublish() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH;
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        
        makeFeedbackSessionPublished(session); //we have to revert to the closed state
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName 
        };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        
        makeFeedbackSessionPublished(session); //we have to revert to the closed state
        
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
        
        makeFeedbackSessionUnpublished(session); //we have to revert to the closed state
    }
    
    @Test
    public void InstructorHomePage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        verifyOnlyInstructorsCanAccess(submissionParams);
    }

    @Test
    public void InstructorStudentCommentAdd() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD;
        CommentAttributes comment = dataBundle.comments.get("comment1FromI1C1toS1C1");
        comment.commentText = new Text("New Comment");
        String recipient = dataBundle.students.get("student4InCourse1").email;
        comment.recipients.clear();
        comment.recipients.add(recipient);
        String[] submissionParams = new String[]{
                Const.ParamsNames.COMMENT_TEXT, comment.commentText.getValue(),
                Const.ParamsNames.COURSE_ID, comment.courseId,
                Const.ParamsNames.STUDENT_EMAIL, recipient,
                Const.ParamsNames.RECIPIENT_TYPE, comment.recipientType.toString(),
                Const.ParamsNames.RECIPIENTS, recipient
        };
        verifyUnaccessibleWithoutGiveCommentInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsCanAccess(submissionParams);
        
        List<CommentAttributes> list = 
                commentsDb.getCommentsForReceiver(comment.courseId, comment.recipientType, recipient);
        for (CommentAttributes c : list) {
            commentsDb.deleteEntity(c);
        }
    }
    
    @Test
    public void InstructorStudentCommentEdit() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT;
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        List<CommentAttributes> comments = CommentsLogic.inst().getCommentsForReceiver(instructor.courseId, CommentRecipientType.PERSON, student.email);
        Iterator<CommentAttributes> iterator = comments.iterator();
        while(iterator.hasNext()){
            CommentAttributes commentAttributes = iterator.next();
            if(!commentAttributes.giverEmail.equals(instructor.email)){
                iterator.remove();
            }
        }
        assertEquals(2, comments.size());
        CommentAttributes comment = comments.get(0);
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.COMMENT_ID,comment.getCommentId().toString(),
                Const.ParamsNames.COMMENT_EDITTYPE, "edit",
                Const.ParamsNames.COMMENT_TEXT, "Comment from Instructor 1 to Student 1 in course 1",
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };
        verifyUnaccessibleWithoutModifyCommentInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsCanAccess(submissionParams);
        
        // restore the comment Txt
        commentsDb.updateComment(comment);
    }

    @Test
    public void InstructorStudentListPage() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
        verifyOnlyInstructorsCanAccess(submissionParams);
    }

    @Test
    public void InstructorStudentRecordsPage() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
    public void StudentCommentsPage() throws Exception {
        uri = Const.ActionURIs.STUDENT_COMMENTS_PAGE;
        verifyAnyRegisteredUserCanAccess(submissionParams);
    }

    @Test
    public void StudentCourseDetailsPage() throws Exception {
        uri = Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
        String idOfCourseOfStudent = dataBundle.students
                .get("student1InCourse1").course;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, idOfCourseOfStudent
        };

        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
        verifyUnaccessibleWithoutLogin(submissionParams);

        idOfCourseOfStudent = dataBundle.students.get("student2InCourse1").course;
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, idOfCourseOfStudent
        };

        verifyUnaccessibleForStudentsOfOtherCourses(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
    }

    @Test
    public void StudentCourseJoinLegacyLink() throws Exception {
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN;
        StudentAttributes unregStudent1 = dataBundle.students.get("student2InUnregisteredCourse");
        String key = StudentsLogic.inst().getStudentForEmail(unregStudent1.course, unregStudent1.email).key;
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(key)
        };
        
        verifyAccessibleWithoutLogin(submissionParams);
        verifyAccessibleForUnregisteredUsers(submissionParams);
        verifyAccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }

    @Test
    public void StudentCourseJoin() throws Exception {
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN_NEW;
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, dataBundle.courses.get("typicalCourse1").id
        };
        verifyAccessibleWithoutLogin(submissionParams);
        
        StudentAttributes unregStudent1 = dataBundle.students.get("student1InUnregisteredCourse");
        String key = StudentsLogic.inst().getStudentForEmail(unregStudent1.course, unregStudent1.email).key;
        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(key),
                Const.ParamsNames.COURSE_ID, unregStudent1.course,
                Const.ParamsNames.STUDENT_EMAIL, unregStudent1.email
        };
        verifyAccessibleForUnregisteredUsers(submissionParams);
        verifyAccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }

    @Test
    public void StudentCourseJoinAuthenticated() throws Exception {
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED;
        StudentAttributes unregStudent1 = dataBundle.students.get("student1InUnregisteredCourse");
        String key = StudentsLogic.inst().getStudentForEmail(unregStudent1.course, unregStudent1.email).key;
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(key),
                Const.ParamsNames.NEXT_URL, "randomUrl"
        };

        verifyUnaccessibleWithoutLogin(submissionParams);
        
        unregStudent1.googleId = "";
        StudentsLogic.inst().updateStudentCascade(unregStudent1.email, unregStudent1);
        verifyAccessibleForUnregisteredUsers(submissionParams);
        
        unregStudent1.googleId = "";
        StudentsLogic.inst().updateStudentCascade(unregStudent1.email, unregStudent1);
        verifyAccessibleForStudents(submissionParams);
        
        unregStudent1.googleId = "";
        StudentsLogic.inst().updateStudentCascade(unregStudent1.email, unregStudent1);
        verifyAccessibleForInstructorsOfOtherCourses(submissionParams);
        
        unregStudent1.googleId = "";
        StudentsLogic.inst().updateStudentCascade(unregStudent1.email, unregStudent1);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    @Test
    public void StudentEvaluationResultsPage() throws Exception{
        uri = Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE;
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.published = true;
        assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String studentId = student1InCourse1.googleId;
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name
                };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        
        ______TS("Student not part of course, redirect to home page.");
        gaeSimulation.loginUser("student1InCourse2");
        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
        verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
        
        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);

        gaeSimulation.loginAsInstructor(instructorId);
        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
        verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
        
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);    
        
        eval.published = false;
    }
    
    @Test
    public void StudentEvaluationSubmissionEditPage() throws Exception{
        uri = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE;
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        makeEvaluationOpen(eval);
        ______TS("OPEN evaluation");
        
        assertEquals(EvalStatus.OPEN, eval.getStatus());
        checkAccessControlForEval(eval, true);
        
        ______TS("CLOSED evaluation");
        
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        assertEquals(EvalStatus.CLOSED, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        checkAccessControlForEval(eval, true);
        
        ______TS("PUBLISHED evaluation");
        
        eval.published = true;
        assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        checkAccessControlForEval(eval, true);
        
        ______TS("AWAITING evaluation");
        
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        eval.setDerivedAttributes();
        assertEquals(EvalStatus.AWAITING, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        //We allow accessing it in AWAITING state because it is hard for students to do if 
        //  they don't know the evaluation name. In any case there's no harm if they did it.
        checkAccessControlForEval(eval, true);
        
    }
    
    @Test
    public void StudentEvaluationSubmissionEditSave() throws Exception{
        uri = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_SAVE;
        EvaluationAttributes eval = dataBundle.evaluations.get("evaluation1InCourse1");
        makeEvaluationOpen(eval);
        assertEquals(EvalStatus.OPEN, eval.getStatus());
        SubmissionAttributes sub = dataBundle.submissions.get("submissionFromS1C1ToS3C1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, eval.courseId,
                Const.ParamsNames.EVALUATION_NAME, eval.name,
                Const.ParamsNames.FROM_EMAIL, dataBundle.students.get("student1InCourse1").email,
                Const.ParamsNames.TEAM_NAME, sub.team,
                Const.ParamsNames.TO_EMAIL, sub.reviewee,
                Const.ParamsNames.POINTS, sub.points+"",
                Const.ParamsNames.JUSTIFICATION, sub.justification.toString(),
                Const.ParamsNames.COMMENTS, sub.p2pFeedback.toString()
            };
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForDifferentStudentOfTheSameCourses(submissionParams);
        verifyUnaccessibleForInstructors(submissionParams);
        
        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
    }

    @Test
    public void StudentFeedbackQuestionSubmissionEditPage() throws Exception {
        uri = Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE;
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions
                .get("session1InCourse1");

        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb
                .getFeedbackQuestion(
                        session1InCourse1.feedbackSessionName,
                        session1InCourse1.courseId, 1);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID,
                feedbackQuestion.getId()
        };

        verifyOnlyStudentsOfTheSameCourseCanAccess(submissionParams);

        // below: trying to access questions not meant for the user

        feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                session1InCourse1.feedbackSessionName,
                session1InCourse1.courseId, 3);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID,
                feedbackQuestion.getId()
        };

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void StudentFeedbackQuestionSubmissionEditSave() throws Exception {
        uri = Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE;
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions
                .get("session1InCourse1");

        FeedbackQuestionAttributes feedbackQuestion = fqDb
                .getFeedbackQuestion(session1InCourse1.feedbackSessionName,
                        session1InCourse1.courseId, 1);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID,
                feedbackQuestion.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "0"
        };

        verifyOnlyStudentsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
    public void StudentFeedbackResultsPage() throws Exception {
        uri = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions
                .get("session1InCourse1");
        FeedbackSessionsLogic.inst().publishFeedbackSession(
                session1InCourse1.getSessionName(), session1InCourse1.courseId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName
        };

        verifyOnlyStudentsOfTheSameCourseCanAccess(submissionParams);

        // TODO: test no questions -> redirect after moving detection logic to
        // proper access control level.
    }

    @Test
    public void StudentFeedbackSubmissionEditPage() throws Exception {
        uri = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions
                .get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName
        };

        verifyOnlyStudentsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void StudentFeedbackSubmissionEditSave() throws Exception{
        uri = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE;
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", fr.getResponseDetails().getAnswerString()
        };
        
        verifyOnlyStudentsOfTheSameCourseCanAccess(submissionParams);
        testGracePeriodAccessControlForStudents();
    }
    
    private void testGracePeriodAccessControlForStudents() throws Exception{
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("gracePeriodSession");
        fs.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
        dataBundle.feedbackSessions.put("gracePeriodSession", fs);
        
        assertFalse(fs.isOpened());
        assertTrue(fs.isInGracePeriod());
        assertFalse(fs.isClosed());
                
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1GracePeriodFeedback");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", fr.getResponseDetails().getAnswerString() 
        };
        
        verifyOnlyStudentsOfTheSameCourseCanAccess(submissionParams);
    }
    
    @Test
    public void StudentHomePage() throws Exception{
        uri = Const.ActionURIs.STUDENT_HOME_PAGE;
        String[] submissionParams = new String[]{};
        verifyOnlyLoggedInUsersCanAccess(submissionParams);
        
    }
    
    @Test
    public void StudentProfileEditSave() throws Exception{
        uri = Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE;
        String[] submissionParams = createValidParamsForProfile();
        verifyAnyRegisteredUserCanAccess(submissionParams);
    }
    
    @Test
    public void StudentProfilePage() throws Exception{
        uri = Const.ActionURIs.STUDENT_PROFILE_PAGE;
        verifyAnyRegisteredUserCanAccess(submissionParams);
    }
    
    @Test
    public void StudentProfilePictureEdit() throws Exception{
        uri = Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
        String[] submissionParams = new String[] {
                Const.ParamsNames.PROFILE_PICTURE_LEFTX, "0",
                Const.ParamsNames.PROFILE_PICTURE_RIGHTX, "100",
                Const.ParamsNames.PROFILE_PICTURE_TOPY, "0",
                Const.ParamsNames.PROFILE_PICTURE_BOTTOMY, "100",
                Const.ParamsNames.PROFILE_PICTURE_HEIGHT, "500",
                Const.ParamsNames.PROFILE_PICTURE_WIDTH, "300",
                Const.ParamsNames.BLOB_KEY, "random-blobKey"
        };
        verifyAnyRegisteredUserCanAccess(submissionParams);
    }
    
    private void modifyFeedbackSessionPublishState(FeedbackSessionAttributes session, boolean isPublished) throws Exception {
        // startTime < endTime <= resultsVisibleFromTime
        Date startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        Date endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        Date resultsVisibleFromTimeForPublishedSession = TimeHelper.getDateOffsetToCurrentTime(-1);
        
        session.startTime = startTime;
        session.endTime = endTime;
        if(isPublished){
            session.resultsVisibleFromTime = resultsVisibleFromTimeForPublishedSession;
            assertTrue(session.isPublished());
        } else {
            session.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
            assertFalse(session.isPublished());
        }
        session.sentPublishedEmail = true;
        fsDb.updateFeedbackSession(session);
    }
    
    private void makeFeedbackSessionUnpublished(FeedbackSessionAttributes session) throws Exception {
        modifyFeedbackSessionPublishState(session, false);
    }
    
    private void makeFeedbackSessionPublished(FeedbackSessionAttributes session) throws Exception {
        modifyFeedbackSessionPublishState(session, true);
    }

    private void closeSession(FeedbackSessionAttributes fs) throws Exception {
        fs.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
        fsDb.updateFeedbackSession(fs);
    }

    private void makeEvaluationPublished(EvaluationAttributes eval) throws Exception {
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.activated = true;
        eval.published = true;
        assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        
    }

    private void makeEvaluationClosed(EvaluationAttributes eval) throws Exception {
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.activated = true;
        eval.published = false;
        assertEquals(EvalStatus.CLOSED, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
        
    }
    
    private void makeEvaluationOpen(EvaluationAttributes eval) throws Exception {
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        eval.activated = true;
        eval.published = false;
        assertEquals(EvalStatus.OPEN, eval.getStatus());
        evaluationsDb.updateEvaluation(eval);
    }
    
    private String[] addQuestionIdToParams(String questionId, String[] params) {
        List<String> list = new ArrayList<String>();
        list.add(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        list.add(questionId);
        for (String s : params) {
            list.add(s);
        }
        return list.toArray(new String[list.size()]);
    }

    private void checkAccessControlForEval(EvaluationAttributes eval, boolean isEditableForStudent)
            throws Exception {

        String courseId = eval.courseId;
        String evalName = eval.name;
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String studentId = student1InCourse1.googleId;
        
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.EVALUATION_NAME, evalName};
        
        verifyUnaccessibleWithoutLogin(submissionParams);
        
        gaeSimulation.loginUser("student1InCourse2");
        //if the user is not a student of the course, we redirect to home page.
        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
        verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
        
        if(isEditableForStudent){
            verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        }else {
            verifyUnaccessibleForStudents(submissionParams);
        }
        
        
        gaeSimulation.loginAsInstructor(instructorId);
        //if the user is not a student of the course, we redirect to home page.
        verifyRedirectTo(Const.ActionURIs.STUDENT_HOME_PAGE, submissionParams);
        verifyCannotMasquerade(addUserIdToParams(studentId,submissionParams));
        
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
    }
}
