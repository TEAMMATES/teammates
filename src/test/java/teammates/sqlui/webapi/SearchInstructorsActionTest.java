package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.argThat;
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
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.output.JoinState;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.SearchInstructorsAction;

/**
 * SUT: {@link SearchInstructorsAction}.
 */
public class SearchInstructorsActionTest extends BaseActionTest<SearchInstructorsAction> {

    private String searchKey = "search-key";
    private List<Instructor> instructors;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        Mockito.reset(mockLogic);

        Instructor instructor1 = getTypicalInstructor();

        Instructor instructor2 = getTypicalInstructor();
        Account instructor2Account = getTypicalAccount();
        instructor2.setName("instructor2-name");
        instructor2.setAccount(instructor2Account);
        instructor2.setEmail(instructor2Account.getEmail());
        instructor2.setDisplayedToStudents(true);

        instructors = List.of(instructor1, instructor2);
    }

    @Test
    void testExecute_searchInstructors_success() throws SearchServiceException {
        when(mockLogic.searchInstructorsInWholeSystem(searchKey)).thenReturn(instructors);
        for (Instructor instructor : instructors) {
            when(mockLogic.getCourseInstitute(instructor.getCourseId())).thenReturn(instructor.getCourse().getInstitute());
        }

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchInstructorsAction action = getAction(params);
        InstructorsData instructorsData = (InstructorsData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).searchInstructorsInWholeSystem(searchKey);
        verify(mockLogic, times(instructors.size())).getCourseInstitute(argThat(courseId ->
                instructors.stream().map(Instructor::getCourseId).anyMatch(id -> id.equals(courseId))
        ));
        verifyNoMoreInteractions(mockLogic);

        assertEquals(instructors.size(), instructorsData.getInstructors().size());

        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            InstructorData instructorData = instructorsData.getInstructors().get(i);

            assertEquals(instructor.getGoogleId(), instructorData.getGoogleId());
            assertEquals(instructor.getCourseId(), instructorData.getCourseId());
            assertEquals(instructor.getEmail(), instructorData.getEmail());
            assertEquals(instructor.isDisplayedToStudents(), instructorData.getIsDisplayedToStudents());
            assertEquals(instructor.getDisplayName(), instructorData.getDisplayedToStudentsAs());
            assertEquals(instructor.getName(), instructorData.getName());
            assertEquals(instructor.getRole(), instructorData.getRole());
            assertEquals(
                    instructor.getAccount() == null ? JoinState.NOT_JOINED : JoinState.JOINED,
                    instructorData.getJoinState()
            );
            assertEquals(instructor.getRegKey(), instructorData.getKey());
            assertEquals(instructor.getCourse().getInstitute(), instructorData.getInstitute());
        }
    }

    @Test
    void testExecute_searchInstructorsNoMatch_success() throws SearchServiceException {
        when(mockLogic.searchInstructorsInWholeSystem(searchKey)).thenReturn(List.of());

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchInstructorsAction action = getAction(params);
        InstructorsData instructorsData = (InstructorsData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).searchInstructorsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        assertEquals(0, instructorsData.getInstructors().size());
    }

    @Test
    void testExecute_searchServiceException_failure() throws SearchServiceException {
        when(mockLogic.searchInstructorsInWholeSystem(searchKey))
                .thenThrow(new SearchServiceException("Search service error", 500));

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchInstructorsAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action, 500).getOutput();

        verify(mockLogic, times(1)).searchInstructorsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        assertEquals("Search service error", actionOutput.getMessage());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructor_cannotAccess() {
        loginAsInstructor("instructor-googleId");

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent("student-googleId");

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        verifyCannotAccess(params);
    }
}
