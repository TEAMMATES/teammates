package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.output.StudentsData;
import teammates.ui.webapi.GetStudentsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetStudentsAction}.
 */
public class GetStudentsActionTest extends BaseActionTest<GetStudentsAction> {
    private Instructor stubInstructorWithoutPrivileges;
    private Instructor stubInstructorWithAllPrivileges;
    private Instructor stubInstructorWithOnlyViewSectionPrivileges;
    private Instructor stubInstructorWithOnlyViewPrivilegesForDifferentSection;
    private Instructor stubInstructorWithCourseLevelPrivilege;
    private Course stubCourse;
    private Team stubTeamOne;
    private Student stubStudentOne;
    private Student stubStudentTwo;
    private List<Student> stubStudentListSectionOneTeamOne;
    private List<Student> stubStudentListSectionOne;
    private List<Student> stubStudentListAll;
    private List<Student> stubStudentListSectionTwo;
    private InstructorPrivileges sectionPrivilegesOnly;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    protected void setUp() {
        stubCourse = getTypicalCourse();

        // Instructor with co-owner privileges (course and section privileges)
        stubInstructorWithAllPrivileges = getTypicalInstructor();

        // Instructor without any privileges
        InstructorPrivileges customInstructorPrivileges1 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        stubInstructorWithoutPrivileges = getTypicalInstructor();
        stubInstructorWithoutPrivileges.setPrivileges(customInstructorPrivileges1);

        // Instructor with only privilege to view students in sections they are in
        sectionPrivilegesOnly =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        sectionPrivilegesOnly.updatePrivilege("section-1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        stubInstructorWithOnlyViewSectionPrivileges = getTypicalInstructor();
        stubInstructorWithOnlyViewSectionPrivileges.setPrivileges(sectionPrivilegesOnly);

        // Instructor with privilege to view students in sections other than the one the student is in
        InstructorPrivileges customInstructorPrivileges2 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        customInstructorPrivileges2.updatePrivilege("random-1",
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        stubInstructorWithOnlyViewPrivilegesForDifferentSection = getTypicalInstructor();
        stubInstructorWithOnlyViewPrivilegesForDifferentSection.setPrivileges(customInstructorPrivileges2);

        stubInstructorWithOnlyViewSectionPrivileges.setAccount(getTypicalAccount());
        stubInstructorWithoutPrivileges.setAccount(getTypicalAccount());
        stubInstructorWithAllPrivileges.setAccount(getTypicalAccount());

        // Instructor with privilege to view students in sections at course level
        InstructorPrivileges customInstructorPrivileges3 =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        customInstructorPrivileges3.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        stubInstructorWithCourseLevelPrivilege = getTypicalInstructor();
        stubInstructorWithCourseLevelPrivilege.setPrivileges(customInstructorPrivileges3);

        // Section one
        Section stubSection = new Section(stubCourse, "section-1");
        stubTeamOne = new Team(stubSection, "team-1");
        Team stubTeamTwo = new Team(stubSection, "team-2");
        stubStudentListSectionOneTeamOne = new ArrayList<>();
        stubStudentListSectionOne = new ArrayList<>();

        //Section two
        Section stubSectionTwo = new Section(stubCourse, "section-2");
        Team stubTeamThree = new Team(stubSectionTwo, "team-3");
        stubStudentListSectionTwo = new ArrayList<>();

        stubStudentListAll = new ArrayList<>();

        // Students in section one
        stubStudentOne = getTypicalStudent();
        stubStudentOne.setTeam(stubTeamOne);
        stubStudentTwo = new Student(stubCourse, "student-2", "student2@teammates.tmt", "comments", stubTeamTwo);
        Student stubStudentThree = new Student(stubCourse, "student-3", "student3@teammates.tmt", "comments", stubTeamTwo);
        stubStudentListSectionOne.add(stubStudentOne);
        stubStudentListSectionOne.add(stubStudentTwo);
        stubStudentListSectionOne.add(stubStudentThree);
        stubStudentListSectionOneTeamOne.add(stubStudentOne);

        // Students in section two
        Student stubStudentFour = new Student(stubCourse, "student-4", "student4@teammates.tmt", "comments", stubTeamThree);
        stubStudentListSectionTwo.add(stubStudentFour);

        // Students in the entire Course
        stubStudentListAll.add(stubStudentOne);
        stubStudentListAll.add(stubStudentTwo);
        stubStudentListAll.add(stubStudentThree);
        stubStudentListAll.add(stubStudentFour);
        reset(mockLogic);
        logoutUser();
    }

    /**
     * Enum to represent the type of user.
     */
    enum Type {
        INSTRUCTOR, STUDENT
    }

    @Test
    void testExecute_invalidParameters_throwsInvalidHttpParameterException() {
        String[] params1 = {};
        verifyHttpParameterFailure(params1);
        String[] params2 = {
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        verifyHttpParameterFailure(params2);
    }

    @Test
     void testExecute_instructorWithPermission_success() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithAllPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithAllPrivileges);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListAll);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        verifyStudentsData(stubStudentListAll, actualStudentsData, Type.INSTRUCTOR);
        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
    }

