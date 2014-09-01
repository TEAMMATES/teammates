package teammates.test.util;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.testng.Assert;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.SubmissionsLogic;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.com.google.common.base.Joiner;
import com.google.gson.Gson;

public class TestHelper extends BaseComponentTestCase{
    
    private static final Logic logic = new Logic();
    protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();

    private static final CoursesDb coursesDb = new CoursesDb();
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
    private static final StudentsDb studentsDb = new StudentsDb();
    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final CommentsDb commentsDb = new CommentsDb();
    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

    private static Gson gson = Utils.getTeammatesGson();

    /** 
     * Verifies submissions required to support the current Team structure
     *    exists in the database, for all evaluations under the give course. 
     *    However, there could also be orphaned submissions in the database. 
     *    This method does not care about those.
     */
    
    public static void verifySubmissionsExistForCurrentTeamStructureInAllExistingEvaluations(
            List<SubmissionAttributes> submissionList, String courseId) throws EntityDoesNotExistException {
        CourseDetailsBundle course = CoursesLogic.inst().getCourseDetails(courseId);
        List<StudentAttributes> students = studentsDb.getStudentsForCourse(courseId);

        for(EvaluationDetailsBundle e: course.evaluations){
            verifySubmissionsExistForCurrentTeamStructureInEvaluation(e.evaluation.name, students, submissionList);
        }
    }
    
    public static void verifySubmissionsExistForCurrentTeamStructureInEvaluation(String evaluationName,
            List<StudentAttributes> students, List<SubmissionAttributes> submissions) {

        for (StudentAttributes reviewer : students) {
            for (StudentAttributes reviewee : students) {
                if (!reviewer.team.equals(reviewee.team)) {
                    continue;
                }
                verifySubmissionExists(evaluationName, reviewer.email, reviewee.email,
                        reviewer.team, submissions);
            }
        }

    }

    /**
     * Verifies if there is a submission in the list for the 
     *    given evaluation for the same reviewer and reviewee 
     *    under the same team. Does not check other attributes.
     */
    public static void verifySubmissionExists(String evaluationName, String reviewer,
            String reviewee, String team, List<SubmissionAttributes> submissions) {
        int count = 0;
        for (SubmissionAttributes s : submissions) {
            if (s.evaluation.equals(evaluationName)
                    && s.reviewer.equals(reviewer)
                    && s.reviewee.equals(reviewee) 
                    && s.team.equals(team)) {
                count++;
            }
        }
        String errorMsg = "Count is not 1 for "+evaluationName+":"+team+":"+reviewer+"->"+reviewee;
        assertEquals(errorMsg, 1, count);
    }

    public static MimeMessage getEmailToStudent(StudentAttributes s,
            List<MimeMessage> emailsSent) throws MessagingException {
        for (MimeMessage m : emailsSent) {
            boolean emailSentToThisStudent = m.getAllRecipients()[0].toString()
                    .equalsIgnoreCase(s.email);
            if (emailSentToThisStudent) {
                print("email sent to:" + s.email);
                return m;
            }
        }
        return null;
    }
    
    public static List<MimeMessage> getEmailsToInstructor(InstructorAttributes i,
            List<MimeMessage> emailsSent) throws MessagingException {
        List<MimeMessage> emailsToInstructor = new ArrayList<MimeMessage>();
        for (MimeMessage m : emailsSent) {
            boolean emailSentToThisInstructor = m.getAllRecipients()[0].toString()
                    .equalsIgnoreCase(i.email);
            if (emailSentToThisInstructor) {
                print("email sent to:" + i.email);
                emailsToInstructor.add(m);
            }
        }
        return emailsToInstructor;
    }

    public static void verifyJoinInviteToStudent(StudentAttributes student,
            MimeMessage email) throws MessagingException {
        assertEquals(student.email, email.getAllRecipients()[0].toString());
        AssertHelper.assertContains(Emails.SUBJECT_PREFIX_STUDENT_COURSE_JOIN,
                email.getSubject());
        AssertHelper.assertContains(student.course, email.getSubject());
    }
    
