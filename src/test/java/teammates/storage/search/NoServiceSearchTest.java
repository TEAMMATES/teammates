package teammates.storage.search;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.exception.SearchNotImplementedException;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.test.TestProperties;

/**
 * SUT: {@link InstructorsDb}, {@link StudentsDb}.
 */
public class NoServiceSearchTest extends BaseSearchTest {

    private InstructorsDb instructorsDb = new InstructorsDb();
    private StudentsDb studentsDb = new StudentsDb();

    @Test
    public void allTests() throws Exception {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        assertThrows(SearchNotImplementedException.class,
                () -> instructorsDb.searchInstructorsInWholeSystem("anything"));
        assertThrows(SearchNotImplementedException.class,
                () -> studentsDb.search("anything", new ArrayList<>()));
        assertThrows(SearchNotImplementedException.class,
                () -> studentsDb.searchStudentsInWholeSystem("anything"));
    }

}
