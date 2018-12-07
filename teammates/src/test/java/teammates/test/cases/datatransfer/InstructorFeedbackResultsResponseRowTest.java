package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

/**
 * SUT: {@link InstructorFeedbackResultsResponseRow}.
 */
public class InstructorFeedbackResultsResponseRowTest extends BaseTestCase {

    @Test
    public void testDefaultSortOrderWithSameTeam() {
        List<InstructorFeedbackResultsResponseRow> responseRows = new ArrayList<>();
        responseRows.add(createNewFeedbackResponseRow("Team1", "Alice Betsy"));
        responseRows.add(createNewFeedbackResponseRow("Team1", "Emma Farrell"));
        responseRows.add(createNewFeedbackResponseRow("Team1", "Benny Charles"));
        responseRows.add(createNewFeedbackResponseRow("Team1", "Francis Gabriel"));

        List<InstructorFeedbackResultsResponseRow> sortedList = InstructorFeedbackResultsResponseRow
                .sortListWithDefaultOrder(responseRows);

        assertEquals("Alice Betsy", sortedList.get(0).getGiverDisplayableIdentifier());
        assertEquals("Benny Charles", sortedList.get(1).getGiverDisplayableIdentifier());
        assertEquals("Emma Farrell", sortedList.get(2).getGiverDisplayableIdentifier());
        assertEquals("Francis Gabriel", sortedList.get(3).getGiverDisplayableIdentifier());
    }

    @Test
    public void testDefaultSortOrderWithDifferentTeams() {
        List<InstructorFeedbackResultsResponseRow> responseRows = new ArrayList<>();
        responseRows.add(createNewFeedbackResponseRow("Team1", "Alice Betsy"));
        responseRows.add(createNewFeedbackResponseRow("Team2", "Emma Farrell"));
        responseRows.add(createNewFeedbackResponseRow("Team3", "Benny Charles"));
        responseRows.add(createNewFeedbackResponseRow("Team1", "Francis Gabriel"));

        List<InstructorFeedbackResultsResponseRow> sortedList = InstructorFeedbackResultsResponseRow
                .sortListWithDefaultOrder(responseRows);

        assertEquals("Alice Betsy", sortedList.get(0).getGiverDisplayableIdentifier());
        assertEquals("Francis Gabriel", sortedList.get(1).getGiverDisplayableIdentifier());
        assertEquals("Emma Farrell", sortedList.get(2).getGiverDisplayableIdentifier());
        assertEquals("Benny Charles", sortedList.get(3).getGiverDisplayableIdentifier());

    }

    private InstructorFeedbackResultsResponseRow createNewFeedbackResponseRow(String giverTeam,
            String giverDisplayableIdentifier) {
        return new InstructorFeedbackResultsResponseRow(giverDisplayableIdentifier, giverTeam, null, null,
                null, null, false);
    }

}
