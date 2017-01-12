package teammates.test.cases.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.logic.core.StudentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.Action;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

/**
 * Parent class for *ActionTest classes.
 */
public abstract class BaseActionTest extends BaseComponentTestCase {
    
    /**URI that matches with the action being tested */
    protected static String uri;

    private DataBundle data = getTypicalDataBundle();
    
    /** Executes the action and returns the result.
     * Assumption: The action returns a ShowPageResult.
     */
    protected ShowPageResult getShowPageResult(Action a) {
        return (ShowPageResult) a.executeAndPostProcess();
    }
    
    /** Executes the action and returns the result.
     * Assumption: The action returns a RedirectResult.
     */
    protected RedirectResult getRedirectResult(Action a) {
        //TODO: check existing code to use this method instead of casting independently
        return (RedirectResult) a.executeAndPostProcess();
    }
    
    /** Executes the action and returns the result.
     * Assumption: The action returns a AjaxResult.
     */
    protected AjaxResult getAjaxResult(Action a) {
        return (AjaxResult) a.executeAndPostProcess();
    }

    /**
     * @return The {@code params} array with the {@code userId}
     * (together with the parameter name) inserted at the beginning.
     */
    protected String[] addUserIdToParams(String userId, String[] params) {
        List<String> list = new ArrayList<String>();
        list.add(Const.ParamsNames.USER_ID);
        list.add(userId);
        for (String s : params) {
            list.add(s);
        }
        return list.toArray(new String[list.size()]);
    }
    
    private String[] addStudentAuthenticationInfo(String[] params) {
        StudentAttributes unregStudent =
                StudentsLogic.inst().getStudentForEmail("idOfTypicalCourse1", "student6InCourse1@gmail.tmt");
        List<String> list = new ArrayList<String>();
        list.add(Const.ParamsNames.REGKEY);
        list.add(StringHelper.encrypt(unregStudent.key));
        list.add(Const.ParamsNames.STUDENT_EMAIL);
        list.add(unregStudent.email);
        for (String s : params) {
            list.add(s);
        }
        return list.toArray(new String[list.size()]);
    }
    
    protected String[] createValidParamsForProfile() {
        String[] submissionParams = new String[]{
                Const.ParamsNames.STUDENT_SHORT_NAME, "short ",
                Const.ParamsNames.STUDENT_PROFILE_EMAIL, "e@email.com  ",
                Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, " TEAMMATES Test Institute 5   ",
                Const.ParamsNames.STUDENT_NATIONALITY, "American",
                Const.ParamsNames.STUDENT_GENDER, "  other   ",
                Const.ParamsNames.STUDENT_PROFILE_MOREINFO, "   This is more info on me   "
        };
        return submissionParams;
    }
    
    protected String[] createInvalidParamsForProfile() {
        String[] submissionParams = new String[]{
                Const.ParamsNames.STUDENT_SHORT_NAME, "$$short",
                Const.ParamsNames.STUDENT_PROFILE_EMAIL, "invalid.email",
                Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, "institute",
                Const.ParamsNames.STUDENT_NATIONALITY, "USA",
                Const.ParamsNames.STUDENT_GENDER, "female",
                Const.ParamsNames.STUDENT_PROFILE_MOREINFO, "This is more info on me"
        };
        return submissionParams;
    }

    protected String[] createParamsCombinationForFeedbackSession(String courseId, String fsName, int order) {
        String[] typicalCase = createParamsForTypicalFeedbackSession(courseId, fsName);
        if (order == 0) {
            return typicalCase;
        }
        
        List<String> paramList = Arrays.asList(typicalCase);
        int indexOfSessionVisibleDate = 1 + paramList.indexOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE);
        int indexOfSessionVisibleTime = 1 + paramList.indexOf(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME);
        int indexOfSessionVisibleButtonValue =
                1 + paramList.indexOf(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
        
        int indexOfSessionPublishDate = 1 + paramList.indexOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE);
        int indexOfSessionPublishTime = 1 + paramList.indexOf(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME);
        int indexOfResultsVisibleButtonValue =
                1 + paramList.indexOf(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
        
        int indexOfSessionInstructionsValue = 1 + paramList.indexOf(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS);
        
        switch (order) {
        case 1:
            typicalCase[indexOfSessionVisibleButtonValue] = Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN;
            typicalCase[indexOfSessionVisibleDate] = "";
            typicalCase[indexOfSessionVisibleTime] = "0";
            
            typicalCase[indexOfResultsVisibleButtonValue] = Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM;
            typicalCase[indexOfSessionPublishDate] = "08/05/2014";
            typicalCase[indexOfSessionPublishTime] = "2";
            break;
        case 2:
            typicalCase[indexOfSessionVisibleButtonValue] = Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_NEVER;
            typicalCase[indexOfSessionVisibleDate] = "";
            typicalCase[indexOfSessionVisibleTime] = "0";
            
            typicalCase[indexOfResultsVisibleButtonValue] = Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER;
            
            typicalCase[indexOfSessionInstructionsValue] = "<script>test</script>instructions";
            break;
        case 3:
            typicalCase[indexOfResultsVisibleButtonValue] = Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER;
            typicalCase[indexOfSessionInstructionsValue] = "";
            break;
        default:
            Assumption.fail("Incorrect order");
            break;
        }
        
        return typicalCase;
    }
    
