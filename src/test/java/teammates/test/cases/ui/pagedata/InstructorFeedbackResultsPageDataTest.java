package teammates.test.cases.ui.pagedata;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorFeedbackResultsPageData;

public class InstructorFeedbackResultsPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @Test
    public void testInitForViewByQuestion() {
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account);
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("typical case: view all sections, all questions, show stats");
        data.instructor = instructor;
        data.courseId = instructor.courseId;
        data.feedbackSessionName = dataBundle.feedbackSessions.get("session1InCourse1").feedbackSessionName;
        data.showStats = "on";
        data.groupByTeam = "on";
        data.sortType = "question";
        data.selectedSection = InstructorFeedbackResultsPageData.ALL_SECTION_OPTION;
        
        
            
    }
    
}
