package teammates.test.cases.ui.pagedata;

import java.util.Arrays;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCourseStudentDetailsPageData;
import teammates.ui.template.StudentInfoTable;
import teammates.ui.template.StudentProfile;

public class InstructorCourseStudentDetailsPageDataTest extends BaseTestCase {
    private static final String[] USERS_COMMENT_BOX_SHOWN_TO = {"student", "team", "section"};
    
    private StudentAttributes inputStudent;
    private StudentProfileAttributes inputStudentProfile;
    private String pictureUrl;
    private boolean isAbleToAddComment;
    private boolean hasSection;
    private String commentRecipient;

    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
    }
    
    @Test
    public void allTests() {
        ______TS("With picture key, no comment recipient");
        String pictureKey = "examplePictureKey";
        createStudentData(pictureKey);
        InstructorCourseStudentDetailsPageData data = createData();
        testData(data);
        
        ______TS("With empty picture key, no comment recipient");
        pictureKey = "";
        createStudentData(pictureKey);
        data = createData();
        testData(data);
        
        ______TS("With null picture key, no comment recipient");
        pictureKey = null;
        createStudentData(pictureKey);
        data = createData();
        testData(data);
        
        ______TS("With comment recipient unauthorised to see comment box");
        data = createData("someOtherCommentRecipient");
        testData(data);
        
        ______TS("With comment recipient authorised to see comment box");
        for (String user : USERS_COMMENT_BOX_SHOWN_TO) {
            data = createData(user);
            testData(data);
        }
    }
    
    private void testData(InstructorCourseStudentDetailsPageData data) {
        testStudentProfile(data.getStudentProfile());
        testStudentInfoTable(data.getStudentInfoTable());
        testCommentRecipient(data.getCommentRecipient(), data.isCommentBoxShown());
    }

    private void testCommentRecipient(String commentRecipient, boolean isCommentBoxShown) {
        assertEquals(this.commentRecipient, commentRecipient);
        
        if (Arrays.asList(USERS_COMMENT_BOX_SHOWN_TO).contains(commentRecipient)) {
            assertTrue(isCommentBoxShown);
        } else {
            assertFalse(isCommentBoxShown);
        }
    }

    private void testStudentProfile(StudentProfile studentProfile) {
        assertNotNull(studentProfile);
        
        assertNotNull(studentProfile.getPictureUrl());
        assertEquals(pictureUrl, studentProfile.getPictureUrl());
        assertEquals(inputStudent.name, studentProfile.getName());
        assertEquals(inputStudentProfile.shortName, studentProfile.getShortName());
        assertEquals(inputStudentProfile.gender, studentProfile.getGender());
        assertEquals(inputStudentProfile.email, studentProfile.getEmail());
        assertEquals(inputStudentProfile.institute, studentProfile.getInstitute());
        assertEquals(inputStudentProfile.nationality, studentProfile.getNationality());
        assertEquals(inputStudentProfile.moreInfo, studentProfile.getMoreInfo());
    }

    protected void testStudentInfoTable(StudentInfoTable studentInfoTable) {
        assertNotNull(studentInfoTable);
        
        assertEquals(inputStudent.name, studentInfoTable.getName());
        assertEquals(inputStudent.email, studentInfoTable.getEmail());
        assertEquals(inputStudent.section, studentInfoTable.getSection());
        assertEquals(inputStudent.team, studentInfoTable.getTeam());
        assertEquals(inputStudent.comments, studentInfoTable.getComments());
        assertEquals(inputStudent.course, studentInfoTable.getCourse());
        assertEquals(isAbleToAddComment, studentInfoTable.isAbleToAddComment());
        assertEquals(hasSection, studentInfoTable.getHasSection());
    }
    
    private void createStudentData(String pictureKey) {
        String name = "John Doe";
        String email = "john@doe.com";
        
        createStudent(name, email);
        createStudentProfile(email, pictureKey);
    }
    
    protected void createStudent(String name, String email) {
        String comments = "This is a comment for John Doe.";
        String courseId = "CourseForJohnDoe";
        String team = "TeamForJohnDoe";
        String section = "SectionForJohnDoe";
        
        inputStudent = new StudentAttributes(null, email, name, comments, courseId, team, section);
    }
    
    private void createStudentProfile(String email, String pictureKey) {
        String shortName = "John";
        String institute = "InstituteForJohnDoe";
        String nationality = "Singaporean";
        String gender = Const.GenderTypes.MALE;
        String moreInfo = "Information for John Doe.";
        
        if (pictureKey == null || pictureKey.isEmpty()) {
            this.pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
        } else {
            this.pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE + "?"
                            + Const.ParamsNames.BLOB_KEY + "=" + pictureKey + "&"
                            + Const.ParamsNames.USER_ID + "=null";
        }
        
        inputStudentProfile = new StudentProfileAttributes(
                null, shortName, email, institute, nationality, gender, moreInfo, pictureKey);
    }

    protected InstructorCourseStudentDetailsPageData createData() {
        createCommonData();
        
        return new InstructorCourseStudentDetailsPageData(new AccountAttributes(), inputStudent, inputStudentProfile,
                                                          isAbleToAddComment, hasSection, commentRecipient);
    }
    
    private InstructorCourseStudentDetailsPageData createData(String commentRecipient) {
        createCommonData();
        this.commentRecipient = commentRecipient;
        
        return new InstructorCourseStudentDetailsPageData(new AccountAttributes(), inputStudent, inputStudentProfile,
                                                          isAbleToAddComment, hasSection, commentRecipient);
    }
    
    private void createCommonData() {
        isAbleToAddComment = true;
        hasSection = true;
        commentRecipient = null;
    }
}