    public static void verifyJoinInviteToInstructor(InstructorAttributes instr,
            MimeMessage email) throws MessagingException {
        assertEquals(instr.email, email.getAllRecipients()[0].toString());
        AssertHelper.assertContains(Emails.SUBJECT_PREFIX_INSTRUCTOR_COURSE_JOIN,
                email.getSubject());
        AssertHelper.assertContains(instr.courseId, email.getSubject());
    }

    public static void verifyEvaluationInfoExistsInList(EvaluationAttributes evaluation,
            ArrayList<EvaluationDetailsBundle> evalInfoList) {

        for (EvaluationDetailsBundle edd : evalInfoList) {
            if (edd.evaluation.name.equals(evaluation.name))
                return;
        }
        Assert.fail("Did not find " + evaluation.name + " in the evaluation info list");
    }

    public static void verifyEvaluationInfoExistsInAttributeList(EvaluationAttributes evaluation,
            ArrayList<EvaluationAttributes> evalInfoList) {

        for (EvaluationAttributes evalAttr : evalInfoList) {
            if (evalAttr.name.equals(evaluation.name))
                return;
        }
        Assert.fail("Did not find " + evaluation.name + " in the evaluation info list");
    }

    public static void verifyEnrollmentDetailsForStudent(StudentAttributes expectedStudent,
            String oldTeam, StudentEnrollDetails enrollmentResult, StudentAttributes.UpdateStatus status) {
        assertEquals(expectedStudent.email, enrollmentResult.email);
        assertEquals(expectedStudent.team, enrollmentResult.newTeam);
        assertEquals(expectedStudent.course, enrollmentResult.course);
        assertEquals(oldTeam, enrollmentResult.oldTeam);
        assertEquals(status, enrollmentResult.updateStatus);
    }
    
    public static void verifyEnrollmentResultForStudent(StudentAttributes expectedStudent,
            StudentAttributes enrollmentResult, StudentAttributes.UpdateStatus status) {
        String errorMessage = "mismatch! \n expected:\n"
                + Utils.getTeammatesGson().toJson(expectedStudent)
                + "\n actual \n"
                + Utils.getTeammatesGson().toJson(enrollmentResult);
        assertEquals(errorMessage, true,
                enrollmentResult.isEnrollInfoSameAs(expectedStudent) &&
                enrollmentResult.updateStatus == status);
    }

    public static void verifyAbsentInDatastore(AccountAttributes account)
            throws Exception {
        assertNull(logic.getAccount(account.googleId));
    }

    
    public static void verifyAbsentInDatastore(SubmissionAttributes submission)
            throws Exception {
        assertEquals(
                null,
                invokeGetSubmission(submission.course, submission.evaluation,
                        submission.reviewer, submission.reviewee));
    }

    public static void verifyAbsentInDatastore(InstructorAttributes expectedInstructor) {
        assertNull(instructorsDb.getInstructorForGoogleId(expectedInstructor.courseId, expectedInstructor.googleId));
    }

    public static void verifyAbsentInDatastore(CourseAttributes course) {
        assertNull(coursesDb.getCourse(course.id));
    }

    public static void verifyAbsentInDatastore(StudentAttributes student) {
        assertNull(logic.getStudentForEmail(student.course, student.email));
    }

    public static void verifyAbsentInDatastore(EvaluationAttributes evaluation) {
        assertNull(logic.getEvaluation(evaluation.courseId, evaluation.name));
    }
    
    public static void verifyAbsentInDatastore(FeedbackSessionAttributes fsa) {
        assertNull(fsDb.getFeedbackSession(fsa.courseId, fsa.feedbackSessionName));    
    }
    
    public static void verifyAbsentInDatastore(FeedbackQuestionAttributes fqa) {
        assertNull(fqDb.getFeedbackQuestion(fqa.feedbackSessionName, fqa.courseId, fqa.questionNumber));    
    }
    
