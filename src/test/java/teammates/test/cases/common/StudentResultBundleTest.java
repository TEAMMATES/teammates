package teammates.test.cases.common;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EnrollException;
import teammates.test.cases.BaseTestCase;

public class StudentResultBundleTest extends BaseTestCase{

    @Test
    public void testSortOutgoingByStudentNameAscending()
            throws EnrollException {

        StudentResultBundle result = new StudentResultBundle(new StudentAttributes(
                "s1", "t1", "adam", "a@b", "", "dummy-course"));

        SubmissionAttributes s1 = new SubmissionAttributes();
        s1.details.revieweeName = "Benny";
        result.outgoing.add(s1);

        SubmissionAttributes s2 = new SubmissionAttributes();
        s2.details.revieweeName = "Alice";
        result.outgoing.add(s2);

        SubmissionAttributes s3 = new SubmissionAttributes();
        s3.details.revieweeName = "Charlie";
        result.outgoing.add(s3);

        result.sortOutgoingByStudentNameAscending();

        AssertJUnit.assertEquals("Alice", result.outgoing.get(0).details.revieweeName);
        AssertJUnit.assertEquals("Benny", result.outgoing.get(1).details.revieweeName);
        AssertJUnit
                .assertEquals("Charlie", result.outgoing.get(2).details.revieweeName);
    }

    @Test
    public void testSortIncomingByStudentNameAscending() throws Exception {
        StudentResultBundle result = new StudentResultBundle(new StudentAttributes(
                "s1", "t1", "adam", "a@b", "", "dummy-course"));

        SubmissionAttributes s1 = new SubmissionAttributes();
        s1.details.reviewerName = "Benny";
        result.incoming.add(s1);

        SubmissionAttributes s2 = new SubmissionAttributes();
        s2.details.reviewerName = "Alice";
        result.incoming.add(s2);

        SubmissionAttributes s3 = new SubmissionAttributes();
        s3.details.reviewerName = "Charlie";
        result.incoming.add(s3);

        result.sortIncomingByStudentNameAscending();

        AssertJUnit.assertEquals("Alice", result.incoming.get(0).details.reviewerName);
        AssertJUnit.assertEquals("Benny", result.incoming.get(1).details.reviewerName);
        AssertJUnit
                .assertEquals("Charlie", result.incoming.get(2).details.reviewerName);
    }

    @Test
    public void testSortIncomingByFeedbackAscending() throws Exception{
        StudentResultBundle result = new StudentResultBundle(new StudentAttributes(
                "s1", "t1", "adam", "a@b", "", "dummy-course"));

        SubmissionAttributes s1 = new SubmissionAttributes();
        s1.p2pFeedback = new Text("ghk");
        result.incoming.add(s1);

        SubmissionAttributes s2 = new SubmissionAttributes();
        s2.p2pFeedback = new Text("def");
        result.incoming.add(s2);

        SubmissionAttributes s3 = new SubmissionAttributes();
        s3.p2pFeedback = new Text("abc");
        result.incoming.add(s3);

        result.sortIncomingByFeedbackAscending();

        AssertJUnit.assertEquals("abc",
                result.incoming.get(0).p2pFeedback.getValue());
        AssertJUnit.assertEquals("def",
                result.incoming.get(1).p2pFeedback.getValue());
        AssertJUnit.assertEquals("ghk",
                result.incoming.get(2).p2pFeedback.getValue());
    }

}