    protected String[] createParamsForTypicalFeedbackSession(String courseId, String fsName) {
        
        return new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsName,
                Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, "01/02/2012",
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, "0",
                Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, "01/01/2015",
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, "0",
                
                Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON,
                Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM,
                
                Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, "01/01/2012",
                Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME, "0",
                
                Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON,
                Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE,
                
                Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, "",
                Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME, "0",
                Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE, "8",
                Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD, "10",
                Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS, "instructions"
        };
    }
    
    protected String[] createParamsForTypicalFeedbackQuestion(String courseId, String fsName) {
        
        return new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit"
        };
    }
    
    /**
     * Modifies the value of a key in a parameter list.
     * Assumes Key is present. Use for testing.
     * @param params An array of Strings in the form {key1, value1, key2, value2,....}
     * @param key Key to modify
     * @param value Value to set
     */
    protected void modifyParamValue(String[] params, String key, String value) {
        for (int i = 0; i < params.length; i += 2) {
            if (params[i] == key) {
                if (i + 1 >= params.length) {
                    fail("Cannot find parameter to modify.");
                } else {
                    params[i + 1] = value;
                    return;
                }
            }
        }
        fail("Cannot find parameter to modify.");
    }
    
    /**
     * Verifies that the {@code parameters} violates an assumption of the
     * matching {@link Action}. e.g., missing a compulsory parameter.
     */
    protected void verifyAssumptionFailure(String... parameters) {
        try {
            Action c = gaeSimulation.getActionObject(uri, parameters);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (AssertionError | NullPostParameterException e) {
            ignoreExpectedException();
        }
    }

    /*
     * 'high-level' tests here means it tests access control of an action for the
     * full range of user types.
     */
    
    protected void verifyAnyRegisteredUserCanAccess(String[] submissionParams) {
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyAccessibleForStudents(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
    }
    
    protected void verifyOnlyAdminsCanAccess(String[] submissionParams) {
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructors(submissionParams);
        //we omit checking for admin access because these are covered by UI tests
    }
    
    protected void verifyOnlyLoggedInUsersCanAccess(String[] submissionParams) {
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyAccessibleForUnregisteredUsers(submissionParams);
        verifyAccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfOtherCourses(submissionParams);
        //No need to check for instructors of the same course since even other instructors can access.
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    protected void verifyOnlyInstructorsCanAccess(String[] submissionParams) {
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    protected void verifyOnlyInstructorsOfTheSameCourseCanAccess(String[] submissionParams) {
        verifyUnaccessibleWithoutLogin(submissionParams);
        verifyUnaccessibleForUnregisteredUsers(submissionParams);
        verifyUnaccessibleForStudents(submissionParams);
        verifyUnaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
    
    protected void verifyOnlyStudentsOfTheSameCourseCanAccess(String[] submissionParams) {
        verifyAccessibleWithoutLogin(submissionParams);
        verifyAccessibleForUnregisteredStudents(submissionParams);
        verifyUnaccessibleForStudentsOfOtherCourses(submissionParams);
        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        verifyUnaccessibleForInstructors(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsStudent(submissionParams);
    }

    /*
     * 'mid-level' tests here tests access control of an action for
     * one user types.
     */
    protected void verifyAccessibleWithoutLogin(String[] submissionParams) {
        gaeSimulation.logoutUser();
        verifyCanAccess(addStudentAuthenticationInfo(submissionParams));
    }

    protected void verifyAccessibleForUnregisteredUsers(String[] submissionParams) {
        
        ______TS("non-registered users can access");
        
        String unregUserId = "unreg1.user";
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        
        gaeSimulation.loginUser(unregUserId);
        verifyCanAccess(submissionParams);
        verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId, submissionParams));
        verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId, submissionParams));
    }

    protected void verifyAccessibleForStudentsOfTheSameCourse(String[] submissionParams) {
        
        ______TS("students of the same course can access");
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);
        verifyCanAccess(submissionParams);
        verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId, submissionParams));
        
    }

    protected void verifyAccessibleForStudents(String[] submissionParams) {
        
        ______TS("students can access");
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        StudentAttributes otherStudent = data.students.get("student1InCourse2");
        
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);
        verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId, submissionParams));
        verifyCannotMasquerade(addUserIdToParams(otherStudent.googleId, submissionParams));
        verifyCanAccess(submissionParams);
        
    }

    private void verifyAccessibleForUnregisteredStudents(String[] submissionParams) {
        
        gaeSimulation.logoutUser();
        verifyCanAccess(addStudentAuthenticationInfo(submissionParams));
    }

    protected void verifyAccessibleForInstructorsOfTheSameCourse(String[] submissionParams) {
        
        ______TS("course instructor can access");
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        InstructorAttributes otherInstructor = data.instructors.get("instructor1OfCourse2");
        
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCanAccess(submissionParams);
        
        verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId, submissionParams));
        verifyCannotMasquerade(addUserIdToParams(otherInstructor.googleId, submissionParams));
        
    }

    protected void verifyAccessibleForInstructorsOfOtherCourses(String[] submissionParams) {
        
        ______TS("other course instructor can access");
    
        InstructorAttributes otherInstructor = data.instructors.get("instructor1OfCourse2");
        
        gaeSimulation.loginAsInstructor(otherInstructor.googleId);
        verifyCanAccess(submissionParams);
    }

    protected void verifyAccessibleForAdminToMasqueradeAsInstructor(String[] submissionParams) {
        
        ______TS("admin can access");
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        
        gaeSimulation.loginAsAdmin("admin.user");
        //not checking for non-masquerade mode because admin may not be an instructor
        verifyCanMasquerade(addUserIdToParams(instructor1OfCourse1.googleId, submissionParams));
        
    }

    protected void verifyAccessibleForAdminToMasqueradeAsStudent(String[] submissionParams) {
        
        ______TS("admin can access");
        
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        
        gaeSimulation.loginAsAdmin("admin.user");
        //not checking for non-masquerade mode because admin may not be a student
        verifyCanMasquerade(addUserIdToParams(student1InCourse1.googleId, submissionParams));
        
    }

    protected void verifyUnaccessibleWithoutLogin(String[] submissionParams) {
        
        ______TS("not-logged-in users cannot access");
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        
        gaeSimulation.logoutUser();
        verifyRedirectToLoginOrUnauthorisedException(submissionParams);
        verifyUnaccessibleWithoutLoginMasquerade(addUserIdToParams(student1InCourse1.googleId, submissionParams));
        verifyUnaccessibleWithoutLoginMasquerade(addUserIdToParams(instructor1OfCourse1.googleId, submissionParams));
    }
    
    private void verifyUnaccessibleWithoutLoginMasquerade(String... params) {
        verifyRedirectToLoginOrUnauthorisedException(params);
    }

    private void verifyRedirectToLoginOrUnauthorisedException(String... params) {
        try {
            Action c = gaeSimulation.getActionObject(uri, params);
            assertFalse(c.isValidUser());
        } catch (UnauthorizedAccessException ue) {
            ignoreExpectedException();
        }
    }

    protected void verifyUnaccessibleForUnregisteredUsers(String[] submissionParams) {
        
        ______TS("non-registered users cannot access");
        
        String unregUserId = "unreg.user";
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        
        gaeSimulation.loginUser(unregUserId);
        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId, submissionParams));
        verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId, submissionParams));
        
    }
    
    protected void verifyUnaccessibleWithoutModifyCoursePrivilege(String[] submissionParams) {
        
        ______TS("without Modify-Course privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutModifyInstructorPrivilege(String[] submissionParams) {
        
        ______TS("without Modify-Instructor privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutModifySessionPrivilege(String[] submissionParams) {

        ______TS("without Modify-Session privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutModifyStudentPrivilege(String[] submissionParams) {
        
        ______TS("without Modify-Student privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutViewStudentInSectionsPrivilege(String[] submissionParams) {
        
        ______TS("without View-Student-In-Sections privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutGiveCommentInSectionsPrivilege(String[] submissionParams) {
        
        ______TS("without Give-Comment-In-Sections privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutModifyCommentInSectionsPrivilege(String[] submissionParams) {
        
        ______TS("without Modify-Comment-In-Sections privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutViewSessionInSectionsPrivilege(String[] submissionParams) {
        
        ______TS("without View-Student-In-Sections privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutModifySessionInSectionsPrivilege(String[] submissionParams) {
        
        ______TS("without Modify-Session-In-Sections privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutSubmitSessionInSectionsPrivilege(String[] submissionParams) {
        
        ______TS("without Submit-Session-In-Sections privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleWithoutModifySessionCommentInSectionsPrivilege(String[] submissionParams) {
        
        ______TS("without Modify-Session-Comment-In-Sections privilege cannot access");
        
        InstructorAttributes helperOfCourse1 = data.instructors.get("helperOfCourse1");
        
        gaeSimulation.loginAsInstructor(helperOfCourse1.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    protected void verifyUnaccessibleForStudents(String[] submissionParams) {
        
        ______TS("students cannot access");
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);
        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(addUserIdToParams(instructor1OfCourse1.googleId, submissionParams));
        
    }
    
    protected void verifyUnaccessibleForStudentsOfOtherCourses(String[] submissionParams) {
        
        ______TS("students of other courses cannot access");
    
        StudentAttributes studentInOtherCourse = data.students.get("student1InCourse2");
        
        gaeSimulation.loginAsStudent(studentInOtherCourse.googleId);
        verifyCannotAccess(submissionParams);
    }

    protected void verifyUnaccessibleForDifferentStudentOfTheSameCourses(String[] submissionParams) {
        
        ______TS("other students of the same course cannot access");
    
        StudentAttributes differentStudentInSameCourse = data.students.get("student2InCourse1");
        
        gaeSimulation.loginAsStudent(differentStudentInSameCourse.googleId);
        verifyCannotAccess(submissionParams);
    }

    protected void verifyUnaccessibleForInstructors(String[] submissionParams) {
        
        ______TS("instructors cannot access");
        
        InstructorAttributes instructor1OfCourse1 = data.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = data.students.get("student1InCourse1");
        
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);
        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(addUserIdToParams(student1InCourse1.googleId, submissionParams));
        
    }
    
    protected void verifyUnaccessibleForInstructorsOfOtherCourses(String[] submissionParams) {
        
        ______TS("other course instructor cannot access");
    
        InstructorAttributes otherInstructor = data.instructors.get("instructor1OfCourse2");
        
        gaeSimulation.loginAsInstructor(otherInstructor.googleId);
        verifyCannotAccess(submissionParams);
    }
    
    /*
     * 'low-level' tests here it tests an action once with the given parameters.
     * These methods are not aware of the user type.
     */
    /**
     * Verifies that the {@link Action} matching the {@code params} is
     * accessible to the logged in user.
     */
    protected void verifyCanAccess(String... params) {
        Action c = gaeSimulation.getActionObject(uri, params);
        assertTrue(c.isValidUser());
        c.executeAndPostProcess();
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is
     * accessible to the logged in user masquerading as another user.
     */
    protected void verifyCanMasquerade(String... params) {
        Action c = gaeSimulation.getActionObject(uri, params);
        assertTrue(c.isValidUser());
        c.executeAndPostProcess();
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not
     * accessible to the logged in user.
     * This could be one of the following ways:
     * -> Unauthorised Access Exception
     * ->
     */
    protected void verifyCannotAccess(String... params) {
        try {
            Action c = gaeSimulation.getActionObject(uri, params);
            ActionResult result = c.executeAndPostProcess();
            
            String classNameOfResult = result.getClass().getName();
            assertEquals(classNameOfResult, result.getClass().getName());
            AssertHelper.assertContains("You are not registered in the course ", result.getStatusMessage());
        } catch (UnauthorizedAccessException e) {
            ignoreExpectedException();
        }
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is not
     * accessible to the logged in user masquerading as another user.
     */
    protected void verifyCannotMasquerade(String... params) {
        try {
            Action c = gaeSimulation.getActionObject(uri, params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException e) {
            ignoreExpectedException();
        }
    }

    /**
     * Verifies that the {@link Action} matching the {@code params} is
     * redirected to {@code expectedRedirectUrl}. Note that only the base
     * URI is matched and parameters are ignored. E.g. "/page/studentHome"
     * matches "/page/studentHome?user=abc".
     */
    protected void verifyRedirectTo(String expectedRedirectUrl, String... params) {
        Action c = gaeSimulation.getActionObject(uri, params);
        RedirectResult r = (RedirectResult) c.executeAndPostProcess();
        AssertHelper.assertContains(expectedRedirectUrl, r.destination);
    }

    protected void verifyNoTasksAdded(Action action) {
        Map<String, Integer> tasksAdded = action.getTaskQueuer().getNumberOfTasksAdded();
        assertEquals(0, tasksAdded.keySet().size());
    }

    protected void verifySpecifiedTasksAdded(Action action, String taskName, int taskCount) {
        Map<String, Integer> tasksAdded = action.getTaskQueuer().getNumberOfTasksAdded();
        assertEquals(taskCount, tasksAdded.get(taskName).intValue());
    }

    protected void verifyNoEmailsSent(Action action) {
        assertTrue(getEmailsSent(action).isEmpty());
    }

    protected List<EmailWrapper> getEmailsSent(Action action) {
        return action.getEmailSender().getEmailsSent();
    }

    protected void verifyNumberOfEmailsSent(Action action, int emailCount) {
        assertEquals(emailCount, action.getEmailSender().getEmailsSent().size());
    }

}
