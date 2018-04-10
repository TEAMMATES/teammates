package teammates.test.cases.pagedata;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.InstructorCourseStudentDetailsPageData;
import teammates.ui.template.StudentInfoTable;
import teammates.ui.template.StudentProfile;

/**
 * SUT: {@link InstructorCourseStudentDetailsPageData}.
 */
public class InstructorCourseStudentDetailsPageDataTest extends BaseTestCase {

    private StudentAttributes inputStudent;
    private StudentProfileAttributes inputStudentProfile;
    private String pictureUrl;
    private boolean hasSection;

    @Test
    public void allTests() {
        ______TS("With picture key");
        String pictureKey = "examplePictureKey";
        createStudentData(pictureKey);
        InstructorCourseStudentDetailsPageData data = createData();
        testData(data);

        ______TS("With empty picture key");
        pictureKey = "";
        createStudentData(pictureKey);
        data = createData();
        testData(data);

        ______TS("With null picture key");
        createStudentData(null);
        data = createData();
        testData(data);
    }

    private void testData(InstructorCourseStudentDetailsPageData data) {
        testStudentProfile(data.getStudentProfile());
        testStudentInfoTable(data.getStudentInfoTable());
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

    private void testStudentInfoTable(StudentInfoTable studentInfoTable) {
        assertNotNull(studentInfoTable);

        assertEquals(inputStudent.name, studentInfoTable.getName());
        assertEquals(inputStudent.email, studentInfoTable.getEmail());
        assertEquals(inputStudent.section, studentInfoTable.getSection());
        assertEquals(inputStudent.team, studentInfoTable.getTeam());
        assertEquals(inputStudent.comments, studentInfoTable.getComments());
        assertEquals(inputStudent.course, studentInfoTable.getCourse());
        assertEquals(hasSection, studentInfoTable.getHasSection());
    }

    private void createStudentData(String pictureKey) {
        String name = "John Doe";
        String email = "john@doe.com";

        createStudent(name, email);
        createStudentProfile(email, pictureKey);
    }

    private void createStudent(String name, String email) {
        String comments = "This is a comment for John Doe.";
        String courseId = "CourseForJohnDoe";
        String team = "TeamForJohnDoe";
        String section = "SectionForJohnDoe";

        inputStudent = StudentAttributes
                .builder(courseId, name, email)
                .withSection(section)
                .withTeam(team)
                .withComments(comments)
                .build();
    }

    private void createStudentProfile(String email, String pictureKey) {
        String googleId = "valid.googleId";
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

        inputStudentProfile = StudentProfileAttributes.builder(googleId)
                .withShortName(shortName)
                .withEmail(email)
                .withInstitute(institute)
                .withNationality(nationality)
                .withGender(gender)
                .withMoreInfo(moreInfo)
                .withPictureKey(pictureKey)
                .build();
    }

    private InstructorCourseStudentDetailsPageData createData() {
        createCommonData();

        return new InstructorCourseStudentDetailsPageData(AccountAttributes.builder().build(), dummySessionToken,
                inputStudent, inputStudentProfile, hasSection);
    }

    private void createCommonData() {
        hasSection = true;
    }
}