    public static void verifyAbsentInDatastore(FeedbackResponseAttributes fra) {
        assertNull(frDb.getFeedbackResponse(fra.feedbackQuestionId, fra.giverEmail, fra.recipientEmail));
    }
    
    public static void verifyAbsentInDatastore(CommentAttributes comment) {
        assertNull(commentsDb.getComment(comment));
    }
    
    public static void verifyAbsentInDatastore(FeedbackResponseCommentAttributes frComment) {
        assertNull(frcDb.getFeedbackResponseComment(frComment.feedbackResponseId, 
                frComment.giverEmail, frComment.createdAt));
    }
    
    /**
     * Only checks if the entity exists
     * @param expectedAccount
     */
    public static void verifyPresentInDatastore(AccountAttributes expectedAccount) {
        AccountAttributes actualAccount = logic.getAccount(expectedAccount.googleId);
        // Account when created by createInstructor may take up different values in NAME and EMAIL
        // from the typicalDataBundle. Hence we only check that the account exists in the DataStore
        assertTrue(actualAccount != null);
    }

    public static void verifyPresentInDatastore(StudentAttributes expectedStudent) {
        StudentAttributes actualStudent = studentsDb.getStudentForEmail(expectedStudent.course,
                expectedStudent.email);
        expectedStudent.updateStatus = UpdateStatus.UNKNOWN;
        expectedStudent.lastName = StringHelper.splitName(expectedStudent.name)[1];
        equalizeIrrelevantData(expectedStudent, actualStudent);
        assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
    }

