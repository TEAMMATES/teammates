package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackSessionAttributes}.
 */
public class FeedbackSessionAttributesTest extends BaseTestCase {
    private Date startTime;
    private Date endTime;

    private FeedbackSessionAttributes fsa;

    @BeforeClass
    public void classSetup() {
        startTime = TimeHelper.combineDateTime("09/05/2016", "1000");
        endTime = TimeHelper.combineDateTime("09/05/2017", "1000");

        fsa = new FeedbackSessionAttributes(null,
                null, null, null, null, startTime, endTime,
                null, null, 8, 15, FeedbackSessionType.STANDARD,
                false, false, false, false, false, false, false);
    }

    @Test
    public void testBasicGetters() {
        ______TS("get session stime, etime, name");

        assertEquals(fsa.getSessionEndTime(), endTime);
        assertEquals(fsa.getSessionStartTime(), startTime);

    }

    @Test
    public void testValidate() {

        ______TS("null parameter error messages");

        List<String> expectedErrorMessage = new ArrayList<>();
        String[] fieldNames = new String[]{
                "feedback session name",
                "course ID",
                "instructions to students",
                "time for the session to become visible",
                "creator's email",
                "session creation time"};
        for (String fieldName : fieldNames) {
            expectedErrorMessage.add(FieldValidator.NON_NULL_FIELD_ERROR_MESSAGE.replace("${fieldName}", fieldName));
        }

        //expect all the error messages to be appended together.
        assertEquals(fsa.getInvalidityInfo(), expectedErrorMessage);

        ______TS("invalid parameters error messages");

    }

}
