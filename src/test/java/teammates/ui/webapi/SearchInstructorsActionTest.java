package teammates.ui.webapi;

import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.output.JoinState;

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
    void testExecute_searchInstructors_success() {
        when(mockLogic.searchInstructorsInWholeSystem(searchKey)).thenReturn(instructors);

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchInstructorsAction action = getAction(params);
        InstructorsData instructorsData = (InstructorsData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).searchInstructorsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        Assertions.assertEquals(instructors.size(), instructorsData.getInstructors().size());

        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            InstructorData instructorData = instructorsData.getInstructors().get(i);

            Assertions.assertEquals(instructor.getGoogleId(), instructorData.getGoogleId());
            Assertions.assertEquals(instructor.getCourseId(), instructorData.getCourseId());
            Assertions.assertEquals(instructor.getEmail(), instructorData.getEmail());
            Assertions.assertEquals(instructor.isDisplayedToStudents(), instructorData.getIsDisplayedToStudents());
            Assertions.assertEquals(instructor.getDisplayName(), instructorData.getDisplayedToStudentsAs());
            Assertions.assertEquals(instructor.getName(), instructorData.getName());
            Assertions.assertEquals(instructor.getRole(), instructorData.getRole());
            Assertions.assertEquals(
                    instructor.getAccount() == null ? JoinState.NOT_JOINED : JoinState.JOINED,
                    instructorData.getJoinState()
            );
            Assertions.assertEquals(instructor.getRegKey(), instructorData.getKey());
            Assertions.assertEquals(instructor.getCourse().getInstitute(), instructorData.getInstitute());
        }
    }

    @Test
    void testExecute_searchInstructorsNoMatch_success() {
        when(mockLogic.searchInstructorsInWholeSystem(searchKey)).thenReturn(List.of());

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchInstructorsAction action = getAction(params);
        InstructorsData instructorsData = (InstructorsData) getJsonResult(action).getOutput();

        verify(mockLogic, times(1)).searchInstructorsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        Assertions.assertEquals(0, instructorsData.getInstructors().size());
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };
        verifyOnlyAdminsCanAccess(params);
    }
}
