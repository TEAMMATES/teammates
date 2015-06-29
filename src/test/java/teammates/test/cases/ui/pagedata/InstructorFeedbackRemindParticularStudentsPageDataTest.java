package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.util.ArrayList;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.controller.InstructorFeedbackRemindParticularStudentsPageData;
import teammates.ui.template.RemindParticularStudentsCheckboxEmailNamePair;

public class InstructorFeedbackRemindParticularStudentsPageDataTest extends BaseComponentTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testInit() {
        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");

        InstructorFeedbackRemindParticularStudentsPageData data = 
            new InstructorFeedbackRemindParticularStudentsPageData(instructorAccount);

        assertNull(data.getEmailNamePairs());

        FeedbackSessionResponseStatus feedbackSessionResponseStatus = new FeedbackSessionResponseStatus();

        data.responseStatus = feedbackSessionResponseStatus;

        ______TS("Init with no students without response(s) existing");

        data.init();

        assertEquals(data.getEmailNamePairs(), new ArrayList<RemindParticularStudentsCheckboxEmailNamePair>());

        ______TS("Init with only one student without response(s) existing");

        feedbackSessionResponseStatus.noResponse.add("LazyBoy@lazyboy.com");
        feedbackSessionResponseStatus.emailNameTable.put("LazyBoy@lazyboy.com", "Lazy Boy");

        ______TS("Init with multiple students without response(s) existing");

        
    }
}
