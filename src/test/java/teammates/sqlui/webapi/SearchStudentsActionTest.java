package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.JoinState;
import teammates.ui.output.MessageOutput;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;
import teammates.ui.webapi.SearchStudentsAction;

/**
 * SUT:{@link SearchStudentsAction}.
 */
public class SearchStudentsActionTest extends BaseActionTest<SearchStudentsAction> {

    private String searchKey = "search-key";
    private String instructorId = "instructor-id";
    private List<Student> students;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic);

        Student student1 = getTypicalStudent();

        Student student2 = getTypicalStudent();
        Account student2Account = getTypicalAccount();
        student2.setName("student2-name");
        student2.setAccount(student2Account);
        student2.setEmail(student2Account.getEmail());
        student2.setComments("comments2");
        student2.setTeam(getTypicalTeam());

        students = List.of(student1, student2);
    }

    private List<Instructor> setupInstructors() {
        Instructor instructor1 = getTypicalInstructor();
        Instructor instructor2 = getTypicalInstructor();
        return List.of(instructor1, instructor2);
    }

    private void verifyStudentsData(List<Student> expectedStudents, StudentsData actualStudentsData, boolean isAdmin) {
        assertEquals(expectedStudents.size(), actualStudentsData.getStudents().size());

        for (int i = 0; i < expectedStudents.size(); i++) {
            Student student = expectedStudents.get(i);
            StudentData studentData = actualStudentsData.getStudents().get(i);

            assertEquals(student.getId(), studentData.getStudentId());
            assertEquals(student.getEmail(), studentData.getEmail());
            assertEquals(student.getCourseId(), studentData.getCourseId());
            assertEquals(student.getName(), studentData.getName());
            assertEquals(student.getComments(), studentData.getComments());
            assertEquals(
                    student.getAccount() == null ? JoinState.NOT_JOINED : JoinState.JOINED,
                    studentData.getJoinState()
            );
            assertEquals(student.getTeamName(), studentData.getTeamName());
            assertEquals(student.getSectionName(), studentData.getSectionName());

            if (isAdmin) {
                assertEquals(student.getGoogleId(), studentData.getGoogleId());
                assertEquals(student.getRegKey(), studentData.getKey());
                assertEquals(student.getCourse().getInstitute(), studentData.getInstitute());
            } else {
                assertNull(studentData.getGoogleId());
                assertNull(studentData.getKey());
                assertNull(studentData.getInstitute());
            }
        }
    }

    @Test
    void testExecute_instructorSearchStudentsWithValidEntity_success() throws SearchServiceException {
        loginAsInstructor(instructorId);
        List<Instructor> instructors = setupInstructors();

        when(mockLogic.getInstructorsForGoogleId(instructorId)).thenReturn(instructors);
        when(mockLogic.searchStudents(searchKey, instructors)).thenReturn(students);

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        SearchStudentsAction action = getAction(params);
        StudentsData studentsData = (StudentsData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getInstructorsForGoogleId(instructorId);
        verify(mockLogic, times(1)).searchStudents(searchKey, instructors);
        verify(mockLogic, never()).searchStudentsInWholeSystem(any());
        verify(mockLogic, never()).getCourseInstitute(any());
        verifyNoMoreInteractions(mockLogic);

        verifyStudentsData(students, studentsData, false);
    }

    @Test
    void testExecute_instructorSearchStudentsWithValidEntityNoMatch_success() throws SearchServiceException {
        loginAsInstructor(instructorId);
        List<Instructor> instructors = setupInstructors();

        when(mockLogic.getInstructorsForGoogleId(instructorId)).thenReturn(instructors);
        when(mockLogic.searchStudents(searchKey, instructors)).thenReturn(List.of());

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        SearchStudentsAction action = getAction(params);
        StudentsData studentsData = (StudentsData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).getInstructorsForGoogleId(instructorId);
        verify(mockLogic, times(1)).searchStudents(searchKey, instructors);
        verify(mockLogic, never()).searchStudentsInWholeSystem(any());
        verify(mockLogic, never()).getCourseInstitute(any());
        verifyNoMoreInteractions(mockLogic);

        assertEquals(0, studentsData.getStudents().size());
    }

    @Test
    void testExecute_adminSearchStudentsWithValidEntity_success() throws SearchServiceException {
        loginAsAdmin();

        when(mockLogic.searchStudentsInWholeSystem(searchKey)).thenReturn(students);
        for (Student student : students) {
            when(mockLogic.getCourseInstitute(student.getCourseId())).thenReturn(student.getCourse().getInstitute());
        }

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        SearchStudentsAction action = getAction(params);
        StudentsData studentsData = (StudentsData) getJsonResult(action).getOutput();

        verify(mockLogic, never()).getInstructorsForGoogleId(any());
        verify(mockLogic, never()).searchStudents(any(), any());
        verify(mockLogic, times(1)).searchStudentsInWholeSystem(searchKey);
        verify(mockLogic, times(students.size())).getCourseInstitute(argThat(courseId ->
                students.stream().map(Student::getCourseId).anyMatch(id -> id.equals(courseId))
        ));
        verifyNoMoreInteractions(mockLogic);

        verifyStudentsData(students, studentsData, true);
    }

    @Test
    void testExecute_adminSearchStudentsWithValidEntityNoMatch_success() throws SearchServiceException {
        loginAsAdmin();

        when(mockLogic.searchStudentsInWholeSystem(searchKey)).thenReturn(List.of());

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        SearchStudentsAction action = getAction(params);
        StudentsData studentsData = (StudentsData) getJsonResult(action).getOutput();

        verify(mockLogic, never()).getInstructorsForGoogleId(any());
        verify(mockLogic, never()).searchStudents(any(), any());
        verify(mockLogic, times(1)).searchStudentsInWholeSystem(searchKey);
        verify(mockLogic, never()).getCourseInstitute(any());
        verifyNoMoreInteractions(mockLogic);

        assertEquals(0, studentsData.getStudents().size());
    }

    @Test
    void testExecute_instructorSearchStudentsWithInvalidEntity_throwsInvalidHttpParameterException() {
        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_adminSearchStudentsWithInvalidEntity_throwsInvalidHttpParameterException() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_searchServiceException_failure() throws SearchServiceException {
        loginAsAdmin();

        when(mockLogic.searchStudentsInWholeSystem(searchKey))
                .thenThrow(new SearchServiceException("Search service error", 500));

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        SearchStudentsAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action, 500).getOutput();

        verify(mockLogic, never()).getInstructorsForGoogleId(any());
        verify(mockLogic, never()).searchStudents(any(), any());
        verify(mockLogic, times(1)).searchStudentsInWholeSystem(searchKey);
        verify(mockLogic, never()).getCourseInstitute(any());
        verifyNoMoreInteractions(mockLogic);

        assertEquals("Search service error", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        loginAsAdmin();
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingSearchKey_throwsInvalidHttpParameterException() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingEntityType_throwsInvalidHttpParameterException() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructor_canAccess() {
        loginAsInstructor(instructorId);

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        verifyCannotAccess(params);
    }
}
