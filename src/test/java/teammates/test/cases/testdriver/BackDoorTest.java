package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.util.Priority;

import com.google.gson.Gson;

@Priority(2)
public class BackDoorTest extends BaseTestCase {

    private static Gson gson = Utils.getTeammatesGson();
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static String jsonString = gson.toJson(dataBundle);

    @BeforeClass
    public static void setUp() throws Exception {
        printTestClassHeader();
        dataBundle = getTypicalDataBundle();
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, BackDoor.removeAndRestoreDataBundleFromDb(dataBundle));
    }

    @SuppressWarnings("unused")
    private void ____SYSTEM_level_methods_________________________________() {
    }
    
    @Priority(-2)
    @Test
    public void testPersistence() {
        // typical bundle should be restored in the @BeforeClass method above
        verifyPresentInDatastore(jsonString);
    }

    @Test
    public void testDeletion() {
        
        // ----------deleting Instructor entities-------------------------
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor2OfCourse2");
        verifyPresentInDatastore(instructor1OfCourse1);
        String status = BackDoor.deleteInstructor(instructor1OfCourse1.courseId, instructor1OfCourse1.email);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(instructor1OfCourse1);
        
        //try to delete again: should indicate as success because delete fails silently.
        status = BackDoor.deleteInstructor(instructor1OfCourse1.email, instructor1OfCourse1.courseId);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        
        // ----------deleting Course entities-------------------------
        // #COURSE 2
        CourseAttributes course2 = dataBundle.courses.get("typicalCourse2");
        verifyPresentInDatastore(course2);
        status = BackDoor.deleteCourse(course2.id);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(course2);

        // check if related student entities are also deleted
        StudentAttributes student2InCourse2 = dataBundle.students
                .get("student2InCourse2");
        verifyAbsentInDatastore(student2InCourse2);
        
        // #COURSE 1
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        verifyPresentInDatastore(course1);
        status = BackDoor.deleteCourse(course1.id);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(course1);
        
        // check if related student entities are also deleted
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        verifyAbsentInDatastore(student1InCourse1);
        
        
        // #COURSE NO EVALS
        CourseAttributes courseNoEvals = dataBundle.courses.get("courseNoEvals");
        verifyPresentInDatastore(courseNoEvals);
        status = BackDoor.deleteCourse(courseNoEvals.id);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyAbsentInDatastore(courseNoEvals);
        
        // ----------deleting Feedback Session entities-------------------------
        // TODO: do proper deletion test
        BackDoor.deleteFeedbackSessions(dataBundle);

    }
    
    @SuppressWarnings("unused")
    private void ____ACCOUNT_level_methods_________________________________() {
    }
    
    @Test
    public void testAccounts() throws Exception{
        
        testCreateAccount();
        testGetAccountAsJson();
        testEditAccount();
        testDeleteAccount();
    }
    
    public void testCreateAccount() {
        AccountAttributes newAccount = dataBundle.accounts.get("instructor1OfCourse1");
        BackDoor.deleteAccount(newAccount.googleId);
        verifyAbsentInDatastore(newAccount);
        BackDoor.createAccount(newAccount);
        verifyPresentInDatastore(newAccount);
    }
    
    public void testGetAccountAsJson() {
        AccountAttributes testAccount = dataBundle.accounts.get("instructor1OfCourse1");
        verifyPresentInDatastore(testAccount);
        String actualString = BackDoor.getAccountAsJson(testAccount.googleId);
        AccountAttributes actualAccount = gson.fromJson(actualString, AccountAttributes.class);
        actualAccount.createdAt = testAccount.createdAt;
        assertEquals(gson.toJson(testAccount), gson.toJson(actualAccount));
    }
    
    public void testEditAccount() {
        AccountAttributes testAccount = dataBundle.accounts.get("instructor1OfCourse1");
        verifyPresentInDatastore(testAccount);
        testAccount.name = "New name";
        testAccount.institute = "TEAMMATES Test Institute 7";
        BackDoor.editAccount(testAccount);
        verifyPresentInDatastore(testAccount);
    }
    
    public void testDeleteAccount() {
        AccountAttributes testAccount = dataBundle.accounts.get("instructor2OfCourse1");
        BackDoor.createAccount(testAccount);
        verifyPresentInDatastore(testAccount);
        BackDoor.deleteAccount(testAccount.googleId);
        verifyAbsentInDatastore(testAccount);
    }

    @SuppressWarnings("unused")
    private void ____INSTRUCTOR_level_methods_________________________________() {
    }

    public void testDeleteInstructors() {
        // already tested by testPersistenceAndDeletion
    }

    @Test
    public void testCreateInstructor() {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        String instructorId = "tmapitt.tcc.instructor";
        String courseId = "tmapitt.tcc.course";
        String name = "Tmapitt testInstr Name";
        String email = "tmapitt@tci.tmt";
        @SuppressWarnings("deprecation")
        InstructorAttributes instructor = new InstructorAttributes(instructorId, courseId, name, email);
        
        // Make sure not already inside
        BackDoor.deleteInstructor(courseId, email);
        verifyAbsentInDatastore(instructor);
        
        // Perform creation
        BackDoor.createInstructor(instructor);
        verifyPresentInDatastore(instructor);
        instructor = BackDoor.getInstructorByEmail(email, courseId);
        // Clean up
        BackDoor.deleteInstructor(courseId, email);
        BackDoor.deleteAccount(instructor.googleId);
        verifyAbsentInDatastore(instructor);
    }

    public void testGetInstructorAsJson() {
        // already tested by testPersistenceAndDeletion
    }

    public void testDeleteInstructor() {
        // already tested by testPersistenceAndDeletion
    }

    public void testEditInstructor() {
        // method not implemented
    }

    

    @SuppressWarnings("deprecation")
    // decrepated methods are used correctly
    @Test
    public void testGetCoursesByInstructorId() throws InvalidParametersException {

        // testing for non-existent instructor
        String[] courses = BackDoor.getCoursesByInstructorId("nonExistentInstructor");
        assertEquals("[]", Arrays.toString(courses));
        
        // Create 2 courses for a new instructor
        String course1 = "AST.TGCBCI.course1";
        String course2 = "AST.TGCBCI.course2";
        BackDoor.deleteCourse(course1);
        BackDoor.deleteCourse(course2);
        String status = BackDoor.createCourse(new CourseAttributes(course1, "tmapit tgcbci c1OfInstructor1"));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        status = BackDoor.createCourse(new CourseAttributes(course2, "tmapit tgcbci c2OfInstructor1"));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        
        // create a fresh instructor with relations for the 2 courses
        String instructor1Id = "AST.TGCBCI.instructor1";
        String instructor1name = "AST TGCBCI Instructor";
        String instructor1email = "instructor1@ast.tmt";
        BackDoor.deleteAccount(instructor1Id);
        status = BackDoor.createInstructor(new InstructorAttributes(instructor1Id, course1, instructor1name, instructor1email));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        status = BackDoor.createInstructor(new InstructorAttributes(instructor1Id, course2, instructor1name, instructor1email));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        //============================================================================
        // Don't be confused by the following: it has no relation with the above instructor/course(s)
        
        // add a course that belongs to a different instructor
        String course3 = "AST.TGCBCI.course3";
        BackDoor.deleteCourse(course3);
        status = BackDoor.createCourse(new CourseAttributes(course3, "tmapit tgcbci c1OfInstructor2"));
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);

        courses = BackDoor.getCoursesByInstructorId(instructor1Id);
        assertEquals("[" + course1 + ", " + course2 + "]", Arrays.toString(courses));

        BackDoor.deleteInstructor(instructor1email, course1);
        BackDoor.deleteInstructor(instructor1email, course2);
    }

    @SuppressWarnings("unused")
    private void ____COURSE_level_methods_________________________________() {
    }

    @Test
    public void testCreateCourse() throws InvalidParametersException {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        String courseId = "tmapitt.tcc.course";
        CourseAttributes course = new CourseAttributes(courseId,
                "Name of tmapitt.tcc.instructor");
        
        // Make sure not already inside
        BackDoor.deleteCourse(courseId);
        verifyAbsentInDatastore(course);
        
        // Perform creation
        BackDoor.createCourse(course);
        verifyPresentInDatastore(course);
        
        // Clean up
        BackDoor.deleteCourse(courseId);
        verifyAbsentInDatastore(course);
    }

    
    public void testGetCourseAsJson() {
        // already tested by testPersistenceAndDeletion
    }
    
    public void testEditCourse() {
        // not implemented
    }

    
    public void testDeleteCourse() {
        // already tested by testPersistenceAndDeletion
    }

    @SuppressWarnings("unused")
    private void ____STUDENT_level_methods_________________________________() {
    }

    @Test
    public void testCreateStudent() throws EnrollException {
        // only minimal testing because this is a wrapper method for
        // another well-tested method.

        StudentAttributes student = new StudentAttributes(
                "section name", "team name", "name of tcs student", "tcsStudent@gmail.tmt", "",
                "tmapit.tcs.course");
        BackDoor.deleteStudent(student.course, student.email);
        verifyAbsentInDatastore(student);
        BackDoor.createStudent(student);
        verifyPresentInDatastore(student);
        BackDoor.deleteStudent(student.course, student.email);
        verifyAbsentInDatastore(student);
    }

    @Test
    public void testGetKeyForStudent() throws EnrollException {

        StudentAttributes student = new StudentAttributes("sect1", "t1", "name of tgsr student", "tgsr@gmail.tmt", "", "course1");
        BackDoor.createStudent(student);
        String key = "[BACKDOOR_STATUS_FAILURE]";
        while (key.startsWith("[BACKDOOR_STATUS_FAILURE]")) {
            key = BackDoor.getKeyForStudent(student.course, student.email);
        }

        // The following is the google app engine description about generating
        // keys.
        //
        // A key can be converted to a string by passing the Key object to
        // str(). The string is "urlsafe"—it uses only characters valid for use in URLs. 
        //
        // RFC3986 definition of a safe url pattern
        // Characters that are allowed in a URI but do not have a reserved
        // purpose are called unreserved. 
        // unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
        String pattern = "(\\w|-|~|\\.)*";

        String errorMessage = key + "[length=" + key.length() + "][reg="
                + StringHelper.isMatching(key, pattern) + "] is not as expected";
        assertTrue(errorMessage, key.length() > 30 && StringHelper.isMatching(key, pattern));

        // clean up student as this is an orphan entity
        BackDoor.deleteStudent(student.course, student.email);

    }

    public void testGetStudentAsJson() {
        // already tested by testPersistenceAndDeletion
    }

    @Test
    public void testEditStudent() {

        // check for successful edit        
        StudentAttributes student = dataBundle.students.get("student4InCourse1");
        // try to create the entity in case it does not exist
        BackDoor.createStudent(student);
        
        String originalEmail = student.email;
        student.name = "New name";
        student.lastName = "name";
        student.email = "new@gmail.tmt";
        student.comments = "new comments";
        student.team = "new team";
        String status = BackDoor.editStudent(originalEmail, student);
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, status);
        verifyPresentInDatastore(student);

        // test for unsuccessful edit
        student.course = "non-existent";
        status = BackDoor.editStudent(originalEmail, student);
        assertTrue(status.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE));
        verifyAbsentInDatastore(student);
    }

    public void testDeleteStudent() {
        // already tested by testPersistenceAndDeletion
    }

    @SuppressWarnings("unused")
    private void ____EVALUATION_level_methods______________________________() {
    }
    

    @SuppressWarnings("unused")
    private void ____helper_methods_________________________________() {
    }

    

    private void verifyAbsentInDatastore(AccountAttributes account) {
        assertEquals("null", BackDoor.getAccountAsJson(account.googleId));
    }
    
    private void verifyAbsentInDatastore(CourseAttributes course) {
        assertEquals("null", BackDoor.getCourseAsJson(course.id));
    }
    
    private void verifyAbsentInDatastore(InstructorAttributes expectedInstructor) {
        assertEquals("null", BackDoor.getInstructorAsJsonByEmail(expectedInstructor.email, expectedInstructor.courseId));
    }

    private void verifyAbsentInDatastore(StudentAttributes student) {
        assertEquals("null",
                BackDoor.getStudentAsJson(student.course, student.email));
    }

    
    private void verifyPresentInDatastore(String dataBundleJsonString) {
        Gson gson = Utils.getTeammatesGson();

        DataBundle data = gson.fromJson(dataBundleJsonString, DataBundle.class);
        HashMap<String, AccountAttributes> accounts = data.accounts;
        for (AccountAttributes expectedAccount : accounts.values()) {
            verifyPresentInDatastore(expectedAccount);
        }

        HashMap<String, CourseAttributes> courses = data.courses;
        for (CourseAttributes expectedCourse : courses.values()) {
            verifyPresentInDatastore(expectedCourse);
        }
        
        HashMap<String, InstructorAttributes> instructors = data.instructors;
        for (InstructorAttributes expectedInstructor : instructors.values()) {
            verifyPresentInDatastore(expectedInstructor);
        }

        HashMap<String, StudentAttributes> students = data.students;
        for (StudentAttributes expectedStudent : students.values()) {
            verifyPresentInDatastore(expectedStudent);
        }

    }


    private void verifyPresentInDatastore(StudentAttributes expectedStudent) {
        String studentJsonString = "null";
        while (studentJsonString.equals("null")) {
            studentJsonString = BackDoor.getStudentAsJson(expectedStudent.course, expectedStudent.email);
        }
        StudentAttributes actualStudent = gson.fromJson(studentJsonString,
                StudentAttributes.class);
        equalizeIrrelevantData(expectedStudent, actualStudent);
        expectedStudent.lastName = StringHelper.splitName(expectedStudent.name)[1];
        assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
    }

    private void verifyPresentInDatastore(CourseAttributes expectedCourse) {
        String courseJsonString = "null";
        while (courseJsonString.equals("null")) {
            courseJsonString = BackDoor.getCourseAsJson(expectedCourse.id);
        }
        CourseAttributes actualCourse = gson.fromJson(courseJsonString,
                CourseAttributes.class);
        // Ignore time field as it is stamped at the time of creation in testing
        actualCourse.createdAt = expectedCourse.createdAt;
        assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
    }

    private void verifyPresentInDatastore(InstructorAttributes expectedInstructor) {
        String instructorJsonString = "null";
        while (instructorJsonString.equals("null")) {
            instructorJsonString = BackDoor.getInstructorAsJsonByEmail(expectedInstructor.email, expectedInstructor.courseId);
        }
        InstructorAttributes actualInstructor = gson.fromJson(instructorJsonString, InstructorAttributes.class);
        
        equalizeIrrelevantData(expectedInstructor, actualInstructor);
        assertEquals(gson.toJson(expectedInstructor), gson.toJson(actualInstructor));
    }
    
    private void verifyPresentInDatastore(AccountAttributes expectedAccount) {
        String accountJsonString = BackDoor.getAccountAsJson(expectedAccount.googleId);
        AccountAttributes actualAccount = gson.fromJson(accountJsonString, AccountAttributes.class);
        // Ignore time field as it is stamped at the time of creation in testing
        actualAccount.createdAt = expectedAccount.createdAt;
        
        if (expectedAccount.studentProfile == null) {
            expectedAccount.studentProfile = new StudentProfileAttributes();
            expectedAccount.studentProfile.googleId = expectedAccount.googleId;
        }
        expectedAccount.studentProfile.modifiedDate = actualAccount.studentProfile.modifiedDate;
        assertEquals(gson.toJson(expectedAccount), gson.toJson(actualAccount));
    }
    
    private void equalizeIrrelevantData(
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
    
    private void equalizeIrrelevantData(
            InstructorAttributes expectedInstructor,
            InstructorAttributes actualInstructor) {
        
        // pretend keys match because the key is generated only before storing into database
        if ((actualInstructor.key != null)) {
            expectedInstructor.key = actualInstructor.key;
        }
    }

    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