    @Test
    void testExecute_instructorWithSameSectionPrivilegesAsStudents_success() {
        loginAsInstructor(stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithOnlyViewSectionPrivileges
                .getGoogleId())).thenReturn(stubInstructorWithOnlyViewSectionPrivileges);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListAll);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action1 = getAction(params);
        JsonResult jsonResult1 = getJsonResult(action1);
        StudentsData actualStudentsData1 = (StudentsData) jsonResult1.getOutput();

        // Section two students are not in the StudentsData
        verifyStudentsData(stubStudentListSectionOne, actualStudentsData1, Type.INSTRUCTOR);

        sectionPrivilegesOnly.updatePrivilege("section-1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, false);
        sectionPrivilegesOnly.updatePrivilege("section-2", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        GetStudentsAction action2 = getAction(params);
        JsonResult jsonResult2 = getJsonResult(action2);
        StudentsData actualStudentsData2 = (StudentsData) jsonResult2.getOutput();

        // Section One students are not in the StudentsData
        verifyStudentsData(stubStudentListSectionTwo, actualStudentsData2, Type.INSTRUCTOR);

        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
        verify(mockLogic, times(2)).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_instructorWithOnlyViewSectionCourseLevelPrivilege_success() {
        loginAsInstructor(stubInstructorWithCourseLevelPrivilege.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), stubInstructorWithCourseLevelPrivilege.getGoogleId()))
                .thenReturn(stubInstructorWithCourseLevelPrivilege);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListAll);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        verifyStudentsData(stubStudentListAll, actualStudentsData, Type.INSTRUCTOR);
        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
    }

    @Test
    void testExecute_instructorWithDifferentSectionPrivilegesAsStudents_emptyList() {
        loginAsInstructor(stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId());
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId()))
                .thenReturn(stubInstructorWithOnlyViewPrivilegesForDifferentSection);
        when(mockLogic.getStudentsForCourse(stubCourse.getId())).thenReturn(stubStudentListAll);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        assertEquals(0, actualStudentsData.getStudents().size());
        verify(mockLogic, never()).getStudentsByTeamName(null, stubCourse.getId());
        verify(mockLogic, times(1)).getStudentsForCourse(stubCourse.getId());
    }

    @Test
    void testExecute_student_success() {
        loginAsStudent(stubStudentOne.getGoogleId());
        when(mockLogic.getStudentsByTeamName(stubStudentOne.getTeam().getName(),
                stubStudentOne.getCourse().getId())).thenReturn(stubStudentListSectionOneTeamOne);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        GetStudentsAction action = getAction(params);
        JsonResult jsonResult = getJsonResult(action);
        StudentsData actualStudentsData = (StudentsData) jsonResult.getOutput();

        verifyStudentsData(stubStudentListSectionOneTeamOne, actualStudentsData, Type.STUDENT);
        verify(mockLogic, times(1))
                .getStudentsByTeamName(stubStudentOne.getTeamName(), stubCourse.getId());
        verify(mockLogic, never()).getStudentsForCourse(stubCourse.getId());
    }

    private void verifyStudentsData(List<Student> expectedStudents, StudentsData actualStudentsData, Type type) {
        assertEquals(expectedStudents.size(), actualStudentsData.getStudents().size());

        for (int i = 0; i < expectedStudents.size(); i++) {
            assertEquals(expectedStudents.get(i).getCourseId(), actualStudentsData.getStudents().get(i).getCourseId());
            assertEquals(expectedStudents.get(i).getTeamName(), actualStudentsData.getStudents().get(i).getTeamName());
            assertEquals(expectedStudents.get(i).getSectionName(), actualStudentsData.getStudents().get(i).getSectionName());
            assertEquals(expectedStudents.get(i).getEmail(), actualStudentsData.getStudents().get(i).getEmail());
            assertEquals(expectedStudents.get(i).getName(), actualStudentsData.getStudents().get(i).getName());
            assertEquals(expectedStudents.get(i).getGoogleId(), actualStudentsData.getStudents().get(i).getGoogleId());
            assertNull(actualStudentsData.getStudents().get(i).getKey());
            if (type == Type.INSTRUCTOR) {
                assertEquals(expectedStudents.get(i).getComments(), actualStudentsData.getStudents().get(i).getComments());
            } else {
                assertNull(actualStudentsData.getStudents().get(i).getJoinState());
                assertNull(actualStudentsData.getStudents().get(i).getComments());
            }
        }
    }

    // 1번 메서드: 모든 접근 권한이 있는 교육자만 학생 목록에 접근 가능(교육자 전용 경로)
    @Test
    void testSpecificAccessControl_loggedInInstructorWithPrivileges_canAccess() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());  // 모든 권한을 가진 교육자로 로그

        // 코스 아이디랑 구글 아이디로 교육자를 조회할 때, 모든 권한을 가진 교육자를 반환
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithAllPrivileges.getGoogleId())).thenReturn(stubInstructorWithAllPrivileges);

        // 코스 정보 조회 시 유효한 코스 반환
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        // 요청 파라미터로 코스 아이디만 전달
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);  // 접근 가능

        // 학생 조회 API는 호출되지 않았다는 것 확인
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(),
                stubInstructorWithAllPrivileges.getGoogleId());

        // 교육자 조회 API는 정확히 한 번 호출되었다는 것 확인
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithAllPrivileges.getGoogleId());
    }

    // 2번 메서드: 권한이 없는 교육자는 학생 목록에 접근 불가능(교육자 전용 경로)
    @Test
    void testSpecificAccessControl_loggedInInstructorWithoutPrivileges_cannotAccess() {

        // 권한이 없는 교육자로 로그인
        loginAsInstructor(stubInstructorWithoutPrivileges.getGoogleId());

        // 코스 아이디와 구글 아이디로 교육자를 조회할 때, 권한이 없는 교육자를 반환
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithoutPrivileges.getGoogleId())).thenReturn(stubInstructorWithoutPrivileges);

        // 코스 정보 조회 시 유효한 코스 반환
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        // 요청 파라미터로 코스 아이디만 전달
        String[] params1 = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params1);  // 접근 불가능

        // 학생 조회 API는 호출되지 않았다는 것 확인
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(),
                stubInstructorWithoutPrivileges.getGoogleId());

        // 교육자 조회 API는 정확히 한 번 호출되었다는 것 확인
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithoutPrivileges.getGoogleId());
    }


    // 3번 메서드: 각 학생들로 구성된 팀원을 조회할 때는 어떤 교육자도 접근 불가능(교육자 전용 경로)
    @Test
    void testSpecificAccessControl_instructorWithTeamParams_cannotAccess() {
        // 접근 권한이 없는 교육자로 로그인
        loginAsInstructor(stubInstructorWithoutPrivileges.getGoogleId());

        // 코스 아이디와 교육자의 아이디로 학생 조회 시 Null 값을 반환
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubInstructorWithoutPrivileges.getGoogleId()))
                .thenReturn(null);

        // 요청 파라미터로 코스 아이디와 팀명을 전달
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        verifyCannotAccess(params);  // 접근 불가능

        logoutUser();  // 교육자 로그아웃

        // 모든 권한을 가진 교육자로 로그인
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());

        // 코스 아이디와 교육자의 아이디로 학생 조회 시 Null 값을 반환
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), stubInstructorWithAllPrivileges.getGoogleId()))
                .thenReturn(null);
        verifyCannotAccess(params);  // 접근 불가능

        // 교육자 조회 API는 호출되지 않았다는 것 확인
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithoutPrivileges.getGoogleId());
    }

    // 4번 메서드: 섹션 단위의 학생 조회 권한만 가진 교육자는 학생 목록에 접근 가능(교육자 전용 경로)
    @Test
    void testSpecificAccessControl_loggedInInstructorWithOnlyViewSectionPrivileges_canAccess() {

        // 섹션 단위의 학생 조회 권한만 가진 교육자로 로그인
        loginAsInstructor(stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());

        // 코스 아이디와 교육자의 아이디로 조회하면 섹션 단위의 학생 조회 권한만 가진 교육자 반환
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewSectionPrivileges.getGoogleId()))
                .thenReturn(stubInstructorWithOnlyViewSectionPrivileges);

        // 코스 정보 조회 시 유효한 코스 반환
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        // 파라미터로 코스 아이디만 전달
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);  // 접근 가능

        // 학생 조회 API는 호출되지 않은 것 확인
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());

        // 교육자 조회 API는 한 번 호출된 것 확인
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewSectionPrivileges.getGoogleId());
    }

    // 5번 메서드: 코스 레벨 권한만 가지고 있는 교육자는 학생 목록 접근 가능(교육자 전용 경로)
    @Test
    void testSpecificAccessControl_loggedInInstructorWithOnlyViewSectionCourseLevelPrivilege_canAccess() {

        // 코스 권한이 있는 교육자로 로그인
        loginAsInstructor(stubInstructorWithCourseLevelPrivilege.getGoogleId());

        // 코스 아이디와 교육자의 아이디로 조회 시 코스 권한이 있는 교육자 반환
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithCourseLevelPrivilege.getGoogleId()))
                .thenReturn(stubInstructorWithCourseLevelPrivilege);

        // 코스 정보 조회 시 유효한 코스 반환
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        // 파라미터로 코스 아이디만 전달
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);  // 접근 가능

        // 학생 조회 API는 한 번도 호출되는 않은 것 확인
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(),
                stubInstructorWithCourseLevelPrivilege.getGoogleId());

        // 교육자 조회 API는 한 번 호출된 것 확인
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithCourseLevelPrivilege.getGoogleId());
    }

    // 6번 메서드: 다른 섹션에 대한 접근 권한만 가지고 있는 교육자는 학생 목록에 접근 가능
    @Test
    void testSpecificAccessControl_instructorWithDifferentSectionPrivilegesAsStudents_canAccess() {

        // 다른 섹션에 대한 접근 권한만 가지고 있는 교육자로 로그인
        loginAsInstructor(stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId());

        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(),
                stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId()))
                .thenReturn(stubInstructorWithOnlyViewPrivilegesForDifferentSection);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        // 파라미터로 코스 아이디만 전달
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);  // 접근 가능
    }

    @Test
    void testAccessControl_student_sameTeam_canAccess() {
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "student-googleId"))
                .thenReturn(stubStudentOne);

        verifyStudentsCanAccess(
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME,  stubStudentOne.getTeam().getName()
        );
    }

    @Test
    void testAccessControl_student_otherTeam_cannotAccess() {
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "student-googleId"))
                .thenReturn(stubStudentOne);
        verifyStudentsCannotAccess(
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME,  stubStudentTwo.getTeam().getName()
        );
    }

    @Test
    void testAccessControl_student_otherCourse_cannotAccess() {
        when(mockLogic.getStudentByGoogleId("another-course-id", "student-googleId"))
                .thenReturn(null);
        verifyStudentsCannotAccess(
                Const.ParamsNames.COURSE_ID, "another-course-id",
                Const.ParamsNames.TEAM_NAME,  stubStudentTwo.getTeam().getName()
        );
    }

    // 10번 메서드: 로그인되지 않은 사용자는 학생 목록에 접근 불가능(로그인 X 전용 경로)
    @Test
    void testSpecificAccessControl_userNotLoggedIn_cannotAccess() {
        logoutUser();

        String[] params1 = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubTeamOne.getName(),
        };
        verifyCannotAccess(params1);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(), null);
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(), null);

        String[] params2 = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
        };
        verifyCannotAccess(params2);

        verify(mockLogic, never()).getInstructorByGoogleId(stubStudentOne.getCourseId(), null);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(), null);
    }

    @Test
    void testAccessControl_admin_teamParams_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentTwo.getTeam().getName(),
        };
        verifyAdminsCannotAccess(params);
        verifyWithoutLoginCannotAccess(params);
    }

    // 12번 메서드: 미등록으로 로그인했더라도 코스에 등록된 학생이라면 팀원 접근 가능(미등록 전용 경로)
    @Test
    void testSpecificAccessControl_loginAsUnregisteredValidStudent_canAccess() {
        loginAsUnregistered("unregistered-student");
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "unregistered-student")).thenReturn(stubStudentOne);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        verifyCanAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, times(1)).getStudentByGoogleId(stubCourse.getId(), "unregistered-student");
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(), "unregistered-student");
    }

    // 13번 메서드: 미등록으로 로그인하고, 코스에 등록되지 않은 학생이라면 팀원 접근 불가능(미등록 전용 경로)
    @Test
    void testSpecificAccessControl_loginAsUnregisteredInvalidStudent_cannotAccess() {
        loginAsUnregistered("unregistered-student");
        when(mockLogic.getStudentByGoogleId(stubCourse.getId(), "unregistered-student")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubStudentOne.getCourse().getId(),
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, times(1)).getStudentByGoogleId(stubCourse.getId(), "unregistered-student");
        verify(mockLogic, never()).getInstructorByGoogleId(stubCourse.getId(), "unregistered-student");
    }

    // 14번 메서드: 미등록된 교육자라도 학생 조회 권한이 있는 경우 학생 목록에 접근 가능(미등록 전용 경로)
    @Test
    void testSpecificAccessControl_loginAsUnregisteredValidInstructor_canAccess() {
        loginAsUnregistered("unregistered-instructor");
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "unregistered-instructor"))
                .thenReturn(stubInstructorWithAllPrivileges);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);
        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCanAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(), "unregistered-instructor");
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(), "unregistered-instructor");
    }

    // 15번 메서드: 미등록된 교육자인데 학생 조회 권한까지 앖는 경우 학생 목록에 접근 불가능
    @Test
    void testSpecificAccessControl_loginAsUnregisteredInvalidInstructor_cannotAccess() {
        loginAsUnregistered("unregistered-instructor");
        when(mockLogic.getInstructorByGoogleId(stubCourse.getId(), "unregistered-instructor")).thenReturn(null);
        when(mockLogic.getCourse(stubCourse.getId())).thenReturn(stubCourse);

        String[] params = {
                Const.ParamsNames.COURSE_ID, stubCourse.getId(),
        };
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, never()).getStudentByGoogleId(stubCourse.getId(), "unregistered-instructor");
        verify(mockLogic, times(1)).getInstructorByGoogleId(stubCourse.getId(), "unregistered-instructor");
    }

    // 16번 메서드: 파라미터가 잘못된 경우, 접근 불가능
    @Test
    void testSpecificAccessControl_invalidParams_throwsInvalidHttpParameterException() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());

        String[] params = {};
        verifyHttpParameterFailure(params);

        String[] params2 = {
                Const.ParamsNames.TEAM_NAME, stubStudentOne.getTeam().getName(),
        };
        verifyHttpParameterFailure(params2);
    }

    // 17번 메서드: 잘못된 코스 아이디로 접근 시 접근 불가능(교육자 전용 경로)
    @Test
    void testSpecificAccessControl_invalidCourse_cannotAccess() {
        loginAsInstructor(stubInstructorWithAllPrivileges.getGoogleId());
        when(mockLogic.getInstructorByGoogleId("invalid-course-id", stubInstructorWithAllPrivileges.getGoogleId()))
                .thenReturn(null);
        when(mockLogic.getCourse("invalid-course-id")).thenReturn(null);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
        verify(mockLogic, never()).getStudentByGoogleId("invalid-course-id",
                stubInstructorWithAllPrivileges.getGoogleId());
        verify(mockLogic, times(1)).getInstructorByGoogleId("invalid-course-id",
                stubInstructorWithAllPrivileges.getGoogleId());
    }

    // 18번 메서드: 다른 섹션의 학생 목록을 볼 수 있는 권한이 있는 교육자는 다른 코스의 학생 목록에 접근 불가능(교육자 전용 경로)
    @Test
    void testSpecificAccessControl_instructorDifferentCourseAsStudent_cannotAccess() {
        loginAsInstructor(stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId());
        when(mockLogic.getInstructorByGoogleId("another-course-id",
                stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId()))
                .thenReturn(stubInstructorWithOnlyViewPrivilegesForDifferentSection);
        stubCourse.setId("another-course-id");
        when(mockLogic.getCourse("another-course-id")).thenReturn(stubCourse);

        String[] params = {
                Const.ParamsNames.COURSE_ID, "another-course-id",
        };
        verifyCannotAccess(params);

        verify(mockLogic, never()).getStudentByGoogleId("another-course-id",
                stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId());
        verify(mockLogic, times(1)).getInstructorByGoogleId("another-course-id",
                stubInstructorWithOnlyViewPrivilegesForDifferentSection.getGoogleId());
    }
}