    public static void verifyPresentInDatastore(SubmissionAttributes expected)
            throws Exception {
        SubmissionAttributes actual = invokeGetSubmission(expected.course,
                expected.evaluation, expected.reviewer, expected.reviewee);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    public static void verifyPresentInDatastore(EvaluationAttributes expected) {
        EvaluationAttributes actual = evaluationsDb.getEvaluation(expected.courseId,
                expected.name);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    public static void verifyPresentInDatastore(CourseAttributes expected) {
        CourseAttributes actual = coursesDb.getCourse(expected.id);
        // Ignore time field as it is stamped at the time of creation in testing
        actual.createdAt = expected.createdAt;
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    public static void verifyPresentInDatastore(InstructorAttributes expected) {
        InstructorAttributes actual;
        
        if (expected.googleId != null) {
            actual = instructorsDb.getInstructorForGoogleId(expected.courseId, expected.googleId);
        } else {
            actual = instructorsDb.getInstructorForEmail(expected.courseId, expected.email);
        }
        equalizeIrrelevantData(expected, actual);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(FeedbackSessionAttributes expected) {
        FeedbackSessionAttributes actual = fsDb.getFeedbackSession(expected.courseId, expected.feedbackSessionName);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    public static void verifyPresentInDatastore(FeedbackQuestionAttributes expected) {
        FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(
                expected.feedbackSessionName, expected.courseId, expected.questionNumber);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(FeedbackQuestionAttributes expected, boolean wildcardId) {
        FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(
                expected.feedbackSessionName, expected.courseId, expected.questionNumber);
        if(wildcardId){
            expected.setId(actual.getId());
        }
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(FeedbackResponseAttributes expected) {
        FeedbackResponseAttributes actual = frDb.getFeedbackResponse(
                expected.feedbackQuestionId, expected.giverEmail, expected.recipientEmail);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(FeedbackResponseAttributes expected, boolean wildcardId) {
        FeedbackResponseAttributes actual = frDb.getFeedbackResponse(
                expected.feedbackQuestionId, expected.giverEmail, expected.recipientEmail);
        if(wildcardId){
            expected.setId(actual.getId());
        }
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(CommentAttributes expected){
        CommentAttributes actual = commentsDb.getComment(expected);
        assertEquals(expected.courseId, actual.courseId);
        assertEquals(expected.giverEmail, actual.giverEmail);
        assertEquals(expected.recipients, actual.recipients);
        assertEquals(expected.commentText, actual.commentText);
    }
    
    public static void verifyPresentInDatastore(FeedbackResponseCommentAttributes expected){
        FeedbackResponseCommentAttributes actual = frcDb.getFeedbackResponseComment(expected.courseId, expected.createdAt,
                expected.giverEmail);
        assertEquals(expected.courseId, actual.courseId);
        assertEquals(expected.giverEmail, actual.giverEmail);
        assertEquals(expected.feedbackSessionName, actual.feedbackSessionName);
        assertEquals(expected.commentText, actual.commentText);
    }
    
    public static void verifySameEvaluationData(EvaluationAttributes expected,
            EvaluationAttributes actual) {
        assertEquals(expected.courseId, actual.courseId);
        assertEquals(expected.name, actual.name);
        AssertHelper.assertSameDates(expected.startTime, actual.startTime);
        AssertHelper.assertSameDates(expected.endTime, actual.endTime);
        assertEquals(expected.timeZone, actual.timeZone, 0.1);
        assertEquals(expected.instructions, actual.instructions);
        assertEquals(expected.p2pEnabled, actual.p2pEnabled);
        assertEquals(expected.published, actual.published);
        assertEquals(expected.activated, actual.activated);
    }


    public static void verifyEntityDoesNotExistException(String methodName,
            Class<?>[] paramTypes, Object[] params) throws Exception {

        Method method = Logic.class.getDeclaredMethod(methodName, paramTypes);

        try {
            method.setAccessible(true); // in case it is a private method
            method.invoke(logic, params);
            Assert.fail();
        } catch (Exception e) {
            assertEquals(EntityDoesNotExistException.class, e.getCause()
                    .getClass());
        }
    }
    
    public static void equalizeIrrelevantData(
            StudentAttributes expectedStudent,
            StudentAttributes actualStudent) {
        
        // For these fields, we consider null and "" equivalent.
        if ((expectedStudent.googleId == null) && (actualStudent.googleId.equals(""))) {
            actualStudent.googleId = null;
        }
        if ((expectedStudent.team == null) && (actualStudent.team.equals(""))) {
            actualStudent.team = null;
        }
        if ((expectedStudent.comments == null)
                && (actualStudent.comments.equals(""))) {
            actualStudent.comments = null;
        }

        // pretend keys match because the key is generated on the server side
        // and cannot be anticipated
        if ((actualStudent.key != null)) {
            expectedStudent.key = actualStudent.key;
        }
    }
    
    public static void equalizeIrrelevantData(InstructorAttributes expectedInstructor,
            InstructorAttributes actualInstructor) {
        
        // pretend keys match because the key is generated only before storing into database
        if ((actualInstructor.key != null)) {
            expectedInstructor.key = actualInstructor.key;
        }
        if (!expectedInstructor.instructorPrivilegesAsText.equals(actualInstructor.instructorPrivilegesAsText)
                && expectedInstructor.privileges.equals(actualInstructor.privileges)) {
            actualInstructor.instructorPrivilegesAsText = expectedInstructor.getTextFromInstructorPrivileges();
        }
    }

    @SuppressWarnings("unused")
    private void ____invoking_private_methods__() {
    }


    public static void invokeEditEvaluation(EvaluationAttributes e)
            throws InvalidParametersException, EntityDoesNotExistException {
        logic.updateEvaluation(e.courseId, e.name, e.instructions.getValue(), e.startTime,
                e.endTime, e.timeZone, e.gracePeriod, e.p2pEnabled);
    }


    public static SubmissionAttributes invokeGetSubmission(String course,
            String evaluation, String reviewer, String reviewee)
            throws Exception {
        Method privateMethod = BackDoorLogic.class.getDeclaredMethod("getSubmission",
                new Class[] { String.class, String.class, String.class,
                        String.class });
        privateMethod.setAccessible(true);
        Object[] params = new Object[] { course, evaluation, reviewer, reviewee };
        return (SubmissionAttributes) privateMethod.invoke(new BackDoorLogic(), params);
    }


    @SuppressWarnings("unused")
    private void ____test_object_manipulation_methods__() {
    }

    public static void createNewEvaluationWithSubmissions(String courseId,
            String evaluationName, int[][] input)
            throws EntityAlreadyExistsException, InvalidParametersException,
            EntityDoesNotExistException {
        // create course
        
        logic.createAccount("instructorForTestingER", "Instructor 1", true, "instructor@email.tmt", "TEAMMATES Test Institute 1");
        logic.createCourseAndInstructor("instructorForTestingER", courseId,
                "Course For Testing Evaluation Results");
        // create students
        int teamSize = input.length;
        String teamName = "team1";
        for (int i = 0; i < teamSize; i++) {
            StudentAttributes student = new StudentAttributes();
            int studentNumber = i + 1;
            student.email = "s" + studentNumber + "@gmail.tmt";
            student.name = "Student " + studentNumber;
            student.team = teamName;
            student.course = courseId;
            student.comments = "";
            student.googleId="";
            studentsDb.createStudentWithoutDocument(student);
        }
        // create evaluation
        EvaluationAttributes e = new EvaluationAttributes();
        e.courseId = courseId;
        e.name = evaluationName;
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.gracePeriod = 0;
        e.instructions = new Text("instructions for " + e.name);
        logic.createEvaluationWithoutSubmissionQueue(e);
        // create submissions
        ArrayList<SubmissionAttributes> submissions = new ArrayList<SubmissionAttributes>();
        for (int i = 0; i < teamSize; i++) {
            for (int j = 0; j < teamSize; j++) {
                SubmissionAttributes sub = new SubmissionAttributes();
                sub.course = courseId;
                sub.evaluation = e.name;
                sub.team = teamName;
                int reviewerNumber = i + 1;
                sub.reviewer = "s" + reviewerNumber + "@gmail.tmt";
                int revieweeNumber = j + 1;
                sub.reviewee = "s" + revieweeNumber + "@gmail.tmt";
                sub.points = input[i][j];
                sub.justification = new Text("jus[s" + reviewerNumber + "->s"
                        + revieweeNumber + "]");
                sub.p2pFeedback = new Text("p2p[s" + reviewerNumber + "->s"
                        + revieweeNumber + "]");
                submissions.add(sub);
            }
        }
        logic.updateSubmissions(submissions);
    }

    public static void alterSubmission(SubmissionAttributes submission) {
        submission.points = submission.points + 10;
        submission.p2pFeedback = new Text(submission.p2pFeedback.getValue()
                + "x");
        submission.justification = new Text(submission.justification.getValue()
                + "y");
    }

    public static void setPointsForSubmissions(int[][] points) throws Exception {
        int teamSize = points.length;
        ArrayList<SubmissionAttributes> submissions = new ArrayList<SubmissionAttributes>();
        for (int i = 0; i < teamSize; i++) {
            for (int j = 0; j < teamSize; j++) {
                SubmissionAttributes s = invokeGetSubmission("idOfTypicalCourse1",
                        "evaluation1 In Course1", "student" + (i + 1)
                                + "InCourse1@gmail.tmt", "student" + (j + 1)
                                + "InCourse1@gmail.tmt");
                s.points = points[i][j];
                submissions.add(s);
            }
        }
        logic.updateSubmissions(submissions);
    }
    
    //this function used to check whether two lists have same contents,ignoring order
    
    @SuppressWarnings("rawtypes")
    
    public static boolean isSameContentIgnoreOrder(List a, List b) {

        String expectedListAsString = Joiner.on("\t").join(a);
        String actualListAsString = Joiner.on("\t").join(b);

        List<String> expectedStringTypeList = new ArrayList<String>(
                Arrays.asList(expectedListAsString.split("\t")));
        List<String> actualStringTypeList = new ArrayList<String>(
                Arrays.asList(actualListAsString.split("\t")));

        Collections.sort(expectedStringTypeList);
        Collections.sort(actualStringTypeList);

        return expectedStringTypeList.equals(actualStringTypeList);

    }

}
